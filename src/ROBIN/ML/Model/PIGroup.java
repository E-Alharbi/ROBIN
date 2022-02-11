package ROBIN.ML.Model;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;

import ROBIN.Utilities.CSVReader;
import ROBIN.Utilities.CSVWriter;

public class PIGroup {
// convert prediction interval to prediction group. 
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Parameters.setPrefix("2320617411910776024");
		new PIGroup().PIToGroupFromCSV("/Users/emadalharbi/eclipse/jee-2020-12/Eclipse.app/Contents/MacOS/Predicted_datasets.csv");
	}
   public  void PIToGroupFromCSV(String CSVPath) throws IOException {
    	
	   HashMap<String, Vector<HashMap<String, String>>> predicteddataset = new CSVReader(CSVPath).ReadIntoHashMap( "PDB");
	   
	    
	    for(String pdb : predicteddataset.keySet()) {
	    	new PIGroup().GroupingPI(predicteddataset,pdb,"R-work","0.05");
	    	new PIGroup().GroupingPI(predicteddataset,pdb,"R-free","0.05");
	    	new PIGroup().GroupingPI(predicteddataset,pdb,"Completeness","5");
	    	 
	    }
	    
	    new CSVWriter().WriteFromHashMapContainsRepatedRecord(predicteddataset, "Predicted_dataset_with_prediction_group.csv", "PDB", false);
    }
	void GroupingPI(HashMap<String, Vector<HashMap<String, String>>> predicteddataset, String pdb,String evaluation_col, String Margin) {
		 
		    HashMap<String,String[]> evaluation_measure = new  HashMap<String,String[]>();
		   
		   
		    for(int pipe=0; pipe < predicteddataset.get(pdb).size();++pipe) {
		    	predicteddataset.get(pdb).get(pipe).remove("Prediction"); // remove this col. No need to be in the final CSV

		    	if(!predicteddataset.get(pdb).get(pipe).get(evaluation_col).equals("-")) {
		    	
		    		evaluation_measure.put(predicteddataset.get(pdb).get(pipe).get("Pipeline"), new String [] {predicteddataset.get(pdb).get(pipe).get(evaluation_col),predicteddataset.get(pdb).get(pipe).get(evaluation_col+" prediction interval high"),predicteddataset.get(pdb).get(pipe).get(evaluation_col+" prediction interval low")});
		    	
		    	}
		    	else {// we do not want them to be in the final CSV
		    		predicteddataset.get(pdb).get(pipe).remove(evaluation_col+" prediction interval high");
			    	predicteddataset.get(pdb).get(pipe).remove(evaluation_col+" prediction interval low");	
			    	predicteddataset.get(pdb).get(pipe).put(evaluation_col+" prediction group", "-");

		    	}
		    }
		    if(evaluation_measure.size()!=0) {
		   
		    HashMap<String,String[]> predictiongroup= new PIGroup().PIScoreToGroup(evaluation_measure,Margin);
		    for(int pipe=0; pipe < predicteddataset.get(pdb).size();++pipe) {
		    	predicteddataset.get(pdb).get(pipe).remove(evaluation_col+" prediction interval high");
		    	predicteddataset.get(pdb).get(pipe).remove(evaluation_col+" prediction interval low");
		    	if(predictiongroup.containsKey(predicteddataset.get(pdb).get(pipe).get("Pipeline")))
		    	predicteddataset.get(pdb).get(pipe).put(evaluation_col+" prediction group", predictiongroup.get(predicteddataset.get(pdb).get(pipe).get("Pipeline"))[1]);
		    }
		    }
		   
	}
	
	public PIGroup() {
		
	}
	HashMap<String,String[]> PIScoreToGroup( HashMap<String,String[]> PI, String Margin ){
		LinkedHashMap<String,BigDecimal> Score = new LinkedHashMap<String,BigDecimal>();// LinkedHashMap sorts by value 
		for(String Pipeline : PI.keySet()) {
			BigDecimal diff = new BigDecimal(PI.get(Pipeline)[2]).subtract(new BigDecimal(PI.get(Pipeline)[1])).abs();
			Score.put(Pipeline, diff);
		}
		
		
		Score = Score.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
	
		String PreviousPipeline="";
		double PipeScore =1;
		HashMap<String,String[]> Groups = new HashMap<String,String[]>();// LinkedHashMap sorts by value 

		for(String Pipeline : Score.keySet()) {
			String [] predction= PI.get(Pipeline);
			predction[1]=String.valueOf(PipeScore);
			predction[2]=String.valueOf(PipeScore);
			if(PreviousPipeline.equals("")) {
				Groups.put(Pipeline, predction);
				
			}
			else {
				BigDecimal diff = Score.get(Pipeline).subtract(Score.get(PreviousPipeline)).abs();
				
			
				if(diff.compareTo(new BigDecimal(Margin))<=0) {
					Groups.put(Pipeline, predction);
					
				}
				else {
					PipeScore++;
					predction[1]=String.valueOf(PipeScore);
					predction[2]=String.valueOf(PipeScore);
					Groups.put(Pipeline, predction);
				}
			}
			PreviousPipeline=Pipeline;
		}
		
		return Groups;
	}
}
