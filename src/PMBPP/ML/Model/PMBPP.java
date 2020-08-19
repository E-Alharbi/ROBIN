package PMBPP.ML.Model;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.json.simple.parser.ParseException;

import PMBPP.Data.Preparation.ClassificationPreparer;
import PMBPP.Data.Preparation.ClassificationPreparerWithOptimizeClasses;
import PMBPP.Data.Preparation.PredictionTrainingDataPreparer;
import PMBPP.Data.Preparation.PrepareFeatures;
import PMBPP.Log.Log;
import PMBPP.Mining.Paper.MiningResearchPaper;
import PMBPP.Prediction.Analysis.ModelPerformance;
import PMBPP.Prediction.Analysis.NumberOfTreesImpact;
import PMBPP.Updater.Update;
import PMBPP.Utilities.Cluster;
import PMBPP.Utilities.ExecutionTime;
import PMBPP.Utilities.FilesUtilities;
import PMBPP.Utilities.UncompressMLModel;
import PMBPP.Validation.ClassifyDatasets;
import PMBPP.Validation.PredictDatasets;

public class PMBPP {
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
	
		      
		      
		   
		/*
		 * To build the project in Eclipse 1- right click on the project > maven > build
		 * maven 2- A runnable JAR will be built in target folder 3- The build number
		 * will be increased in every time the project is built Ex
		 * PMBPP-Runnable-1.0.(10) is the built number
		 */
		new Log().PseudoText("PMBPP");

		

		Vector<String> Parm = new Vector<String>();
		for (int i = 0; i < args.length; ++i) {

			if (args[i].contains("=")) {
				Parm.addAll(Arrays.asList(args[i].split("=")));
			}

		}

		new PMBPP().SetParameters(Parm);
		if (System.getenv("CCP4") == null && Parameters.getIgnoreCCP4Env().equals("F")) {
			new Log().Error(new PMBPP(),
					" CCP4 environment variables not set. Set up the environment variables by using CCP4  ccp4.setup-sh (Ex source ccp4.setup-sh)");

		}
		
		if (args[0].equals("PredictDatasets")) {

			SetTrainedModelPathToDef();
			if(checkArg(Parm, "Datasets") != null) {
				Instant start = Instant.now();
				new PredictDatasets().Predict(checkArg(Parm, "Datasets"));
				Instant finish = Instant.now();
				long timeElapsed = Duration.between(start, finish).toMillis();

				new Log().Info(new PMBPP(), "Execution time " + (timeElapsed / 1000) + " seconds");
				new Log().Info(new PMBPP(), "The results were save to PredictedDatasets.csv. For individual pipeline results were saved in PredictedDatasets folder");

			}
			
			else
			new Log().Info(new PMBPP(), "No datasets are found!. Please use this keyword to set the datasets path Datasets=PathToDatasets/");	
		}
		
		if (args[0].equals("UncompressMLModel")) {
			new UncompressMLModel().Uncompress();
		}
		
		
		if (args[0].equals("MiningResearchPaper")) {
if(checkArg(Parm, "CSV") != null)
			new MiningResearchPaper().Fetch(checkArg(Parm, "CSV"));
else
	new Log().Error(new PMBPP(),"Please set the path for the CSV file. Ex CSV=CSVToUseInStatisticalTestFiltered/ARPwARP-Completeness.csv OR CSV=CSVToUseInStatisticalTest/ARPwARP.csv");

		}
		
		if (args[0].equals("PrepareDataForPredictionModels")) {

			new PMBPP().PrepareDataForPredictionModels(Parm);

		}

		if (args[0].equals("PredictionModels")) {

			new PMBPP().PredictionModels(Parm);
		}

		if (args[0].equals("Predict")) {
			Instant start = Instant.now();
			
			
			if (new File("features.csv").exists())
				FileUtils.deleteQuietly(new File("features.csv")); // if not removed, then will cause to read the
																	// features from csv

			if( Parameters.getRMSD()!=-1 || Parameters.getResolution()!=-1 || Parameters.getMax()!=-1 || Parameters.getMin()!=-1 || Parameters.getSkew()!=-1) {
              if(Parameters.getMR().equals("T")) {
            	  double[] instanceValue1=new double[6];
            	  instanceValue1=new double[6];
            	  instanceValue1[5]=Double.parseDouble(Parameters.getSequenceIdentity());
            	  Parameters.setInstanceValue1(instanceValue1);
				}
              else {
            	  
            	  double[] instanceValue1=new double[5];
            	  Parameters.setInstanceValue1(instanceValue1);
              }
				String [] Features=Parameters.getFeatures().split(",");
				for(int f=0 ;f < Features.length;++f) {
					Parameters.getInstanceValue1()[f]=Double.parseDouble(Parameters.class.getMethod("get"+Features[f], null).invoke(null, null).toString());
				}
				Parameters.setUsecfft(false);
				String[] arg = { "" };
				Predict Pre = new Predict();
				Pre.PredictMultipleModles(arg);
				Pre.Print(Pre.PipelinesPredictions);
			}
			
			// saving to Models folder is done from the Predictor
			else if (checkArg(Parm, "mtz") != null && checkArg(Parm, "TrainedModelsPath") != null
					&& checkArg(Parm, "Phases") != null) {
				String[] arg = { checkArg(Parm, "mtz") };
				Predict Pre = new Predict();
				if (!new File(Parameters.getTrainedModelsPath()).exists()) {
					new Log().Error(new PMBPP(), "Models folder not found!");

				}

				Pre.PredictMultipleModles(arg);
				Pre.Print(Pre.PipelinesPredictions);

			} else if (checkArg(Parm, "mtz") != null && checkArg(Parm, "TrainedModelsPath") == null
					&& checkArg(Parm, "Phases") != null) {

				
				
				SetTrainedModelPathToDef();
				String[] arg = { checkArg(Parm, "mtz") };
				Predict Pre = new Predict();

				Pre.PredictMultipleModles(arg);

				new Log().Info(new PMBPP(), "Predictions");
				Pre.Print(Pre.PipelinesPredictions);
if(Parameters.getPredictionConfidence().equals("T")) {
	

				Parameters.setCompressedModelFolderName ( "ClassificationModels.zip");
				Parameters.setTrainedModelsPath ( "ClassificationModels");
				if (Parameters.getMR().equals("T")) {
					Parameters.setTrainedModelsPath ( "ClassificationModelsMR");
					Parameters.setCompressedModelFolderName ( "ClassificationModelsMR.zip");
				}
				Parameters.setUsecfft ( false);

				Pre.PredictMultipleModles(arg);
				new Log().Info(new PMBPP(), "Predictions confidence");
				Pre.Print(Pre.PipelinesPredictions);
}
			} else {
				new Log().Info(new PMBPP(),
						"Please type in the phases.  \n Examples: \n -To start from experimental phasing \n java -jar PMBPP-Runnable-(version).jar Predict mtz=1o6a.mtz Phases=HLA,HLB,HLC,HLD Colinfo=FP,SIGFP \n - For MR  \n java -jar PMBPP-Runnable-(version).jar Predict mtz=1o6a.mtz Phases=HLA,HLB,HLC,HLD Colinfo=FP,SIGFP MR=T SequenceIdentity=0.85 \n");
			}
			Instant finish = Instant.now();
			long timeElapsed = Duration.between(start, finish).toMillis();

			new Log().Info(new PMBPP(), "Execution time " + (timeElapsed / 1000) + " seconds");
		}

		if (args[0].equals("PrepareDataForClassification")) {

			new PMBPP().PrepareDataForClassification(Parm);

		}

		if (args[0].equals("FullTraining")) {
			Parameters.setAllExcelFolder(checkArg(Parm, "ExcelFolder"));
			if(Parameters.getPipelines().size()!=0) {
				new PMBPP().CheckDirAndFile("ExcelToUseInTraining");
				
				for(File CopyThis : new File(checkArg(Parm, "ExcelFolder")).listFiles()) {

					if(Parameters.getPipelines().contains(CopyThis.getName().replaceAll( "."+FilenameUtils.getExtension(CopyThis.getName()), "")))
					FileUtils.copyFileToDirectory(CopyThis, new File("ExcelToUseInTraining"));
					
				}
				
				Parm=SetArg(Parm,"ExcelFolder",new File("ExcelToUseInTraining").getAbsolutePath());
				
			}
			Parameters.setExcelFolder(checkArg(Parm, "ExcelFolder"));
			if (Parameters.getCluster().equals("T")
					&& new FilesUtilities().ReadFilesList(checkArg(Parm, "ExcelFolder")).length > 1) {
				new Cluster().Jobs();
				System.exit(-1);
			}

			new PMBPP().PrepareDataForPredictionModels(Parm);
			Vector<String> Temp = new Vector<String>(Parm);
			Parm.clear();
			Parm.add("CSV");// keyword
			Parm.add("CSV");// Path
			new PMBPP().PredictionModels(Parm);
			Parm.clear();
			
			Parameters.setTrainedModelsPath ( "PredictionModels");
			
			//String[] arg = { checkArg(Temp, "ExcelFolder"),
			//		new File(checkArg(Temp, "Datasets")).getAbsolutePath() + "/", "CSV" };
			new PredictDatasets().Predict(checkArg(Temp, "ExcelFolder"),new File(checkArg(Temp, "Datasets")).getAbsolutePath() + "/",true);
			
			
			if(Parameters.getPredictionConfidence().equals("T")) {
				new PMBPP().PrepareDataForClassification(Temp); // Temp = ExcelFolder and Datasets

			}
			new ModelPerformance().SplitOnMeasurementUnitsLevel("CSVToUseInStatisticalTest");
			new ModelPerformance().OmitTrainingdata("CSVToUseInStatisticalTestSplitted",
					"TrainAndTestDataPredictionModels");
			new ModelPerformance().GroupedByResolution("CSVToUseInStatisticalTestFiltered");
			
			Vector<String> EvaluationMatrices = new Vector<String>();
			EvaluationMatrices.add("rootMeanSquaredError");
			EvaluationMatrices.add("meanAbsoluteError");
			new NumberOfTreesImpact().NumberOfTreesTable("PredictionModelsPerformance.xml",EvaluationMatrices);

			
			EvaluationMatrices = new Vector<String>();
			EvaluationMatrices.add("weightedPrecision");
			EvaluationMatrices.add("weightedRecall");
			EvaluationMatrices.add("weightedFMeasure");
			
			if(new File("ClassificationModelsPerformance.xml").exists()) { // if not exists, means PredictionConfidence set to F 
			new NumberOfTreesImpact().NumberOfTreesTable("ClassificationModelsPerformance.xml",EvaluationMatrices);

			Parameters.setTrainedModelsPath ( "ClassificationModels");
			new ClassifyDatasets().Classify("CSVToUseInStatisticalTest",
					new File(checkArg(Temp, "Datasets")).getAbsolutePath());
			new ClassifyDatasets().OmitTrainingData("ClassifedDatasets", "TrainAndTestDataClassificationModels");
			}
			
			new ExecutionTime().AllWithRecommended();
		}

		// Checking for updates
		if(Parameters.getUpdateChecking().equals("T"))
		new Update().IsUpdateRequired();
	}

	static String checkArg(Vector<String> Args, String Keyword) {
		for (int i = 0; i < Args.size(); ++i) {
			if (Args.get(i).equals(Keyword)) {
				new Log().Info(new PMBPP(), "Parameter: " + Keyword + " Value: " + Args.get(i + 1));
				return Args.get(i + 1);
			}
		}
		return null;

	}
	static Vector<String> SetArg(Vector<String> Args, String Keyword, String NewVal) {
		for (int i = 0; i < Args.size(); ++i) {
			if (Args.get(i).equals(Keyword)) {
				new Log().Info(new PMBPP(), "Parameter:  " + Keyword + " Updated value: " + NewVal);

				Args.set(i + 1, NewVal)	;
			}
		}
		
return Args;
	}

	static public boolean CheckDirAndFile(String Path) {

		try {
			File directory = new File(Path);
			if (!directory.exists()) {
				directory.mkdir();
			}
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			new Log().Error(new PMBPP(), "Error: Unable to create " + Path);

			return false;

		}

	}

	void PrepareDataForPredictionModels(Vector<String> Parm) throws IllegalArgumentException, IllegalAccessException,
			IOException, InvocationTargetException, NoSuchMethodException, ParseException {

		if (checkArg(Parm, "ExcelFolder") != null && checkArg(Parm, "Datasets") != null) {

			String[] arg = { checkArg(Parm, "ExcelFolder"),
					new File(checkArg(Parm, "Datasets")).getAbsolutePath() + "/", "CSV" };

			if (new File("CSV").exists()) {
				new Log().Warning(this, " CSV has deleted to create new folder");
				FileUtils.deleteDirectory(new File("CSV"));
			}
if(Parameters.getPrepareFeatures().equals("T")) {
	new PrepareFeatures().Prepare(new File(checkArg(Parm, "Datasets")).getAbsolutePath());

}
			new PredictionTrainingDataPreparer().Prepare(arg);

		} else {
			System.out.println("One of the required parameters is missing ");
			System.out.println("ExcelFolder= Path to a folder contains pipelines results.  ");
			System.out.println("Datasets= Path to a folder contains the mtz files.  ");
		}
	}

	void PredictionModels(Vector<String> Parm) throws Exception {
		// saving to Models folder are done from the Predictor
		if (checkArg(Parm, "CSV") != null) {

			String[] arg = { checkArg(Parm, "CSV") };
			Parameters.setModelFolderName ( "PredictionModels");
			new CreateModels().Models(arg);

		} else {
			System.out.println("One of the required parameters is missing ");
			System.out
					.println("CSV= Path to a folder contains csv files that created by PrepareDataForPredictionModels");

		}
	}

	void PrepareDataForClassification(Vector<String> Parm) throws Exception {
		Parameters.setTrainedModelsPath ( "PredictionModels");
		if (checkArg(Parm, "ExcelFolder") != null && checkArg(Parm, "Datasets") != null) {

			

			for (File csv : new FilesUtilities().ReadFilesList("PredictedDatasets")) {

				
				if (Parameters.getOptimizeClasses().equals("T")) {
					Parameters.setClassificationDatasetsFolderName ("ClassificationDatasetsToOptimize");
					new ClassificationPreparerWithOptimizeClasses().Optimize(
							new File(checkArg(Parm, "Datasets")).getAbsolutePath() + "/", csv.getAbsolutePath());
				}
				Parameters.setClassificationDatasetsFolderName ( "ClassificationDatasets");
				new ClassificationPreparer().Prepare(new File(checkArg(Parm, "Datasets")).getAbsolutePath() + "/",
						csv.getAbsolutePath());

			}

			
			String[] arg2 = { new File(Parameters.getClassificationDatasetsFolderName()).getAbsolutePath() };
			Parameters.setModelFolderName ("ClassificationModels");
			new CreateModels().Models(arg2);

		} else {
			System.out.println("One of the required parameters is missing ");
			System.out.println("ExcelFolder= Path to a folder contains pipelines results.  ");
			System.out.println("Datasets= Path to a folder contains the mtz files.  ");
		}
	}

	void SetParameters(Vector<String> Parm)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Vector<String> UsedParm= new Vector<String>();
		Vector<String> SetterMethods= new Vector<String>();
		for (Method setter : Parameters.class.getDeclaredMethods()) {
			String setterName = setter.getName().replace("set", "");
			if (Parm.contains(setterName)) {
				
				setter.invoke(this, checkArg(Parm, setterName));
				UsedParm.add(setterName);
			}
			SetterMethods.add(setterName);

		}
		Parameters.CheckDependency();
		if(Parameters.getPrintParameters().equals("T")) {
		String ParaLog="These are the parameters with no arguments in thier getter method. \n";
		for (Method getter : Parameters.class.getDeclaredMethods()) {
			if(getter.getParameterCount()==0) {
				ParaLog+=getter.getName().replace("get", "")+":"+ getter.invoke(this)+"\n";
			}
			
		}
		new Log().Info(this, ParaLog);
		}
		
		
		
		//For debugging
		/*
		for (Field field : Parameters.class.getDeclaredFields()) {
			if(SetterMethods.contains(field.getName())==false)
				new Log().Warning(new PMBPP(), "This field "+field.getName()+" does not have a set method! It will be ignored");
		}
		*/
		
	}
	
	static void SetTrainedModelPathToDef() {
		Parameters.setTrainedModelsPath ( "PredictionModels");
		Parameters.setCompressedModelFolderName ("PredictionModels.zip");

		if (Parameters.getMR().equals("T")) {
			Parameters.setTrainedModelsPath ( "PredictionModelsMR");
			Parameters.setCompressedModelFolderName ( "PredictionModelsMR.zip");
		}
	}
}
