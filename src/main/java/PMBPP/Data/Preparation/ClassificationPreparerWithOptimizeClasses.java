package PMBPP.Data.Preparation;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.json.simple.parser.ParseException;

import PMBPP.Log.Log;
import PMBPP.ML.Model.Parameters;
import PMBPP.Utilities.CSVReader;
import PMBPP.Utilities.CSVWriter;
import PMBPP.Validation.CustomException;

public class ClassificationPreparerWithOptimizeClasses {

	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, IOException, ParseException, CustomException {
		// TODO Auto-generated method stub
		String DataPath="/Volumes/PhDHardDrive/EditorsRevision-2/Datasets/NO-NCS/";
		Parameters.AttCSV="/Users/emadalharbi/Downloads/TestPreAcc/PredictionModels/Completeness/Buccaneeri1I5.csv";
		new ClassificationPreparerWithOptimizeClasses().Optimize(DataPath, "PredictedDatasets/Buccaneeri1I5.csv");

	}

	public void Optimize(String DataPath , String CSV) throws IllegalArgumentException, IllegalAccessException, IOException, ParseException, CustomException {
		
		new Log().TxtInRectangle("Optimizing classes for classification");
		
		if(isValid(CSV,DataPath)==false)
			System.exit(-1);
		
		for(String Header : Parameters.MeasurementUnitsToPredict.split(",")) {
			double val= BestValueToSpilt(DataPath,CSV, Header);
			Parameters.setClassLevel(Header, val);
			Parameters.setMaxClassLevel(Header, val);
		}
		
	}
	// Dot not pass Header as direct string. Use a variable instead. Very strange error happens because an extra char is added which cause mismatch    
	
	double BestValueToSpilt(String DataPath , String CSV , String Header) throws IllegalArgumentException, IllegalAccessException, IOException, ParseException, CustomException {
		
		double Best=Integer.MAX_VALUE;
		double BestLevel=-1;
		double i=1;
		double increaseBy=1;
		double max=100;
		
		if(Header.contains("R-")) {
			i=0.01;
			increaseBy=0.01;
			max=1;
		}
		for(; i<= max ; i=i+increaseBy) {
		
		Parameters.setClassLevel(Header,i);
		new ClassificationPreparer().Prepare(DataPath, CSV);
		
		Vector<String> headers = new Vector<String>();
		headers.add(Header);
		
		HashMap<String, Vector<HashMap<String, String>>> map=	new CSVReader().ReadIntoHashMapWithFilterdHeaders("ClassificationDatasets/"+new File(CSV).getName(), "PDB",headers);
		
		HashMap<String,Integer> counted = CountInstanceInClasses(map);
		
		//for(String ID : counted.keySet()) {
			//System.out.println("Class "+ ID + " Count "+counted.get(ID));
		//}
		
		
		if(PercentgeOfClassesInEqualSize(counted , counted.keySet().size()) < Best) {
				Best = PercentgeOfClassesInEqualSize(counted,counted.keySet().size());
				BestLevel=Parameters.getClassLevel(Header);
		}
		FileUtils.deleteDirectory(new File("ClassificationDatasets"));
		}
		
		
	return BestLevel;
	}
	
	HashMap<String,Integer> CountInstanceInClasses(HashMap<String, Vector<HashMap<String, String>>> map){
		HashMap<String,Integer> counted = new HashMap<String,Integer>();
		for(String ID : map.keySet()) {
			
			for(int i =0  ;i< map.get(ID).size();++i) {
				
				for(String Header : map.get(ID).get(i).keySet()) {
					
					if(counted.containsKey(map.get(ID).get(i).get(Header))) {
						counted.put(map.get(ID).get(i).get(Header), counted.get(map.get(ID).get(i).get(Header))+1);
					}
					else {
						counted.put(map.get(ID).get(i).get(Header),1);
					}
					
				}
			}
		}
		return counted;
	}
	
	double PercentgeOfClassesInEqualSize(HashMap<String,Integer>  Diff , int NumOfClasses) {
	
		Map<String,Integer> SortedMap = new TreeMap<>(Diff);
		boolean GetFirstClass=false;
		double FirstClass = Collections.max(Diff.values());
		for(String ID : SortedMap.keySet()) {
			if(GetFirstClass==false) {
				GetFirstClass=true;
				FirstClass=SortedMap.get(ID);
				break;
			}
			
		}
		
		
		
		
		//sum the rest of classes 
		int total=0;
		for (String Class : Diff.keySet()) {
			if(FirstClass != Diff.get(Class)) {
				total+=Diff.get(Class);
			}
		}
		
		return Math.abs(FirstClass - total);
		
	}
boolean isValid(String CSV, String PathToDatasets) {
		
		if(!new File(CSV).exists()) {
			new Log().Error(this,"CSV file is not found (Maybe it is wrong directory!)");
           return false;
		}
		if(!new File(PathToDatasets).exists()) {
			new Log().Error(this,"Datasets directory is not found  (Maybe it is wrong directory!)");
          return false;
		}
		
		return true;
	}
}
