package PMBPP.ML.Model;

import java.io.File;
import java.io.InputStream;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import PMBPP.Data.Preparation.GetFeatures;
import PMBPP.Log.Log;
import PMBPP.Utilities.FilesUtilities;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

/*
 * From here you can predict the performance of the pipelines.
 */

public class Predict {
	public String PredictionTable = "";
	public String[][] RowData;

	public HashMap<String, HashMap<String, String>> PipelinesPredictions;

	public static void main(String[] args) throws Exception {

		String Path = "1o6a-1.9-parrot-noncs.mtz";

		String[] arg = { Path };

		Predict Pre = new Predict();
		Parameters.setTrainedModelsPath ( "PredictionModels");
		Parameters.setCompressedModelFolderName ( "PredictionModels.zip");
		Parameters.setPhases ("HLA,HLB,HLC,HLD");

		Pre.PredictMultipleModles(arg);
		Pre.Print(Pre.PipelinesPredictions);

		Parameters.setTrainedModelsPath ( "ClassificationModels");
		Parameters.setCompressedModelFolderName ( "ClassificationModels.zip");

		Pre.PredictMultipleModles(arg);

		Pre.Print(Pre.PipelinesPredictions);

	}

	public void PredictMultipleModles(String[] args) throws Exception {
		// TODO Auto-generated method stub

		if (!new File(Parameters.getTrainedModelsPath()).exists()) { // if not found, then use models from resources

			CopyModelsFromResources();
			
			new Log().Info(this, "Models were copied from resources ");
		}
		if(Parameters.getLoadAllMLModelsAtOnce().equals("T")) {
			Parameters.setPreloadedMLModels( Parameters.getTrainedModelsPath());
			Parameters.setLoadAllMLModelsAtOnce("F");// to avoid multiple reading 
		}
		String Path = args[0];

		MLModel Pre = new MLModel();

		HashMap<String, HashMap<String, String>> Predictions = new HashMap<String, HashMap<String, String>>();// Ex
																												// R-free,
																												// {Phenix
																												// ,
																												// 0.20}

		for (File Folder : new FilesUtilities().ReadFilesList(Parameters.getTrainedModelsPath())) {
			Predictions.put(Folder.getName(), null);
		}

		boolean ThereisAmodel = false;
		double[] instanceValue1 = null;
		for (String Folder : Predictions.keySet()) {
			HashMap<String, String> PipelinesPredictions = new HashMap<String, String>();
			File[] models = new FilesUtilities().FilesByExtension(Parameters.getTrainedModelsPath() + "/" + Folder,".model");
			if (Parameters.getFilterModels().equals("T"))
				models = new FilesUtilities().ReadFilteredModels(Parameters.getTrainedModelsPath() + "/" + Folder);
//new Log().Info(this, Folder,false);
//int count=1;
			for (File m : models) {
				//new Log().Info(this, count+" out of "+models.length,true); count++;
					Parameters.setAttCSV(  m.getParent() + "/"
							+ m.getName().replaceAll("." + FilenameUtils.getExtension(m.getName()), "") + ".csv");

					Pre.ReadModel(m.getAbsolutePath());

					DecimalFormat df = new DecimalFormat("#.##");
					df.setRoundingMode(RoundingMode.HALF_UP);
					String modelName = m.getName().replaceAll("." + FilenameUtils.getExtension(m.getName()), "");

					if (Parameters.isUsecfft() == true) {
						if (instanceValue1 == null) { // no need to re-run cfft because all models use same features in
														// same order. Only we need to update AttCSV because is differ
														// in each model
							instanceValue1 = new GetFeatures().GetUsingFeatures(Path);
							Parameters.setInstanceValue1 ( instanceValue1);// to speed up when read
																		// classification/Prediction model in PMBPP.java
						}
					} else
						instanceValue1 = Parameters.getInstanceValue1();

					PipelinesPredictions.put(modelName, Pre.Predict(instanceValue1, Parameters.getAttCSV()));

					ThereisAmodel = true;

				
			}
			Predictions.put(Folder, PipelinesPredictions);

		}

		if (ThereisAmodel == true) {
			// Print(Predictions);
			PipelinesPredictions = Predictions;

		}

		if (ThereisAmodel == false)
			System.out.println("No models are found!");

		Parameters.getFilteredModels().clear();// return to default
		Parameters.setFilterModels ( "F");// return to default
	}

	public void Print(HashMap<String, HashMap<String, String>> Measures) {
		
		List<String> headersList = new ArrayList<String>();
		List<List<String>> rowsList = new ArrayList<List<String>>();
		
		headersList.add("Pipeline variant");
		for (String Key : Measures.keySet())
			headersList.add(Key);

		Vector<String> PrintedPipelines = new Vector<String>();
		for (String Key : Measures.keySet()) { // loop on folders ex R-free, R-work and Completeness

			for (String Key2 : Measures.get(Key).keySet()) { // loop on Pipelines/mooels
				if (!PrintedPipelines.contains(Key2)) { // if not printed
					ArrayList<String> list = new ArrayList<String>();
					list.add(Key2); // remove .model
					
					for (String Key3 : Measures.keySet()) { // go again to folders level
						boolean Found = false;
						for (String Key4 : Measures.get(Key3).keySet()) { // search for this Pipeline results in all the
																			// folders
							if (Key2.equals(Key4)) {
								list.add(Measures.get(Key3).get(Key4));
								Found = true;
							}
						}
						if (Found == false)
							list.add("-");
					}

					rowsList.add(list);
					PrintedPipelines.add(Key2);

				}

			}
		}

		String tableString = new Log().CreateTable(headersList, rowsList);

		System.out.println(tableString);
		PredictionTable = tableString;
		if(Parameters.getHTMLTable().equals("T")) {
			String html = new Log().CreateHTMLTable(headersList, rowsList);
			System.out.println("HTML: \n"+html);
		}

		// convert to array for GUI
		// https://stackoverflow.com/questions/371839/java-nested-list-to-array-conversion
		String[][] array = new String[rowsList.size()][];
		String[] blankArray = new String[0];
		for (int i = 0; i < rowsList.size(); i++) {
			array[i] = rowsList.get(i).toArray(blankArray);
		}
		RowData = array;
	}

	public void CopyModelsFromResources() throws Exception {
		new Log().Info(this, "Uncompressing the models (it takes a bit longer and only needed at the first time of using this tool)");
		// Files must compress without the main folder  itself for example 
		// zip -r ../zipped_dir.zip *
		Parameters.setTrainedModelsPath ( new File(Parameters.getCompressedModelFolderName()).getName().replaceAll(
				"." + FilenameUtils.getExtension(new File(Parameters.getCompressedModelFolderName()).getName()), ""));
		FileUtils.deleteDirectory(new File(Parameters.getTrainedModelsPath()));
		PMBPP.CheckDirAndFile(Parameters.getTrainedModelsPath());

		InputStream in = this.getClass().getResourceAsStream("/" + Parameters.getCompressedModelFolderName());
		Files.copy(in, Paths.get(System.getProperty("user.dir") + "/" + Parameters.getTrainedModelsPath() + "/"
				+ Parameters.getCompressedModelFolderName()), StandardCopyOption.REPLACE_EXISTING);

		try {

			ZipFile zipFile = new ZipFile(
					"./" + Parameters.getTrainedModelsPath() + "/" + Parameters.getCompressedModelFolderName());
			zipFile.extractAll("./" + Parameters.getTrainedModelsPath());

		} catch (ZipException e) {
			e.printStackTrace();
		}
		FileUtils.deleteQuietly(
				new File("./" + Parameters.getTrainedModelsPath() + "/" + Parameters.getCompressedModelFolderName()));

	}

	void RemoveModels() {

		for (File m : new FilesUtilities().ReadFilesList(Parameters.getTrainedModelsPath())) {
			for (File model : new FilesUtilities().ReadFilesList(m.getAbsolutePath())) {

				String modelName = model.getName().replaceAll("." + FilenameUtils.getExtension(model.getName()), "");

				if (!Parameters.getFilteredModels().contains(modelName)) {

					FileUtils.deleteQuietly(model);
				}
			}
		}

	}

}
