/*
 * Created on 2004/8/5
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package gpa.http.monitor;
import java.util.TimerTask;

import net.gpa.util.MessageLogger;


//import java.io.IOException;
//import java.io.FileInputStream;


/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MonitorTask extends TimerTask {
	
	String monitoredPoint;
	ServiceStarter sv;
	Monitor monitor;
	
    public MonitorTask(Monitor monitor,ServiceStarter sv, String monitoredPoint) {
	    this.sv = sv;
	    this.monitor = monitor;
	    this.monitoredPoint = monitoredPoint;
    }
	    
	public void run() {

		 /*
        try {
      	  MessageLogger.setErr(new PrintStream(new FileOutputStream(new File("C:\\Monitor.txt"))));
      	
      } catch(Exception e) {
            e.printStackTrace(MessageLogger.out);
      }
        */ 
		try{
	    	MessageLogger.out.println("Starting validate");
		    
		    
         	if(!sv.validateComponent(monitoredPoint)) {
         		
         		MessageLogger.out.println("component fail");
         		if(!sv.restartServlet()) {
         			
//         		if(!sv.startServlet()) {
         			MessageLogger.out.println("Fail to restart");
         		} else {
         			
         			monitor.addCrashCount();
         	        MessageLogger.out.println("success crash");	
         		}
         	}
	    } catch (Exception e){
	    	e.printStackTrace(MessageLogger.out);
	    }
	}

}
