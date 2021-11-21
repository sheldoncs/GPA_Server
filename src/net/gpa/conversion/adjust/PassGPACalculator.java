package net.gpa.conversion.adjust;

public class PassGPACalculator extends Calculator {

	public PassGPACalculator(double oldGPA){
		super(oldGPA);
	}
	public double getDegreeConvertedGPA() {
		// TODO Auto-generated method stub
		newGPA = ((49*newGPA)+149)/99;
		return newGPA;
	}
	public double getCurrentGPA(double convertedGPA, double previousHours, double currentGPA, double currentHRS) {
		// TODO Auto-generated method stub
		return 0;
	}

}
