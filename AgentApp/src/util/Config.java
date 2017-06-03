package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

	public Config() {}
	
	public Properties getProperties() {
		Properties properties = new Properties();
		
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config/config.properties");
		
		if (inputStream != null) {
			try {
				properties.load(inputStream);
			} catch (IOException e) {
				e.printStackTrace();
				return properties;
			}
		} 
		return properties;
	}
	
	public String getMasterAddress() {
		return getProperties().getProperty("master_address").trim();
	}
	
}
