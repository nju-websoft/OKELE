package main;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import common.Constants;
import common.PropFeature;
import model.PropertyFeature;
import model.PropertyFeature.ValueType;

public class FeatureExtractor {
	
	private String featureFilePath;
	private Map<String,PropertyFeature> propertyFeatureMap;
	
	public FeatureExtractor(String filePath) {
		super();
		this.featureFilePath = filePath;
		this.propertyFeatureMap = new HashMap<String,PropertyFeature>();
		try {
			loadPropertyFeatures();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void loadPropertyFeatures() throws IOException {
		List<String> features = IOUtils.readLines(new FileInputStream(featureFilePath), Constants.FILE_ENCODING);
		for(int i=1;i<features.size();i++) {
			String[] splits = features.get(i).split("\t");
			PropertyFeature propertyFeature = new PropertyFeature();
			propertyFeature.setSingleValue(Integer.valueOf(splits[PropFeature.SINGLE_MULTI.getIndex()])==1);
			if(Integer.valueOf(splits[PropFeature.IS_DIGIT_VALUE.getIndex()])==1) {
				propertyFeature.setValueType(ValueType.DIGIT);
			}
			else if(Integer.valueOf(splits[PropFeature.IS_DATE_VALUE.getIndex()])==1) {
				propertyFeature.setValueType(ValueType.DATE);
			}
			else if(Integer.valueOf(splits[PropFeature.IS_DIGIT_STR_VALUE.getIndex()])==1) {
				propertyFeature.setValueType(ValueType.DIGITSTRING);
			}
			else if(Integer.valueOf(splits[PropFeature.IS_PURE_STR_VALUE.getIndex()])==1) {
				propertyFeature.setValueType(ValueType.STRING);
			}
			if(splits.length>=5) {
				propertyFeature.setAverageValueCnt(Double.valueOf(splits[PropFeature.AVERAGE_VALUE_CNT.getIndex()]));
			}else{
				propertyFeature.setAverageValueCnt(1);
			}
			this.propertyFeatureMap.put(splits[0], propertyFeature);
		}
	}
	
	public boolean isSingleValue(String propertyName) {
		PropertyFeature propertyFeature = this.propertyFeatureMap.get(propertyName);
		return (propertyFeature == null)?false:propertyFeature.isSingleValue();
	}
	
	public ValueType getPropertyDataType(String propertyName) {
		PropertyFeature propertyFeature = this.propertyFeatureMap.get(propertyName);
		return (propertyFeature == null)?ValueType.STRING:propertyFeature.getValueType();
	}
	
	public double getPropertyAverageValueCnt(String propertyName) {
		PropertyFeature propertyFeature = this.propertyFeatureMap.get(propertyName);
		return (propertyFeature == null)?1:propertyFeature.getAverageValueCnt();
	}
	
}
