package algorithm.single;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;

import algorithm.Fusioner;
import algorithm.FusionerParameters;
import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBQuadExpr;
import gurobi.GRBVar;
import model.DataItem;
import model.DataItemValue;
import model.DataSet;
import model.Source;
import util.ConvergenceTester;

public class CATD extends Fusioner{
	private double alpha = 0.05;
	private Map<Integer,Source> indexSourceMap;

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public CATD(DataSet dataSet, FusionerParameters params) {
		super(dataSet, params);
		this.indexSourceMap = new HashMap<Integer,Source>();
	}

	@Override
	protected int runFusioner() {
		System.out.println(this.getClass().getSimpleName() + ">>>>");
		boolean continueComputation = true;
		int iterationCount = 0;
		initialization();
		while (continueComputation)  {
			iterationCount++;
			
			computeTrustworthiness();
			computeConfidence();

			continueComputation = !ConvergenceTester.isConvergence(dataSet);
		}
		return iterationCount;
	}

	private void initialization() {
		int totalSourceSize = 0;
		double maxConfidence = 0;
		double confidence = 0;
		int maxValueIndetifier=-1;
		for (DataItem dataItem: dataSet.getDataItemMap().values()) {
			totalSourceSize = 0;
			maxConfidence = 0;
			for(DataItemValue value:dataItem.getValues()) {
				totalSourceSize += value.getSources().size();
			}
			for(DataItemValue value:dataItem.getValues()) {
				value.setTrueByFusioner(false);
				confidence = 1.0*value.getSources().size()/totalSourceSize;
				value.setConfidence(confidence);
				if(confidence > maxConfidence) {
					maxValueIndetifier = value.getValueIdentifier();
				}
			}
			dataItem.addInferTruth(maxValueIndetifier);
		}
		int i = 0;
		for(Source source:dataSet.getSourceMap().values()) {
			this.indexSourceMap.put(i++,source);
		}
	}

	private void computeTrustworthiness(){
		double maxTrust = 0;
		for (Source source : dataSet.getSourceMap().values()) {
			int errorCnt = 1;
			for (String sourceClaimIdentifier : source.getSourceClaims()) {
				DataItemValue value = dataSet.getDataItemValue(dataSet.getSourceClaim(sourceClaimIdentifier).getValueIdentifier());
				Set<Integer> inferedTruths = value.getDataItem().getInferTruths();
				if(!inferedTruths.contains(value.getValueIdentifier())) {
					errorCnt = errorCnt+2;
				}
			}
			ChiSquaredDistribution chi = new ChiSquaredDistribution(source.getSourceClaims().size());
			source.setErrorVariance(errorCnt/chi.inverseCumulativeProbability(alpha/2));
		}
		try {
			GRBEnv env = new GRBEnv("example.log");
			GRBModel model = new GRBModel(env);
			model.set("LogToConsole", "0");
			int size = indexSourceMap.size();
			GRBVar[] ws = new GRBVar[size];
			GRBQuadExpr qexpr = new GRBQuadExpr();
			GRBLinExpr lexpr = new GRBLinExpr();
			for (int i=0;i<size;i++) {
				ws[i] = model.addVar(0.0, GRB.INFINITY, 0.0, GRB.CONTINUOUS, "w_"+i);
				qexpr.addTerm(indexSourceMap.get(i).getErrorVariance(), ws[i], ws[i]);
				lexpr.addTerm(1.0, ws[i]);
			}
			model.addConstr(lexpr, GRB.EQUAL, 1.0, "c");
			model.setObjective(qexpr, GRB.MINIMIZE);
			model.write("model.lp");
			model.optimize();
			for(int i = 0;i < ws.length;i++) {
				Source source = indexSourceMap.get(i);
				source.setOldTrustworthiness(source.getTrustworthiness());
				source.setTrustworthiness(ws[i].get(GRB.DoubleAttr.X));
				if(ws[i].get(GRB.DoubleAttr.X) > maxTrust) {
					maxTrust = ws[i].get(GRB.DoubleAttr.X);
				}
			}
			System.out.println("Obj: " + model.get(GRB.DoubleAttr.ObjVal));
			model.dispose();
			env.dispose();
		} catch (GRBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(Source source:dataSet.getSourceMap().values()) {
			source.setTrustworthiness(source.getTrustworthiness()/maxTrust);
		}
	}

	private void computeConfidence() {
		double maxConfidence = 0;
		int maxValueIndetifier=-1;
		double confidence = 0;
		double normalConfidence = 0;
		for (DataItem dataItem: dataSet.getDataItemMap().values()) {
			normalConfidence = 0;
			for(String sourceIdentifier:dataItem.getSources()) {
				normalConfidence += dataSet.getSource(sourceIdentifier).getTrustworthiness();
			}
			maxConfidence = 0;
			maxValueIndetifier=-1;
			for(DataItemValue value:dataItem.getValues()) {
				confidence = 0;
				for(String sourceIdentifier:value.getSources()) {
					confidence += dataSet.getSource(sourceIdentifier).getTrustworthiness();
				}
				confidence = confidence/normalConfidence;
				value.setConfidence(confidence);
				if(confidence > maxConfidence) {
					maxConfidence = confidence;
					maxValueIndetifier = value.getValueIdentifier();
				}
			}
			dataItem.addInferTruth(maxValueIndetifier);
		}
	}

}
