package algorithm.single;

import algorithm.Fusioner;
import algorithm.FusionerParameters;
import common.Constants;
import model.DataItem;
import model.DataItemValue;
import model.DataSet;
import model.Source;
import util.ConvergenceTester;

public class LCA extends Fusioner {
	private double beta = 0.5;

	public double getBeta() {
		return beta;
	}

	public void setBeta(double beta) {
		this.beta = beta;
	}

	public LCA(DataSet dataSet, FusionerParameters params) {
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
			computeConfidence();
			computeTrustworthiness();
			continueComputation = !ConvergenceTester.isConvergence(dataSet);
		}
		return iterationCount;
	}

	private void initialization() {
		for (Source source : dataSet.getSourceMap().values()) {
			source.setTrustworthiness(params.getStartingTrust());
			source.setOldTrustworthiness(params.getStartingTrust());
		}
	}
	
	private void computeTrustworthiness() {
		for (Source source : dataSet.getSourceMap().values()) {
			double trust = 0.0;
			for (String sourceClaimIdentifier : source.getSourceClaims()) {
				trust = trust + dataSet.getDataItemValue(dataSet.getSourceClaim(sourceClaimIdentifier).getValueIdentifier()).getConfidence();
			}
			trust = trust/source.getDataItems().size();
			source.setOldTrustworthiness(source.getTrustworthiness());
			source.setTrustworthiness((trust>1)?1:trust);
		}
	}

	private void computeConfidence() {
		for(DataItem dataItem:dataSet.getDataItemMap().values()) {
			int m = dataItem.getValues().size();
			if(m == 1) {
				for(DataItemValue value:dataItem.getValues()) {
					value.setConfidence(1.0);
				}
			}else {
				for(DataItemValue value:dataItem.getValues()) {
					double belief = 1.0;
					for(String sourceIdentifier:value.getSources()) {// w_s_m = 1 and b_s_ym = 1
						belief *= dataSet.getSource(sourceIdentifier).getTrustworthiness();
					}
					for(String sourceIdentifier:value.getDisagreeSourceIdentifiers()) { // w_s_m = 1 and b_s_ym = 0
						belief *= (1-dataSet.getSource(sourceIdentifier).getTrustworthiness())/(m-1);
					}
					value.setConfidence(beta*belief);
				}
			}
		}
	}
}