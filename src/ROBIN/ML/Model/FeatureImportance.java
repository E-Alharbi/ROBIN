package ROBIN.ML.Model;

import java.util.HashMap;

public class FeatureImportance extends HashMap<String,MLEvaluation> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String HashMapName;

	public String getHashMapName() {
		return HashMapName;
	}

	public void setHashMapName(String hashMapName) {
		HashMapName = hashMapName;
	}
	
}
