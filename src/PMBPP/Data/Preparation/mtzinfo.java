package PMBPP.Data.Preparation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

import PMBPP.Log.Log;

/*
 * Parsing resolution from ccp4 mtzinfo tool
 */
public class mtzinfo {

	public static void main(String[] args) throws IOException {
		// Example
		new mtzinfo().GetReso("1o6a-1.9-parrot-noncs.mtz");
	}

	public double GetReso(String mtzin) throws IOException {

		String st = null;
		String Chltofom = System.getenv("CCP4") + "/share/python/CCP4Dispatchers/mtzinfo.py";
		String[] callAndArgs = { "python", Chltofom, "HKLIN", mtzin,

		};

		Process p = new ProcessBuilder(callAndArgs).start();

		BufferedReader stdInput = new BufferedReader(new

		InputStreamReader(p.getInputStream()));

		BufferedReader stdError = new BufferedReader(new

		InputStreamReader(p.getErrorStream()));

		double Reso = -1;
		while ((st = stdInput.readLine()) != null) {
			if (st.contains("XDATA")) {
				String R = ParseReso(st);

				Reso = Double.parseDouble(R);
			}

		}

		while ((st = stdError.readLine()) != null) {

			System.out.println(st);

		}

		if (Reso == -1) {
			new Log().Error(this, "unable to parse resolution");

		}

		return Reso;

	}

	String ParseReso(String line) {

		Vector<String> LinesContents = new Vector<String>();
		String Word = "";
		for (int i = 0; i < line.length(); ++i) {
			if (line.charAt(i) != ' ') {
				Word += line.charAt(i);
			} else {

				if (Word.trim().length() != 0) // ignore spaces
					LinesContents.add(Word);
				Word = "";
			}
		}
		return LinesContents.get(8);// resolution
	}
}
