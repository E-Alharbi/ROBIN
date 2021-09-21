package PMBPP.ML.Model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Vector;

import weka.classifiers.meta.RegressionByDiscretization;
import weka.classifiers.trees.RandomForest;
import weka.core.Instance;

//public class RandomForestPI extends  RandomForest{
public class RandomForestPI implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9134752506473378280L;
	
	
	public RandomForest MLPredictor;
	public RegressionByDiscretization MLPredictorPI;
	
	
	
	public RandomForestPI() {
		
	}
	
/*
	String  [] PredictionIntervals(Instance instance) throws Exception {
		Vector<Double> preds = new Vector<Double>();
		
		for (int i = 0; i < m_NumIterations; i++) {
		       
		        preds.add(m_Classifiers[i].classifyInstance(instance));
		}
		
		Collections.sort(preds);
		double percentilelower=((100 - 95) / 2.)/100;
		double percentilehigher=(100-(100 - 95) / 2.)/100;
		
		double nlen = preds.size() - 1; 
				    
		double freclower=nlen * percentilelower;
		double frechigher=nlen * percentilehigher;
		
		double prmidlower= (preds.get((int)Math.ceil(freclower)) +preds.get((int)Math.floor(freclower)) )/2.0;
		double prmidhigher= (preds.get((int)Math.ceil(frechigher)) +preds.get((int)Math.floor(frechigher)) )/2.0;

	String [] lowerhigher= new String[2];
	lowerhigher[0]=String.valueOf(prmidlower);
	lowerhigher[1]=String.valueOf(prmidhigher); 
		//return "("+prmidlower+" "+prmidhigher+")";
	return lowerhigher;
	}
	*/
	
}
