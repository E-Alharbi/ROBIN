package PMBPP.Data.Preparation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import PMBPP.Log.Log;

public class mtzdump {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String[] command = {
                "/bin/bash" , "source", "/Applications/ccp4-7.0/setup-scripts/ccp4.setup-sh"};
		
		Runtime.getRuntime().exec(command);
		
		new mtzdump().GetReso("/Volumes/PhDHardDrive/MRDatasets/DatasetsForBuilding/4M9H-1.9-parrot-noncs.mtz");
	}

	public double GetReso(String mtzin) throws IOException {
		
		 String st = null;
		 String Chltofom=System.getenv("CCP4")+"/share/python/CCP4Dispatchers/mtzdump.py";
		 String[]callAndArgs= {"python",Chltofom,"hklin",mtzin};
		 
		String EOF="<<< EOF";
		Process p = new ProcessBuilder(callAndArgs).start();
		OutputStream out = p.getOutputStream();
		out.write(EOF.getBytes());
		out.close();	             

		BufferedReader stdInput = new BufferedReader(new 

			                  InputStreamReader(p.getInputStream()));



			             BufferedReader stdError = new BufferedReader(new 

			                  InputStreamReader(p.getErrorStream()));



			            double Reso=-1;
			            String Resolution="";
			            boolean collectTxt=false;
			             while ((st = stdInput.readLine()) != null) {
			            	// System.out.println(st);
			            	 if(st.contains("*  Resolution Range :"))
			            		 collectTxt=true;
			            	 if(st.contains("* Sort Order :"))
			            		 collectTxt=false;
			            	 if(collectTxt==true)
			            		 Resolution+=st;

			             }
			           //  System.out.println(Resolution);
			             //System.out.println(CM);
			             while ((st = stdError.readLine()) != null) {

				                 System.out.println(st);

				             }
			             
			if(Resolution.trim().length()!=0)     
			Reso=Double.parseDouble(Resolution.split("-")[1].split("A")[0]);
			
			        
	if(Reso==-1) {
		new Log().Error(this,"unable to parse resolution");
		
	}   

			return Reso;	 
				 
		}
}
