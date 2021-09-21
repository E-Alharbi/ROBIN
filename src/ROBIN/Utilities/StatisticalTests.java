package ROBIN.Utilities;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Vector;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.inference.OneWayAnova;
import org.apache.commons.math3.stat.inference.TTest;
import smile.stat.hypothesis.FTest;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
/*
 * calculate Statistical Tests
 */
public class StatisticalTests {
	
	public static void main(String[] args) throws IOException {
		Vector<Double> Var1 = new Vector<Double> ();
		Var1.add(1.0);
		Var1.add(2.0);
		Var1.add(3.0);
		//System.out.println(new StatisticalTests().mean(Var1));
		Var1 = new Vector<Double> ();
		Var1.add(0.1);
		Var1.add(0.02);
		Var1.add(0.03);
		Var1.add(0.1);
		Var1.add(0.02);
		Var1.add(0.03);
		//System.out.println(new StatisticalTests().mean(Var1));
		
		Vector<Double> Var2 = new Vector<Double> ();
		Var2.add(0.1);
		Var2.add(0.02);
		Var2.add(0.1);
		Var2.add(0.1);
		Var2.add(0.02);
		Var2.add(10.0);
		//System.out.println(new StatisticalTests().mean(Var2));
		//System.out.println(new StatisticalTests().anova(Var1, Var2));
		System.out.println(new StatisticalTests().fTest(Var1, Var2));
		System.out.println(new StatisticalTests().RSquared(Var1, Var2));
		System.out.println(new StatisticalTests().STD(Var1));
		System.out.println(new StatisticalTests().STD( Var2));
	}
	public String Mann(Vector<Double> Var1, Vector<Double> Var2) {
		double[] var1 = VectorDoubleToArray(Var1);
		double[] var2 = VectorDoubleToArray(Var2);

		MannWhitneyUTest a = new MannWhitneyUTest();

		return new BigDecimal(a.mannWhitneyUTest(var1, var2)).setScale(2, RoundingMode.HALF_UP).toString();

	}

	
	public String RSquared (Vector<Double> Var1, Vector<Double> Var2) {
		SimpleRegression simple = new SimpleRegression();
		for(int i= 0 ; i < Var1.size(); ++i) {
			simple.addData(Var1.get(i), Var2.get(i));
		}
		
		return new BigDecimal(simple.getRSquare()).setScale(2, RoundingMode.HALF_UP).toString();

	}
	
	public String fTest(Vector<Double> Var1, Vector<Double> Var2) {
		double[] var1 = VectorDoubleToArray(Var1);
		double[] var2 = VectorDoubleToArray(Var2);
		
		return new BigDecimal(FTest.test(var1, var2).pvalue).setScale(2, RoundingMode.HALF_UP).toString();
	}
	public String TTest(Vector<Double> Var1, Vector<Double> Var2) {
		double[] var1 = VectorDoubleToArray(Var1);
		double[] var2 = VectorDoubleToArray(Var2);

		TTest a = new TTest();

		return new BigDecimal(a.pairedTTest(var1, var2)).setScale(2, RoundingMode.HALF_UP).toString();

	}

	public String mean(Vector<Double> Var1) {
		double[] var1 = VectorDoubleToArray(Var1);

		Mean m = new Mean();

		return new BigDecimal(m.evaluate(var1)).setScale(2, RoundingMode.HALF_UP).toString();

	}

	public String STD(Vector<Double> Var1) {
		double[] var1 = VectorDoubleToArray(Var1);
		StandardDeviation SD = new StandardDeviation();

		return new BigDecimal(SD.evaluate(var1)).setScale(2, RoundingMode.HALF_UP).toString();

	}

	public double[] VectorDoubleToArray(Vector<Double> Var) {
		double[] var = new double[Var.size()];
		for (int i = 0; i < Var.size(); ++i)
			var[i] = Var.get(i).doubleValue();

		return var;
	}

	public String anova(Vector<Double> Var1, Vector<Double> Var2) {
		
		OneWayAnova a = new OneWayAnova();
		Vector<double[]> categoryData = new Vector<double[]>();
		categoryData.add(VectorDoubleToArray(Var1));
		categoryData.add(VectorDoubleToArray(Var2));
		
		return new BigDecimal(a.anovaPValue(categoryData)).setScale(2, RoundingMode.HALF_UP).toString();

	}

	public double PC(Vector<String> Actual, Vector<String> Predicted) {
		double[] actual = VectorStringToArray(Actual);
		double[] predicted = VectorStringToArray(Predicted);

		PearsonsCorrelation P = new PearsonsCorrelation();
		return P.correlation(actual, predicted);
	}

	double[] VectorStringToArray(Vector<String> Var) {
		double[] var = new double[Var.size()];
		for (int i = 0; i < Var.size(); ++i)
			var[i] = Double.parseDouble(Var.get(i));

		return var;
	}
}
