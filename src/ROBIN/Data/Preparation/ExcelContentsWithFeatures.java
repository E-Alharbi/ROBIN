package ROBIN.Data.Preparation;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.Vector;

import Comparison.Analyser.ExcelContents;

/*
 * An extended class from ExcelContents to include features  
 */

public class ExcelContentsWithFeatures extends ExcelContents {

	public Features CM = new Features();

	public Vector<ExcelContentsWithFeatures> Addall(Vector<ExcelContents> Excel)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Vector<ExcelContentsWithFeatures> Temp = new Vector<ExcelContentsWithFeatures>();

		for (ExcelContents e : Excel) {
			ExcelContentsWithFeatures temp = new ExcelContentsWithFeatures();

			Field[] Fields = e.getClass().getFields();
			Field[] Fields2 = temp.getClass().getFields();
			for (Field F : Fields) {
				for (Field F2 : Fields2) {
					if (F.getName().equals(F2.getName())) {

						F2.set(temp, F.get(e));
					}
				}

			}

			Temp.add(temp);
		}

		return Temp;

	}

	public static Comparator<ExcelContentsWithFeatures> SortingByPDB = new Comparator<ExcelContentsWithFeatures>() {

		public int compare(ExcelContentsWithFeatures Ele1, ExcelContentsWithFeatures Ele2) {

			String Ele11 = Ele1.PDB_ID;
			String Ele22 = Ele2.PDB_ID;

			// ascending order
			return Ele11.compareTo(Ele22);

			// descending order
			// return Ele11.compareTo(Ele22);
		}

	};

}
