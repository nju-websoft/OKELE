package util;

import com.att.research.solomon.format.PersonListCleaner;

import model.PropertyFeature.ValueType;

public class DataCleaner {

	/**
	 * If the value cannot be cleaned into the given data type. it is cleaned as a String.
	 * @param value
	 * @param dataType
	 * @return
	 */
	public static String clean(String value, ValueType dataType) {
		if (dataType.equals(ValueType.DATE)) {
			if(value.length() < 5)
				return value;
			return DateTimeUtils.dateStr2DateStr(value,false);
		}
		if (dataType.equals(ValueType.PERSONNAME)) {
			try {
				return PersonListCleaner.INSTANCE.clean(value.replaceAll(",", " "));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (dataType.equals(ValueType.STRING) || dataType.equals(ValueType.DIGITSTRING)) {
			try {
				return value.replaceAll("[-]", " ").trim().toLowerCase();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return value;
	}
}
