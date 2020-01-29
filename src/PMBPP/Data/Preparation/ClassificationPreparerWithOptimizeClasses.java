package PMBPP.Data.Preparation;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.json.simple.parser.ParseException;

import PMBPP.Log.Log;
import PMBPP.ML.Model.Parameters;

import PMBPP.Utilities.CSVReader;
import PMBPP.Utilities.CSVWriter;
import PMBPP.Utilities.StatisticalTests;
import PMBPP.Validation.CustomException;

public class ClassificationPreparerWithOptimizeClasses {

	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, IOException, ParseException, CustomException {
		// TODO Auto-generated method stub
		Parameters.Log="F";
		Parameters.PearsonsCorrelation="T";
		String DataPath="/Volumes/PhDHardDrive/EditorsRevision-2/Datasets/NO-NCS/";
		Parameters.AttCSV="/Volumes/PhDHardDrive/FinalTraining/Experimental/ParrotPhases/PredictionModels/Completeness/Buccaneeri1I5.csv";
		new ClassificationPreparerWithOptimizeClasses().Optimize(DataPath, "/Volumes/PhDHardDrive/FinalTraining/Experimental/ParrotPhases/PredictedDatasets/Buccaneeri1I5.csv");
		new ClassificationPreparer().Prepare(new File(DataPath).getAbsolutePath()+"/","/Volumes/PhDHardDrive/FinalTraining/Experimental/ParrotPhases/PredictedDatasets/Buccaneeri1I5.csv");

	}

	public void Optimize(String DataPath , String CSV) throws IllegalArgumentException, IllegalAccessException, IOException, ParseException, CustomException {
		
		new Log().TxtInRectangle("Optimizing classes for classification");
		
		isValid(CSV,DataPath);
			
		
		for(String Header : Parameters.MeasurementUnitsToPredict.split(",")) {
			double val= BestValueToSpilt(DataPath,CSV, Header);
			Parameters.setClassLevel(Header, val);
			Parameters.setMaxClassLevel(Header, val);// any class above this value then it will set to this value/class
			System.out.println("Best Class level "+Parameters.getClassLevel(Header));
	
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
		
		HashMap<String, Vector<HashMap<String, String>>> map=	new CSVReader().ReadIntoHashMapWithFilterdHeaders(Parameters.ClassificationDatasetsFolderName+"/"+new File(CSV).getName(), "PDB",headers);
		
		HashMap<String,Integer> counted = CountInstanceInClasses(map);
		
		
		
		if(Parameters.PearsonsCorrelation.equals("F") && Parameters.AnovaForClassification.equals("F"))
		if(PercentgeOfClassesInEqualSize(counted , counted.keySet().size()) < Best) {
				Best = PercentgeOfClassesInEqualSize(counted,counted.keySet().size());
				BestLevel=Parameters.getClassLevel(Header);
		}
		
		if(Parameters.PearsonsCorrelation.equals("T") || Parameters.AnovaForClassification.equals("T"))
		if(CalculateStatisticalTest(counted.keySet().toArray(new String[counted.keySet().size()]),CSV,map,Header)==true) {
			if(PercentgeOfClassesInEqualSize(counted , counted.keySet().size()) < Best) {
					Best = PercentgeOfClassesInEqualSize(counted,counted.keySet().size());
					BestLevel=Parameters.getClassLevel(Header);
			}
		}
		
		FileUtils.deleteDirectory(new File(Parameters.ClassificationDatasetsFolderName));
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
	
		Map<String,Integer> SortedMap = new TreeMap<>(new SortedByIntKeys());
		
		for(String ID : Diff.keySet()) { // to sort the map
			SortedMap.put(ID, Diff.get(ID));
		}
		
		
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
void isValid(String CSV, String PathToDatasets) {
		
		if(!new File(CSV).exists()) {
			new Log().Error(this,"CSV file is not found (Maybe it is wrong directory!)");
           
		}
		if(!new File(PathToDatasets).exists()) {
			new Log().Error(this,"Datasets directory is not found  (Maybe it is wrong directory!)");
          
		}
		
		
	}

boolean CalculateStatisticalTest(String [] Calsses, String PredcitedDatasetsCSV,HashMap<String, Vector<HashMap<String, String>>> ClassifedDatasetsForTraning, String Header ) throws IOException {
	TreeMap<String,Vector<String>> GroupedPDBbyClass= new TreeMap<String,Vector<String>>(new SortedByIntKeys());
	
	// Getting PDB and grouped them depending on their classes 
	for(int i=0 ; i < Calsses.length;++i) {
		Vector<String> Temp= new Vector<String>();
		for(String PDB : ClassifedDatasetsForTraning.keySet()) {
			if(ClassifedDatasetsForTraning.get(PDB).get(0).get(Header).equals(Calsses[i])) { // we use get(0) because there is the  PDB IF here are unique (no multiple records with same PDB ID) 
				Temp.add(PDB);
			}
		}
		GroupedPDBbyClass.put(Calsses[i], Temp);
		
	}
	
	
	//Getting predicted and actual values 
	Vector<String> HeadersWanted= new Vector<String>();
	HeadersWanted.add(Header);
	HeadersWanted.add("Prediction");
	HashMap<String, Vector<HashMap<String,String>>> Prediction = new CSVReader().ReadIntoHashMapWithFilterdHeaders(PredcitedDatasetsCSV, "PDB", HeadersWanted);

	Vector<String> Actual = new Vector<String>();
	Vector<String> Predicted = new Vector<String>();
	
	for(String PDB: GroupedPDBbyClass.get(GroupedPDBbyClass.firstKey())) {
		Vector<HashMap<String,String>> ActualAndPredicted=Prediction.get(PDB); // Two vectors one contains actual and the other contains Predicted value
		
		if(ActualAndPredicted.get(0).get("Prediction").equals("T"))
			Predicted.add(ActualAndPredicted.get(0).get(Header));
		if(ActualAndPredicted.get(1).get("Prediction").equals("F"))
			Actual.add(ActualAndPredicted.get(1).get(Header));
	}
	if(Actual.size()<2 || Predicted.size()<2) // can not calculate PC
		return false;
	
	double ClassOnePC=new StatisticalTests().PC(Actual,Predicted);
	Vector<Double> TempActual = new Vector<Double>();
	Vector<Double> TempPredicted = new Vector<Double>();
	for(String s : Actual)
		TempActual.add(Double.parseDouble(s));
	for(String s : Predicted)
		TempPredicted.add(Double.parseDouble(s));
	
	double anovaForForstClass=Double.parseDouble(new StatisticalTests().anova(TempActual, TempPredicted));
	Actual.clear();
	Predicted.clear();
	GroupedPDBbyClass.remove(GroupedPDBbyClass.firstKey()); // remove first class we do not need it
	
	
	for(String Class: GroupedPDBbyClass.keySet()) {
		for(String PDB:GroupedPDBbyClass.get(Class)) {
			Vector<HashMap<String,String>> ActualAndPredicted=Prediction.get(PDB);
			if(ActualAndPredicted.get(0).get("Prediction").equals("T"))
				Predicted.add(ActualAndPredicted.get(0).get(Header));
			if(ActualAndPredicted.get(1).get("Prediction").equals("F"))
				Actual.add(ActualAndPredicted.get(1).get(Header));
		}
	}
	if(Actual.size()<2 || Predicted.size()<2) // can not calculate PC  
		return false;
	
	double RestofClassesPC=new StatisticalTests().PC(Actual,Predicted);
	
	
	TempActual.clear();
	TempPredicted.clear();
	for(String s : Actual)
		TempActual.add(Double.parseDouble(s));
	for(String s : Predicted)
		TempPredicted.add(Double.parseDouble(s));
	
	double anovaForRestClasses=Double.parseDouble(new StatisticalTests().anova(TempActual, TempPredicted));
	
	if(Parameters.AnovaForClassification.equals("T")) {
	if(anovaForForstClass > 0.05 && anovaForRestClasses <=0.05)
	return true;
	else
	return false;
	}
	
	// Or PC
	if((((ClassOnePC-RestofClassesPC)*100)/ClassOnePC) >=5)
	//if(Math.abs(ClassOnePC-RestofClassesPC) >= 0.05)
		return true;
	else
		return false;
	
	
	
}

class SortedByIntKeys implements Comparator<String>
{
    public int compare(String o1,String o2)
    {
        o1=o1.replace("±", "");
        o2=o2.replace("±", "");
        BigDecimal v1= new BigDecimal(o1);
        BigDecimal v2= new BigDecimal(o2);
       
        return  v1.compareTo(v2);
    }
}


}
