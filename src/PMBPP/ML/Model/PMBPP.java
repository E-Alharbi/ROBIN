package PMBPP.ML.Model;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.json.simple.parser.ParseException;

import Comparison.Runner.RunningParameter;
import PMBPP.Data.Preparation.ClassificationPreparer;
import PMBPP.Data.Preparation.ClassificationPreparerWithOptimizeClasses;
import PMBPP.Data.Preparation.GetFeatures;
import PMBPP.Data.Preparation.PrepareFeatures;
import PMBPP.Log.Log;
import PMBPP.Prediction.Analysis.NumberOfTreesImpact;
import PMBPP.Prediction.Analysis.ModelPerformance;
import PMBPP.Updater.Update;
import PMBPP.Data.Preparation.PredictionTrainingDataPreparer;
import PMBPP.Utilities.Cluster;
import PMBPP.Utilities.FilesUtilities;
import PMBPP.Validation.ClassifyDatasets;
import PMBPP.Validation.PredictDatasets;

public class PMBPP {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
	
		/*
		 * To build the project in Eclipse 
		 * 1- right click on the project > maven > build maven 
		 * 2- A runnable JAR will be built in target folder 
		 * 3- The build number will be increased in every time the project is built Ex PMBPP-Runnable-1.0.(10) is the built number     
		 */
		new Log().PseudoText("PMBPP");
		new Log().TxtInRectangle("Parameters from commnad line");
		
		if(System.getenv("CCP4")==null) {
			new Log().Error(new PMBPP(),"Error: CCP4 environment variables not set. Set up the environment variables by using CCP4  ccp4.setup-sh (Ex source ccp4.setup-sh)");
		
		}
		
		 Vector <String> Parm = new Vector<String>();
		 for (int i=0 ; i < args.length ; ++i){
			 
			 if(args[i].contains("=")){
				 Parm.addAll(Arrays.asList(args[i].split("=")));
				 
			 }
			 
		 }
		 
		 new PMBPP().SetParameters(Parm);
		 
		 if(args[0].equals("PrepareDataForPredictionModels")){
			
			new  PMBPP().PrepareDataForPredictionModels(Parm);
			
		 }
		 
		 if(args[0].equals("PredictionModels")){
				
			 new  PMBPP().PredictionModels(Parm);
			 }
		 
		 if(args[0].equals("Predict")){
			
			 if(new File("features.csv").exists())
				 FileUtils.deleteQuietly(new File("features.csv")); // if not removed, then will cause to read the features from csv
			
			 
			
			    //saving to Models folder are done from the Predictor
				if(checkArg(Parm,"mtz")!=null  && checkArg(Parm,"TrainedModelsPath")!=null && checkArg(Parm,"Phases")!=null) {
					String [] arg= {checkArg(Parm,"mtz")};
                    Predict Pre = new Predict();
					if(!new File(Parameters.TrainedModelsPath).exists()) {
						new Log().Error(new PMBPP(),"Models folder not found!");
						
					}
					
					Pre.PredictMultipleModles(arg);
					Pre.Print(Pre.PipelinesPredictions);
					
				}
				if(checkArg(Parm,"mtz")!=null && checkArg(Parm,"TrainedModelsPath")==null && checkArg(Parm,"Phases")!=null) {
					
					Parameters.TrainedModelsPath="PredictionModels";
					Parameters.CompressedModelFolderName="PredictionModels.zip";
					
					if(Parameters.MR.equals("T")) {
						Parameters.TrainedModelsPath="PredictionModelsMR";
					   Parameters.CompressedModelFolderName="PredictionModelsMR.zip";
					}
					String [] arg= {checkArg(Parm,"mtz")};
					Predict Pre = new Predict();
                   
					System.out.println("Predictions ");
					Pre.PredictMultipleModles(arg);
					Pre.Print(Pre.PipelinesPredictions);
					
					Parameters.CompressedModelFolderName="ClassificationModels.zip";
					Parameters.TrainedModelsPath="ClassificationModels";
					if(Parameters.MR.equals("T")) {
					Parameters.TrainedModelsPath="ClassificationModelsMR";
					Parameters.CompressedModelFolderName="ClassificationModelsMR.zip";
					}
					Parameters.Usecfft=false;
					
					System.out.println("Predictions confidence:");
					Pre.PredictMultipleModles(arg);
					Pre.Print(Pre.PipelinesPredictions);
					
					
				}
			 }
		 
		 
		 if(args[0].equals("PrepareDataForClassification")){
				
			 new PMBPP().PrepareDataForClassification(Parm);
			
		 }
		 
		 if(args[0].equals("FullTraining")){
			
			 if(Parameters.Cluster.equals("T") && new FilesUtilities().ReadFilesList(checkArg(Parm,"ExcelFolder")).length>1) {
				 new Cluster().Jobs();
				 System.exit(-1);
			 }
			 
			new  PMBPP().PrepareDataForPredictionModels(Parm);
			Vector <String> Temp = new   Vector <String>(Parm);
			Parm.clear();
			Parm.add("CSV");// keyword
			Parm.add("CSV");// Path
			new  PMBPP().PredictionModels(Parm);
			Parm.clear();
			new  PMBPP().PrepareDataForClassification(Temp); // Temp = ExcelFolder and Datasets
			new ModelPerformance().SplitOnMeasurementUnitsLevel("CSVToUseInStatisticalTest");
			new ModelPerformance().OmitTrainingdata("CSVToUseInStatisticalTestSplitted","TrainAndTestDataPredictionModels");
			new ModelPerformance().GroupedByResolution("CSVToUseInStatisticalTestFiltered");
			new NumberOfTreesImpact().NumberOfTreesTable("PredictionModelsPerformance.xml");
			
			Parameters.TrainedModelsPath="ClassificationModels";
			new ClassifyDatasets().Classify("CSVToUseInStatisticalTest",new File(checkArg(Temp,"Datasets")).getAbsolutePath());
			new ClassifyDatasets().OmitTrainingData("ClassifedDatasets","TrainAndTestDataClassificationModels");
		 }
		 
		 
		 //Checking for updates 
		 new Update().IsUpdateRequired();
	}
	static String checkArg(Vector<String> Args, String Keyword ){
		for (int i=0 ; i< Args.size() ; ++i){
			if(Args.get(i).equals(Keyword)) {
				 new Log().Info(new  PMBPP(), "Parameter: "+Keyword+" Value: "+Args.get(i+1));
				return Args.get(i+1);
			}
		}
		return null;
		
	}
	static public boolean CheckDirAndFile(String Path){
		
		   try {
			File directory = new File(Path);
			    if (! directory.exists()){
			        directory.mkdir();
			    }
			    return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			new Log().Error(new PMBPP(), "Error: Unable to create "+Path);
			
			
			return false;
			
		}
		   
	}
	
	void PrepareDataForPredictionModels(Vector <String> Parm) throws IllegalArgumentException, IllegalAccessException, IOException, InvocationTargetException, NoSuchMethodException, ParseException {
		 
			if(checkArg(Parm,"ExcelFolder")!=null && checkArg(Parm,"Datasets")!=null ) {
				
				String [] arg= {checkArg(Parm,"ExcelFolder"),new File(checkArg(Parm,"Datasets")).getAbsolutePath()+"/","CSV"};
				
				if(new File("CSV").exists()) {
					 new Log().Warning(this, " CSV has deleted to create new folder");
					 FileUtils.deleteDirectory(new File("CSV"));
				 }
				
				new PrepareFeatures().Prepare(new File(checkArg(Parm,"Datasets")).getAbsolutePath());
				new PredictionTrainingDataPreparer().Prepare(arg);
				
			}
			else {
				System.out.println("One of the required parameters is missing ");
				System.out.println("ExcelFolder= Path to a folder contains pipelines results.  ");
				System.out.println("Datasets= Path to a folder contains the mtz files.  ");
			}
	}
	
	void PredictionModels(Vector <String> Parm) throws Exception {
		//saving to Models folder are done from the Predictor
		if(checkArg(Parm,"CSV")!=null ) {
			
			String [] arg= {checkArg(Parm,"CSV")};
			Parameters.ModelFolderName="PredictionModels";
			new CreateModels().Models(arg);
			
		}
		else {
			System.out.println("One of the required parameters is missing ");
			System.out.println("CSV= Path to a folder contains csv files that created by PrepareDataForPredictionModels");
			
		}
	}
	
	void PrepareDataForClassification(Vector <String> Parm) throws Exception {
		 Parameters.TrainedModelsPath="PredictionModels";
			if(checkArg(Parm,"ExcelFolder")!=null && checkArg(Parm,"Datasets")!=null ) {
				
				
				String [] arg= {checkArg(Parm,"ExcelFolder"),new File(checkArg(Parm,"Datasets")).getAbsolutePath()+"/","CSV"};
				new PredictDatasets().Predict(arg);
				
				
				
				for(File csv : new FilesUtilities().ReadFilesList("PredictedDatasets")) {
					
					//String [] arg2= {new File(checkArg(Parm,"Datasets")).getAbsolutePath()+"/",csv.getAbsolutePath()};
					
					if(Parameters.OptimizeClasses.equals("T")) {
						Parameters.ClassificationDatasetsFolderName="ClassificationDatasetsToOptimize";
					new ClassificationPreparerWithOptimizeClasses().Optimize(new File(checkArg(Parm,"Datasets")).getAbsolutePath()+"/", csv.getAbsolutePath());
					}
					Parameters.ClassificationDatasetsFolderName="ClassificationDatasets";
					new ClassificationPreparer().Prepare(new File(checkArg(Parm,"Datasets")).getAbsolutePath()+"/",csv.getAbsolutePath());
					
				}
				
				//new PMBPP().RemoveDSStoreFileForMac("ClassificationDatasets");
				String [] arg2= {new File(Parameters.ClassificationDatasetsFolderName).getAbsolutePath()};
				Parameters.ModelFolderName="ClassificationModels";
				new CreateModels().Models(arg2);
				
				
				
				
			}
			else {
				System.out.println("One of the required parameters is missing ");
				System.out.println("ExcelFolder= Path to a folder contains pipelines results.  ");
				System.out.println("Datasets= Path to a folder contains the mtz files.  ");
			}
	}
	
	void SetParameters(Vector <String> Parm) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		 
		
		for (Method setter : Parameters.class.getDeclaredMethods()) {
			 String setterName=setter.getName().replace("set", "");
			 if(Parm.contains(setterName)) {
				 
				
				 setter.invoke(this, checkArg(Parm,setterName));
				 
			 }
			 
		 }
		 Parameters.CheckDependency();
		 
		 
	}
}
