package PMBPP.Data.Preparation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import PMBPP.Log.Log;
import PMBPP.ML.Model.Parameters;
import PMBPP.Utilities.FilesUtilities;



public class cfft {
String Phases="";
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String Path="/Volumes/PhDHardDrive/EditorsRevision-2/Datasets/NO-NCS/";
		/*
		for(File M : new FilesUtilities().ReadFilesList(Path)) {
			if(M.getName().contains(".mtz")) {
			cfft c = new cfft();
			c.Phases="HLA,HLB,HLC,HLD";
			
			System.out.print(M+"\t"+c.Cfft(M.getAbsolutePath()).Max+"\t");
			c.Phases="parrot.ABCD.A,parrot.ABCD.B,parrot.ABCD.C,parrot.ABCD.D";
			System.out.println(c.Cfft(M.getAbsolutePath()).Max+"\t");}
		}
		*/
new cfft().Cfft("/Volumes/PhDHardDrive/EditorsRevision-2/Datasets/NO-NCS/1o6a-1.9-parrot-noncs.mtz");
	}

	public Features Cfft(String mtzin) throws IOException {
		
	 String st = null;
	 String PathToCfft=System.getenv("CCP4")+"/share/python/CCP4Dispatchers/cfft.py";
	 String[]callAndArgs= {"python",PathToCfft,
	"-mtzin",mtzin,
	"-colin-fo",Parameters.colinfo,
	"-colin-hl",Parameters.Phases,
	"-stats",
	};
	 
	Process p = Runtime.getRuntime().exec(callAndArgs);
	//Process p = new ProcessBuilder(callAndArgs).start();
		             

	BufferedReader stdInput = new BufferedReader(new 

		                  InputStreamReader(p.getInputStream()));



		             BufferedReader stdError = new BufferedReader(new 

		                  InputStreamReader(p.getErrorStream()));



		             Features CM= new Features();
		            
		             while ((st = stdInput.readLine()) != null) {
		            	// System.out.println(st);
		            	 if(st.contains("About mean (rmsd)")) {
		            		// System.out.println(st.substring(st.indexOf("About mean (rmsd)")).replaceAll("[^0-9\\\\.]+", ""));
		            	
		            		 CM.RMSD=Double.parseDouble(st.substring(st.indexOf("About mean (rmsd)")).replaceAll("[^0-9\\\\.]+", ""));
		            		 if(st.substring(st.indexOf("About mean (rmsd)")).contains("-")) // Parsing statement removes negative sign  
		            			 CM.RMSD=-CM.RMSD; 
		            		
		            	 }
		            	 if(st.contains("About mean (skew)")) {
		            		
		            	CM.Skew=Double.parseDouble(st.substring(st.indexOf("About mean (skew)")).replaceAll("[^0-9\\\\.]+", ""));
		            	
		            	if(st.substring(st.indexOf("About mean (skew)")).contains("-")) // Parsing statement removes negative sign  
	            			 CM.Skew=-CM.Skew; 
		            	 
		            	 }
		            	 if(st.contains("Range:")) {
		            		// System.out.println(st.substring(st.indexOf("Min"),st.indexOf("Max")).replaceAll("[^0-9\\\\.]+", ""));
		            		// System.out.println(st.substring(st.indexOf("Max")).replaceAll("[^0-9\\\\.]+", ""));
		            		
CM.Min=Double.parseDouble(st.substring(st.indexOf("Min"),st.indexOf("Max")).replaceAll("[^0-9\\\\.]+", ""));
CM.Max=Double.parseDouble(st.substring(st.indexOf("Max")).replaceAll("[^0-9\\\\.]+", ""));
    	
if(st.substring(st.indexOf("Min"),st.indexOf("Max")).contains("-")) // Parsing statement removes negative sign  
	 CM.Min=-CM.Min; 
if(st.substring(st.indexOf("Max")).contains("-"))
	CM.Max=-CM.Max;
		            	 }
		             }
		            
		             //System.out.println(CM);
		             boolean Error=false;
		             while ((st = stdError.readLine()) != null) {

			                 System.out.println(st);
			                 Error=true;
			                 
			             }
		             if(Error==true) {
		            	new Log().Error(this,"Cfft error: Please fix the above error. Probably, you are using wrong phases or wrong FP,SIGFP! (Example colinfo=FP,SIGFP Phases=HLA,HLB,HLC,HLD) ");
		             
		             }
		           
		         
		        

		          
		return CM;	 
			 
	}
}
