package algorithm;

import java.util.ArrayList;
import java.util.List;

import main.Executor;
import model.DataItem;
import model.DataItemValue;
import model.DataSet;

public abstract class Fusioner {

	protected DataSet dataSet;	
	protected FusionerParameters params;
	
	protected abstract int runFusioner();
	
	public Fusioner(DataSet dataSet, FusionerParameters params) {
		this.dataSet = dataSet;
		this.params = params;
	}
	
	public int launchFusioner(double trueThreshold, boolean featureFlag) {
		this.dataSet.resetDataSet(params.getStartingTrust(), params.getStartingConfidence());
		System.out.println("Fact verification Begin!");
		long startTime=System.currentTimeMillis();
		int iterationCount = runFusioner();
		long endTime=System.currentTimeMillis();
		System.out.println("End! Time: " + (endTime-startTime)+"ms");
		if(featureFlag)
			computeTruthByFeature(trueThreshold); 
		else
			computeTruthByThreshold(trueThreshold); 
		return iterationCount;
	}

	public void computeTruthByThreshold(double confidence) {
		boolean getmaxflag = false;
		DataItemValue max;
		if (getmaxflag) {
			for (DataItem dataItem: dataSet.getDataItemMap().values()) {
				List<DataItemValue> values = new ArrayList<DataItemValue>(dataItem.getValues());
				max = values.get(0);
				for (DataItemValue value : values) {
					value.setTrueByFusioner(false);
					if (value.getConfidence() > max.getConfidence()) {
						max = value;
					}
				}
				max.setTrueByFusioner(true);
				for (String sourceClaimIdentifier: max.getSourceClaims()) {
					dataSet.getSourceClaim(sourceClaimIdentifier).setTrueByFusioner(true);
				}

				for(DataItemValue value:values) {
					if(value.getConfidence()==max.getConfidence()) {
						value.setTrueByFusioner(true);
						for (String sourceClaimIdentifier : value.getSourceClaims()) {
							dataSet.getSourceClaim(sourceClaimIdentifier).setTrueByFusioner(true);
						}
					}
				}
			}
		} else {
			for (DataItem dataItem: dataSet.getDataItemMap().values()) {
				boolean flag = false;
				for (DataItemValue value : dataItem.getValues()) {
					if (value.getConfidence() > confidence) {
						flag = true;
						value.setTrueByFusioner(true);
						for (String sourceClaimIdentifier: value.getSourceClaims()) {
							dataSet.getSourceClaim(sourceClaimIdentifier).setTrueByFusioner(true); 
						}
					}else{
						value.setTrueByFusioner(false);
					}
				}
				if(!flag) {
					List<DataItemValue> values = new ArrayList<DataItemValue>(dataItem.getValues());
					max = values.get(0);
					for (DataItemValue value : values) {
						if (value.getConfidence() > max.getConfidence()) {
							max = value;
						}
					}
					max.setTrueByFusioner(true);
					for (String sourceClaimIdentifier: max.getSourceClaims()) {
						dataSet.getSourceClaim(sourceClaimIdentifier).setTrueByFusioner(true);
					}
					for(DataItemValue value:values) {
						if(value.getConfidence()==max.getConfidence()) {
							value.setTrueByFusioner(true);
							for (String sourceClaimIdentifier : value.getSourceClaims()) {
								dataSet.getSourceClaim(sourceClaimIdentifier).setTrueByFusioner(true);
							}
						}
					}
				}
			}
		}
	}
	public void computeTruthByFeature(double confidence) {
		
		DataItemValue max;
		for (DataItem dataItem: dataSet.getDataItemMap().values()) {
			boolean isSingleValue = dataItem.isSingleValue();
			if(isSingleValue) {
				List<DataItemValue> values = new ArrayList<DataItemValue>(dataItem.getValues());
				max = values.get(0);
				for (DataItemValue value : values) {
					value.setTrueByFusioner(false);
					if (value.getConfidence() > max.getConfidence()) {
						max = value;
					}
				}
				max.setTrueByFusioner(true);
				for (String sourceClaimIdentifier: max.getSourceClaims()) {
					dataSet.getSourceClaim(sourceClaimIdentifier).setTrueByFusioner(true);
				}
				if(Executor.isTail) {
					for(DataItemValue value:values) {
						if(value.getConfidence()==max.getConfidence()) {
							value.setTrueByFusioner(true);
							for (String sourceClaimIdentifier : value.getSourceClaims()) {
								dataSet.getSourceClaim(sourceClaimIdentifier).setTrueByFusioner(true);
							}
						}
					}
				}
			}else{
				boolean flag = false;
				for (DataItemValue value : dataItem.getValues()) {
					if (value.getConfidence() > confidence) {
						flag = true;
						value.setTrueByFusioner(true);
						for (String sourceClaimIdentifier: value.getSourceClaims()) {
							dataSet.getSourceClaim(sourceClaimIdentifier).setTrueByFusioner(true);
						}
					}else{
						value.setTrueByFusioner(false);
					}
				}
				if(!flag) {
					List<DataItemValue> values = new ArrayList<DataItemValue>(dataItem.getValues());
					max = values.get(0);
					for (DataItemValue value : values) {
						if (value.getConfidence() > max.getConfidence()) {
							max = value;
						}
					}
					max.setTrueByFusioner(true);
					for (String sourceClaimIdentifier: max.getSourceClaims()) {
						dataSet.getSourceClaim(sourceClaimIdentifier).setTrueByFusioner(true);
					}
					for(DataItemValue value:values) {
						if(value.getConfidence()==max.getConfidence()) {
							value.setTrueByFusioner(true);
							for (String sourceClaimIdentifier : value.getSourceClaims()) {
								dataSet.getSourceClaim(sourceClaimIdentifier).setTrueByFusioner(true);
							}
						}
					}
				}
			}
		}
	}
}
