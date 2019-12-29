package PMBPP.ML.Model;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Element;

import PMBPP.Utilities.XML;
import PMBPP.Log.Log;
import PMBPP.Utilities.CSVReader;
import PMBPP.Utilities.FilesUtilities;
import me.tongfei.progressbar.ProgressBar;
import table.draw.Block;
import table.draw.Board;
import table.draw.Table;
import weka.attributeSelection.ClassifierAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.Prediction;
import weka.core.Instances;

public class CreateModels {

	//HashMap<String , Evaluation> evaluations= new HashMap<String , Evaluation>();
	HashMap<String , HashMap<String,Vector<Object>>> evaluations= new HashMap<String , HashMap<String,Vector<Object>>>();
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		
		
		
		//Prediction
		//String[] args2= {"CSV"};
		//Parameters.ModelFolderName="PredictionModels/";
		
		//Classification
		String[] args2= {"/Users/emadalharbi/Downloads/TestPreAcc/ClassificationDatasets"};
		Parameters.ModelFolderName="ClassificationModels/";
		
		
		/* MR
		 * Parameters.MR="T";
		 * Parameters.Featuers="RMSD,Skew,Resolution,Max,Min,SequenceIdentity";
		 */
		
		//ِExtra options 
		//Parameters.MultipleModels="F";
		//Parameters.SplitOnStructureLevel="F";
		
		//Parameters.Featuers="RMSD,Skew,Resolution,Max,Min,SequenceIdentity";
		//Parameters.MR="T";
		//Parameters.MultipleModels="T";
		//Parameters.StartNumberOfTrees="1";
		//Parameters.IncreaseNumberOfTrees="2";
		//Parameters.MaxNumberOfTrees="100";
		//Parameters.SplitOnStructureLevel="T";
		
new CreateModels().Models(args2);
		
	}

	public void Models(String[] args) throws Exception {
		
		
		new Log().TxtInRectangle("Creating models");
		String PathToCSV=args[0];
		
		isValid(PathToCSV);
		
		
		File [] files = new FilesUtilities().ReadFilesList(PathToCSV);
				
				
				
				if(new File(Parameters.ModelFolderName).exists())
				{
					new Log().Error(this, Parameters.ModelFolderName+" folder is exists!. Can not create models folder. Remove models folder or change its name");
					
					
				}
				
				HashMap <String,Boolean> measurements= new CSVReader().FilterByFeatures(PathToCSV,false);
				
				
				CreateModels CM= new CreateModels();
				for(String F : measurements.keySet()){
					
					if(Parameters.MeasurementUnitsToPredict.contains(F)) {
						
					 PMBPP.CheckDirAndFile(Parameters.ModelFolderName);
						PMBPP.CheckDirAndFile(Parameters.ModelFolderName+"/"+F);
						for(File CSV : files) {
							new Log().Info(this, "Model: "+F+"-"+CSV.getName());
							Set<String> temp=  new HashSet<String>(measurements.keySet());
							temp.remove(F);
							Vector<String> att = new Vector<String>(temp);
						
								CM.CreateModel("./"+Parameters.ModelFolderName+"/"+F+"/"+CSV.getName().substring(0,CSV.getName().indexOf(".")), CSV.getAbsolutePath(), F ,att,CSV.getName().substring(0,CSV.getName().indexOf(".")) );
	
							
							att.clear();
							new Log().Info(this, "Model: "+F+"-"+CSV.getName() +" has created using "+CSV.getAbsolutePath());
						}
					}
				}
				
				PrintEvaluation(CM.evaluations);
				
	}
	void CreateModel(String ModelName , String Data , String Label , Vector<String> RemovedAtt, String Pipeline) throws Exception {
		
		
		MLModel Pre = new MLModel();
		Pre.LoadData(Data, Label); // Label id here starts from zero
		Pre.RemoveAttribute(RemovedAtt , Label); // here starts from 1. First attribute is 1
		Evaluation evaluation=null;
		Pre.Split(0.66, Pipeline+"-"+Label);
		
		if(Parameters.MultipleModels.equals("F")) {
		Pre.Train();
		
		evaluation=Pre.Evaluate();
		
		Pre.SaveModel(ModelName);
		Ranker AttributeEval =Pre.RankAttributes();
		SaveEvaluation(evaluation,ModelName,Pre,AttributeEval);
		}
		if(Parameters.MultipleModels.equals("T")) {
			for(int I=Integer.valueOf(Parameters.StartNumberOfTrees); I < Integer.valueOf(Parameters.MaxNumberOfTrees); I=I*Integer.valueOf(Parameters.IncreaseNumberOfTrees)) {
				Parameters.NumberOfTrees=String.valueOf(I);
				
				Pre.ModelReset();
				Pre.Train();
				
				evaluation=Pre.Evaluate();

				Pre.SaveModel(ModelName);
				Ranker AttributeEval =Pre.RankAttributes();
				SaveEvaluation(evaluation,ModelName,Pre,AttributeEval);
			}
		}
		
		
	   // evaluations.put(Name, evaluation);
	   
	   
	}
	void SaveEvaluation(Evaluation evaluation,String Name , MLModel Pre , Ranker AttributeEval) {
		 HashMap<String,Vector<Object>> Temp;
		    if(evaluations.containsKey(Name)) {
		    	 Temp =evaluations.get(Name);
		    }
		    else {
		        Temp = new HashMap<String,Vector<Object>>();
		    }
		    Vector<Object> Evaluations = new Vector<Object>();
	    	Evaluations.add(evaluation);
	    	Evaluations.add(AttributeEval);
	    	Evaluations.add(Pre.getTest());
	    	Evaluations.add(Pre.getTrain());
	    	Temp.put(Parameters.NumberOfTrees, Evaluations);
	    	evaluations.put(Name, Temp);
	}
	void PrintEvaluation(HashMap<String , HashMap<String,Vector<Object>>> evaluations1) throws Exception {
		
        XML xml = new XML();
        xml.CreateDocument();
        Element rootElement = xml.getDocument().createElement("Models");
        xml.getDocument().appendChild(rootElement);
        for(String model : evaluations1.keySet()) {
        	 Element elm = xml.getDocument().createElement("Model");
        	 DecimalFormat df = new DecimalFormat("#.##");
     		df.setRoundingMode(RoundingMode.HALF_UP);
     		 rootElement.appendChild(elm);
     		 Element name = xml.getDocument().createElement("Name");
     		 name.setTextContent(model);
     		 elm.appendChild(name);
        	for(String NumberOfTrees : evaluations1.get(model).keySet()) {
        		 Element elm2 = xml.getDocument().createElement("NumberOfIteration");
	        	 
	        	elm2.setAttribute("NumberOfIteration", NumberOfTrees);
	        	 elm.appendChild(elm2);
	        	
	        for(int i=0 ; i < evaluations1.get(model).get(NumberOfTrees).size() ; i++) {	 
	        	
	      if(evaluations1.get(model).get(NumberOfTrees).get(i) instanceof Evaluation) 	{  
	    	  
	    	 
	    	  
	    	  Element Evaluationelm3 = xml.getDocument().createElement("Evaluation");  
	    	  elm2.appendChild(Evaluationelm3);
	      for (Method method : evaluations1.get(model).get(NumberOfTrees).get(i).getClass().getDeclaredMethods()) {
	    	  try {
					method.invoke(evaluations1.get(model).get(NumberOfTrees).get(i),null);
					
					Element elm3 = xml.getDocument().createElement(method.getName());
					elm3.setTextContent(df.format((Double)method.invoke(evaluations1.get(model).get(NumberOfTrees).get(i),null)));
					Evaluationelm3.appendChild(elm3);
		        	 
	        	} catch (Exception e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				} 
	        	 }
	      Evaluation TempEvaluation=(Evaluation)evaluations1.get(model).get(NumberOfTrees).get(i);
	      Element TestDataAndPrediction = xml.getDocument().createElement("TestData");  
    	  elm2.appendChild(TestDataAndPrediction);
    	  int Ins=1;
	      for (Prediction p : TempEvaluation.predictions()) {
	           
	            Element Instance = xml.getDocument().createElement("Instance"+String.valueOf(Ins));
	            ++Ins;
	            TestDataAndPrediction.appendChild(Instance);
	            Element actual = xml.getDocument().createElement("actual");
	            actual.setTextContent(String.valueOf(p.actual()));
	            Instance.appendChild(actual);
	            Element predicted = xml.getDocument().createElement("predicted");
	            predicted.setTextContent(String.valueOf(p.predicted()));
	            Instance.appendChild(predicted);
	            
	        }
	        }
	      
	      if(evaluations1.get(model).get(NumberOfTrees).get(i) instanceof Ranker) {
	    	
	    	 Element Rankerelm3 = xml.getDocument().createElement("Ranker");
	    	 elm2.appendChild(Rankerelm3);
	    	  Ranker ranker = (Ranker)evaluations1.get(model).get(NumberOfTrees).get(i);
	    	  Instances testorTrain=null;
	    	  for(int t =0 ; t < evaluations1.get(model).get(NumberOfTrees).size();++t) {
	    		  if(evaluations1.get(model).get(NumberOfTrees).get(t) instanceof Instances)
	    			  testorTrain=(Instances)evaluations1.get(model).get(NumberOfTrees).get(t);
	    	  }
	    	  for (int r = 0; r < ranker.rankedAttributes().length; r++) { 
	    		  
	    		    Element elm3 = xml.getDocument().createElement(testorTrain.attribute((int)ranker.rankedAttributes()[r][0]).name());
					elm3.setTextContent(String.valueOf(ranker.rankedAttributes()[r][1]));
					Rankerelm3.appendChild(elm3);
				   
				  }
	      }
        	}
        	}
        }
        
        String XMLName=new File(Parameters.ModelFolderName).getName()+"Performance.xml";
        if(new File(XMLName).exists())
        	FileUtils.deleteQuietly(new File(XMLName));
        
        xml.WriteDocument(xml.getDocument(), XMLName);

	}
	
	void isValid(String PathToCSV) {
		if(!new File(PathToCSV).exists()) {
			new Log().Error(this,new File(PathToCSV).getName()+" folder is not found  (Maybe it is wrong directory!)");
          
		}
		
		if(new FilesUtilities().ReadFilesList(PathToCSV).length==0) {
			new Log().Error(this,new File(PathToCSV).getName()+" folder is empty!");
			 
		}
		
	}
}
