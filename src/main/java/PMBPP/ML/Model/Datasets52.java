package PMBPP.ML.Model;

import java.io.File;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Vector;

import org.apache.commons.io.FilenameUtils;

import Comparison.Analyser.ExcelContents;
import Comparison.Analyser.ExcelLoader;
import PMBPP.Utilities.FilesUtilities;

public class Datasets52 {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//String CSV="RMSD,Skew,Resolution,Max,Min,Completeness,CompletenessP,Class\n";
		String CSV="RMSD,Skew,Resolution,Max,Min,Class\n";
		 CSV+=new Datasets52().SaveToCSV("/Volumes/PhDHardDrive/jcsg1200Results/Fasta/VikingRunShelxeForThePaperRevision/All/OrginalBuccEx54ExFaliedCases/noncs/PhenixHAL.xlsx"); 
		 CSV+=new Datasets52().SaveToCSV("/Volumes/PhDHardDrive/jcsg1200Results/Fasta/VikingRunShelxeForThePaperRevision/All/SyntheticBuccInc54ExFaliedCases/noncs/PhenixHAL.xlsx"); 

try(  PrintWriter out = new PrintWriter("PhenixHAL.csv" )  ){
    out.println(CSV );
}
	}

	String SaveToCSV(String ExcelPath) throws Exception {
		
		File [] Data=new FilesUtilities().ReadFilesList("/Volumes/PhDHardDrive/EditorsRevision-2/Datasets/NO-NCS");
		ExcelLoader f = new ExcelLoader();
		Vector<ExcelContents> Excel = new Vector<ExcelContents>();
		String CSV="";
		Excel =  f.ReadExcel(ExcelPath);
		for(ExcelContents E : Excel) {
			System.out.println(Excel.size());
			for(File file :Data) {
				String FileName=file.getName().replaceAll("."+FilenameUtils.getExtension(file.getName()),"");
				if(file.getName().contains(".mtz")&&FileName.equals(E.PDB_ID)) {
					Predict Pre = new Predict();
					Parameters.AttCSV="/Users/emadalharbi/eclipse-workspace/ProteinModelBuildingPipelinePredictor/target/classes/att.csv";
		 		   String [] arg= {file.getAbsolutePath()};
		 		   Pre.main(arg);
		  		  //CSV+=FileName+","+E.Completeness+","+E.R_factor0Cycle+","+E.R_free0Cycle+",R,ARPwARP\n";

		 		 // CSV+=FileName+","+Pre.RowData[0][3]+","+Pre.RowData[0][2]+","+Pre.RowData[0][1]+",P,ARPwARP\n";
		 		 String Resolution=file.getName().substring(5, 8);
		 		// CSV+=FileName+","+E.Completeness+","+E.R_factor0Cycle+","+E.R_free0Cycle+",R,ARPwARP\n";
		 		double Diff= Math.abs(Double.parseDouble(E.Completeness) - Double.parseDouble(Pre.RowData[0][3]));
		 		 //double Diff=Double.parseDouble(Pre.RowData[0][1])- Double.parseDouble(E.R_free0Cycle) ;
		 		 //Diff= 5.0*(Math.round(Diff/5.0));
		 		 Diff= 20.0*(Math.round(Diff/20.0));
		 		 if(Diff>20)
		 			Diff=20;
		 		/*
		 		DecimalFormat df = new DecimalFormat("#.##");
		 		df.setRoundingMode(RoundingMode.HALF_UP);
		 		String RFactor = df.format(BigDecimal.valueOf(Double.valueOf(Diff)));
		 		System.out.println(RFactor);
		 		if(Double.parseDouble(df.format(BigDecimal.valueOf(Double.valueOf(Diff)))) >= 0 && Double.parseDouble(df.format(BigDecimal.valueOf(Double.valueOf(Diff)))) <= 0.05)
		 			RFactor="0.0";
		 		if(Double.parseDouble(df.format(BigDecimal.valueOf(Double.valueOf(Diff)))) >= 0.06 &&  Double.parseDouble(df.format(BigDecimal.valueOf(Double.valueOf(Diff)))) <=0.1)
		 			RFactor="+0.06 - 0.1";
		 		
		 		if(Double.parseDouble(df.format(BigDecimal.valueOf(Double.valueOf(Diff)))) >= 0.11 &&  Double.parseDouble(df.format(BigDecimal.valueOf(Double.valueOf(Diff)))) <=0.15)
		 			RFactor="+0.11 - 0.15";
		 		
		 		//if(Double.parseDouble(df.format(BigDecimal.valueOf(Double.valueOf(Diff)))) >= 0.16 &&  Double.parseDouble(df.format(BigDecimal.valueOf(Double.valueOf(Diff)))) <=0.20)
		 			//RFactor="+0.16 - 0.20";
		 		if(Double.parseDouble(df.format(BigDecimal.valueOf(Double.valueOf(Diff)))) >= 0.16 )
		 			RFactor="0.16+";
		 		
		 		if(Double.parseDouble(df.format(BigDecimal.valueOf(Double.valueOf(Diff)))) < 0 && Double.parseDouble(df.format(BigDecimal.valueOf(Double.valueOf(Diff)))) >= -0.05)
		 			RFactor="0.0";
		 		if(Double.parseDouble(df.format(BigDecimal.valueOf(Double.valueOf(Diff)))) <= -0.06 &&  Double.parseDouble(df.format(BigDecimal.valueOf(Double.valueOf(Diff)))) >= -0.1)
		 			RFactor="+0.06 - 0.1";
		 		if(Double.parseDouble(df.format(BigDecimal.valueOf(Double.valueOf(Diff)))) <= -0.11 &&  Double.parseDouble(df.format(BigDecimal.valueOf(Double.valueOf(Diff)))) >= -0.15)
		 			RFactor="+0.11 - 0.15";
		 		
		 		//if(Double.parseDouble(df.format(BigDecimal.valueOf(Double.valueOf(Diff)))) <= -0.16 &&  Double.parseDouble(df.format(BigDecimal.valueOf(Double.valueOf(Diff)))) >= -0.20)
		 			//RFactor="+0.16 - 0.20";
		 		
		 		if(Double.parseDouble(df.format(BigDecimal.valueOf(Double.valueOf(Diff)))) <= -0.16 )
		 			RFactor="0.16+";
		 		
		 		 //Diff= RFactor;
		 		  * 
		 		  */
		 		 System.out.println(Diff);
		 		// System.out.println(Diff);
//		 		CSV+=Pre.cfftM.RMSD+","+Pre.cfftM.Skew+","+Resolution+","+Pre.cfftM.Max+","+Pre.cfftM.Min+","+E.Completeness+","+Pre.RowData[0][3]+",±"+Diff+"\n";
		 		//CSV+=Pre.cfftM.RMSD+","+Pre.cfftM.Skew+","+Resolution+","+Pre.cfftM.Max+","+Pre.cfftM.Min+",±"+Diff+"\n";

		 		//CSV+=Pre.cfftM.RMSD+","+Pre.cfftM.Skew+","+Resolution+","+Pre.cfftM.Max+","+Pre.cfftM.Min+","+"±"+RFactor+"\n";
 
				//System.out.println(CSV);
				}

			}
		}
		return CSV;
	}
}
