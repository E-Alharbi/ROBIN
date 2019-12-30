package PMBPP.ML.Model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.commons.lang3.math.NumberUtils;
import org.w3c.dom.Element;

import PMBPP.Log.Log;
import PMBPP.Utilities.CSVReader;
import PMBPP.Utilities.CSVWriter;
import weka.attributeSelection.ClassifierAttributeEval;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.attributeSelection.WrapperSubsetEval;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SimpleLinearRegression;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.Bagging;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;
public class MLModel {

	private RandomForest MLPredictor;
	private Instances train ;
	public Instances getTrain() {
		return train;
	}
	public void setTrain(Instances train) {
		this.train = train;
	}
	public Instances getTest() {
		return test;
	}
	public void setTest(Instances test) {
		this.test = test;
	}

	private Instances test;
	private Random rand = new Random(1);
	private Instances dataset;
	private Instances Unfiltereddataset;
	//private int numFolds;
	//private int ClassIndex;
	//private int ClassIndex;
	public void ModelReset() {
		init();
	}
	public MLModel () {
		init();
	}
	void init() {
		MLPredictor = new RandomForest();
		MLPredictor.setNumIterations(Integer.parseInt(Parameters.NumberOfTrees));
		MLPredictor.setNumExecutionSlots(Runtime.getRuntime().availableProcessors());
	}
	void LoadData (String DataPath ,  String ClassName) throws Exception {
		DataSource source = new DataSource(DataPath);
		
		dataset = source.getDataSet();
		dataset.setClassIndex(GetAttIndexByItName(dataset,ClassName));
		
		//this.numFolds=numFolds;
		train=dataset;
		test=dataset;
		//this.ClassIndex=ClassIndex;
		train.setClassIndex(GetAttIndexByItName(dataset,ClassName));
		
		
		
		Unfiltereddataset= new Instances(dataset);
	}
	int GetAttIndexByItName(Instances DatasetToGetAtt, String Label) {
		
		for(int i=0 ; i < DatasetToGetAtt.numAttributes() ; ++i) {
			if(DatasetToGetAtt.attribute(i).name().equals(Label)) {
				
				return i;
			}
		}
		
		return -1;
	}
	

	void Split(double Percentage , String PipelineAndLabel) throws Exception {
		
		if(dataset.attribute(dataset.classIndex()).numValues()==1) { // this will cause "Cannot handle unary class" in Weka
			new Log().Error(this, "Can not create a predictive model with only one value in the class that want to predict. This might happen when you have a small dataset. ");
		}
		
		if(Parameters.SplitOnStructureLevel.equals("F")) {
		dataset.randomize(rand);
			
		//https://stackoverflow.com/questions/14682057/java-weka-how-to-specify-split-percentage
		int trainSize = (int) Math.round(dataset.numInstances() * Percentage);
		int testSize = dataset.numInstances() - trainSize;
		train = new Instances(dataset, 0, trainSize);
		test = new Instances(dataset, trainSize, testSize);
		
		
		}
		
		
		if(Parameters.SplitOnStructureLevel.equals("T"))
		SplitOnStructureLevel(Percentage);
		
		
		
		
		//SaveToCSV
		String WhereToSave="TrainAndTestData"+Parameters.ModelFolderName;
		PMBPP.CheckDirAndFile(WhereToSave);
		new CSVWriter().WriteInstancesToCSV(train, WhereToSave+"/"+PipelineAndLabel+"-train");
		new CSVWriter().WriteInstancesToCSV(test, WhereToSave+"/"+PipelineAndLabel+"-test");
		new CSVWriter().WriteInstancesToCSV(dataset, WhereToSave+"/"+PipelineAndLabel+"-dataset");

	}
	
	void SplitOnStructureLevel(double Percentage) throws Exception {
		
		Vector<String> PDB = new Vector<String>();
		for (int i=0 ; i< Unfiltereddataset.numInstances() ; ++i) { // find unique PDB entities  
			
			String PDBEntity=Unfiltereddataset.get(i).stringValue(GetAttIndexByItName(Unfiltereddataset,"PDB")).substring(0,4);
			if(!PDB.contains(PDBEntity))
				PDB.add(PDBEntity);
			
		}
		int trainSize = (int) Math.round(PDB.size() * Percentage); // now find the size 
		
		Collections.shuffle(PDB); // randomize
		
		Vector<String> PDBtrain = new Vector<String>();
		Vector<String> PDBtest =  new Vector<String>();
		PDBtrain.addAll(PDB.subList(0, trainSize)); // split unique PDB entities  
		PDBtest.addAll(PDB.subList(trainSize, PDB.size()));
		
		Instances traindataset = FillIn(Unfiltereddataset,PDBtrain); // add all PDB original and synthetic that for same PDB 
		Instances testdataset = FillIn(Unfiltereddataset,PDBtest);;
		
		Vector <String> AttributesToRemove=new Vector <String>();
		for(int a=0 ; a < Unfiltereddataset.numAttributes();++a) { // Unfiltereddataset contains all attributes and we need to remove unwanted attributes to match the attributes in dataset     
			boolean found=false;
			for(int d=0 ; d < dataset.numAttributes();++d) {
				if(dataset.attribute(d).name().equals(Unfiltereddataset.attribute(a).name())) {
					found=true;
				}
			}
			if(found==false) {
				AttributesToRemove.add(Unfiltereddataset.attribute(a).name());
				
			}
		}
		
		    Remove removeFilter = new Remove();
			removeFilter.setAttributeIndices(AttributeIndices(AttributesToRemove,Unfiltereddataset));
			removeFilter.setInputFormat(Unfiltereddataset);
			traindataset = Filter.useFilter(traindataset, removeFilter);
			testdataset = Filter.useFilter(testdataset, removeFilter);
				
			traindataset.setClassIndex(GetAttIndexByItName(traindataset,dataset.attribute(dataset.classIndex()).name()));
			testdataset.setClassIndex(GetAttIndexByItName(testdataset,dataset.attribute(dataset.classIndex()).name()));
			
			train =traindataset;
			test =testdataset;
			
	
	}
	Instances FillIn(Instances dataset, Vector<String> PDB) {
		
		Instances temp = new Instances(dataset) ;
		temp.clear();
		
		for(String pdb : PDB) {
			for (int i=0 ; i< Unfiltereddataset.numInstances() ; ++i) {
				String PDBEntity=Unfiltereddataset.get(i).stringValue(GetAttIndexByItName(Unfiltereddataset,"PDB")).substring(0,4);
				if(PDBEntity.equals(pdb)) {
	temp.add(Unfiltereddataset.get(i));
			}}
		}
		return temp;
	}
	void Train() throws Exception {
		
		
		MLPredictor.buildClassifier(train);
		
		
		
	}
	String AttributeIndices(Vector <String> Attributes , Instances DatasetToGetIndex) {
		String Indices="";
		for(int i=0 ; i < Attributes.size();++i) {
			if(i+1 < Attributes.size())
				Indices+=String.valueOf(GetAttIndexByItName(DatasetToGetIndex,Attributes.get(i))+1 )+","; // here we start from 1 for first Attribute not like when set class index 
			else
				Indices+=String.valueOf( GetAttIndexByItName(DatasetToGetIndex,Attributes.get(i))+1 );
		}
		
		return Indices;
	}
	void RemoveAttribute(Vector <String> Attributes , String ClassName ) throws Exception {
		
		
		    Remove removeFilter = new Remove();
			removeFilter.setAttributeIndices(AttributeIndices(Attributes,train));
			removeFilter.setInputFormat(train);
			dataset = Filter.useFilter(dataset, removeFilter);
			dataset.setClassIndex(GetAttIndexByItName(dataset,ClassName));
			
			
	}
	
	Evaluation Evaluate () throws Exception {
		
		
		Evaluation evaluation = new Evaluation(train); // it is only need train to get the instance structure not values
		evaluation.evaluateModel(MLPredictor, test);
		
		
	
		
return evaluation;
       
	}
	
	void SaveModel(String Name) throws Exception {
		
		 //weka.core.SerializationHelper.write("Models"+"/"+Name+".model", MLPredictor);
		if(new File(Name+".model").exists()) {
		 weka.core.SerializationHelper.write(Name+Parameters.NumberOfTrees+".model", MLPredictor);
		 
		 SaveAttributes(Name+Parameters.NumberOfTrees);
		 }
		else {
			weka.core.SerializationHelper.write(Name+".model", MLPredictor);
			 
			 SaveAttributes(Name);
		}
	}
	void ReadModel(String Name) throws Exception {
        

        	if(Name.contains(".model"))
			MLPredictor = (RandomForest) weka.core.SerializationHelper.read(Name);
        	else
        	MLPredictor = (RandomForest) weka.core.SerializationHelper.read(Name+".model");
			
    
        
	}
	
	void Normalize() throws Exception {
		int Class = dataset.classIndex();
		Normalize normalize = new Normalize();
		normalize.setInputFormat(dataset);
		Instances newdata = Filter.useFilter(dataset, normalize);
		dataset=newdata;
		dataset.setClassIndex(Class);
		
	}
	
	  String Predicte(double[] instanceValue1, String Att) throws Exception {
		  DataSource source = new DataSource(Att);
		  
		  
		   
		  Instances Tempdataset = source.getDataSet();
		  Tempdataset.clear();// we just want the attributes
		  Tempdataset.setClassIndex(Tempdataset.numAttributes()-1);
		  Tempdataset.add(new DenseInstance(1.0, instanceValue1));
		 
	       
		  String Prediction=Tempdataset.classAttribute().value((int) MLPredictor.classifyInstance( Tempdataset.firstInstance()));
		  DecimalFormat df = new DecimalFormat("#.##");
		  df.setRoundingMode(RoundingMode.HALF_UP);
		  
		  if(Prediction.isEmpty()) {
			 
			  Prediction=df.format(BigDecimal.valueOf(MLPredictor.classifyInstance( Tempdataset.firstInstance())));
		  }
		  
		  if(Prediction.contains("±")) {
			  Parameters.Log="F";// no need to the log here only tables are needed
			  HashMap <String,Boolean> MeasurementUnitsToPredict=  new CSVReader().FilterByFeatures(Parameters.AttCSV, false);
			 Vector<String> Headers= new Vector<String>();
			 Headers.add(String.valueOf(MeasurementUnitsToPredict.keySet().toArray()[0]));
			  HashMap<String, Vector<HashMap<String, String>>> map=	new CSVReader().ReadIntoHashMapWithFilterdHeaders(Parameters.AttCSV,  String.valueOf(MeasurementUnitsToPredict.keySet().toArray()[0]), Headers);
              if(map.keySet().size()==2) { // if it binary classification 
            	  List <String>classes = new ArrayList<>(map.keySet());
            	  Collections.sort(classes);
            	  
                if(Prediction.equals(classes.get(0)))
                Prediction="Strong prediction";
                if(Prediction.equals(classes.get(1)))
                   Prediction="Weak prediction";
              }
              Parameters.Log="T";
		  }
		  
		 
		  return  Prediction;
	        
	        
	    }
	  
	  double Accuracy (boolean Completeness) throws Exception {
		  
		  double WithinFiveDiff=0;
		  double diff = 0.05;
		  if (Completeness==true) diff=5;
	        for (int i = 0; i < test.numInstances(); i++)      
	        {
	            String trueClassLabel;
	            trueClassLabel = test.instance(i).toString(test.classIndex());
	            
	             // Discreet prediction
	            double predictionIndex = 
	            		MLPredictor.classifyInstance(test.instance(i)); 
	            
	            if(predictionIndex >= Double.parseDouble(trueClassLabel) -   diff   &&  predictionIndex <=Double.parseDouble(trueClassLabel) +   diff )
	            	WithinFiveDiff++;
	            
	           
	        }
	        
	        return (WithinFiveDiff*100)/test.numInstances();
	        
	    	
	  }
	  
	  public void SaveAttributes(String Name) throws FileNotFoundException {
		  
		  String CSV="";
		  
		  for(int i=0 ; i < dataset.numAttributes() ; ++i) {
				
					if(i+1 < dataset.numAttributes())
					CSV+=dataset.attribute(i).name()+",";
					else
					CSV+=dataset.attribute(i).name()+"\n";
				
			}
		 
		  for(int i=0 ; i < dataset.attribute(train.classIndex()).numValues() ; ++i) {
			  for(int a=0 ; a < dataset.numAttributes() ; ++a) {
				  if(a+1 < dataset.numAttributes())
						CSV+="0,";
						else
						CSV+=dataset.attribute(train.classIndex()).value(i)+"\n";
			  }
		  }
		  
		  if(dataset.attribute(train.classIndex()).numValues()==0) {
			  for(int i=0 ; i < dataset.numAttributes() ; ++i) {
					
					if(i+1 < dataset.numAttributes())
					CSV+="0,";
					else
					CSV+="0\n";
				
			} 
		  }
		  
		  try(  PrintWriter out = new PrintWriter(Name+".csv")){
			    out.println( CSV );
			}
	  }
	  
	  Ranker RankAttributes() throws Exception {
		 // We used the full dataset here as in weka GUI 
		  
		  ClassifierAttributeEval  evaluator = new ClassifierAttributeEval() ;
				  evaluator.setLeaveOneAttributeOut(true) ;
				  evaluator.setEvaluationMeasure(new SelectedTag(WrapperSubsetEval.EVAL_MAE, WrapperSubsetEval.TAGS_EVALUATION)) ;
				 
				  evaluator.setClassifier(MLPredictor) ;

				  // Build evaluator and rank the attributes 
				  evaluator.buildEvaluator(dataset) ;
				  Ranker ranker = new Ranker() ;
				  ranker.search(evaluator, dataset) ;

				 
				 // for (int i = 0; i < ranker.rankedAttributes().length; i++) { 
				   // System.out.println( test.attribute((int)ranker.rankedAttributes()[i][0]).name() + " " + ranker.rankedAttributes()[i][1] );
				 // } 
				  return ranker;
	  }
	  
	  
}
