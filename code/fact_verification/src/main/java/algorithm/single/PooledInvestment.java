package algorithm.single;

import algorithm.Fusioner;
import algorithm.FusionerParameters;
import common.Constants;
import model.DataItem;
import model.DataItemValue;
import model.DataSet;
import model.Source;
import util.ConvergenceTester;

public class PooledInvestment extends Fusioner{

	private double g = 1.4;

	public double getG() {
		return g;
	}

	public void setG(double g) {
		this.g = g;
	}

	public PooledInvestment(DataSet dataSet, FusionerParameters params) {
		super(dataSet, params);
	}

	@Override
	protected int runFusioner() {
		System.out.println(this.getClass().getSimpleName() + ">>>>");
		boolean continueComputation = true;
		int iterationCount = 0;
		initialization();
		while (continueComputation && iterationCount < Constants.maxIterationCount) {
			iterationCount++;
			
			computeTrustworthiness(iterationCount);
			computeConfidence(iterationCount);
			
			continueComputation = !ConvergenceTester.isConvergence(dataSet);
		}
		return iterationCount;
	}

	// init value confidence by uniform
	private void initialization() {
		for (DataItemValue value: dataSet.getDataItemValueMap().values()) {
			value.setTrueByFusioner(false);
			value.setConfidence(1.0/value.getDataItem().getValues().size());
		}
	}
	
	private void computeTrustworthiness(int iterationCount) {
		for (Source source : dataSet.getSourceMap().values()) {
			double trust = 0.0;
			int claimSize = source.getSourceClaims().size();
			for (String sourceClaimIdentifier : source.getSourceClaims()) {
				DataItemValue value = dataSet.getDataItemValue(dataSet.getSourceClaim(sourceClaimIdentifier).getValueIdentifier());
				double tempSum = 0;
				for(String sourceIdentifier:value.getSources()) {
					Source rsource = dataSet.getSource(sourceIdentifier);
				/*	if(rsource.getOldTrustworthiness()==0) {
						System.out.println(iterationCount + "\t" + rsource.getSourceIdentifier() + "\t" +rsource.getOldTrustworthiness() + "\t"+ tempSum);
					}*/
					tempSum += rsource.getOldTrustworthiness()/rsource.getSourceClaims().size();
				}
				trust = trust + value.getConfidence()*source.getOldTrustworthiness()/(claimSize*tempSum);
			/*	if(Double.isNaN(trust)) {
					//	trust = 0;
						System.out.println(iterationCount + "\t" + source.getSourceIdentifier() + "\t" + value.getConfidence() + "\t" + claimSize + "\t" + tempSum);
						return false;
				}*/
			}
			if(Double.isNaN(trust))
					trust = 0;
			source.setOldTrustworthiness(source.getTrustworthiness());
			source.setTrustworthiness(trust);
		}
	}

	private void computeConfidence(int iterationCount) {
		for (DataItem dataItem: dataSet.getDataItemMap().values()) {
			double totalBeliefSum = 0;
			for(DataItemValue value:dataItem.getValues()) {
				double hc = 0;
				for(String sourceIdentifier:value.getSources()) {
					Source source = dataSet.getSource(sourceIdentifier);
					hc += source.getTrustworthiness()/source.getSourceClaims().size();
				}
				value.setConfidence(hc);
				totalBeliefSum += Math.pow(hc, g);
			}
			for(DataItemValue value:dataItem.getValues()) {
				value.setConfidence(value.getConfidence()*Math.pow(value.getConfidence(), g)/totalBeliefSum);
			}
		}
	}

}
