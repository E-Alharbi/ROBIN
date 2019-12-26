package PMBPP.Validation;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Vector;

import PMBPP.ML.Model.Parameters;
import PMBPP.Utilities.FilesUtilities;

public class SimilarDatasets {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		Vector<String>  datasets= new Vector<String>();
		for(File CSV : new FilesUtilities().ReadFilesList("/Users/emadalharbi/Downloads/TestCSV")) {
			
			Path path = Paths.get(CSV.getAbsolutePath());
			List<String> Lines= Files.readAllLines(path);
			
			
			for (String Line : Lines) {
				int count=0;
				//System.out.println(Line);
				for(File CSV2 : new FilesUtilities().ReadFilesList("/Users/emadalharbi/Downloads/TestCSV")) {
					Path path2 = Paths.get(CSV2.getAbsolutePath());
					List<String> Lines2= Files.readAllLines(path2);
					boolean found=false;
					for (String Line2 : Lines2) {
						if(Line.equals(Line2)) {
							found=true;
							break;
							
						}
					}
					if(found==true)
						count++;
				}
				
			if(count==15) {
				datasets.add(Line);
				System.out.println(count);
				System.out.println(Line);
			}
			
			}
			
		}
		for(String PDB : datasets)
		System.out.println(PDB);
	}

}
