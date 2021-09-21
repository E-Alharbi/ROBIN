package PMBPP.Utilities;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import PMBPP.Log.Log;
import PMBPP.ML.Model.MLModel;
import PMBPP.ML.Model.RandomForestPI;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.meta.RegressionByDiscretization;
import weka.classifiers.trees.RandomForest;

class ReadingMLModel implements Runnable {
	/*
	 * This class to read a bunch of models at once to speed up reading process
	 * using multithreading.
	 * 
	 */

	public void run() {
		try {

			File model = LoadMLModel.GetModelToRead();
			
			if(model!=null) {
				new Log().Info(this, "Loading "+model.getAbsolutePath());
			MLModel ModelToRead= new MLModel();
			
			ModelToRead.ReadModel(model.getAbsolutePath());
			
			LoadMLModel.AddReadModel(model.getAbsolutePath(), ModelToRead.MLPredictor);
			new Log().Info(this, "Loading is done "+model.getAbsolutePath());
			}
			
		} catch (Exception e) {
			// Throwing an exception
			e.printStackTrace();

		}
	}
}

public class LoadMLModel {

	static Stack<File> ModelsStack = new Stack<File>();
	static HashMap<String, RandomForestPI> AllModels = new HashMap<String, RandomForestPI>();

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		Vector<File> models = new Vector<File>();
		for(File model : new File("/Volumes/PhDHardDrive/PMBPP/FinalTraining/PMBPPResults/Experimental/Parrot/PredictionModels").listFiles()) {
			if(model.isDirectory()) {
				
				models.addAll(Arrays.asList(model.listFiles()));
			}
			
		}
		System.out.println(models.size());
		System.out.println(new LoadMLModel().LoadSetOfModels(models.toArray(new File[0])).size());
	}

	static synchronized File GetModelToRead() {

		if(!ModelsStack.isEmpty())
		return ModelsStack.pop();
		
		return null;
	}

	static synchronized void AddReadModel(String ModelName, RandomForestPI model) {
		AllModels.put(ModelName, model);

	}

	public HashMap<String, Object> LoadSetOfModels(File[] models) throws Exception {

		new Log().TxtInRectangle("Reading a set of ML models");
		for (File m : models) {
			if (m.getName().contains(".model")) {
				ModelsStack.push(m);
			}
		}
		ExecutorService es = Executors.newCachedThreadPool();
		while (ModelsStack.size() != 0) {

			
				es.execute(new ReadingMLModel());// new thread
				
		

		}
		
		es.shutdown();
		
		while(es.isTerminated()==false) ;
		
		new Log().Info(this, "Finished reading the set of ML models");

		HashMap<String, Object> temp = new HashMap<String, Object>();
		temp.putAll(AllModels);
		ModelsStack.clear();
		AllModels.clear();

		return temp;
	}
}
