package agents.mapreduce;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateful;

import agentgeneric.AID;
import agentgeneric.Agent;
import agentgeneric.AgentType;
import constants.Constants;
import data.Data;
import jms.SendJMSMessage;
import message.ACLMessage;
import message.Performative;
import printer.Printer;
import util.Converter;

@Stateful
public class MapReduceSlave extends Agent {

	public MapReduceSlave() {}
	
	public MapReduceSlave(String name) {
		AID aid = new AID();
		aid.setHost(Constants.getAgentCenter());
		aid.setName(name);
		aid.setType(new AgentType(MapReduceSlave.class.getSimpleName(), Constants.AGENT_APP_MODULE));
		setId(aid);
		Printer.print(this, "MapReduceSlave created: "+name);
	}
	
	@Override
	public void handleMessage(ACLMessage message) {
		Printer.print(this, "MapReduceSlave handle message");
		if (message.getPerformative() == Performative.INFORM) {
			
			ACLMessage reply = message.makeReply(Performative.CONFIRM);
			Data.stopAgent(getId());
			
			Map<String, Integer> wordCount = getWordCount(message.getContent());
			reply.setContent(Converter.getJSONString(wordCount));
			
			new SendJMSMessage(Converter.getJSONString(reply));
		}
	}
	
	private String readFile(String fileName) {
		
		try(BufferedReader br = new BufferedReader(new FileReader(fileName))) {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    String everything = sb.toString();

		    
		    return everything;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		
		
	}
	
	private Map<String, Integer> getWordCount(String fileName) {
		
		String outString = readFile(fileName);
		
		Map<String, Integer> wordsMap = new HashMap<>();
		
		String[] words = outString.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
		
		for (String word : words) {
			if (wordsMap.get(word) == null) {
				wordsMap.put(word, 1);
			} else {
				wordsMap.put(word, wordsMap.get(word)+1);
			}
		}
		
		return wordsMap;
	}
	
}
