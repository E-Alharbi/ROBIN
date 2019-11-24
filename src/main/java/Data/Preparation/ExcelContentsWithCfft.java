package Data.Preparation;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Vector;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

import Comparison.Analyser.ExcelContents;





public class ExcelContentsWithCfft extends ExcelContents  {

	public CfftMeasures CM = new CfftMeasures();
	public String PathToSaveCSV="./";
	Vector<ExcelContentsWithCfft> Addall(Vector<ExcelContents> Excel) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		Vector<ExcelContentsWithCfft> Temp = new Vector<ExcelContentsWithCfft>();
		
		for(ExcelContents e : Excel) {
			ExcelContentsWithCfft temp= new ExcelContentsWithCfft();
			
			Field [] Fields = e.getClass().getFields();
			Field [] Fields2 = temp.getClass().getFields();
			for(Field F : Fields) {
				for(Field F2 : Fields2) {
					if(F.getName().equals(F2.getName())) {
						//F2=F.get(e);
						F2.set(temp, F.get(e));
					}
				}
				

			}
			
			
			//BeanUtils.copyProperties(temp, e);
			
			//System.out.println(temp.toString());
			//System.out.println(temp.toString());
			Temp.add(temp);
		}
		
		return Temp;
		
	}
	
	void WriteToCSV(Vector<ExcelContentsWithCfft> Excel , String Pipeline) throws FileNotFoundException {
		
		//String CSV="PDB,R-free,R-work,Completeness,RMSD,Skew,Max,Min,Pipeline,Resolution\n";
		//String CSV="PDB,R-free,R-work,Completeness,RMSD,Skew,Max,Min,Resolution\n";
		String CSV="RMSD,Skew,Resolution,Max,Min,Completeness,R-free,R-work\n";
		
		for(ExcelContentsWithCfft E : Excel) {
//			CSV+=E.PDB_ID+","+E.R_free0Cycle+","+E.R_factor0Cycle+","+E.Completeness+","+E.CM.RMSD+","+E.CM.Skew+","+E.CM.Max+","+E.CM.Min+","+Pipeline+","+E.Resolution+"\n";
			//CSV+=E.R_free0Cycle+","+E.R_factor0Cycle+","+E.Completeness+","+E.CM.RMSD+","+E.CM.Skew+","+E.CM.Max+","+E.CM.Min+","+E.Resolution+"\n";
			
			
			CSV+=E.CM.RMSD+","+E.CM.Skew+","+E.Resolution+","+E.CM.Max+","+E.CM.Min+","+E.Completeness+","+E.R_free0Cycle+","+E.R_factor0Cycle+"\n";
		}
		try(  PrintWriter out = new PrintWriter( PathToSaveCSV+"/"+Pipeline.substring(0,Pipeline.indexOf("."))+".csv")){
		    out.println( CSV );
		}
		//System.out.println(CSV);
	}
	
}
