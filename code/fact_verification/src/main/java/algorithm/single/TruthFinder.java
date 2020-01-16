package algorithm.single;

import algorithm.Fusioner;
import algorithm.FusionerParameters;
import common.Constants;
import model.DataItem;
import model.DataItemValue;
import model.DataSet;
import model.Source;
import util.ConvergenceTester;
import util.DataComparator;

public class TruthFinder extends Fusioner {

	private double baseSim = 0.5; //base_sim
	private double rho = 0.5; // rho, p
	private double gamma = 0.3; // gamma

	public TruthFinder(DataSet dataSet, FusionerParameters params) {
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
			
			computeConfidenceScore();
			computeConfidenceScoreWithSimilarity();
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
	private void computeConfidenceScore() {
		double lnSum = 0;
		for (DataItem dataItem: dataSet.getDataItemMap().values()) {
			for (DataItemValue value : dataItem.getValues()) {
				lnSum = 0;
				for (String source : value.getSources()) {
					lnSum = lnSum - Math.log(1 - dataSet.getSource(source).getTrustworthiness());
				}
				value.setConfidence(lnSum);
			}
		}
	}

	private void computeConfidenceScoreWithSimilarity() {
		double similarity;
		double similaritySum;
		for (DataItem dataItem: dataSet.getDataItemMap().values()) {
			for (DataItemValue value1 : dataItem.getValues()) {
				similaritySum = 0;
				for (DataItemValue value2 : dataItem.getValues()) {
					if (value1.getValueIdentifier() == value2.getValueIdentifier()) {
						continue;
					}
					similarity = computeValuesSimilarity(value1, value2);
					similaritySum = similaritySum + (value2.getConfidence() * similarity);
				}
				similaritySum = value1.getConfidence() + (rho * similaritySum);
				value1.setConfidence(similaritySum);
			}
		}		
	}

	private void computeConfidence() {
		for (DataItem dataItem: dataSet.getDataItemMap().values()) {
			for (DataItemValue value : dataItem.getValues()) {
				value.setConfidence(1/(1 + Math.exp(-1 * gamma * value.getConfidence())));
			}
		}
	}
	private double computeValuesSimilarity(DataItemValue value1, DataItemValue value2) {
		double result = DataComparator.computeImplication(value1, value2, value1.getValueType());
		result = result - baseSim;
		return result;
	}
	
	private void computeTrustworthiness() {
		double sum;
		for (Source source : dataSet.getSourceMap().values()) {
			sum = 0.0;
			for (String sourceClaimIdentifier : source.getSourceClaims()) {
				sum = sum + dataSet.getDataItemValue(dataSet.getSourceClaim(sourceClaimIdentifier).getValueIdentifier()).getConfidence();
			}
			source.setOldTrustworthiness(source.getTrustworthiness());
			source.setTrustworthiness(sum / source.getSourceClaims().size());
		}
	}
}
