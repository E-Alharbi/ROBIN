package ROBIN.Utilities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import ROBIN.Data.Preparation.ExcelContentsWithFeatures;
import ROBIN.Log.Log;
import ROBIN.ML.Model.Parameters;
import weka.core.Instances;

/*
 * Writing from HashMap to CSV
 */
public class CSVWriter {

	public String PathToSaveCSV = "./";

	// Ex HashMap {1o6a,{R-free,0.20,R-work,0.10,Com,20}}
	public void WriteFromHashMap(HashMap<String, LinkedHashMap<String, String>> CSVContents, String Name, String FirstColTitle )
			throws FileNotFoundException {
		Vector<String> MeasurementUnitsHeaders = new Vector<String>();
		for (String PDB : CSVContents.keySet()) {

			for (String Key : CSVContents.get(PDB).keySet()) { // give each an index R-free index 0 R-work 1 ... etc

				if (!MeasurementUnitsHeaders.contains(Key))
					MeasurementUnitsHeaders.add(Key);
			}

		}
		String CSV = FirstColTitle;

		for (int i = 0; i < MeasurementUnitsHeaders.size(); ++i)// add all headers
			CSV += "," + MeasurementUnitsHeaders.get(i);
		CSV += "\n";
		
		for (String PDB : CSVContents.keySet()) {
			int HeaderIndex = 0;

			String Record1 = PDB;

			for (String Key : CSVContents.get(PDB).keySet()) {
				if (MeasurementUnitsHeaders.get(HeaderIndex).equals(Key)) { // check the headers order because hashmaps
																			// are unsorted

					Record1 += "," + CSVContents.get(PDB).get(Key);
					HeaderIndex++;
				} else {// very rare to happen
					System.out.println("Error: Can not continue because there is a change in the headers order!  ");
					System.exit(-1);
				}
			}

			CSV += Record1;
			CSV += "\n";

		}
		//try (PrintWriter out = new PrintWriter(Parameters.getPrefix()+Name)) {
	//		out.println(CSV);
	//	}
		new TxtFiles().WriteStringToTxtFile(Name, CSV);
	}
	  
	
	public void WriteFromHashMapContainsRepatedRecord(HashMap<String, Vector<HashMap<String, String>>> CSVContents,
			String Name,String FirstColTitle, boolean IgnoreReaptedRecord) throws IOException {
		if(IgnoreReaptedRecord==true) {
		HashMap<String, LinkedHashMap<String, String>> CSV = ConvertToNoneReaptedRecord(CSVContents);
		WriteFromHashMap(CSV, Name,FirstColTitle);
		}
		else {
			Vector<String> Headers= new Vector<String>();
			for(String p : CSVContents.keySet()) {
				for(int i=0; i < CSVContents.get(p).size();++i) {
					for(String k : CSVContents.get(p).get(i).keySet()) {
						if(!Headers.contains(k)) {
							Headers.add(k);
						}
					}
				}
			}
			
			
			String CSV=FirstColTitle;
			for(int i=0  ; i < Headers.size();++i) {
				CSV+=","+Headers.get(i);
			}
			CSV+="\n";
			for(String p : CSVContents.keySet()) {
				for(int i=0; i < CSVContents.get(p).size();++i) {
					CSV+=p;
					for(int h=0 ; h < Headers.size(); ++h) {
						CSV+=","+CSVContents.get(p).get(i).get(Headers.get(h));
					}
					CSV+="\n";
				}
			}
			new TxtFiles().WriteStringToTxtFile(Name, CSV);
		}
	}
    
	HashMap<String, LinkedHashMap<String, String>> ConvertToNoneReaptedRecord(
			HashMap<String, Vector<HashMap<String, String>>> CSVContentswithMultipleRecords) throws IOException {
		HashMap<String, LinkedHashMap<String, String>> CSVContents = new HashMap<String, LinkedHashMap<String, String>>();

		for (String PDB : CSVContentswithMultipleRecords.keySet()) {

			LinkedHashMap<String, String> temp = new LinkedHashMap<String, String>();
			if (CSVContentswithMultipleRecords.get(PDB).size() > 1) {
				new Log().Warning(this,
						"This CSV contains two records or more with same headr ID. Only one will be taken. Please check if this not effect your final results");
			}
			for (String key : CSVContentswithMultipleRecords.get(PDB).get(0).keySet()) {
				temp.put(key, CSVContentswithMultipleRecords.get(PDB).get(0).get(key));
			}
			CSVContents.put(PDB, temp);
		}
		return CSVContents;
	}

	public void WriteToCSV(Vector<ExcelContentsWithFeatures> Excel, String Pipeline)
			throws FileNotFoundException, IllegalArgumentException, IllegalAccessException {

		String[] features = Parameters.getFeatures().split(",");
		String[] MeasurementUnitsToPredict = Parameters.getMeasurementUnitsToPredict().split(",");

		String CSV = Parameters.getFeatures() + "," + Parameters.getMeasurementUnitsToPredict() + ",PDB\n";// headers

		for (ExcelContentsWithFeatures E : Excel) {

			for (int i = 0; i < features.length; ++i) {
				if (i + 1 < features.length)
					CSV += E.CM.GetFeatureByName(features[i]) + ",";
				else
					CSV += E.CM.GetFeatureByName(features[i]);
			}
			for (int i = 0; i < MeasurementUnitsToPredict.length; ++i) {
				if (MeasurementUnitsToPredict[i].equals("Completeness"))
					CSV += "," + E.Completeness;
				if (MeasurementUnitsToPredict[i].equals("R-free"))
					CSV += "," + E.R_free0Cycle;
				if (MeasurementUnitsToPredict[i].equals("R-work"))
					CSV += "," + E.R_factor0Cycle;

			}
			CSV += "," + E.PDB_ID + "\n";
			// CSV+=E.CM.RMSD+","+E.CM.Skew+","+E.Resolution+","+E.CM.Max+","+E.CM.Min+","+E.Completeness+","+E.R_free0Cycle+","+E.R_factor0Cycle+"\n";
		}
		//try (PrintWriter out = new PrintWriter(
		//		PathToSaveCSV + "/" + Parameters.getPrefix()+Pipeline.substring(0, Pipeline.indexOf(".")) + ".csv")) {
		//	out.println(CSV);
		//}
		new TxtFiles().WriteStringToTxtFile(PathToSaveCSV + "/" + Pipeline.substring(0, Pipeline.indexOf(".")) + ".csv", CSV);

	}

	public void WriteInstancesToCSV(Instances dataset, String Name) throws FileNotFoundException {
		String CSV = "";
		for (int i = 0; i < dataset.numAttributes(); ++i) {
			if (i + 1 < dataset.numAttributes())
				CSV += dataset.attribute(i).name() + ",";
			else
				CSV += dataset.attribute(i).name();
		}
		CSV += "\n";
		for (int n = 0; n < dataset.numInstances(); ++n) {
			String Record = "";
			for (int i = 0; i < dataset.numAttributes(); ++i) {
				String Val = "";
				// if(dataset.attribute(i).name().equals("PDB")){
				// Val=dataset.get(n).stringValue(i);
				// }
				// else {
				// Val=String.valueOf(dataset.get(n).value(i));
				// Val=dataset.get(n).stringValue(i);

				// }

				if (dataset.get(n).attribute(i).isNominal())
					Val = dataset.get(n).stringValue(i);
				if (dataset.get(n).attribute(i).isNumeric())
					Val = String.valueOf(dataset.get(n).value(i));

				if (i + 1 < dataset.numAttributes()) {
					Record += Val + ",";
				} else {
					Record += Val;
				}
			}
			CSV += Record + "\n";
		}
		//try (PrintWriter out = new PrintWriter(Parameters.getPrefix()+Name + ".csv")) {
		//	out.println(CSV);
		//}
		new TxtFiles().WriteStringToTxtFile(Name, CSV);
	}
}
