package net.gpa.http;

import gpa.http.monitor.ServiceStarter;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Timer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;

import net.gpa.remote.GPARemoteService;
import net.gpa.util.CourseAndGrade;
import net.gpa.util.DegreeAwarded;
import net.gpa.util.GPA;
import net.gpa.util.MessageLogger;
import net.gpa.util.StudentProgram;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class GPAServlet extends JAXMServlet {

	private int interval;
	
	public void init(ServletConfig servletConfig) throws ServletException {

		super.init(servletConfig);

		leasingTimer = new Timer();

		Properties uddiProperties = (Properties) servletConfig
				.getServletContext().getAttribute("UDDIProperties");

		try {
			URL url = this.getClass().getResource(
					"/" + this.getClass().getName().replace('.', '/')
							+ ".class");

			servletConfig.getServletContext().setAttribute("classURL", url);
			servletConfig.getServletContext().setAttribute("MonitoredPoint",
					endPoint.getProperty("AccessPoint"));

			sv = new ServiceStarter(url);
			sv.startMonitor(endPoint.getProperty("Monitor"),
					endPoint.getProperty("ContextPath"));
		} catch (Exception e) {
			MessageLogger.out.println(e.getMessage());
		}
		/*
		
		interval= 60;
		
		GPATask task = new GPATask(endPoint);
		
		
		Timer timer = new Timer();
		timer.schedule(task,0, interval * 180 * 1000);
		*/
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Document performTask(String requestName, int id,
			Iterator parameter) {
		// TODO Auto-generated method stub

		GPARemoteService rs = new GPARemoteService();

		try {

			HashMap hmParameter = parseParameter(parameter, requestName);

			MessageLogger.out.println(requestName);

			if (requestName.equals("getConvertedGPA")) {
			
				GPA gpa = rs.getConvertedGPA(
						hmParameter.get("term").toString(),
						hmParameter.get("id").toString(),
						hmParameter.get("isDegreeFlag").toString()
								.equals("true"));
				MessageLogger.out.println("gpa=" + gpa.getGpa());

				return serializeGPA(id, gpa);

			} else if  (requestName.equals("getCourseAndGrades")) {
				
				
				ArrayList list = rs.getCourseAndGrades(hmParameter.get("term").toString(), 
						hmParameter.get("id").toString(), hmParameter.get("isPreviousGPA").toString()
						.equals("true"),hmParameter.get("isDegreeFlag").toString()
						.equals("true"));
				
				
				MessageLogger.out.println("Course And Grades =" +hmParameter.get("term").toString() + " List Size = " + list.size());
				
				return serializeCourseAndGrades(id,list);

			} else if  (requestName.equals("getDegreeAwarded")) {
				
				DegreeAwarded da = rs.getDegreeAwarded(hmParameter.get("id").toString());
				
				MessageLogger.out.println(da.getAward()+ " "+da.getDegree());
				
				return  serializeDegreeAwarded(id, da);
				
			} else if (requestName.equals("getStudentID")){
				
				CourseAndGrade cag = rs.getStudentID(Long.parseLong(hmParameter.get("pidm").toString()));
				
				MessageLogger.out.println(cag.getId());
				
				return serializeCourseAndGrades(id, cag);
			} else if (requestName.equals("getStudentProgram")){
				
				StudentProgram studentProgram = rs.getStudentProgram(hmParameter.get("id").toString());
	            
				return serializeStudentProgram (id, studentProgram);
			}
			
			MessageLogger.out.println(requestName);
		} catch (Exception e) {

		}

		return null;
	}

	private HashMap parseParameter(Iterator parameter, String requestName) {

		HashMap hmParameter = new HashMap();

		while (parameter.hasNext()) {
			SOAPElement subElement = (SOAPElement) parameter.next();
			Name subElementName = subElement.getElementName();

			MessageLogger.out.println("ParameterName ==> "
					+ subElementName.getLocalName());
			MessageLogger.out.println(subElement.getValue());

			if (requestName.equals("getConvertedGPA")) {

				if (subElementName.getLocalName().equals("String_1"))
					hmParameter.put("term", subElement.getValue());
				if (subElementName.getLocalName().equals("String_2"))
					hmParameter.put("id", subElement.getValue());
				if (subElementName.getLocalName().equals("boolean_3"))
					hmParameter.put("isDegreeFlag", subElement.getValue());

			} else if (requestName.equals("getCourseAndGrades")) {

				if (subElementName.getLocalName().equals("String_1"))
					hmParameter.put("term", subElement.getValue());
				if (subElementName.getLocalName().equals("String_2"))
					hmParameter.put("id", subElement.getValue());
				if (subElementName.getLocalName().equals("boolean_3"))
					hmParameter.put("isPreviousGPA", subElement.getValue());
				if (subElementName.getLocalName().equals("boolean_4"))
					hmParameter.put("isDegreeFlag", subElement.getValue());
				
			} else if (requestName.equals("getAccumGPA")
					|| (requestName.equals("getCurrentDegreeGPA"))) {

				if (subElementName.getLocalName().equals("String_1"))
					hmParameter.put("id", subElement.getValue());
				if (subElementName.getLocalName().equals("String_2"))
					hmParameter.put("oldterm", subElement.getValue());
				if (subElementName.getLocalName().equals("String_3"))
					hmParameter.put("newgpa", subElement.getValue());
				if (subElementName.getLocalName().equals("String_4"))
					hmParameter.put("newterm", subElement.getValue());

			}  else if (requestName.equals("getDegreeAwarded")) {

				if (subElementName.getLocalName().equals("String_1"))
					hmParameter.put("id", subElement.getValue());	
			} else if (requestName.equals("getStudentID")){
				if (subElementName.getLocalName().equals("long_1"))
					hmParameter.put("pidm", subElement.getValue());
			} else if (requestName.equals("getStudentProgram")){
				if (subElementName.getLocalName().equals("String_1"))
					hmParameter.put("id", subElement.getValue());
			}

		}

		return hmParameter;
	}

	private Document serializeStudentProgram (int id, StudentProgram studentProgram) throws Exception {
		
		DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document doc = builder.newDocument();

		MessageLogger.out.println(studentProgram.getId() + " " + studentProgram.getFirstname()+ " "+studentProgram.getLastname()+ " "+studentProgram.getFaculty() + " "+studentProgram.getMajor()+" "+studentProgram.getProgram() );
		
		Element objectElement = doc.createElement("StudentProgram");
		doc.appendChild(objectElement);
		objectElement.setAttribute("id", "ID" + id);
		objectElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
				SERVICE_PREFIX + ":StudentProgram");

		Element idObjectElement = doc.createElement("id");
		idObjectElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
				XMLSCHEMA_PREFIX + ":string");
		idObjectElement
				.appendChild(doc.createTextNode(studentProgram.getId()));
		objectElement.appendChild(idObjectElement);

		Element lastNameObjectElement = doc.createElement("lastname");
		lastNameObjectElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
				XMLSCHEMA_PREFIX + ":string");
		lastNameObjectElement
				.appendChild(doc.createTextNode(studentProgram.getLastname()));
		objectElement.appendChild(lastNameObjectElement);
		
		Element firstNameObjectElement = doc.createElement("firstname");
		firstNameObjectElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
				XMLSCHEMA_PREFIX + ":string");
		firstNameObjectElement
				.appendChild(doc.createTextNode(studentProgram.getFirstname()));
		objectElement.appendChild(firstNameObjectElement);

		Element facultyObjectElement = doc.createElement("faculty");
		facultyObjectElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
				XMLSCHEMA_PREFIX + ":string");
		facultyObjectElement
				.appendChild(doc.createTextNode(studentProgram.getFaculty()));
		objectElement.appendChild(facultyObjectElement);
		
		Element programObjectElement = doc.createElement("program");
		programObjectElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
				XMLSCHEMA_PREFIX + ":string");
		programObjectElement
				.appendChild(doc.createTextNode(studentProgram.getProgram()));
		objectElement.appendChild(programObjectElement);
		
		Element majorObjectElement = doc.createElement("major");
		majorObjectElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
				XMLSCHEMA_PREFIX + ":string");
		majorObjectElement
				.appendChild(doc.createTextNode(studentProgram.getMajor()));
		objectElement.appendChild(majorObjectElement);
		
		return doc;
	}
	private Document serializeGPA(int id, GPA gpa) throws Exception {

		DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document doc = builder.newDocument();

		Element gpaObjectElement = doc.createElement("GPA");
		doc.appendChild(gpaObjectElement);
		gpaObjectElement.setAttribute("id", "ID" + id);
		gpaObjectElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
				SERVICE_PREFIX + ":GPA");

		Element gpaElement = doc.createElement("gpa");
		gpaElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
				XMLSCHEMA_PREFIX + ":double");
		gpaElement
				.appendChild(doc.createTextNode(Double.toString(gpa.getGpa())));
		gpaObjectElement.appendChild(gpaElement);

		return doc;

	}
	private Document serializeCourseAndGrades(int id, CourseAndGrade cag)
			throws Exception {
		
		DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document doc = builder.newDocument();
		Element cagObjectElement = doc.createElement("CourseAndGrade");
		
		
		doc.appendChild(cagObjectElement);
		cagObjectElement.setAttribute("id", "ID" + id);
		cagObjectElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
				SERVICE_PREFIX + ":CourseAndGrade");

		Element idElement = doc.createElement("id");
		idElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
				XMLSCHEMA_PREFIX + ":string");
		idElement
				.appendChild(doc.createTextNode(cag.getId()));
		cagObjectElement.appendChild(idElement);

		return doc;
	}
	private Document serializeDegreeAwarded(int id, DegreeAwarded da) throws Exception {

		DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document doc = builder.newDocument();

		Element degreeAwardedObjectElement = doc.createElement("DegreeAwarded");
		doc.appendChild(degreeAwardedObjectElement);
		degreeAwardedObjectElement.setAttribute("id", "ID" + id);
		degreeAwardedObjectElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
				SERVICE_PREFIX + ":degreeAwarded");

		Element awardedElement = doc.createElement("awarded");
		awardedElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
				XMLSCHEMA_PREFIX + ":string");
		awardedElement
				.appendChild(doc.createTextNode(da.getAward()));
		degreeAwardedObjectElement.appendChild(awardedElement);
		
		Element degreeElement = doc.createElement("degree");
		degreeElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
				XMLSCHEMA_PREFIX + ":string");
		degreeElement
				.appendChild(doc.createTextNode(da.getDegree()));
		degreeAwardedObjectElement.appendChild(degreeElement);

		return doc;

	}
/*private Document serializeLecturerCourses(int id, ArrayList list)
			throws Exception {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document doc = builder.newDocument();

		Element courseListElement = doc.createElement("anyType");
		doc.appendChild(courseListElement);
		courseListElement.setAttribute("id", "ID" + id);
		courseListElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
				"ns2" + ":arrayList");
		courseListElement.setAttribute(SOAP_ENC_PREFIX + ":arrayType",
				XMLSCHEMA_PREFIX + ":anyType[]");

		Iterator iterator = list.iterator();

		while (iterator.hasNext()) {

			Element courseInfoElement = doc.createElement("item");
			courseListElement.appendChild(courseInfoElement);
			courseInfoElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
					SERVICE_PREFIX + ":LecturerCourses");

			CourseProperties course = (CourseProperties) iterator.next();

			
			  MessageLogger.out.println(course.getCourseName()
			  +","+course.getEndTime()+","+course.getStartTime()+","+
			  course.getCourseType
			  ()+","+course.getBuilding()+","+course.getRoom
			  ()+","+course.getDay()+
			  ","+course.getEndDate()+","+course.getStartDate
			  ()+","+course.getSubjCode()+","+course.getCrseNumb());
			

			Element courseNameElement = doc.createElement("courseName");
			courseInfoElement.appendChild(courseNameElement);
			courseNameElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
					XMLSCHEMA_PREFIX + ":string");
			course.setCourseName(course.getCourseName());
			courseNameElement.appendChild(doc.createTextNode(course
					.getCourseName()));

			Element endTimeElement = doc.createElement("endTime");
			courseInfoElement.appendChild(endTimeElement);
			endTimeElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
					XMLSCHEMA_PREFIX + ":string");
			endTimeElement.appendChild(doc.createTextNode(course.getEndTime()));

			Element startTimeElement = doc.createElement("startTime");
			courseInfoElement.appendChild(startTimeElement);
			startTimeElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
					XMLSCHEMA_PREFIX + ":string");
			startTimeElement.appendChild(doc.createTextNode(course
					.getStartTime()));

			Element courseTypeElement = doc.createElement("courseType");
			courseInfoElement.appendChild(courseTypeElement);
			courseTypeElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
					XMLSCHEMA_PREFIX + ":string");
			courseTypeElement.appendChild(doc.createTextNode(course
					.getCourseType()));

			Element buildingElement = doc.createElement("building");
			courseInfoElement.appendChild(buildingElement);
			buildingElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
					XMLSCHEMA_PREFIX + ":string");
			course.setBuilding(course.getBuilding());
			buildingElement
					.appendChild(doc.createTextNode(course.getBuilding()));

			Element roomElement = doc.createElement("room");
			courseInfoElement.appendChild(roomElement);
			roomElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
					XMLSCHEMA_PREFIX + ":string");
			course.setRoom(course.getRoom());
			roomElement.appendChild(doc.createTextNode(course.getRoom()));

			Element dayElement = doc.createElement("day");
			courseInfoElement.appendChild(dayElement);
			dayElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
					XMLSCHEMA_PREFIX + ":string");
			course.setDay(course.getDay());
			dayElement.appendChild(doc.createTextNode(course.getDay()));

			Element endDateElement = doc.createElement("endDate");
			courseInfoElement.appendChild(endDateElement);
			endDateElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
					XMLSCHEMA_PREFIX + ":string");
			endDateElement.appendChild(doc.createTextNode(course.getEndDate()));

			Element startDateElement = doc.createElement("startDate");
			courseInfoElement.appendChild(startDateElement);
			startDateElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
					XMLSCHEMA_PREFIX + ":string");
			startDateElement.appendChild(doc.createTextNode(course
					.getStartDate()));

			Element subjCodeElement = doc.createElement("subjCode");
			courseInfoElement.appendChild(subjCodeElement);
			subjCodeElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
					XMLSCHEMA_PREFIX + ":string");
			subjCodeElement
					.appendChild(doc.createTextNode(course.getSubjCode()));

			Element crseNumbElement = doc.createElement("crseNumb");
			courseInfoElement.appendChild(crseNumbElement);
			crseNumbElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
					XMLSCHEMA_PREFIX + ":string");
			crseNumbElement
					.appendChild(doc.createTextNode(course.getCrseNumb()));

			Element crnElement = doc.createElement("crn");
			courseInfoElement.appendChild(crnElement);
			crnElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
					XMLSCHEMA_PREFIX + ":string");
			crnElement.appendChild(doc.createTextNode(course.getCrn()));

			Element idNumberElement = doc.createElement("idNumber");
			courseInfoElement.appendChild(idNumberElement);
			idNumberElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
					XMLSCHEMA_PREFIX + ":string");
			idNumberElement
					.appendChild(doc.createTextNode(course.getIdNumber()));
		}

		return doc;
	}
*/
	private Document serializeCourseAndGrades(int id, ArrayList list)
			throws Exception {

		DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document doc = builder.newDocument();

		
		
		
		Element courseListElement = doc.createElement("anyType");
		doc.appendChild(courseListElement);
		courseListElement.setAttribute("id", "ID" + id);
		courseListElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
				"ns2" + ":arrayList");
		courseListElement.setAttribute(SOAP_ENC_PREFIX + ":arrayType",
				XMLSCHEMA_PREFIX + ":anyType[]");

		Iterator iterator = list.iterator();
		
	

        while (iterator.hasNext()){
        	
        	Element courseandGradeElement = doc.createElement("item");
			courseListElement.appendChild(courseandGradeElement);
			courseandGradeElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
					SERVICE_PREFIX + ":CourseAndGrade");
			
			
			CourseAndGrade cag = (CourseAndGrade) iterator.next();

			
			
			Element clasElement = doc.createElement("clas");
			courseandGradeElement.appendChild(clasElement);
			clasElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
					XMLSCHEMA_PREFIX + ":string");
			clasElement.appendChild(doc.createTextNode(cag.getClas()));
			
			Element subjCodeElement = doc.createElement("subjCode");
			courseandGradeElement.appendChild(subjCodeElement);
			subjCodeElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
					XMLSCHEMA_PREFIX + ":string");
			subjCodeElement.appendChild(doc.createTextNode(cag.getSubjCode()));
			
			Element courseNumbElement = doc.createElement("courseNumb");
			courseandGradeElement.appendChild(courseNumbElement);
			courseNumbElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
					XMLSCHEMA_PREFIX + ":string");
			courseNumbElement.appendChild(doc.createTextNode(cag.getCourseNumb()));
			
			Element gradeElement = doc.createElement("grade");
			courseandGradeElement.appendChild(gradeElement);
			gradeElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
					XMLSCHEMA_PREFIX + ":string");
			gradeElement.appendChild(doc.createTextNode(cag.getGrade()));
			
			Element creditHrElement = doc.createElement("creditHr");
			courseandGradeElement.appendChild(creditHrElement);
			creditHrElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
					XMLSCHEMA_PREFIX + ":string");
			creditHrElement.appendChild(doc.createTextNode(Double.toString(cag.getCreditHr())));
			
			Element qualityPointsElement = doc.createElement("qualityPoints");
			courseandGradeElement.appendChild(qualityPointsElement);
			qualityPointsElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
					XMLSCHEMA_PREFIX + ":string");
			qualityPointsElement.appendChild(doc.createTextNode(Double.toString(cag.getQualityPoints())));
			
        }

		return doc;

	}
	private Document serializeCourseAndGrades(int id, HashMap map)
			throws Exception {

		DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document doc = builder.newDocument();

		Element mapEntryElement = doc.createElement("anyType");
		doc.appendChild(mapEntryElement);
		mapEntryElement.setAttribute("id", "ID" + id);
		mapEntryElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
				"ns2" + ":hashMap");
		mapEntryElement.setAttribute(SOAP_ENC_PREFIX + ":arrayType",
				XMLSCHEMA_PREFIX + ":anyType[]");
		
		Iterator keyIterator = map.keySet().iterator();

        while (keyIterator.hasNext()){
        	
        	Element itemElement = doc.createElement("item");
        	mapEntryElement.appendChild(itemElement);
        	
        	String key = keyIterator.next().toString();
        	CourseAndGrade cag = (CourseAndGrade)map.get(key);
        	
        	Element keyElement = doc.createElement("key");
        	itemElement.appendChild(keyElement);
			keyElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
					XMLSCHEMA_PREFIX + ":string");
			keyElement.appendChild(doc.createTextNode(key));
			
			Element valueElement = doc.createElement("value");
			itemElement.appendChild(valueElement);
			valueElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
					SERVICE_PREFIX + ":CourseAndGrade");
			
			Element clasElement = doc.createElement("clas");
			valueElement.appendChild(clasElement);
			clasElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
					XMLSCHEMA_PREFIX + ":string");
			clasElement.appendChild(doc.createTextNode(cag.getClas()));
			
			Element subjCodeElement = doc.createElement("subjCode");
			valueElement.appendChild(subjCodeElement);
			subjCodeElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
					XMLSCHEMA_PREFIX + ":string");
			subjCodeElement.appendChild(doc.createTextNode(cag.getSubjCode()));
			
			Element courseNumbElement = doc.createElement("courseNumb");
			valueElement.appendChild(courseNumbElement);
			courseNumbElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
					XMLSCHEMA_PREFIX + ":string");
			courseNumbElement.appendChild(doc.createTextNode(cag.getCourseNumb()));
			
			Element gradeElement = doc.createElement("grade");
			valueElement.appendChild(gradeElement);
			gradeElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
					XMLSCHEMA_PREFIX + ":string");
			gradeElement.appendChild(doc.createTextNode(cag.getGrade()));
			
			Element creditHrElement = doc.createElement("creditHr");
			valueElement.appendChild(creditHrElement);
			creditHrElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
					XMLSCHEMA_PREFIX + ":string");
			creditHrElement.appendChild(doc.createTextNode(Double.toString(cag.getCreditHr())));
			
			Element qualityPointsElement = doc.createElement("qualityPoints");
			valueElement.appendChild(qualityPointsElement);
			qualityPointsElement.setAttribute(XMLSCHEMA_INSTANCE_PREFIX + ":type",
					XMLSCHEMA_PREFIX + ":string");
			qualityPointsElement.appendChild(doc.createTextNode(Double.toString(cag.getQualityPoints())));
			
        }

		return doc;

	}
}

/*
 * <?xml version="1.0" encoding="UTF-8"?> <SOAP-ENV:Envelope
 * xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"
 * xmlns:xsd="http://www.w3.org/2001/XMLSchema"
 * xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"> <SOAP-ENV:Body>
 * <ns1:setHashtableResponse
 * SOAP-ENV:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"
 * xmlns:ns1="MyService"> <setHashtableResult href="#id0"/>
 * </ns1:setHashtableResponse> <multiRef id="id0" SOAP-ENC:root="0"
 * xsi:type="ns2:HashMap"
 * xmlns:SOAP-ENC="http://schemas.xmlsoap.org/soap/encoding/"
 * xmlns:ns2="urn:MyService"> <item> <key xsi:type="xsd:string">profession</key>
 * <value xsi:type="xsd:string">political genius</value> </item> <item> <key
 * xsi:type="xsd:string">lname</key> <value xsi:type="xsd:string">Browne</value>
 * </item> <item> <key xsi:type="xsd:string">fname</key> <value
 * xsi:type="xsd:string">Harry</value> </item> </multiRef> </SOAP-ENV:Body>
 * </SOAP-ENV:Envelope>
 */
