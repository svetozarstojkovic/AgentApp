package init;

import static constants.Constants.AGENT_APP_MODULE;
import static constants.Constants.MASTER;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

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
import data.Data;
import printer.Printer;
import requests.Requests;
import services.Synchronize;
import util.Config;
import util.Converter;

@Startup
@Singleton
public class Initialize {
			
	@PostConstruct
	public void postConstruct() {
		Printer.print(this, "post construct");			
		Timer timer = new Timer();

		timer.schedule(new TimerTask() {
		  @Override
		  public void run() {
				try {
					String currentAddress = getCurrentAddress();
					
					timer.cancel();
					
					String masterAddress = new Config().getMasterAddress();					
					
					AgentCenter agentCenter = new AgentCenter(getHostName(), currentAddress);
					Data.addAgentCenter(agentCenter);
					
					Constants.setAgentCenter(agentCenter);
					
					setTypes();
					
					Printer.print(this, "Master: "+masterAddress);
					Printer.print(this, "Current: "+currentAddress);

					if (!masterAddress.equals(currentAddress)) {
						MASTER = false;
						initialHandshake(currentAddress, masterAddress);
					} else {
						MASTER = true;
					}
					
					heartbeat();

				} catch (InstanceNotFoundException | AttributeNotFoundException | MalformedObjectNameException
						| ReflectionException | MBeanException e) {
					e.printStackTrace();
				}
				
		  }
		}, 1*1000, 1*1000);
	}
	
	@PreDestroy
	public void preDestroy() {
		Printer.print(this, "PreDestroy");
		Synchronize.sendChangeToAll("/synchronize/stop_agents", Data.getRunningAgents());
		Data.removeAgentCenter(Constants.getAgentCenter());
		for(AgentCenter center : Data.getAgentCenters()) {
			Printer.print(this, "Center that is being removed: "+Constants.getAgentCenter().getAddress());
			new Requests().makeDeleteRequest("http://"+center.getAddress()+"/AgentApp/rest/synchronize/node/"+Constants.getAgentCenter().getAddress());
		}
	}
	
	public void initialHandshake(String currentAddress, String masterAddress) {
		
		Printer.print(this, "Initial handshake");
		Printer.print(this, Converter.getJSONString(Data.getAgentCenters()));
				
		//new Requests().makePostRequestForSigningNode("http://"+new Config().getMasterAddress()+"/AgentApp/rest/synchronize/node", Converter.getJSONString(Data.getAgentCenters().toArray()), 0);
		Synchronize.sendChangeToMaster("/synchronize/node", Data.getAgentCenters().toArray());
	}
	
	public String getHostName() {
		try  {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e){
			return "unknown-PC";
		}
	}
	
	public String getCurrentAddress() throws InstanceNotFoundException, AttributeNotFoundException, MalformedObjectNameException, ReflectionException, MBeanException {
		String port = ManagementFactory.getPlatformMBeanServer().getAttribute(new ObjectName("jboss.as:socket-binding-group=standard-sockets,socket-binding=http"), "boundPort").toString();
		String host = ManagementFactory.getPlatformMBeanServer().getAttribute(new ObjectName("jboss.as:interface=public"), "inet-address").toString();
		
		Printer.print(this, "Host: "+host);
		Printer.print(this, "Port: "+port);
				
		return host+":"+port;
	}
	
	private void setTypes() {
		AgentType pingType = new AgentType(Ping.class.getSimpleName(), AGENT_APP_MODULE);
		AgentType pongType = new AgentType(Pong.class.getSimpleName(), AGENT_APP_MODULE);
		
		AgentType mapReduceType = new AgentType(MapReduce.class.getSimpleName(), AGENT_APP_MODULE);
		
		AgentType initiatorType = new AgentType(Initiator.class.getSimpleName(), AGENT_APP_MODULE);
		AgentType participantType = new AgentType(Participant.class.getSimpleName(), AGENT_APP_MODULE);
		
		Data.getAgentTypes().add(pingType);
		Data.getAgentTypes().add(pongType);
		Data.getAgentTypes().add(mapReduceType);
		Data.getAgentTypes().add(initiatorType);
		Data.getAgentTypes().add(participantType);
		
		Data.addToMapClasses(Constants.getAgentCenter(), Data.getAgentTypes());
	}
		
	public void heartbeat() {
		Timer timer = new Timer();

		timer.schedule(new TimerTask(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (AgentCenter agentCenter : Data.getAgentCenters()) {
					if (!agentCenter.matches(Constants.getAgentCenter())) {
						heartbeatRequest(agentCenter);
					}
				}
			}
			
		}, 60*1000, 60*1000);
	}
	
	public void heartbeatRequest(AgentCenter agentCenter) {
		try {
			
			Printer.print(this, "AgentCenter: "+Constants.getAgentCenter().getAddress()+" is doing heartbeat");
			
			HttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet("http://"+agentCenter.getAddress()+"/AgentApp/rest/synchronize/node");
			
			RequestConfig defaultRequestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.BEST_MATCH).setExpectContinueEnabled(true).setStaleConnectionCheckEnabled(true).setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST)).setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC)).build();
			
			RequestConfig requestConfig = RequestConfig.copy(defaultRequestConfig).setSocketTimeout(5000).setConnectTimeout(5000).setConnectionRequestTimeout(5000).build();
			request.setConfig(requestConfig);
		
			client.execute(request);

		} catch (Exception e) {
			if (e instanceof SocketTimeoutException) {
				Data.removeAgentCenter(agentCenter);
				new Requests().makeDeleteRequest("http://"+Constants.getAgentCenter().getAddress()+"/AgentApp/rest/synchronize/node/"+agentCenter.getAddress());
			}
		}
	}
	

}
