package PMBPP.Data.Preparation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.util.Vector;

import PMBPP.Log.Log;
import PMBPP.ML.Model.Parameters;
import PMBPP.Utilities.MTZReader;

// Class to run cfft tool from ccp4
public class cfft {

	public static void main(String[] args) throws IOException {

		// Example to run multiple data sets
		// String Path="/NO-NCS/"; // Path to data sets
		/*
		 * for(File M : new FilesUtilities().ReadFilesList(Path)) {
		 * if(M.getName().contains(".mtz")) { cfft c = new cfft();
		 * Parameters.Phases="HLA,HLB,HLC,HLD";
		 * 
		 * System.out.print(M+"\t"+c.Cfft(M.getAbsolutePath()).Max+"\t");
		 * Parameters.colinfo="parrot.ABCD.A,parrot.ABCD.B,parrot.ABCD.C,parrot.ABCD.D";
		 * System.out.println(c.Cfft(M.getAbsolutePath()).Max+"\t");} }
		 */

		// Example: single data set
		/*
		for(int i=0 ; i < 10 ; ++i) {
		Instant start = Instant.now();
		Parameters.colinfo="SIGFP.F_sigF.F,SIGFP.F_sigF.sigF";
		Parameters.Phases="HLA,HLB,HLC,HLD";
		new cfft().Cfft("/Users/emadalharbi/eclipse-workspaceC++Ph/TestJNI/Debug/final.mtz");
		Instant finish = Instant.now();
		long timeElapsed = Duration.between(start, finish).toMillis();
		System.out.println(timeElapsed);
		}
		*/
		Parameters.setPhases("model.HLA,model.HLB,model,HLC,model.HLD");
		new cfft().Cfft("/Volumes/PhDHardDrive/MRDatasets/DatasetsForBuilding/1BD9-2.1-parrot-noncs.mtz");

	}

	public Features Cfft(String mtzin) throws IOException {

		
		String st = null;
		String PathToCfft = System.getenv("CCP4") + "/share/python/CCP4Dispatchers/cfft.py";
		String[] callAndArgs = { "python", PathToCfft, "-mtzin", mtzin, "-colin-fo", Parameters.getColinfo(), "-colin-hl",
				Parameters.getPhases(), "-stats", };

		Process p = Runtime.getRuntime().exec(callAndArgs);

		BufferedReader stdInput = new BufferedReader(new

		InputStreamReader(p.getInputStream()));

		BufferedReader stdError = new BufferedReader(new

		InputStreamReader(p.getErrorStream()));

		Features CM = new Features();

		while ((st = stdInput.readLine()) != null) {
			
			if (st.contains("About mean (rmsd)")) {

				CM.RMSD = Double
						.parseDouble(st.substring(st.indexOf("About mean (rmsd)")).replaceAll("[^0-9\\\\.]+", ""));
				if (st.substring(st.indexOf("About mean (rmsd)")).contains("-")) // Parsing statement removes negative
																					// sign
					CM.RMSD = -CM.RMSD;

			}
			if (st.contains("About mean (skew)")) {

				CM.Skew = Double
						.parseDouble(st.substring(st.indexOf("About mean (skew)")).replaceAll("[^0-9\\\\.]+", ""));

				if (st.substring(st.indexOf("About mean (skew)")).contains("-")) // Parsing statement removes negative
																					// sign
					CM.Skew = -CM.Skew;

			}
			if (st.contains("Range:")) {

				CM.Min = Double
						.parseDouble(st.substring(st.indexOf("Min"), st.indexOf("Max")).replaceAll("[^0-9\\\\.]+", ""));
				CM.Max = Double.parseDouble(st.substring(st.indexOf("Max")).replaceAll("[^0-9\\\\.]+", ""));

				if (st.substring(st.indexOf("Min"), st.indexOf("Max")).contains("-")) // Parsing statement removes
																						// negative sign
					CM.Min = -CM.Min;
				if (st.substring(st.indexOf("Max")).contains("-"))
					CM.Max = -CM.Max;
			}
			if (st.contains("Number of points in cell:")) {
				
				CM.PointsInCell=Double.parseDouble(st.split(":")[1].trim());
			}
		}

		boolean Error = false;
		while ((st = stdError.readLine()) != null) {

			System.out.println(st);
			Error = true;

		}
		if (Error == true) {
			
			new Log().Error(this,
					"Cfft error: Please fix the above error. Probably, you are using wrong phases or wrong FP,SIGFP! (Example colinfo=FP,SIGFP Phases=HLA,HLB,HLC,HLD) ");

		}

		new Log().Info(this, "RMSD " + CM.RMSD + " Skew " + CM.Skew + " Max " + CM.Max + " Min " + CM.Min);

		return CM;

	}
}
