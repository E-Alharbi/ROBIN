package PMBPP.Utilities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Vector;

import Comparison.Analyser.ExcelContents;
import Comparison.Analyser.ExcelLoader;
import Comparison.Analyser.ExcelSheet;

public class Test {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub

		ExcelLoader f = new ExcelLoader();
		Vector<ExcelContents> Org = f.ReadExcel("/Users/emadalharbi/Desktop/OrginalBuccEx54ExFaliedCases/noncs/Buccaneeri1I5.xlsx");
		Vector<ExcelContents> Sy=f.ReadExcel("/Users/emadalharbi/Desktop/SyntheticBuccEx54ExFaliedCases/noncs/Buccaneeri1I5.xlsx");
		System.out.println(Org.size());
		System.out.println(Sy.size());
		Collections.shuffle(Org);
		Vector<ExcelContents> Traning= new Vector<ExcelContents>();
		Traning.addAll(Org.subList(0, 100));
		Vector<ExcelContents> Testing= new Vector<ExcelContents>();
		Testing.addAll(Org.subList(100, 148));
		System.out.println(Traning.size());
		System.out.println(Testing.size());
		
		
		for(ExcelContents elm : Sy) {
			
			for(ExcelContents elm2 : Traning) {
				if(elm.PDBIDTXT.equals(elm2.PDBIDTXT)) {
					Traning.add(elm);
					
					
					break;
				}
			}
			
		}
		
		
		for(ExcelContents elm : Sy) {
			for(ExcelContents elm2 : Testing) {
				if(elm.PDBIDTXT.equals(elm2.PDBIDTXT)) {
					Testing.add(elm);
					break;
				}
			}
		}
		System.out.println(Traning.size());
		System.out.println(Testing.size());
		new ExcelSheet().FillInExcel(Testing, "Testing");
		new ExcelSheet().FillInExcel(Traning, "Traning");
	}

}
