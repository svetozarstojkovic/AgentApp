package services;

import java.io.IOException;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import agentgeneric.AID;
import agentgeneric.AgentType;
import data.Data;
import printer.Printer;
import websockets.RefreshView;

@Path("agents")
public class AgentService {
	
	@GET
	@Path("/centers")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCenters() throws JsonGenerationException, JsonMappingException, IOException {
		new RefreshView().refreshCenters(Data.getAgentCenters());
		return new ObjectMapper().writeValueAsString(Data.getAgentCenters());
	}
	
	@GET
	@Path("/classes")
	@Produces(MediaType.APPLICATION_JSON)
	public String getClasses() throws JsonGenerationException, JsonMappingException, IOException {
		new RefreshView().refreshCenters(Data.getAgentCenters());
		return new ObjectMapper().writeValueAsString(Data.getAgentTypes());
	}
	
	@POST
	@Path("/classes")
	@Produces(MediaType.APPLICATION_JSON)
	public boolean setClasses(String agentTypesJSON) {
		try {
			AgentType[] agentTypes = new ObjectMapper().readValue(agentTypesJSON, AgentType[].class);
			Data.addAgentType(agentTypes);
			new RefreshView().refreshTypes(Data.getAgentTypes());
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	@GET
	@Path("/running")
	@Produces(MediaType.APPLICATION_JSON)
	public String getRunningAgents() throws JsonGenerationException, JsonMappingException, IOException {
		new RefreshView().refreshAID(Data.getRunningAID());
		return new ObjectMapper().writeValueAsString(Data.getRunningAID());
	}
	
	@PUT
	@Path("/running/{type}/{name}")
	public void activateAgent(@PathParam("type")String type, @PathParam("name")String name) {
		AID newAID = Data.addAgent(type, name);
		if (newAID != null) {
			Synchronize.sendChangeToAll("/synchronize/add_agent", newAID);
		}
	}
	
	@DELETE
	@Path("/running/{aid}")
	public void stopAgent(@PathParam("aid")String aidJSON) throws JsonParseException, JsonMappingException, IOException {
		Printer.print(this, "Stop agent: "+aidJSON);
		AID aid = new ObjectMapper().readValue(aidJSON, AID.class);
		AID retAID = Data.stopAgent(aid);
		if (retAID != null) {
			Synchronize.sendChangeToAll("/synchronize/stop_agent", retAID);
		}
	}

}
