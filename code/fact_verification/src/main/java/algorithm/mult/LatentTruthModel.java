package algorithm.mult;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.UniformRandomGenerator;

import algorithm.Fusioner;
import algorithm.FusionerParameters;
import model.DataItem;
import model.DataItemValue;
import model.DataSet;
import model.Source;
import model.SourceClaim;

public class LatentTruthModel extends Fusioner {
	
	HashMap<String, LTMSourceData> sourceCounts;

	double b1=10, b0=10;
	double a00=1000, a01=100, a10=50, a11=50;
	int iterationCount=300;
	int burnIn=100;
	int thin=5;

	public LatentTruthModel(DataSet dataSet,FusionerParameters params) {
		super(dataSet, params);
		sourceCounts = new HashMap<String, LTMSourceData>();
		for (String key : dataSet.getSourceMap().keySet()) {
			sourceCounts.put(key, new LTMSourceData());
		}
	}
	@Override
	protected int runFusioner() {
		System.out.println(this.getClass().getSimpleName() + ">>>>");
		dataSet.resetDataSet(0, 0);
		initialization();
		sampling();
		LTMSourceData sCount;
		for(Source source : dataSet.getSourceMap().values()){
			sCount = sourceCounts.get(source.getSourceIdentifier());
			//precision as source trust worthiness
			source.setTrustworthiness(sCount.getE11()/(sCount.getE11()+sCount.getE01()));
			source.setPrecision(sCount.getE11()/(sCount.getE11()+sCount.getE01()));
			source.setRecall(sCount.getE11()/(sCount.getE10()+sCount.getE11()));
			source.setSpecificity(sCount.getE00()/(sCount.getE01()+sCount.getE00()));
		}
		return iterationCount;
	}
	private void initialization() {
		boolean tf = false;
		for (DataItem dataItem: dataSet.getDataItemMap().values()) {
			for (DataItemValue value : dataItem.getValues()) {
				Set<String> disagreeSources = new HashSet<String>(dataItem.getSources());
				disagreeSources.removeAll(value.getSources());
				if (!tf) { // truth:false
					for (String sourceClaimIdentifier : value.getSourceClaims()) {
						SourceClaim claim = dataSet.getSourceClaim(sourceClaimIdentifier);
						claim.setTrueByFusioner(false);
						sourceCounts.get(claim.getSourceIdentifier()).incrementN01();//observation:true
						for (String disagreeingSource : disagreeSources) {
							sourceCounts.get(disagreeingSource).incrementN00();//observation:false
						}
					}
				} else { // truth:true
					for (String sourceClaimIdentifier : value.getSourceClaims()) {
						SourceClaim claim = dataSet.getSourceClaim(sourceClaimIdentifier);
						claim.setTrueByFusioner(true);
						sourceCounts.get(claim.getSourceIdentifier()).incrementN11();//observation:true
						for (String disagreeingSource : disagreeSources) {
							sourceCounts.get(disagreeingSource).incrementN10();//observation:false
						}
					}
				}
				tf = !tf;
			}
		}
	}
	private void sampling() {
		LTMSourceData sCount;
		double ptf, p1minustf;
		boolean tf;
		boolean flag;

		RandomGenerator rg;
		UniformRandomGenerator random;

		int sampleSize;
		if (thin > 0) {
			sampleSize = ((iterationCount-burnIn) / thin);
		} else {
			sampleSize = iterationCount - burnIn;
		}

		for (int i = 0; i < iterationCount; i ++) {
			rg = new JDKRandomGenerator();
			rg.setSeed(Math.round((Math.random() * 100000)));
			random = new UniformRandomGenerator(rg);

			for (DataItem dataItem: dataSet.getDataItemMap().values()) {
				for (DataItemValue value : dataItem.getValues()) {
					tf = dataSet.getSourceClaim(value.getSourceClaims().get(0)).isTrueByFusioner();
					if (tf) {
						ptf = b1;
						p1minustf = b0;
					} else {
						ptf = b0;
						p1minustf = b1;
					}
					Set<String> disagreeSources = new HashSet<String>(dataItem.getSources());
					disagreeSources.removeAll(value.getSources());
					// oc = 1
					for (String sourceClaimIdentifier : value.getSourceClaims()) {
						SourceClaim claim = dataSet.getSourceClaim(sourceClaimIdentifier);
						sCount = sourceCounts.get(claim.getSourceIdentifier());
						if (tf) { // tf = 1 and oc = 1
							ptf = (ptf * (sCount.getN11() -1 + a11 ) )
									/ 
									(sCount.getN11() + sCount.getN10() - 1 + a11 + a10) ;
							p1minustf = (p1minustf * (sCount.getN01()  + a01 ))
									/
									(sCount.getN01() + sCount.getN00() + a01 + a00);

						} else { // tf = 0 and oc = 1
							ptf = (ptf * (sCount.getN01() -1 + a01 ) )
									/ 
									(sCount.getN01() + sCount.getN00() - 1 + a01 + a00) ;

							p1minustf = (p1minustf * (sCount.getN11() + a11 ))
									/
									(sCount.getN11() + sCount.getN10() + a11 + a10);

						}
					}
					// oc = 0
					for (String key : disagreeSources) {
						sCount = sourceCounts.get(key);
						if (tf) { // tf = 1 and oc = 0
							ptf = (ptf * (sCount.getN10()  -1 + a10 ) ) 
									/
									(sCount.getN11() + sCount.getN10() - 1 + a11 + a10);

							p1minustf = (p1minustf * (sCount.getN00() + a00 )) 
									/ 
									(sCount.getN01() + sCount.getN00() + a01 + a00);
						} else { // tf = 0 and oc = 0
							ptf = (ptf * (sCount.getN00()  -1 + a00 ) )
									/ 
									(sCount.getN01() + sCount.getN00() - 1 + a01 + a00) ;

							p1minustf = (p1minustf * (sCount.getN10()  + a10 ))
									/
									(sCount.getN11() + sCount.getN10() + a11 + a10);
						}
					}
					double rand = random.nextNormalizedDouble() + Math.sqrt(3); 
					rand = ((double)rand/(2*Math.sqrt(3)));
					double temp = p1minustf/(ptf + p1minustf);
					if (rand < temp) {
						// tf changed, update counts
						// oc = 1 
						for (String sourceClaimIdentifier : value.getSourceClaims()) {
							SourceClaim claim = dataSet.getSourceClaim(sourceClaimIdentifier);
							sCount = sourceCounts.get(claim.getSourceIdentifier());
							if (tf) { // tf was true and now false
								claim.setTrueByFusioner(false);
								sCount.decrementN11();
								sCount.incrementN01();
							} else { // tf was false and now true
								claim.setTrueByFusioner(true);
								sCount.decrementN01();
								sCount.incrementN11();
							}
						}
						// oc = 0
						for (String key : disagreeSources) {
							sCount = sourceCounts.get(key);
							if (tf) { // tf was true and now false
								sCount.decrementN10();
								sCount.incrementN00();
							} else { // tf was false and now true
								sCount.decrementN00();
								sCount.incrementN10();
							}
						}
					}
					if (thin == 0 || (thin > 0 && i % thin == 0)) {
						flag = true;
					} else  {
						flag = false;
					}
					if (i > burnIn && flag) {
						if (dataSet.getSourceClaim(value.getSourceClaims().get(0)).isTrueByFusioner()) {
							value.setConfidence(value.getConfidence() + (((double)1/sampleSize)));
						}
						for(Source source : dataSet.getSourceMap().values()){
							sCount = sourceCounts.get(source.getSourceIdentifier());
							sCount.setE00(sCount.getE00()+sCount.getN00());
							sCount.setE01(sCount.getE01()+sCount.getN01());
							sCount.setE10(sCount.getE10()+sCount.getN10());
							sCount.setE11(sCount.getE11()+sCount.getN11());
						}
					}
				}
			}
		 }
		
		for(Source source : dataSet.getSourceMap().values()){
			sCount = sourceCounts.get(source.getSourceIdentifier());
			sCount.setE00(sCount.getE00()/sampleSize + a00);
			sCount.setE01(sCount.getE01()/sampleSize + a01);
			sCount.setE10(sCount.getE10()/sampleSize + a10);
			sCount.setE11(sCount.getE11()/sampleSize + a11);
		}
	}
	
}
