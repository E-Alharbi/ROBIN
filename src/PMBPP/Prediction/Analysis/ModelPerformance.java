package PMBPP.Prediction.Analysis;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.commons.io.FilenameUtils;

import PMBPP.Log.Log;
import PMBPP.ML.Model.PMBPP;
import PMBPP.ML.Model.Parameters;
import PMBPP.Utilities.CSVReader;
import PMBPP.Utilities.CSVWriter;
import PMBPP.Utilities.FilesUtilities;
import PMBPP.Utilities.StatisticalTests;
import PMBPP.Utilities.TxtFiles;

import org.apache.commons.math3.stat.inference.TTest;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.inference.OneWayAnova;
import org.apache.commons.math3.stat.inference.WilcoxonSignedRankTest;
public class ModelPerformance {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Parameters.MR="T";
		Parameters.Features="RMSD,Skew,Resolution,Max,Min,SequenceIdentity";
		new ModelPerformance().SplitOnMeasurementUnitsLevel("/Users/emadalharbi/Downloads/ParrotPhases/MR/CSVToUseInStatisticalTest");
		new ModelPerformance().OmitTrainingdata("CSVToUseInStatisticalTestSplitted","/Users/emadalharbi/Downloads/ParrotPhases/MR/TrainAndTestDataPredictionModels");
		new ModelPerformance().GroupedByResolution("CSVToUseInStatisticalTestFiltered");
	
		//new StatisticalTest().OmitTrainingdata("ClassifedDatasets","/Volumes/PhDHardDrive/FinalTraining/Experimental/ParrotPhases/TrainAndTestDataClassificationModels");

	}

	public void SplitOnMeasurementUnitsLevel(String PathToCSV) throws IOException {
		
		for(File CSV : new FilesUtilities().ReadFilesList(PathToCSV)) {
			HashMap <String,Boolean> features= new CSVReader().FilterByFeatures(CSV.getAbsolutePath(), true);
			HashMap <String,Boolean> MeasurementsUnits= new CSVReader().FilterByFeatures(CSV.getAbsolutePath(), false);
			for(String unit : MeasurementsUnits.keySet()) {
				Vector<String> Headers=new Vector<String>(features.keySet());
				if(Parameters.getMeasurementUnitsToPredict().contains(unit)) {
					Headers.add(unit);
					Headers.add("Achieved"+unit);
					String CSVName=CSV.getName().replaceAll("."+FilenameUtils.getExtension(CSV.getName()),"");
					PMBPP.CheckDirAndFile("CSVToUseInStatisticalTestSplitted");
					HashMap<String, Vector<HashMap<String,String>>> filteredCSV=new CSVReader().ReadIntoHashMapWithFilterdHeaders(CSV.getAbsolutePath(), "PDB", Headers);
					new CSVWriter().WriteFromHashMapContainsRepatedRecord(filteredCSV, "CSVToUseInStatisticalTestSplitted/"+CSVName+"-"+unit+".csv");
					
				}
			}
		}
	}
	
	public void OmitTrainingdata(String PathToCSV, String PathToTestCSV) throws IOException {
		for(File CSV : new FilesUtilities().ReadFilesList(PathToCSV)) {
			String CSVName=CSV.getName().replaceAll("."+FilenameUtils.getExtension(CSV.getName()),"");

			for(File TestCSV : new FilesUtilities().ReadFilesList(PathToTestCSV)) {
				if((CSVName+"-test.csv").equals(TestCSV.getName())) {
					
				
					PMBPP.CheckDirAndFile("CSVToUseInStatisticalTestFiltered");
					new CSVWriter().WriteFromHashMapContainsRepatedRecord(omit(CSV,TestCSV), "CSVToUseInStatisticalTestFiltered/"+CSV.getName());

				}
			}
		}
		
	}
	
	
	public HashMap<String, Vector<HashMap<String,String>>> omit( File CSV,File TestCSV) throws IOException {
		
		HashMap<String, HashMap<String,String>> Testdata= new CSVReader().ReadIntoHashMapWithnoIDHeader(TestCSV.getAbsolutePath());
		HashMap<String, Vector<HashMap<String,String>>> StatisticalTestdata= new CSVReader().ReadIntoHashMap(CSV.getAbsolutePath(),"PDB");
		HashMap<String, Vector<HashMap<String,String>>> FilteredStatisticalTestdata = new HashMap<String, Vector<HashMap<String,String>>>();
		int count=0;

		for(String ID : Testdata.keySet()) {
			
			
		for(String STID : StatisticalTestdata.keySet()) {
			
			Vector<Boolean> Found= new Vector<Boolean>();
		for(String Header : Testdata.get(ID).keySet()) {
			
			if(Parameters.Features.contains(Header))
				for(String STHeader : StatisticalTestdata.get(STID).get(0).keySet()) {
					if(Header.equals(STHeader) && Testdata.get(ID).get(Header).equals(StatisticalTestdata.get(STID).get(0).get(STHeader))) {
						Found.add(true);
						
					break;
					}
				}
			}
		if(Found.size()== Testdata.get(ID).keySet().size()-1) {// we do not need to compare the correct class/label. Only the features are needed
			
			count++;
			FilteredStatisticalTestdata.put(STID, StatisticalTestdata.get(STID));
		}
		}
		
		
	}
		
		if(count!=Testdata.size()) {
			new Log().Error(this, "We cannot identify all the test data. "+count +" instances are found out of "+Testdata.size()+". This will lead to wrong statistical test results. Please review your data!");
		}
		if(count!=FilteredStatisticalTestdata.size()) {
			new Log().Error(this, "We cannot identify all the test data. "+count +" instances are found and "+FilteredStatisticalTestdata.size()+" are stored. This will lead to wrong statistical test results. This might happen when you have duplicate instances in test data");
		}
		return FilteredStatisticalTestdata;
	}
	
	public void GroupedByResolution(String PathToCSV) throws IOException {
		TreeMap<String,String> ResoGroups = new TreeMap<String,String>();
		TreeMap<String,String> ResoHeaders = new TreeMap<String,String>();
		TreeMap<String, TreeMap<String , TreeMap<String , String>>> Pipelines = new TreeMap<String, TreeMap<String , TreeMap<String , String>>>();
		for(File file : new FilesUtilities().ReadFilesList(PathToCSV)) {
			HashMap<String, Vector<HashMap<String,String>>> CSV= new CSVReader().ReadIntoHashMap(file.getAbsolutePath(), "PDB");
			HashMap<String,HashMap<String, Vector<HashMap<String,String>>>> Groups= new HashMap<String,HashMap<String, Vector<HashMap<String,String>>>>();
			for(String PDB : CSV.keySet()) {
				if(CSV.get(PDB).size()>1) {
					new Log().Error(this, "CSV contains more than one record with same HeaderID name. This will lead to wrong statistical test results. So, we cannot continue. To solve this, you need to check the CSV file and make sure it is not contins multiple records with same HeaderID.");
				}
				for(String Header : CSV.get(PDB).get(0).keySet()) {
					if(Header.equals("Resolution")) {
						DecimalFormat df = new DecimalFormat("#.#");
						df.setRoundingMode(RoundingMode.HALF_UP);
						
					//	String Resolution=df.format(Double.parseDouble(CSV.get(PDB).get(0).get(Header)));
						String Resolution=CSV.get(PDB).get(0).get(Header);// Grouping as two decimal places 
						
						
						String Bin="";
						double Reso= BigDecimal.valueOf(Double.parseDouble(Resolution)).setScale(1,RoundingMode.HALF_UP).doubleValue();

						if(Parameters.MR.equals("F")) {

							if(Reso <=3.1) {
								Bin="1.0 - 3.1";
								
							}
							//if(Reso <2) {
							//	Bin="1.0 - 1.9";
								
							//}
							//if(Reso >=2 && Reso <=3.1) {
							//	  Bin="2.0 - 3.1";
							//}
							
							 if(Reso ==3.2) {
								 Bin="3.2";
							}
							 if(Reso ==3.4) {
								 Bin="3.4";
							}
							 if(Reso ==3.6) {
								 Bin="3.6";
							}
							 if(Reso ==3.8) {
								 Bin="3.8";
							}
							 if(Reso >= 4) {
								 Bin="4.0+";
							}
						}
						if(Parameters.MR.equals("T")) {
							
							if(Reso <1.6) {
								Bin="1.0 - 1.5";
								
							}
							  if(Reso >=1.6 && Reso <=2) {
								  Bin="1.6 - 2.0";
							}
							  
							  if(Reso >=2.1 && Reso <=2.5) {
								  Bin="2.1 - 2.5";
							}
							  
							  if(Reso >=2.6 && Reso <=3.0) {
								  Bin="2.6 - 3.0";
							}
							  if(Reso >=3.1) {
								  Bin="3.1+";
							}
							  
						}
						
						
						if(Groups.containsKey(Bin)) {
							HashMap<String, Vector<HashMap<String,String>>> Temp = Groups.get(Bin);
							Temp.put(PDB, CSV.get(PDB));
							Groups.put(Bin, Temp);
						}
						else {
							HashMap<String, Vector<HashMap<String,String>>> Temp = new HashMap<String, Vector<HashMap<String,String>>>();
							Temp.put(PDB, CSV.get(PDB));
							Groups.put(Bin, Temp);
						}
					}
				}
			}
			Parameters.Log="F";
			TreeMap<String , TreeMap<String , String>> StatisticalMeasures= new TreeMap<String , TreeMap<String , String>>();

			for(String Key : Groups.keySet()) {
				ResoGroups.put(Key, Key);
				ResoHeaders.put(Key+"("+Groups.get(Key).size()+")", Key+"("+Groups.get(Key).size()+")");// with number of datasets
				
				HashMap <String,Boolean> MeasurementsUnits= new CSVReader().FilterByFeatures(file.getAbsolutePath(), false);
				
				for(String Measure : MeasurementsUnits.keySet()) {
					if(Parameters.MeasurementUnitsToPredict.contains(Measure)) {
					
						Vector<Double> Var1 = new  Vector<Double>();
						Vector<Double> Var2 = new  Vector<Double>();
						
						for(String PDB : Groups.get(Key).keySet()) {
							for(String Header : Groups.get(Key).get(PDB).get(0).keySet()) {
								if(Header.equals(Measure)) {
									Var1.add(Double.parseDouble(Groups.get(Key).get(PDB).get(0).get(Header)));
								}
								if(Header.equals("Achieved"+Measure)) {
									Var2.add(Double.parseDouble(Groups.get(Key).get(PDB).get(0).get(Header)));
								}
							}
						}
						
						
						
						
						TreeMap<String , String> measure = new TreeMap<String , String>();
						if(StatisticalMeasures.containsKey("mean")) {
							measure=StatisticalMeasures.get("mean");
						}
						
						measure.put(Key, (new StatisticalTests().mean(Var1)+"(P)-"+new StatisticalTests().mean(Var2)+"(A)"));
						StatisticalMeasures.put("mean", measure);
						
						measure = new TreeMap<String , String>();
						if(StatisticalMeasures.containsKey("SD")) {
							measure=StatisticalMeasures.get("SD");
							
						}
						measure.put(Key, (new StatisticalTests().STD(Var1)+"(P)-"+new StatisticalTests().STD(Var2)+"(A)"));
						StatisticalMeasures.put("SD", measure);
						
						measure = new TreeMap<String , String>();
						if(StatisticalMeasures.containsKey("T-Test")) {
							measure=StatisticalMeasures.get("T-Test");
						}
						if(Double.parseDouble(new StatisticalTests().TTest(Var1,Var2))>0.05)
						measure.put(Key, new StatisticalTests().TTest(Var1,Var2));
						if(Double.parseDouble(new StatisticalTests().TTest(Var1,Var2))<=0.05)
						measure.put(Key, new StatisticalTests().TTest(Var1,Var2)+"*");
						StatisticalMeasures.put("T-Test", measure);
						
						
						measure = new TreeMap<String , String>();
						if(StatisticalMeasures.containsKey("anova")) {
							measure=StatisticalMeasures.get("anova");	
						}
						if(Double.parseDouble(new StatisticalTests().anova(Var1,Var2))>0.05)
						measure.put(Key, new StatisticalTests().anova(Var1,Var2));
						
						if(Double.parseDouble(new StatisticalTests().anova(Var1,Var2))<=0.05)
						 measure.put(Key, new StatisticalTests().anova(Var1,Var2)+"*");
						StatisticalMeasures.put("anova", measure);
						
						
						
						
					}
				}
			}
			
			Pipelines.put(file.getName().substring(0,file.getName().indexOf('.')), StatisticalMeasures);
		}
		
		
		
		Vector<String> EvaluationMatrices= new Vector<String>();
		EvaluationMatrices.add("mean");
		EvaluationMatrices.add("SD");
		EvaluationMatrices.add("T-Test");
		EvaluationMatrices.add("anova");
		
		//Produce latex table 
		String numofIterations="&&&";
		for(String key : ResoHeaders.keySet()) {
			numofIterations+=key+"&";
		}
	
		String Table="\\begin{table} \\resizebox{\\textwidth}{!}{\n" + 
				"\\begin{tabular}{lcccccccccccccccccccccc}  Pipeline variant & Statistical measure & Structure evaluation & \\multicolumn{"+ResoGroups.keySet().size()+"}{c}{Resolution}  \\\\ \\hline "+numofIterations+"\\\\ \\hline \n";
		Vector<String> PrintedPipelines= new Vector<String>();
	   
		for(String Pipe : Pipelines.keySet()) { //loop on Pipelines
			String PipelineAndEvaluationmatrix=Pipe.split("-")[0];
			
			if(!PrintedPipelines.contains(PipelineAndEvaluationmatrix)) {
				PrintedPipelines.add(PipelineAndEvaluationmatrix);
				String Row="\\multirow{6}{*}{"+Pipe.split("-")[0]+"}&";
               for(int m=0 ; m < EvaluationMatrices.size();++m) {
            	   
            	   Row+="\\multirow{ 3}{*}{"+EvaluationMatrices.get(m)+"}&";
            	   for(String StructureEvaluation : Pipelines.keySet()) {
            		   String PipelineName=StructureEvaluation.split("-")[0];
            		   
            		  if(PipelineName.equals(PipelineAndEvaluationmatrix))
            		   for(String Evaluationmatrix : Pipelines.get(StructureEvaluation).keySet()) {
            			   if(Evaluationmatrix.equals(EvaluationMatrices.get(m))) {
            				   Row+=StructureEvaluation.substring(StructureEvaluation.indexOf('-')+1)+"&"; 
            				   for(String key : ResoGroups.keySet()) {
            					   
            						for(String Iteration :  Pipelines.get(StructureEvaluation).get(Evaluationmatrix).keySet()) {
            							
            							
            								if(Iteration.equals(ResoGroups.get(key))) {
            									Row+=Pipelines.get(StructureEvaluation).get(Evaluationmatrix).get(Iteration)+"&";
            									
            								}
            								
            							}
            						
            						}
            				  
            				   Row=Row.substring(0,Row.lastIndexOf('&'));
            				  
            						Row+="\\\\&&";
            			   }
            		   }
            	   }
            	   Row=Row.substring(0,Row.lastIndexOf('&'));
}
               Row=Row.substring(0,Row.lastIndexOf('&'));
               Table+=Row+" \\hline  \n";
			}
		}
		Table+="\\end{tabular}}\n" + 
				"\n" + 
				"\n" + 
				"\\end{table}";
		
		
		PMBPP.CheckDirAndFile("EvaluationTablesAndPlots");
		 
		new TxtFiles().WrtieStringToTxtFile("EvaluationTablesAndPlots/StatisticalMeasures.tex", Table);
		
		
		
		
	}
	
	
	
	
}
