package util;

import model.DataItemValue;
import model.DataSet;
import model.Source;

public class ConvergenceTester {
	public  static double convergenceThreshold = 1.0E-3;
	
	public static boolean isConvergence(DataSet dataSet) {
		boolean flag = true;
		double a,b;
		for (Source source : dataSet.getSourceMap().values()) {
			a = source.getOldTrustworthiness();
			b = source.getTrustworthiness();
			if(Math.abs(b-a) > convergenceThreshold) {
				flag = false;
				break;
			}
		}
		return flag;
	}
	
	public static double computeCosineSimilarity(DataSet dataSet) {
		double a,b;
		double sumAB = 0;
		double sumA2= 0;
		double sumB2 = 0;
		for (Source source : dataSet.getSourceMap().values()) {
			a = source.getOldTrustworthiness();
			b = source.getTrustworthiness();
			sumAB = sumAB + (a*b);
			sumA2 = sumA2 + (a*a);
			sumB2 = sumB2 + (b*b);
		}
		sumA2 = Math.pow(sumA2, 0.5);
		sumB2 = Math.pow(sumB2, 0.5);
		if ((sumA2 * sumB2) == 0) {
			System.out.println(sumA2 + " " + sumB2 + " " + Double.MAX_VALUE);
			return Double.MAX_VALUE;
		}
		if (Double.isInfinite(sumAB)) {
			if (Double.isInfinite((sumA2 * sumB2))) {
				return 1.0;
			}
		}
		double cosineSimilarity = sumAB / (sumA2 * sumB2);
		return cosineSimilarity;
	}


	public static double computeValueSimilarity(DataSet dataSet){
		double distance = 0.0;
		for(int valueid:dataSet.getDataItemValueMap().keySet()){
			DataItemValue value = dataSet.getDataItemValue(valueid);
			int before_truth=0;
			if(value.isTrueByFusioner())
				before_truth = 1;
			int now_truth=0;
			if(value.getConfidence()>0.50) {
				now_truth = 1;
				value.setTrueByFusioner(true);
			}else{
				value.setTrueByFusioner(false);
			}
			distance += (before_truth-now_truth)*(before_truth-now_truth)*1.0;
		}
		return distance;
	}
}
