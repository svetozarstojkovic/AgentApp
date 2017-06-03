package agents.contractnet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class Initiator extends Agent {
	
	private Map<AID, Integer> offers = new HashMap<>();
	private List<AID> participants;
	private int currentNumberOfReplied = 0;
	private int numberOfParticipants = 0;
	
	public Initiator() {}
	
	public Initiator(String name) {
		AID aid = new AID();
		aid.setHost(Constants.getAgentCenter());
		aid.setName(name);
		aid.setType(Data.getAgentType(Initiator.class.getSimpleName()));
		setId(aid);
		Printer.print(this, "Initiator created: "+name);
	}
	
	@Override
	public void handleMessage(ACLMessage message) {
		if (message.getPerformative() == Performative.REQUEST) {
			Printer.print(this, "Initiator handle request");
			participants = getParticipantsAID();
			offers = new HashMap<>();
			currentNumberOfReplied = 0;
			numberOfParticipants = participants.size();
			if (numberOfParticipants == 0) {
				Printer.print(this, "No participants");
				return;
			}
			ACLMessage msgToParticipants = new ACLMessage(Performative.CALL_FOR_PROPOSAL);
			msgToParticipants.setSender(getId());
			msgToParticipants.setReceivers(participants);
			Message.sendMessage(msgToParticipants);
		} else if (message.getPerformative() == Performative.PROPOSE) {
			currentNumberOfReplied++;
			offers.put(message.getSender(),Integer.parseInt(message.getContent()));
			Printer.print(this, "Current number: "+currentNumberOfReplied);
			Printer.print(this, "All participants: "+numberOfParticipants);
			if (currentNumberOfReplied == numberOfParticipants) {
				sendRejectOrAccept();
			}
		} else if (message.getPerformative() == Performative.REFUSE) {
			currentNumberOfReplied++;
			Printer.print(this, "Current number: "+currentNumberOfReplied);
			Printer.print(this, "All participants: "+numberOfParticipants);
			if (currentNumberOfReplied == numberOfParticipants) {
				sendRejectOrAccept();
			}
		} else if (message.getPerformative() == Performative.FAILURE) {
			Printer.print(this, "I accepted and then "+message.getSender().getName()+" refused : failure");
		} else if (message.getPerformative() == Performative.INFORM) {
			Printer.print(this, "inform-done : inform by: "+message.getSender().getName());
		} else if (message.getPerformative() == Performative.INFORM_REF) {
			Printer.print(this, "inform-result : inform by: "+message.getSender().getName());
		} else {
			Printer.print(this, "Unhandled performative");
		}
	}
	
	private void sendRejectOrAccept() {
		AID maxOffer = getMaxOffer();
		if (maxOffer != null) {
			for (AID aid : participants) {
				if (aid.matches(maxOffer)) {
					ACLMessage reply = new ACLMessage(Performative.ACCEPT_PROPOSAL);
					reply.setSender(getId());
					reply.addReceiver(aid);
					Message.sendMessage(reply);
				} else {
					ACLMessage reply = new ACLMessage(Performative.REJECT_PROPOSAL);
					reply.setSender(getId());
					reply.addReceiver(aid);
					Message.sendMessage(reply);
				}
			}
		} else {
			Printer.print(this, "Everyone refused");
		}
		
	}
	
	private AID getMaxOffer() {
		AID maxAID = null;
		int maxOffer = 0;
		for (AID aid : offers.keySet()) {
			if (offers.get(aid) >= maxOffer) {
				maxOffer = offers.get(aid);
				maxAID = aid;
			}
		}
		return maxAID;
		
	}
	
	private List<AID> getParticipantsAID() {
		List<AID> participants = new ArrayList<>();
		for (AID aid : Data.getRunningAID()) {
			if (aid.getType().getName().equals(Participant.class.getSimpleName())) {
				participants.add(aid);
			}
		}
		return participants;
	}

}
