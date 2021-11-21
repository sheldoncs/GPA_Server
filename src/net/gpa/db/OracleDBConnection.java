package net.gpa.db;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import net.gpa.logger.MessageLogger;
import net.gpa.util.NewDateFormatter;

public class OracleDBConnection {
   
	protected Connection conn;
	protected Connection conn1;
	protected ArrayList list;
	protected String effTerm;
    
    
	public OracleDBConnection(){
	}
	
	
	public void openConn(){
		
		
		
		try {
			
			
			
			Class.forName("oracle.jdbc.driver.OracleDriver");
			String url = "jdbc:oracle:thin:@bandb-prod.ec.cavehill.uwi.edu:8000:PROD";
			//String url = "jdbc:oracle:thin:@hermes.cavehill.uwi.edu:1521:PRODCH";
			conn = DriverManager.getConnection(url, "svc_update", "e98ce36209");
			conn.setAutoCommit(true);
        
			
           
	       } catch (ClassNotFoundException e){
		     e.printStackTrace();
	       } catch (SQLException ex){
		     ex.printStackTrace();
		     MessageLogger.out.println(ex.getMessage());  
	       }
	       
	       
	}
    public void openConnMockDB(){
		
		
		
		try {
			
			
			
		      Class.forName("oracle.jdbc.driver.OracleDriver");
              String url = "jdbc:oracle:thin:@antares.cavehill.uwi.edu:1521:MOCKDB";
              conn = DriverManager.getConnection(url,"20003569", "kentish1968");
              conn.setAutoCommit(true);
        
			
           
	       } catch (ClassNotFoundException e){
		     e.printStackTrace();
	       } catch (SQLException ex){
		     ex.printStackTrace();
		     MessageLogger.out.println(ex.getMessage());  
	       }
	       
	       
	}
	public void connectPeopleSoft(){
		
		try {
		Class.forName("oracle.jdbc.driver.OracleDriver");
        String url = "jdbc:oracle:thin:@kronos1.cavehill.uwi.edu:1521:HRPRD90";
        conn = DriverManager.getConnection(url,"admin", "proxyfield");
        conn.setAutoCommit(true);
		}  
		catch (ClassNotFoundException e){
		     e.printStackTrace();
	    } catch (SQLException ex){
		     ex.printStackTrace();
		     MessageLogger.out.println(ex.getMessage());  
	    }
	}
	
	
	
	public String getCurrentTerm(){
		 String term = null;
			
		 /*
		  * 
		  *SELECT stvterm_code FROM stvterm WHERE stvterm_code IN  
           (SELECT MAX( A.stvterm_code) AS NEW_TERM FROM 
           stvterm A WHERE current_date BETWEEN A.stvterm_start_date AND A.stvterm_end_date 
           UNION
           SELECT MIN(B.stvterm_code) AS NEW_TERM FROM stvterm B
           WHERE B.stvterm_start_date > current_date)
           */
		 
			String sqlstmt = "select max(stvterm_code) as maxtermcode from stvterm where stvterm_start_date < ? and stvterm_end_date > ?";
			NewDateFormatter df = new NewDateFormatter();
			
			try {
				  
				  PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
				  prepStmt.setDate(1, java.sql.Date.valueOf((df.getSimpleDate())));
			      prepStmt.setDate(2, java.sql.Date.valueOf(df.getSimpleDate()));
			     
			      ResultSet rs = prepStmt.executeQuery();
			      while (rs.next()){
			    	  term = rs.getString(1);
			      }
			      if ((term != null) && (term.indexOf("40") >=0)){
				      
			    	   term = null;
			        
			      }
			      
			      if (term == null){
						sqlstmt = "select min(stvterm_code) as maxtermcode from stvterm where stvterm_start_date > ?";
						df = new NewDateFormatter();
						System.out.println(java.sql.Date.valueOf((df.getSimpleDate())));
						prepStmt = conn.prepareStatement(sqlstmt);
						prepStmt.setDate(1, java.sql.Date.valueOf((df.getSimpleDate())));
						
						rs = prepStmt.executeQuery();
					    while (rs.next()){
					    	  term = rs.getString(1);
					    }
				  }
			      
			      
			      
			      rs.close();
			      prepStmt.close();
			}
			catch(SQLException e){
				  e.printStackTrace();
			}
			
			
			return term;
	 }
	public String getNewCurrentTerm(){
		 String term = null;
			
		 
			String sqlstmt = "SELECT stvterm_code FROM stvterm WHERE stvterm_code IN " +  
                             "(SELECT MAX( A.stvterm_code) AS NEW_TERM FROM " +
                             "stvterm A WHERE ? BETWEEN A.stvterm_start_date AND A.stvterm_end_date " + 
                             "UNION " +
                             "SELECT MIN(B.stvterm_code) AS NEW_TERM FROM stvterm B " +
                             "WHERE B.stvterm_start_date > ?) " +
                             "AND stvterm_code IS NOT NULL " +
                             "AND SUBSTR(stvterm_code,5,6) <> ? ";
			
			NewDateFormatter df = new NewDateFormatter();
			
			try {
				  
				  PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
				  prepStmt.setDate(1, java.sql.Date.valueOf((df.getSimpleDate())));
			      prepStmt.setDate(2, java.sql.Date.valueOf(df.getSimpleDate()));
			      prepStmt.setString(3, "04");
			      
			      ResultSet rs = prepStmt.executeQuery();
			      while (rs.next()){
			    	  term = rs.getString(1);
			      }
			      	      
			      
			      
			      rs.close();
			      prepStmt.close();
			}
			catch(SQLException e){
				  e.printStackTrace();
			}
			
			
			return term;
	 }
	 public void openPeopleSoftConnection(){
		 try {
				
			    Class.forName("oracle.jdbc.driver.OracleDriver");
		        String url = "jdbc:oracle:thin:@196.1.163.244:1521:HRSYS8";
		        conn = DriverManager.getConnection(url,"admin", "proxyfield");
		        conn.setAutoCommit(true);
		        MessageLogger.out.println("PeopleSoft Connection Open");
		        
			} catch (ClassNotFoundException e){
				e.printStackTrace();
			} catch (SQLException ex){
				ex.printStackTrace();
				MessageLogger.out.println(ex.getMessage());  
			}
	 }
	 
	 protected String lPad(String MyValue, String MyPadCharacter, int MyPaddedLength){
		 String padString="";
		 int padLength = 0;
		 
		 padLength = MyPaddedLength - MyValue.length();
		 for (int i = 0; i < padLength; i++ ){
			 padString = padString + MyPadCharacter;
		 }
		 padString = padString + MyValue;
		 
		 return padString;
	 }
	 public void setEffectiveTerm(String effTerm){
			this.effTerm = effTerm;
	 }
	 public void closeConn(){
		 try {
		 conn.close();
		 //exportDb.closeConnections();
		 //adsl.closeConnections();
		 
		 } catch(SQLException e){
			  e.printStackTrace();
			}
		 
	 }

	 
	 public static void main(String[] args) throws RemoteException,SQLException {
			//OracleDBConnection db = new OracleDBConnection();
		    //OracleDBConnection db = new OracleDBConnection(0);
		    //db.openConn();
		    
		    //System.out.println(db.getCurrentTerm());
		    //System.out.println(db.getCurrentTerm());
			//db.openPeopleSoftConnection();
	 }
	 
}
