package PMBPP.ML.Model;

import weka.classifiers.evaluation.Evaluation;
import weka.core.Instances;

public class MLEvaluation extends Evaluation{

	String Dataset="";
	public String getDataset() {
		return Dataset;
	}


	public void setDataset(String dataset) {
		Dataset = dataset;
	}


	public MLEvaluation(Instances data) throws Exception {
		super(data);
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
