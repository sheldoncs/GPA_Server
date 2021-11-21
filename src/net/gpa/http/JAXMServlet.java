package net.gpa.http;

import gpa.service.exceptions.MalRequestFormatException;
import gpa.service.exceptions.ReplyException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.gpa.util.MessageLogger;;





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
public abstract class JAXMServlet extends SAAJServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static Logger logger = Logger.getLogger("Samples/Book");

    // The XML Schema namespace
    protected static final String XMLSCHEMA_URI = "http://www.w3.org/2001/XMLSchema";
    
    //soap11-enc="http://schemas.xmlsoap.org/soap/encoding/"
    
    protected static final String SOAP_ENC_URI = "http://schemas.xmlsoap.org/soap/encoding/";
    
    // The XML Schema instance namespace
    protected static final String XMLSCHEMA_INSTANCE_URI = "http://www.w3.org/2001/XMLSchema-instance";

//  The XML Schema instance namespace
    protected static final String XMLSCHEMA_INSTANCE_LIST_URI = "http://java.sun.com/jax-rpc-ri/internal";
    
    protected static final String XMLSCHEMA_SOAP_ENC_URI = "http://schemas.xmlsoap.org/soap/encoding/";
    
    //soap11-enc="http://schemas.xmlsoap.org/soap/encoding/"
    // Namespace prefix for XML Schema
    protected static final String XMLSCHEMA_PREFIX = "xsd";

    // Namespace prefix for XML Schema instance
    protected static final String XMLSCHEMA_INSTANCE_PREFIX = "xsi";    

    //  Namespace prefix for service    
    protected static final String SERVICE_PREFIX = "ns0";
   
    //  Namespace prefix for list service    
    protected static final String SERVICE_LIST_PREFIX = "ns2";
   
//  The namespace prefix used for SOAP encoding
    protected static final String SOAP_ENC_PREFIX = "soap11-enc";

    // The XML service Schema namespace    
    protected static String SERVICE_URI;
    
    protected static String NS_PREFIX = "ans1";

    private SOAPFactory soapFactory;
    
    protected ServletContext servletContext;    
    
    protected Timer leasingTimer;
    
    ServletConfig servletConfig;
    
    protected Properties endPoint;
   
    protected String effTerm;
    
    private List operations;
 
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        
        this.servletConfig = servletConfig; 
        
        
        servletContext = servletConfig.getServletContext();
		endPoint = (Properties)servletContext.getAttribute("EndPointProperties");
		
		if (endPoint == null)
		  System.out.println("EndPoint is Null");
		else
			System.out.println("EndPoint is not Null");
		
		SERVICE_URI = endPoint.getProperty("AccessPoint")+"?WSDL";
		
		String logFile = endPoint.getProperty("LogFile");

        try {
        	  MessageLogger.setErr(new PrintStream(new FileOutputStream(new File(logFile))));
        	  MessageLogger.setOut(new PrintStream(new FileOutputStream(new File(logFile))));
        	  
        	  
        } catch(Exception e) {
              e.printStackTrace(MessageLogger.out);
        }            
		
		try {
			  URL url = servletContext.getResource(endPoint.getProperty("WSDLFileName"));
			  
			  
			  WSDLFactory factory = WSDLFactory.newInstance();
			  WSDLReader wsdlReader = factory.newWSDLReader();

			  Definition wsdlDefinition = wsdlReader.readWSDL(url.toString());
              
              
			  parseWSDLFile(wsdlDefinition);
		} catch (WSDLException e) {
			e.printStackTrace(MessageLogger.out);
		} catch (Exception e) {
			e.printStackTrace(MessageLogger.out);
		}
		
        try {
              leasingTimer = new Timer();
              soapFactory = SOAPFactory.newInstance();
             


        } catch(SOAPException ex) {
            throw new ServletException("Unable to create soap factory" + ex.getMessage());
        }
    }
    
	public SOAPMessage onMessage(SOAPMessage message) {
		
		try {
			
			SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
            SOAPHeader header = message.getSOAPHeader();
            SOAPBody body = message.getSOAPBody();
            
            SOAPMessage replyMessage = getReplyMessage();

            SOAPBody replyBody   = replyMessage.getSOAPPart().getEnvelope().getBody();            
            SOAPBody requestBody = message.getSOAPPart().getEnvelope().getBody();
            
//          ==========================================================
            
            Iterator iter = requestBody.getChildElements();
            
            try {
             DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
           	 Document responseRef = builder.newDocument();
           	 ArrayList responseDoc =  new ArrayList();

           	 int responseID = 1;
           	 
                while (iter.hasNext()) {
                    SOAPElement element = (SOAPElement)iter.next();
                    Name elementName = element.getElementName();
               
                    String requestName = elementName.getLocalName(); 
                    if(!validateRequest(requestName)) {
                        // Unrecognized request - this is a fault.                	
                        createFault(replyBody, "SOAP-ENV:Client.UnknownRequest", "Unrecognized request", SERVICE_URI, requestName);
                        throw new MalRequestFormatException();
                    }
               
                    Iterator subIter = element.getChildElements();                
                    if(!subIter.hasNext() && paramaterRequired(requestName)) {
                       // No request - this is a fault
                    	createFault(replyBody, "SOAP-ENV:Client.MissingRequest", "Missing request", SERVICE_URI, "No request found");
                    	throw new MalRequestFormatException();
                    }

                	 Element responseElement = responseRef.createElement(requestName + "Response");    	
                	 responseRef.appendChild(responseElement);    	

                	Document resultDoc = performTask(requestName,responseID,subIter);
                	 
                        if(responseRequired(requestName)) {
                        	if(resultDoc == null)
                        		throw new ReplyException();
                    	    responseElement.appendChild(getResponseDoc(responseRef,responseID++));
                    	    responseDoc.add(resultDoc);
                        }
                   
                }   
                replyBody.addDocument(responseRef);
                for(int i=0;i<responseDoc.size();i++)
                    replyBody.addDocument((Document)responseDoc.get(i));
               
                
           } catch(Exception e) {
            	createFault(replyBody, "SOAP-ENV:Client.InternalError", "Message Error", SERVICE_URI, e.getMessage());
               logger.log(Level.SEVERE,"Error in processing or replying to a message", e);            	
               e.printStackTrace(MessageLogger.out);
           }                 
           
           
           MessageLogger.out.println("MessageLogger.out");
           
           //replyMessage.writeTo(MessageLogger.out);
           
          
           
           return replyMessage;
            
		} catch (Exception ex){
			ReplyException e = new ReplyException("Error in processing or replying to a message");
			MessageLogger.out.println(ex.getMessage());
			
		}
		
		return null;
	}
	protected void registerLeasingTask(TimerTask task) {
	        leasingTimer.schedule(task,0,2000);
	        
	}
	private void parseWSDLFile(Definition wsdlDefinition) {
		Map dMap = wsdlDefinition.getServices();
		for(Iterator i = dMap.keySet().iterator();i.hasNext();) {
			QName name = (QName)i.next();
			Service service = wsdlDefinition.getService(name);
				
			Map sMap = service.getPorts();
			for(Iterator si = sMap.keySet().iterator();si.hasNext();) {
				String pname = (String)si.next();
				Binding binding = service.getPort(pname).getBinding();
				
				PortType portType = binding.getPortType();
					
				operations = portType.getOperations();
				
			}
		}
    }
    private SOAPMessage getReplyMessage() throws SOAPException {
    	
        SOAPMessage reply = msgFactory.createMessage();

        SOAPEnvelope replyEnvelope = reply.getSOAPPart().getEnvelope();
        replyEnvelope.getHeader().detachNode();
      
        replyEnvelope.addNamespaceDeclaration(SERVICE_PREFIX, SERVICE_URI);              
        replyEnvelope.addNamespaceDeclaration(XMLSCHEMA_PREFIX, XMLSCHEMA_URI);
        replyEnvelope.addNamespaceDeclaration(XMLSCHEMA_INSTANCE_PREFIX, XMLSCHEMA_INSTANCE_URI);              
        replyEnvelope.addNamespaceDeclaration(SOAP_ENC_PREFIX,SOAP_ENC_URI);
        replyEnvelope.addNamespaceDeclaration(SERVICE_LIST_PREFIX,XMLSCHEMA_INSTANCE_LIST_URI);
        replyEnvelope.setEncodingStyle(SOAPConstants.URI_NS_SOAP_ENCODING);  
        

        return reply;            
    }
    private boolean validateRequest(String requestName) {
    	MessageLogger.out.println("Request == >"+requestName);
		Iterator opIterator = operations.iterator();
		while (opIterator.hasNext()) {
			Operation operation = (Operation)opIterator.next();
			if (!operation.isUndefined()) {
				if(requestName.equals(operation.getName()))
					return true;
			}
		}	
        
    	return false;
    }
    private void createFault(SOAPBody replyBody, String faultCode, String faultString,String faultActor, String detailString) throws SOAPException {
        
	replyBody.removeContents();
	
   SOAPFault fault = replyBody.addFault();
   fault.setFaultCode(faultCode);
   fault.setFaultString(faultString);
   fault.setFaultActor(faultActor);
   if (detailString != null) {
       Name detailName = soapFactory.createName("BookFaultDetail", SERVICE_PREFIX, SERVICE_URI);
       Detail detail = fault.addDetail();
       DetailEntry detailEntry = detail.addDetailEntry(detailName);
       detailEntry.addTextNode(detailString);
   }
  }
    private boolean paramaterRequired(String requestName) {
		Iterator opIterator = operations.iterator();
		while (opIterator.hasNext()) {
			Operation operation = (Operation)opIterator.next();
			if (!operation.isUndefined()) {
				if(requestName.equals(operation.getName())) {
					
					return (operation.getParameterOrdering() != null);
				}
			}
		}	
    	
    	return false;
    }
    private boolean responseRequired(String requestName) {
		Iterator opIterator = operations.iterator();
		while (opIterator.hasNext()) {
			Operation operation = (Operation)opIterator.next();
			if (!operation.isUndefined()) {
				if(requestName.equals(operation.getName())) {
					return (operation.getOutput().getMessage().getParts().size() != 0);
				}
			}
		}	
    	
    	return false;
    }
    private Element getResponseDoc(Document doc,int id) throws Exception {
       	
        	Element resultElement = doc.createElement("result");		
        	resultElement.setAttribute("href","#ID"+id);
        	
        	return resultElement;
    }
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            MimeHeaders headers = getHeaders(request);
            if(checkForContent(headers)) {
                writeInvalidMethodType(response, "Invalid Method Type");
                if(logger.isLoggable(Level.INFO))
                {
                    logger.severe("JAXRPC.JAXRPCSERVLET.63: must use Post for this type of request");
                    logger.severe("Must use Http POST for the service request");
                }
                return;
            }
        } catch(Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new ServletException(e.getMessage());
        }
        
        if(request.getQueryString() != null) {        	
            if(request.getQueryString().equals("WSDL")) {
            	    MessageLogger.out.println(endPoint.getProperty("WSDLFileName"));
                	publishWSDL(request, response,endPoint.getProperty("WSDLFileName"));
            } else if(request.getQueryString().equals("model")) {
                    response.setContentType("application/x-gzip");
                    InputStream istream = servletContext.getResourceAsStream(endPoint.getProperty("ModelFileName"));
                    copyStream(istream, response.getOutputStream());
                    istream.close();
            } else {
                writeNotFoundErrorPage(response, "Invalid request");
            }
        } else
        	if(request.getPathInfo() == null) {
                response.setContentType("text/html");
                PrintWriter out = response.getWriter();
                out.println("<html>");
                out.println("<head><title> Web Services </title></head>");
                out.println("<body>");
                out.println("<h1>Web Services</h1>");

                out.println("<table width='100%' border='1'>");
                
                
                out.println("<tr>");
                out.println("<td> Port Name </td>");
                out.println("<td> Status </td>");
                out.println("<td> Information </td>");
                out.println("</tr>");
                
                String baseAddress = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
                
                String endpointAddress = baseAddress + endPoint.getProperty("URLPattern");
                
                out.println("<tr>");
                    
                out.println("<td>");
                    
                    out.println("<table border='0'>");
                    out.println("<tr><td>Address:</td>");
                    out.println("<td>"+endpointAddress+"</td></tr>");
                    out.println("<tr><td>WSDL:</td>");
                    out.println("<td><a href="+endpointAddress+"?WSDL>"+endpointAddress+"?WSDL</a></td></tr>");
                    out.println("<tr><td>Model:</td>");
                    out.println("<td><a href="+endpointAddress+"?model>"+endpointAddress+"?model</a></td></tr>");
                    
                    out.println("<tr><td colspan=2></td>");
                    
                    out.println("<tr><td colspan=2><table border=1 cellspacing=0 width=100% cellpadding=5>");
                    
                    
                    //out.println("<tr><td><table>");
                    
                    //out.println("<tr><td>Process Time of this Bridge</td>");
                    
                    //out.println("</table></td></tr>");
                    
                    out.println("</table></td></tr>");
                    
                    out.println("</table>");
                    out.println("</td>");
                    
                    out.println("<td>");
                    out.println((getComponentStatus()?"Shutting Down,Stopped":" ACTIVE "));
                    out.println("</td>");
                    
                    
                    out.println("</tr>");  

                out.println("</table>");

                out.println("</body>");
                out.println("</html>");
              } else {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html>");
            out.println("<head><title> Web Services </title></head>");
            out.println("<body>");
            out.println("</body>");
            out.println("</html>");
        }    
    }
    protected void writeNotFoundErrorPage(HttpServletResponse response, String message) throws IOException {
    	response.setStatus(404);
    	response.setContentType("text/html");
    	PrintWriter out = response.getWriter();
    	out.println("<html>");
    	out.println("<head><title> Web Services </title></head>");
    	out.println("<body>");
    	out.println("<h1>404 Not Found: "+message+"</h1>");
    	out.println("</body>");
    	out.println("</html>");
    }
    protected void writeInvalidMethodType(HttpServletResponse response, String message)
        throws IOException {
        response.setStatus(405);
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head><title> Web Services </title></head>");
        out.println("<body>");
        out.println("JAXRPC.JAXRPCSERVLET.63: must use Post for this type of request"+ message);
        out.println("</body>");
        out.println("</html>");
    }
    boolean checkForContent(MimeHeaders headers) {
        return checkContentType(headers) && checkContentLength(headers);
    }
    protected boolean checkContentType(MimeHeaders headers) {
        String contentTypes[] = headers.getHeader("Content-Type");
        return contentTypes != null && contentTypes.length >= 1 && contentTypes[0].indexOf("text/xml") != -1;
    }
    protected boolean checkContentLength(MimeHeaders headers) {
        String contentLength[] = headers.getHeader("Content-Length");
        if(contentLength != null && contentLength.length > 0) {
            int length = (new Integer(contentLength[0])).intValue();
            if(length > 0)
                return true;
        }
        return false;
    }
    protected void publishWSDL(HttpServletRequest request, HttpServletResponse response, String wsdlFile) throws IOException {
    	response.setContentType("text/xml");    	
    	
    	OutputStream outputStream = response.getOutputStream();
    	InputStream inputStream =  servletContext.getResourceAsStream((wsdlFile));
    	
    	copyStream(inputStream,outputStream);
    }
    protected void copyStream(InputStream istream, OutputStream ostream) throws IOException {
        byte[] buf = new byte[2048];
        int num = 0;
        while ((num = istream.read(buf)) != -1) {
            ostream.write(buf, 0, num);
        }
        ostream.flush();
    }
    protected abstract Document performTask(String requestName,int id,Iterator paramater);
}
