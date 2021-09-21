package PMBPP.Validation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import Comparison.Analyser.ExcelContents;
import Comparison.Analyser.ExcelLoader;
import Comparison.Analyser.ExcelSheet;
import PMBPP.Data.Preparation.Features;
import PMBPP.Data.Preparation.PredictionTrainingDataPreparer;
import PMBPP.Data.Preparation.PrepareFeatures;
import PMBPP.Log.Log;
import PMBPP.ML.Model.PMBPP;
import PMBPP.ML.Model.Parameters;
import PMBPP.ML.Model.Predict;
import PMBPP.Utilities.CSVReader;
import PMBPP.Utilities.CSVWriter;
import PMBPP.Utilities.FilesUtilities;
import PMBPP.Utilities.TxtFiles;

/*
 * Predict set of data and save them in CSV
 */
public class PredictDatasets {

	public static void main(String[] args) throws Exception {

		//Parameters.setPhases("model.HLA,model.HLB,model.HLC,model.HLD");
		//Parameters.setMR("T");
		// String [] arg= {"/Volumes/PhDHardDrive/PMBPP/FinalTraining/PMBPPResults/Experimental/HLAandParrot/noncs2",new File("/Users/emadalharbi/Downloads/PMBPP/MRdata").getAbsolutePath()+"/"};
		 //Parameters.setTrainedModelsPath("/Users/emadalharbi/Downloads/PMBPP/PredictionModelsMR");
		 //new PredictDatasets().Predict(arg[0],args[1]);
		 
		Parameters.setFilteredModels("ARPwARP");
		Parameters.setFilterModels("T");
		new PredictDatasets().Predict("DatasetsParrot");


		 
		new PredictDatasets().UpdateFeaturesFromCSVAtt("PredictionModelsMR");
		/*
		 * MR String [] arg= {"/Users/emadalharbi/Downloads/PMBPP/noncsMR2",new
		 * File("/Volumes/PhDHardDrive/MRDatasets/DatasetsForBuilding/").getAbsolutePath
		 * ()+"/"}; Parameters.TrainedModelsPath="PredictionModels";
		 * Parameters.Phases="model.HLA,model.HLB,model.HLC,model.HLD";
		 * Parameters.Featuers="RMSD,Skew,Resolution,Max,Min,SequenceIdentity";
		 * Parameters.MR="T"; new PredictDatasets().Predict(arg);
		 */
	}
public void Predict(String PathToDatasets) throws Exception {
	
	
	
	if(new File("Temp/").exists())
	FileUtils.cleanDirectory(new File("Temp/")); 
	if(new File("PredictedDatasets/").exists())
	FileUtils.cleanDirectory(new File("PredictedDatasets/")); 
	
	//if(new File(Parameters.getTrainedModelsPath()).exists()) 
	//	FileUtils.deleteDirectory(new File(Parameters.getTrainedModelsPath()));
	
	if(!new File(Parameters.getTrainedModelsPath()).exists()) 
	new Predict().CopyModelsFromResources();
	
	UpdateFeaturesFromCSVAtt(Parameters.getTrainedModelsPath());
	
	if(Parameters.getParrotPhases()==null)// if parrot phases not set, then prepare all features and save them into csv 
	new PrepareFeatures().Prepare(new File(PathToDatasets).getAbsolutePath());
	
	Vector<ExcelContents> excel = new Vector<ExcelContents>();
	for (File mtz : new FilesUtilities().FilesByExtension(PathToDatasets,".mtz")) {
		
		ExcelContents pdb= new ExcelContents();
		pdb.PDB_ID=mtz.getName().replaceAll("." + FilenameUtils.getExtension(mtz.getName()), "");
		excel.add(pdb);
	}
	PMBPP.CheckDirAndFile("Temp");
	// loop on all models names. Not necessary all the models are exists in structure evaluation measures   
Vector<String> modelsNames= new Vector<String>();
for(File folder : new FilesUtilities().ReadFilesList(Parameters.getTrainedModelsPath())) {
	if(folder.isDirectory()) {
		for(File model : new FilesUtilities().FilesByExtension(folder.getAbsolutePath(), ".model")) {
			String modelname=model.getName().replaceAll("." + FilenameUtils.getExtension(model.getName()), "");
			if(!modelsNames.contains(modelname)) {
				modelsNames.add(modelname);
				if(Parameters.getFilterModels().equals("T") && Parameters.getFilteredModels().contains(modelname))
				new ExcelSheet().FillInExcel(excel, "Temp/"+modelname);
				if(Parameters.getFilterModels().equals("F"))
				new ExcelSheet().FillInExcel(excel, "Temp/"+modelname);
				
				if(new File("Temp/").listFiles().length>0) {//at least 1 file. it might be no files when use FilteredModels  
				//Parameters.setLoadAllMLModelsAtOnce("T");
				//Parameters.setPreloadedMLModels( Parameters.getTrainedModelsPath());// we loading the ML models here because if loaded in Predict.java will affect on the inference times for the first data set will be predicted 
				Predict(new File("Temp/").getAbsolutePath(),PathToDatasets,false);
				//Predict(new File("Temp/").getAbsolutePath(),PathToDatasets,false); do it twice when you  need an accurate inference time because first data set to be predicted, its inference time will include Weka warm up time.    

				}
				FileUtils.cleanDirectory(new File("Temp/")); 
				Parameters.getPreloadedMLModels().clear();
			}
		}
	}
}

File [] csv = new FilesUtilities().ReadFilesList("PredictedDatasets");
HashMap<String, LinkedHashMap<String, String>> csvinmap = new HashMap<String, LinkedHashMap<String, String>>();
int countRecord=1;
for (File c : csv ) {
	
	HashMap<String, HashMap<String, String>> temp =  new CSVReader().ReadIntoHashMapWithnoIDHeader(c.getAbsolutePath());

	for(String key :temp.keySet()) {
		LinkedHashMap<String, String> HashMapToLinked= new LinkedHashMap<String, String>(temp.get(key));
		csvinmap.put(String.valueOf(countRecord), HashMapToLinked);
		countRecord++;
	}
	
    
}
		new CSVWriter().WriteFromHashMap(csvinmap, "PredictedDatasets.csv","ID");
		//FileUtils.deleteDirectory(new File("PredictedDatasets/")); 
		FileUtils.deleteDirectory(new File("CSVToUseInStatisticalTest/")); 
		FileUtils.deleteDirectory(new File("Temp/")); 
	
		
}
	public void Predict(String PathToExcelFolder, String PathToDatasets, boolean Traning) throws Exception {
		// TODO Auto-generated method stub
		new Log().TxtInRectangle("Predicting datasets");
		//String PathToExcelFolder = args[0];
		//String PathToDatasets = args[1];

		isValid(PathToExcelFolder, PathToDatasets);

		//Parameters.setPreloadedMLModels( Parameters.getTrainedModelsPath());
		
		for (File Excel : new FilesUtilities().ReadFilesList(PathToExcelFolder)) { // loop on all excel files

			String ExcelName = Excel.getName().replaceAll("." + FilenameUtils.getExtension(Excel.getName()), "");
			new Log().Info(this, " Predicting " + ExcelName);
			ExcelLoader f = new ExcelLoader();
			Vector<ExcelContents> excel = new Vector<ExcelContents>();
			HashMap<String, HashMap<String, String>> Results = new HashMap<String, HashMap<String, String>>();
			excel = f.ReadExcel(Excel.getAbsolutePath());
			
			Parameters.setFilterModels ( "T");
			Parameters.getFilteredModels().add(ExcelName); // remove the others models. Only keep the model that
														// match this excel
			Parameters.setPreloadedMLModels( Parameters.getTrainedModelsPath());// we loading the ML models here because if loaded in Predict.java will affect on the inference times for the first data set will be predicted 
			Parameters.getFilteredModels().clear();// clear because will add the ExcelName again in for loop below
			for (int i = 0; i < excel.size(); ++i) {// read the excel records
				for (File mtz : new FilesUtilities().ReadFilesList(PathToDatasets)) {// find the mtz
					String MTZName = mtz.getName().replaceAll("." + FilenameUtils.getExtension(mtz.getName()), "");
					String MTZEx = FilenameUtils.getExtension(mtz.getName());

					if (MTZName.equals(excel.get(i).PDB_ID) && MTZEx.equals("mtz")) {
						String[] arg = { mtz.getAbsolutePath() };

						Parameters.setUsecfft ( true);
						Predict Pre = new Predict(); // will return setFilterModels to F and clear  getFilteredModels 
						Parameters.setFilterModels ( "T");
						Parameters.getFilteredModels().add(ExcelName); // remove the others models. Only keep the model that
																	// match this excel
						//https://stackoverflow.com/questions/180158/how-do-i-time-a-methods-execution-in-java
						long startTime = System.nanoTime();
						Pre.PredictMultipleModles(arg,false);
						long endTime   = System.nanoTime();
						long inferencetimes = (endTime - startTime)/1000000;//divide by 1000000 to get milliseconds.
						
						// Pre.Print(Pre.PipelinesPredictions);
						HashMap<String, String> PipelineResults = new HashMap<String, String>();
						for (String Key : Pre.PipelinesPredictions.keySet()) { // save into a map >> R-free, 0.2 and so
																				// on
							// System.out.println(MTZName);
							if( Pre.PipelinesPredictions.get(Key).get(ExcelName)!=null) {// in case R-free ML model not found 
							PipelineResults.put(Key, Pre.PipelinesPredictions.get(Key).get(ExcelName)[0]);
							if(Traning==false) {
							PipelineResults.put(Key+" prediction interval low", Pre.PipelinesPredictions.get(Key).get(ExcelName)[1]);
							PipelineResults.put(Key+" prediction interval high", Pre.PipelinesPredictions.get(Key).get(ExcelName)[2]);

							}
							}
							else {
								PipelineResults.put(Key, "-");
								PipelineResults.put(Key+" prediction interval low", "-");
								PipelineResults.put(Key+" prediction interval high", "-");

							}

						}
						PipelineResults.put("inference times", String.valueOf(inferencetimes));

						Results.put(excel.get(i).PDB_ID, PipelineResults); // A map {PDB1, {R-free,0.2 }} {PDB2,
																			// {R-free,0.2 }}
					}
				}

			}

			// write to csv
			Vector<String> MeasurementUnitsHeaders = new Vector<String>();
			for (String PDB : Results.keySet()) {

				for (String Key : Results.get(PDB).keySet()) { // give each an index R-free index 0 R-work 1 ... etc

					if (!MeasurementUnitsHeaders.contains(Key))
						MeasurementUnitsHeaders.add(Key);
				}

			}
			String CSV = "PDB";
			String CSVToUseInStatisticalTest = "PDB";
			for (int i = 0; i < MeasurementUnitsHeaders.size(); ++i)// add all headers
				CSV += "," + MeasurementUnitsHeaders.get(i);
			CSVToUseInStatisticalTest = CSV;
			for (int i = 0; i < MeasurementUnitsHeaders.size(); ++i)// add all headers
				CSVToUseInStatisticalTest += ",Achieved" + MeasurementUnitsHeaders.get(i);
			CSV += ",Prediction,Pipeline\n";
			CSVToUseInStatisticalTest += ",";
			for (String PDB : Results.keySet()) {
				int HeaderIndex = 0;

				String Record1 = PDB;
				String Record2 = PDB;
				String Record2ForCSVToUseInStatisticalTest = "";
				String FeaturesForCSVToUseInStatisticalTest = "";
				for (String Key : Results.get(PDB).keySet()) {
					if (MeasurementUnitsHeaders.get(HeaderIndex).equals(Key)) { // check the headers order because
																				// hashmaps are unsorted
						FeaturesForCSVToUseInStatisticalTest = ""; // empty string
						Record1 += "," + Results.get(PDB).get(Key);
						HeaderIndex++;
						if(Traning==true) {
							
						
						
						Vector<ExcelContents> TempExcel = new Vector<ExcelContents>();
						for (int i = 0; i < excel.size(); ++i) { // write temp excel that only contains this PDB
							if (excel.get(i).PDB_ID.equals(PDB)) {
								TempExcel.add(excel.get(i));
								break;
							}
						}

						PMBPP.CheckDirAndFile("TempExcel");
						new ExcelSheet().FillInExcel(TempExcel, "TempExcel/Temp");
						PMBPP.CheckDirAndFile("TempCSV");
						String[] arg = { "TempExcel", new File(PathToDatasets).getAbsolutePath() + "/", "TempCSV" };
						new PredictionTrainingDataPreparer().Prepare(arg); // create a csv that only contains this only
																			// PDB in the excel
						String Val = new CSVReader().GetRecordByHeaderName("TempCSV/Temp.csv", Key, 0); // now get the
																										// value

						Record2 += "," + Val;
						Record2ForCSVToUseInStatisticalTest += Val + ",";
						for (int p = 0; p < Parameters.getFeatures().split(",").length; ++p) { // Features will be updated
																							// from model's header CSV
																							// when we predict
							Val = new CSVReader().GetRecordByHeaderName("TempCSV/Temp.csv",
									Parameters.getFeatures().split(",")[p], 0); // now get the value
							if (p + 1 < Parameters.getFeatures().split(",").length) {
								FeaturesForCSVToUseInStatisticalTest += Val + ",";
								if (CSVToUseInStatisticalTest.split("\n").length == 1
										&& !CSVToUseInStatisticalTest.contains(Parameters.getFeatures().split(",")[p])) // we
																													// cannot
																													// add
																													// the
																													// features
																													// headers
																													// until
																													// Parameters.Features
																													// got
																													// updated
																													// from
																													// the
																													// model
																													// CSV
																													// when
																													// first
																													// prediction
																													// is
																													// done
									CSVToUseInStatisticalTest += Parameters.getFeatures().split(",")[p] + ",";
							} else {
								FeaturesForCSVToUseInStatisticalTest += Val;
								if (CSVToUseInStatisticalTest.split("\n").length == 1
										&& !CSVToUseInStatisticalTest.contains(Parameters.getFeatures().split(",")[p]))
									CSVToUseInStatisticalTest += Parameters.getFeatures().split(",")[p] + ",Pipeline\n";
							}
						}
						FileUtils.deleteDirectory(new File("TempExcel"));
						FileUtils.deleteDirectory(new File("TempCSV"));
					}
					} else {// very rare to happen
						new Log().Error(this, "Can not continue because there is a change in the headers order!  ");

					}
				}

				CSVToUseInStatisticalTest += Record1 + "," + Record2ForCSVToUseInStatisticalTest
						+ FeaturesForCSVToUseInStatisticalTest + "," + ExcelName + "\n";
				Record1 += ",T," + ExcelName + "\n";
				Record2 += ",F," + ExcelName + "\n";

				CSV += Record1;
				if(Traning==true)
				CSV += Record2;
				// pb.step();
			}

			if (new File("PredictedDatasets/" + ExcelName + ".csv").exists()) {
				new Log().Warning(this, ExcelName + ".csv has found in PredictedDatasets and deleted to create a new ");
				FileUtils.deleteQuietly(new File("PredictedDatasets/" + ExcelName + ".csv"));
			}
			if (new File("CSVToUseInStatisticalTest/" + ExcelName + ".csv").exists()) {
				new Log().Warning(this,
						ExcelName + ".csv has found in CSVToUseInStatisticalTest and deleted to create a new ");
				FileUtils.deleteQuietly(new File("CSVToUseInStatisticalTest/" + ExcelName + ".csv"));
			}
			PMBPP.CheckDirAndFile("PredictedDatasets");
			try (PrintWriter out = new PrintWriter("PredictedDatasets/" + ExcelName + ".csv")) {
				out.println(CSV);
			}
			PMBPP.CheckDirAndFile("CSVToUseInStatisticalTest");
			try (PrintWriter out = new PrintWriter("CSVToUseInStatisticalTest/" + ExcelName + ".csv")) {
				out.println(CSVToUseInStatisticalTest);
			}

		}

	}

	void isValid(String files, String PathToDatasets) {

		if (!new File(files).exists()) {
			new Log().Error(this, "Excel files are not found (Maybe it is wrong directory!)");

		}
		if (!new File(PathToDatasets).exists()) {
			new Log().Error(this, "Datasets directory is not found  (Maybe it is wrong directory!)");

		}

	}
	
	void UpdateFeaturesFromCSVAtt(String Dir) throws IOException, IllegalArgumentException, IllegalAccessException {
		HashMap<Integer,String> Features= new HashMap<Integer,String>();
		 File [] csv = new FilesUtilities().FilesByExtensionRecursively(Dir,".csv");
		 for (Field field : new Features().getClass().getDeclaredFields()) {
			Vector<Boolean> foundInAll = new Vector<Boolean>();
			Vector<Integer> FeatureIndexInCSV = new Vector<Integer>();
		 for(File file : csv ) {
			 
			 Vector<String> FeatureFromCSV=new Vector<String>(Arrays.asList(new TxtFiles().readFileAsString(file.getAbsolutePath()).split("\n")[0].split(",")));
			 if(FeatureFromCSV.contains(field.getName())) {
				 foundInAll.add(true);
				 FeatureIndexInCSV.add(FeatureFromCSV.indexOf(field.getName()));
			 }
		 
		 }
		
		
		 if(foundInAll.size()==csv.length) {
			
			 if(IsAllSame(FeatureIndexInCSV)==false)
				 new Log().Error(this, "Features order are not same accroess all CSV attribute files");
			
			 Features.put(FeatureIndexInCSV.get(0), field.getName());
		 }
			// Features+=field.getName()+",";
		 
		 if(foundInAll.size()!=csv.length && foundInAll.size()!=0)// foundInAll.size()!=0 because a feature can be in features class but not used 
			 new Log().Error(this, "This feature "+field.getName()+" not present in all the models's csv files! ");
		
		 }
		 String Fea="";
		 SortedSet<Integer> keys = new TreeSet<>(Features.keySet());
		 for(Integer key : keys) {
			 Fea+=Features.get(key)+",";
		 }
		
		 Parameters.setFeatures(Fea.substring(0,Fea.lastIndexOf(",")));
		
	}
	boolean IsAllSame(Vector<Integer> Vec) {
		
		for(int i =1 ; i < Vec.size(); ++i)
			if(Vec.get(i)!=Vec.get(0)) return false;
		return true;
	}
}
