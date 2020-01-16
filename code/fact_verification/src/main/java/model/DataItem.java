package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataItem {
	
	private String dataItemIdentifier;
	private String entity;
	private String property;
	private double averageValueCnt;
	private Set<DataItemValue> values;
	private Set<String> sourceIdentifiers;
	private List<String> sourceClaimIdentifiers;
	private Set<Integer> inferTruths;
	private boolean isSingleValue;
	private boolean prior_flag = false;

	public boolean isPrior_flag() {
		return prior_flag;
	}

	public void setPrior_flag(boolean prior_flag) {
		this.prior_flag = prior_flag;
	}

	public DataItem(String dataItemIdentifier, String entity, String property, boolean isSingleValue, double averageValueCnt) {
		super();
		this.dataItemIdentifier = dataItemIdentifier;
		this.entity = entity;
		this.property = property;
		this.averageValueCnt = averageValueCnt;
		this.isSingleValue = isSingleValue;
		this.values = new HashSet<DataItemValue>();
		this.sourceIdentifiers = new HashSet<String>();
		this.sourceClaimIdentifiers = new ArrayList<String>();
		this.setPrior_flag(false);
	}

	public String getDataItemIdentifier() {
		return dataItemIdentifier;
	}
	public List<String> getSourceClaims() {
		return sourceClaimIdentifiers;
	}
	public void addSourceClaim(String sourceClaimIdentifier) {
		this.sourceClaimIdentifiers.add(sourceClaimIdentifier);
	}
	public String getEntity() {
		return entity;
	}

	public String getProperty() {
		return property;
	}

	public Set<DataItemValue> getValues() {
		return values;
	}

	public double getAverageValueCnt() {
		return averageValueCnt;
	}

	public boolean isSingleValue() {
		return isSingleValue;
	}

	public Set<String> getSources() {
		return sourceIdentifiers;
	}
	public DataItemValue getValue(String valueString) {
		for(DataItemValue value:this.values) {
			if(value.getValueString().equalsIgnoreCase(valueString))
				return value;
		}
		return null;
	}
	public void addValue(DataItemValue value) {
		this.values.add(value);
	}
	
	public void addSource(String sourceIdentifier) {
		this.sourceIdentifiers.add(sourceIdentifier);
	}
	public void resetInferTruths() {
		this.inferTruths.clear();
	}
	public Set<Integer> getInferTruths() {
		return this.inferTruths;
	}
	public void addInferTruth(int valueIdentifier) {
		if(this.inferTruths == null) {
			this.inferTruths = new HashSet<Integer>();
		}
		this.inferTruths.add(valueIdentifier);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataItemIdentifier == null) ? 0 : dataItemIdentifier.hashCode());
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
		DataItem other = (DataItem) obj;
		if (dataItemIdentifier == null) {
			if (other.dataItemIdentifier != null)
				return false;
		} else if (!dataItemIdentifier.equals(other.dataItemIdentifier))
			return false;
		return true;
	}
	
}
