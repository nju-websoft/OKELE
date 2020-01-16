package algorithm;

import util.ConvergenceTester;

public class FusionerParameters {
	private double cosineSimDiffStoppingCriteria;
	private double startingTrust; 
	private double startingConfidence; 

	public FusionerParameters() {
		super();
		this.cosineSimDiffStoppingCriteria = 1.0E-3;
		this.startingTrust = 0.8;
		this.startingConfidence = 0.5;
		ConvergenceTester.convergenceThreshold = cosineSimDiffStoppingCriteria;
	}

	public FusionerParameters(double cosineSimDiffStoppingCriteria, double startingTrust, double startingConfidence) {
		if (cosineSimDiffStoppingCriteria > 0) {
			this.cosineSimDiffStoppingCriteria = cosineSimDiffStoppingCriteria;
		}
		if (startingTrust > 0) {
			this.startingTrust = startingTrust;
		}
		if (startingConfidence > 0) {
			this.startingConfidence = startingConfidence;
		}
		ConvergenceTester.convergenceThreshold = cosineSimDiffStoppingCriteria;
	}

	public double getCosineSimDiffStoppingCriteria() {
		return cosineSimDiffStoppingCriteria;
	}

	public void setCosineSimDiffStoppingCriteria(double cosineSimDiffStoppingCriteria) {
		this.cosineSimDiffStoppingCriteria = cosineSimDiffStoppingCriteria;
	}

	public double getStartingTrust() {
		return startingTrust;
	}

	public void setStartingTrust(double startingTrust) {
		this.startingTrust = startingTrust;
	}

	public double getStartingConfidence() {
		return startingConfidence;
	}

	public void setStartingConfidence(double startingConfidence) {
		this.startingConfidence = startingConfidence;
	}
	
}
