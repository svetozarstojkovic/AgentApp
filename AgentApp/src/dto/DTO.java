package dto;

public class DTO {
	
	private String method;
	private String url;
	private String object;
	
	public DTO() {}
	
	public DTO(String method, String url, String object) {
		super();
		this.method = method;
		this.url = url;
		this.object = object;
	}
	
	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}


	

}
