package ROBIN.Mining.Paper;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ROBIN.Data.Preparation.Features;
import ROBIN.Log.Log;
import ROBIN.ML.Model.ROBIN;
import ROBIN.ML.Model.Parameters;
import ROBIN.Utilities.CSVReader;
import ROBIN.Utilities.CSVWriter;
import ROBIN.Utilities.FilesUtilities;
import ROBIN.Utilities.TxtFiles;

public class MiningResearchPaper {
	
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		
		//input AuthorsInformation.csv because NonDuplicatedPubid.csv contains combined PDB ID. However, the code here will remove duplicated papers   
		
		HashMap<String, Vector<HashMap<String, String>>> Papers=	new CSVReader().ReadIntoHashMap("AuthorsInformation.csv","PDB");
		

		//inputs AuthorsInformation.csv, CSVToUseInStatisticalTest and CSVToUseInStatisticalTestFiltered

new ROBIN().CheckDirAndFile("EvaluationTablesAndPlots");
new MiningResearchPaper().RecommendedPipeline(new MiningResearchPaper().RemoveDuplicatedPapers(Papers), "CSVToUseInStatisticalTest","CSVToUseInStatisticalTestFiltered");
new MiningResearchPaper().LatexTable("RecommendedPipeline.csv");

		
	}
	
	public HashMap<String, Vector<HashMap<String, String>>> RemoveDuplicatedPapers(HashMap<String, Vector<HashMap<String, String>>> Papers) throws IOException {
		//HashMap<String, Vector<HashMap<String, String>>> Papers=	new CSVReader().ReadIntoHashMap(PaperCSV, "PDB");
		HashMap<String, Vector<HashMap<String, String>>> RemovedDuplicatedPapers=	new HashMap<String, Vector<HashMap<String, String>>>();

		for(String PDB : Papers.keySet()) {
			if(Papers.get(PDB).size()==1) {
				RemovedDuplicatedPapers.put(PDB, Papers.get(PDB));
			}
			else {
				
				HashMap<String,String> SamePipeline= new HashMap<String,String>();
				for(int p=0 ; p < Papers.get(PDB).size() ; ++p) {
					SamePipeline.put(Papers.get(PDB).get(p).get("Tool"), "");
				}
				if(SamePipeline.size()==1)
					RemovedDuplicatedPapers.put(PDB, Papers.get(PDB));
			}
		}
		//new CSVWriter().WriteFromHashMapContainsRepatedRecord(RemovedDuplicatedPapers, "NonDuplicatedPapers.csv", "PDB");
		//System.out.println(RemovedDuplicatedPapers);
		return RemovedDuplicatedPapers;
	}
	public void LatexTable(String RecommendedPipelineCSV) throws IOException {
		HashMap<String, Vector<HashMap<String, String>>> UsedRecommendedPipeline=	new CSVReader().ReadIntoHashMap(RecommendedPipelineCSV, "PDB");

		String LatexTable="";
		for(String pdb : UsedRecommendedPipeline.keySet()) {
		
			String UsedPipeline=UsedRecommendedPipeline.get(pdb).get(0).get("UsedPipeline");
			String UsedPipelineCompleteness=UsedRecommendedPipeline.get(pdb).get(0).get("AchievedCompleteness");
			String RecommendedPipeline=UsedRecommendedPipeline.get(pdb).get(0).get("BestPipeline");;
			String RecommendedPipelineCompleteness="";
			String RecommendedPipelinePredictedCompleteness="";
			 Vector<HashMap<String, String>> RealAndPredicted=UsedRecommendedPipeline.get(pdb);
			 for(int i=0 ;i < RealAndPredicted.size();++i) {
				 if(RealAndPredicted.get(i).get("Better").equals("T")|| RealAndPredicted.get(i).get("Better").equals("F")) {
					 RecommendedPipelineCompleteness=RealAndPredicted.get(i).get("BestCompleteness");
				 } 
				 if(RealAndPredicted.get(i).get("Better").equals("PT")|| RealAndPredicted.get(i).get("Better").equals("PF")) {
					 RecommendedPipelinePredictedCompleteness=RealAndPredicted.get(i).get("BestCompleteness");
				 }
				 
			 }
			 RecommendedPipeline=RecommendedPipeline.replaceAll(".csv", "");
			 if(!RecommendedPipeline.equals(UsedPipeline)) {
			 if(new BigDecimal(UsedPipelineCompleteness).compareTo(new BigDecimal(RecommendedPipelineCompleteness)) >0)
			 LatexTable+="\\cellcolor{red!30} "+pdb.substring(0,4)+"& \\cellcolor{red!30} "+UsedPipeline+"& \\cellcolor{red!30} "+UsedPipelineCompleteness+"& \\cellcolor{red!30} "+RecommendedPipeline+"& \\cellcolor{red!30} "+RecommendedPipelineCompleteness+"& \\cellcolor{red!30} "+RecommendedPipelinePredictedCompleteness+"\\\\ \\hline \n";
			 else
				 LatexTable+=" \\cellcolor{green!30} "+pdb.substring(0,4)+"& \\cellcolor{green!30} "+UsedPipeline+"& \\cellcolor{green!30} "+UsedPipelineCompleteness+"& \\cellcolor{green!30} "+RecommendedPipeline+"& \\cellcolor{green!30} "+RecommendedPipelineCompleteness+"& \\cellcolor{green!30} "+RecommendedPipelinePredictedCompleteness+"\\\\ \\hline \n";
			 }
			 else {
				 LatexTable+=""+pdb.substring(0,4)+"&"+UsedPipeline+"&"+UsedPipelineCompleteness+"&"+RecommendedPipeline+"&"+RecommendedPipelineCompleteness+"&"+RecommendedPipelinePredictedCompleteness+"\\\\ \\hline \n";
 
			 }
		}
		new TxtFiles().WriteStringToTxtFile("EvaluationTablesAndPlots/RecommendedPipeline.tex",LatexTable);
		
	}
	public void RecommendedPipeline(HashMap<String, Vector<HashMap<String, String>>> paper, String CSVFolder, String CSVFolderTesting) throws IOException {
		//HashMap<String, Vector<HashMap<String, String>>> paper=	new CSVReader().ReadIntoHashMap(PapersCSV, "PDB");
		HashMap<String, Vector<HashMap<String, String>>> pdb=	new CSVReader().ReadIntoHashMap(new FilesUtilities().ReadFilesList(CSVFolderTesting)[0].getAbsolutePath(), "PDB");// any of the files just to get the list of PDB id
		HashMap<String, Vector<HashMap<String, String>>> paper2=	new HashMap<String, Vector<HashMap<String, String>>>();

		System.out.println(paper.size());
		System.out.println(pdb.size());
		//remove PDB from training dataset
		for(String p: pdb.keySet()) {
			
			for(String p2: paper.keySet()) {
				
				if(p.contains(p2)) {
					
					paper2.put(p, paper.get(p2));
					break;
				}
			}
		}
		paper.clear();
		paper=paper2;
		
		String CSV="PDB,AchievedCompleteness,UsedPipeline,BestCompleteness,BestPipeline,Better,LinkDirection\n";
		int LinkDirection=0;
		for(String PDBID:paper.keySet() ) {
			
			HashMap<String, Vector<HashMap<String, String>>> MRdata=	new CSVReader().ReadIntoHashMap(CSVFolder+"/"+paper.get(PDBID).get(0).get("Tool")+".csv", "PDB");
			
			System.out.println(CSVFolder+"/"+paper.get(PDBID).get(0).get("Tool")+".csv");
			String AchievedCompleteness=MRdata.get(PDBID).get(0).get("AchievedCompleteness");
			
			System.out.println(MRdata.get(PDBID).get(0).get("AchievedCompleteness"));
			double BestCom=0;
			double AchievedCompletenessOfRecommended=0;
			String Pipeline="";
			for(File file : new File(CSVFolder).listFiles()) {
				//if(!file.getName().equals(paper.get(PDBID).get(0).get("Tool")+".csv")) {
				HashMap<String, Vector<HashMap<String, String>>> pipeline=	new CSVReader().ReadIntoHashMap(file.getAbsolutePath(), "PDB");

				if(Double.parseDouble(pipeline.get(PDBID).get(0).get("Completeness")) > BestCom) {
					BestCom=Double.parseDouble(pipeline.get(PDBID).get(0).get("Completeness"));
					AchievedCompletenessOfRecommended=Double.parseDouble(pipeline.get(PDBID).get(0).get("AchievedCompleteness"));
					Pipeline=file.getName();
				}
				//}
			}
			if( (AchievedCompletenessOfRecommended - Double.parseDouble(AchievedCompleteness) )  > 0 ) {
			CSV+=PDBID+","+AchievedCompleteness+","+paper.get(PDBID).get(0).get("Tool")+","+AchievedCompletenessOfRecommended+","+Pipeline+",T,"+LinkDirection+"\n";
			CSV+=PDBID+","+AchievedCompleteness+","+paper.get(PDBID).get(0).get("Tool")+","+BestCom+","+Pipeline+",PT,"+LinkDirection+"\n";

			}else {
				CSV+=PDBID+","+AchievedCompleteness+","+paper.get(PDBID).get(0).get("Tool")+","+AchievedCompletenessOfRecommended+","+Pipeline+",F,"+LinkDirection+"\n";
				CSV+=PDBID+","+AchievedCompleteness+","+paper.get(PDBID).get(0).get("Tool")+","+BestCom+","+Pipeline+",PF,"+LinkDirection+"\n";

			}
			LinkDirection++;
		}
		
		new TxtFiles().WriteStringToTxtFile("RecommendedPipeline.csv", CSV);

	}
	
}
