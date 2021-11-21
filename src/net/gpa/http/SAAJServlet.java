package net.gpa.http;

import gpa.http.monitor.ServiceStarter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;




import net.gpa.util.MessageLogger;



/*
 * Created on Sep 2, 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author Owner
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class SAAJServlet extends HttpServlet implements Shutdownable {

	
	/**
     * The factory used to build messages
     */
    protected MessageFactory msgFactory;
    private boolean shutdown = false;
    protected 	ServiceStarter      sv;
    /**
     * Initialisation - create the MessageFactory
     */
    protected boolean getComponentStatus() {
    	return shutdown;
    }
    public void shutdown() {
    	MessageLogger.out.println("Component is shutting down now ........");
    	shutdown = true;
    }
    protected void doShutDown() {
    	MessageLogger.out.println("Component shutdown");
    //	sv.stopServlet();
    }
    
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            msgFactory = MessageFactory.newInstance(  );
        } catch (SOAPException ex) {
            throw new ServletException("Failed to create MessageFactory", ex);
        }
    }
    /**
     * Handles a POST request from a client. The request is assumed
     * to contain a SOAP message with the HTTP binding.
     */
    public void doPost(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
                                
    	try {
            MimeHeaders headers = getHeaders(req);
            java.io.InputStream is = req.getInputStream();
            SOAPMessage msg = msgFactory.createMessage(headers, is);
            SOAPMessage reply = null;
            reply = onMessage(msg);
            
            
           
            if(reply != null) {
            	
                if(reply.saveRequired())
                    reply.saveChanges();
                resp.setStatus(200);
                putHeaders(reply.getMimeHeaders(), resp);
                OutputStream os = resp.getOutputStream();
                reply.writeTo(os);
                               
               reply.writeTo(MessageLogger.out);
                os.flush();
            } else {
                resp.setStatus(204);
            }
            
            
            
                        
            
        } catch(Exception ex) {
            throw new ServletException("SAAJ POST failed " + ex.getMessage());
        }       
    }
    /**
     * Method implemented by subclasses to handle a received SOAP message.
     * @param message the received SOAP message.
     * @return the reply message, or <code>null</code> if there is
     * no reply to be sent.
     */
    protected static MimeHeaders getHeaders(HttpServletRequest req) {
        Enumeration enumerate = req.getHeaderNames();
        MimeHeaders headers = new MimeHeaders();
        
        while(enumerate.hasMoreElements()) {
            String headerName = (String)enumerate.nextElement();
            String headerValue = req.getHeader(headerName);
            for(StringTokenizer values = new StringTokenizer(headerValue, ","); values.hasMoreTokens(); headers.addHeader(headerName, values.nextToken().trim()));
        }
        return headers;
    }
    protected static void putHeaders(MimeHeaders headers, HttpServletResponse res) {
	    
	        for(Iterator it = headers.getAllHeaders(); it.hasNext();) {
	            MimeHeader header = (MimeHeader)it.next();
	            String values[] = headers.getHeader(header.getName());

	            if(values.length == 1) {
	                res.setHeader(header.getName(), header.getValue());
	            } else {
	                StringBuffer concat = new StringBuffer();
	                for(int i = 0; i < values.length;) {
	                    if(i != 0)
	                        concat.append(',');
	                    concat.append(values[i++]);
	                }

	                res.setHeader(header.getName(), concat.toString());
	            }
	        }
	}
    protected abstract SOAPMessage onMessage(SOAPMessage message) 
        throws SOAPException; 

}
