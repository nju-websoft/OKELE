package truth;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;

import gurobi.*;

public class gurobi{
  public static void main(String[] args) {
	  int size = 6;
	  double alpha = 0.05;
	  ChiSquaredDistribution chi = new ChiSquaredDistribution(size);
	//  System.out.println((372.5965885613522+1)/chi.inverseCumulativeProbability(alpha/2));
	  System.out.println(6/chi.inverseCumulativeProbability(alpha/2));
  }
  public static void grb() {
   try {
    	double ua = 0.1229,ub=3.6871,uc=3.9498,ud=118.49;
      GRBEnv    env   = new GRBEnv("example.log");
      GRBModel  model = new GRBModel(env);
      model.set("LogToConsole", "0");

      // Create variables
      GRBVar wa = model.addVar(0.0, GRB.INFINITY, 0.0, GRB.CONTINUOUS, "wa");
      GRBVar wb = model.addVar(0.0, GRB.INFINITY, 0.0, GRB.CONTINUOUS, "wb");
      GRBVar wc = model.addVar(0.0, GRB.INFINITY, 0.0, GRB.CONTINUOUS, "wc");
      GRBVar wd = model.addVar(0.0, GRB.INFINITY, 0.0, GRB.CONTINUOUS, "wd");

      // Set objective: minimize z
      GRBQuadExpr expr = new GRBQuadExpr();
      expr.addTerm(ua,wa,wa); expr.addTerm(ub,wb,wb); expr.addTerm(uc,wc,wc); expr.addTerm(ud,wd,wd);
      model.setObjective(expr, GRB.MINIMIZE);

      // Add constraint: wa+wb+wc+wd = 1
      GRBLinExpr expr1 = new GRBLinExpr();
      expr1.addTerm(1.0, wa); expr1.addTerm(1.0, wb); expr1.addTerm(1.0, wc);expr1.addTerm(1.0, wd);
      model.addConstr(expr1, GRB.EQUAL, 1.0, "c0");


      // Optimize model
      model.write("model.lp");
      model.optimize();

      System.out.println(wa.get(GRB.StringAttr.VarName)
                         + " " +wa.get(GRB.DoubleAttr.X));
      System.out.println(wb.get(GRB.StringAttr.VarName)
                         + " " +wb.get(GRB.DoubleAttr.X));
      System.out.println(wc.get(GRB.StringAttr.VarName)
                         + " " +wc.get(GRB.DoubleAttr.X));
      System.out.println(wd.get(GRB.StringAttr.VarName)
              + " " +wd.get(GRB.DoubleAttr.X));

      System.out.println("Obj: " + model.get(GRB.DoubleAttr.ObjVal));

      // Dispose of model and environment

      model.dispose();
      env.dispose();

    } catch (GRBException e) {
      System.out.println("Error code: " + e.getErrorCode() + ". " +
                         e.getMessage());
    }
  }
}
