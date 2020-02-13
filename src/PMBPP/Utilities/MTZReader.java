package PMBPP.Utilities;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.json.simple.parser.ParseException;

import PMBPP.Data.Preparation.mtzdump;
import PMBPP.Log.Log;
import PMBPP.ML.Model.Parameters;

public class MTZReader {

	public static void main(String[] args) throws IOException, NumberFormatException, IllegalArgumentException,
			IllegalAccessException, ParseException {
		// TODO Auto-generated method stub

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
		Parameters.Phases = "model.HLA,model.HLB,model.HLC,model.HLD";
		Parameters.Log = "F";
		for (File m : new FilesUtilities().ReadMtzList("/Volumes/PhDHardDrive/MRDatasets/DatasetsForBuilding")) {

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

	public String GetResolution() {
		byte[] subarray2 = Arrays.copyOfRange(mtzbytes, StartOfHeaderRecord + 22, StartOfHeaderRecord + 36);
		String str2 = new String(subarray2, StandardCharsets.UTF_8);
		double pomAsInt = Double.parseDouble(str2);
		pomAsInt = Math.sqrt(pomAsInt);
		pomAsInt = 1 / pomAsInt;

		return BigDecimal.valueOf(pomAsInt).setScale(2, RoundingMode.HALF_UP).toString();

	}
}
