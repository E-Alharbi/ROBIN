package PMBPP.Utilities;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import org.json.simple.parser.ParseException;

import PMBPP.Data.Preparation.mtzdump;
import PMBPP.Log.Log;
import PMBPP.ML.Model.Parameters;

public class MTZReader {
	static String readFile(String path, Charset encoding)
			  throws IOException
			{
			  byte[] encoded = Files.readAllBytes(Paths.get(path));
			  return new String(encoded, encoding);
			}
	public static void main(String[] args) throws IOException, NumberFormatException, IllegalArgumentException,
			IllegalAccessException, ParseException {
		// TODO Auto-generated method stub

		
				  MTZReader mtz= new MTZReader("/Volumes/PhDHardDrive/EditorsRevision-2/Datasets/NO-NCS/1o6a-1.9-parrot-noncs.mtz");

				  System.out.println(mtz.GetColLabels());
				  System.out.println(mtz.Spacegroup());
				  System.out.println(mtz.Cell());
		
		
		
		/*
		  String content = new MTZReader().readFile("/Volumes/PhDHardDrive/EPData/phasesPHIB/failures", StandardCharsets.US_ASCII);
		  System.out.println(content);
		String [] lines = content.split("\n");
		String script="";
		for (int i=0 ; i < lines.length;++i) {
			script+="rm -r "+lines[i]+" \n";
		}
		try (PrintWriter out = new PrintWriter("/Volumes/PhDHardDrive/EPData/phasesPHIB/ToRemove.sh")) {
		    out.println(script);
		}
		*/
		/*
		 * long startTime = System.currentTimeMillis();
		 * 
		 * MTZReader mtz= new MTZReader("2hsb-1.9-parrot-noncs.mtz");
		 * System.out.println(mtz.GetResolution()); long stopTime =
		 * System.currentTimeMillis(); long elapsedTime = stopTime - startTime;
		 * System.out.println(elapsedTime);
		 */

		/*
		 * ExcelLoader f = new ExcelLoader(); Vector<ExcelContents> Excel = new
		 * Vector<ExcelContents>();
		 * 
		 * Excel = f.ReadExcel("Buccaneeri1I5-1.xlsx"); int count=0; Parameters.Log="F";
		 * for(File m : new FilesUtilities().ReadMtzList("NO-NCS")) { String
		 * CaseName=m.getName().replaceAll("."+FilenameUtils.getExtension(m.getName()),
		 * ""); MTZReader mtz =new MTZReader(m.getAbsolutePath()); //String
		 * Reso=CaseName.substring(5,8); double resoinfo = new
		 * mtzinfo().GetReso(m.getAbsolutePath()) ; String Reso =
		 * BigDecimal.valueOf(resoinfo).setScale(2,RoundingMode.HALF_UP).toString();
		 * if(!mtz.GetResolution().equals(Reso)) { count++;
		 * System.out.println(m.getName()); System.out.println(mtz.GetResolution());
		 * System.out.println(Reso); } } System.out.println(count);
		 * 
		 */

	}

	public MTZReader() {

	}

	void ValidateResoForMR(String MRDatasets) throws IOException, NumberFormatException, ParseException { // use to test
																											// GetResolution()
		int count = 0;
		Parameters.setPhases ( "model.HLA,model.HLB,model.HLC,model.HLD");
		Parameters.setLog ( "F");
		for (File m : new FilesUtilities().FilesByExtension("/Volumes/PhDHardDrive/MRDatasets/DatasetsForBuilding",".mtz")) {

			MTZReader mtz = new MTZReader(m.getAbsolutePath());

			double resoinfo = new mtzdump().GetReso(m.getAbsolutePath());
			String Reso = BigDecimal.valueOf(resoinfo).setScale(2, RoundingMode.HALF_UP).toString();

			if (!mtz.GetResolution().equals(Reso)) {
				System.out.println(m.getName());
				System.out.println(Reso);
				System.out.println(mtz.GetResolution());
				count++;
			}

		}
		System.out.println(count);
	}

	byte[] mtzbytes;
	int StartOfHeaderRecord = -1;

	public MTZReader(String MTZ) throws IOException {
		Path path = Paths.get(MTZ);
		byte[] fileContents = Files.readAllBytes(path);
		mtzbytes = fileContents;
		StartOfHeaderRecord = GetStartOfResoRecord();

		if (StartOfHeaderRecord == -1)
			new Log().Error(this, "Cannot read resolution");
	}

	int GetStartOfResoRecord() {

		// Start reading from bottom is faster because header information are stored
		// after reflections data which is the big part of the file. Test on MAC and
		// reading from top take around 600 milliseconds compared to 67 milliseconds
		for (int b = mtzbytes.length; b >= 0; b = b - 4) {
			byte[] subarray = Arrays.copyOfRange(mtzbytes, b - 4, b);
			String str = new String(subarray, StandardCharsets.UTF_8);
			// System.out.println("str "+str);

			if (str.equals("RESO")) {

				return b;
			}
		}
		return -1;
	}
	
	int GetStartOfSYMINFRecord() {

		// Start reading from bottom is faster because header information are stored
		// after reflections data which is the big part of the file. Test on MAC and
		// reading from top take around 600 milliseconds compared to 67 milliseconds
		/*
		for (int b = 0; b < mtzbytes.length;  b++) {
			byte[] subarray = Arrays.copyOfRange(mtzbytes, b , b+6);
			String str = new String(subarray, StandardCharsets.UTF_8);
			 System.out.println("str "+str);
			 System.out.println("b "+b);
			if (str.equals("SYMINF")) {

				return b;
			}
		}
		return -1;
		*/
		
		for (int b = mtzbytes.length; b >= 6; b--) {
			byte[] subarray = Arrays.copyOfRange(mtzbytes, b - 6, b);
			String str = new String(subarray, StandardCharsets.UTF_8);
			 //System.out.println("str "+str);
			// System.out.println("b "+b);
			if (str.equals("SYMINF")) {

				return b;
			}
		}
		return -1;
		
	}

	public String GetResolution() {
		byte[] subarray2 = Arrays.copyOfRange(mtzbytes, StartOfHeaderRecord + 22, StartOfHeaderRecord + 36);
		String str2 = new String(subarray2, StandardCharsets.UTF_8);
		double pomAsInt = Double.parseDouble(str2);
		pomAsInt = Math.sqrt(pomAsInt);
		pomAsInt = 1 / pomAsInt;

		return BigDecimal.valueOf(pomAsInt).setScale(2, RoundingMode.HALF_UP).toString();

	}
	public String Cell() {
		for (int b = StartOfHeaderRecord; b<mtzbytes.length; b++) {
			byte[] subarray = Arrays.copyOfRange(mtzbytes, b,b + 6);
			String str = new String(subarray, StandardCharsets.UTF_8);
			
			if(str.trim().equals("DCELL")) {
				byte[] subarray1 = Arrays.copyOfRange(mtzbytes, b+20,b + 80);
				String str1 = new String(subarray1, StandardCharsets.UTF_8);
				
				return str1;
				
			}
		}
		return "";
	}
	public String Spacegroup() {
		
		int SYMINCol=GetStartOfSYMINFRecord();
		boolean startsp=false;
		String SP="";
		for(int i=SYMINCol; i < mtzbytes.length ;++i) {
			byte[] subarray = Arrays.copyOfRange(mtzbytes,i,i + 1);
			
			if(new String(subarray, StandardCharsets.UTF_8).equals("'") && startsp==true) {
				break;
			}
			if(startsp==true) {
				SP+=new String(subarray, StandardCharsets.UTF_8);
				
			}
			if(new String(subarray, StandardCharsets.UTF_8).equals("'")) {
				startsp=true;
			}
		}
		
		
		return SP;
		
	}
	
	public HashMap<String,Vector<String >> GetColLabels() {
		
		
		int StartByte=0;
		for (int b = StartOfHeaderRecord; b<mtzbytes.length; b++) {
			byte[] subarray = Arrays.copyOfRange(mtzbytes, b,b + 3);
			String str = new String(subarray, StandardCharsets.UTF_8);
			if (str.equals("COL")) {
				StartByte=b;
				
				break;
			}
		}
		
		String Label="";
		
		int CountDependentCol=0;
		int NumberOfDependentCol=1;
		HashMap<String,Vector<String >> ColLabels= new HashMap<String, Vector<String> >();
		for (int b = StartByte; b < mtzbytes.length; b=b+80) {
			byte[] subarray = Arrays.copyOfRange(mtzbytes, b,b + 30);
			String str = new String(subarray, StandardCharsets.UTF_8);
			
			if(str.contains("COLUMN")) {
				
			
			
			
			byte[] COLType = Arrays.copyOfRange(mtzbytes, b + 30, b+40);
			String COLTypeSTR = new String(COLType, StandardCharsets.UTF_8);
			
			
			if(COLTypeSTR.trim().equals("A") && CountDependentCol==0) {
				NumberOfDependentCol=4;
			}
			if(COLTypeSTR.trim().equals("H") && CountDependentCol==0) {
				NumberOfDependentCol=3;
			}
			if(!COLTypeSTR.trim().equals("H")&& !COLTypeSTR.trim().equals("A") && CountDependentCol==0) {
				NumberOfDependentCol=1;
			}
			
	
			
			
		
				if(Label.trim().length()!=0)
					Label+=","+str.trim();
				else
				Label=str.trim();
				CountDependentCol++;
				
				
				if(NumberOfDependentCol==CountDependentCol) {
					Vector<String>  temp = new Vector<String>();
					
					if(ColLabels.containsKey(COLTypeSTR.trim()))
				    temp=ColLabels.get(COLTypeSTR.trim());
					Label=Label.replaceAll("COLUMN", "").trim();
					temp.add(Label.trim());
					
					ColLabels.put(COLTypeSTR.trim(), temp);
					
					Label="";
					CountDependentCol=0;
				}
				
				
				
				
				
				
		}
		}
		
		return ColLabels;
	}
	
}
