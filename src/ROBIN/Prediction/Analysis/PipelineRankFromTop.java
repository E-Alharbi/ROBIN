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

public class PipelineRankFromTop {

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
			new PipelineRankFromTop().rank(CSVFolder,false,EvaluationLabels.get(i),"0","0");
			
			
		}
	}
	void rank(String CSVDir, boolean range, String EvaluationLabel ,  String ComMargin , String RfreeMargin) throws IOException {
		File [] Pipelines= new FilesUtilities().ReadFilesList(CSVDir,EvaluationLabel);
		File CompleteFile=null;
		int max=0;
		for(File file : Pipelines) {
			if(max<new CSVReader(Pipelines[0].getAbsolutePath()).ReadIntoHashMap( "PDB").size()) {
				max=new CSVReader(Pipelines[0].getAbsolutePath()).ReadIntoHashMap( "PDB").size();
				CompleteFile=file;
			}
		}
		System.out.println(CompleteFile.getAbsolutePath());
		HashMap<String, Vector<HashMap<String, String>>> PDB=	new CSVReader(CompleteFile.getAbsolutePath()).ReadIntoHashMap( "PDB");
		HashMap<String,HashMap<Integer,Integer>> OrderCount= new HashMap<String,HashMap<Integer,Integer>>();
		HashMap<String,Double> ResoBinsAndNumOfDatasets= new HashMap<String,Double>();
		LinkedHashMap<BigDecimal, Integer> Loss = new LinkedHashMap<BigDecimal, Integer>();
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
				
				PipelinesOrderReal.putAll(new PipelineRankFromTop().BestPipeline(Pipelines,pdb,PipelinesOrderReal,"Achieved"+EvaluationLabel));
			
			}
			for(int f=0 ; f < Pipelines.length;++f) {
				PipelinesOrderPridected.putAll(new PipelineRankFromTop().BestPipeline(Pipelines,pdb,PipelinesOrderPridected,EvaluationLabel));
			
			}
			
			String FirstBestReal=PipelinesOrderReal.keySet().iterator().next();
			String FirstBestPre=PipelinesOrderPridected.keySet().iterator().next();
			System.out.println(pdb+" "+PipelinesOrderReal);
			System.out.println(pdb+" "+PipelinesOrderPridected);
			System.out.println("FirstBestReal "+FirstBestReal);
			System.out.println("FirstBestPre "+FirstBestPre);
			
			BigDecimal FirstBestRealVal=PipelinesOrderReal.get(FirstBestReal);
			BigDecimal FirstBestPreVal=PipelinesOrderReal.get(FirstBestPre);// yes from the real 
			System.out.println(FirstBestRealVal.subtract(FirstBestPreVal));
			BigDecimal LossVal=FirstBestRealVal.subtract(FirstBestPreVal);
			if(Loss.containsKey(LossVal)) {
				Loss.put(LossVal, Loss.get(LossVal) +1 );
				
			}
			else {
				Loss.put(LossVal, 1 );
			}
		}
		
		}
		System.out.println(Loss);
		//String CSV="LossLevel,LossValue\n";
		String CSV="LossValue,LossValue\n";
		//for(String Level: Loss.keySet()) {
		//	CSV+=Level+","+Loss.get(Level)+"\n";
		//}
		for(BigDecimal Level: Loss.keySet()) {
			for(int i=0 ; i < Loss.get(Level);++i ) {
				CSV+=Level+"\n";
			}
		}
		new TxtFiles().WriteStringToTxtFile("./PipelineLossFromTop"+EvaluationLabel+".csv", CSV);

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
if(pipeline.containsKey(PDB))	{			
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

		}
		if(Rounded==true)	{	
		PipelineNameAndCom.put(PipelineName, new BigDecimal(EvValue).setScale(0, RoundingMode.HALF_UP));

		}
		else {
		PipelineNameAndCom.put(PipelineName, new BigDecimal(EvValue));
		
		}
		
		
		return PipelineNameAndCom;
	}
	
}
