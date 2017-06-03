package services;

import static constants.Constants.MASTER;

import java.io.IOException;

import javax.websocket.Session;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.map.ObjectMapper;

import agentcenter.AgentCenter;
import agentgeneric.AID;
import agentgeneric.Agent;
import agentgeneric.AgentType;
import constants.Constants;
import data.Data;
import jms.SendJMSMessage;
import printer.Printer;
import requests.Requests;
import util.Config;
import util.Converter;
import websockets.AgentWebSocket;
import websockets.RefreshView;

@Path("synchronize")
public class Synchronize {

	@POST
	@Path("/node")
	@Consumes(MediaType.APPLICATION_JSON)
	public void agentCenters(String agentCentersJSON) {
		try {
			Printer.print(this, "Before adding []: "+agentCentersJSON);
			if (agentCentersJSON.indexOf("{") == 0) {
				agentCentersJSON = "["+agentCentersJSON+"]";
			}
			Printer.print(this, "After adding []: "+agentCentersJSON);
			Printer.print(this, agentCentersJSON);
			AgentCenter[] agentCenters = new ObjectMapper().readValue(agentCentersJSON, AgentCenter[].class);
			if (MASTER && agentCenters.length == 1) {
				Data.addAgentCenter(agentCenters[0]);
				Printer.print(this, "AgentCenter added");
				
				// add new type if it is not already here, and if there are any changes send change to all other slaves
				String typesJSON = new Requests().makeGetRequest("http://"+agentCenters[0].getAddress()+"/AgentApp/rest/agents/classes");
				AgentType[] agentTypes = new ObjectMapper().readValue(typesJSON, AgentType[].class);
				Data.addToMapClasses(agentCenters[0], agentTypes);
				boolean changes = Data.addAgentType(agentTypes);
				if (changes) {
					sendChangeToSlaves("/agents/classes", Data.getAgentTypes());
				}
				Printer.print(this, "type part over");
				
				// send new agent center to all other slaves
				sendChangeToSlaves("/synchronize/node", agentCenters[0]);
				Printer.print(this, "slaves informed about new agent center");	
				
				// send other agent centers to new slave
				String otherAgentCentersJSON = new ObjectMapper().writeValueAsString(Data.getAgentCenters());
				new Requests().makePostRequestForSigningNode("http://"+agentCenters[0].getAddress()+"/AgentApp/rest/synchronize/node", otherAgentCentersJSON, 0, agentCenters[0]);
				Printer.print(this, "other centers are sent to new center");
				
				// send running agents to new slave
				String runningAgentsJSON = new ObjectMapper().writeValueAsString(Data.getRunningAID());
				new Requests().makePostRequestForSigningNode("http://"+agentCenters[0].getAddress()+"/AgentApp/rest/synchronize/running_agents", runningAgentsJSON, 0, agentCenters[0]);
				Printer.print(this, "new center is informed about running agents");
				
				new RefreshView().refreshCenters(Data.getAgentCenters());
				
			} else if (!MASTER && agentCenters.length == 1) {
				Data.addAgentCenter(agentCenters[0]);
				Printer.print(this, "Agent center added in slave");
			} else if (!MASTER && agentCenters.length > 1) {
				Data.setAgentCenters(agentCenters);
				Printer.print(this, "Agent centers set in slave");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@GET
	@Path("/node")
	public void checkIfAlive() {
		Printer.print(this, "AgentCenter: "+Constants.getAgentCenter().getAddress()+" is hit for heartbeat");
	}
	
	@DELETE
	@Path("node/{address}")
	public void deleteNode(@PathParam("address")String address) {
		AgentCenter agentCenter = Data.getAgentCentar(address);
		if (agentCenter != null) {
			Data.removeAgentCenter(agentCenter);
			Data.removeFromMapClasses(agentCenter);
		}
		new RefreshView().refreshCenters(Data.getAgentCenters());
	}
	
	@POST
	@Path("/running_agents")
	@Consumes(MediaType.APPLICATION_JSON)
	public void addRunningAgent(String runningAIDSJSON) {
		try {
//			Agent[] runningAgents = new ObjectMapper().readValue(runningAgentsJSON, Agent[].class);
			AID[] newAID = new ObjectMapper().readValue(runningAIDSJSON, AID[].class);
			for (AID aid : newAID) {
				Data.addRunningAID(aid);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@POST
	@Path("/add_agent")
	@Consumes(MediaType.APPLICATION_JSON)
	public void addAgent(String newAIDJSON) {
		try {
//			Agent[] runningAgents = new ObjectMapper().readValue(runningAgentsJSON, Agent[].class);
			AID newAID = new ObjectMapper().readValue(newAIDJSON, AID.class);
			Data.addRunningAID(newAID);
			new RefreshView().refreshAID(Data.getRunningAID());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@POST
	@Path("/stop_agent")
	@Consumes(MediaType.APPLICATION_JSON)
	public void stopAgent(String newAIDJSON) {
		try {
//			Agent[] runningAgents = new ObjectMapper().readValue(runningAgentsJSON, Agent[].class);
//			Printer.print(this, "newAID: "+newAIDJSON);
			AID newAID = new ObjectMapper().readValue(newAIDJSON, AID.class);
			Data.removeAID(newAID);
			new RefreshView().refreshAID(Data.getRunningAID());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@POST
	@Path("/stop_agents")
	@Consumes(MediaType.APPLICATION_JSON)
	public void stopAgents(String agentsJSON) {
		try {
//			Agent[] runningAgents = new ObjectMapper().readValue(runningAgentsJSON, Agent[].class);
//			Printer.print(this, "newAID: "+newAIDJSON);
			Agent[] agents = new ObjectMapper().readValue(agentsJSON, Agent[].class);
			for (Agent agent : agents) {
				Data.removeAID(agent.getId());
			}
			new RefreshView().refreshAID(Data.getRunningAID());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@POST
	@Path("/new_message")
	@Consumes(MediaType.APPLICATION_JSON)
	public void newMessage(String aclMessageJSON) {
		new SendJMSMessage(aclMessageJSON);
	}
	
	@POST
	@Path("/display_console")
	@Consumes(MediaType.APPLICATION_JSON)
	public void displayConsole(String consoleMessage) {
		
		Data.consoleMessages.add(consoleMessage);
		for (Session session : AgentWebSocket.sessions) {
			try {
				session.getBasicRemote().sendText(Converter.getJSONString(Data.consoleMessages));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				System.out.println("unable to send message to websocket");
			}
		}
	}
	
	public static void sendStringToAll(String url, String text) {
		for (AgentCenter agentCenter: Data.getAgentCenters()) {
			//String hostAddress = AddressUtil.getHostAndPort(host.getAddress());
//				System.out.println("sendChange() "+"http://"+agentCenter.getAddress()+"/AgentApp/rest"+url);
			new Requests().makePostRequest("http://"+agentCenter.getAddress()+"/AgentApp/rest"+url, text);
		}

	}
	
	public static void sendChangeToAll(String url, Object object) {
		try {
			String jsonInString = new ObjectMapper().writeValueAsString(object);
			for (AgentCenter agentCenter: Data.getAgentCenters()) {
				//String hostAddress = AddressUtil.getHostAndPort(host.getAddress());
//				System.out.println("sendChange() "+"http://"+agentCenter.getAddress()+"/AgentApp/rest"+url);
				new Requests().makePostRequest("http://"+agentCenter.getAddress()+"/AgentApp/rest"+url, jsonInString);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	
	public static Object sendChangeToMaster(String url, Object object) {
		try {
			String jsonInString = new ObjectMapper().writeValueAsString(object);
			String masterAddress = new Config().getMasterAddress();
//			System.out.println("http://"+masterAddress+"/AgentApp/rest"+url);
			return new Requests().makePostRequest("http://"+masterAddress+"/AgentApp/rest"+url, jsonInString);
		} catch (IOException e) {
			//e.printStackTrace();
			return null;
		}

	}
	
	public static void sendChangeToSlaves(String url, Object object) {
		try {
			String jsonInString = new ObjectMapper().writeValueAsString(object);
			for (AgentCenter agentCenter: Data.getAgentCenters()) {
				if (!agentCenter.getAddress().equals(new Config().getMasterAddress())) {
//					System.out.println("sendChange() "+"http://"+agentCenter.getAddress()+"/AgentApp/rest"+url);
					new Requests().makePostRequest("http://"+agentCenter.getAddress()+"/AgentApp/rest"+url, jsonInString);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static void sendChangeToSpecific(String url, Object object, AgentCenter agentCenter) {
		try {
			String jsonInString = new ObjectMapper().writeValueAsString(object);
			//String hostAddress = AddressUtil.getHostAndPort(host.getAddress());
//			System.out.println("sendChange() "+"http://"+agentCenter.getAddress()+"/AgentApp/rest"+url);
			new Requests().makePostRequest("http://"+agentCenter.getAddress()+"/AgentApp/rest"+url, jsonInString);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

		
}
