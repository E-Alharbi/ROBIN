package Data.Preparation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;



public class cfft {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
new cfft().Cfft("/Volumes/PhDHardDrive/EditorsRevision-2/Datasets/NO-NCS/1o6a-1.9-parrot-noncs.mtz");
	}

	public CfftMeasures Cfft(String mtzin) throws IOException {
		
	 String st = null;
	 String Chltofom=System.getenv("CCP4")+"/share/python/CCP4Dispatchers/cfft.py";
	 String[]callAndArgs= {"python",
			 Chltofom,
	"-mtzin",mtzin,
	"-colin-fo","FP,SIGFP",
	"-colin-hl","HLA,HLB,HLC,HLD",
	//"-colin-hl","parrot.ABCD.A,parrot.ABCD.B,parrot.ABCD.C,parrot.ABCD.D",
	"-stats",
	};
	 
	Process p = Runtime.getRuntime().exec(callAndArgs);

		             

	BufferedReader stdInput = new BufferedReader(new 

		                  InputStreamReader(p.getInputStream()));



		             BufferedReader stdError = new BufferedReader(new 

		                  InputStreamReader(p.getErrorStream()));



		             CfftMeasures CM= new CfftMeasures();
		             while ((st = stdInput.readLine()) != null) {
		            	// System.out.println(st);
		            	 if(st.contains("About mean (rmsd)")) {
		            		// System.out.println(st.substring(st.indexOf("About mean (rmsd)")).replaceAll("[^0-9\\\\.]+", ""));
		            	
		            		 CM.RMSD=Double.parseDouble(st.substring(st.indexOf("About mean (rmsd)")).replaceAll("[^0-9\\\\.]+", ""));
		            		 if(st.substring(st.indexOf("About mean (rmsd)")).contains("-")) // Parsing statement removes negative sign  
		            			 CM.RMSD=-CM.RMSD; 
		            	 }
		            	 if(st.contains("About mean (skew)")) {
		            		// System.out.println(st.substring(st.indexOf("About mean (skew)")).replaceAll("[^0-9\\\\.]+", ""));
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
		             while ((st = stdError.readLine()) != null) {

			                 System.out.println(st);

			             }
		             
		           
		         
		        

		          
		return CM;	 
			 
	}
}
