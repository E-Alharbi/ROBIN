package PMBPP.Utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import org.apache.commons.io.FilenameUtils;

import Comparison.Analyser.ExcelContents;
import Comparison.Analyser.ExcelLoader;
import PMBPP.Data.Preparation.ExcelContentsWithFeatures;
import PMBPP.Log.Log;
import PMBPP.ML.Model.Parameters;



import java.util.Comparator;

import java.util.LinkedHashMap;
import java.util.Map;
 
import static java.util.stream.Collectors.*;
import static java.util.Map.Entry.*;

public class ExecutionTime {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
	
		Parameters.setAllExcelFolder("/Volumes/PhDHardDrive/PMBPP/FinalTraining/PMBPPResults/MR2/noncs");
		Parameters.setExcelFolder("/Volumes/PhDHardDrive/PMBPP/FinalTraining/PMBPPResults/MR2/noncs");
		//String CSV="PDB,TotalExecutionTime,Best,Size,Fmap,Resolution,Pipeline\n";
		//CSV+=new ExecutionTime().FindFirstBest("/Volumes/PhDHardDrive/PMBPP/FinalTraining/PMBPPResults/Experimental/HLAandParrot/noncs3","/Volumes/PhDHardDrive/PMBPP/FinalTraining/PMBPPResults/Experimental/HLAandParrot/CSVToUseInStatisticalTestFiltered","Completeness",true);
		//CSV+=new ExecutionTime().FindFirstBest("/Volumes/PhDHardDrive/PMBPP/FinalTraining/PMBPPResults/Experimental/HLAandParrot/noncs3","/Volumes/PhDHardDrive/PMBPP/FinalTraining/PMBPPResults/Experimental/HLAandParrot/CSVToUseInStatisticalTestFiltered","R-free",false);
		//CSV+=new ExecutionTime().FindFirstBest("/Volumes/PhDHardDrive/PMBPP/FinalTraining/PMBPPResults/Experimental/HLAandParrot/noncs3","/Volumes/PhDHardDrive/PMBPP/FinalTraining/PMBPPResults/Experimental/HLAandParrot/CSVToUseInStatisticalTestFiltered","R-work",false);

		//new TxtFiles().WriteStringToTxtFile("ExecutionTime.csv", CSV);
		
		new ExecutionTime().AllWithRecommended();
		/*
		File [] Excel = new FilesUtilities().ReadFilesList("/Volumes/PhDHardDrive/FinalTraining/FinalTraining/Experimental/noncs");
		File [] CSVFiles = new FilesUtilities().ReadFilesList("/Volumes/PhDHardDrive/FinalTraining/FinalTraining/Experimental/CSVToUseInStatisticalTestFiltered");
int count=0;
		for(File excel : Excel) {
			String excelName=excel.getName().substring(0,excel.getName().indexOf('.'));
			boolean found=false;
			for(File csvFile : CSVFiles) {
				String csvFileName=csvFile.getName().substring(0,csvFile.getName().indexOf('-'));
if(excelName.equals(csvFileName)) {
	found=true;
}
			}
			if(found==true)
				count++;

		}
		System.out.println(count);
		*/
	}
	
	
	
	public void AllWithRecommended() throws IOException {
		
		String CSV="PDB,TotalExecutionTime,Best,Size,Fmap,Resolution,Pipeline\n";
		CSV+=FindFirstBest(Parameters.getExcelFolder(),"/Volumes/PhDHardDrive/PMBPP/FinalTraining/PMBPPResults/MR2/CSVToUseInStatisticalTestFiltered","Completeness",true,0,"",0);
		CSV+=FindFirstBest(Parameters.getExcelFolder(),"/Volumes/PhDHardDrive/PMBPP/FinalTraining/PMBPPResults/MR2/CSVToUseInStatisticalTestFiltered","R-free",false,0,"",2);
		CSV+=FindFirstBest(Parameters.getExcelFolder(),"/Volumes/PhDHardDrive/PMBPP/FinalTraining/PMBPPResults/MR2/CSVToUseInStatisticalTestFiltered","R-work",false,0,"",2);
		
		//CSV+=FindFirstBest(Parameters.getExcelFolder(),"/Volumes/PhDHardDrive/PMBPP/FinalTraining/PMBPPResults/Experimental/HLAandParrot/CSVToUseInStatisticalTestFiltered","Completeness",true,5,"at least 5%",0);
		//CSV+=FindFirstBest(Parameters.getExcelFolder(),"/Volumes/PhDHardDrive/PMBPP/FinalTraining/PMBPPResults/Experimental/HLAandParrot/CSVToUseInStatisticalTestFiltered","R-free",false,0.05,"at least 5%",2);
		//CSV+=FindFirstBest(Parameters.getExcelFolder(),"/Volumes/PhDHardDrive/PMBPP/FinalTraining/PMBPPResults/Experimental/HLAandParrot/CSVToUseInStatisticalTestFiltered","R-work",false,0.05,"at least 5%",2);

		new TxtFiles().WriteStringToTxtFile("ExecutionTime.csv", CSV);
	}
	
	
	
	
	
	
	
	String FindFirstBest(String ExcelPath, String CSVFilesPath , String StrectureMeasure , boolean HighIsBetter, double LevelofComparison, String LevelofComparisonLabel, int RoundedTo  ) throws IOException {
		File [] Excel = new FilesUtilities().ReadFilesList(ExcelPath);
		File [] CSVFiles = new FilesUtilities().ReadFilesList(CSVFilesPath);

		Vector<Vector<ExcelContents>> ExcelFile = new Vector<Vector<ExcelContents>>();
		ExcelLoader f = new ExcelLoader();
		HashMap<String,Double> PipelineSpeedScore = new HashMap<String,Double>();
        int indexOfExcel=0;// Uses when we have unequal excel size. Ex, Shelxe excel  
        int NumberOfPDB=0;
		for(File e : Excel) {
			
			ExcelFile.add(f.ReadExcel(e.getAbsolutePath()));
			
			if(ExcelFile.get(ExcelFile.size()-1).size() > NumberOfPDB) {
				NumberOfPDB=ExcelFile.get(ExcelFile.size()-1).size();
				indexOfExcel=ExcelFile.size()-1;
			}
			
			PipelineSpeedScore.put(e.getName().replaceAll("."+FilenameUtils.getExtension(e.getName()), ""), 0.0);
		}
		PipelineSpeedScore=ScroePipelinesBasedOnSpeed(PipelineSpeedScore);
		
		String CSV="";
		
		for(int i=0 ; i < ExcelFile.get(indexOfExcel).size(); ++i) {
			
			System.out.println("i "+i);
			
			String BestPipelinePredictionFirstBest="";
			
			

			
			
			
			double BestPredictionFirstBest=-1;
		
			
			double TotalTimeFirstBest=0;
			double BestTimeFirstBest=0;
	
			
			if(HighIsBetter==false) {
				BestPredictionFirstBest=Double.MAX_VALUE;
				
			}
			
	//finding from Prediction
			HashMap <String,Double> StructureEvaluationMeasure = new HashMap <String,Double>();
			for(File file : CSVFiles) {
				
				String StructureEvaluation=file.getName().substring(file.getName().indexOf('-')+1,file.getName().indexOf('.'));
				
				if(StructureEvaluation.equals(StrectureMeasure)) {
					
					HashMap<String, Vector<HashMap<String, String>>> map=	new CSVReader().ReadIntoHashMap(file.getAbsolutePath(), "PDB");
					
					for(String PDB: map.keySet()) {	
						
						
					if(PDB.equals(ExcelFile.get(0).get(i).PDB_ID)) {
						
						for(String Evaluation : map.get(PDB).get(0).keySet()) {
							
							if(Evaluation.equals(StrectureMeasure)) {
								double Measure = new BigDecimal(map.get(PDB).get(0).get(Evaluation)).setScale(RoundedTo, RoundingMode.HALF_UP).doubleValue();
								if(Measure==BestPredictionFirstBest) {
									//System.out.println(file.getName());
									//System.out.println(BestPipelinePredictionFirstBest);
									//System.out.println(PipelineSpeedScore);
									if(PipelineSpeedScore.get(file.getName().split("-")[0]) < PipelineSpeedScore.get(BestPipelinePredictionFirstBest.split("-")[0])) {
										//System.out.println("Equal " +BestPipelinePredictionFirstBest+" by "+file.getName());
										BestPredictionFirstBest=Measure;
										BestPipelinePredictionFirstBest=file.getName();
									}
								}
								StructureEvaluationMeasure.put(file.getName(),Measure);
								if(HighIsBetter==true) {
	
									if(Measure  >  BestPredictionFirstBest) {
									BestPredictionFirstBest=Measure;
									BestPipelinePredictionFirstBest=file.getName();
									
								}
								
								}
								
								if(HighIsBetter==false) {
									
										
									    if(Measure     <  BestPredictionFirstBest) {
										BestPredictionFirstBest=Measure;
										BestPipelinePredictionFirstBest=file.getName();
										
									}
									
								}
							}
						}
						break;
					}
				}
				}
				
				
			}
			
			
if(!StructureEvaluationMeasure.isEmpty()) {
	
	// Sort the hashmap 
	//System.out.println(StructureEvaluationMeasure);
	 
	  boolean IsAllHaveDiffWithinComLevel=true;
	  for(String k1 : StructureEvaluationMeasure.keySet()) {
	  for(String k2 : StructureEvaluationMeasure.keySet()) {
		  if(!k1.equals(k2)) {
			  
				  if(Math.abs((StructureEvaluationMeasure.get(k1) - StructureEvaluationMeasure.get(k2) )) > LevelofComparison)
				  {
					  
					  IsAllHaveDiffWithinComLevel=false;
					  break;
				  }
			  
			  
		  }
		  
	  }
	  }
	  if(IsAllHaveDiffWithinComLevel==true) {
		  //System.out.println(StructureEvaluationMeasure);
		  //System.out.println("Best "+BestPredictionFirstBest);
		  //System.out.println("Best "+BestPipelinePredictionFirstBest);
		  //Find the fastest pipeline 
		  double Lowest= Double.MAX_VALUE;
		  String FastestPipeline="";
		  for(String k1 : StructureEvaluationMeasure.keySet()) {
			  if(PipelineSpeedScore.get(k1.split("-")[0]) <  Lowest) {
				  Lowest= PipelineSpeedScore.get(k1.split("-")[0]);
				  FastestPipeline=k1;
			  }
		  }
		 
		  if(!FastestPipeline.equals(BestPipelinePredictionFirstBest)) {
			  System.out.println("Fastest "+FastestPipeline +" Time "+Lowest);
			  BestPipelinePredictionFirstBest=FastestPipeline;
			  BestPredictionFirstBest=StructureEvaluationMeasure.get(FastestPipeline);
		  }
		  
	  }
	  
}
			
			
			
			
			
			
			
			//Finding best from the real results
			String FileNamePrediction="";
			double HighestFromInd=0;// the highest execution time from individual pipelines 
			double HighestFromCom=0;// the highest execution time from combined pipelines
			if(BestPredictionFirstBest!=-1 && BestPredictionFirstBest!=Double.MAX_VALUE) {
			FileNamePrediction=BestPipelinePredictionFirstBest.substring(0,BestPipelinePredictionFirstBest.indexOf('-'));

			for(File file : Excel) {
				String FileNameActual=file.getName().substring(0,file.getName().indexOf('.'));

				Vector<ExcelContents> excel= f.ReadExcel(file.getAbsolutePath());
				for(int elm=0; elm<excel.size();++elm ) {
				if(excel.get(elm).PDB_ID.equals(ExcelFile.get(0).get(i).PDB_ID)) {
				if(FileNameActual.equals(FileNamePrediction)) {
					BestTimeFirstBest=Double.parseDouble(excel.get(elm).TimeTaking);
					
					if(FileNameActual.contains("#")) {
						String FirstPipelineName=FileNameActual.split("#")[0];
						Vector<ExcelContents> FirstPipeline = new Vector<ExcelContents>();
						//We used Parameters.getAllExcelFolder() here because not all the excel in ExcelFolder same as in AllExcelFolder. For example, when we train Buccaneeri1I5#PhenixHLA.xlsx and not train Buccaneer model, we still need Buccaneer excel to get the time taking from.      
						FirstPipeline = f.ReadExcel(Parameters.getAllExcelFolder()+"/"+FirstPipelineName+".xlsx");
						for(int elm1=0; elm1<FirstPipeline.size();++elm1 ) {
							if(FirstPipeline.get(elm1).PDB_ID.equals(ExcelFile.get(0).get(i).PDB_ID)) {
							//	System.out.println("BestTimeFirstBest1 "+BestTimeFirstBest);
								BestTimeFirstBest+=Double.parseDouble(FirstPipeline.get(elm1).TimeTaking);
							//System.out.println("BestTimeFirstBest2 "+BestTimeFirstBest);
								break;
							}
						}
					}
					if(FileNameActual.contains("#")&&Double.parseDouble(excel.get(elm).TimeTaking)==BestTimeFirstBest)
						new Log().Error(this, "This is a pipeline combination but we did not find the execution time of the first pipeline. PDB: "+excel.get(elm).PDB_ID +" Excel: "+file.getName());
				}
				TotalTimeFirstBest+=Double.parseDouble(excel.get(elm).TimeTaking);
				if(FileNameActual.contains("#")) {
					if(Double.parseDouble(excel.get(elm).TimeTaking)>HighestFromCom) {
						HighestFromCom=Double.parseDouble(excel.get(elm).TimeTaking);
						//System.out.println("Updated");
					}
				}
				else {
					if(Double.parseDouble(excel.get(elm).TimeTaking)>HighestFromInd) {
					HighestFromInd=Double.parseDouble(excel.get(elm).TimeTaking);
					}
				}
				//System.out.println("File "+file.getName()+" PDB "+excel.get(elm).PDB_ID+" " +HighestFromCom +" HighestFromInd "+HighestFromInd);

				break; 
				}
				}
			}
			
			
		
			
			
		}
			
			if(BestPredictionFirstBest!=-1 && BestPredictionFirstBest!=Double.MAX_VALUE) { // not all the structure were used in testing data sets
			
			FileNamePrediction=BestPipelinePredictionFirstBest.substring(0,BestPipelinePredictionFirstBest.indexOf('-'));

		  
			FileNamePrediction=BestPipelinePredictionFirstBest.substring(0,BestPipelinePredictionFirstBest.indexOf('-'));
			
				
				
				CSV+=ExcelFile.get(0).get(i).PDB_ID+","+TotalTimeFirstBest+","+"All (when run in sequence)"+","+ExcelFile.get(0).get(i).NumberofAtomsinFirstPDB+","+ExcelFile.get(0).get(i).F_mapCorrelation+","+ExcelFile.get(0).get(i).Resolution+","+"All"+"\n";
				CSV+=ExcelFile.get(0).get(i).PDB_ID+","+BestTimeFirstBest+","+"Recommended "+StrectureMeasure+"("+LevelofComparisonLabel+")"+","+ExcelFile.get(0).get(i).NumberofAtomsinFirstPDB+","+ExcelFile.get(0).get(i).F_mapCorrelation+","+ExcelFile.get(0).get(i).Resolution+","+BestPipelinePredictionFirstBest+"\n";
				CSV+=ExcelFile.get(0).get(i).PDB_ID+","+(HighestFromCom+HighestFromInd)+","+"All (when run in parallel)"+","+ExcelFile.get(0).get(i).NumberofAtomsinFirstPDB+","+ExcelFile.get(0).get(i).F_mapCorrelation+","+ExcelFile.get(0).get(i).Resolution+","+"highest execution time from individual and combined pipelines"+"\n";

			//	CSV+=ExcelFile.get(0).get(i).PDB_ID+","+(HighestFromCom+HighestFromInd)+","+"All (when run in parallel) "+StrectureMeasure+","+ExcelFile.get(0).get(i).NumberofAtomsinFirstPDB+","+ExcelFile.get(0).get(i).F_mapCorrelation+","+ExcelFile.get(0).get(i).Resolution+","+BestPipelinePredictionFirstBest+"\n";
//System.out.println(TotalTimeFirstBest);
//System.out.println(HighestFromCom+HighestFromInd);
//System.out.println(BestTimeFirstBest);
//System.out.println(BestPipelinePredictionFirstBest);
				
				
			}
		
		}
		return CSV;
	}
	
	void All() throws FileNotFoundException {
		File [] Excel = new FilesUtilities().ReadFilesList("/Volumes/PhDHardDrive/FinalTraining/FinalTraining/MR/noncs");

		Vector<Vector<ExcelContents>> ExcelFile = new Vector<Vector<ExcelContents>>();
		ExcelLoader f = new ExcelLoader();
		for(File e : Excel) {
			ExcelFile.add(f.ReadExcel(e.getAbsolutePath()));
		}
		String CSV="PDB,TotalExecutionTime,Size,Type\n";
		for(int i=0 ; i < ExcelFile.get(0).size(); ++i) {
			double time=0;
			for(int c=0 ;c < ExcelFile.size(); ++c) {
				for(int elm=0; elm<ExcelFile.get(c).size();++elm ) {
					if(ExcelFile.get(c).get(elm).PDB_ID.equals(ExcelFile.get(0).get(i).PDB_ID)) {
						time+=Double.parseDouble(ExcelFile.get(c).get(elm).TimeTaking);
					}
				}
			}
			time=time/60;
			CSV+=ExcelFile.get(0).get(i).PDB_ID+","+time+","+ExcelFile.get(0).get(i).NumberofAtomsinFirstPDB+",MR \n";
			System.out.println(ExcelFile.get(0).get(i).PDB_ID+"-"+time);
		}
		new TxtFiles().WriteStringToTxtFile("ExecutionTimeMR.csv", CSV);
	}

	
	HashMap<String,Double> ScroePipelinesBasedOnSpeed(HashMap<String,Double> Pipelines) {
		
		for(String Pipe: Pipelines.keySet()) {
			
			Vector<String> PipeName=new Vector<String>();
			if(Pipe.contains("#")) {
				PipeName= new Vector<String>(Arrays.asList(Pipe.split("#"))); 
			}
			else{
				PipeName.add(Pipe);
			}
			
			double Score=0;
			for(int i=0 ; i < PipeName.size();++i) {
				if(PipeName.get(i).contains("Buccaneer"))
					Score+=1;
				if(PipeName.get(i).contains("ARPwARP"))
					Score+=2;
				if(PipeName.get(i).contains("Shelxe"))
					Score+=3;
				if(PipeName.get(i).contains("Phenix"))
					Score+=4;
			}
			if(Score==0)
				new Log().Error(this, "Score is zero, meaing there is a pipeline we did not recognise ");
			Pipelines.put(Pipe, Score);
			
		}
		return Pipelines;
	}
}
