package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.PropertyFeature.ValueType;

public class DataItemValue { 
	private int valueIdentifier;
	private DataItem dataItem;
	private ValueType valueType;
	private String valueString; 
	private List<String> valueOriginString; 
	private List<String> sourceClaimIdentifiers;
	private Set<String> sourceIdentifiers;
	private Set<String> disagreeSourceIdentifiers;
	private boolean trueByFusioner;
	private double confidence = 0.5;
	private boolean prior_flag = false; 

	public DataItemValue(int valueIdentifier, DataItem dataItem, ValueType valueType, String valueString) {
		super();
		this.valueIdentifier = valueIdentifier;
		this.dataItem = dataItem;
		this.valueType = valueType;
		this.valueString = valueString;
		this.sourceClaimIdentifiers = new ArrayList<String>();
		this.sourceIdentifiers = new HashSet<String>();
		this.prior_flag = false;
	}

    public DataItemValue(int valueIdentifier,DataItem dataItem, ValueType valueType, String valueString, boolean prior_flag) {
        super();
        this.valueIdentifier = valueIdentifier;
        this.dataItem = dataItem;
        this.valueType = valueType;
        this.valueString = valueString;
        this.sourceClaimIdentifiers = new ArrayList<String>();
        this.sourceIdentifiers = new HashSet<String>();
        this.prior_flag = prior_flag;
    }

	public int getValueIdentifier() {
		return valueIdentifier;
	}

    public boolean isPrior_flag() {
        return prior_flag;
    }

    public void setPrior_flag(boolean prior_flag) {
        this.prior_flag = prior_flag;
    }

    public void setValueIdentifier(int valueIdentifier) {
		this.valueIdentifier = valueIdentifier;
	}

	public ValueType getValueType() {
		return valueType;
	}

	public String getValueString() {
		return valueString;
	}

	public List<String> getValueOriginString() {
		return valueOriginString;
	}

	public void addValueOriginString(String valueOriginString) {
		if(this.valueOriginString == null) {
			this.valueOriginString = new ArrayList<String>();
		}
		this.valueOriginString.add(valueOriginString);
	}

	public boolean isTrueByFusioner() {
		return trueByFusioner;
	}

	public double getConfidence() {
		return confidence;
	}

	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}
	
	public DataItem getDataItem() {
		return dataItem;
	}

	public List<String> getSourceClaims() {
		return sourceClaimIdentifiers;
	}

	public Set<String> getSources() {
		return sourceIdentifiers;
	}

	public Set<String> getDisagreeSourceIdentifiers() {
		if(disagreeSourceIdentifiers == null) {
			disagreeSourceIdentifiers = new HashSet<String>(dataItem.getSources());
			disagreeSourceIdentifiers.removeAll(sourceIdentifiers);
		}
		return disagreeSourceIdentifiers;
	}

	public void setTrueByFusioner(boolean trueByFusioner) {
		this.trueByFusioner = trueByFusioner;
	}

	public void addSourceClaim(String sourceClaimIdentifier) {
		this.sourceClaimIdentifiers.add(sourceClaimIdentifier);
	}

	public void addSource(String sourceIdentifier) {
		this.sourceIdentifiers.add(sourceIdentifier);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + valueIdentifier;
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
		DataItemValue other = (DataItemValue) obj;
		if (valueIdentifier != other.valueIdentifier)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DataItemValue{" +
				"valueIdentifier=" + valueIdentifier +
				", valueString='" + valueString + "\',source=" + sourceIdentifiers.toString() +
				'}';
	}
}
