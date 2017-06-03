package message;

import java.util.HashMap;
import java.util.Map;

import agentcenter.AgentCenter;
import agentgeneric.AID;
import data.Data;
import jms.SendJMSMessage;
import printer.Printer;
import services.Synchronize;
import util.Converter;

public class Message {
	
	public static void sendMessage(ACLMessage message) {
		for (AgentCenter center : Data.getAgentCenters()) {
			boolean exists = false;
			for (AID aid : message.getReceivers()) {
				if (aid.getHost().matches(center)) {
					exists = true;
				}
			}
			if (exists) {
				Synchronize.sendChangeToSpecific("/synchronize/new_message", message, center);
			}
		}
		//new SendJMSMessage(Converter.getJSONString(message));
		
		
	}

}
