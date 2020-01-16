package main;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import common.Constants;
import model.*;
import model.PropertyFeature.ValueType;
import util.DataComparator;
import util.DateTimeUtils;
import util.MethodUtil;

public class DataParser {
	
	public static DataSet parse(String dataName,KnowledgeDataLoader dataLoader) throws IOException{
		FeatureExtractor featureExtractor = dataLoader.getFeatureExtractor();
		int valueIdentifier = 0;
		int differentClaimSize = 0;
		int emptyClaimSize = 0;
		int nonEmptyClaimSize = 0;
		DataSet dataSet = new DataSet(dataName,false);
		dataSet.setDataLoader(dataLoader);
		for(Claim claim:dataLoader.getClaims()) {
			if(!claim.getValues().isEmpty())
				nonEmptyClaimSize++;
			else
				emptyClaimSize++;
			String property = claim.getRelationmid();
			boolean singleValue = featureExtractor.isSingleValue(property);

			ValueType valueType = featureExtractor.getPropertyDataType(property);
			double averageValueCnt = featureExtractor.getPropertyAverageValueCnt(property);
			int j = 0;
			for(Value value:claim.getValues()) {
			/*	boolean flag;
				switch(valueType) {
				case DIGIT:flag = MethodUtil.isDigit(value.getLable());break;
				case DATE:flag = MethodUtil.isDate(value.getLable());break;
				case DIGITSTRING:flag = MethodUtil.isDigitStr(value.getLable());break;
				case STRING:flag = MethodUtil.isStr(value.getLable());break;
				default:flag = true;break;
				}*/
			//	if(flag) {
					String dataItemIdentifier = MethodUtil.strip(claim.getEntitymid()) + Constants.dataItemIdentifierDelimiter 
							+ MethodUtil.strip(property);
					DataItem dataItem = dataSet.getDataItem(dataItemIdentifier);
					if(dataItem == null) {
					//	System.out.println(dataItemIdentifier + "\t" + claim.getEntitylable());
						dataItem = new DataItem(dataItemIdentifier,claim.getEntitylable(),claim.getRelationmid(),singleValue,averageValueCnt);
						dataSet.addDataItem(dataItem);
					}
				//	String valueCleanedString = DataCleaner.clean(value.getLable(), valueType);
					String valueCleanedString = value.getLable();
					DataItemValue dataItemValue = dataItem.getValue(valueCleanedString);
					if(dataItemValue == null) {
						dataItemValue = new DataItemValue(valueIdentifier,dataItem,valueType,valueCleanedString,false);
						dataItemValue.addValueOriginString(value.getLable());
						dataItem.addValue(dataItemValue);
						dataSet.addDataItemValue(dataItemValue);
					}
					Set<String> typeSource = new HashSet<String>();
					int k = 0;
					for(String source:value.getSource()) {
//						System.out.println(source);
						String extractType = value.getExtract_type().get(k);
						int start_http = source.indexOf("http");
						if(source.contains("http") || source.contains("https"))
							source = new URL(source.substring(start_http,source.length()-1)).getHost();
					//	typeSource.add(extractType + "#" + source +"#"+ MethodUtil.strip(property));
						typeSource.add(extractType + "#" + source);
						k++;
					}
					k = 0;
					for(String sourceIdentifier:typeSource) {
						differentClaimSize++;
						Source source = dataSet.getSource(sourceIdentifier);
						if(source == null) {
							source = new Source(sourceIdentifier);
							dataSet.addSource(source);
						}
						String sourceClaimIdentifier = claim.getId()+ Constants.sourceClaimIdentifierDelimiter +j + Constants.sourceClaimIdentifierDelimiter + k;
						SourceClaim sourceClaim = new SourceClaim(sourceClaimIdentifier,dataItemIdentifier,
							dataItemValue.getValueIdentifier(),sourceIdentifier);
						k++;
						source.addDataItem(dataItemIdentifier,dataItemValue.getValueIdentifier());
						source.addSourceClaim(sourceClaimIdentifier);
						dataItem.addSource(sourceIdentifier);
						dataItem.addSourceClaim(sourceClaimIdentifier);
						dataItemValue.addSource(sourceIdentifier);
						dataItemValue.addSourceClaim(sourceClaimIdentifier);
						dataSet.addSourceClaim(sourceClaim);
					}
			//	}
				valueIdentifier++;
				j++;
			}
		}
		System.out.println("The number of lines with null values in claim file: " + emptyClaimSize);
		System.out.println("The number of lines with non-null values in claim file:" + nonEmptyClaimSize);
		System.out.println("The number of sourceclaims: " + differentClaimSize);
		differentClaimSize = 0;
		emptyClaimSize = 0;
		nonEmptyClaimSize = 0;

		if(dataLoader.getPrior_claims() != null) {
		for(Claim claim:dataLoader.getPrior_claims()) {
			if(!claim.getValues().isEmpty())
				nonEmptyClaimSize++;
			else
				emptyClaimSize++;
			int j = 0;
			String property = claim.getRelationmid();
			String dataItemIdentifier = MethodUtil.strip(claim.getEntitymid()) + Constants.dataItemIdentifierDelimiter
					+ MethodUtil.strip(property);
			HashMap<String, Truth> truths = dataLoader.getPrior_truths();
			if(truths.containsKey(dataItemIdentifier)) { 
				Truth truth = truths.get(dataItemIdentifier);
				Set<String> tValues = truth.getTrueValue();
				boolean singleValue = featureExtractor.isSingleValue(property);

				ValueType valueType = featureExtractor.getPropertyDataType(property);
				double averageValueCnt = featureExtractor.getPropertyAverageValueCnt(property);
				for(Value value:claim.getValues()) {
					DataItem dataItem = dataSet.getDataItem(dataItemIdentifier);
					if(dataItem == null) {
						dataItem = new DataItem(dataItemIdentifier,claim.getEntitylable(),claim.getRelationmid(),singleValue,averageValueCnt);
						dataItem.setPrior_flag(true);
						dataSet.addDataItem(dataItem);				
					}
					String valueCleanedString = value.getLable();
					DataItemValue dataItemValue = dataItem.getValue(valueCleanedString);
					if(dataItemValue == null) {
						dataItemValue = new DataItemValue(valueIdentifier,dataItem,valueType,valueCleanedString,true);
					//	dataItemValue.setPrior_flag(true); 
						boolean true_flag = false;
						String relation = dataSet.getDataItem(dataItemIdentifier).getProperty();
						for(String tValue:tValues) {
							if(relation.contains("date")||relation.contains("year"))
								tValue = DateTimeUtils.dateStr2DateStr(tValue,false);  
							//if(DataComparator.isEqual(tValue, dataItemValue.getValueString(),relation)){
							if(DataComparator.jaroSimilarity(tValue, dataItemValue.getValueString())>=0.9){
								true_flag = true;
							}
						}
						if(true_flag){ 
							dataItemValue.setConfidence(1.0);
						}else{
							dataItemValue.setConfidence(0.0);
						}
						dataItemValue.addValueOriginString(value.getLable());
						dataItem.addValue(dataItemValue);
						dataSet.addDataItemValue(dataItemValue);
					}
					Set<String> typeSource = new HashSet<String>();
					int k = 0;
					for(String source:value.getSource()) {
//							System.out.println(source);
						String extractType = value.getExtract_type().get(k);
						int start_http = source.indexOf("http");
						if(source.contains("http") || source.contains("https"))
							source = new URL(source.substring(start_http,source.length()-1)).getHost();
						//	typeSource.add(extractType + "#" + source +"#"+ MethodUtil.strip(property));
						typeSource.add(extractType + "#" + source);
						k++;
					}
					k = 0;
					for(String sourceIdentifier:typeSource) {
						differentClaimSize++;
						Source source = dataSet.getSource(sourceIdentifier);
						if(source == null) {
							source = new Source(sourceIdentifier);
							dataSet.addSource(source);
						}
						String sourceClaimIdentifier = claim.getId()+ Constants.sourceClaimIdentifierDelimiter +j + Constants.sourceClaimIdentifierDelimiter + k + "-1";
						SourceClaim sourceClaim = new SourceClaim(sourceClaimIdentifier,dataItemIdentifier,
								dataItemValue.getValueIdentifier(),sourceIdentifier);
						k++;
						source.addDataItem(dataItemIdentifier,dataItemValue.getValueIdentifier());
						source.addSourceClaim(sourceClaimIdentifier);
						dataItem.addSource(sourceIdentifier);
						dataItem.addSourceClaim(sourceClaimIdentifier);
						dataItemValue.addSource(sourceIdentifier);
						dataItemValue.addSourceClaim(sourceClaimIdentifier);
						dataSet.addSourceClaim(sourceClaim);
					}
					valueIdentifier++;
					j++;
				}
			}
		}
		}
		return dataSet;
	}
}
