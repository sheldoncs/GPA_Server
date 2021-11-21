/*
 * Created on 2004/8/5
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package gpa.http.monitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import net.gpa.util.MessageLogger;




/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ServiceStarter {

	URL file;
	
	String classFileName;
	String stoppedClassFileName;
	
	public ServiceStarter() {
		
	}
	
	public ServiceStarter(URL file) {
		this.file = file;
		classFileName = file.getFile().replaceAll("%20"," ");
		stoppedClassFileName = classFileName+".stop";
	}

	public boolean startMonitor(String starterURL, String contextPath) {

		String sURL = starterURL+"?command=startMonitor&contextPath="+contextPath;
		
	    
		try {
			         
  	    	URL resourceURL = new URL(sURL);
  	    	URLConnection connection = resourceURL.openConnection();
  	    	
  	    	System.out.println(connection.getContent());
  	    	
  	    	BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
  	    	StringBuffer content = new StringBuffer();
  	    	String response = in.readLine();
      	    while(response != null) {
      	    	content.append(response+"\n");
      	    	response = in.readLine();
      	    }
      	    in.close();

      	    System.out.println("ContentType="+connection.getContentType());
      	    System.out.println("Content = "+content);
      	    String header = connection.getHeaderField(0);

      	    if(header.indexOf("OK")!= -1){
      	    	if(content.indexOf("OK")!=-1)
      	    		
      	    	    return true;
      	    }
  	    } catch(MalformedURLException e) {
  	    	e.printStackTrace(MessageLogger.out);
  	    } catch(IOException e) {
  	    	e.printStackTrace(MessageLogger.out);
  	    } catch(Exception e) {
  	    	e.printStackTrace(MessageLogger.out);
  	    }
  	    return false;
	}
	
	public boolean stopServlet() {
		File classFile  = new File(classFileName);
		File fakeFile   = new File(classFileName);
		File stoppedClassFile = new File(stoppedClassFileName);
		
		try {
			  if(stoppedClassFile.exists())
			      stoppedClassFile.delete();
			  
			  if(classFile.renameTo(stoppedClassFile))
			      return fakeFile.createNewFile();
			  
		} catch(IOException e) {
			e.printStackTrace(MessageLogger.out);
		}

		return false;
	}
	
	/*
	private boolean startComponent() {
	
		String sURL = "http://localhost:8080/test/Starter?start=true";
		
		try {
  	    	URL resourceURL = new URL(sURL);
  	    	URLConnection connection = resourceURL.openConnection();
  	    	
  	    	BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
  	    	StringBuffer content = new StringBuffer();
  	    	String response = in.readLine();
      	    while(response != null) {
      	    	content.append(response+"\n");
      	    	response = in.readLine();
      	    }
      	    in.close();

      	    System.out.println("ContentType="+connection.getContentType());
      	    System.out.println("Content = "+content);
      	    String header = connection.getHeaderField(0);
      	    if(header.equals("HTTP/1.1 200 OK"))
      	    	if(content.indexOf("")!=-1)
      	    	    return true;

      
  	    } catch(MalformedURLException e) {
  	    	e.printStackTrace();
  	    } catch(IOException e) {
  	    	e.printStackTrace();
  	    } catch(Exception e) {
  	    	e.printStackTrace();
  	    }
  	    return false;
	}
*/
	
	public boolean validateComponent(String sURL) {
		
  	    try {
  	    	
  	    	URLConnection connection = new URL(sURL).openConnection();
  	    	
  	    	BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
  	    	
  	    	
  	    	StringBuffer line = new StringBuffer();
  	    	String response = in.readLine();
  	    	
  	    	MessageLogger.out.println(response+ " Response");
  	    	while(response != null) {
      	    	line.append(response+"\n");
      	    	response = in.readLine();
      	    }
      	    in.close();
            
      	    String header = connection.getHeaderField(0);
      	    
      	    MessageLogger.out.println("================ "+header);
      	    if(header.indexOf("OK")!= -1) {
      	    	MessageLogger.out.println("success page");
      	    	
      	    	MessageLogger.out.println(response  + " Sheldon");
      	    	
      	    	if(line.indexOf("ACTIVE") != -1) {
      	    		MessageLogger.out.println("Without ACTIVE");
      	    		return true;
      	    	}
      	    } else 
      	    	MessageLogger.out.println(line);
      	    	
	      
  	    } catch(MalformedURLException e) {
//  	    	e.printStackTrace(MessageLogger.out);
  	    } catch(IOException e) {
//  	    	e.printStackTrace(MessageLogger.out);
  	    } catch(Exception e) {
//  	    	e.printStackTrace(MessageLogger.out);
  	    }
  	    return false;
	}
	
	
	public boolean restartServlet() {
		File classFile  = new File(classFileName);
				
		long timestamp = classFile.lastModified();
		
		long currentTime = System.currentTimeMillis();
		
		return classFile.setLastModified(currentTime);

	}
	
	public boolean startServlet() {
		File classFile  = new File(classFileName);
		File fakeFile   = new File(classFileName);
		File stoppedClassFile = new File(stoppedClassFileName);
				
		if(stoppedClassFile.exists())
			if(fakeFile.delete())
				return stoppedClassFile.renameTo(classFile);
		
		return false;
	}
}
