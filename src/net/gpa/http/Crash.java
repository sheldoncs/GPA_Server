/*
 * Created on Jul 20, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.gpa.http;

import java.util.Properties;

import javax.servlet.http.HttpServlet;

import net.gpa.db.GPADb;
import net.gpa.util.MessageLogger;




/**
 * @author Owner
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Crash {
	private Properties prop;
	private Shutdownable server;
	private HttpServlet servlet;
	private GPADb db;
	
	public Crash(Shutdownable server)
	{
		this.server = server;
		
	}
	public void setDb(GPADb db){
	  this.db = db;
	}
	
	public void setServlet(HttpServlet servlet)
	{
		this.servlet = servlet;
	}
	public void crashXRun()
	{
		MessageLogger.out.println("Crashing GPA Component......");
		if(server != null)
		{
		
			db.closeConn();
			try {
			MessageLogger.out.println("Starting to Destroy Component ....");			
			server.shutdown();
			
			MessageLogger.out.println("Component Destroyed");
			} catch(Exception ex){
				MessageLogger.out.println("Error on Servlet Destruction " + ex.getMessage());
			}
			
			
		}
	}
	public void setServer(Shutdownable server)
	{
		this.server = server;
	}
	
}
