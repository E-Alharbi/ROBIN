package PMBPP.Utilities;

import java.util.Vector;

import Comparison.Analyser.ExcelContents;
import Comparison.Analyser.ExcelLoader;

public class CompareExcel {

	public static void main(String[] args) {
		// TODO Auto-generated method stub


ExcelLoader f = new ExcelLoader();
Vector<ExcelContents> Excel1 =  f.ReadExcel("/Users/emadalharbi/Downloads/TestSplitOnStrectureLevel/Excel/Buccaneeri1I5.xlsx");
Vector<ExcelContents> Excel2 =  f.ReadExcel("/Users/emadalharbi/Downloads/PairwiseRunningAndIndvPipelines/ExcelFiles/AllExFaliedCasesExcludedBuccaneerDevSet/noncs/Buccaneer.xlsx");

for(ExcelContents E : Excel1) {
	boolean found=false;
	for (ExcelContents E2 : Excel2) {
		if(E.PDB_ID.equals(E2.PDB_ID)) {
			found=true;
		}
	}
	if(found==false) {
		System.out.println(E.PDB_ID);
	}
}
	}

}
