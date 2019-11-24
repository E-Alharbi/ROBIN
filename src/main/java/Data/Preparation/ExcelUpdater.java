package Data.Preparation;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

import Comparison.Analyser.ExcelContents;
import Comparison.Analyser.ExcelLoader;


public class ExcelUpdater {

	public static void main(String[] args) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException {
		// TODO Auto-generated method stub
		File [] files =  new File(args[0]).listFiles();
		String PathToDataset=args[1];
		Vector<ExcelContentsWithCfft> Excel2 = new Vector<ExcelContentsWithCfft>();
		for(File e :files ) {
		ExcelLoader f = new ExcelLoader();
		Vector<ExcelContents> Excel = new Vector<ExcelContents>();
		
		Excel =  f.ReadExcel(e.getAbsolutePath());
		
		Excel2=	new ExcelContentsWithCfft().Addall(Excel);
		
		int Count=1;
		 for(ExcelContentsWithCfft EE : Excel2) {
			 
			 EE.CM=new cfft().Cfft(PathToDataset+EE.PDB_ID+".mtz");
			 System.out.println(Count + " out of "+ Excel2.size());
			 Count++;
		 }
		 ExcelContentsWithCfft EC= new ExcelContentsWithCfft();
		 if(args.length>2)
		 EC.PathToSaveCSV=args[2];
		 EC.WriteToCSV(Excel2,e.getName());
		
	}
		
	}

}
