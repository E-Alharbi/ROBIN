package PMBPP.Validation;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import org.apache.commons.io.FilenameUtils;

import PMBPP.ML.Model.PMBPP;
import PMBPP.ML.Model.Parameters;
import PMBPP.ML.Model.Predict;
import PMBPP.Prediction.Analysis.ModelPerformance;
import PMBPP.Utilities.CSVReader;
import PMBPP.Utilities.CSVWriter;
import PMBPP.Utilities.FilesUtilities;

/*
 * Classify set of data and save them in CSV
 */
public class ClassifyDatasets {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Parameters.setTrainedModelsPath ( "ClassificationModels");
		// new
		// ClassifyDatasets().Classify("/Volumes/PhDHardDrive/FinalTraining/Experimental/ParrotPhases/CSVToUseInStatisticalTest","/Volumes/PhDHardDrive/EditorsRevision-2/Datasets/NO-NCS");
		new ClassifyDatasets().OmitTrainingData("ClassifedDatasets", "TrainAndTestDataClassificationModels");
	}

	public void Classify(String PathToCSV, String Datasets) throws Exception {

		for (File csv : new FilesUtilities().ReadFilesList(PathToCSV)) {
			HashMap<String, Vector<HashMap<String, String>>> CSV = new CSVReader()
					.ReadIntoHashMap(csv.getAbsolutePath(), "PDB");

			for (String PDB : CSV.keySet()) {

				for (File mtz : new FilesUtilities().FilesByExtension(Datasets,".mtz")) {
					String MTZName = mtz.getName().replaceAll("." + FilenameUtils.getExtension(mtz.getName()), "");
					if (PDB.equals(MTZName)) {
						String[] arg = { mtz.getAbsolutePath() };
						Parameters.setUsecfft ( true);
						Predict Pre = new Predict();
						Parameters.setFilterModels ( "T");
						Parameters.getFilteredModels()
								.add(csv.getName().replaceAll("." + FilenameUtils.getExtension(csv.getName()), ""));
						Pre.PredictMultipleModles(arg);
						HashMap<String, String> temp = CSV.get(PDB).get(0);
						for (String Key : Pre.PipelinesPredictions.keySet()) { // save into a map >> R-free, 0.2 and so
																				// on
							temp.put(Key + "Classifying", Pre.PipelinesPredictions.get(Key).get(
									csv.getName().replaceAll("." + FilenameUtils.getExtension(csv.getName()), "")));

						}
						Vector<HashMap<String, String>> TempVec = new Vector<HashMap<String, String>>();
						TempVec.add(temp);
						CSV.put(PDB, TempVec);
					}
				}
			}
			PMBPP.CheckDirAndFile("ClassifedDatasets");
			new CSVWriter().WriteFromHashMapContainsRepatedRecord(CSV, "ClassifedDatasets/" + csv.getName(),"PDB");

		}
	}

	public void OmitTrainingData(String PathToCSV, String PathToTestCSV) throws IOException {
		for (File CSV : new FilesUtilities().ReadFilesList(PathToCSV)) {
			String CSVName = CSV.getName().replaceAll("." + FilenameUtils.getExtension(CSV.getName()), "");

			for (File TestCSV : new FilesUtilities().ReadFilesList(PathToTestCSV)) {
				String TestFile = TestCSV.getName().split("-")[0] + "-"
						+ TestCSV.getName().split("-")[TestCSV.getName().split("-").length - 1]; // We want to remove
																									// measurementUnit
																									// from the middle.
																									// PhenixHLAArp-R-free-test.csv
																									// becomes
																									// PhenixHLAArp-test.csv.
																									// Here we assume
																									// all the training
																									// data sets are
																									// same.

				if ((CSVName + "-test.csv").equals(TestFile)) {

					PMBPP.CheckDirAndFile("CSVToUseInClassificationPlots");
					new CSVWriter().WriteFromHashMapContainsRepatedRecord(new ModelPerformance().omit(CSV, TestCSV),
							"CSVToUseInClassificationPlots/" + CSV.getName(),"PDB");

				}
			}
		}
	}
}
