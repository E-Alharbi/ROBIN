package PMBPP.Utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Vector;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.inference.OneWayAnova;
import org.apache.commons.math3.stat.inference.TTest;

public class StatisticalTests {
	public String TTest(Vector<Double> Var1,Vector<Double> Var2) {
		double[] var1 = VectorDoubleToArray(Var1);
		double[] var2 = VectorDoubleToArray(Var2);
		
		
		TTest a = new TTest();
		System.out.println(var1.length);
		System.out.println(var2.length);
		return new BigDecimal(a.pairedTTest(var1, var2)).setScale(2, RoundingMode.HALF_UP).toString();

	}
	
	public String mean (Vector<Double> Var1) {
		double[] var1 = VectorDoubleToArray(Var1);
		
		Mean m = new Mean();
		
		return new BigDecimal(m.evaluate(var1)).setScale(2, RoundingMode.HALF_UP).toString();
		
	}
	public String STD(Vector<Double> Var1) {
		double[] var1 = VectorDoubleToArray(Var1);
		StandardDeviation  SD= new StandardDeviation();
		
		return new BigDecimal(SD.evaluate(var1)).setScale(2, RoundingMode.HALF_UP).toString();
		
	}
	public double [] VectorDoubleToArray(Vector<Double> Var) {
		double[] var = new double[Var.size()];
		for(int i=0 ; i < Var.size() ; ++i)
			var[i]=Var.get(i).doubleValue();
		
		return var;
	}
	
	public String anova(Vector<Double> Var1,Vector<Double> Var2) {
		OneWayAnova a = new OneWayAnova();
		Vector<double[]> categoryData = new Vector<double[]>();
		categoryData.add(VectorDoubleToArray(Var1));
		categoryData.add(VectorDoubleToArray(Var2));
		return new BigDecimal(a.anovaPValue(categoryData)).setScale(2, RoundingMode.HALF_UP).toString();

	}
	public double PC(Vector<String> Actual , Vector<String> Predicted) {
		double [] actual = VectorStringToArray(Actual);
		double [] predicted = VectorStringToArray(Predicted);
		
		
		PearsonsCorrelation P = new PearsonsCorrelation();
		return P.correlation(actual, predicted);
	}
	double [] VectorStringToArray(Vector<String> Var) {
		double[] var = new double[Var.size()];
		for(int i=0 ; i < Var.size() ; ++i)
			var[i]=Double.parseDouble(Var.get(i));
		
		return var;
	}
}
