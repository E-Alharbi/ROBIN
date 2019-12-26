package PMBPP.Data.Preparation;

import java.lang.reflect.Field;

public class Features {

	@Override
	public String toString() {
		return "CfftMeasures [RMSD=" + RMSD + ", Skew=" + Skew + ", Min=" + Min + ", Max=" + Max + "]";
	}
	public double RMSD;
	public double Skew;
	public double Min;
	public double Max;
	public double Resolution;
	public double SequenceIdentity; // MR
	public Object GetFeatureByName(String Name) throws IllegalArgumentException, IllegalAccessException {
		for (Field field : this.getClass().getDeclaredFields()) {
	        if(Name.equals(field.getName()))
	        	return (double)field.get(this); 
	        
	       
	    }
		return null;
	}
	
	void SetFeatureByName(String Name, double Val) throws IllegalArgumentException, IllegalAccessException {
		for (Field field : this.getClass().getDeclaredFields()) {
	        if(Name.equals(field.getName()))
	        field.set(this, Val);
	       
	    }
		
	}
	
	boolean isEmpty() throws IllegalArgumentException, IllegalAccessException {
		
		//if all fields are zero then it is empty. The fields cannot be all zeros 
		int NumberOffields=0;
		for (Field field : this.getClass().getDeclaredFields()) {
	       
	        if((double)field.get(this)==0) {
	        	NumberOffields++;
	        }
	       
	    }
		
		if(NumberOffields==this.getClass().getDeclaredFields().length)
			return true;
		
		return false;
	}
	
	
}
