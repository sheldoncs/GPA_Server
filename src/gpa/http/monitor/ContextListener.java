/*
 * Created on 2004/8/1
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gpa.http.monitor;

//import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

//import java.io.FileOutputStream;
//import java.io.PrintStream;
import java.util.Properties;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ContextListener implements ServletContextAttributeListener, ServletContextListener {
    
	private ServletContext context;
    private static final String EndPointProperties = "/WEB-INF/EndPoint.properties";
    private static final String UDDIProperties = "/WEB-INF/UDDIRegistration.properties";
    
	public ContextListener() {
		 
	}
	
    public void attributeAdded(ServletContextAttributeEvent event) {
    }

    public void attributeRemoved(ServletContextAttributeEvent event) {
    }

    public void attributeReplaced(ServletContextAttributeEvent event) {
    }

    public void contextDestroyed(ServletContextEvent event) {
        context = null;
    }
	
    public void contextInitialized(ServletContextEvent event) {
    	context = event.getServletContext();
    	
    	try {
    		  Properties endPointProperties = new Properties();
    		  Properties uddiProperties = new Properties();
    		  
    		  endPointProperties.load(context.getResourceAsStream(EndPointProperties));
    		  uddiProperties.load(context.getResourceAsStream(UDDIProperties));
    		  
    		  context.setAttribute("EndPointProperties", endPointProperties);
    		  context.setAttribute("UDDIProperties", uddiProperties);
    		  
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    }
}
