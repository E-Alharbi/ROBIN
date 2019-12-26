package PMBPP.Utilities;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class TxtFiles {

	public void WrtieStringToTxtFile(String File, String Txt) throws FileNotFoundException {
		try(  PrintWriter out = new PrintWriter(File )  ){
		    out.println(Txt );
		}
	}
}
