package PMBPP.Data.Preparation;

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
import PMBPP.ML.Model.Parameters;





public class ExcelContentsWithFeatures extends ExcelContents  {

	public Features CM = new Features();
	
	Vector<ExcelContentsWithFeatures> Addall(Vector<ExcelContents> Excel) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		Vector<ExcelContentsWithFeatures> Temp = new Vector<ExcelContentsWithFeatures>();
		
		for(ExcelContents e : Excel) {
			ExcelContentsWithFeatures temp= new ExcelContentsWithFeatures();
			
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
	

	
}
