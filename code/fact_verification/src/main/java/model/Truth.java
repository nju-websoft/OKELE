package model;

import java.util.HashSet;
import java.util.Set;

import model.PropertyFeature.ValueType;

public class Truth {
	
	private Set<String> trueValue;
	private ValueType valueType;

	public Set<String> getTrueValue() {
		return trueValue;
	}

	public void setTrueValue(Set<String> trueValue) {
		this.trueValue = trueValue;
	}
	public void addTrueValue(String trueValue) {
		if(this.trueValue == null) {
			this.trueValue = new HashSet<String>();
		}
		this.trueValue.add(trueValue);
	}
	public ValueType getValueType() {
		return valueType;
	}

	public void setValueType(ValueType valueType) {
		this.valueType = valueType;
	}
	
	public int getNumberOfTrueValues() {
		return (this.trueValue==null)?0:this.trueValue.size();
	}
	
}
