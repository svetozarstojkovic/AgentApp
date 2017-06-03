package message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agentgeneric.AID;

public class ACLMessage {
	
	private Performative performative;
	private AID sender;
	private List<AID> receivers;
	private AID replyTo;
	private String content;
	private Object contentObject;
	private Map<String, Object> userArgs;
	private String language;
	private String encoding;
	private String ontology;
	private String protocol;
	private String conversationId;
	private String replyWith;
	private String inReplyWith;
	private String inReplyTo;
	private long replyBy;
	
	public ACLMessage() {
	}
	
	public ACLMessage(Performative performative) {
		this.performative = performative;
		this.receivers = new ArrayList<>();
		this.userArgs = new HashMap<>();
	}

	public ACLMessage(Performative performative, AID sender, List<AID> receivers, AID replyTo, String content,
			Object contentObject, Map<String, Object> userArgs, String language, String encoding, String ontology,
			String protocol, String conversationId, String replyWith, String inReplyWith, String inReplyTo,
			long replyBy) {
		super();
		this.performative = performative;
		this.sender = sender;
		this.receivers = receivers;
		this.replyTo = replyTo;
		this.content = content;
		this.contentObject = contentObject;
		this.userArgs = userArgs;
		this.language = language;
		this.encoding = encoding;
		this.ontology = ontology;
		this.protocol = protocol;
		this.conversationId = conversationId;
		this.replyWith = replyWith;
		this.inReplyWith = inReplyWith;
		this.inReplyTo = inReplyTo;
		this.replyBy = replyBy;
	}



	public Performative getPerformative() {
		return performative;
	}

	public void setPerformative(Performative performative) {
		this.performative = performative;
	}

	public AID getSender() {
		return sender;
	}

	public void setSender(AID sender) {
		this.sender = sender;
	}

	public List<AID> getReceivers() {
		return receivers;
	}

	public void setReceivers(List<AID> receivers) {
		this.receivers = receivers;
	}
	
	public void addReceiver(AID receiver) {
		this.receivers.add(receiver);
	}

	public AID getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(AID replyTo) {
		this.replyTo = replyTo;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Object getContentObject() {
		return contentObject;
	}

	public void setContentObject(Object contentObject) {
		this.contentObject = contentObject;
	}

	public Map<String, Object> getUserArgs() {
		return userArgs;
	}

	public void setUserArgs(Map<String, Object> userArgs) {
		this.userArgs = userArgs;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getOntology() {
		return ontology;
	}

	public void setOntology(String ontology) {
		this.ontology = ontology;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getConversationId() {
		return conversationId;
	}

	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}

	public String getReplyWith() {
		return replyWith;
	}

	public void setReplyWith(String replyWith) {
		this.replyWith = replyWith;
	}

	public String getInReplyWith() {
		return inReplyWith;
	}

	public void setInReplyWith(String inReplyWith) {
		this.inReplyWith = inReplyWith;
	}

	public String getInReplyTo() {
		return inReplyTo;
	}

	public void setInReplyTo(String inReplyTo) {
		this.inReplyTo = inReplyTo;
	}

	public long getReplyBy() {
		return replyBy;
	}

	public void setReplyBy(long replyBy) {
		this.replyBy = replyBy;
	}
	
	public boolean canReplyTo() {
		return sender != null || replyTo != null;
	}
	
	public ACLMessage makeReply(Performative performative) {
		if (!canReplyTo())
			throw new IllegalArgumentException("There's no-one to receive the reply.");
		ACLMessage reply = new ACLMessage(performative);
		// receiver
		reply.receivers.add(replyTo != null ? replyTo : sender);
		// description of content
		reply.language = language;
		reply.ontology = ontology;
		reply.encoding = encoding;
		// control of conversation
		reply.protocol = protocol;
		reply.conversationId = conversationId;
		reply.inReplyTo = replyWith;
		return reply;
	}

}
