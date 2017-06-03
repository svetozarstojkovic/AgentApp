package agentgeneric;

import java.io.Serializable;

import agentcenter.AgentCenter;

public class AgentType implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3852660468161671290L;
	
	private String name;
	private String module;
	
	public AgentType() {
		
	}

	public AgentType(String name, String module) {
		super();
		this.name = name;
		this.module = module;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}
	
	public boolean matches(AgentType at) {
		return name.equals(at.name) && module.equals(at.module);
	}

}
