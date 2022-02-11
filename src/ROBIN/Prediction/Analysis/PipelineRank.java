package ROBIN.Prediction.Analysis;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.Vector;
import java.util.stream.Collectors;

import ROBIN.ML.Model.ROBIN;
import ROBIN.ML.Model.Parameters;
import ROBIN.Utilities.CSVReader;
import ROBIN.Utilities.CSVWriter;
import ROBIN.Utilities.FilesUtilities;
import ROBIN.Utilities.TxtFiles;

public class PipelineRank {

	public static void main(String[] args) throws IOException{
		Parameters.setMR("T");
		String CSVFolder="CSVToUseInStatisticalTestFiltered";
		File [] csv = new File(CSVFolder).listFiles();
		Vector<String> EvaluationLabels = new Vector<String>();
		for(File file : csv) {
			
			if(file.getName().contains("-")) {
			if(!EvaluationLabels.contains(file.getName().substring(file.getName().indexOf("-")+1,file.getName().indexOf("."))) ){
				EvaluationLabels.add(file.getName().substring(file.getName().indexOf("-")+1,file.getName().indexOf(".")));
			}
			}
		}
		
		for(int i=0; i < EvaluationLabels.size();++i) {
			new PipelineRank().rank(CSVFolder,false,EvaluationLabels.get(i),"0","0");
			new PipelineRank().rank(CSVFolder,true,EvaluationLabels.get(i),"0","0");
			new PipelineRank().rank(CSVFolder,false,EvaluationLabels.get(i),"1","-0.01");
			new PipelineRank().rank(CSVFolder,true,EvaluationLabels.get(i),"1","-0.01");
			new PipelineRank().rank(CSVFolder,false,EvaluationLabels.get(i),"5","-0.05");
			new PipelineRank().rank(CSVFolder,true,EvaluationLabels.get(i),"5","-0.05");
			
		}
	}
	void rank(String CSVDir, boolean range, String EvaluationLabel ,  String ComMargin , String RfreeMargin) throws IOException {
		
		
		String RangeTitle="";
		String FolderToSave="EvaluationMatrix";
		
		if (range==true) {
			RangeTitle="Range";
			FolderToSave="EvaluationMatrix"+RangeTitle;
		}
		new ROBIN().CheckDirAndFile(FolderToSave); 
		new ROBIN().CheckDirAndFile(FolderToSave+"Reso"); 
		
		
		//Parameters.setMR("T");
		File [] Pipelines= new FilesUtilities().ReadFilesList(CSVDir,EvaluationLabel);
	
		
		
		HashMap<String, Vector<HashMap<String, String>>> PDB=	new CSVReader(Pipelines[0].getAbsolutePath()).ReadIntoHashMap( "PDB");
		HashMap<String, Vector<HashMap<String, String>>> PipelinesOrder=	new CSVReader(Pipelines[0].getAbsolutePath()).ReadIntoHashMap( "PDB");
		HashMap<String,HashMap<Integer,Integer>> OrderCount= new HashMap<String,HashMap<Integer,Integer>>();
		HashMap<String,Double> ResoBinsAndNumOfDatasets= new HashMap<String,Double>();
		
		
		for(String pdb : PDB.keySet()) {
			double Reso = BigDecimal.valueOf(Double.parseDouble(PDB.get(pdb).get(0).get("Resolution")))
					.setScale(1, RoundingMode.HALF_UP).doubleValue();
			
			PDB.get(pdb).get(0).put("ResoBin", String.valueOf(new ModelPerformance().ClassifyResolution(Reso)));
			HashMap<Integer,Integer> empty= new HashMap<Integer,Integer>();
			OrderCount.put(new ModelPerformance().ClassifyResolution(Reso), empty);
			if(ResoBinsAndNumOfDatasets.containsKey(new ModelPerformance().ClassifyResolution(Reso)))
				ResoBinsAndNumOfDatasets.put(new ModelPerformance().ClassifyResolution(Reso),ResoBinsAndNumOfDatasets.get(new ModelPerformance().ClassifyResolution(Reso))+1);
			else
				ResoBinsAndNumOfDatasets.put(new ModelPerformance().ClassifyResolution(Reso),1.0);

		}
		System.out.println(OrderCount);
		
		for(String ResoBin : OrderCount.keySet()) {
			System.out.println(ResoBin);
			HashMap<String, Vector<HashMap<String, String>>> FilterdCopy = new HashMap<String, Vector<HashMap<String, String>>>();
			for(String pdb : PDB.keySet()) {
				if(PDB.get(pdb).get(0).get("ResoBin").equals(ResoBin)) {
					FilterdCopy.put(pdb, PDB.get(pdb));
				}
			}
		for(String pdb : FilterdCopy.keySet()) {
			//Vector<String> PipelinesOrderReal = new Vector<String>();
			//Vector<String> PipelinesOrderPridected = new Vector<String>();
			
			LinkedHashMap<String, BigDecimal> PipelinesOrderReal = new LinkedHashMap<String, BigDecimal>();
			LinkedHashMap<String, BigDecimal> PipelinesOrderPridected = new LinkedHashMap<String, BigDecimal>();

			
			for(int f=0 ; f < Pipelines.length;++f) {
				PipelinesOrderReal.putAll(new PipelineRank().BestPipeline(Pipelines,pdb,PipelinesOrderReal,"Achieved"+EvaluationLabel));
			
			}
			for(int f=0 ; f < Pipelines.length;++f) {
				PipelinesOrderPridected.putAll(new PipelineRank().BestPipeline(Pipelines,pdb,PipelinesOrderPridected,EvaluationLabel));
			
			}
			System.out.println(pdb+" "+PipelinesOrderReal);
			System.out.println(pdb+" "+PipelinesOrderPridected);
			PipelinesOrder.get(pdb).get(0).put("Order", PipelinesOrderReal.toString().replaceAll(",", ""));
			for(int c=1;c < Pipelines.length+1 ; c++) {
				HashMap<Integer,Integer> temp = OrderCount.get(ResoBin);
				if(!temp.containsKey(c)) {
					temp.put(c, 0);
				}
			}
			
			int pipeorder=0;
			for(int c=1;c < Pipelines.length+1 ; c++) {
				pipeorder=new PipelineRank().Compare(PipelinesOrderReal,PipelinesOrderPridected,c,  ComMargin ,  RfreeMargin);
			//if(new PipelineSequenceOrder().Compare(PipelinesOrderReal,PipelinesOrderPridected,c)==c) {
			
			//count++;
			//}
			
			HashMap<Integer,Integer> temp = OrderCount.get(ResoBin);
			
			
			
			if(range==false) {
			if(temp.containsKey(pipeorder)) {
				
				temp.put(pipeorder, temp.get(pipeorder)+1);
				OrderCount.put(ResoBin, temp);
			}
			else {
				temp.put(pipeorder, 1);
				OrderCount.put(ResoBin, temp);
			}
			}
			else {
			for(int update=pipeorder; update < Pipelines.length+1;++update) {
				if(temp.containsKey(update)) {
					
					temp.put(update, temp.get(update)+1);
					OrderCount.put(ResoBin, temp);
				}
				else {
					temp.put(update, 1);
					OrderCount.put(ResoBin, temp);
				}
			}
			}
				
			if(pipeorder!=0)break;
			//System.out.println(temp);
			
			}
		}
	}
		System.out.println(OrderCount);
		new CSVWriter().WriteFromHashMapContainsRepatedRecord(PipelinesOrder, "Order"+EvaluationLabel+RangeTitle+ComMargin+".csv", "PDB",true);
	
		String CSV="ResoBin,NumberOfPipeline,Range,Count,Evaluation,ComMargin,RfreeMargin\n";
		System.out.println(ResoBinsAndNumOfDatasets);
		Vector<Integer> PrePipelineNum= new Vector<Integer>();
		Integer PrePipelineIndex=-1;
		for(String ResoBin : OrderCount.keySet()) {
			PrePipelineNum.clear();
			for(Integer PipelineNum : OrderCount.get(ResoBin).keySet()) {
				String PipelineRange=String.valueOf(PipelineNum);
				if(range==false)
				CSV+=ResoBin+","+PipelineNum+","+((OrderCount.get(ResoBin).get(PipelineNum)*100)/ResoBinsAndNumOfDatasets.get(ResoBin))+","+EvaluationLabel+","+ComMargin+"%,"+RfreeMargin+"\n";
				if(range==true) {
					if(PipelineNum==OrderCount.get(ResoBin).keySet()
						      .stream()
						      .mapToInt(v -> v)
						      .min().orElseThrow(NoSuchElementException::new))
						
						CSV+=ResoBin+","+PipelineNum+","+PipelineNum+","+((OrderCount.get(ResoBin).get(PipelineNum)*100)/ResoBinsAndNumOfDatasets.get(ResoBin))+","+EvaluationLabel+","+ComMargin+"%,"+RfreeMargin+"\n";

					else {
						 double percentge=(Double.valueOf(OrderCount.get(ResoBin).get(PipelineNum))*100)/Double.valueOf(ResoBinsAndNumOfDatasets.get(ResoBin));	

						 percentge=percentge-(Double.valueOf(OrderCount.get(ResoBin).get(PrePipelineIndex))*100)/Double.valueOf(ResoBinsAndNumOfDatasets.get(ResoBin));	
							CSV+=ResoBin+","+PipelineNum+","+PipelineNum+","+percentge+","+EvaluationLabel+","+ComMargin+"%,"+RfreeMargin+"\n";

					}
						for(int i=0 ; i < PrePipelineNum.size();++i) {
						if(i==0) {
						CSV+=ResoBin+","+PipelineNum+","+PrePipelineNum.get(i)+","+((OrderCount.get(ResoBin).get(PrePipelineNum.get(i))*100)/ResoBinsAndNumOfDatasets.get(ResoBin))+","+EvaluationLabel+","+ComMargin+"%,"+RfreeMargin+"\n";

						}else {
						
						 double percentge=(Double.valueOf(OrderCount.get(ResoBin).get(PrePipelineNum.get(i)))*100)/Double.valueOf(ResoBinsAndNumOfDatasets.get(ResoBin));	

						 percentge=percentge-(Double.valueOf(OrderCount.get(ResoBin).get(PrePipelineNum.get(i-1)))*100)/Double.valueOf(ResoBinsAndNumOfDatasets.get(ResoBin));	

						 CSV+=ResoBin+","+PipelineNum+","+PrePipelineNum.get(i)+","+percentge+","+EvaluationLabel+","+ComMargin+"%,"+RfreeMargin+"\n";

						
						
						}
					}
				}
				
				PrePipelineNum.add(PipelineNum);
				PrePipelineIndex=PipelineNum;
			}
		}
		
		new TxtFiles().WriteStringToTxtFile(FolderToSave+"Reso/PipelineOrderReso"+EvaluationLabel+RangeTitle+ComMargin+".csv", CSV);
		
		HashMap<Integer,Integer> PipelinesOrderAllDatasets= new HashMap<Integer,Integer>();
		
		for(String ResoBin : OrderCount.keySet()) {
			
			for(Integer PipelineNum : OrderCount.get(ResoBin).keySet()) {
				
				if(PipelinesOrderAllDatasets.containsKey(PipelineNum)) {
					PipelinesOrderAllDatasets.put(PipelineNum, PipelinesOrderAllDatasets.get(PipelineNum)+OrderCount.get(ResoBin).get(PipelineNum))	;
				}
				else {
					PipelinesOrderAllDatasets.put(PipelineNum,OrderCount.get(ResoBin).get(PipelineNum))	;

				}

			}
		}
		
		
			 CSV="NumberOfPipeline,Count,Range,Evaluation,ComMargin,RfreeMargin\n";
		 
		 Map<Integer,Integer> map = new TreeMap<Integer,Integer>(PipelinesOrderAllDatasets); //sorted keys
		 
		 
		 PrePipelineNum.clear();
		//Vector<Integer> PrePipelineNum= new Vector<Integer>();
		
		PrePipelineIndex=-1;
		
		for(Integer PipelineNum : map.keySet()) {
			String PipelineRange=String.valueOf(PipelineNum);
			/*
			if(range==true)
			{
				PipelineRange=map.keySet()
					      .stream()
					      .mapToInt(v -> v)
					      .min().orElseThrow(NoSuchElementException::new) + "-" +String.valueOf(PipelineNum);
			}
			*/
			if(range==false)	
			CSV+=PipelineRange+","+(Double.valueOf(PipelinesOrderAllDatasets.get(PipelineNum))*100)/Double.valueOf(PDB.keySet().size())+","+PipelineNum+","+EvaluationLabel+","+ComMargin+"%,"+RfreeMargin+"\n";
			//System.out.println((Double.valueOf(PipelinesOrderAllDatasets.get(PipelineNum))*100)/Double.valueOf(PDB.keySet().size()));
	
				if(range==true) {
				if(PipelineNum==map.keySet()
					      .stream()
					      .mapToInt(v -> v)
					      .min().orElseThrow(NoSuchElementException::new))
					CSV+=PipelineRange+","+(Double.valueOf(PipelinesOrderAllDatasets.get(PipelineNum))*100)/Double.valueOf(PDB.keySet().size())+","+PipelineNum+","+EvaluationLabel+","+ComMargin+"%,"+RfreeMargin+"\n";

				else {
					 double percentge=(Double.valueOf(PipelinesOrderAllDatasets.get(PipelineNum))*100)/Double.valueOf(PDB.keySet().size());	
					 percentge=percentge-(Double.valueOf(PipelinesOrderAllDatasets.get(PrePipelineIndex))*100)/Double.valueOf(PDB.keySet().size());	
					CSV+=PipelineRange+","+percentge+","+PipelineNum+","+EvaluationLabel+","+ComMargin+"%,"+RfreeMargin+"\n";

				}
					for(int i=0 ; i < PrePipelineNum.size();++i) {
					if(i==0) {
					CSV+=PipelineRange+","+(Double.valueOf(PipelinesOrderAllDatasets.get(PrePipelineNum.get(i)))*100)/Double.valueOf(PDB.keySet().size())+","+PrePipelineNum.get(i)+","+EvaluationLabel+","+ComMargin+"%,"+RfreeMargin+"\n";
					}else {
					
					 double percentge=(Double.valueOf(PipelinesOrderAllDatasets.get(PrePipelineNum.get(i)))*100)/Double.valueOf(PDB.keySet().size());	
					 percentge=percentge-(Double.valueOf(PipelinesOrderAllDatasets.get(PrePipelineNum.get(i-1)))*100)/Double.valueOf(PDB.keySet().size());	

					CSV+=PipelineRange+","+percentge+","+PrePipelineNum.get(i)+","+EvaluationLabel+","+ComMargin+"%,"+RfreeMargin+"\n";
					
					
					//System.out.println(PipelinesOrderAllDatasets.get(PrePipelineNum.get(i)));

					//System.out.println(PipelinesOrderAllDatasets.get(PrePipelineNum.get(i)) -PipelinesOrderAllDatasets.get(PrePipelineNum.get(i-1)));
					}
				}
			}
			
			PrePipelineNum.add(PipelineNum);
			PrePipelineIndex=PipelineNum;
		}
		new TxtFiles().WriteStringToTxtFile(FolderToSave+"/PipelineOrder"+EvaluationLabel+RangeTitle+ComMargin+".csv", CSV);
		System.out.println(PipelinesOrderAllDatasets);
		

	
	
	}

	LinkedHashMap<String, BigDecimal> BestPipeline(File [] Pipelines, String PDB,LinkedHashMap<String, BigDecimal> PipelinesChecked, String EvaluationType ) throws IOException {
		String PipelineName="";
		String EvValue="-1";
		if(!EvaluationType.contains("Completeness"))
			EvValue="1";
		boolean Rounded=false;
		LinkedHashMap<String, BigDecimal> PipelineNameAndCom= new LinkedHashMap<String, BigDecimal>();
		for(File file : Pipelines) {
			if(file.getName().contains(EvaluationType.replaceAll("Achieved", "")) && !PipelinesChecked.containsKey(file.getName())) {
				HashMap<String, Vector<HashMap<String, String>>> pipeline=	new CSVReader(file.getAbsolutePath()).ReadIntoHashMap( "PDB");
				
if(file.getName().contains("Completeness"))		{// round if it Completeness
	Rounded=true;
if(new BigDecimal(pipeline.get(PDB).get(0).get(EvaluationType)).setScale(0, RoundingMode.HALF_UP).compareTo(new BigDecimal(EvValue).setScale(0, RoundingMode.HALF_UP)) >=0) {
	EvValue=pipeline.get(PDB).get(0).get(EvaluationType);
	
	PipelineName=file.getName();
}}
else {
	if(new BigDecimal(pipeline.get(PDB).get(0).get(EvaluationType)).compareTo(new BigDecimal(EvValue)) <=0) { // R-free and R-work lower is better 
		EvValue=pipeline.get(PDB).get(0).get(EvaluationType);
		
		PipelineName=file.getName();
	}
}

			}

		}
		if(Rounded==true)	{	
		PipelineNameAndCom.put(PipelineName, new BigDecimal(EvValue).setScale(0, RoundingMode.HALF_UP));

		}
		else {
		PipelineNameAndCom.put(PipelineName, new BigDecimal(EvValue));
		
		}
		
		
		return PipelineNameAndCom;
	}
	
	int Compare(LinkedHashMap<String, BigDecimal> V1 , LinkedHashMap<String, BigDecimal> V2 , int NumberOfEleToCompare, String ComMargin , String RfreeMargin ) {
		
		int count=0;
		
		/*
		Vector<String> Vec1= new Vector<String>(V1.keySet());
		Vector<String> Vec2= new Vector<String>(V2.keySet());
		for(int v1=0 ;v1 < NumberOfEleToCompare;++v1) {
			
				if(Vec1.get(v1).equals(Vec2.get(v1))) {
					count++;
				}
				else {
					break;
				}
				
				
			
		}*/
		
		Vector<String> Vec1= new Vector<String>(V1.keySet());
		Vector<String> Vec2= new Vector<String>(V2.keySet());
		boolean RworkorRfree=false;
		for(int v1=0 ;v1 < Vec1.size();++v1) {
			if(Vec1.get(v1).contains("R-work") || Vec1.get(v1).contains("R-free")) {
				RworkorRfree=true;
				break;
			}
		}
		
		System.out.println("RworkorRfree "+RworkorRfree);
		int order=0;
		for(int v1=0 ;v1 < Vec2.size();++v1) {
			//if(Vec1.get(0).equals(Vec2.get(v1)) ) {
			if(Vec2.get(0).equals(Vec1.get(v1)) ) {
			
				//System.out.println("Vec1.get(0) "+Vec1.get(0));
				//System.out.println("Vec2.get(v1) "+Vec2.get(v1));
				System.out.println("Vec2.get(0) "+Vec2.get(0));
				System.out.println("Vec1.get(v1) "+Vec1.get(v1));
				order=v1+1;
				
				//return 1;
			}
			
		}
		System.out.println("order "+order);
		//|| V1.get(Vec1.get(0)).subtract(V2.get(Vec2.get(v1))).compareTo(new BigDecimal("5")) >=0 
		int RankWithMargin=-1;
		if(order!=1) {
			// Find predicted pipeline rank in actual pipelines rank 
			//-0.05
			for(int v1=order-1 ;v1 >=0 ;--v1) {
				if(RworkorRfree==true) {
				if(V1.get(Vec1.get(v1)).subtract(V1.get(Vec1.get(order-1))).compareTo(new BigDecimal(RfreeMargin)) >=0) {//R-free or R-work //-0.05
					RankWithMargin=v1+1;
					
				}
				}
				else {
					if(V1.get(Vec1.get(v1)).subtract(V1.get(Vec1.get(order-1))).compareTo(new BigDecimal(ComMargin)) <=0) { //5
						RankWithMargin=v1+1;
						
					}
				}
			}
			if(RankWithMargin!=-1)order=RankWithMargin;
		}
		
		System.out.println("order "+order);
		System.out.println("ActualRank "+RankWithMargin);
		return order;
	}
}
