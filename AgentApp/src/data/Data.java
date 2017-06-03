package data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import agentcenter.AgentCenter;
import agentgeneric.AID;
import agentgeneric.Agent;
import agentgeneric.AgentType;
import agents.contractnet.Initiator;
import agents.contractnet.Participant;
import agents.mapreduce.MapReduce;
import agents.pingpong.Ping;
import agents.pingpong.Pong;
import constants.Constants;

public class Data {
	
	public static List<String> consoleMessages = new ArrayList<>();
		
	private static List<Agent> runningAgents = new ArrayList<>();
	private static List<AID> runningAID = new ArrayList<>();
	
	private static Map<AgentCenter, List<AgentType>> mapClasses = new HashMap<>();

	private static List<AgentType> agentTypes = new ArrayList<>();
	
	private static List<AgentCenter> agentCenters = new ArrayList<>();
	
//	static {
//		AgentType pingType = new AgentType(Ping.class.getSimpleName(), AGENT_APP_MODULE);
//		AgentType pongType = new AgentType(Pong.class.getSimpleName(), AGENT_APP_MODULE);
//		
//		AgentType mapReduceType = new AgentType(MapReduce.class.getSimpleName(), AGENT_APP_MODULE);
//		
//		AgentType initiatorType = new AgentType(Initiator.class.getSimpleName(), AGENT_APP_MODULE);
//		AgentType participantType = new AgentType(Participant.class.getSimpleName(), AGENT_APP_MODULE);
//		
//		agentTypes.add(pingType);
//		agentTypes.add(pongType);
//		agentTypes.add(mapReduceType);
//		agentTypes.add(initiatorType);
//		agentTypes.add(participantType);
//		
//	}
	
	public static List<AgentCenter> getAgentCenters() {
		return agentCenters;
	}

	public static void setAgentCenters(List<AgentCenter> agentCenters) {
		Data.agentCenters = agentCenters;
	}
	
	public static void addAgentCenter(AgentCenter agentCenter) {
		for (AgentCenter ac: Data.agentCenters) {
			if (ac.getAddress().equals(agentCenter.getAddress())) {
				return;
			}
		}
		Data.agentCenters.add(agentCenter);
	}
	
	public static void removeAgentCenter(AgentCenter agentCenter) {
		Data.agentCenters.remove(agentCenter);
	}
	
	public static void setAgentCenters(AgentCenter[] agentCenters) {
		Data.agentCenters.clear();
		for (AgentCenter agentCenter : agentCenters) {
			Data.agentCenters.add(agentCenter);
		}
	}
	
	public static AgentCenter getAgentCentar(String address) {
		for (AgentCenter ac : Data.agentCenters) {
			if (ac.getAddress().equals(address)) {
				return ac;
			}
		}
		return null;
	}
	
	public static void setRunningAgents(Agent[] runningAgents) {
		Data.runningAgents.clear();
		for (Agent agent : runningAgents) {
			Data.runningAgents.add(agent);
		}
	
	}
	
	public static void addRunningAgents(Agent[] runningAgents) {
		for (Agent a1 : runningAgents) {
			boolean exists = false;
			for (Agent a2 : Data.runningAgents) {
				if (a1.getId().matches(a2.getId())) {
					exists = true;
				}
			}
			if (!exists) {
				Data.runningAgents.add(a1);
			}
		}
	}

	public static List<Agent> getRunningAgents() {
		return runningAgents;
	}
	
	public static void setRunningAgents(List<Agent> runningAgents) {
		Data.runningAgents = runningAgents;
	}
	
	public static boolean addRunningAgent(Agent runningAgent) {
		if ("".equals(runningAgent.getId().getName())) {
			return false;
		}
		for (Agent agent : Data.runningAgents) {
			if (agent.getId().matches(runningAgent.getId())) {
				return false;
			}
		}
		Data.getRunningAgents().add(runningAgent);
		//Data.getRunningAID().add(runningAgent.getId());
		return true;

	}
	
	public static AID stopAgent(AID aid) {
		for (Agent agent : runningAgents) {
			if (agent.getId().matches(aid)) {
				runningAgents.remove(agent);
				return agent.getId();
			}
		}
		return null;
	}
		
	public static List<AgentType> getAgentTypes() {
		return agentTypes;
	}
	
	public static AgentType getAgentType(String name) {
		for (AgentType agentType : agentTypes) {
			if (agentType.getName().equals(name)) {
				return agentType;
			}
		}
		return null;
	}
	

	public static void setAgentTypes(List<AgentType> agentTypes) {
		Data.agentTypes = agentTypes;
	}
	
	public static boolean addAgentType(AgentType[] agentTypes) {
		boolean retVal = false;
		for (AgentType at1 : agentTypes) {
			boolean exists = false;
			for (AgentType at2 : Data.agentTypes) {
				if (at1.matches(at2)) {
					exists = true;
				}
			}
			if (!exists) {
				Data.agentTypes.add(at1);
				retVal = true;
			}
		}
		retVal = false;
		return retVal;
		
	}
	
	public static void removeFromTypes(AgentType type) {
		for (AgentType at : Data.agentTypes) {
			if (at.matches(type)) {
				Data.agentTypes.remove(at);
				return;
			}
		}
	}
	
	public static AID addAgent(String typeName, String agentName) {
		if (Ping.class.getSimpleName().equals(typeName)) {
			Ping ping = new Ping(agentName);
			Data.addRunningAgent(ping);
			return ping.getId();
		} else if (Pong.class.getSimpleName().equals(typeName)) {
			Pong pong = new Pong(agentName);
			Data.addRunningAgent(pong);
			return pong.getId();
		} else if (MapReduce.class.getSimpleName().equals(typeName)) {
			MapReduce mapReduce = new MapReduce(agentName);
			Data.addRunningAgent(mapReduce);
			return mapReduce.getId();
		} else if (Initiator.class.getSimpleName().equals(typeName)) {
			Initiator initiator = new Initiator(agentName);
			Data.addRunningAgent(initiator);
			return initiator.getId();
		} else if (Participant.class.getSimpleName().equals(typeName)) {
			Participant participant = new Participant(agentName);
			Data.addRunningAgent(participant);
			return participant.getId();
		} else {
			return null;
		}
	}
	

	
	public static Agent getAgentFromAID(AID aid) {
		for (Agent agent : Data.getRunningAgents()) {
			if (agent.getId().matches(aid)) {
				return agent;
			}
		}
		return null;
	}
	
	public static Agent getAgentForName(String name) {
		for (Agent agent : Data.getRunningAgents()) {
			if (agent.getId().getName().equals(name)) {
				return agent;
			}
		}
		return null;
	}
	
	public static AID getAIDForName(String name) {
		for (AID aid: Data.getRunningAID()) {
			if (aid.getName().equals(name)) {
				return aid;
			}
		}
		return null;
	}
	
	public static List<AID> getRunningAID() {
		return runningAID;
	}

	public static void setRunningAID(List<AID> runningAID) {
		Data.runningAID = runningAID;
	}
	
	public static void addRunningAID(AID newAID) {
		boolean exists = false;
		for (AID aid : Data.runningAID) {
			if (aid.matches(newAID)) {
				exists = true;
				break;
			}
		}
		if (!exists) {
			Data.runningAID.add(newAID);
		}
		
	}
	
	public static void removeAID(AID newAID) {
		for (AID aid : Data.runningAID) {
			if (aid.matches(newAID)) {
				Data.runningAID.remove(aid);
				return;
			}
		}
		
	}
	
	public static Map<AgentCenter, List<AgentType>> getOtherClasses() {
		return mapClasses;
	}

	public static void setMapClasses(Map<AgentCenter, List<AgentType>> otherClasses) {
		Data.mapClasses = otherClasses;
	}
	
	public static void addToMapClasses(AgentCenter center, AgentType[] agentTypes) {
		Data.addToMapClasses(center, Arrays.asList(agentTypes));
	}
	
	public static void addToMapClasses(AgentCenter center, List<AgentType> agentTypes) {
		Data.mapClasses.put(center, agentTypes);
	}
	
	public static boolean mapOfTypesContains(AgentCenter center, AgentType type) {
		List<AgentType> types = Data.mapClasses.get(center);
		for (AgentType t : types) {
			if (t.matches(type)) {
				return true;
			}
		}
		return false;
	}
	
	public static void removeFromMapClasses(AgentCenter center) {
		List<AgentType> types = Data.mapClasses.get(center);
		Set<AgentType> found = new HashSet<>();
		Data.mapClasses.remove(center);
		for (AgentType agentType : types) {
			for (AgentCenter ac : Data.mapClasses.keySet()) {
				if (mapOfTypesContains(ac, agentType)) {
					found.add(agentType);
				}
			}
		}
		
		if (found.size() == types.size()) {
			for (AgentType at1 : found) {
				boolean exists = false;
				for (AgentType at2 : types) {
					if (at1.matches(at2)) {
						exists = true;
					}
				}
				if (!exists) {
					removeFromTypes(at1);
				}
			}
		}
	}
	
}
