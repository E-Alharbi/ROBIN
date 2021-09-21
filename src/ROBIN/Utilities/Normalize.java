package ROBIN.Utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;

import ROBIN.ML.Model.Parameters;

public class Normalize {

	public String NormalizeVal(String Val , String StructureEvaluation, boolean round ) {
		if(Parameters.getNormalizeStructureEvaluationInErrorTable().equals("T")) {
			if(Parameters.getStructureEvaluationToBeNormalized().contains(StructureEvaluation)) {
				if(round==true)
				Val= new BigDecimal(Val).divide(new BigDecimal("100")).setScale(2,  RoundingMode.HALF_UP).toString();
				else
					Val= new BigDecimal(Val).divide(new BigDecimal("100")).toString();
	
			}
		}
		return Val;
	}
	
}
