/*
 * Created on 2004/8/5
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package gpa.http.monitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Timer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.gpa.util.MessageLogger;





/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ServiceMonitor extends HttpServlet implements Monitor {

    final long TIME2START = 10000;

	ServiceStarter st;
	
	Timer timer;
	
	URL url;
	
	String monitoredPoint;
	String contextPath;
	String logFile = "C:\\Monitor.txt";
	
	int crashCount = 0;
	
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        
        timer = new Timer();
        try {
        	 
        	  System.out.println("Start Here"); 
        	  MessageLogger.setErr(new PrintStream(new FileOutputStream(new File(logFile))));
        	  MessageLogger.setOut(new PrintStream(new FileOutputStream(new File(logFile))));
        	
        	  MessageLogger.out.println("Start Here");
        	  
        } catch(Exception e) {
              e.printStackTrace(MessageLogger.out);
        }
        
    }
    
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	doGet(req,resp);
    }
    
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	resp.setContentType("text/html");
    	PrintWriter out = resp.getWriter();

    	String command = req.getParameter("command");
    	if(command != null) {
    		if(command.equals("startComponent")) {
    			if(startComponent())
    				returnMessage(out,"OK - Started application");
    			else
    				returnMessage(out,"FAIL - No context exists");
    		} else if(command.equals("startMonitor")) {
    			if(startMonitor(req.getParameter("contextPath"))) 
    				returnMessage(out,"OK - Start monitoring");
    			else
    				returnMessage(out,"FAIL - fail to start monitoring");
    		}
    	} else {
    		
    		MessageLogger.out.println("Message Logger Start Here");
    		returnMessage(out,inspection());
    	}
    }
    
    private String inspection() {
    	if(st == null || url == null)
    		return "Status : sleeping";
    	else
    		return "Status : monitoring Component at "+contextPath+"<BR>"+
			       "         local file:"+url.getFile()+"<BR>"+
			       "         times be crashed:"+crashCount;
    		
    }
    
    private void returnMessage(PrintWriter out,String sMessage) {
    	out.println(sMessage);
    }
    
    private boolean startMonitor(String contextPath) {
    	ServletContext context = getServletContext().getContext(contextPath);
    	MessageLogger.out.println("Start Monitoring");
    	if(context != null) {
    	   url = (URL)context.getAttribute("classURL");
    	   monitoredPoint = (String)context.getAttribute("MonitoredPoint");
    	   this.contextPath = contextPath;
    	   
    	   st = new ServiceStarter(url);
    	   
    	   timer.schedule(new MonitorTask(this,st,monitoredPoint), 0, TIME2START);
    	   return true;
    	}
    	return false;
    }
    
    public boolean startComponent() {
    	return st.restartServlet();
    	//return st.startServlet();
    }
    
    public void addCrashCount() {
    	crashCount ++;
    }
}
