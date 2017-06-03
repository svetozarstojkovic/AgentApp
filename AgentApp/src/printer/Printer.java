package printer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import data.Data;
import services.Synchronize;

public class Printer {
	
	
	
	public static void print(Object object, String message)  {
		LocalDateTime ldt = LocalDateTime.now();
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss.SSS");
        String formatDateTime = ldt.format(formatter);
        
		String displayMessage = formatDateTime+" - "+object.getClass().getSimpleName() +" - "+message;
		
		Synchronize.sendChangeToAll("/synchronize/display_console", displayMessage);
		
		System.out.println(displayMessage);
	}
	


}
