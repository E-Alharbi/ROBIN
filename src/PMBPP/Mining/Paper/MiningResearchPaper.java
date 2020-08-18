package PMBPP.Mining.Paper;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

import PMBPP.Data.Preparation.Features;
import PMBPP.Log.Log;
import PMBPP.ML.Model.Parameters;
import PMBPP.Utilities.CSVReader;
import PMBPP.Utilities.TxtFiles;

public class MiningResearchPaper {
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {

		
		
		
		HashMap<String, Vector<HashMap<String, String>>> paper=	new CSVReader().ReadIntoHashMap("/Users/emadalharbi/Downloads/testp/MRPapersTestDatasets.csv", "PDB");
		
		
		
		String CSV="PDB,AchievedCompleteness,UsedPipeline,BestCompleteness,BestPipeline,Better,LinkDirection\n";
		int LinkDirection=0;
		for(String PDBID:paper.keySet() ) {
			HashMap<String, Vector<HashMap<String, String>>> MRdata=	new CSVReader().ReadIntoHashMap("/Volumes/PhDHardDrive/PMBPP/FinalTraining/PMBPPResults/MR/CSVToUseInStatisticalTest/"+paper.get(PDBID).get(0).get("Pipeline")+".csv", "PDB");
			String AchievedCompleteness=MRdata.get(PDBID).get(0).get("AchievedCompleteness");
			
			System.out.println(MRdata.get(PDBID).get(0).get("AchievedCompleteness"));
			double BestCom=0;
			double AchievedCompletenessOfRecommended=0;
			String Pipeline="";
			for(File file : new File("/Volumes/PhDHardDrive/PMBPP/FinalTraining/PMBPPResults/MR/CSVToUseInStatisticalTest/").listFiles()) {
				//if(!file.getName().equals(paper.get(PDBID).get(0).get("Pipeline")+".csv")) {
				HashMap<String, Vector<HashMap<String, String>>> pipeline=	new CSVReader().ReadIntoHashMap(file.getAbsolutePath(), "PDB");

				if(Double.parseDouble(pipeline.get(PDBID).get(0).get("Completeness")) > BestCom) {
					BestCom=Double.parseDouble(pipeline.get(PDBID).get(0).get("Completeness"));
					AchievedCompletenessOfRecommended=Double.parseDouble(pipeline.get(PDBID).get(0).get("AchievedCompleteness"));
					Pipeline=file.getName();
				}
				//}
			}
			if( (AchievedCompletenessOfRecommended - Double.parseDouble(AchievedCompleteness) )  > 0 ) {
			CSV+=PDBID+","+AchievedCompleteness+","+paper.get(PDBID).get(0).get("Pipeline")+","+AchievedCompletenessOfRecommended+","+Pipeline+",T,"+LinkDirection+"\n";
			CSV+=PDBID+","+AchievedCompleteness+","+paper.get(PDBID).get(0).get("Pipeline")+","+BestCom+","+Pipeline+",PT,"+LinkDirection+"\n";

			}else {
				CSV+=PDBID+","+AchievedCompleteness+","+paper.get(PDBID).get(0).get("Pipeline")+","+AchievedCompletenessOfRecommended+","+Pipeline+",F,"+LinkDirection+"\n";
				CSV+=PDBID+","+AchievedCompleteness+","+paper.get(PDBID).get(0).get("Pipeline")+","+BestCom+","+Pipeline+",PF,"+LinkDirection+"\n";

			}
			LinkDirection++;
		}
		
		new TxtFiles().WriteStringToTxtFile("MRPapersWithBestPipelineAchievedCompleteness.csv", CSV);

		
	}
	
	public void Fetch(String CSVPath) throws ParserConfigurationException, SAXException, IOException {
		// TODO Auto-generated method stub
		/*
		String CSV="PDB,Pipeline,PaperLink\n";
		String PaperNotFound="PDB,DOI\n";
		String PaperFoundButNotUsePipeline="PDB,DOI\n";
		HashMap<String, Vector<HashMap<String, String>>> map=	new CSVReader().ReadIntoHashMap(CSVPath, "PDB");
	     System.out.println(map.size());
		int count=0;
		int countDOI=0;
		
		int countsyndication=0;
		int countPaper=0;
		//for(String PDBID: map.keySet()) {
		//	PDBIDs+=PDBID.substring(0,4)+",";
		//}
		//System.out.println(PDBIDs);
		for(String PDBID: map.keySet()) {
			countPaper++;
			System.out.println("Paper: "+countPaper);
String PDBIDAsFRomTheExcel=	PDBID;
 PDBID=PDBID.substring(0, 4);
String PDBBankRes= new MiningResearchPaper().GetHttpRequste("https://www.rcsb.org/pdb/rest/customReport.csv?pdbids="+PDBID+"&customReportColumns=pubmedId,doi&primaryOnly=1&service=wsfile&format=csv");
//System.out.println(new CSVReader().ReadIntoHashMap(PDBBankRes, "structureId"));
System.out.println(PDBID);

String pubmedId="";
if(new CSVReader().ReadIntoHashMap(PDBBankRes, "structureId").size()>0) { // in case not data found in PDBBank
pubmedId=new CSVReader().ReadIntoHashMap(PDBBankRes, "structureId").get(PDBID).get(0).get("pubmedId");
String PMC= new MiningResearchPaper().GetHttpRequste("https://www.ebi.ac.uk/europepmc/webservices/rest/"+pubmedId+"/fullTextXML");
if(PMC.trim().length()==0) {
	PMC=new MiningResearchPaper().GetHttpRequste("https://api.elsevier.com/content/article/doi/"+new CSVReader().ReadIntoHashMap(PDBBankRes, "structureId").get(PDBID).get(0).get("doi")+"?APIKey="+Parameters.getElsevierToken());
if(PMC.trim().length()==0) {
	//System.out.println(new CSVReader().ReadIntoHashMap(PDBBankRes, "structureId").get(PDBID).get(0).get("doi"));
	countDOI++;
	PMC=new MiningResearchPaper().GetHttpRequste("https://doi.crossref.org/servlet/query?pid="+Parameters.getCrossrefEmail()+"&format=unixref&id="+new CSVReader().ReadIntoHashMap(PDBBankRes, "structureId").get(PDBID).get(0).get("doi"));
	if(PMC.contains("collection property=")) {
		System.out.println(PMC.split("collection property=")[1].split("</collection>")[0]);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new URL("https://doi.crossref.org/servlet/query?pid="+Parameters.getCrossrefEmail()+"&format=unixref&id="+new CSVReader().ReadIntoHashMap(PDBBankRes, "structureId").get(PDBID).get(0).get("doi")).openStream());
		
		if(!PMC.contains("syndication")) { // links contain syndication do not work  
	
	//System.out.println(doc.getElementsByTagName("resource").item(0).getTextContent());
	//System.out.println(doc.getElementsByTagName("resource").item(1).getTextContent());
	if(doc.getElementsByTagName("resource").item(1).getTextContent().toLowerCase().contains("pdf")) {
		new MiningResearchPaper().Download(doc.getElementsByTagName("resource").item(1).getTextContent().trim(),"pdf");

	
	File myFile = new File("paper.pdf");

     PDDocument docpdf;
	try {
		docpdf = PDDocument.load(myFile);
		 PDFTextStripper stripper = new PDFTextStripper();
	        String text = stripper.getText(docpdf);
	        PMC=text;
	       // System.out.println("Text size: " + text.length() + " characters:");
	        docpdf.close();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		
	} 

       
        //System.out.println(text);
    }
	else {
		//System.out.println("not pdf");
		new MiningResearchPaper().Download(doc.getElementsByTagName("resource").item(1).getTextContent().trim(),"html");
		PMC=new TxtFiles().readFileAsString("paper.html");
	}
	
   	

		
		
		
	

}
else {
	
if(doc.getElementsByTagName("resource").item(0).getTextContent().contains("gad")) {
	System.out.println("GAD");
	new MiningResearchPaper().Download("http://genesdev.cshlp.org/content/"+doc.getElementsByTagName("item_number").item(0).getTextContent()+".full","html");
	PMC=new TxtFiles().readFileAsString("paper.html");
}
else if(doc.getElementsByTagName("resource").item(0).getTextContent().contains("jbc")) {
	System.out.println("JBC");
	String ID=doc.getElementsByTagName("item_number").item(0).getTextContent().replaceAll("/jbc/", "");
	ID=ID.replaceAll(".atom", "");
	System.out.println(ID);
	new MiningResearchPaper().Download("https://www.jbc.org/content/"+doc.getElementsByTagName("item_number").item(0).getTextContent()+".full","html");
	PMC=new TxtFiles().readFileAsString("paper.html");
}
else {
	
	System.out.println("CAN");
	new MiningResearchPaper().Download(doc.getElementsByTagName("resource").item(0).getTextContent().trim(),"html");
	PMC=new TxtFiles().readFileAsString("paper.html");
	
}





}
	
	}
}
}
//System.out.println(pubmedId);
//System.out.println(PMC);
//Vector<String> Tools= new Vector<String>();
//Tools.add("arp/warp");
//Tools.add("buccaneer");
//Tools.add("shelxe");
///Tools.add("phenix.autobuild");
//Tools.add("phenix autobuild");

if(PMC.length()==0) {
	PaperNotFound+=PDBIDAsFRomTheExcel+","+new CSVReader().ReadIntoHashMap(PDBBankRes, "structureId").get(PDBID).get(0).get("doi")+"\n";
}

HashMap<String,String> Tools= new HashMap<String,String>();
Tools.put("arp/warp", "ARPwARP");
Tools.put("buccaneer", "Buccaneeri1I5ModelSeed");
Tools.put("shelxe", "ShelxeWithTFlag");
Tools.put("phenix.autobuild", "Phenix");
Tools.put("phenix autobuild", "Phenix");
boolean UsePipeline=false;
if(PMC.trim().length()!=0)
for(String tool : Tools.keySet() ) {
	if(PMC.toLowerCase().contains(tool)) {
		System.out.println("Found "+tool +" PDB "+PDBID);
		++count;
		CSV+=PDBIDAsFRomTheExcel+","+Tools.get(tool)+","+new CSVReader().ReadIntoHashMap(PDBBankRes, "structureId").get(PDBID).get(0).get("doi")+"\n";
		UsePipeline=true;
	}
	}
if(UsePipeline==false && PMC.length()!=0)
PaperFoundButNotUsePipeline+=PDBIDAsFRomTheExcel+","+new CSVReader().ReadIntoHashMap(PDBBankRes, "structureId").get(PDBID).get(0).get("doi")+"\n";
	}

		}
		
		System.out.println(count);
		System.out.println(countDOI);
		System.out.println(countsyndication);
		new TxtFiles().WriteStringToTxtFile("MRPapers.csv", CSV);
		new TxtFiles().WriteStringToTxtFile("MRPapersNOTFound.csv", PaperNotFound);
		new TxtFiles().WriteStringToTxtFile("MRPapersFoundButNotUsePipeline.csv", PaperFoundButNotUsePipeline);
		
	}
	String GetHttpRequste(String urllink) {
		URL url=null;
		try {
			url = new URL(urllink);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpURLConnection conn=null;
		try {
			conn = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		try {
			conn.setRequestMethod("GET");
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		conn.setRequestProperty("Accept", "application/xml");

		
		

		BufferedReader br=null;
		try {
			br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}

		if(br==null)
			return"";
		
		String output;
		
		String Txt="";
		try {
			while ((output = br.readLine()) != null) {
				
				Txt+=output+"\n";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		conn.disconnect();
		return Txt;
		*/
	}
	
	
}
