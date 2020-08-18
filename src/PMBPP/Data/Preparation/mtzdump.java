package PMBPP.Data.Preparation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Vector;

import org.apache.commons.io.FilenameUtils;
import org.json.simple.parser.ParseException;

import PMBPP.Log.Log;
import PMBPP.ML.Model.Parameters;
import PMBPP.Utilities.FilesUtilities;

public class mtzdump {
	/*
	 * Parsing resolution from ccp4 mtzdump tool
	 */
	public static void main(String[] args) throws IOException, NumberFormatException, ParseException {

		// Example
		String[] command = { "/bin/bash", "source", "/ccp4-7.0/setup-scripts/ccp4.setup-sh" };

		Runtime.getRuntime().exec(command);

		new mtzdump().GetReso("/Volumes/PhDHardDrive/EditorsRevision-2/Datasets/NO-NCS/2awa-2.7-parrot-noncs.mtz");
	/*
		File [] dataset = new FilesUtilities().ReadMtzList("/Volumes/PhDHardDrive/MRDatasets/DatasetsForBuilding");
		//File [] dataset = new FilesUtilities().ReadMtzList("/Volumes/PhDHardDrive/EditorsRevision-2/Datasets/NO-NCS");

		Parameters.Log="F";
		//System.out.println(dataset.length);
		int count=0;
		for(File mtz : dataset ) {
			String mtzName = mtz.getName().replaceAll("." + FilenameUtils.getExtension(mtz.getName()),"");
			//System.out.println(mtzName);
			//double Reso = Double.parseDouble(new PMBPP.Utilities.JSONReader()
				//	.JSONToHashMap(mtz.getParent() + "/" + mtzName + ".json").get("data_resolution"));
			
			//double Resodump=new mtzdump().GetReso(mtz.getAbsolutePath());
			//System.out.println(Resodump);
			double Resodump = BigDecimal.valueOf(new mtzdump().GetReso(mtz.getAbsolutePath()))
					.setScale(1, RoundingMode.HALF_UP).doubleValue();
			
		
			
			
			double Reso = BigDecimal.valueOf(Double.parseDouble(new PMBPP.Utilities.JSONReader()
					.JSONToHashMap(mtz.getParent() + "/" + mtzName + ".json").get("data_resolution")))
					.setScale(1, RoundingMode.HALF_UP).doubleValue();
			
			if(Reso!=Resodump) {
				System.out.println("PDB: "+mtz.getName().substring(0,4));
				System.out.println("Correct: "+Resodump);
				System.out.println("Wrong: "+Reso);
				count++;
			}
				
			
			

		}
		System.out.println(count);
		*/
	}

	public double GetReso(String mtzin) throws IOException {

		String st = null;
		String Chltofom = System.getenv("CCP4") + "/share/python/CCP4Dispatchers/mtzdump.py";
		String[] callAndArgs = { "python", Chltofom, "hklin", mtzin };

		String EOF = "<<< EOF";
		Process p = new ProcessBuilder(callAndArgs).start();
		OutputStream out = p.getOutputStream();
		out.write(EOF.getBytes());
		out.close();

		BufferedReader stdInput = new BufferedReader(new

		InputStreamReader(p.getInputStream()));

		BufferedReader stdError = new BufferedReader(new

		InputStreamReader(p.getErrorStream()));

		double Reso = -1;
		String Resolution = "";
		boolean collectTxt = false;
		HashMap<String,HashMap<String,Double>> ColumnAndReso = new HashMap<String,HashMap<String,Double>>();
		boolean TableHasStarted = false;
		while ((st = stdInput.readLine()) != null) {
			if(TableHasStarted==true) {
				//System.out.println(st);
				//System.out.println(SplitLineBySpace(st).size());
				if(SplitLineBySpace(st).size()>=3) { // at least three columns 
					Vector<String> LineVal=	SplitLineBySpace(st);
					HashMap<String,Double> TypeAndReso= new HashMap<String,Double>();
					TypeAndReso.put(LineVal.get(LineVal.size()-2), Double.parseDouble(LineVal.get(LineVal.size()-3)));
					ColumnAndReso.put(LineVal.get(LineVal.size()-1), TypeAndReso);
				}
				if(ColumnAndReso.size()!=0 && SplitLineBySpace(st).size()==0)// end of table
					break;
				
				
			}
			if(st.contains(" label ")) {
			//System.out.println(st);
			TableHasStarted=true;
			}
			if (st.contains("*  Resolution Range :"))
				collectTxt = true;
			if (st.contains("* Sort Order :"))
				collectTxt = false;
			if (collectTxt == true)
				Resolution += st;

		}

		while ((st = stdError.readLine()) != null) {

			System.out.println(st);

		}

		if (Resolution.trim().length() != 0)
			Reso = Double.parseDouble(Resolution.split("-")[1].split("A")[0]);

		if (Reso == -1) {
			new Log().Error(this, "unable to parse resolution");

		}
		
		
	
		double FColumnReso=-1;
		
		for(String Label : ColumnAndReso.keySet()) {
			if(ColumnAndReso.get(Label).containsKey("F")) {
				FColumnReso=ColumnAndReso.get(Label).get("F");
			}
			
			
			
		}
		
		if(FColumnReso!=-1) {
			if(FColumnReso!=Reso) {
				new Log().Warning(this, "The columns have different resolutions. We will use the resolution from column type 'F':"+FColumnReso);
				Reso=FColumnReso;
			}
		}
		else {
			new Log().Error(this, "Your data columns do not contain column type 'F' ");

		}
		new Log().Info(this, "Resolution " + Reso);
		return Reso;

	}
	Vector<String> SplitLineBySpace(String Line){
		Vector<String> LineVal= new Vector<String>();
		String Word="";
		for(int i=0 ; i <Line.length();++i ) {
			if(Line.charAt(i)!=' ') {
				Word+=Line.charAt(i);
			}
			else {
				if(Word.trim().length()!=0)
					LineVal.add(Word);
				Word="";
			}
		}
		if(Word.trim().length()!=0) // added the last word 
			LineVal.add(Word);
		return LineVal;
	}
}
