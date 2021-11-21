package net.gpa.conversion.adjust;

public class FailGPACalculator extends Calculator {

	public FailGPACalculator(double oldGPA){
		super(oldGPA);
	}
	
	public double getDegreeConvertedGPA() {
		// TODO Auto-generated method stub
		
		newGPA = (199*newGPA)/99;
		
		return newGPA;
	}

	public double getCurrentGPA(double convertedGPA, double previousHours, double currentGPA, double currentHours) {
		
		currentGPA = ((previousHours * convertedGPA)+(currentHours * currentGPA))/(previousHours + currentHours) ;
		// TODO Auto-generated method stub
		return currentGPA;
	}

}
