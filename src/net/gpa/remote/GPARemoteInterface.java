package net.gpa.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import net.gpa.util.CourseAndGrade;
import net.gpa.util.DegreeAwarded;
import net.gpa.util.GPA;
import net.gpa.util.StudentProgram;

public interface GPARemoteInterface  extends Remote {

	public GPA getConvertedGPA(String term, String id, boolean isDegreeFlag) throws RemoteException;
	public ArrayList getCourseAndGrades(String term, String id, boolean isPreviousGPA, boolean isDegreeFlag) throws RemoteException;
	public void getUpdateCourseAndGrades(String term, boolean isPreviousGPA, boolean isDegreeFlag) throws RemoteException;
	public GPA getCurrentDegreeGPA(String id, String oldterm, double newgpa, String newTerm) throws RemoteException;
	public GPA getAccumGPA(String id, String oldterm, double newgpa, String newTerm) throws RemoteException;
    public CourseAndGrade getCourseAndGrade(String term, String id) throws RemoteException;
    public DegreeAwarded getDegreeAwarded(String id) throws RemoteException;
    public CourseAndGrade getStudentID(long pidm) throws RemoteException;
    public StudentProgram getStudentProgram(String id) throws RemoteException;
}
