package ROBIN.Data.Preparation;

import java.lang.reflect.Field;

/*
 * A class contains features use in predicting the pipelines performance  
 */
public class Features {

	

	@Override
	public String toString() {
		return "Features [RMSD=" + RMSD + ", Skew=" + Skew + ", Min=" + Min + ", Max=" + Max + ", Resolution="
				+ Resolution + ", SequenceIdentity=" + SequenceIdentity + ", PointsInCell=" + PointsInCell + "]";
	}
	
	public double RMSD;
	public double Skew;
	public double Min;
	public double Max;
	public double Resolution;
	public double SequenceIdentity; // MR
	public double PointsInCell;
	public Object GetFeatureByName(String Name) throws IllegalArgumentException, IllegalAccessException {
		for (Field field : this.getClass().getDeclaredFields()) {
			if (Name.equals(field.getName()))
				return (double) field.get(this);

		}
		return null;
	}

	void SetFeatureByName(String Name, double Val) throws IllegalArgumentException, IllegalAccessException {
		for (Field field : this.getClass().getDeclaredFields()) {
			if (Name.equals(field.getName()))
				field.set(this, Val);

		}

	}

	boolean isEmpty() throws IllegalArgumentException, IllegalAccessException {

		// if all fields are zero then it is empty. The fields cannot be all zeros
		int NumberOffields = 0;
		for (Field field : this.getClass().getDeclaredFields()) {

			if ((double) field.get(this) == 0) {
				NumberOffields++;
			}

		}

		if (NumberOffields == this.getClass().getDeclaredFields().length)
			return true;

		return false;
	}
	boolean isCfttFeaturesNotSet() {
		if(RMSD==0 && Skew==0 && Min==0 && Max==0)
			return true;
		
		return false;
	}

}
