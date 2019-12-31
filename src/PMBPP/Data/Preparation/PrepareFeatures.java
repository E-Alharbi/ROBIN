package PMBPP.Data.Preparation;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.json.simple.parser.ParseException;

import PMBPP.Log.Log;
import PMBPP.ML.Model.Parameters;
import PMBPP.Utilities.CSVWriter;
import PMBPP.Utilities.FilesUtilities;
import me.tongfei.progressbar.ProgressBar;

public class PrepareFeatures {
	public static void main(String[] args) throws IOException, IllegalArgumentException, IllegalAccessException, ParseException {
		
		//experimental phases 
		new PrepareFeatures().Prepare("/Volumes/PhDHardDrive/EditorsRevision-2/Datasets/NO-NCS/");
		
		/* MR
		Parameters.Phases="model.HLA,model.HLB,model.HLC,model.HLD";
		Parameters.Featuers="RMSD,Skew,Resolution,Max,Min,SequenceIdentity";
		Parameters.MR="T";
		new PrepareFeatures().Prepare("/Volumes/PhDHardDrive/MRDatasets/DatasetsForBuilding2/");
	    */
	}
	public  void Prepare(String PathToDatasets) throws IOException, IllegalArgumentException, IllegalAccessException, ParseException {
		// TODO Auto-generated method stub
		new Log().TxtInRectangle("Features prepare");
		
		if(isValid(PathToDatasets)==false)
			System.exit(-1);
		
		
		String MTZFileName="";
  if(new File(PathToDatasets).isFile()) {
	  
	  MTZFileName=new File(PathToDatasets).getName().replaceAll("."+FilenameUtils.getExtension(new File(PathToDatasets).getName()),"");

	if(new File(MTZFileName+"features.csv").exists()) {
		
		new Log().Warning(this,MTZFileName+"features.csv"+" has found and deleted to create new one");
		FileUtils.deleteQuietly(new File(MTZFileName+"features.csv"));
	}	
}
		
		
		File [] mtz = new File[1];// assuming there is a one mtz
		if(new File(PathToDatasets).isFile()) {
			 mtz[0]= new File(PathToDatasets);
		}
		else {
			mtz=new FilesUtilities().ReadMtzList(PathToDatasets);
		}
		HashMap<String,LinkedHashMap<String,String>> PDB= new HashMap<String,LinkedHashMap<String,String>>();
		//ProgressBar pb = new ProgressBar("Preparing features:",new FilesUtilities().ReadMtzList(PathToDatasets).length); 
		//pb.start();
		for(File F :  mtz) {
			//pb.step();
			Features fea=	new GetFeatures().Get(F.getAbsolutePath());
			LinkedHashMap<String,String> FeatureInMap= new LinkedHashMap<String,String>();
			for (Field field : fea.getClass().getDeclaredFields()) {
				if(Parameters.Features.contains(field.getName())) // if this feature is using 
				FeatureInMap.put(field.getName(), String.valueOf((Double)field.get(fea)));
				
		    }
			
			String MTZ=F.getName().replaceAll("."+FilenameUtils.getExtension(F.getName()),"");

			PDB.put(MTZ, FeatureInMap);
		
		}
		//pb.stop();
		if(!new File(PathToDatasets).isFile())
		new CSVWriter().WriteFromHashMap(PDB, "features.csv");
		else
		{
			new CSVWriter().WriteFromHashMap(PDB, MTZFileName+"features.csv");
		}
	}
	
	boolean isValid(String PathToDatasets) {
		
		if(!FilenameUtils.getExtension(new File(PathToDatasets).getName()).isEmpty()) {
			if(!new File(PathToDatasets).exists()) {
				new Log().Error(this, "mtz file is not found (Maybe it is wrong directory!)");
				
				return false;
			}
		}
		
		if(!new File(PathToDatasets).isFile()) {//if not a file then check how many mtz files are there
		if(!new File(PathToDatasets).exists()) {
			new Log().Error(this, " No mtz files are found in "+ PathToDatasets+" (Maybe it is wrong directory!)");
			
			return false;
		}
		if(new FilesUtilities().ReadMtzList(PathToDatasets).length==0) {
			new Log().Error(this," No mtz files are found in "+ PathToDatasets+" (Maybe it is wrong directory!)");
		
		return false;
		}
		}
		
		
		if(new File("features.csv").exists()) {
			
			new Log().Warning(this, "features.csv has found and deleted to create new one");
			FileUtils.deleteQuietly(new File("features.csv"));
		}
		return true;
	}

}
