package Weka.Predictor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Vector;

import Comparison.Runner.RunningParameter;
import Data.Preparation.ExcelUpdater;

public class PMBPP {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		 Vector <String> Parm = new Vector<String>();
		 for (int i=0 ; i < args.length ; ++i){
			 
			 if(args[i].contains("=")){
				 Parm.addAll(Arrays.asList(args[i].split("=")));
				 
			 }
			 
		 }
		 if(args[0].equals("Prepare")){
			
			 PMBPP.CheckDirAndFile("CSV");
			if(checkArg(Parm,"ExcelFolder")!=null && checkArg(Parm,"Datasets")!=null ) {
				
				String [] arg= {checkArg(Parm,"ExcelFolder"),checkArg(Parm,"Datasets"),"CSV"};
				ExcelUpdater.main(arg);
				
			}
		 }
		 
		 if(args[0].equals("CreateModels")){
				
				//saving to Models folder are done from the Predictor
				if(checkArg(Parm,"CSV")!=null ) {
					String [] arg= {checkArg(Parm,"CSV"),"Models"};
					CreateModels.main(arg);
					
				}
			 }
		 
		 if(args[0].equals("Predicte")){
				
				//saving to Models folder are done from the Predictor
				if(checkArg(Parm,"mtz")!=null  && checkArg(Parm,"Models")!=null) {
					String [] arg= {checkArg(Parm,"mtz")};
					Parameters.ModelsPath=checkArg(Parm,"Models"); // not tested
					new Predict().main(arg);
					
				}
				if(checkArg(Parm,"mtz")!=null && checkArg(Parm,"Models")==null) {
					String [] arg= {checkArg(Parm,"mtz")};
					new Predict().main(arg);
					
				}
			 }
	}
	static String checkArg(Vector<String> Args, String Keyword ){
		for (int i=0 ; i< Args.size() ; ++i){
			if(Args.get(i).equals(Keyword))
				return Args.get(i+1);
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
			System.out.println("Error: Unable to create "+Path);
			System.exit(-1);
			return false;
			
		}
		   
	}
}
