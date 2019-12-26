package PMBPP.Data.Preparation;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.json.simple.parser.ParseException;

import Comparison.Analyser.ExcelContents;
import Comparison.Analyser.ExcelLoader;
import PMBPP.Log.Log;
import PMBPP.ML.Model.PMBPP;
import PMBPP.ML.Model.Parameters;
import PMBPP.Utilities.CSVWriter;
import PMBPP.Utilities.FilesUtilities;
import me.tongfei.progressbar.ProgressBar;


public class PredictionTrainingDataPreparer {

	public static void main(String[] args) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException, NumberFormatException, IllegalArgumentException, ParseException {
		
		//experimental phases 
		String [] arg = {"/Users/emadalharbi/Downloads/TestPreAcc/noncs/","/Volumes/PhDHardDrive/EditorsRevision-2/Datasets/NO-NCS/", "CSV"};
	
		new PredictionTrainingDataPreparer().Prepare(arg);
		
		
		//MR
		/* Parameters.Phases="model.HLA,model.HLB,model.HLC,model.HLD";
		 * Parameters.Featuers="RMSD,Skew,Resolution,Max,Min,SequenceIdentity";
		 * Parameters.MR="T";
		 * 		String [] arg = {"ExcelFolder/","Dataset/"};
		 * new TrainingDataPreparer().Prepare(arg);
		 */
	}
	public  void Prepare (String[] args) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException, NumberFormatException, IllegalArgumentException, ParseException {
		// TODO Auto-generated method stub
		
		new Log().TxtInRectangle("Prediction training data preparer");
		
		if(isValid(args[0],args[1])==false)
			System.exit(-1);
		
		File [] files =  new FilesUtilities().ReadFilesList(args[0]);
		String PathToDataset=args[1];
		Vector<ExcelContentsWithFeatures> Excel2 = new Vector<ExcelContentsWithFeatures>();
		for(File e :files ) {
		ExcelLoader f = new ExcelLoader();
		Vector<ExcelContents> Excel = new Vector<ExcelContents>();
		
		Excel =  f.ReadExcel(e.getAbsolutePath());
		
		Excel2=	new ExcelContentsWithFeatures().Addall(Excel);
		new Log().TxtInRectangle("Excel: "+e.getName());
		
		 for(ExcelContentsWithFeatures EE : Excel2) {
			 EE.CM= new GetFeatures().Get(PathToDataset+EE.PDB_ID+".mtz");
			
			 new Log().Info(this, "Get data from the excel file: "+EE.PDB_ID);
		 }
		
		
		 CSVWriter CW = new CSVWriter();
		 if(args.length>2) {
		 CW.PathToSaveCSV=args[2];
		 if(new File(CW.PathToSaveCSV).exists()) {
			
			
			 new Log().Warning(this, CW.PathToSaveCSV+" has deleted to create new folder");
			 FileUtils.deleteDirectory(new File(CW.PathToSaveCSV));
		 }
		 PMBPP.CheckDirAndFile(CW.PathToSaveCSV);
		 }
		 CW.WriteToCSV(Excel2,e.getName());
	}
		
	}
	boolean isValid(String files, String PathToDatasets) {
		
		if(!new File(files).exists()) {
			new Log().Error(this,"Excel files are not found (Maybe it is wrong directory!)");
           return false;
		}
		if(!new File(PathToDatasets).exists()) {
			new Log().Error(this,"Datasets directory is not found  (Maybe it is wrong directory!)");
          return false;
		}
		
		return true;
	}
}
