package algorithm.mult;

import java.util.Set;

import algorithm.Fusioner;
import algorithm.FusionerParameters;
import common.Constants;
import model.DataItem;
import model.DataItemValue;
import model.DataSet;
import model.Source;
import util.ConvergenceTester;

public class BWA extends Fusioner{
	private double lambda = 1.0;
	private double mu = 0;
	private double av = 30;
	private double errorRate = 0;
	
	public BWA(DataSet dataSet, FusionerParameters params) {
		super(dataSet, params);
	}

	@Override
	protected int runFusioner() {
		System.out.println(this.getClass().getSimpleName() + ">>>>");
		boolean continueComputation = true;
		int iterationCount = 0;
		initialization();
		computeErrorRate();
		while (continueComputation && iterationCount < Constants.maxIterationCount) {
			iterationCount++;
			
			computeTrustworthiness();
			computeConfidence();

			continueComputation = !ConvergenceTester.isConvergence(dataSet);
			
		}
		return iterationCount;
	}

	private void computeErrorRate(){
		double ni0 = 0,ni1=0;
		double numerator = 0,denominator = 0;
		for(DataItemValue value:dataSet.getDataItemValueMap().values()) {
			ni1 = value.getSources().size();
			ni0 = value.getDisagreeSourceIdentifiers().size();
			numerator += (ni0*ni1)/(ni0+ni1);
			denominator += ni0 + ni1;
		}
		errorRate = 2*numerator / denominator;
		System.out.println("errorRate:" + errorRate);
	}
	private void computeMu(){
		double sum = 0;
		for(DataItemValue value:dataSet.getDataItemValueMap().values()) {
			sum += value.getConfidence();
		}
		mu = sum/dataSet.getDataItemValueMap().values().size();
		System.out.println("mu:" + mu);
	}
	
	private void initialization() {
		for (DataItem dataItem: dataSet.getDataItemMap().values()) {
			int totalSourceSize = dataItem.getSources().size();
			for(DataItemValue value:dataItem.getValues()) {
				value.setTrueByFusioner(false);
				value.setConfidence(1.0 * value.getSources().size() / totalSourceSize);
			}
		}
	}
	
	private void computeConfidence() {
		double numerator = 0,denominator = 0;
		for(DataItemValue value:dataSet.getDataItemValueMap().values()) {
			numerator = 0;
			denominator = 0;
			for(String sourceIdentifier:value.getSources()) {
				numerator += dataSet.getSource(sourceIdentifier).getTrustworthiness();
			}
			for(String sourceIdentifier:value.getDisagreeSourceIdentifiers()) {
				denominator += dataSet.getSource(sourceIdentifier).getTrustworthiness();
			}
			denominator += numerator;
			value.setConfidence((lambda*mu+numerator)/(lambda+denominator));
		}
	}

	private void computeTrustworthiness() {
		int count = 0;
		double variance = 0;
		double trustworthiness = 0;
		for (Source source : dataSet.getSourceMap().values()) {
			count = 0;
			variance = 0;
			for(String dataItemIdentifier:source.getDataItems()){
				DataItem dataItem = dataSet.getDataItem(dataItemIdentifier);
				for(DataItemValue value:dataItem.getValues()){
					count++;
					Set<String> sources = value.getSources();
					if(sources.contains(source.getSourceIdentifier())){
						variance += Math.pow(value.getConfidence()-1,2);
					}else{
						variance += Math.pow(value.getConfidence(),2);
					}
				}
			}
			trustworthiness = (av+count)/(av*errorRate+variance);
			source.setOldTrustworthiness(source.getTrustworthiness());
			source.setTrustworthiness(trustworthiness);
		}
		computeMu();
	}
}
