package websockets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.websocket.Session;

import agentcenter.AgentCenter;
import agentgeneric.AID;
import agentgeneric.AgentType;
import message.Performative;
import util.Converter;

public class RefreshView {
	
	public void refreshAID(List<AID> aids) {
		for (Session session : AgentWebSocket.sessions) {
			try {
				session.getBasicRemote().sendText("aid"+Converter.getJSONString(aids));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void refreshTypes(List<AgentType> types) {
		for (Session session : AgentWebSocket.sessions) {
			try {
				session.getBasicRemote().sendText("types"+Converter.getJSONString(types));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void refreshCenters(List<AgentCenter> centers) {
		for (Session session : AgentWebSocket.sessions) {
			try {
				session.getBasicRemote().sendText("center"+Converter.getJSONString(centers));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void refreshPerformative() {
		for (Session session : AgentWebSocket.sessions) {
			try {
				session.getBasicRemote().sendText("perfomative"+Converter.getJSONString(new ArrayList<Performative>(Arrays.asList(Performative.values()))));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
