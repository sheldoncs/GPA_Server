package net.gpa.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.gpa.util.MessageLogger;


public class StoredGPADb {
	
	protected Connection conn;
	
	public StoredGPADb(String username, String password, String servername){
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		
		    conn = DriverManager.getConnection("jdbc:mysql://"+servername+":3305/gpa",  username, password);
			conn.setAutoCommit(true);
			
		} catch (ClassNotFoundException e){
			e.printStackTrace();
		} catch (SQLException ex){
			ex.printStackTrace();
		}
		
	}
	public void insertGPA(String id, double gpa_hrs, double gpa, double grade_points, String type){
		
		String insertStatement = "insert into PriorGPA  (spriden_id, gpa_hours, gpa, grade_points, type) values ( ? , ? , ?, ?, ? )";

		PreparedStatement prepStmt;
		try {
			prepStmt = conn.prepareStatement(insertStatement,
					PreparedStatement.RETURN_GENERATED_KEYS);
			
			prepStmt.setString(1, id);
			prepStmt.setDouble(2, gpa_hrs);
			prepStmt.setDouble(3, gpa);
			prepStmt.setDouble(4, grade_points);
			prepStmt.setString(5, type);
			
			MessageLogger.out.println(id + ", "+gpa_hrs + ", "+gpa + ", "+grade_points);
			
			prepStmt.execute("use gpa");
			prepStmt.getGeneratedKeys();
			prepStmt.executeUpdate();
			
			prepStmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void closeConnections() {
		try {
			conn.close();
		} catch (SQLException e) {
			MessageLogger.out.println(e.getMessage());
		}
	}

}
