package net.gpa.util;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;



  public class NewDateFormatter {
	  private Date date;
	public String printDate(){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		date = new Date();
		
		
		String s = formatter.format(date);
		return s;
		
	}
	public String getSimpleOracleDate(){
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

		date = new Date();
		
		
		
		String s = formatter.format(date);
		return s;
		
	}
	
	
	public String printDateSlash(){
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

		date = new Date();
		
		
		String s = formatter.format(date);
		return s;
		
	}
	public String printDateSQLServer(){
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

		date = new Date();
		
		
		String s = formatter.format(date);
		return s;
	}
	public String getSimpleDate(){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		date = new Date();
		String s = formatter.format(date);
		return s;
	}
	public String getMMMDate(Date date){
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy");
		//date = new Date();
		String s = formatter.format(date);
		return s;
	}
	public String getFormattedDate(Date d){
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");

		String s = formatter.format(d);
		return s;
	}

	public static void main(String[] args) throws RemoteException,SQLException {
		NewDateFormatter tb = new NewDateFormatter();
		System.out.println(tb.getSimpleOracleDate());
		System.out.println(java.sql.Date.valueOf(tb.getSimpleOracleDate()));
		//System.out.println(tb.getMMMDate());
	}
}
