package PMBPP.ML.Model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;



import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.jakewharton.fliptables.FlipTable;

import PMBPP.Data.Preparation.Features;
import PMBPP.Data.Preparation.GetFeatures;
import PMBPP.Data.Preparation.PrepareFeatures;
import PMBPP.Data.Preparation.cfft;
import PMBPP.Data.Preparation.mtzinfo;
import PMBPP.Log.Log;
import PMBPP.Utilities.FilesUtilities;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import table.draw.Block;
import table.draw.Board;
import table.draw.Table;

public class Predict {
	public String PredictionTable="";
	public String[][] RowData;
	//public  Features cfftM;
	
	public HashMap<String , HashMap<String,String>> PipelinesPredictions;
	public  static void main(String[] args) throws Exception{
		
		String Path="/Volumes/PhDHardDrive/EditorsRevision-2/Datasets/NO-NCS/1o6a-1.9-parrot-noncs.mtz";
	
		
		String [] arg= {Path};
		
		Predict Pre = new Predict();
       // Parameters.TrainedModelsPath="/Volumes/PhDHardDrive/FinalTraining/FinalTraining/Experimental/Prediction/PredictionModels";
        Parameters.Phases="HLA,HLB,HLC,HLD";
        Pre.PredictMultipleModles(arg);
		Pre.Print(Pre.PipelinesPredictions);
		
		
		//Parameters.TrainedModelsPath="/Volumes/PhDHardDrive/FinalTraining/FinalTraining/Experimental/Prediction/ClassificationModels";
		Pre.PredictMultipleModles(arg);
		Pre.Print(Pre.PipelinesPredictions);
		
		
		
	}
	
	public  void PredictMultipleModles(String[] args) throws Exception {
		// TODO Auto-generated method stub

		
		if(!new File(Parameters.TrainedModelsPath).exists()) { // if not found, then use models from resources
			
			
			CopyModelsFromResources();
		//	System.out.println("Models were copied from resources ");
			new Log().Info(this, "Models were copied from resources ");
		}
		
		String Path=args[0];
		
		
	
		
		
		 
		
		
		
		
		MLModel Pre = new MLModel();
		
		
		
	
		
		HashMap<String , HashMap<String,String>> Predictions= new HashMap<String , HashMap<String,String>>();// Ex R-free, {Phenix , 0.20} 
		
		for(File Folder : new FilesUtilities().ReadFilesList(Parameters.TrainedModelsPath)) {
			Predictions.put(Folder.getName(), null);
		}
		
		boolean ThereisAmodel=false;
		double[] instanceValue1=null;
		for(String Folder : Predictions.keySet()) {
			HashMap<String,String> PipelinesPredictions= new HashMap<String,String>();
			File [] models=new FilesUtilities().ReadFilesList(Parameters.TrainedModelsPath+"/"+Folder);
			if(Parameters.FilterModels.equals("T"))
				models=new FilesUtilities().ReadFilteredModels(Parameters.TrainedModelsPath+"/"+Folder);
			for(File m : models) {
				if(m.getName().contains(".model")) {
					Parameters.AttCSV=m.getParent()+"/"+m.getName().replaceAll("."+FilenameUtils.getExtension(m.getName()),"")+".csv";	
				
				
				Pre.ReadModel(m.getAbsolutePath());
				
				
				DecimalFormat df = new DecimalFormat("#.##");
				df.setRoundingMode(RoundingMode.HALF_UP);
				String modelName=m.getName().replaceAll("."+FilenameUtils.getExtension(m.getName()),"");

				
			 	if(Parameters.Usecfft==true) {
			 	if(instanceValue1==null) { // no need to re-run cfft because all models use same features in same order. Only we need to update AttCSV because are differ in each model 
				instanceValue1 = new GetFeatures().GetUsingFeatures(Path);
			 	Parameters.instanceValue1=instanceValue1;// to speed up when read classification/Prediction model in PMBPP.java
			 	}
			 	}
				else
				instanceValue1=Parameters.instanceValue1;
			 	
			 	
			 
				PipelinesPredictions.put(modelName, Pre.Predicte(instanceValue1,Parameters.AttCSV));
				
				ThereisAmodel=true;
				
				}
			}
			Predictions.put(Folder, PipelinesPredictions);
			
		}
		

		if(ThereisAmodel==true) {
			//Print(Predictions);
			PipelinesPredictions=Predictions;
			
		}
		
		if(ThereisAmodel==false)
		System.out.println("No models are found!");	
		
		
		Parameters.FilteredModels.clear();// return to default
		Parameters.FilterModels="F";// return to default
	}
	
	
	public void Print(HashMap<String , HashMap<String,String>> Measures) {
		//List<String> headersList = Arrays.asList("Pipeline");
		List<String> headersList = new ArrayList<String>();
        List<List<String>> rowsList = new ArrayList<List<String>>();
        headersList.add("Pipeline variant");
        for(String Key : Measures.keySet())
        headersList.add(Key);
		
        Vector<String> PrintedPipelines = new Vector<String>();
        for(String Key : Measures.keySet()) { // loop on folders ex R-free, R-work and Completeness
        	
        	for(String Key2 : Measures.get(Key).keySet()) { // loop on Pipelines/mooels
        		if(!PrintedPipelines.contains(Key2)) { // if not printed   
        		ArrayList<String> list = new ArrayList<String>();
        		list.add(Key2); // remove .model
        		//System.out.print(Key2+"\t");
        		for(String Key3 : Measures.keySet()) { // go again to folders level 
        			boolean Found=false;
        			for(String Key4 : Measures.get(Key3).keySet()) { // search  for this Pipeline results in all the folders 
        				if(Key2.equals(Key4)) {
            				list.add(Measures.get(Key3).get(Key4));
            				Found=true;
            			}
        			}
        			if(Found==false)
        				list.add("-");
        		}
        		
        			rowsList.add(list);
            		PrintedPipelines.add(Key2);
        			
        		}
        		
        	}
        }
		

    String tableString = new Log().CreateTable(headersList, rowsList);
		 
	System.out.println(tableString);
	PredictionTable=tableString;
	
	//convert to array for GUI
	//https://stackoverflow.com/questions/371839/java-nested-list-to-array-conversion
	String[][] array = new String[rowsList.size()][];
	String[] blankArray = new String[0];
	for(int i=0; i < rowsList.size(); i++) {
	    array[i] = rowsList.get(i).toArray(blankArray);
	}
	RowData=array;
	}
	
	void CopyModelsFromResources() throws IOException, URISyntaxException {
		// Files must compress without the main folder (Models) itself for example zip -r ../zipped_dir.zip *
		Parameters.TrainedModelsPath=new File(Parameters.CompressedModelFolderName).getName().replaceAll("."+FilenameUtils.getExtension(new File(Parameters.CompressedModelFolderName).getName()),"");

		FileUtils.deleteDirectory(new File(Parameters.TrainedModelsPath));
		PMBPP.CheckDirAndFile(Parameters.TrainedModelsPath);
		
		 InputStream in = this.getClass().getResourceAsStream("/"+Parameters.CompressedModelFolderName);
         Files.copy(in, Paths.get(System.getProperty("user.dir")+"/"+Parameters.TrainedModelsPath+"/"+Parameters.CompressedModelFolderName), StandardCopyOption.REPLACE_EXISTING);

try {
			
			
		    ZipFile zipFile = new ZipFile(Parameters.TrainedModelsPath+"/"+Parameters.CompressedModelFolderName);
		    zipFile.extractAll("./"+Parameters.TrainedModelsPath);
		} catch (ZipException e) {
		    e.printStackTrace();
		}
		FileUtils.deleteQuietly(new File("./"+Parameters.TrainedModelsPath+"/"+Parameters.CompressedModelFolderName));
		
	}
	
	void RemoveModels() {
		
		for(File m : new FilesUtilities().ReadFilesList(Parameters.TrainedModelsPath)) {
			for(File model : new FilesUtilities().ReadFilesList(m.getAbsolutePath())) {
				
				String modelName=model.getName().replaceAll("."+FilenameUtils.getExtension(model.getName()),"");

				if(!Parameters.FilteredModels.contains(modelName) ) {
					
				FileUtils.deleteQuietly(model);
				}
			}
		}
		
	}
	
}
