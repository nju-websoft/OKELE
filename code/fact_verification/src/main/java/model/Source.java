package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Source {
	
	private String sourceIdentifier;
	private List<String> sourceClaimIdentifiers;
	private Map<String,Set<Integer>> dataItemValues;
	private double trustworthiness; // Accuracy,(TPs+TNs)/(TPs+FPs+TNs+FNs)
	private double oldTrustworthiness;
	private double precision; // TPs/(TPs+FPs)
	private double oldPrecision;
	private double recall; // TPs/(TPs+FNs)
	private double oldRecall;
	private double specificity;// TNs/(FPs+TNs)
	private double oldSpecificity;
	private double claimCount;
	private double errorVariance;
	private double TPs;
	private double TNs;
	private double FPs;
	private double FNs;
	public void incrementTPs() {
		TPs++;
	}
	public void incrementTNs() {
		TNs++;
	}
	public void incrementFPs() {
		FPs++;
	}
	public void incrementFNs() {
		FNs++;
	}
	public double getTPs() {
		return TPs;
	}

	public void setTPs(double tPs) {
		TPs = tPs;
	}

	public double getTNs() {
		return TNs;
	}

	public void setTNs(double tNs) {
		TNs = tNs;
	}

	public double getFPs() {
		return FPs;
	}

	public void setFPs(double fPs) {
		FPs = fPs;
	}

	public double getFNs() {
		return FNs;
	}

	public void setFNs(double fNs) {
		FNs = fNs;
	}

	
	public Source(String sourceIdentifier) {
		super();
		this.sourceIdentifier = sourceIdentifier;
		this.sourceClaimIdentifiers = new ArrayList<String>();
		this.dataItemValues = new HashMap<String,Set<Integer>>();
	}

	public String getSourceIdentifier() {
		return sourceIdentifier;
	}

	public List<String> getSourceClaims() {
		return sourceClaimIdentifiers;
	}

	public Set<String> getDataItems() {
		return dataItemValues.keySet();
	}

	public double getTrustworthiness() {
		return trustworthiness;
	}

	public void setTrustworthiness(double trustworthiness) {
		this.trustworthiness = trustworthiness;
	}

	public double getOldTrustworthiness() {
		return oldTrustworthiness;
	}

	public double getErrorVariance() {
		return errorVariance;
	}

	public void setErrorVariance(double errorVariance) {
		this.errorVariance = errorVariance;
	}

	public double getClaimCount() {
		return claimCount;
	}

	public void setClaimCount(double claimCount) {
		this.claimCount = claimCount;
	}

	public void setOldTrustworthiness(double oldTrustworthiness) {
		this.oldTrustworthiness = oldTrustworthiness;
	}
	
	public double getPrecision() {
		return precision;
	}

	public void setPrecision(double precision) {
		this.precision = precision;
	}

	public double getOldPrecision() {
		return oldPrecision;
	}

	public void setOldPrecision(double oldPrecision) {
		this.oldPrecision = oldPrecision;
	}

	public double getRecall() {
		return recall;
	}

	public void setRecall(double recall) {
		this.recall = recall;
	}

	public double getOldRecall() {
		return oldRecall;
	}

	public void setOldRecall(double oldRecall) {
		this.oldRecall = oldRecall;
	}

	public double getSpecificity() {
		return specificity;
	}

	public void setSpecificity(double specificity) {
		this.specificity = specificity;
	}

	public double getOldSpecificity() {
		return oldSpecificity;
	}

	public void setOldSpecificity(double oldSpecificity) {
		this.oldSpecificity = oldSpecificity;
	}

	public void addSourceClaim(String sourceClaimIdentifier) {
		this.sourceClaimIdentifiers.add(sourceClaimIdentifier);
	}
	
	public void addDataItem(String dataItemIdentifier,int valueIdentifier) {
		Set<Integer> values = this.dataItemValues.get(dataItemIdentifier);
		if(values == null) {
			values = new HashSet<Integer>();
			this.dataItemValues.put(dataItemIdentifier, values);
		}
		values.add(valueIdentifier);
	}
	public Set<Integer> getDataItemValues(String dataItemIdentifier) {
		return this.dataItemValues.get(dataItemIdentifier);
	}

	@Override
	public String toString() {
		return "Source [sourceIdentifier=" + sourceIdentifier + ", trustworthiness=" + trustworthiness + ", precision="
				+ precision + ", recall=" + recall + ", specificity=" + specificity + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sourceIdentifier == null) ? 0 : sourceIdentifier.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Source other = (Source) obj;
		if (sourceIdentifier == null) {
			if (other.sourceIdentifier != null)
				return false;
		} else if (!sourceIdentifier.equals(other.sourceIdentifier))
			return false;
		return true;
	}
	
	
	
}
