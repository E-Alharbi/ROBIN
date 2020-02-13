package PMBPP.Utilities;

import java.io.File;
import java.util.HashMap;
import java.util.Stack;

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
			RandomForest ML = (RandomForest) weka.core.SerializationHelper.read(model.getAbsolutePath());
			LoadMLModel.AddReadModel(model.getAbsolutePath(), ML);

		} catch (Exception e) {
			// Throwing an exception
			e.printStackTrace();

		}
	}
}

public class LoadMLModel {

	static Stack<File> ModelsStack = new Stack<File>();
	static HashMap<String, RandomForest> AllModels = new HashMap<String, RandomForest>();

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	static synchronized File GetModelToRead() {

		return ModelsStack.pop();
	}

	static synchronized void AddReadModel(String ModelName, RandomForest model) {
		AllModels.put(ModelName, model);

	}

	public HashMap<String, RandomForest> LoadSetOfModels(File[] models) throws Exception {

		for (File m : models) {
			if (m.getName().contains(".model")) {
				ModelsStack.push(m);
			}
		}

		while (ModelsStack.size() != 0) {

			while (Thread.activeCount() != Runtime.getRuntime().availableProcessors() - 1) {
				Thread t = new Thread(new ReadingMLModel());
				t.start();
			}

		}
		while (Thread.activeCount() != 1)
			;

		HashMap<String, RandomForest> temp = new HashMap<String, RandomForest>();
		temp.putAll(AllModels);
		ModelsStack.clear();
		AllModels.clear();

		return temp;
	}
}
