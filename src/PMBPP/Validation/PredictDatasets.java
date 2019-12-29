package PMBPP.Validation;

import java.io.File;
import java.io.PrintWriter;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import Comparison.Analyser.ExcelContents;
import Comparison.Analyser.ExcelLoader;
import Comparison.Analyser.ExcelSheet;
import PMBPP.Data.Preparation.PrepareFeatures;
import PMBPP.Log.Log;
import PMBPP.Data.Preparation.PredictionTrainingDataPreparer;
import PMBPP.ML.Model.PMBPP;
import PMBPP.ML.Model.Parameters;
import PMBPP.ML.Model.Predict;
import PMBPP.Utilities.CSVReader;
import PMBPP.Utilities.FilesUtilities;
import me.tongfei.progressbar.ProgressBar;

import java.util.HashMap;
public class PredictDatasets {

	public static void main(String[] args) throws Exception{
		
		String [] arg= {"/Users/emadalharbi/Downloads/TestPreAcc/noncs",new File("/Volumes/PhDHardDrive/EditorsRevision-2/Datasets/NO-NCS").getAbsolutePath()+"/"};
		Parameters.TrainedModelsPath="PredictionModels";
		new PredictDatasets().Predict(arg);
		
		/* MR
		String [] arg= {"/Users/emadalharbi/Downloads/PMBPP/noncsMR2",new File("/Volumes/PhDHardDrive/MRDatasets/DatasetsForBuilding/").getAbsolutePath()+"/"};
		Parameters.TrainedModelsPath="PredictionModels";
		Parameters.Phases="model.HLA,model.HLB,model.HLC,model.HLD";
		Parameters.Featuers="RMSD,Skew,Resolution,Max,Min,SequenceIdentity";
		Parameters.MR="T";
		new PredictDatasets().Predict(arg);
		*/
	}
	
	public  void Predict(String[] args) throws Exception {
		// TODO Auto-generated method stub
		new Log().TxtInRectangle("Predicting datasets");
String PathToExcelFolder=args[0];
String PathToDatasets=args[1];

isValid(PathToExcelFolder,PathToDatasets);
	

for(File Excel : new FilesUtilities().ReadFilesList(PathToExcelFolder)) { //loop on all excel files 
	
	
	String ExcelName=Excel.getName().replaceAll("."+FilenameUtils.getExtension(Excel.getName()),"");
	new Log().Info(this," Predicting "+ExcelName);
	ExcelLoader f = new ExcelLoader();
	Vector<ExcelContents> excel = new Vector<ExcelContents>();
	HashMap<String,HashMap<String,String> > Results = new HashMap<String,HashMap<String,String>>();
	excel =  f.ReadExcel(Excel.getAbsolutePath());
	
	
	
	
	for(int i=0 ; i < excel.size(); ++i) {// read the excel records 
	for(File mtz : new FilesUtilities().ReadFilesList(PathToDatasets)) {// find the mtz 
		String MTZName=mtz.getName().replaceAll("."+FilenameUtils.getExtension(mtz.getName()),"");
		String MTZEx=FilenameUtils.getExtension(mtz.getName());

		if(MTZName.equals(excel.get(i).PDB_ID) && MTZEx.equals("mtz")) {
		String [] arg= {mtz.getAbsolutePath()};
		
		Parameters.Usecfft=true;
		Predict Pre = new Predict();
		Parameters.FilterModels="T";
		Parameters.FilteredModels.add(ExcelName); // remove the others models. Only keep the model that mathc this excel 
		
		Pre.PredictMultipleModles(arg);
		//Pre.Print(Pre.PipelinesPredictions);
		HashMap<String,String> PipelineResults= new HashMap<String,String>();
		for(String Key : Pre.PipelinesPredictions.keySet()) { // save into a map >> R-free, 0.2 and so on 
			//System.out.println(MTZName);
			PipelineResults.put(Key, Pre.PipelinesPredictions.get(Key).get(ExcelName));
		}
		Results.put(excel.get(i).PDB_ID, PipelineResults); // A map {PDB1, {R-free,0.2 }} {PDB2, {R-free,0.2 }}
	}
}
	
	}
	
	
	//write to csv 
	Vector<String> MeasurementUnitsHeaders = new Vector<String> ();
	for(String PDB : Results.keySet()) {
		
		for(String Key : Results.get(PDB).keySet()) { // give each an index R-free index 0 R-work 1 ... etc 
			
			if(!MeasurementUnitsHeaders.contains(Key))
				MeasurementUnitsHeaders.add(Key);
		}
		
		
	}
	String CSV="PDB";
	for(int  i=0; i < MeasurementUnitsHeaders.size() ; ++i)// add all headers 
		CSV+=","+MeasurementUnitsHeaders.get(i);
	CSV+=",Prediction,Pipeline\n";
	
	for(String PDB : Results.keySet()) {
		int HeaderIndex=0;
		
		String Record1=PDB; 
		String Record2=PDB; 
		for(String Key : Results.get(PDB).keySet()) {
			if(MeasurementUnitsHeaders.get(HeaderIndex).equals(Key)) { // check the headers order because hashmaps are unsorted  
					
				Record1+=","+Results.get(PDB).get(Key);	
				HeaderIndex++;
				Vector<ExcelContents> TempExcel = new Vector<ExcelContents>();
				for(int i=0 ; i < excel.size(); ++i) { // write temp excel that only contains  this PDB
					if(excel.get(i).PDB_ID.equals(PDB)) {
						TempExcel.add(excel.get(i));
						break;
					}
				}
				
				PMBPP.CheckDirAndFile("TempExcel");
				new ExcelSheet().FillInExcel(TempExcel, "TempExcel/Temp");
				PMBPP.CheckDirAndFile("TempCSV");
				String [] arg= {"TempExcel",new File(PathToDatasets).getAbsolutePath()+"/","TempCSV"};
				new PredictionTrainingDataPreparer().Prepare(arg); // create a csv that only contains this only PDB in the excel 
				 String Val = new CSVReader().GetRecordByHeaderName("TempCSV/Temp.csv", Key, 0); // now get the value 
				
				Record2+=","+Val;
				FileUtils.deleteDirectory(new File("TempExcel")); 
				FileUtils.deleteDirectory(new File("TempCSV")); 
			}
			else {// very rare to happen 
				new Log().Error(this,"Can not continue because there is a change in the headers order!  ");
			
			
			}
		}
		
		Record1+=",T,"+ExcelName+"\n";
		Record2+=",F,"+ExcelName+"\n";
		
		
		CSV+=Record1;
		CSV+=Record2;
		//pb.step();
	}
	
	if(new File("PredictedDatasets/"+ExcelName+".csv").exists()) {
		new Log().Warning(this,ExcelName+".csv has found and deleted to create a new ");
		FileUtils.deleteQuietly(new File("PredictedDatasets/"+ExcelName+".csv"));
	}
	PMBPP.CheckDirAndFile("PredictedDatasets");
	try(  PrintWriter out = new PrintWriter( "PredictedDatasets/"+ExcelName+".csv")){
	    out.println( CSV );
	}
}
		
	}

void isValid(String files, String PathToDatasets) {
		
		if(!new File(files).exists()) {
			new Log().Error(this,"Excel files are not found (Maybe it is wrong directory!)");
           
		}
		if(!new File(PathToDatasets).exists()) {
			new Log().Error(this,"Datasets directory is not found  (Maybe it is wrong directory!)");
          
		}
		
		
	}
}
