package truth;

import common.Constants;
import main.DataParser;
import main.Evaluation;
import main.Executor;
import main.KnowledgeDataLoader;
import main.TruthInference;
import model.*;

public class Test {
	
	  public static void main(String[] args) throws Exception{
		  compareMethodsRun();
	  }	   
	 public static void compareMethodsRun() throws Exception {
		  String[] domains = {"building","book","ship","software","food","mountain","drug","album","film","actor"};
		  String[] types = {"architecture.building","book.book","boats.ship","computer.software","food.food","geography.mountain","medicine.drug","music.album","film.film","film.actor"};
		  String dir = Constants.POPULAR_FILE_PATH;
		  String exp = ".synthetic";
		  Executor.isTail = false;
		  int t = 4;
		  for(t=0;t<10;t++){
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
}
