package ROBIN.Prediction.Analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Vector;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;

import ROBIN.Log.Log;
import ROBIN.ML.Model.ROBIN;
import ROBIN.ML.Model.Parameters;
import ROBIN.Utilities.CSVReader;
import ROBIN.Utilities.CSVWriter;
import ROBIN.Utilities.FilesUtilities;
import ROBIN.Utilities.StatisticalTests;
import ROBIN.Utilities.TxtFiles;

/*
 * Generating latex tables for Statistical Tests. Oneway Anova and T-test. Also mean and SD
 */

public class ModelPerformance {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
	    Parameters.setMR("T");
		Parameters.setFeatures ( "RMSD,Skew,Resolution,Max,Min,SequenceIdentity");
		
		
		
		//Parameters.setHeatmap("F");
		
		//new ModelPerformance().SplitOnMeasurementUnitsLevel("/Volumes/PhDHardDrive/PMBPP/FinalTraining/PMBPPResults/MR/CSVToUseInStatisticalTest");
		//new ModelPerformance().OmitTrainingdata("CSVToUseInStatisticalTestSplitted",
		//		"/Volumes/PhDHardDrive/PMBPP/FinalTraining/PMBPPResults/MR/TrainAndTestDataPredictionModels"); // CSVToUseInStatisticalTestSplitted will be created by
														// SplitOnMeasurementUnitsLevel
		
		new ModelPerformance().GroupedByResolution("/Users/emadalharbi/Downloads/MRWithShelxePhenixNoRebuild/CSVToUseInStatisticalTestFiltered");// CSVToUseInStatisticalTestFiltered
																						// will be created by
																						// OmitTrainingdata

		// new
		// StatisticalTest().OmitTrainingdata("ClassifedDatasets","TrainAndTestDataClassificationModels");

	}

	public void SplitOnMeasurementUnitsLevel(String PathToCSV) throws IOException {

		for (File CSV : new FilesUtilities().ReadFilesList(PathToCSV)) {
			HashMap<String, Boolean> features = new CSVReader(CSV.getAbsolutePath()).FilterByFeatures( true);
			HashMap<String, Boolean> MeasurementsUnits = new CSVReader(CSV.getAbsolutePath()).FilterByFeatures( false);
			for (String unit : MeasurementsUnits.keySet()) {
				Vector<String> Headers = new Vector<String>(features.keySet());
				if (Parameters.getMeasurementUnitsToPredict().contains(unit)) {
					Headers.add(unit);
					Headers.add("Achieved" + unit);
					String CSVName = CSV.getName().replaceAll("." + FilenameUtils.getExtension(CSV.getName()), "");
					ROBIN.CheckDirAndFile("CSVToUseInStatisticalTestSplitted");
					HashMap<String, Vector<HashMap<String, String>>> filteredCSV = new CSVReader(CSV.getAbsolutePath())
							.ReadIntoHashMapWithFilterdHeaders( "PDB", Headers);
					new CSVWriter().WriteFromHashMapContainsRepatedRecord(filteredCSV,
							"CSVToUseInStatisticalTestSplitted/" + CSVName + "-" + unit + ".csv","PDB",true);

				}
			}
		}
	}

	public void OmitTrainingdata(String PathToCSV, String PathToTestCSV) throws IOException {
		for (File CSV : new FilesUtilities().ReadFilesList(PathToCSV)) {
			String CSVName = CSV.getName().replaceAll("." + FilenameUtils.getExtension(CSV.getName()), "");

			for (File TestCSV : new FilesUtilities().ReadFilesList(PathToTestCSV)) {
				if ((CSVName + "-test.csv").equals(TestCSV.getName())) {

					ROBIN.CheckDirAndFile("CSVToUseInStatisticalTestFiltered");
					new CSVWriter().WriteFromHashMapContainsRepatedRecord(omit(CSV, TestCSV),
							"CSVToUseInStatisticalTestFiltered/" + CSV.getName(),"PDB",true);

				}
			}
		}

	}

	public HashMap<String, Vector<HashMap<String, String>>> omit(File CSV, File TestCSV) throws IOException {

		HashMap<String, HashMap<String, String>> Testdata = new CSVReader(TestCSV.getAbsolutePath())
				.ReadIntoHashMapWithnoIDHeader();
		HashMap<String, Vector<HashMap<String, String>>> StatisticalTestdata = new CSVReader(CSV.getAbsolutePath())
				.ReadIntoHashMap( "PDB");
		HashMap<String, Vector<HashMap<String, String>>> FilteredStatisticalTestdata = new HashMap<String, Vector<HashMap<String, String>>>();
		int count = 0;

		for (String ID : Testdata.keySet()) {

			for (String STID : StatisticalTestdata.keySet()) {

				Vector<Boolean> Found = new Vector<Boolean>();
				for (String Header : Testdata.get(ID).keySet()) {

					if (Parameters.getFeatures().contains(Header))
						for (String STHeader : StatisticalTestdata.get(STID).get(0).keySet()) {
							if (Header.equals(STHeader) && Testdata.get(ID).get(Header)
									.equals(StatisticalTestdata.get(STID).get(0).get(STHeader))) {
								Found.add(true);

								break;
							}
						}
				}
				if (Found.size() == Testdata.get(ID).keySet().size() - 1) {// we do not need to compare the correct
																			// class/label. Only the features are needed

					count++;
					FilteredStatisticalTestdata.put(STID, StatisticalTestdata.get(STID));
				}
			}

		}

		if (count != Testdata.size()) {
			new Log().Error(this, "We cannot identify all the test data. " + count + " instances are found out of "
					+ Testdata.size() + ". This will lead to wrong statistical test results. Please review your data!");
		}
		if (count != FilteredStatisticalTestdata.size()) {
			new Log().Error(this, "We cannot identify all the test data. " + count + " instances are found and "
					+ FilteredStatisticalTestdata.size()
					+ " are stored. This will lead to wrong statistical test results. This might happen when you have duplicate instances in test data");
		}
		return FilteredStatisticalTestdata;
	}

	public void GroupedByResolution(String PathToCSV) throws IOException {
		TreeMap<String, String> ResoGroups = new TreeMap<String, String>();
		TreeMap<String, String> ResoHeaders = new TreeMap<String, String>();
		TreeMap<String, TreeMap<String, TreeMap<String, String>>> Pipelines = new TreeMap<String, TreeMap<String, TreeMap<String, String>>>();
		for (File file : new FilesUtilities().ReadFilesList(PathToCSV)) {
			HashMap<String, Vector<HashMap<String, String>>> CSV = new CSVReader(file.getAbsolutePath())
					.ReadIntoHashMap( "PDB");
			HashMap<String, HashMap<String, Vector<HashMap<String, String>>>> Groups = new HashMap<String, HashMap<String, Vector<HashMap<String, String>>>>();
			for (String PDB : CSV.keySet()) {
				if (CSV.get(PDB).size() > 1) {
					new Log().Error(this,
							"CSV contains more than one record with same HeaderID name. This will lead to wrong statistical results. So, we cannot continue. To solve this, you need to check the CSV file and make sure it does not contins multiple records with same HeaderID.");
				}
				for (String Header : CSV.get(PDB).get(0).keySet()) {
					if (Header.equals("Resolution")) {
						DecimalFormat df = new DecimalFormat("#.#");
						df.setRoundingMode(RoundingMode.HALF_UP);

						// String
						// Resolution=df.format(Double.parseDouble(CSV.get(PDB).get(0).get(Header)));
						String Resolution = CSV.get(PDB).get(0).get(Header);// Grouping as two decimal places

						String Bin = "";
						double Reso = BigDecimal.valueOf(Double.parseDouble(Resolution))
								.setScale(1, RoundingMode.HALF_UP).doubleValue();

						Bin=ClassifyResolution(Reso);

						
						if (Groups.containsKey(Bin)) {
							HashMap<String, Vector<HashMap<String, String>>> Temp = Groups.get(Bin);
							Temp.put(PDB, CSV.get(PDB));
							Groups.put(Bin, Temp);
						} else {
							HashMap<String, Vector<HashMap<String, String>>> Temp = new HashMap<String, Vector<HashMap<String, String>>>();
							Temp.put(PDB, CSV.get(PDB));
							Groups.put(Bin, Temp);
						}
					}
				}
			}
			Parameters.setLog ( "F");
			TreeMap<String, TreeMap<String, String>> StatisticalMeasures = new TreeMap<String, TreeMap<String, String>>();

			for (String Key : Groups.keySet()) {
				ResoGroups.put(Key, Key);
				ResoHeaders.put(Key + "(" + Groups.get(Key).size() + ")", Key + "(" + Groups.get(Key).size() + ")");// with
																													// number
																													// of
																													// datasets

				HashMap<String, Boolean> MeasurementsUnits = new CSVReader(file.getAbsolutePath()).FilterByFeatures(
						false);

				for (String Measure : MeasurementsUnits.keySet()) {
					if (Parameters.getMeasurementUnitsToPredict().contains(Measure)) {

						Vector<Double> Var1 = new Vector<Double>();
						Vector<Double> Var2 = new Vector<Double>();

						for (String PDB : Groups.get(Key).keySet()) {
							for (String Header : Groups.get(Key).get(PDB).get(0).keySet()) {
								if (Header.equals(Measure)) {
									Var1.add(Double.parseDouble(Groups.get(Key).get(PDB).get(0).get(Header)));
								}
								if (Header.equals("Achieved" + Measure)) {
									Var2.add(Double.parseDouble(Groups.get(Key).get(PDB).get(0).get(Header)));
								}
							}
						}

						TreeMap<String, String> measure = new TreeMap<String, String>();
						if (StatisticalMeasures.containsKey("mean")) {
							measure = StatisticalMeasures.get("mean");
						}

						measure.put(Key, (new StatisticalTests().mean(Var1) + "(P)-" + new StatisticalTests().mean(Var2)
								+ "(A)"));
						//measure.put(Key, ( ColorCell(new StatisticalTests().mean(Var1),new StatisticalTests().mean(Var2),Measure)+new StatisticalTests().mean(Var1) + "(P)-" + new StatisticalTests().mean(Var2)
						//		+ "(A)"));
						
						
						
						
						StatisticalMeasures.put("mean", measure);

						measure = new TreeMap<String, String>();
						if (StatisticalMeasures.containsKey("SD")) {
							measure = StatisticalMeasures.get("SD");

						}
						measure.put(Key,
								(new StatisticalTests().STD(Var1) + "(P)-" + new StatisticalTests().STD(Var2) + "(A)"));
					
						
						//measure.put(Key, ( ColorCell(new StatisticalTests().STD(Var1),new StatisticalTests().STD(Var2),Measure)+new StatisticalTests().STD(Var1) + "(P)-" + new StatisticalTests().STD(Var2)
						//		+ "(A)"));
						
						StatisticalMeasures.put("SD", measure);

						measure = new TreeMap<String, String>();
						if (StatisticalMeasures.containsKey("T-Test")) {
							measure = StatisticalMeasures.get("T-Test");
						}
						if (Double.parseDouble(new StatisticalTests().TTest(Var1, Var2)) > 0.05)
							measure.put(Key, new StatisticalTests().TTest(Var1, Var2));
						if (Double.parseDouble(new StatisticalTests().TTest(Var1, Var2)) <= 0.05)
							measure.put(Key, new StatisticalTests().TTest(Var1, Var2) + "*");
						StatisticalMeasures.put("T-Test", measure);

						measure = new TreeMap<String, String>();
						if (StatisticalMeasures.containsKey("anova")) {
							measure = StatisticalMeasures.get("anova");
						}
						if (Double.parseDouble(new StatisticalTests().anova(Var1, Var2)) > 0.05)
							measure.put(Key, new StatisticalTests().anova(Var1, Var2));

						if (Double.parseDouble(new StatisticalTests().anova(Var1, Var2)) <= 0.05)
							measure.put(Key, new StatisticalTests().anova(Var1, Var2) + "*");
						StatisticalMeasures.put("anova", measure);
						
						measure = new TreeMap<String, String>();
						if (StatisticalMeasures.containsKey("FTest")) {
							measure = StatisticalMeasures.get("FTest");
						}
						if (Double.parseDouble(new StatisticalTests().fTest(Var1, Var2)) > 0.05)
							measure.put(Key, new StatisticalTests().fTest(Var1, Var2));

						if (Double.parseDouble(new StatisticalTests().fTest(Var1, Var2)) <= 0.05)
							measure.put(Key, new StatisticalTests().fTest(Var1, Var2) + "*");
						StatisticalMeasures.put("FTest", measure);
						
						measure = new TreeMap<String, String>();
						if (StatisticalMeasures.containsKey("RSquared")) {
							measure = StatisticalMeasures.get("RSquared");
						}
						if (Double.parseDouble(new StatisticalTests().RSquared(Var1, Var2)) > 0.05)
							measure.put(Key, new StatisticalTests().RSquared(Var1, Var2));

						if (Double.parseDouble(new StatisticalTests().RSquared(Var1, Var2)) <= 0.05)
							measure.put(Key, new StatisticalTests().RSquared(Var1, Var2) + "*");
						StatisticalMeasures.put("RSquared", measure);
						
						measure = new TreeMap<String, String>();
						if (StatisticalMeasures.containsKey("Mann")) {
							measure = StatisticalMeasures.get("Mann");
						}
						if (Double.parseDouble(new StatisticalTests().Mann(Var1, Var2)) > 0.05)
							measure.put(Key, new StatisticalTests().Mann(Var1, Var2));

						if (Double.parseDouble(new StatisticalTests().Mann(Var1, Var2)) <= 0.05)
							measure.put(Key, new StatisticalTests().Mann(Var1, Var2) + "*");
						StatisticalMeasures.put("Mann", measure);
						

					}
				}
			}

			Pipelines.put(file.getName().substring(0, file.getName().indexOf('.')), StatisticalMeasures);
		}

		if(Parameters.getHeatmap().equals("T")) {
		HeatMap(Pipelines,ResoHeaders,ResoHeaders.keySet().size());
		
	}
		Vector<String> EvaluationMatrices = new Vector<String>();
		EvaluationMatrices.add("mean");
		EvaluationMatrices.add("SD");
		//EvaluationMatrices.add("T-Test");
		//EvaluationMatrices.add("anova");
		//EvaluationMatrices.add("RSquared");
		//EvaluationMatrices.add("Mann");
		
		//EvaluationMatrices.add("FTest");

		// Produce latex table
		String numofIterations = "&&&";
		for (String key : ResoHeaders.keySet()) {
			numofIterations += key + "&";
		}
		
		String Table = "\\begin{table} \\resizebox{\\textwidth}{!}{\n"
				+ "\\begin{tabular}{lcccccccccccccccccccccc}  Pipeline variant & Statistical measure & Structure evaluation & \\multicolumn{"
				+ ResoGroups.keySet().size() + "}{c}{Resolution}  \\\\ \\hline " + numofIterations + "\\\\ \\hline \n";
		Vector<String> PrintedPipelines = new Vector<String>();

		for (String Pipe : Pipelines.keySet()) { // loop on Pipelines
			String PipelineAndEvaluationmatrix = Pipe.split("-")[0];

			if (!PrintedPipelines.contains(PipelineAndEvaluationmatrix)) {
				PrintedPipelines.add(PipelineAndEvaluationmatrix);
				String Row = "\\multirow{6}{*}{" + Pipe.split("-")[0] + "}&";
				for (int m = 0; m < EvaluationMatrices.size(); ++m) {

					Row += "\\multirow{ 3}{*}{" + EvaluationMatrices.get(m) + "}&";
					for (String StructureEvaluation : Pipelines.keySet()) {
						String PipelineName = StructureEvaluation.split("-")[0];

						if (PipelineName.equals(PipelineAndEvaluationmatrix))
							for (String Evaluationmatrix : Pipelines.get(StructureEvaluation).keySet()) {
								if (Evaluationmatrix.equals(EvaluationMatrices.get(m))) {
									Row += StructureEvaluation.substring(StructureEvaluation.indexOf('-') + 1) + "&";
									for (String key : ResoGroups.keySet()) {

										for (String Iteration : Pipelines.get(StructureEvaluation).get(Evaluationmatrix)
												.keySet()) {

											if (Iteration.equals(ResoGroups.get(key))) {
												Row += Pipelines.get(StructureEvaluation).get(Evaluationmatrix)
														.get(Iteration) + "&";

											}

										}

									}

									Row = Row.substring(0, Row.lastIndexOf('&'));

									Row += "\\\\&&";
								}
							}
					}
					Row = Row.substring(0, Row.lastIndexOf('&'));
				}
				Row = Row.substring(0, Row.lastIndexOf('&'));
				Table += Row + " \\hline  \n";
			}
		}
		Table += "\\end{tabular}}\n" + "\n" + "\n" + "\\end{table}";

		ROBIN.CheckDirAndFile("EvaluationTablesAndPlots");

		new TxtFiles().WriteStringToTxtFile("EvaluationTablesAndPlots/StatisticalMeasures.tex", Table);

	}

	String ClassifyResolution(double Reso) {
		String Bin="";
		if (Parameters.getMR().equals("F")) {

			if (Reso <= 3.1) {
				Bin = "1.0 - 3.1";

			}
			// if(Reso <2) {
			// Bin="1.0 - 1.9";

			// }
			// if(Reso >=2 && Reso <=3.1) {
			// Bin="2.0 - 3.1";
			// }

			if (Reso == 3.2) {
				Bin = "3.2";
			}
			if (Reso == 3.4) {
				Bin = "3.4";
			}
			if (Reso == 3.6) {
				Bin = "3.6";
			}
			if (Reso == 3.8) {
				Bin = "3.8";
			}
			if (Reso >= 4) {
				Bin = "4.0+";
			}
			
		}
		if (Parameters.getMR().equals("T")) {

			if (Reso < 1.6) {
				Bin = "1.0 - 1.5";

			}
			if (Reso >= 1.6 && Reso <= 2) {
				Bin = "1.6 - 2.0";
			}

			if (Reso >= 2.1 && Reso <= 2.5) {
				Bin = "2.1 - 2.5";
			}

			if (Reso >= 2.6 && Reso <= 3.0) {
				Bin = "2.6 - 3.0";
			}
			if (Reso >= 3.1) {
				Bin = "3.1+";
			}

				
		}
		if(Bin.trim().equals("")) {
			
			Parameters.setLog("T");
			new Log().Warning(this, "This resolution "+ Reso +" does not fit in any of the bins. We will try to classify in "+(Reso-0.1));
		return ClassifyResolution(Reso-0.1);
		}
		return Bin;
	}
	
	String ColorCell(String Var1, String Var2, String StructureEvaluation) {
		
		if(StructureEvaluation.contains("Completeness")) {
		if(new BigDecimal(Var1).subtract(new BigDecimal(Var2)).abs().compareTo(new BigDecimal("5")) <0) {
			
			return "\\cellcolor{green!25}";
		}
		else {
			return "\\cellcolor{red!25}";
		}}
		else {
			if(new BigDecimal(Var1).subtract(new BigDecimal(Var2)).abs().compareTo(new BigDecimal("0.05")) <0) {
				
				return "\\cellcolor{green!25}";
			}
			else {
				return "\\cellcolor{red!25}";
			}
		}
	}
	
	void HeatMap(TreeMap<String, TreeMap<String, TreeMap<String, String>>> Pipelines, TreeMap<String, String> ResoHeaders , int NumberOfResoGroups) throws FileNotFoundException {
		double max=0;
		double min = Double.MAX_VALUE;
		
		HashMap<String,Double> MaxAndMin= new HashMap<String,Double>();
		MaxAndMin.put("SDMax", 0.0);
		MaxAndMin.put("SDMin", Double.MAX_VALUE);
		MaxAndMin.put("MeanMax", 0.0);
		MaxAndMin.put("MeanMin", Double.MAX_VALUE);
		for (String Pipe : Pipelines.keySet()) {
			
			double ToConvertTo100Scale=1;
			if(!Pipe.contains("Completeness"))
				ToConvertTo100Scale=100;
			TreeMap<String, String> SD=Pipelines.get(Pipe).get("SD");
			TreeMap<String, String> mean=Pipelines.get(Pipe).get("mean");
			for(String Val: SD.keySet()) {
				BigDecimal P=new BigDecimal(SD.get(Val).split("-")[0].replaceAll("\\(P\\)", ""));
				BigDecimal A= new BigDecimal(SD.get(Val).split("-")[1].replaceAll("\\(A\\)", ""));
				if(ToConvertTo100Scale==1) {
					P=P.setScale(0, RoundingMode.HALF_UP);
					A=A.setScale(0, RoundingMode.HALF_UP);
				}
				
				double Diff=P.subtract(A).abs().doubleValue();
				
			Diff=Diff*ToConvertTo100Scale;
			
			
			if(Diff > MaxAndMin.get("SDMax")) {
				
				MaxAndMin.put("SDMax", Diff);
				}
			if(Diff < MaxAndMin.get("SDMin")) {
				
				MaxAndMin.put("SDMin", Diff);
			}
			
			
			
			
			}
			
			 
			for(String Val: mean.keySet()) {
				BigDecimal P=new BigDecimal(mean.get(Val).split("-")[0].replaceAll("\\(P\\)", ""));
				BigDecimal A= new BigDecimal(mean.get(Val).split("-")[1].replaceAll("\\(A\\)", ""));
				if(ToConvertTo100Scale==1) {
					P=P.setScale(0, RoundingMode.HALF_UP);
					A=A.setScale(0, RoundingMode.HALF_UP);
				}
				
				double Diff=P.subtract(A).abs().doubleValue();	
				
				Diff=Diff*ToConvertTo100Scale;
				
				if(Diff > MaxAndMin.get("MeanMax")) {
					
					MaxAndMin.put("MeanMax",Diff);
					
					}
				if(Diff < MaxAndMin.get("MeanMin")) {
					
					
					MaxAndMin.put("MeanMin",Diff);
					
					
				}
				
				
				
			
			}
			
		}
		
		
		
		
		MaxAndMin.put("SDRange", MaxAndMin.get("SDMax")-MaxAndMin.get("SDMin"));
		MaxAndMin.put("MeanRange", MaxAndMin.get("MeanMax")-MaxAndMin.get("MeanMin"));
		
		
		
		System.out.println(MaxAndMin);
		for (String Pipe : Pipelines.keySet()) {
			double ToConvertTo100Scale=1;
			if(!Pipe.contains("Completeness"))
				ToConvertTo100Scale=100;
			TreeMap<String, String> SD=Pipelines.get(Pipe).get("SD");
			TreeMap<String, String> mean=Pipelines.get(Pipe).get("mean");
			for(String Val: SD.keySet()) {
				double Diff=new BigDecimal(SD.get(Val).split("-")[0].replaceAll("\\(P\\)", "")).subtract(new BigDecimal(SD.get(Val).split("-")[1].replaceAll("\\(A\\)", ""))).abs().doubleValue();
               Diff=Diff*ToConvertTo100Scale;
			//	double ColorTransparency=new BigDecimal(Diff).divide(new BigDecimal(Range),2, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).doubleValue();
				double ColorTransparency=new BigDecimal(Diff).divide(new BigDecimal(MaxAndMin.get("SDRange")),2, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).doubleValue();
	
				
				/*
				ColorTransparency=ColorTransparency*2;
				
				String UpdatedCell="";
				//ColorTransparency=100-ColorTransparency;
				
				if(ColorTransparency<=100.0) {
					
					ColorTransparency=100-ColorTransparency;
					//System.out.println(ColorTransparency);
					UpdatedCell= "\\cellcolor{blue!"+ColorTransparency+"}"+SD.get(Val);
			}
				if(ColorTransparency>100.0) {
					ColorTransparency=ColorTransparency - 100;
				 UpdatedCell= "\\cellcolor{red!"+ColorTransparency+"}"+SD.get(Val);
				 }
				*/
				String UpdatedCell= "\\cellcolor{red!"+ColorTransparency+"}"+SD.get(Val);
                SD.put(Val, UpdatedCell);
			}
			for(String Val: mean.keySet()) {
				double Diff=new BigDecimal(mean.get(Val).split("-")[0].replaceAll("\\(P\\)", "")).subtract(new BigDecimal(mean.get(Val).split("-")[1].replaceAll("\\(A\\)", ""))).abs().doubleValue();
				 Diff=Diff*ToConvertTo100Scale;
//				double ColorTransparency=new BigDecimal(Diff).divide(new BigDecimal(Range),2, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).doubleValue();
			
				double ColorTransparency=new BigDecimal(Diff).divide(new BigDecimal(MaxAndMin.get("MeanRange")),2, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).doubleValue();

				/*
				ColorTransparency=ColorTransparency*2;
				String UpdatedCell="";
				
				if(ColorTransparency<=100.0) {
					ColorTransparency=100-ColorTransparency;
					UpdatedCell= "\\cellcolor{blue!"+ColorTransparency+"}"+mean.get(Val);
			}
				if(ColorTransparency>100.0) {
					ColorTransparency=ColorTransparency - 100;
				 UpdatedCell= "\\cellcolor{red!"+ColorTransparency+"}"+mean.get(Val);
				 }
				*/
				
				String UpdatedCell= "\\cellcolor{green!"+ColorTransparency+"}"+mean.get(Val);
                mean.put(Val, UpdatedCell);
			}
			Pipelines.get(Pipe).put("SD", SD);
			Pipelines.get(Pipe).put("mean", mean);
			//System.out.println(SD);
			//System.out.println(mean);
		}
		
		SDAndMeanTable(Pipelines,ResoHeaders,NumberOfResoGroups, min , max,MaxAndMin);
	}
	void SDAndMeanTable(TreeMap<String, TreeMap<String, TreeMap<String, String>>> Pipelines, TreeMap<String, String> ResoHeaders , int NumberOfResoGroups, double min , double max , HashMap<String,Double> MaxAndMin) throws FileNotFoundException {
		
		String CSV="Pipeline,MeanReal,MeanPredicted,MeanDifference,SDReal,SDPredicted,SDDifference,StructureEvaluation,Resolution\n";
		String Resolution="&&";
		String MeanAndSDHeader="&";
		String PAndA="&";
		for(String Key : ResoHeaders.keySet()) {
			Resolution+="\\multicolumn{4}{c}{"+Key+"}&";
			MeanAndSDHeader+="&\\multicolumn{2}{c}{mean}";
			MeanAndSDHeader+="&\\multicolumn{2}{c}{SD}";
			PAndA+="&P&A&P&A";
		}
		MeanAndSDHeader+="\\\\";
		PAndA+="\\\\   ";
		String Table="\\begin{table*}[t]  \\center \n" + 
				" \\tiny\n" + 
				" \\setlength{\\tabcolsep}{0.6em} \n"
				+ "\\begin{tabular}{lccc|cc|cc|cc|cc|cc|cc|cc|cc|cc|cc|cc}  Pipeline variant &  Structure evaluation & \\multicolumn{"
				+ (NumberOfResoGroups *2) + "}{c}{Resolution}  \\\\ \\toprule " + Resolution + "\\\\  \n";
	
		Table+=MeanAndSDHeader;
		Table+=PAndA +"\\toprule \n";
		String PrevoiusPrintedPipeline="";
		String NormMsg="";
		for (String Pipe : Pipelines.keySet()) {
			String Row="";
			if(!PrevoiusPrintedPipeline.equals(Pipe.split("-")[0])) {
			 Row="\\multirow{3}{*}{"+Pipe.split("-")[0]+"}&"+Pipe.substring(Pipe.indexOf("-")+1);
			Table+="\\hline";
			}
			 else
				Row+="&"+Pipe.substring(Pipe.indexOf("-")+1);
			PrevoiusPrintedPipeline=Pipe.split("-")[0];
			for(String ResoGroup : ResoHeaders.keySet()) {
			//	System.out.println(ResoGroup);
				for(String Test : Pipelines.get(Pipe).keySet()) {
				//	System.out.println(Pipelines.get(Pipe).get(Test));
					
					if(Test.equals("mean")) {
						if(Pipelines.get(Pipe).get(Test).containsKey(ResoGroup.split("\\(")[0])) {
						String CellColor=Pipelines.get(Pipe).get(Test).get(ResoGroup.split("\\(")[0]).split("}")[0]+"}";
						//System.out.println(Pipelines.get(Pipe).get(Test).get(ResoGroup.split("\\(")[0]).split("-")[0].replaceAll("\\(P\\)", ""));
						BigDecimal P=new BigDecimal(Pipelines.get(Pipe).get(Test).get(ResoGroup.split("\\(")[0]).split("-")[0].replaceAll("\\(P\\)", "").replaceAll(Pattern.quote(CellColor), ""));
						BigDecimal A= new BigDecimal(Pipelines.get(Pipe).get(Test).get(ResoGroup.split("\\(")[0]).split("-")[1].replaceAll("\\(A\\)", "").replaceAll(Pattern.quote(CellColor), ""));
						if(Pipe.substring(Pipe.indexOf("-")+1).equals("Completeness")) {
							P=P.setScale(0, RoundingMode.HALF_UP);
							A=A.setScale(0, RoundingMode.HALF_UP);
							
						}
						if(Parameters.getNormalizeStructureEvaluationInErrorTable().equals("T")) {
							if(Parameters.getStructureEvaluationToBeNormalized().contains(Pipe.substring(Pipe.indexOf("-")+1)) ){
							P=P.divide(new BigDecimal(100));
							A=A.divide(new BigDecimal(100));
							NormMsg=Parameters.getStructureEvaluationToBeNormalized()+" was normalised";
							
							}
						}
						CSV+=Pipe.split("-")[0]+","+A+","+P+","+A.subtract(P).abs().doubleValue()+",";
						Row+="&"+CellColor+P+"&"+CellColor+A;

						//Row+="&"+Pipelines.get(Pipe).get(Test).get(ResoGroup.split("\\(")[0]).split("-")[0].replaceAll("\\(P\\)", "")+"&"+CellColor+Pipelines.get(Pipe).get(Test).get(ResoGroup.split("\\(")[0]).split("-")[1].replaceAll("\\(A\\)", "");
					}
						if(!Pipelines.get(Pipe).get(Test).containsKey(ResoGroup.split("\\(")[0]))
							Row+="&"+"-"+"&"+"-";
					}
					
				}
				for(String Test : Pipelines.get(Pipe).keySet()) {
					
					if(Test.equals("SD")) {
						if(Pipelines.get(Pipe).get(Test).containsKey(ResoGroup.split("\\(")[0])) {
						String CellColor=Pipelines.get(Pipe).get(Test).get(ResoGroup.split("\\(")[0]).split("}")[0]+"}";
//System.out.println(Pipelines.get(Pipe).get(Test).get(ResoGroup.split("\\(")[0]).split("-")[0].replaceAll("\\(P\\)", ""));
						BigDecimal P=new BigDecimal(Pipelines.get(Pipe).get(Test).get(ResoGroup.split("\\(")[0]).split("-")[0].replaceAll("\\(P\\)", "").replaceAll(Pattern.quote(CellColor), ""));
						BigDecimal A= new BigDecimal(Pipelines.get(Pipe).get(Test).get(ResoGroup.split("\\(")[0]).split("-")[1].replaceAll("\\(A\\)", "").replaceAll(Pattern.quote(CellColor), ""));
						if(Pipe.substring(Pipe.indexOf("-")+1).equals("Completeness")) {
							P=P.setScale(0, RoundingMode.HALF_UP);
							A=A.setScale(0, RoundingMode.HALF_UP);
							
							
						}
						
						if(Parameters.getNormalizeStructureEvaluationInErrorTable().equals("T")) {
							if(Parameters.getStructureEvaluationToBeNormalized().contains(Pipe.substring(Pipe.indexOf("-")+1)) ){
							P=P.divide(new BigDecimal(100));
							A=A.divide(new BigDecimal(100));
							NormMsg=Parameters.getStructureEvaluationToBeNormalized()+" was normalised";
							}
						}
						CSV+=A+","+P+","+A.subtract(P).abs().doubleValue()+","+Pipe.substring(Pipe.indexOf("-")+1)+","+ResoGroup.split("\\(")[0]+"\n";
						Row+="&"+CellColor+P+"&"+CellColor+A;
					}
						if(!Pipelines.get(Pipe).get(Test).containsKey(ResoGroup.split("\\(")[0]))
							Row+="&"+"-"+"&"+"-";	
					}
					
				}
			}
			Table+=Row+"\\\\ \n";
			
		}
		String HeatMapLegend="\\begin{tikzpicture}";
		double Pos=0;
		for(int i=0 ; i <= 100 ; ++i) {
			HeatMapLegend+="\\node[draw ,fill=red!"+i+",draw=none,minimum height=0.4cm,minimum width=0.02cm, inner sep=0pt,text width=0.5mm] at ("+Pos+", -4){}; \n";
			Pos=Pos+0.044;
		}
		
		HeatMapLegend+="\\node[draw ,fill=none,draw=none,minimum height=0.4cm,minimum width=0.02cm] at ("+(Pos-0.4)+", -4){\\small "+(MaxAndMin.get("SDMax")/100.0)+"};";
		HeatMapLegend+="\\node[draw ,fill=none,draw=none,minimum height=0.4cm,minimum width=0.02cm] at (0.0, -4){\\small "+(MaxAndMin.get("SDMin")/100.0)+"}; ";
		//System.out.println(Table);
		
		
		for(int i=0 ; i <= 100 ; ++i) {
			HeatMapLegend+="\\node[draw ,fill=green!"+i+",draw=none,minimum height=0.4cm,minimum width=0.02cm, inner sep=0pt,text width=0.5mm] at ("+Pos+", -4){}; \n";
			Pos=Pos+0.044;
		}
		
		HeatMapLegend+="\\node[draw ,fill=none,draw=none,minimum height=0.4cm,minimum width=0.02cm] at ("+(Pos-0.4)+", -4){\\small "+(MaxAndMin.get("MeanMax")/100.0)+"};";
		HeatMapLegend+="\\node[draw ,fill=none,draw=none,minimum height=0.4cm,minimum width=0.02cm] at (5.0, -4){\\small "+(MaxAndMin.get("MeanMin")/100.0)+"}; ";
		//System.out.println(Table);
		HeatMapLegend+="\\end{tikzpicture}";
		
		
		
		Table+="\\bottomrule \\end{tabular}\n" + 
				"\n" + HeatMapLegend+
				"\n" + NormMsg+"\n"+
				"\\end{table*}";
		ROBIN.CheckDirAndFile("EvaluationTablesAndPlots");
		new TxtFiles().WriteStringToTxtFile("EvaluationTablesAndPlots/MeanAndSD.tex", Table);
		new TxtFiles().WriteStringToTxtFile("EvaluationTablesAndPlots/MeanAndSDCSV.csv", CSV);
		
	}
}


