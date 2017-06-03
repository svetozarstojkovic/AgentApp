package agents.mapreduce;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.Stateful;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import agentgeneric.AID;
import agentgeneric.Agent;
import constants.Constants;
import data.Data;
import jms.SendJMSMessage;
import message.ACLMessage;
import message.Performative;
import printer.Printer;
import util.Converter;


@Stateful
public class MapReduce extends Agent{
	
	private Map<String, Integer> wordCount = new HashMap<>();
	
	private int numberOfFiles = 0;
	private int answered = 0;
	
	public MapReduce() {}
	
	public MapReduce(String name) {
		AID aid = new AID();
		aid.setHost(Constants.getAgentCenter());
		aid.setName(name);
		aid.setType(Data.getAgentType(MapReduce.class.getSimpleName()));
		setId(aid);
		Printer.print(this, "MapReduce created: "+name);
	}
	
	@Override
	public void handleMessage(ACLMessage message) {
		Printer.print(this, "MapReduce handle message");
		if (message.getPerformative() == Performative.REQUEST) {
			Printer.print(this, "MapReduce request message");
			
			File folder = new File(message.getContent());
			File[] listOfFiles = folder.listFiles();

			if (listOfFiles == null) {
				Printer.print(this, "Path unknown");
				return;
			}
			
			for (File file : listOfFiles) {
			    if (file.isFile()) {
			    	numberOfFiles++;
			        Printer.print(this, file.getAbsolutePath());
			        
			        MapReduceSlave mapReduceSlave = new MapReduceSlave(file.getName());
			        Data.addRunningAgent(mapReduceSlave);
			        
					ACLMessage msgToMapReduce = new ACLMessage(Performative.INFORM);
					msgToMapReduce.setSender(getId());
					msgToMapReduce.addReceiver(mapReduceSlave.getId());
					msgToMapReduce.setContent(file.getAbsolutePath());
					
					new SendJMSMessage(Converter.getJSONString(msgToMapReduce));
			    }
			}

		}  else if (message.getPerformative() == Performative.CONFIRM) {
			Printer.print(this, "MapReduce confirm message");
			answered++;
			
			try {
				Map<String, Integer> wordCount = new ObjectMapper().readValue(message.getContent(),  new TypeReference<HashMap<String,Integer>>() {});
				for (String key : wordCount.keySet()) {
					if (this.wordCount.get(key) == null) {
						this.wordCount.put(key, 1);
					} else {
						this.wordCount.put(key, this.wordCount.get(key) + wordCount.get(key));
					}
				}
				if (answered == numberOfFiles) {
					answered = 0;
					numberOfFiles = 0;
					this.wordCount = sortByComparator(this.wordCount, false);
					Printer.print(this, "Final output: "+Converter.getJSONString(this.wordCount));
					this.wordCount = new HashMap<>();
				}
				
				
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
	private Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap, final boolean order)
    {

        List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<String, Integer>>()
        {
            public int compare(Entry<String, Integer> o1,
                    Entry<String, Integer> o2)
            {
                if (order)
                {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else
                {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Entry<String, Integer> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
	
	
	
}
