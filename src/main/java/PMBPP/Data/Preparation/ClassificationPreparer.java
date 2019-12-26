package PMBPP.Data.Preparation;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.simple.parser.ParseException;

import PMBPP.Log.Log;
import PMBPP.ML.Model.PMBPP;
import PMBPP.ML.Model.Parameters;
import PMBPP.Utilities.CSVReader;
import PMBPP.Utilities.CSVWriter;
import PMBPP.Utilities.FilesUtilities;
import PMBPP.Validation.CustomException;

public class ClassificationPreparer {
	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, IOException, ParseException, CustomException {
		String DataPath="/Volumes/PhDHardDrive/EditorsRevision-2/Datasets/NO-NCS";
		String CSVFile="/Users/emadalharbi/Downloads/PredictedDatasets/";
		//String [] arg2= {DataPath,CSVFile};
		//new ClassificationPreparer().Prepare(arg2);
		//Parameters.Phases="model.HLA,model.HLB,model.HLC,model.HLD";
		//Parameters.Featuers="RMSD,Skew,Resolution,Max,Min,SequenceIdentity";
		//Parameters.MR="T";
		for(File csv : new FilesUtilities().ReadFilesList(CSVFile)) {
			
			//String [] arg2= {new File(DataPath).getAbsolutePath()+"/",csv.getAbsolutePath()};
			Parameters.AttCSV="/Users/emadalharbi/Downloads/TestPreAcc/PredictionModels/R-free/Buccaneeri1I5.csv";
			new ClassificationPreparer().Prepare(new File(DataPath).getAbsolutePath()+"/",csv.getAbsolutePath());
			
		}
	}
	public  void Prepare(String DataPath, String CSVFile ) throws IOException, IllegalArgumentException, IllegalAccessException, ParseException, CustomException {
		// TODO Auto-generated method stub
		//Do not assign the value directly to Parameters.ClassLevelForRFactors
		
		new Log().TxtInRectangle("Classification data preparer");
		HashMap<String, Vector<HashMap<String,String>>> csv = new CSVReader().ReadIntoHashMap(CSVFile, "PDB");
		HashMap<String,LinkedHashMap<String,String>> CSVContents= new HashMap<String,LinkedHashMap<String,String>>();

		for(String CSVID : csv.keySet()) { // id in csv. For ex PDB code
		
		HashMap<String,String> Class =  new HashMap<String,String>();
		
		for(HashMap<String,String> map : csv.get(CSVID)) {
			for(String Key2 : map.keySet()) {
				
				
				if (NumberUtils.isParsable(map.get(Key2)))// we do want the column that contains string 
				if(Class.containsKey(Key2)) {
					
					double Val=Double.parseDouble(Class.get(Key2));
			 		double Diff= Math.abs(Val - Double.parseDouble(map.get(Key2)));
			 		
			 		
			 		
			 		String  group="none";
			 		if(Key2.contains("R-")) {
			 			double ClassLevelForRwork=1/Parameters.getClassLevel(Key2);
				 		double rounded = Math.round(Diff * ClassLevelForRwork) / ClassLevelForRwork;
				 		
				 		group=String.valueOf(rounded);
				 		

			 		}
			 			else {
			 		
			 			group=String.valueOf(Parameters.ClassLevelForCompleteness*(Math.round(Diff/Parameters.ClassLevelForCompleteness)));
			 			
			 			
			 			
			 			}
			 		if(Parameters.getMaxClassLevel(Key2)!=-1)
				 		if(Double.parseDouble(group) > Parameters.getMaxClassLevel(Key2)) {
				 			group=String.valueOf(Parameters.getMaxClassLevel(Key2));
				 		}
				 		
				 		if(Parameters.getMinClassLevel(Key2)!=-1)
					 		if(Double.parseDouble(group) < Parameters.getMinClassLevel(Key2)) {
					 			group=String.valueOf(Parameters.getMinClassLevel(Key2));
					 		}
			 		
			 		Class.put(Key2, "±"+String.valueOf(group));
					
				}
				else {
					Class.put(Key2, map.get(Key2));
					
					
				}
			}
			
			
		}
		
		LinkedHashMap<String,String> SortedMap = new GetFeatures().GetUsingFeaturesInHashMap(DataPath+CSVID+".mtz");
		SortedMap.putAll(Class);
		CSVContents.put(CSVID, SortedMap);
		
	}
	
	PMBPP.CheckDirAndFile("ClassificationDatasets");
	new CSVWriter().WriteFromHashMap(CSVContents,"ClassificationDatasets/"+new File(CSVFile).getName().replaceAll("."+FilenameUtils.getExtension(new File(CSVFile).getName()),"")+".csv");
	}

}
