package agents.contractnet;

import java.util.Random;

import javax.ejb.Stateful;

import agentgeneric.AID;
import agentgeneric.Agent;
import constants.Constants;
import data.Data;
import message.ACLMessage;
import message.Message;
import message.Performative;
import printer.Printer;

@Stateful
public class Participant extends Agent {

	public Participant() {}
	
	public Participant(String name) {
		AID aid = new AID();
		aid.setHost(Constants.getAgentCenter());
		aid.setName(name);
		aid.setType(Data.getAgentType(Participant.class.getSimpleName()));
		setId(aid);
		Printer.print(this, "Participant created: "+name);
	}
	
	
	@Override
	public void handleMessage(ACLMessage message) {
		if (message.getPerformative() == Performative.CALL_FOR_PROPOSAL) {
			Printer.print(this, "Participant call for proposal");
			int number = new Random().nextInt(100);
			if (number > 50) {
				ACLMessage reply = message.makeReply(Performative.PROPOSE);
				int bid = new Random().nextInt(1000);
				Printer.print(this, "Name is: "+getId().getName()+" bid is: "+bid);
				reply.setContent(Integer.toString(bid));
				reply.setSender(getId());
				Message.sendMessage(reply);
			} else {
				ACLMessage reply = message.makeReply(Performative.REFUSE);
				Printer.print(this, "Name is: "+getId().getName()+" and I refuse");
				reply.setSender(getId());
				Message.sendMessage(reply);
			}
		} else if (message.getPerformative() == Performative.ACCEPT_PROPOSAL) {
			Printer.print(this, "I "+getId().getName()+" got accepted");
			int decision = new Random().nextInt(100);
			if (decision < 33) {
				ACLMessage reply = message.makeReply(Performative.FAILURE);
				reply.setSender(getId());
				Message.sendMessage(reply);
			} else if (decision > 33 && decision < 66) {
				ACLMessage reply = message.makeReply(Performative.INFORM);
				reply.setSender(getId());
				Message.sendMessage(reply);
			} else if (decision > 66){
				ACLMessage reply = message.makeReply(Performative.INFORM_REF);
				reply.setSender(getId());
				Message.sendMessage(reply);
			}
		} else if (message.getPerformative() == Performative.REJECT_PROPOSAL) {
			Printer.print(this, "I "+getId().getName()+" got rejected");
		} else {
			Printer.print(this, "Unhandled performative");
		}
	}
}
