package ch.blobber.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class PropertiesCon {
	Properties props = new Properties();
	
	public PropertiesCon() {
		try {
			props.load(new FileInputStream("/opt/blobber/loginData.properties"));
			
		} catch (IOException e) {
			System.out.println("Properties (/opt/blobber/loginData.properties) not found.");
			e.printStackTrace();
		}
	}

	public String getParameter(String para) {
		
		String output = "";
		output = props.getProperty(para);		
		
		return output;
	}
}
