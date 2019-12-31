package PMBPP.ML.Model;

import java.util.Vector;

import PMBPP.Log.Log;
import PMBPP.Validation.CustomException;

public class Parameters {
	
	/*
	 * Each parameter must have getter and setter or the value will not be read from command line 
	 */
 
	public static double getClassLevelForRfree() {
		return ClassLevelForRfree;
	}
	public static void setClassLevelForRfree(String classLevelForRfree) {
		ClassLevelForRfree = Double.parseDouble(classLevelForRfree);
	}
	public static double getClassLevel(String Header) throws CustomException {
		
		if(Header.equals("R-work"))
			return Parameters.ClassLevelForRwork;
		if(Header.equals("R-free"))
			return Parameters.ClassLevelForRfree;
		if(Header.equals("Completeness"))
			return Parameters.ClassLevelForCompleteness;
		
		throw new CustomException(Header+ " This header is not recognized");
		
	}
public static String getClassificationDatasetsFolderName() {
		return ClassificationDatasetsFolderName;
	}
	public static void setClassificationDatasetsFolderName(String classificationDatasetsFolderName) {
		ClassificationDatasetsFolderName = classificationDatasetsFolderName;
	}
public static void setClassLevel(String Header, double val) {
		
		if(Header.equals("R-work"))
			 Parameters.ClassLevelForRwork=val;
		if(Header.equals("R-free"))
			 Parameters.ClassLevelForRfree=val;	
		if(Header.equals("Completeness"))
		 Parameters.ClassLevelForCompleteness=val;
	}
	
public static double getMaxClassLevel(String Header) throws CustomException {
		
		if(Header.equals("R-work"))
			return Parameters.MaxClassLevelForRwork;
		if(Header.equals("R-free"))
			return Parameters.MaxClassLevelForRfree;	
		if(Header.equals("Completeness"))
			return Parameters.MaxClassLevelForCompleteness;
		
		throw new CustomException(Header+ " This header is not recognized");
	}

public static void  setMaxClassLevel(String Header, double val) {
	
	if(Header.equals("R-work"))
		 Parameters.MaxClassLevelForRwork=val;
	if(Header.equals("R-free"))
		 Parameters.MaxClassLevelForRfree=val;	
	if(Header.equals("Completeness"))
		Parameters.MaxClassLevelForCompleteness=val;
}
public static double getMinClassLevel(String Header) throws CustomException {
	
	if(Header.equals("R-work"))
		return Parameters.MinClassLevelForRwork;
	if(Header.equals("R-free"))
		return Parameters.MinClassLevelForRfree;	
	if(Header.equals("Completeness"))
		return 	Parameters.MinClassLevelForCompleteness;
	
	throw new CustomException(Header+ " This header is not recognized");
}

public static void setMinClassLevel(String Header, double val) {
	
	if(Header.equals("R-work"))
		 Parameters.MinClassLevelForRwork=val;
	if(Header.equals("R-free"))
		 Parameters.MinClassLevelForRfree=val;	
	if(Header.equals("Completeness"))
		Parameters.MinClassLevelForCompleteness=val;
	
}
	public static double getMaxClassLevelForRFactors() {
		return MaxClassLevelForRwork;
	}
	public static void setMaxClassLevelForRFactors(String maxClassLevelForRFactors) {
		MaxClassLevelForRwork = Double.parseDouble(maxClassLevelForRFactors);
	}
	public static double getMaxClassLevelForCompleteness() {
		return MaxClassLevelForCompleteness;
	}
	public static void setMaxClassLevelForCompleteness(String maxClassLevelForCompleteness) {
		MaxClassLevelForCompleteness = Double.parseDouble(maxClassLevelForCompleteness);
	}
	public static String getAttCSV() {
		return AttCSV;
	}
	public static void setAttCSV(String attCSV) {
		AttCSV = attCSV;
	}
	public static String getTrainedModelsPath() {
		return TrainedModelsPath;
	}
	public static void setTrainedModelsPath(String trainedModelsPath) {
		TrainedModelsPath = trainedModelsPath;
	}
	public static double[] getInstanceValue1() {
		return instanceValue1;
	}
	public static void setInstanceValue1(double[] instanceValue1) {
		Parameters.instanceValue1 = instanceValue1;
	}
	public static boolean isUsecfft() {
		return Usecfft;
	}
	public static void setUsecfft(boolean usecfft) {
		Usecfft = usecfft;
	}
	public static String getFilterModels() {
		return FilterModels;
	}
	public static void setFilterModels(String filterModels) {
		FilterModels = filterModels;
	}
	public static Vector<String> getFilteredModels() {
		return FilteredModels;
	}
	public static void setFilteredModels(Vector<String> filteredModels) {
		FilteredModels = filteredModels;
	}
	public static double getClassLevelForCompleteness() {
		return ClassLevelForCompleteness;
	}
	public static void setClassLevelForCompleteness(String classLevelForCompleteness) {
		ClassLevelForCompleteness = Double.parseDouble(classLevelForCompleteness);
	}
	public static double getClassLevelForRFactors() {
		return ClassLevelForRwork;
	}
	public static void setClassLevelForRFactors(String classLevelForRFactors) {
		ClassLevelForRwork = Double.parseDouble(classLevelForRFactors);
	}
	public static String getFeatuers() {
		return Features;
	}
	public static void setFeatuers(String features) {
		Features = features;
	}
	public static String getMeasurementUnitsToPredict() {
		return MeasurementUnitsToPredict;
	}
	public static void setMeasurementUnitsToPredict(String measurementUnitsToPredict) {
		MeasurementUnitsToPredict = measurementUnitsToPredict;
	}
	public static String getModelFolderName() {
		return ModelFolderName;
	}
	public static void setModelFolderName(String modelFolderName) {
		ModelFolderName = modelFolderName;
	}
	public static String getCompressedModelFolderName() {
		return CompressedModelFolderName;
	}
	public static void setCompressedModelFolderName(String compressedModelFolderName) {
		CompressedModelFolderName = compressedModelFolderName;
	}
	public static String getPhases() {
		return Phases;
	}
	public static void setPhases(String phases) {
		Phases = phases;
	}
	public static String getNumberOfTrees() {
		return NumberOfTrees;
	}
	public static void setNumberOfTrees(String numberOfTrees) {
		NumberOfTrees = numberOfTrees;
	}
	public static String getMR() {
		return MR;
	}
	public static void setMR(String mR) {
		if(mR.equals("T") || mR.equals("F"))
		MR = mR;
		else
			new Log().Error(new Parameters(), "MR should be either T or F");
	}
	public static String getSequenceIdentity() {
		return SequenceIdentity;
	}
	public static void setSequenceIdentity(String sequenceIdentity) {
		if(Double.parseDouble(sequenceIdentity) >=0 && Double.parseDouble(sequenceIdentity) <=1)
		SequenceIdentity = sequenceIdentity;
		else
		{
			
			new Log().Error(new Parameters(),"Sequence identity should be between 0 and 1. For example, if the sequence identity is 53, then must be typed in as 0.53 ");
			
		}
		 
	}
	
	public static void CheckDependency() {
		if(!SequenceIdentity.equals("-1") && MR.equals("F")) {
			new Log().Error(new Parameters(),"If you are using sequence identity, then you have to set MR=T");
			
		}
		
		if(MultipleModels.equals("T")) {
			if(StartNumberOfTrees.equals("-1")) {
				new Log().Error(new Parameters(),"Please set StartNumberOfTrees");
				
			}
			if(IncreaseNumberOfTrees.equals("-1")) {
				new Log().Error(new Parameters(),"Please set IncreaseNumberOfTrees");
				
			}
			if(MaxNumberOfTrees.equals("-1")) {
				new Log().Error(new Parameters(),"Please set MaxNumberOfTrees");
				
			}
		}
	}
	
 
 public static String AttCSV="None";
 public static String TrainedModelsPath="./TrainedModels";
 public static double[] instanceValue1=null;
 public static boolean Usecfft=true;
 public static String FilterModels="F";
 public static double getMinClassLevelForRFactors() {
	return MinClassLevelForRwork;
}
public static void setMinClassLevelForRFactors(String minClassLevelForRFactors) {
	MinClassLevelForRwork = Double.valueOf(minClassLevelForRFactors);
}
public static double getMinClassLevelForCompleteness() {
	return MinClassLevelForCompleteness;
}
public static void setMinClassLevelForCompleteness(String minClassLevelForCompleteness) {
	MinClassLevelForCompleteness = Double.valueOf(minClassLevelForCompleteness);
}


public static Vector <String>  FilteredModels =  new Vector <String>();
 public static double ClassLevelForCompleteness=15;
 public static double ClassLevelForRwork=0.05;
 public static double ClassLevelForRfree=0.05;
 public static double MaxClassLevelForRfree=-1;
 public static double MaxClassLevelForRwork=-1;
 public static double MaxClassLevelForCompleteness=-1;
 public static double MinClassLevelForRwork=-1;
 public static double MinClassLevelForRfree=-1;
 public static double MinClassLevelForCompleteness=-1;
 public static double MinNumClassesLevel=-1;
 public static double MaxNumClassesLevel=-1;


public static double getMaxNumClassesLevel() {
	return MaxNumClassesLevel;
}
public static void setMaxNumClassesLevel(String maxNumClassesLevel) {
	MaxNumClassesLevel = Double.parseDouble(maxNumClassesLevel);
}
public static double getMinNumClassesLevel() {
	return MinNumClassesLevel;
}
public static void setMinNumClassesLevel(String minNumClassesLevel) {
	MinNumClassesLevel = Double.parseDouble(minNumClassesLevel);
}


public static String Features="RMSD,Skew,Resolution,Max,Min"; // features used  
// public static String Featuers="Skew,Resolution,Max"; // features used  
 public static String MeasurementUnitsToPredict="Completeness,R-free,R-work";
 public static String ModelFolderName="PredictionModels";
 public static String CompressedModelFolderName="PredictionModels.zip";
 public static String Phases="parrot.ABCD.A,parrot.ABCD.B,parrot.ABCD.C,parrot.ABCD.D";
 public static String NumberOfTrees="100";
 public static String MR="F";
 public static String getMultipleModels() {
	return MultipleModels;
}
public static void setMultipleModels(String multipleModels) {
	MultipleModels = multipleModels;
}
public static String getStartNumberOfTrees() {
	return StartNumberOfTrees;
}
public static void setStartNumberOfTrees(String startNumberOfTrees) {
	StartNumberOfTrees = startNumberOfTrees;
}
public static String getIncreaseNumberOfTrees() {
	return IncreaseNumberOfTrees;
}
public static void setIncreaseNumberOfTrees(String increaseNumberOfTrees) {
	IncreaseNumberOfTrees = increaseNumberOfTrees;
}
public static String getMaxNumberOfTrees() {
	return MaxNumberOfTrees;
}
public static void setMaxNumberOfTrees(String maxNumberOfTrees) {
	MaxNumberOfTrees = maxNumberOfTrees;
}
public static String getSplitOnStructureLevel() {
	return SplitOnStructureLevel;
}
public static void setSplitOnStructureLevel(String splitOnStructureLevel) {
	SplitOnStructureLevel = splitOnStructureLevel;
}


 public static String SequenceIdentity="-1";
 public static String MultipleModels="F";
 public static String StartNumberOfTrees="-1";
 public static String IncreaseNumberOfTrees="-1";
 public static String MaxNumberOfTrees="-1";
 public static String colinfo="FP,SIGFP";
 public static String getColinfo() {
	return colinfo;
}
public static void setColinfo(String colinfo) {
	Parameters.colinfo = colinfo;
}
public static String getOptimizeClasses() {
	return OptimizeClasses;
}
public static void setOptimizeClasses(String optimizeClasses) {
	OptimizeClasses = optimizeClasses;
}


public static String SplitOnStructureLevel="F";
 public static String OptimizeClasses="T";
 public static String Log="T";
 public static String ClassificationDatasetsFolderName="ClassificationDatasets";
public static String getLog() {
	return Log;
}
public static void setLog(String log) {
	Log = log;
}
}
