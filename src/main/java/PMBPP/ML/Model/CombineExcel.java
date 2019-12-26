package PMBPP.ML.Model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

import Comparison.Analyser.ExcelContents;
import Comparison.Analyser.ExcelLoader;
import Comparison.Analyser.ExcelSheet;

public class CombineExcel {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub

		File [] ExcelO=new File("/Users/emadalharbi/Downloads/PairwiseRunningAndIndvPipelines/52DatasetsBothSAndO/OrginalBuccInc54ExFaliedCases/noncs").listFiles();
		File [] ExcelS=new File("/Users/emadalharbi/Downloads/PairwiseRunningAndIndvPipelines/52DatasetsBothSAndO/SyntheticBuccInc54ExFaliedCases/noncs").listFiles();
	
		for(File O : ExcelO) {
			for(File S : ExcelS) {
				if(O.getName().equals(S.getName())) {
					ExcelLoader f = new ExcelLoader();
					Vector<ExcelContents> Excel1 =  f.ReadExcel(O.getAbsolutePath());
					Vector<ExcelContents> Excel2 =  f.ReadExcel(S.getAbsolutePath());
					Excel1.addAll(Excel2);
					new ExcelSheet().FillInExcel(Excel1, O.getName());
				}
			}
	}
	}

}
