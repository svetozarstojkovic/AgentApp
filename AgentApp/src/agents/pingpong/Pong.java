package agents.pingpong;

import javax.ejb.Stateful;

import agentgeneric.AID;
import agentgeneric.Agent;
import constants.Constants;
import data.Data;
import jms.SendJMSMessage;
import message.ACLMessage;
import message.Message;
import message.Performative;
import printer.Printer;
import services.Synchronize;
import util.Converter;

@Stateful
public class Pong extends Agent {

	public Pong() {}
	
	public Pong(String name) {
		AID aid = new AID();
		aid.setHost(Constants.getAgentCenter());
		aid.setName(name);
		aid.setType(Data.getAgentType(Pong.class.getSimpleName()));
		setId(aid);
		Printer.print(this, "Pong created: "+name);
	}
	

	@Override
	public void handleMessage(ACLMessage message) {
		Printer.print(this, "Pong handle message: "+message);
		if(message.getPerformative() == Performative.REQUEST) {
			ACLMessage reply = message.makeReply(Performative.INFORM);
			Message.sendMessage(reply);
		}
	}
}
