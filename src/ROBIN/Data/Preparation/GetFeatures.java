package ROBIN.Data.Preparation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;

import org.apache.commons.io.FilenameUtils;
import org.json.simple.parser.ParseException;

import ROBIN.Log.Log;
import ROBIN.ML.Model.Parameters;
import ROBIN.Utilities.CSVReader;
import ROBIN.Utilities.FilesUtilities;
import ROBIN.Utilities.MTZReader;

/*
 * Reading features into features object 
 */
public class GetFeatures {
	public static void main(String[] args) throws IOException, NumberFormatException, IllegalArgumentException,
			IllegalAccessException, ParseException {
		// TODO Auto-generated method stub

		// Example

		for (File m : new FilesUtilities().FilesByExtension("/Datasets/NO-NCS",".mtz")) {
			new Log().Info(new GetFeatures(), m.getName());
			new GetFeatures().Get(m.getAbsolutePath());

		}

	}

	public Features Get(String mtz) throws IOException, NumberFormatException, IllegalArgumentException,
			IllegalAccessException, ParseException {
		Features features = new Features();
		String mtzName = new File(mtz).getName().replaceAll("." + FilenameUtils.getExtension(new File(mtz).getName()),
				"");
		boolean ReadFromCSV = true;
		if (new File("features.csv").exists()) {
			HashMap<String, Vector<HashMap<String, String>>> map = new CSVReader().ReadIntoHashMap("features.csv",
					"PDB");
			for (String PDB : map.keySet()) {
				if (PDB.equals(mtzName)) {
					for (int i = 0; i < map.get(PDB).size(); ++i) {
						for (String Col : map.get(PDB).get(i).keySet()) {
							features.SetFeatureByName(Col, Double.parseDouble(map.get(PDB).get(i).get(Col)));
						}
					}
					break;
				}
			}
		}
		if (!new File("features.csv").exists() || features.isEmpty()) { // empty when the mtz not in the csv file
			ReadFromCSV = false;
			
			features = new cfft().Cfft(new File(mtz).getAbsolutePath());

			features.Resolution = new mtzdump().GetReso(new File(mtz).getAbsolutePath());
			//features.Resolution = Double.parseDouble( new MTZReader(new File(mtz).getAbsolutePath() ).GetResolution() ) ;
			
			if (Parameters.getMR().equals("T")) {
				SetSequenceIdentity(features, mtz);
			}

		}

		if (ReadFromCSV == true)
			new Log().Info(this, "features have read from CSV file: " + mtzName);
		else
			new Log().Info(this, "features have read from cfft: " + mtzName);

		return features;
	}

	public Features GetFeaturesFromCSV(String mtz)
			throws IOException, NumberFormatException, IllegalArgumentException, IllegalAccessException {
		Features features = new Features();
		String MTZ = new File(mtz).getName().replaceAll("." + FilenameUtils.getExtension(new File(mtz).getName()), "");
		HashMap<String, Vector<HashMap<String, String>>> map = new CSVReader().ReadIntoHashMap(MTZ + "features.csv",
				"PDB");
		for (int i = 0; i < map.get(MTZ).size(); ++i) {
			for (String Col : map.get(MTZ).get(i).keySet()) {
				features.SetFeatureByName(Col, Double.parseDouble(map.get(MTZ).get(i).get(Col)));
			}
		}
		return features;
	}

	public double[] GetUsingFeatures(String mtz)
			throws IOException, IllegalArgumentException, IllegalAccessException, ParseException {

		Features features = Get(mtz);
		if (Parameters.getMR().equals("T")) {
			SetSequenceIdentity(features, mtz);
		}
		// check used features from AttCSV
		updatefromAttCSV();

		return FeaturesToarray(features);

	}

	public double[] GetUsingFeatures(String mtz, double Resolution)
			throws IOException, IllegalArgumentException, IllegalAccessException, ParseException {
		Features features = new cfft().Cfft(new File(mtz).getAbsolutePath());
		features.Resolution = Resolution;
		if (Parameters.getMR().equals("T")) {
			SetSequenceIdentity(features, mtz);
		}
		// check used features from AttCSV
		updatefromAttCSV();

		return FeaturesToarray(features);
	}

	double[] FeaturesToarray(Features features) throws IllegalArgumentException, IllegalAccessException {
		double[] featuresToarray = new double[Parameters.getFeatures().split(",").length];
		for (int i = 0; i < Parameters.getFeatures().split(",").length; ++i) {

			if (features.GetFeatureByName(Parameters.getFeatures().split(",")[i]) != null)
				featuresToarray[i] = (double) features.GetFeatureByName(Parameters.getFeatures().split(",")[i]);
		}
		return featuresToarray;
	}

	public LinkedHashMap<String, String> GetUsingFeaturesInHashMap(String mtz)
			throws IOException, IllegalArgumentException, IllegalAccessException, ParseException {
		Features features = Get(mtz);

		if (Parameters.getMR().equals("T")) {
			SetSequenceIdentity(features, mtz);
		}
		// check used features from AttCSV
		updatefromAttCSV();

		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		for (int i = 0; i < Parameters.getFeatures().split(",").length; ++i) {

			if (features.GetFeatureByName(Parameters.getFeatures().split(",")[i]) != null)
				map.put(Parameters.getFeatures().split(",")[i],
						String.valueOf(features.GetFeatureByName(Parameters.getFeatures().split(",")[i])));
		}

		return map;

	}

	void updatefromAttCSV() throws IOException {
		BufferedReader att;

		att = new BufferedReader(new FileReader(Parameters.getAttCSV()));
		
		Parameters.setFeatures ( att.readLine());

		// remove MeasurementUnitsToPredict from first line in CSV
		String[] features = Parameters.getFeatures().split(",");
		Parameters.setFeatures(  "");
		for (String feature : features) {
			if (!Parameters.getMeasurementUnitsToPredict().contains(feature)) {
				Parameters.setFeatures(Parameters.getFeatures()+feature + ",") ;
			}
		}
		Parameters.setFeatures ( Parameters.getFeatures().substring(0, Parameters.getFeatures().length() - 1));// remove last comma

		att.close();
	}

	void SetSequenceIdentity(Features features, String mtz)
			throws NumberFormatException, FileNotFoundException, IOException, ParseException {
		String mtzName = new File(mtz).getName().replaceAll("." + FilenameUtils.getExtension(new File(mtz).getName()),
				"");

		if (Parameters.getSequenceIdentity().equals("-1")) {
			if (new File(new File(mtz).getParent() + "/" + mtzName + ".json").exists()) {
				features.SequenceIdentity = Double.parseDouble(new ROBIN.Utilities.JSONReader()
						.JSONToHashMap(new File(mtz).getParent() + "/" + mtzName + ".json").get("gesamt_seqid"));
			} else {

				new Log().Error(this,
						"It seems that you do not type in the sequence identity and no json file was found. Please type in the sequence identity using SequenceIdentity=Val ");

			}
		} else {
			features.SequenceIdentity = Double.parseDouble(Parameters.getSequenceIdentity());
		}

	}
}
