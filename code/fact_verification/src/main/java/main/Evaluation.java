package main;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import model.DataItemValue;
import model.DataSet;
import model.Truth;
import util.DataComparator;
import util.DateTimeUtils;

public class Evaluation {
	
	public static void valueEval(DataSet ds) throws IOException {
		DataLoader dataLoader = ds.getDataLoader();
		Map<String,Truth> truths = dataLoader.getTruths();
		int truthPositive = 0;
		int estimatedPositive = 0;
		int truthCnt = 0;
		int estimatedCnt = 0;
		int dataItemCnt = 0;
		int findDataItemCnt = 0;
		boolean dataItemFindFlag = false;
		boolean truthFindFlag = false;
		for(String dataItemIdentifier:ds.getDataItemMap().keySet()) {
		    if(Executor.isTail && !truths.containsKey(dataItemIdentifier) && !ds.getDataItem(dataItemIdentifier).isPrior_flag()){
                truths.put(dataItemIdentifier, new Truth());
                truths.get(dataItemIdentifier).setTrueValue(new HashSet<String>());
            }
            if(ds.getDataItem(dataItemIdentifier).isPrior_flag())
            	continue;
			if(truths.containsKey(dataItemIdentifier)) {
				dataItemCnt++;
				dataItemFindFlag = false;
				Truth truth = truths.get(dataItemIdentifier);
				String relation = ds.getDataItem(dataItemIdentifier).getProperty();
				Set<DataItemValue> dataItemValues = ds.getDataItem(dataItemIdentifier).getValues();
				Set<String> fusionTrueValues = new HashSet<String>();
				for(DataItemValue cValue:dataItemValues) {
					if(cValue.isTrueByFusioner() && !cValue.isPrior_flag()) { 
						fusionTrueValues.add(cValue.getValueString());
					}
				}
				Set<String> tValues = truth.getTrueValue();
				truthCnt += tValues.size();
				estimatedCnt += fusionTrueValues.size();
				Set<String> trueValues = new HashSet<String>();
				if(Executor.isTail && tValues.size() == 0)
					tValues.add("GGGG##GGGG"); 
				for(String tValue:tValues) {
					if((relation.contains("date")||relation.contains("year")) && !Executor.isTail)
						tValue = DateTimeUtils.dateStr2DateStr(tValue,false);  
					truthFindFlag = false;
					for(String cValue:fusionTrueValues) {
				//		if(DataComparator.jaroSimilarity(tValue, cValue)>=0.9 && !DataComparator.isEqual(tValue, cValue,relation)) {
				//			System.out.println(cValue + " " + tValue);
				//		}
						if(Executor.isTail){
							if(tValue.equals(cValue)) {
								if (!trueValues.contains(cValue)) {
									estimatedPositive++;
									trueValues.add(cValue);
								}
								truthFindFlag = true;
							}
						}else {
							if (DataComparator.isEqual(tValue, cValue, relation)) {
								//		if(DataComparator.jaroSimilarity(tValue, cValue)>=0.9){
								if (!trueValues.contains(cValue)) {
									estimatedPositive++;
									trueValues.add(cValue);
								}
								truthFindFlag = true;
							}
						}
					}
					if(truthFindFlag) {
						truthPositive++;
						dataItemFindFlag = true;
					}
				}
				if(dataItemFindFlag)
					findDataItemCnt++;
			}
		}
		System.out.println("verfied dataitems: " + findDataItemCnt);
		System.out.println("dataitems present in KB: " + truths.size());
		System.out.println("dataitems present in KB and extracted: " + dataItemCnt);
		System.out.println("verified facts: "+ estimatedPositive);
		System.out.println("all facts: "+ estimatedCnt);
		System.out.println("verified truths: "+ truthPositive);
		System.out.println("all truths: "+ truthCnt);
		double precision = 100*estimatedPositive/(double)estimatedCnt;
		double recall = 100*truthPositive/(double)truthCnt;
		System.out.println("Precision: " + String.format("%.2f",precision)+"%");
		System.out.println("Recall: " + String.format("%.2f", recall)+"%");
		System.out.println("F1: " + String.format("%.2f", 2*precision*recall/(precision+recall))+"%");
	}
}

