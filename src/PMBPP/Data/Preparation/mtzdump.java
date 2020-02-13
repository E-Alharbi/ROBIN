package PMBPP.Data.Preparation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import PMBPP.Log.Log;

public class mtzdump {
	/*
	 * Parsing resolution from ccp4 mtzdump tool
	 */
	public static void main(String[] args) throws IOException {

		// Example
		String[] command = { "/bin/bash", "source", "/ccp4-7.0/setup-scripts/ccp4.setup-sh" };

		Runtime.getRuntime().exec(command);

		new mtzdump().GetReso("4M9H-1.9-parrot-noncs.mtz");
	}

	public double GetReso(String mtzin) throws IOException {

		String st = null;
		String Chltofom = System.getenv("CCP4") + "/share/python/CCP4Dispatchers/mtzdump.py";
		String[] callAndArgs = { "python", Chltofom, "hklin", mtzin };

		String EOF = "<<< EOF";
		Process p = new ProcessBuilder(callAndArgs).start();
		OutputStream out = p.getOutputStream();
		out.write(EOF.getBytes());
		out.close();

		BufferedReader stdInput = new BufferedReader(new

		InputStreamReader(p.getInputStream()));

		BufferedReader stdError = new BufferedReader(new

		InputStreamReader(p.getErrorStream()));

		double Reso = -1;
		String Resolution = "";
		boolean collectTxt = false;
		while ((st = stdInput.readLine()) != null) {

			if (st.contains("*  Resolution Range :"))
				collectTxt = true;
			if (st.contains("* Sort Order :"))
				collectTxt = false;
			if (collectTxt == true)
				Resolution += st;

		}

		while ((st = stdError.readLine()) != null) {

			System.out.println(st);

		}

		if (Resolution.trim().length() != 0)
			Reso = Double.parseDouble(Resolution.split("-")[1].split("A")[0]);

		if (Reso == -1) {
			new Log().Error(this, "unable to parse resolution");

		}
		new Log().Info(this, "Resolution " + Reso);
		return Reso;

	}
}
