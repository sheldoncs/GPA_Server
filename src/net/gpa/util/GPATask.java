package net.gpa.util;

import java.util.ArrayList;
import java.util.Properties;
import java.util.TimerTask;

import javax.servlet.http.HttpServlet;

import net.gpa.db.GPADb;
import net.gpa.db.MySQLConnection;


public class GPATask extends TimerTask {

	private String effTerm;
	private Properties endPoint;
	private int ii;
	//private Crash crash;
	//private Shutdownable server;
	private HttpServlet servlet;

	public GPATask(Properties endPoint) {

		// this.effTerm=effTerm;
		this.endPoint = endPoint;

	}

	//public void setServer(Shutdownable server) {
		//crash = new Crash(server);
	//}

	public void setServlet(HttpServlet servlet) {
		this.servlet = servlet;
	}

	public void run() {

		try {
			//crash = new Crash(server);
			//crash.setServlet(servlet);

			GPADb db = new GPADb();
			db.openConn();
			ArrayList mapPrevious = db.gatAllCourseAndGrades("200410", true,
					 true);
			MessageLogger.out.println("Gathered all the courses" + " Map Size = "+mapPrevious.size());
			MySQLConnection conn = new MySQLConnection();
			conn.connectGPADb();
			conn.addCourseAndGrades(mapPrevious, true);
			//HashMap mapAfter = db.gatAllCourseAndGrades("200410", false,
				//	 false);
			//conn.addCourseAndGrades(mapAfter, false);
			
			MessageLogger.out.println("success");
			
			db.closeConn();
			conn.closeConnection();
            
		} catch (Exception ex) {
			ex.printStackTrace();
			
			MessageLogger.out.println("Error uploading "
					+ ex.getMessage());
		}
	}
}
