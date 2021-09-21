package PMBPP.Data.Preparation;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.json.simple.parser.ParseException;

import PMBPP.Log.Log;
import PMBPP.ML.Model.Parameters;
import PMBPP.Utilities.CSVReader;
import PMBPP.Utilities.StatisticalTests;
import PMBPP.Validation.CustomException;

/*
 * Preparing data for classification by finding the best value to use in splitting the data into two equal groups   
 */
public class ClassificationPreparerWithOptimizeClasses {

	public static void main(String[] args)
			throws IllegalArgumentException, IllegalAccessException, IOException, ParseException, CustomException {
		// TODO Auto-generated method stub

		Parameters.setLog ( "F");
		// Parameters.PearsonsCorrelation="T";
		String DataPath = "/Datasets/NO-NCS/";
		Parameters.setAttCSV("/PredictionModels/Completeness/Phenix.csv");			 // not effect if all pipelines use same													// features
		new ClassificationPreparerWithOptimizeClasses().Optimize(DataPath, "PredictedDatasets/PhenixHAL.csv");
		new ClassificationPreparer().Prepare(new File(DataPath).getAbsolutePath() + "/",
				"PredictedDatasets/PhenixHAL.csv");

	}

	public void Optimize(String DataPath, String CSV)
			throws IllegalArgumentException, IllegalAccessException, IOException, ParseException, CustomException {

		new Log().TxtInRectangle("Optimizing classes for classification");

		isValid(CSV, DataPath);

		for (String Header : Parameters.getMeasurementUnitsToPredict().split(",")) {
			double val = BestValueToSpilt(DataPath, CSV, Header, 1);// try 1

			new Log().Info(this, new File(CSV).getName() + "-" + Header + " best value to spilt " + val
					+ " ShannonValToAccept " + 1);

			if (val == -1) {

				val = BestValueToSpilt(DataPath, CSV, Header, 0.9);// if we are unable to get 1, then go to the second
																	// best
				new Log().Info(this, new File(CSV).getName() + "-" + Header + " best value to spilt " + val
						+ " ShannonValToAccept " + 0.9);

			}
			Parameters.setClassLevel(Header, val);
			Parameters.setMaxClassLevel(Header, val);// any class above this value then it will set to this value/class

		}

	}
	// Dot not pass Header as direct string. Use a variable instead. Very strange
	// error happens because an extra char is added which cause mismatch

	double BestValueToSpilt(String DataPath, String CSV, String Header, double ShannonValToAccept)
			throws IllegalArgumentException, IllegalAccessException, IOException, ParseException, CustomException {

		double Best = Integer.MAX_VALUE;
		if (Parameters.getPearsonsCorrelation().equals("T"))
			Best = 0;

		double BestLevel = -1;
		double i = 1;
		double increaseBy = 1;
		double max = 100;

		if (Header.contains("R-")) {
			i = 0.01;
			increaseBy = 0.01;
			max = 1;
		}
		for (; i <= max; i = i + increaseBy) {
			// System.out.println("i "+i);
			Parameters.setClassLevel(Header, i);
			new ClassificationPreparer().Prepare(DataPath, CSV);

			Vector<String> headers = new Vector<String>();
			headers.add(Header);

			HashMap<String, Vector<HashMap<String, String>>> map = new CSVReader().ReadIntoHashMapWithFilterdHeaders(
					Parameters.getClassificationDatasetsFolderName() + "/" + new File(CSV).getName(), "PDB", headers);

			HashMap<String, Integer> counted = CountInstanceInClasses(map);

			Vector<Double> NumberofDatasetsInFirstAndRest = PercentgeOfClassesInEqualSize(counted,
					counted.keySet().size());

			if (Parameters.getPearsonsCorrelation().equals("F")) {
				double CurrentBest = Math
						.abs(NumberofDatasetsInFirstAndRest.get(0) - NumberofDatasetsInFirstAndRest.get(1)); // 0 is the
																												// first
																												// class
																												// and 1
																												// is
																												// the
																												// rest
																												// of
																												// classes
				if (CurrentBest < Best) {
					Best = CurrentBest;
					BestLevel = Parameters.getClassLevel(Header);
				}
			}
			if (Parameters.getPearsonsCorrelation().equals("T")) {

				if (Shannon(NumberofDatasetsInFirstAndRest) == ShannonValToAccept) {

					if (CalculateStatisticalTest(counted.keySet().toArray(new String[counted.keySet().size()]), CSV,
							map, Header) > Best) {

						Best = CalculateStatisticalTest(counted.keySet().toArray(new String[counted.keySet().size()]),
								CSV, map, Header);
						BestLevel = Parameters.getClassLevel(Header);

					}
				}
			}

			FileUtils.deleteDirectory(new File(Parameters.getClassificationDatasetsFolderName()));
		}

		return BestLevel;
	}

	HashMap<String, Integer> CountInstanceInClasses(HashMap<String, Vector<HashMap<String, String>>> map) {
		HashMap<String, Integer> counted = new HashMap<String, Integer>();
		for (String ID : map.keySet()) {

			for (int i = 0; i < map.get(ID).size(); ++i) {

				for (String Header : map.get(ID).get(i).keySet()) {

					if (counted.containsKey(map.get(ID).get(i).get(Header))) {
						counted.put(map.get(ID).get(i).get(Header), counted.get(map.get(ID).get(i).get(Header)) + 1);
					} else {
						counted.put(map.get(ID).get(i).get(Header), 1);
					}

				}
			}
		}
		return counted;
	}

	Vector<Double> PercentgeOfClassesInEqualSize(HashMap<String, Integer> Diff, int NumOfClasses) {

		TreeMap<String, Integer> SortedMap = new TreeMap<>(new SortedByIntKeys());

		for (String ID : Diff.keySet()) { // to sort the map
			SortedMap.put(ID, Diff.get(ID));
		}

		double FirstClass = SortedMap.get(SortedMap.firstKey());
		SortedMap.remove(SortedMap.firstKey());
		double RestOfClasses = 0;
		for (String Class : SortedMap.keySet()) {
			RestOfClasses += SortedMap.get(Class);
		}
		Vector<Double> Temp = new Vector<Double>();
		Temp.add(FirstClass);
		Temp.add(RestOfClasses);
		return Temp;

	}

	void isValid(String CSV, String PathToDatasets) {

		if (!new File(CSV).exists()) {
			new Log().Error(this, "CSV file is not found (Maybe it is wrong directory!)");

		}
		if (!new File(PathToDatasets).exists()) {
			new Log().Error(this, "Datasets directory is not found  (Maybe it is wrong directory!)");

		}

	}

	double CalculateStatisticalTest(String[] Calsses, String PredcitedDatasetsCSV,
			HashMap<String, Vector<HashMap<String, String>>> ClassifedDatasetsForTraning, String Header)
			throws IOException {
		TreeMap<String, Vector<String>> GroupedPDBbyClass = new TreeMap<String, Vector<String>>(new SortedByIntKeys());

		// Getting PDB and grouped them depending on their classes
		for (int i = 0; i < Calsses.length; ++i) {
			Vector<String> Temp = new Vector<String>();
			for (String PDB : ClassifedDatasetsForTraning.keySet()) {
				if (ClassifedDatasetsForTraning.get(PDB).get(0).get(Header).equals(Calsses[i])) { // we use get(0)
																									// because there is
																									// the PDB IF here
																									// are unique (no
																									// multiple records
																									// with same PDB ID)
					Temp.add(PDB);
				}
			}
			GroupedPDBbyClass.put(Calsses[i], Temp);

		}

		// Getting predicted and actual values
		Vector<String> HeadersWanted = new Vector<String>();
		HeadersWanted.add(Header);
		HeadersWanted.add("Prediction");
		HashMap<String, Vector<HashMap<String, String>>> Prediction = new CSVReader()
				.ReadIntoHashMapWithFilterdHeaders(PredcitedDatasetsCSV, "PDB", HeadersWanted);

		Vector<String> Actual = new Vector<String>();
		Vector<String> Predicted = new Vector<String>();

		for (String PDB : GroupedPDBbyClass.get(GroupedPDBbyClass.firstKey())) {
			Vector<HashMap<String, String>> ActualAndPredicted = Prediction.get(PDB); // Two vectors one contains actual
																						// and the other contains
																						// Predicted value

			if (ActualAndPredicted.get(0).get("Prediction").equals("T"))
				Predicted.add(ActualAndPredicted.get(0).get(Header));
			if (ActualAndPredicted.get(1).get("Prediction").equals("F"))
				Actual.add(ActualAndPredicted.get(1).get(Header));
		}
		if (Actual.size() < 2 || Predicted.size() < 2) // can not calculate PC
			return 0;

		double ClassOnePC = new StatisticalTests().PC(Actual, Predicted);
		Vector<Double> TempActual = new Vector<Double>();
		Vector<Double> TempPredicted = new Vector<Double>();
		for (String s : Actual)
			TempActual.add(Double.parseDouble(s));
		for (String s : Predicted)
			TempPredicted.add(Double.parseDouble(s));

		// double anovaForForstClass=Double.parseDouble(new
		// StatisticalTests().anova(TempActual, TempPredicted));
		Actual.clear();
		Predicted.clear();
		GroupedPDBbyClass.remove(GroupedPDBbyClass.firstKey()); // remove first class we do not need it

		for (String Class : GroupedPDBbyClass.keySet()) {
			for (String PDB : GroupedPDBbyClass.get(Class)) {
				Vector<HashMap<String, String>> ActualAndPredicted = Prediction.get(PDB);
				if (ActualAndPredicted.get(0).get("Prediction").equals("T"))
					Predicted.add(ActualAndPredicted.get(0).get(Header));
				if (ActualAndPredicted.get(1).get("Prediction").equals("F"))
					Actual.add(ActualAndPredicted.get(1).get(Header));
			}
		}
		if (Actual.size() < 2 || Predicted.size() < 2) // can not calculate PC
			return 0;

		double RestofClassesPC = new StatisticalTests().PC(Actual, Predicted);

		TempActual.clear();
		TempPredicted.clear();
		for (String s : Actual)
			TempActual.add(Double.parseDouble(s));
		for (String s : Predicted)
			TempPredicted.add(Double.parseDouble(s));

		/*
		 * double anovaForRestClasses=Double.parseDouble(new
		 * StatisticalTests().anova(TempActual, TempPredicted));
		 * 
		 * if(Parameters.AnovaForClassification.equals("T")) { if(anovaForForstClass >
		 * 0.05 && anovaForRestClasses <=0.05) return true; else return false; }
		 */

		// Or PC
		/*
		 * if((((ClassOnePC-RestofClassesPC)*100)/ClassOnePC) >=20)
		 * 
		 * return true; else return false;
		 */
		return (((ClassOnePC - RestofClassesPC) * 100) / ClassOnePC);

	}

	double Shannon(Vector<Double> NumberofDatasetsInFirstAndRest) {
		// Shannon entropy
		// https://stats.stackexchange.com/questions/239973/a-general-measure-of-data-set-imbalance
		double n = NumberofDatasetsInFirstAndRest.get(0) + NumberofDatasetsInFirstAndRest.get(1);

		double k = NumberofDatasetsInFirstAndRest.size();
		double H = 0;

		H += (NumberofDatasetsInFirstAndRest.get(0) / n) * Math.log(NumberofDatasetsInFirstAndRest.get(0) / n);
		H += (NumberofDatasetsInFirstAndRest.get(1) / n) * Math.log(NumberofDatasetsInFirstAndRest.get(1) / n);

		H = -H;

		if (!Double.isNaN(H / Math.log(k))) {

			return new BigDecimal(H / Math.log(k)).setScale(1, RoundingMode.HALF_UP).doubleValue();
		}
		return 0;

	}

	public static class SortedByIntKeys implements Comparator<String> {
		public int compare(String o1, String o2) {
			o1 = o1.replace("±", "");
			o2 = o2.replace("±", "");
			BigDecimal v1 = new BigDecimal(o1);
			BigDecimal v2 = new BigDecimal(o2);

			return v1.compareTo(v2);
		}
	}

}
