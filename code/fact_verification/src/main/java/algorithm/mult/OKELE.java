package algorithm.mult;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;

import algorithm.Fusioner;
import algorithm.FusionerParameters;
import main.Executor;
import model.DataItem;
import model.DataItemValue;
import model.DataSet;
import model.Source;
import util.ConvergenceTester;

/*
 * ChiSquare distribution, beta distribution
 */
public class OKELE extends Fusioner{
	private double alpha = 0.05;
	private double beta1 = 5;
	private double beta2 = 5;
	private double learningRate = 0.001;
	
	public OKELE(DataSet dataSet, FusionerParameters params) {
		super(dataSet, params);
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	@Override
	protected int runFusioner() {
		System.out.println(this.getClass().getSimpleName() + ">>>>");
		int iterationCount = 0;
		computeSourcePrior();
		computeTrustworthiness();
		computeConfidence();
		return iterationCount;
	}
	
	private void computeSourcePrior() {
		initialization();
		int count = 0;
		double variance = 0;
		for (Source source : dataSet.getSourceMap().values()) {
			variance = 0;
			for (String sourceClaimIdentifier : source.getSourceClaims()) {
				DataItemValue div = dataSet.getDataItemValue(dataSet.getSourceClaim(sourceClaimIdentifier).getValueIdentifier());
				variance += Math.pow(div.getConfidence()-1,2);
			}
			count = source.getSourceClaims().size();
			source.setClaimCount(count);
			ChiSquaredDistribution chi = new ChiSquaredDistribution(count);
			source.setErrorVariance(variance/chi.inverseCumulativeProbability(alpha/2));
		}
	}
	
	private void computeConfidence() {
		for(DataItemValue value:dataSet.getDataItemValueMap().values()) {
			if(value.getConfidence() == 1.0){
				continue;
			}
			double rs = 0;
			double tf_k = value.getConfidence();
			double tf_k1 = tf_k,temp = 0;
			double derivative = 0;
			int i = 0;
			do {
				for(String sourceIdentifier:value.getSources()) {
					rs += dataSet.getSource(sourceIdentifier).getTrustworthiness()*(tf_k-1);
				}
				for(String sourceIdentifier:value.getDisagreeSourceIdentifiers()) {
					rs += dataSet.getSource(sourceIdentifier).getTrustworthiness()*tf_k;
				}
				derivative = (beta1-1)/tf_k - (beta2-1)/(1.0-tf_k) - rs;
				temp = tf_k1;
				tf_k1 = tf_k + learningRate * derivative;
				tf_k = temp;
				i++;
			} while(Math.abs(tf_k1-tf_k) > 0.001 && i < 500);
			value.setConfidence(tf_k1);
		}
	}
		
	private void computeTrustworthiness() {
		int count = 0;
		double trustworthiness = 0;
		double variance = 0;
		for (Source source : dataSet.getSourceMap().values()) {
			variance = 0;
			for (String sourceClaimIdentifier : source.getSourceClaims()) {
				DataItemValue div = dataSet.getDataItemValue(dataSet.getSourceClaim(sourceClaimIdentifier).getValueIdentifier());
				variance += Math.pow(div.getConfidence()-1,2);
			}
			count = source.getSourceClaims().size();
			if(Executor.isTail) {
				ChiSquaredDistribution chi = new ChiSquaredDistribution(count);
				variance = variance/chi.inverseCumulativeProbability(alpha/2);
			}
			if(variance == 0) {
				trustworthiness = 1.0;
			} else {
				trustworthiness = (source.getClaimCount() + count)/(source.getClaimCount()*source.getErrorVariance() + variance);
			}
			source.setOldTrustworthiness(source.getTrustworthiness());
			source.setTrustworthiness(trustworthiness);
		}
	}
	
	private void initialization() {
		for (DataItem dataItem: dataSet.getDataItemMap().values()) {
			int totalSourceSize = dataItem.getSources().size();
			for(DataItemValue value:dataItem.getValues()) {
				if(value.isPrior_flag() && value.getConfidence() == 1.0) 
					continue;
				value.setTrueByFusioner(false);
				value.setConfidence(1.0 * value.getSources().size() / totalSourceSize);
			}
		}
		if((dataSet.getName().equals("film") || dataSet.getName().equals("food")) && !Executor.isTail){
			boolean continueComputation = true;
			while (continueComputation) {
				double maxTrust = 0;
				for (Source source : dataSet.getSourceMap().values()) {
					double trust = 0.0;
					for (String sourceClaimIdentifier : source.getSourceClaims()) {
						DataItemValue value = dataSet.getDataItemValue(dataSet.getSourceClaim(sourceClaimIdentifier).getValueIdentifier());
						trust = trust + value.getConfidence()/value.getSources().size();
					}
					source.setOldTrustworthiness(source.getTrustworthiness());
					source.setTrustworthiness(trust);
					if(trust > maxTrust)
						maxTrust = trust;
				}
				for(Source source:dataSet.getSourceMap().values()) {
					source.setTrustworthiness(source.getTrustworthiness()/maxTrust);
				}
				double maxBelief = 0;
				for(DataItemValue value:dataSet.getDataItemValueMap().values()) {
					if(value.getConfidence() == 1.0)
						continue;
					double belief = 0;
					for(String sourceIdentifier:value.getSources()) {
						belief += dataSet.getSource(sourceIdentifier).getTrustworthiness();
					}
					value.setConfidence(belief);
					if(belief > maxBelief) {
						maxBelief = belief;
					}
				}
				for(DataItemValue value:dataSet.getDataItemValueMap().values()) {
					if(value.getConfidence() == 1.0)
						continue;
					value.setConfidence(value.getConfidence()/maxBelief);
				}
				continueComputation = !ConvergenceTester.isConvergence(dataSet);
			}
		}
	}
}
