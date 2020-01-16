package model;

public class PropertyFeature {
	
	private boolean isSingleValue;
	private double averageValueCnt;
	private ValueType valueType;

	public PropertyFeature() {
		super();
		this.isSingleValue = true;
		this.averageValueCnt = 1;
		this.valueType = ValueType.STRING;
	}
	public boolean isSingleValue() {
		return isSingleValue;
	}
	public void setSingleValue(boolean isSingleValue) {
		this.isSingleValue = isSingleValue;
	}
	public double getAverageValueCnt() {
		return averageValueCnt;
	}
	public void setAverageValueCnt(double averageValueCnt) {
		this.averageValueCnt = averageValueCnt;
	}
	
	public ValueType getValueType() {
		return valueType;
	}
	public void setValueType(ValueType valueType) {
		this.valueType = valueType;
	}
	public static enum ValueType {
		DIGIT, DATE, DIGITSTRING, STRING, BOOLEAN, ISBN, PERSONNAME;
	};
	public static boolean savedAsString(ValueType v) {
		if (v.equals(ValueType.DIGITSTRING) || v.equals(ValueType.STRING) || v.equals(ValueType.PERSONNAME)) {
			return true;
		}
		return false;
	}
}
