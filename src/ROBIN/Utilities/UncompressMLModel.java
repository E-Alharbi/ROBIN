package ROBIN.Utilities;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Collections;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FileUtils;

import ROBIN.Log.Log;
import ROBIN.ML.Model.MLModel;
import ROBIN.ML.Model.Parameters;
import ROBIN.ML.Model.Predict;

public class UncompressMLModel {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

	
		
	}
	
	
	public void Uncompress() throws Exception {
		
		
		
			
			Parameters.setTrainedModelsPath ("PredictionModels");
			Parameters.setCompressedModelFolderName ("PredictionModels.zip");

			if (Parameters.getMR().equals("T")) {
				Parameters.setTrainedModelsPath ("PredictionModelsMR");
				Parameters.setCompressedModelFolderName ("PredictionModelsMR.zip");
				Parameters.setModelFolderName("PredictionModelsMR");
			}
			if(new File(Parameters.getTrainedModelsPath()).exists()) {
			FileUtils.deleteDirectory(new File(Parameters.getTrainedModelsPath()));
			
		}
			new Predict().CopyModelsFromResources();
		new Log().Info(this, "Uncompressing ML models...");
		File [] Dir= new FilesUtilities().ReadFilesList(Parameters.getModelFolderName());
	    Vector<File> models= new Vector<File>();
		for(File folder : Dir) {
			if(folder.isDirectory()) {
				File [] m = new FilesUtilities().FilesByExtension(folder.getAbsolutePath(),".model");
				Collections.addAll(models, m);
			}
		}
		for(File f : models) {
			new Log().Info(this, f.getAbsolutePath());
			MLModel model = new MLModel();
			Parameters.setCompressModel("T");
			model.ReadModel(f.getAbsolutePath());
			Parameters.setCompressModel("F");
			f.delete();// delete the compressed model 
			model.SaveModel(f.getAbsolutePath().replaceAll(".model", ""),false);
			
		}
		new Log().Info(this, "Uncompressing is done.");
	}
	

}
