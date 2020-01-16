package main;

import java.util.Vector;

import common.Constants;
import model.Claim;
import model.DataItemValue;
import model.DataSet;
import model.SourceClaim;
import model.Value;

public class Executor {
	  public static boolean isTail = false;
	  public static boolean usePrior = true;
	  public static boolean useFeature = true;
	  public static void main(String[] args) throws Exception{
		  okeleRun();
	  }	   

 	  public static void okeleRun() throws Exception {
 		String[] domains = {"building","book","ship","software","food","mountain","drug","album","film","actor"};
 		String[] types = {"architecture.building","book.book","boats.ship","computer.software","food.food","geography.mountain","medicine.drug","music.album","film.film","film.actor"};
		String dir = Constants.POPULAR_FILE_PATH;
		String prior_dir = Constants.PRIOR_DATA_FILE_PATH_HEAD;
		String exp = ".synthetic";
		
		if(isTail) {
			dir = Constants.TAIL_FILE_PATH;
			prior_dir = Constants.PRIOR_DATA_FILE_PATH_TAIL;
			exp = ".real-world";
		}
		int t = 0;
		for(t=0;t<10;t++){
			System.out.println(types[t]);
			String[] dataSetParams = {
					dir+domains[t]+"/"+types[t]+ exp + ".extraction.okele.json",
					dir+domains[t]+"/"+domains[t]+ exp + ".ground-truth.txt",dir+domains[t]+"/"+types[t]+ exp +".verification.okele.json",
					prior_dir + types[t]+".prior.extraction.okele.json", prior_dir +domains[t]+".prior.ground-truth.txt"};
			KnowledgeDataLoader.setFeatureFile(domains[t],types[t]);
			KnowledgeDataLoader dataLoader;
			if(usePrior) {
				dataLoader = new KnowledgeDataLoader(dataSetParams[0],dataSetParams[1],dataSetParams[3],dataSetParams[4]);
			}else{
				dataLoader = new KnowledgeDataLoader(dataSetParams[0],dataSetParams[1]);
			}
			String algo = "OKELE";
			DataSet ds = DataParser.parse(domains[t],dataLoader);
			ds.resetDataSet();
			TruthInference.launch(ds,algo, 0.5, useFeature);
			Evaluation.valueEval(ds);
			setClaimFlag(ds,dataSetParams[2]);
			System.out.println();
		}
	}
 	public static void compareMethodsRun() throws Exception {
 		  String[] domains = {"building","book","ship","software","food","mountain","drug","album","film","actor"};
 		  String[] types = {"architecture.building","book.book","boats.ship","computer.software","food.food","geography.mountain","medicine.drug","music.album","film.film","film.actor"};
		  String dir = Constants.POPULAR_FILE_PATH;
		  String exp = ".synthetic";
		  Executor.isTail = false;
		  int t = 4;
		  for(t=0;t<9;t++){
			  System.out.println(types[t]);
			  String[] dataSetParams = {dir+domains[t]+"/"+types[t]+ exp +".extraction.okele.json",
	        		dir+domains[t]+"/"+domains[t]+ exp + ".ground-truth.txt",dir+domains[t]+"/"+types[t]+ exp + ".verification.json"};
			  KnowledgeDataLoader.setFeatureFile(domains[t],types[t]);
			  KnowledgeDataLoader dataLoader = new KnowledgeDataLoader(dataSetParams[0],dataSetParams[1]);
			  DataSet ds = DataParser.parse(domains[t],dataLoader);
			  String[] algos = {"Original","MajorityVoting","TruthFinder","PooledInvestment","LCA","LTM","CATD","MBM","BWA"};
			  int i = 1;
			  for(i = 6;i<7;i++) {
				  ds.resetDataSet();
				  TruthInference.launch(ds,algos[i], 0.5, false);
				  Evaluation.valueEval(ds);
				//setClaimFlag(ds,dataSetParams[2]);
				  System.out.println();
			  }
		  }
	}
    private static void setClaimFlag(DataSet ds,String verifiedFile){
         Vector<Claim> originClaims = ds.getDataLoader().getClaims();
         for (String sourceClaimIdentifier:ds.getSourceClaimMap().keySet()) {
             SourceClaim claim = ds.getSourceClaimMap().get(sourceClaimIdentifier);
             DataItemValue div = ds.getDataItemValue(claim.getValueIdentifier());
             if(!div.isPrior_flag()) {
            	 String[] claimValueId = sourceClaimIdentifier.split(Constants.sourceClaimIdentifierDelimiter);
            	 Vector<Value> values = originClaims.get(Integer.valueOf(claimValueId[0])).getValues();
	             if(claim.isTrueByFusioner()) {
	            	 values.get(Integer.valueOf(claimValueId[1])).setFlag(true);
	 			 } else {
	 				 values.get(Integer.valueOf(claimValueId[1])).setFlag(false);
	             }
             }
         }
         KnowledgeDataLoader.after_handle(originClaims, verifiedFile);
     }
}
