package Weka.Predictor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;
public class Predictor {

	private Classifier MLPredictor;
	private Instances train ;
	private Instances test;
	private Random rand = new Random(1);
	private Instances dataset;
	private int numFolds;
	//private int ClassIndex;
	
	public Predictor (String Type) {
		
		if(Type.equals(Parameters.PredictorType)) {
			MLPredictor = new RandomForest();
			
		}
	}
	void LoadData (String DataPath , int numFolds , String ClassName) throws Exception {
		DataSource source = new DataSource(DataPath);
		
		dataset = source.getDataSet();
		dataset.setClassIndex(GetAttIndexByItName(dataset,ClassName));
		
		this.numFolds=numFolds;
		train=dataset;
		//this.ClassIndex=ClassIndex;
		train.setClassIndex(GetAttIndexByItName(dataset,ClassName));
		
	}
	int GetAttIndexByItName(Instances DatasetToGetAtt, String Label) {
		
		for(int i=0 ; i < DatasetToGetAtt.numAttributes() ; ++i) {
			if(DatasetToGetAtt.attribute(i).name().equals(Label)) {
				
				return i;
			}
		}
		
		return -1;
	}
	void Split() {
		dataset.randomize(rand);
		train = dataset.trainCV(numFolds, 0);
		test = dataset.testCV(numFolds, 0);
	}
	
	void Train() throws Exception {
		MLPredictor.buildClassifier(train);
	}
	void RemoveAttribute(Vector <String> Attributes , String ClassName ) throws Exception {
		String Index="";
		for(int i=0 ; i < Attributes.size();++i) {
			if(i+1 < Attributes.size())
			//Index+=Attributes.get(i)+",";
			Index+=String.valueOf(GetAttIndexByItName(train,Attributes.get(i))+1 )+","; // here we start from 1 for first Attribute not like when set class index 
			else
				//Index+=Attributes.get(i);
			Index+=String.valueOf( GetAttIndexByItName(train,Attributes.get(i))+1 );
		}
		//System.out.println(Index);
		/*
		    System.out.println(Index);
		    Remove rm = new Remove();
			rm.setAttributeIndices(Index);
			FilteredClassifier fc = new FilteredClassifier();
			fc.setFilter(rm);
			fc.setClassifier(MLPredictor);
			System.out.println(train.classIndex());
			
			fc.buildClassifier(train);
			MLPredictor = fc;
			*/
			 
			 
			
			Remove removeFilter = new Remove();
			removeFilter.setAttributeIndices(Index);
			//removeFilter.setInvertSelection(true);
			removeFilter.setInputFormat(train);
			//System.out.println(dataset.numAttributes());
			
			dataset = Filter.useFilter(dataset, removeFilter);
			dataset.setClassIndex(GetAttIndexByItName(dataset,ClassName));
			//System.out.println("Class Used "+dataset.attribute(dataset.classIndex()).name());
			
	}
	
	void Evaluate () throws Exception {
		Evaluation evaluation = new Evaluation(train);
        //evaluation.crossValidateModel(rf, test, numFolds, new Random(1));
		
		if(test != null && test.numInstances()!=0)
       evaluation.evaluateModel(MLPredictor, test);
		else
			evaluation.evaluateModel(MLPredictor, train);
       //System.out.println(evaluation.toSummaryString());
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.HALF_UP);
       System.out.print(df.format(BigDecimal.valueOf(evaluation.meanAbsoluteError()))+"\t"+df.format(BigDecimal.valueOf(evaluation.correlationCoefficient()))+"\t");
	}
	
	void SaveModel(String Name) throws Exception {
		 PMBPP.CheckDirAndFile("Models");
		 weka.core.SerializationHelper.write("Models"+"/"+Name+".model", MLPredictor);
	}
	void ReadModel(String Name , String Type) throws Exception {
        
        if(Type.equals(Parameters.PredictorType)) {
        	
        	if(Name.contains(".model"))
			MLPredictor = (RandomForest) weka.core.SerializationHelper.read(Name);
        	else
        	MLPredictor = (RandomForest) weka.core.SerializationHelper.read(Name+".model");
			
		}
	}
	
	void Normalize() throws Exception {
		int Class = dataset.classIndex();
		Normalize normalize = new Normalize();
		normalize.setInputFormat(dataset);
		Instances newdata = Filter.useFilter(dataset, normalize);
		dataset=newdata;
		dataset.setClassIndex(Class);
		
	}
	
	  double Predicte(double[] instanceValue1, String Att) throws Exception {
		  DataSource source = new DataSource(Att);
		  
		  
		   //double[] instanceValue1 = new double[]{20, 20, 200,10,2};
		  Instances Tempdataset = source.getDataSet();
		  Tempdataset.clear();// we just want the attributes
		  Tempdataset.setClassIndex(Tempdataset.numAttributes()-1);
		  Tempdataset.add(new DenseInstance(1.0, instanceValue1));
		  //System.out.println(Tempdataset.firstInstance());
	       return  MLPredictor.classifyInstance( Tempdataset.firstInstance());
	        
	        
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
	            
	            //System.out.println(trueClassLabel+"\t"+predictionIndex+"\t"+WithinFiveDiff);
	           
	        }
	        
	        return (WithinFiveDiff*100)/test.numInstances();
	        
	    	
	  }
}
