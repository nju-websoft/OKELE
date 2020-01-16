package main;

import java.io.IOException;

import algorithm.Fusioner;
import algorithm.FusionerParameters;
import algorithm.mult.BWA;
import algorithm.mult.LatentTruthModel;
import algorithm.mult.MBM;
import algorithm.mult.OKELE;
import algorithm.single.CATD;
import algorithm.single.MajorityVoting;
import algorithm.single.Original;
import algorithm.single.PooledInvestment;
import algorithm.single.LCA;
import algorithm.single.TruthFinder;
import model.DataSet;

public class TruthInference {

	public static void launch(DataSet ds,String algo_name,double trueThreshold, boolean featureFlag) throws IOException {
		Fusioner algo;
		FusionerParameters params = new FusionerParameters();
		switch(algo_name){
			case "Original":
				algo = new Original(ds,params);
				break;
			case "MajorityVoting":
				algo = new MajorityVoting(ds,params);
				break;
			case "TruthFinder":
				algo = new TruthFinder(ds, params);
				break;
			case "PooledInvestment":
				algo = new PooledInvestment(ds,params);
				break;
			case "LCA":
				algo = new LCA(ds,params);
				break;
			case "CATD":
				algo = new CATD(ds,params);
				break;
			case "LTM":
				algo = new LatentTruthModel(ds, params);
				break;
			case "MBM":
				algo = new MBM(ds,params);
				break;
			case "BWA":
				algo = new BWA(ds,params);
				break;
			case "OKELE":
				algo = new OKELE(ds,params);
				break;
			default:
				throw new RuntimeException("Unknown algorithm '" + algo_name + "'");
		}
		algo.launchFusioner(trueThreshold, featureFlag);
	}
}
