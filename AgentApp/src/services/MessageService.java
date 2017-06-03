package services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import message.ACLMessage;
import message.Message;
import message.Performative;
import printer.Printer;
import websockets.RefreshView;

@Path("messages")
public class MessageService {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void postMessages(String messageJSON) throws JsonParseException, JsonMappingException, IOException {
		Printer.print(this, "here goes the message: "+messageJSON);
		ACLMessage message = new ObjectMapper().readValue(messageJSON, ACLMessage.class);
		//Synchronize.sendChangeToAll("/synchronize/new_message", message);
		Message.sendMessage(message);
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getPerformative() throws JsonGenerationException, JsonMappingException, IOException {
		new RefreshView().refreshPerformative();
		return new ObjectMapper().writeValueAsString(new ArrayList<Performative>(Arrays.asList(Performative.values())));
		
	}
	
}
