package websockets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import dto.DTO;
import printer.Printer;
import requests.Requests;

@ServerEndpoint("/agents") 
public class AgentWebSocket {
	
	public static List<Session> sessions = new ArrayList<>();
	
	
	/**
     * @throws IOException 
	 * @OnOpen allows us to intercept the creation of a new session.
     * The session class allows us to send data to the user.
     * In the method onOpen, we'll let the user know that the handshake was 
     * successful.
     */
    @OnOpen
    public void onOpen(Session session) throws IOException{
        sessions.add(session);
        Printer.print(this, session.getId() + " has opened a connection"); 
    }
 
    /**
     * When a user sends a message to the server, this method will intercept the message
     * and allow us to react to it. For now the message is read as a String.
     * @throws IOException 
     * @throws JsonMappingException 
     * @throws JsonParseException 
     */
    @OnMessage
    public void onMessage(String message, Session session) throws JsonParseException, JsonMappingException, IOException{
    	 Printer.print(this, "Message from " + session.getId() + ": " + message);
    	 
    	 DTO dto = new ObjectMapper().readValue(message, DTO.class);
    	 
    	 switch (dto.getMethod()) {
    	 	case "PUT" : new Requests().makePutRequest(dto.getUrl()); break;
    	 	case "POST" : new Requests().makePostRequest(dto.getUrl(), dto.getObject()); break;
    	 	case "GET" : new Requests().makeGetRequest(dto.getUrl()); break;
    	 	case "DELETE" : new Requests().makeDeleteRequest(dto.getUrl()); break;
    	 }
    	    	 
    }
 
    /**
     * The user closes the connection.
     * 
     * Note: you can't send messages to the client from this method
     */
    @OnClose
    public void onClose(Session session){
    	Printer.print(this, "Session " +session.getId()+" has ended");
    	sessions.remove(session);
    }

}
