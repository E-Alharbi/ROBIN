package ROBIN.ML.Model;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import org.apache.commons.io.FilenameUtils;

import ROBIN.Log.Log;
import ROBIN.Utilities.FilesUtilities;
import ROBIN.Utilities.LoadMLModel;
import ROBIN.Validation.CustomException;
import weka.classifiers.trees.RandomForest;

public class Parameters {

	/*
	 * Each parameter must have getter and setter or the value will not be read from
	 * command line
	 */

	private static String AttCSV = "None";
	
	private static String ClassificationDatasetsFolderName = "ClassificationDatasets";

	private static double ClassLevelForCompleteness = 15;

	private static double ClassLevelForRfree = 0.05;

	private static double ClassLevelForRwork = 0.05;

	private  static String Cluster = "F";

	private  static String colinfo = "FP,SIGFP";

	private static String CompressedModelFolderName = "PredictionModels.zip";

	private static String CompressModel="T";

	private static String Features = "RMSD,Skew,Resolution,Max,Min"; // features used

	private static Vector<String> FilteredModels = new Vector<String>();

	private  static String FilterModels = "F";

	private static String Heatmap="T";

	private static String HTMLTable="F";

	private static String IgnoreCCP4Env="F";
	private static String IgnoreCfftError="F";

	public static String getIgnoreCfftError() {
		return IgnoreCfftError;
	}
	public static void setIgnoreCfftError(String ignoreCfftError) {
		IgnoreCfftError = ignoreCfftError;
	}

	private static String IncreaseNumberOfTrees = "-1";

	private static double[] instanceValue1 = null;
	private static double[] instanceValueParrot = null;

	public static double[] getInstanceValueParrot() {
		return instanceValueParrot;
	}
	public static void setInstanceValueParrot(double[] instanceValueParrot) {
		Parameters.instanceValueParrot = instanceValueParrot;
	}

	private static String Log = "T";

	private static double Max=-1;

	private static double MaxClassLevelForCompleteness = -1;

	private static double MaxClassLevelForRfree = -1;

	private static double MaxClassLevelForRwork = -1;

	private static String MaxNumberOfTrees = "-1";

	private static double MaxNumClassesLevel = -1;

	// public static String Featuers="Skew,Resolution,Max"; // features used  
	private static String MeasurementUnitsToPredict = "Completeness,R-free,R-work";
	
	private static double Min=-1;

	private static double MinClassLevelForCompleteness = -1;

	private static double MinClassLevelForRfree = -1;

	private static double MinClassLevelForRwork = -1;

	private static double MinNumClassesLevel = -1;

	private static String ModelFolderName = "PredictionModels";

	private static String MR = "F";

	private static String MultipleModels = "F";

	private static String NormalizeStructureEvaluationInErrorTable="T";

	private static String NumberOfTrees = "100";

	private static String OptimizeClasses = "T";

	private static String PearsonsCorrelation = "F";

	//private static String Phases = "parrot.ABCD.A,parrot.ABCD.B,parrot.ABCD.C,parrot.ABCD.D";
	private static String Phases = "HLA,HLB,HLC,HLD";

	private static String PredictionConfidence  = "F";

	private static String PrepareFeatures="T";

	private static String PrintParameters="F";

	private static double Resolution=-1;

	private static double RMSD=-1;

	private static String SequenceIdentity = "-1";

	private static double Skew=-1;

	private static String SplitOnStructureLevel = "F";

	private static String StartNumberOfTrees = "-1";

	private static String StringInputSplitter=",";

	private static String StructureEvaluationToBeNormalized="Completeness";

	private static String TrainedModelsPath = "./TrainedModels";
	private static String UpdateChecking = "T";
	private static boolean Usecfft = true;
	private static HashMap<String,Object>PreloadedMLModels = new HashMap<String,Object>(); // it can be used to load ML models and use when predict large data sets. It should speed up the prediction  
	private static String LoadAllMLModelsAtOnce = "F";
	private static String Pipelines="";
	private static String AllExcelFolder=""; // we need it in ExecutionTime class when the recommended is  a combination. It uses with  Pipelines="". For example, when run ARP/wARP no Rfree and with R-free in combination, you need to set the path for folder which contains ARP/wARP with R-free here 
	private static String ExcelFolder="";
	private static String ReflectionFile="";
	private static String ParrotPhases=null;
	private static String GenerateScript="F";
	private static String PickFirstMatchCol="F";
	private static String Prefix="";
	
	public static String getFeaturesInCSV() {
		return getPrefix()+FeaturesInCSV;
	}
	public static void setFeaturesInCSV(String featuresInCSV) {
		FeaturesInCSV = featuresInCSV;
	}

	private static String FeaturesInCSV="features.csv";
	
	
	
	public static String getPrefix() {
		return Prefix;
	}
	public static void setPrefix(String prefix) {
		Prefix = prefix;
	}
	public static String getPickFirstMatchCol() {
		return PickFirstMatchCol;
	}
	public static void setPickFirstMatchCol(String pickFirstMatchCol) {
		PickFirstMatchCol = pickFirstMatchCol;
	}
	public static String getGenerateScript() {
		return GenerateScript;
	}
	public static void setGenerateScript(String generateScript) {
		GenerateScript = generateScript;
	}
	public static String getParrotPhases() {
		return ParrotPhases;
	}
	public static void setParrotPhases(String parrotPhases) {
		ParrotPhases = parrotPhases;
	}
	public static String getReflectionFile() {
		return ReflectionFile;
	}
	public static String getReflectionFileName() {

		return new File(ReflectionFile).getName().replaceAll("."+FilenameUtils.getExtension(new File(ReflectionFile).getName()),"");
	}
	public static void setReflectionFile(String reflectionFile) {
		ReflectionFile = reflectionFile;
	}
	public static String getCrossrefEmail() {
		return CrossrefEmail;
	}
	public static void setCrossrefEmail(String crossrefEmail) {
		CrossrefEmail = crossrefEmail;
	}
	public static String getElsevierToken() {
		return ElsevierToken;
	}
	public static void setElsevierToken(String elsevierToken) {
		ElsevierToken = elsevierToken;
	}

	private static String CrossrefEmail="";
	private static String ElsevierToken="";
	public static String getExcelFolder() {
		return ExcelFolder;
	}
	public static void setExcelFolder(String excelFolder) {
		ExcelFolder = excelFolder;
	}
	public static String getAllExcelFolder() {
		return AllExcelFolder;
	}
	public static void setAllExcelFolder(String allExcelFolder) {
		AllExcelFolder = allExcelFolder;
	}
	public static Vector<String> getPipelines() {
		Vector<String> Pipe= new Vector<String>();
		if(Pipelines.contains(StringInputSplitter))
		 Pipe= new Vector<String>(Arrays.asList(Pipelines.split(StringInputSplitter))); 
		else {
			if(Pipelines.trim().length()!=0) {
				 Pipe= new Vector<String>();
				 Pipe.add(Pipelines);
			}
		}
		return Pipe;
	}
	public static void setPipelines(String pipelines) {
		Pipelines = pipelines;
	}
	public static String getLoadAllMLModelsAtOnce() {
		return LoadAllMLModelsAtOnce;
	}
	public static void setLoadAllMLModelsAtOnce(String loadAllMLModelsAtOnce) {
		LoadAllMLModelsAtOnce = loadAllMLModelsAtOnce;
	}
	public static HashMap<String, Object> getPreloadedMLModels() {
		return PreloadedMLModels;
	}
	public static void setPreloadedMLModels(String MLModelsFolder) throws Exception {
		
		Vector<File> models = new Vector<File>();
		for(File model : new File(MLModelsFolder).listFiles()) {
			if(model.isDirectory()) {
				if(Parameters.getFilterModels().equals("F"))
				models.addAll(Arrays.asList(model.listFiles()));
				
				if(Parameters.getFilterModels().equals("T"))
					models.addAll(Arrays.asList(new FilesUtilities().ReadFilteredModels(model.getAbsolutePath())));	
				
			}
			
		}
		
		
		
		PreloadedMLModels=new LoadMLModel().LoadSetOfModels(models.toArray(new File[0]));
		
	}
	
	public static void CheckDependency() {
		if (!SequenceIdentity.equals("-1") && MR.equals("F")) {
			new Log().Error(new Parameters(), "If you are using sequence identity, then you have to set MR=T");

		}

		if (MultipleModels.equals("T")) {
			if (StartNumberOfTrees.equals("-1")) {
				new Log().Error(new Parameters(), "Please set StartNumberOfTrees");

			}
			if (IncreaseNumberOfTrees.equals("-1")) {
				new Log().Error(new Parameters(), "Please set IncreaseNumberOfTrees");

			}
			if (MaxNumberOfTrees.equals("-1")) {
				new Log().Error(new Parameters(), "Please set MaxNumberOfTrees");

			}
		}
	}
	public static String getAttCSV() {
		return AttCSV;
	}

	public static String getClassificationDatasetsFolderName() {
		return ClassificationDatasetsFolderName;
	}

	public static double getClassLevel(String Header) throws CustomException {

		if (Header.equals("R-work"))
			return Parameters.ClassLevelForRwork;
		if (Header.equals("R-free"))
			return Parameters.ClassLevelForRfree;
		if (Header.equals("Completeness"))
			return Parameters.ClassLevelForCompleteness;

		throw new CustomException(Header + " This header is not recognized");

	}

	public static double getClassLevelForCompleteness() {
		return ClassLevelForCompleteness;
	}

	public static double getClassLevelForRFactors() {
		return ClassLevelForRwork;
	}

	public static double getClassLevelForRfree() {
		return ClassLevelForRfree;
	}
	public static String getCluster() {
		return Cluster;
	}
	public static String getColinfo() {
		return colinfo;
	}
	public static String getCompressedModelFolderName() {
		return CompressedModelFolderName;
	}
	public static String getCompressModel() {
		return CompressModel;
	}
	public static String getFeatures() {
		return Features;
	}
	public static Vector<String> getFilteredModels() {
		return FilteredModels;
	}
	public static String getFilterModels() {
		return FilterModels;
	}
	public static String getHeatmap() {
		return Heatmap;
	}
	public static String getHTMLTable() {
		return HTMLTable;
	}
	public static String getIgnoreCCP4Env() {
	return IgnoreCCP4Env;
}
	public static String getIncreaseNumberOfTrees() {
		return IncreaseNumberOfTrees;
	}
public static double[] getInstanceValue1() {
	return instanceValue1;
}
	public static String getLog() {
		return Log;
	}

public static double getMax() {
	return Max;
}

	public static double getMaxClassLevel(String Header) throws CustomException {

		if (Header.equals("R-work"))
			return Parameters.MaxClassLevelForRwork;
		if (Header.equals("R-free"))
			return Parameters.MaxClassLevelForRfree;
		if (Header.equals("Completeness"))
			return Parameters.MaxClassLevelForCompleteness;

		throw new CustomException(Header + " This header is not recognized");
	}

	public static double getMaxClassLevelForCompleteness() {
		return MaxClassLevelForCompleteness;
	}

	public static double getMaxClassLevelForRFactors() {
		return MaxClassLevelForRwork;
	}

	public static String getMaxNumberOfTrees() {
		return MaxNumberOfTrees;
	}

	public static double getMaxNumClassesLevel() {
		return MaxNumClassesLevel;
	}
public static String getMeasurementUnitsToPredict() {
		return MeasurementUnitsToPredict;
	}
	public static double getMin() {
		return Min;
	}
	public static double getMinClassLevel(String Header) throws CustomException {

		if (Header.equals("R-work"))
			return Parameters.MinClassLevelForRwork;
		if (Header.equals("R-free"))
			return Parameters.MinClassLevelForRfree;
		if (Header.equals("Completeness"))
			return Parameters.MinClassLevelForCompleteness;

		throw new CustomException(Header + " This header is not recognized");
	}
	public static double getMinClassLevelForCompleteness() {
		return MinClassLevelForCompleteness;
	}
	public static double getMinClassLevelForRFactors() {
		return MinClassLevelForRwork;
	}
	public static double getMinNumClassesLevel() {
		return MinNumClassesLevel;
	}

	public static String getModelFolderName() {
		return ModelFolderName;
	}

	public static String getMR() {
		return MR;
	}

	public static String getMultipleModels() {
		return MultipleModels;
	}

	public static String getNormalizeStructureEvaluationInErrorTable() {
		return NormalizeStructureEvaluationInErrorTable;
	}

	public static String getNumberOfTrees() {
		return NumberOfTrees;
	}

	public static String getOptimizeClasses() {
		return OptimizeClasses;
	}

	public static String getPearsonsCorrelation() {
		return PearsonsCorrelation;
	}

	public static String getPhases() {
		return Phases;
	}

	public static String getPredictionConfidence() {
		return PredictionConfidence;
	}

	public static String getPrepareFeatures() {
		return PrepareFeatures;
	}

	public static String getPrintParameters() {
		return PrintParameters;
	}
	public static double getResolution() {
		return Resolution;
	}
	public static double getRMSD() {
		return RMSD;
	}
	public static String getSequenceIdentity() {
		return SequenceIdentity;
	}
	public static double getSkew() {
		return Skew;
	}
	public static String getSplitOnStructureLevel() {
		return SplitOnStructureLevel;
	}

	public static String getStartNumberOfTrees() {
		return StartNumberOfTrees;
	}

	public static String getStringInputSplitter() {
		return StringInputSplitter;
	}

	public static Vector<String> getStructureEvaluationToBeNormalized() {
		Vector<String> StructureEvaluations;
		if(StructureEvaluationToBeNormalized.contains(StringInputSplitter)) {
		 StructureEvaluations= new Vector<String>(Arrays.asList(StructureEvaluationToBeNormalized.split(",")));;
		}else
		 {
			 StructureEvaluations =  new Vector<String>();
			 StructureEvaluations.add(StructureEvaluationToBeNormalized);
		 }
		return StructureEvaluations;
	}

	public static String getTrainedModelsPath() {
		return TrainedModelsPath;
	}

	public static String getUpdateChecking() {
		return UpdateChecking;
	}

	public static boolean isUsecfft() {
		return Usecfft;
	}

	public static void setAttCSV(String attCSV) {
		AttCSV = attCSV;
	}

	public static void setClassificationDatasetsFolderName(String classificationDatasetsFolderName) {
		ClassificationDatasetsFolderName = classificationDatasetsFolderName;
	}

	public static void setClassLevel(String Header, double val) {

		if (Header.equals("R-work"))
			Parameters.ClassLevelForRwork = val;
		if (Header.equals("R-free"))
			Parameters.ClassLevelForRfree = val;
		if (Header.equals("Completeness"))
			Parameters.ClassLevelForCompleteness = val;
	}
	public static void setClassLevelForCompleteness(String classLevelForCompleteness) {
		ClassLevelForCompleteness = Double.parseDouble(classLevelForCompleteness);
	}
	public static void setClassLevelForRFactors(String classLevelForRFactors) {
		ClassLevelForRwork = Double.parseDouble(classLevelForRFactors);
	}
	public static void setClassLevelForRfree(String classLevelForRfree) {
		ClassLevelForRfree = Double.parseDouble(classLevelForRfree);
	}
	public static void setCluster(String cluster) {
		Cluster = cluster;
	}

	public static void setColinfo(String colinfo) {
		Parameters.colinfo = colinfo;
	}
	public static void setCompressedModelFolderName(String compressedModelFolderName) {
		CompressedModelFolderName = compressedModelFolderName;
	}

	public static void setCompressModel(String compressModel) {
		CompressModel = compressModel;
	}
	public static void setFeatures(String features) {
		Features = features;
	}
	//public static void setFilteredModels(Vector<String> filteredModels) {
	//	FilteredModels = filteredModels;
	//}
	public static void setFilteredModels(String filteredModels) {
		if(filteredModels.contains(",")) {
			FilteredModels= new Vector<String>(Arrays.asList(filteredModels.split(",")));

		}
		else {
			FilteredModels.add(filteredModels);
		}
		
	}
	
	public static void setFilterModels(String filterModels) {
		FilterModels = filterModels;
	}

	public static void setHeatmap(String heatmap) {
		Heatmap = heatmap;
	}

	public static void setHTMLTable(String hTMLTable) {
		HTMLTable = hTMLTable;
	}

	public static void setIgnoreCCP4Env(String ignoreCCP4Env) {
		IgnoreCCP4Env = ignoreCCP4Env;
	}

	public static void setIncreaseNumberOfTrees(String increaseNumberOfTrees) {
		IncreaseNumberOfTrees = increaseNumberOfTrees;
	}

	public static void setInstanceValue1(double[] instanceValue1) {
		Parameters.instanceValue1 = instanceValue1;
	}

	public static void setLog(String log) {
		Log = log;
	}

	public static void setMax(String max) {
		Max =  Double.parseDouble(max);
	}
	public static void setMaxClassLevel(String Header, double val) {

		if (Header.equals("R-work"))
			Parameters.MaxClassLevelForRwork = val;
		if (Header.equals("R-free"))
			Parameters.MaxClassLevelForRfree = val;
		if (Header.equals("Completeness"))
			Parameters.MaxClassLevelForCompleteness = val;
	}
	public static void setMaxClassLevelForCompleteness(String maxClassLevelForCompleteness) {
		MaxClassLevelForCompleteness = Double.parseDouble(maxClassLevelForCompleteness);
	}
	public static void setMaxClassLevelForRFactors(String maxClassLevelForRFactors) {
		MaxClassLevelForRwork = Double.parseDouble(maxClassLevelForRFactors);
	}
	public static void setMaxNumberOfTrees(String maxNumberOfTrees) {
		MaxNumberOfTrees = maxNumberOfTrees;
	}
	public static void setMaxNumClassesLevel(String maxNumClassesLevel) {
		MaxNumClassesLevel = Double.parseDouble(maxNumClassesLevel);
	}

	public static void setMeasurementUnitsToPredict(String measurementUnitsToPredict) {
		MeasurementUnitsToPredict = measurementUnitsToPredict;
	}

	public static void setMin(String min) {
		Min =  Double.parseDouble(min);
	}

	public static void setMinClassLevel(String Header, double val) {

		if (Header.equals("R-work"))
			Parameters.MinClassLevelForRwork = val;
		if (Header.equals("R-free"))
			Parameters.MinClassLevelForRfree = val;
		if (Header.equals("Completeness"))
			Parameters.MinClassLevelForCompleteness = val;

	}

	public static void setMinClassLevelForCompleteness(String minClassLevelForCompleteness) {
		MinClassLevelForCompleteness = Double.valueOf(minClassLevelForCompleteness);
	}

	public static void setMinClassLevelForRFactors(String minClassLevelForRFactors) {
		MinClassLevelForRwork = Double.valueOf(minClassLevelForRFactors);
	}

	public static void setMinNumClassesLevel(String minNumClassesLevel) {
		MinNumClassesLevel = Double.parseDouble(minNumClassesLevel);
	}

	public static void setModelFolderName(String modelFolderName) {
		ModelFolderName = modelFolderName;
	}

	public static void setMR(String mR) {
		if (mR.equals("T") || mR.equals("F"))
			MR = mR;
		else
			new Log().Error(new Parameters(), "MR should be either T or F");
	}

	public static void setMultipleModels(String multipleModels) {
		MultipleModels = multipleModels;
	}

	public static void setNormalizeStructureEvaluationInErrorTable(String normalizeCompletenessInErrorTable) {
		NormalizeStructureEvaluationInErrorTable = normalizeCompletenessInErrorTable;
	}

	public static void setNumberOfTrees(String numberOfTrees) {
		NumberOfTrees = numberOfTrees;
	}

	public static void setOptimizeClasses(String optimizeClasses) {
		OptimizeClasses = optimizeClasses;
	}
	
	public static void setPearsonsCorrelation(String pearsonsCorrelation) {
		PearsonsCorrelation = pearsonsCorrelation;
	}
	public static void setPhases(String phases) {
		
		if (phases.split(",").length != 4 && phases.split(",").length != 2)
			new Log().Error(new Parameters(), "Phases are wrong! (for Example, Phases=HLA,HLB,HLC,HLD or Phases=PHIB,FOM)");

		
		Phases = phases;
	}

	public static void setPredictionConfidence(String predictionConfidence) {
		PredictionConfidence = predictionConfidence;
	}

	public static void setPrepareFeatures(String prepareFeatures) {
		PrepareFeatures = prepareFeatures;
	}
	
	public static void setPrintParameters(String printParameters) {
		PrintParameters = printParameters;
	}

	public static void setResolution(String resolution) {
		Resolution =  Double.parseDouble(resolution);
	}

	public static void setRMSD(String rMSD) {
		RMSD = Double.parseDouble(rMSD);
	}
	 public static void setSequenceIdentity(String sequenceIdentity) {
		if (Double.parseDouble(sequenceIdentity) >= 0 && Double.parseDouble(sequenceIdentity) <= 1)
			SequenceIdentity = sequenceIdentity;
		else {

			new Log().Error(new Parameters(),
					"Sequence identity should be between 0 and 1. For example, if the sequence identity is 53, then must be typed in as 0.53 ");

		}

	}
	 public static void setSkew(String skew) {
		Skew =  Double.parseDouble(skew);
	}
	 public static void setSplitOnStructureLevel(String splitOnStructureLevel) {
		SplitOnStructureLevel = splitOnStructureLevel;
	}

	public static void setStartNumberOfTrees(String startNumberOfTrees) {
		StartNumberOfTrees = startNumberOfTrees;
	}

	public static void setStringInputSplitter(String stringInputSplitter) {
		StringInputSplitter = stringInputSplitter;
	}

	public static void setStructureEvaluationToBeNormalized(String structureEvaluationToBeNormalized) {
		StructureEvaluationToBeNormalized = structureEvaluationToBeNormalized;
	}

	public static void setTrainedModelsPath(String trainedModelsPath) {
		TrainedModelsPath = trainedModelsPath;
	}

	public static void setUpdateChecking(String updateChecking) {
		UpdateChecking = updateChecking;
	}

	public static void setUsecfft(boolean usecfft) {
		Usecfft = usecfft;
	}
}
