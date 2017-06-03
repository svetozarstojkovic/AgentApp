package util;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONObject;

public class Converter {
	
	public static String getJSONString(Object object) {
		try {
			return new ObjectMapper().writeValueAsString(object);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	public static JSONObject getJSONObject(Object obj) {
	    ObjectMapper mapper = new ObjectMapper();       
	    try {
	    	String str = mapper.writeValueAsString(obj);
			return new JSONObject(str);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    return null;
		}

	}
	

}
