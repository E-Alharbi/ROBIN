package PMBPP.Utilities;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/*
 * Write and read txt file from or to string
 */
public class TxtFiles {

	public void WrtieStringToTxtFile(String File, String Txt) throws FileNotFoundException {
		try (PrintWriter out = new PrintWriter(File)) {
			out.println(Txt);
		}
	}

	public String readFileAsString(String filePath) throws IOException {
		StringBuffer fileData = new StringBuffer();
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
		}
		reader.close();
		return fileData.toString();
	}
}
