import java.util.*;
import java.lang.*;

public class Command {
		
	String s;
	
	public Command(String command) {
		s = command;
	}
	
	public String getOutput(){
		String [] temp;
		String return_result = "";
		boolean result = true;
		Connector c = new Connector();
		System.out.println("connected Successfully");
		System.out.println("command: "+s);
		temp = s.split(":;:");
		//////////////////////User/////////////////////////

		if(temp[0].equals("CHICKEN_BROILER")) 
		{
			String output;
			output = c.get_chicken(temp[1], temp[2]);
			if(output.length() >0) 
			{
				return_result = output;
			}
			else 
			{
			 	return_result = "none";
			}
		}
		//////////////////////Default/////////////////////////
		else {
			return_result = "Nothing is being requested.";
		}
		System.out.println(return_result);
		return return_result;
	}
}
