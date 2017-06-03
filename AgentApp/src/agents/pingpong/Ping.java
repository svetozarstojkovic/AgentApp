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
public class Ping extends Agent {
		
	public Ping() {}
	
	public Ping(String name) {
		AID aid = new AID();
		aid.setHost(Constants.getAgentCenter());
		aid.setName(name);
		aid.setType(Data.getAgentType(Ping.class.getSimpleName()));
		setId(aid);
		Printer.print(this, "Ping created: "+name);
	}
	

	@Override
	public void handleMessage(ACLMessage message) {
		Printer.print(this, "Ping handle message: "+message.toString());		
		if (message.getPerformative() == Performative.REQUEST) { 
			
			AID pongAID = Data.getAIDForName(message.getContent());
			if (pongAID == null) {
				Printer.print(this, "Unknown pong agent");
				return;
			}
			ACLMessage msgToPong = new ACLMessage(Performative.REQUEST);
			msgToPong.setSender(getId());
			msgToPong.addReceiver(pongAID);

			Message.sendMessage(msgToPong);
			
		} else if (message.getPerformative() == Performative.INFORM) {
			// wait for the message
			ACLMessage msgFromPong = message;
			// we can put and retrieve custom user arguments using the userArgs field of the ACL message
			Printer.print(this, "Pong responded");

		}
	}
}
