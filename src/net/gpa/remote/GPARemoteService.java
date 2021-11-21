package net.gpa.remote;

import java.rmi.RemoteException;
import java.util.ArrayList;

import net.gpa.conversion.adjust.Calculator;
import net.gpa.conversion.adjust.CurrentCalculator;
import net.gpa.conversion.adjust.FailGPACalculator;
import net.gpa.conversion.adjust.FirstGPACalculator;
import net.gpa.conversion.adjust.LowerSecondGPACalculator;
import net.gpa.conversion.adjust.PassGPACalculator;
import net.gpa.conversion.adjust.UpperSecondGPACalculator;
import net.gpa.db.GPADb;
import net.gpa.db.MySQLConnection;
import net.gpa.util.CourseAndGrade;
import net.gpa.util.DegreeAwarded;
import net.gpa.util.GPA;
import net.gpa.util.StudentProgram;

public class GPARemoteService implements GPARemoteInterface {

	/* First Class Grade Scheme Classification */
	private final double MinFirstGPA = 3.60;
	private final double MaxFirstGPA = 4.30;

	/* Upper Second Grade Scheme Classification */
	private final double MinUpperGPA = 3.00;
	private final double MaxUpperGPA = 3.56;

	/* Lower Second Grade Scheme Classification */
	private final double MinLowerGPA = 2.50;
	private final double MaxLowerGPA = 2.99;

	/* Pass Grade Scheme Classification */
	private final double MinPassGPA = 2.00;
	private final double MaxPassGPA = 2.49;

	/* Fail Grade Scheme Classification */
	private final double MinFailGPA = 0.00;
	private final double MaxFailGPA = 1.99;

	public GPA getConvertedGPA(String term, String id, boolean isDegreeFlag) {
		// TODO Auto-generated method stub
        /*Calculates Previous GPA*/
		GPADb db = new GPADb();
		db.openConn();

		double convertedGPA = convertedGPA(db.getGPA(term, id, true,
				isDegreeFlag, true));
		db.closeConn();

		GPA gpa = new GPA();
		gpa.setGpa(convertedGPA);
		return gpa;
	}

	public ArrayList getCourseAndGrades(String term, String id,
			boolean isPreviousGPA, boolean isDegreeFlag) {
		// TODO Auto-generated method stub

		
		GPADb db = new GPADb();
		db.openConn();
		ArrayList list = db.getCourseAndGrades(term, id, isPreviousGPA, isDegreeFlag);
		db.closeConn();
		return list;
	}

	public double convertedGPA(double gpa) {

		double newgpa = 0;
		Calculator calculator;

		if (gpa >= MinFirstGPA && gpa <= MaxFirstGPA) {

			calculator = new FirstGPACalculator(gpa);
			newgpa = calculator.getDegreeConvertedGPA();

		} else if (gpa >= MinUpperGPA && gpa <= MaxUpperGPA) {

			calculator = new UpperSecondGPACalculator(gpa);
			newgpa = calculator.getDegreeConvertedGPA();

		} else if (gpa >= MinLowerGPA && gpa <= MaxLowerGPA) {

			calculator = new LowerSecondGPACalculator(gpa);
			newgpa = calculator.getDegreeConvertedGPA();

		} else if (gpa >= MinPassGPA && gpa <= MaxPassGPA) {

			calculator = new PassGPACalculator(gpa);
			newgpa = calculator.getDegreeConvertedGPA();

		} else if (gpa >= MinFailGPA && gpa <= MaxFailGPA) {

			calculator = new FailGPACalculator(gpa);
			newgpa = calculator.getDegreeConvertedGPA();

		}

		return newgpa;
	}

	public GPA getCurrentDegreeGPA(String id, String oldterm, double newgpa,
			String newTerm) {
		// TODO Auto-generated method stub
		
		/*Calculates Combined Degree GPA*/
		
		GPADb db = new GPADb();
		db.openConn();

		GPA convertedgpa = getConvertedGPA(oldterm, id, true);
		double convertedGPA = convertedgpa.getGpa();
		double previousHours = db.getGPA(oldterm, id, false, true, true);

		double currentGPA = db.getGPA(newTerm, id, true, true, false);
		double currentHours = db.getGPA(newTerm, id, false, true, false);

		Calculator calculator = new CurrentCalculator(0);

		double gpa = calculator.getCurrentGPA(convertedGPA, previousHours,
				currentGPA, currentHours);

		db.closeConn();

		GPA currentgpa = new GPA();
		currentgpa.setGpa(gpa);
		return currentgpa;
	}

	public GPA getAccumGPA(String id, String oldterm, double newgpa,
			String newTerm) {
		// TODO Auto-generated method stub
		
		/*Calculates combined Accumulative GPA*/
		
		GPADb db = new GPADb();
		db.openConn();

		GPA convertedgpa = getConvertedGPA(oldterm, id, true);
		double convertedGPA = convertedgpa.getGpa();
		double previousHours = db.getGPA(oldterm, id, true, false, true);

		double currentGPA = db.getGPA(newTerm, id, true, true, false);
		double currentHours = db.getGPA(newTerm, id, false, false, false);

		Calculator calculator = new CurrentCalculator(0);

		double gpa = calculator.getCurrentGPA(convertedGPA, previousHours,
				currentGPA, currentHours);

		GPA currentgpa = new GPA();
		currentgpa.setGpa(gpa);
		db.closeConn();

		return currentgpa;
	}

	public CourseAndGrade getCourseAndGrade(String term, String id)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public void  updateCourseAndGrades(String term, boolean isPreviousGPA,
			boolean isDegreeFlag) throws RemoteException {
		// TODO Auto-generated method stub
		
		MySQLConnection conn = new MySQLConnection();
		GPADb db = new GPADb();
		
		ArrayList list = db.gatAllCourseAndGrades(term, true, true);
		
	}

	public void getUpdateCourseAndGrades(String term, boolean isPreviousGPA,
			boolean isDegreeFlag) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	public DegreeAwarded getDegreeAwarded(String id) throws RemoteException {
		// TODO Auto-generated method stub
		GPADb db = new GPADb();
		db.openConn();
		
		DegreeAwarded da = db.getAwarded(id);
		db.closeConn();
		return da;
	}

	public CourseAndGrade getStudentID(long pidm) throws RemoteException {
		// TODO Auto-generated method stub
		CourseAndGrade cag = new CourseAndGrade();
		GPADb db = new GPADb();
		db.openConn();
		
		String id = db.getSpridenID(pidm);
		cag.setId(id);
		db.closeConn();
		return cag;
	}

	public StudentProgram getStudentProgram(String id) throws RemoteException {
		
		// TODO Auto-generated method stub
		GPADb db = new GPADb();
		db.openConn();
		
		StudentProgram studentProgram = db.getStudentProgram(id); 
		db.closeConn();
		
		return studentProgram;
		
	}

}
