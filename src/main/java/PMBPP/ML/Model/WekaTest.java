package PMBPP.ML.Model;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.SMOreg;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;

import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.net.search.global.GeneticSearch;
import weka.classifiers.meta.AttributeSelectedClassifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.LMT;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.RandomForest; 

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.Remove;
import weka.clusterers.EM;
import java.awt.BorderLayout;
import java.io.File;
import java.util.Random;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
public class WekaTest {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		DataSource source = new DataSource("/Users/emadalharbi/Downloads/PMBPP2/ClassificationDatasets/Phenix.csv");
		Instances dataset = source.getDataSet();
		
		dataset.setClassIndex(dataset.numAttributes()-1);
		//EM clusterer = new EM();
		
		
		Remove removeFilter = new Remove();
		removeFilter.setAttributeIndices("1,7,8");
		//removeFilter.setInvertSelection(true);
		removeFilter.setInputFormat(dataset);
		//System.out.println(dataset.numAttributes());
		
		dataset = Filter.useFilter(dataset, removeFilter);
		dataset.setClassIndex(dataset.numAttributes()-1);
		Instances train = dataset.trainCV(3, 0);
		Instances test = dataset.testCV(3, 0);
		
		System.out.println(dataset.enumerateAttributes());
	    //clusterer.buildClusterer(dataset);
	    
		//J48 classifier = new J48(); // you should inject this as a collaborator or pass it as a parameter
		//NaiveBayes classifier = new NaiveBayes();
		RandomForest classifier= new RandomForest();
		classifier.buildClassifier(train);
		
		//System.out.println(classifier.toSummaryString());
	    System.out.println(classifier.toString());
	    
	    Evaluation evaluation = new Evaluation(test);
        //evaluation.crossValidateModel(rf, test, numFolds, new Random(1));
        
       evaluation.evaluateModel(classifier, test);
       
        System.out.println(evaluation.toSummaryString());
        System.out.println(evaluation.toClassDetailsString());
        
        weka.core.SerializationHelper.write("test.model", classifier);
        RandomForest RF = (RandomForest) weka.core.SerializationHelper.read("test.model");
        System.out.println( test.firstInstance());
        System.out.println(RF.classifyInstance( test.firstInstance()));
        DataSource source2 = new DataSource("/Users/emadalharbi/Downloads/PMBPP2/ClassificationDatasets/Buccaneer2.csv");
		  
		  
		  double[] instanceValue1 = new double[]{1000.0,19.0,2,-5,2};
		  Instances Tempdataset = source2.getDataSet();
		  Tempdataset.clear();// we just want the attributes
		  
		  Tempdataset.setClassIndex(Tempdataset.numAttributes()-1);
		  Tempdataset.add(new DenseInstance(1.0, instanceValue1));
		  
		  System.out.println("here "+Tempdataset.attribute(Tempdataset.numAttributes()-1).value(0));
		  
		 // System.out.println(test.numAttributes());
		  
		  //System.out.println(Tempdataset.numAttributes());
		  //System.out.println(test.equalHeaders(Tempdataset));
		  
		 
		 System.out.println("RF "+RF.classifyInstance( Tempdataset.firstInstance()));
        
		 System.out.println(  " -> " + test.classAttribute().value((int) RF.classifyInstance( Tempdataset.firstInstance())));
		 /* 
		 for(int i=0 ; i < test.numAttributes() ; ++i) {
				
			  System.out.println(test.attribute(i).equals(Tempdataset.attribute(i)));
				System.out.println(test.attribute(i).name());
			}
		  System.out.println("-------");
		  for(int i=0 ; i < Tempdataset.numAttributes() ; ++i) {
				
				
				System.out.println(Tempdataset.attribute(i).name());
			}
		  */
		  
		  
		/*
		Predictor Pre = new Predictor("RandomForest");
		Pre.LoadData("/Users/emadalharbi/eclipse-workspace/ProteinModelBuildingPipelinePredictor/Phenix.csv", 3, 5);
		//Pre.Normalize();
		Pre.Split();
		Pre.Train();
		Pre.Evaluate();
		Pre.SaveModel("mm");
		Pre.ReadModel("mm", "RandomForest");
		System.out.println(Pre.Predicte(null));	
	
		DataSource source = new DataSource("/Users/emadalharbi/eclipse-workspace/ProteinModelBuildingPipelinePredictor/Buccaneeri1I5.csv");
		
		
		Instances dataset = source.getDataSet();
		dataset.setClassIndex(dataset.numAttributes()-1);
		*/
		//Normalize normalize = new Normalize();
		//normalize.setInputFormat(dataset);
		//Instances newdata = Filter.useFilter(dataset, normalize);
		//newdata.setClassIndex(newdata.numAttributes()-1);
		/*
		 // linear regression model
		 
		
		LinearRegression lr = new LinearRegression();
		lr.buildClassifier(newdata);
		//System.out.println(lr);
		Evaluation lreval = new Evaluation(newdata);
		lreval.evaluateModel(lr, newdata);
		System.out.println(lreval.toSummaryString());
		
		 //svm regression model
		 
		SMOreg smoreg = new SMOreg();
		smoreg.buildClassifier(newdata);
		Evaluation svmregeval = new Evaluation(newdata);
		svmregeval.evaluateModel(smoreg, newdata);
		System.out.println(svmregeval.toSummaryString());
		*/
		
		// Instances data = source.getDataSet();
		// data.deleteAttributeAt(0);
		/*
		int numFolds = 3;					
		Random rand = new Random(1);   // create seeded number generator
		Instances randData = new Instances(dataset);   // create copy of original data
		randData.randomize(rand); 
		int TrainNum=(int)(randData.numInstances()*0.7);
		int TestNum=(int)(randData.numInstances()*0.3);
		System.out.println(TrainNum);
		System.out.println(TestNum);
		Instances train = randData.trainCV(numFolds, 0);
		Instances test = randData.testCV(numFolds, 0);
				
		train.setClassIndex(train.numAttributes()-1);
	   
	       
	       
	       
	 
	        RandomForest rf = new RandomForest();
	
	        rf.buildClassifier(train);
	        */
	       
	       // Remove rm = new Remove();
	    	///rm.setAttributeIndices("");
	    	//FilteredClassifier fc = new FilteredClassifier();
	    	//fc.setFilter(rm);
	    	//fc.setClassifier(rf);
	    	//fc.buildClassifier(train);
	        
	        
	        /*
	        System.out.print("Evaluation");
	        Evaluation evaluation = new Evaluation(train);
	        //evaluation.crossValidateModel(rf, test, numFolds, new Random(1));
	        
	       evaluation.evaluateModel(rf, test);
	       
	        System.out.println(evaluation.toSummaryString());
	        weka.core.SerializationHelper.write("rf.model", rf);
	        RandomForest cls = (RandomForest) weka.core.SerializationHelper.read("rf.model");
	        
	        DataSource source2 = new DataSource("/Users/emadalharbi/eclipse-workspace/ProteinModelBuildingPipelinePredictor/Buccaneeri1I6.csv");
			
			
			Instances dataset2 = source2.getDataSet();
			dataset2.setClassIndex(dataset2.numAttributes()-1);
	        System.out.println(dataset2.firstInstance());
	        System.out.println(cls.classifyInstance(dataset2.firstInstance()));
	        */
	        
	        
	      //  Evaluation evaluation2 = new Evaluation(test);
	       // evaluation2.crossValidateModel(fc, test, numFolds, new Random(1));
	       // fc.buildClassifier(test);
	      //  System.out.println(evaluation2.toSummaryString());
		
	        
	        
	       /*
	        for (int i = 0; i < test.numInstances(); i++)      
	        {
	            String trueClassLabel;
	            trueClassLabel = test.instance(i).toString(test.classIndex());
	             // Discreet prediction
	            double predictionIndex = 
	            rf.classifyInstance(test.instance(i)); 
	            //System.out.println(trueClassLabel);
	            // Get the predicted class label from the predictionIndex.
	            String predictedClassLabel;            
	            predictedClassLabel = test.classAttribute().value((int) predictionIndex);
	           
	            //System.out.println((i+1)+"\t"+trueClassLabel+"\t"+predictionIndex);
	            System.out.println(trueClassLabel+","+predictionIndex);
	           
	        }
	        
	    	*/
	        /*
	        System.out.println(evaluation.toSummaryString("\nResults\n======\n", true));
	        System. out.println(evaluation.toClassDetailsString());
	        System. out.println("Results For Class -1- ");
	        System. out.println("Precision=  " + evaluation.precision(0));
	        System. out.println("Recall=  " + evaluation.recall(0));
	        System. out.println("F-measure=  " + evaluation.fMeasure(0));
	        System. out.println("Results For Class -2- ");
	        System. out.println("Precision=  " + evaluation.precision(1));
	        System. out.println("Recall=  " + evaluation.recall(1));
	        System. out.println("F-measure=  " + evaluation.fMeasure(1)); 
	       */
	}

}
