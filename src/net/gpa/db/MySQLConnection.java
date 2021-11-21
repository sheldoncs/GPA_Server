package net.gpa.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.gpa.logger.MessageLogger;
import net.gpa.util.CourseAndGrade;



public class MySQLConnection {
    
	protected Connection conn;
   
	public  MySQLConnection(){
		
	}
	
	
	public void connectLookups(){
		
	}
	
	public void connectGPADb(){
		try {
			
			
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://owl2:3305/gpa",  "admin", "kentish");
		    
			conn.setAutoCommit(true);
		        
			
			} catch (SQLException ex){
				ex.printStackTrace();
				
			} catch (ClassNotFoundException e){
				e.printStackTrace();
			}
	}
	

	public void addCourseAndGrades(ArrayList list, boolean isPrior){
		
		String sqlstmt = "insert into studentgrades (student_id, credit_hr, quality_points, subj_code, crse_numb, clas_code, grade, isprior) values ( ? , ? , ?, ?, ?, ?, ?, ?)";
		
		try {
			PreparedStatement prepStmt = conn.prepareStatement(sqlstmt, 
					PreparedStatement.RETURN_GENERATED_KEYS);

			Iterator keyIterator = list.iterator();

			while (keyIterator.hasNext()) {

				
				MessageLogger.out.println("Add Course And Grades");
				CourseAndGrade cag = (CourseAndGrade) keyIterator.next();

			
					prepStmt.setString(1, cag.getId());
					prepStmt.setDouble(2, cag.getCreditHr());
					prepStmt.setDouble(3, cag.getQualityPoints());
					prepStmt.setString(4, cag.getSubjCode());
					prepStmt.setString(5, cag.getCourseNumb());
					prepStmt.setString(6, cag.getClas());
					prepStmt.setString(7, cag.getGrade());
					prepStmt.setBoolean(8, isPrior);
					
					prepStmt.execute("use gpa");
					prepStmt.executeUpdate();
				}
			
			
			prepStmt.close();
		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}
	
	private boolean courseAndGradeExist(String id, String subj_code, String crse_numb, boolean isPrior){
		
		boolean exist = false;
		String sqlstmt = "select * from studentgrades where student_id = ? and subj_code = ? and crse_numb = ? and isprior = ?"; 
		
		 try {
				PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
				prepStmt.setString(1, id);
				prepStmt.setString(2, subj_code);
				prepStmt.setString(3, crse_numb);
				prepStmt.setBoolean(4, isPrior);
				
				ResultSet rs = prepStmt.executeQuery();
	             
				while (rs.next()) {
				exist = true;	
				}
				prepStmt.close();
				rs.close();
		 } catch (SQLException ex){
			 ex.printStackTrace();
		 }
		return exist;
	}
	public void closeConnection(){
		try {
			conn.close();
		}catch (SQLException ex){
			 ex.printStackTrace();
			 
		 }
	}
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//COMP, PHYS, MATH and ELET
		MySQLConnection db = new MySQLConnection();
		
		//db.releaseStudentFromHold();
		//ArrayList alist = new ArrayList();
		//alist.add("COMP");
		//alist.add("PHYS");
		//alist.add("MATH");
		//alist.add("ELET");
		//db.acquireCourses(alist,"200000815");
		
		
	}
}
