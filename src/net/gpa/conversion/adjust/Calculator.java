package net.gpa.conversion.adjust;

public abstract class Calculator {

	protected double newGPA;
	protected double currentGPA;

	

	public Calculator(double g) {

		this.newGPA = g;

	}
	

	public abstract double getDegreeConvertedGPA();

	public double getCurrentGPA(double convertedGPA, double previousHours,
			double currentGPA, double currentHours) {

		currentGPA = ((previousHours * convertedGPA) + (currentHours * currentGPA))
				/ (previousHours + currentHours);
		// TODO Auto-generated method stub
		return currentGPA;
	}

}
