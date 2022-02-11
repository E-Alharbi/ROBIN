package ROBIN.ML.Model;

import java.io.File;
import java.lang.reflect.Method;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Element;

import ROBIN.Log.Log;
import ROBIN.Utilities.CSVReader;
import ROBIN.Utilities.FilesUtilities;
import ROBIN.Utilities.XML;
import weka.attributeSelection.Ranker;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.Prediction;
import weka.core.Instances;

/*
 * Creating ML models. See the examples 
 */
public class MLModelTraining {

	HashMap<String, HashMap<String, Vector<Object>>> evaluations = new HashMap<String, HashMap<String, Vector<Object>>>();

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		// examples

		// Prediction
		String[] args2= {"AllCSV"};
		//Parameters.setNumberOfTrees("1024");
		Parameters.setModelFolderName("PredictionModels");
		
		
		// Classification
		//String[] args2 = { "/ClassificationDatasets" };
		//Parameters.ModelFolderName = "ClassificationModels/";
		//Parameters.NumberOfTrees = "4096";

		/*
		 * MR Parameters.MR="T";
		 * Parameters.Featuers="RMSD,Skew,Resolution,Max,Min,SequenceIdentity";
		 */

		// ِExtra options
		// Parameters.MultipleModels="F";
		// Parameters.SplitOnStructureLevel="F";

		// Parameters.Featuers="RMSD,Skew,Resolution,Max,Min,SequenceIdentity";
		// Parameters.MR="T";
		// Parameters.MultipleModels="T";
		// Parameters.StartNumberOfTrees="1";
		// Parameters.IncreaseNumberOfTrees="2";
		// Parameters.MaxNumberOfTrees="100";
		// Parameters.SplitOnStructureLevel="T";

		new MLModelTraining().Models(args2);

	}

	public void Models(String[] args) throws Exception {

		new Log().TxtInRectangle(" ML models training");
		String PathToCSV = args[0];

		isValid(PathToCSV);

		File[] files = new FilesUtilities().ReadFilesList(PathToCSV);

		if (new File(Parameters.getModelFolderName()).exists()) {
			new Log().Error(this, Parameters.getModelFolderName()
					+ " folder is exists!. Can not create models folder. Remove models folder or change its name");

		}

		HashMap<String, Boolean> measurements = new CSVReader(PathToCSV).FilterByFeatures( false);

		MLModelTraining CM = new MLModelTraining();
		for (String F : measurements.keySet()) {

			if (Parameters.getMeasurementUnitsToPredict().contains(F)) {

				ROBIN.CheckDirAndFile(Parameters.getModelFolderName());
				ROBIN.CheckDirAndFile(Parameters.getModelFolderName() + "/" + F);
				for (File CSV : files) {
					new Log().Info(this, "Model: " + F + "-" + CSV.getName());
					Set<String> temp = new HashSet<String>(measurements.keySet());
					temp.remove(F);// meaning, this label will not be deleted
					Vector<String> att = new Vector<String>(temp);

					CM.CreateModel(
							"./" + Parameters.getModelFolderName() + "/" + F + "/"
									+ CSV.getName().substring(0, CSV.getName().indexOf(".")),
							CSV.getAbsolutePath(), F, att, CSV.getName().substring(0, CSV.getName().indexOf(".")));

					att.clear();
					new Log().Info(this,
							"Model: " + F + "-" + CSV.getName() + " has created using " + CSV.getAbsolutePath());
				}
			}
		}

		PrintEvaluation(CM.evaluations);

	}

	void CreateModel(String ModelName, String Data, String Label, Vector<String> RemovedAtt, String Pipeline)
			throws Exception {

		MLModel Pre = new MLModel();
		Pre.LoadData(Data, Label); // Label id here starts from zero
		Pre.setDataset(Pre.RemoveAttribute(RemovedAtt, Label,Pre.getDataset()));  // here starts from 1. First attribute is 1
		MLEvaluation TestEvaluation = null;
		MLEvaluation TrainEvaluation = null;
		
		// Pre.Split(0.66, Pipeline+"-"+Label);
		Pre.Split(0.80, Pipeline + "-" + Label);

		if (Parameters.getMultipleModels().equals("F")) {
			Pre.Train();
			//Pre.Print();
			TestEvaluation = Pre.Evaluate(true);
			TrainEvaluation=Pre.Evaluate(false);
			
			
			Pre.SaveModel(ModelName,true);
			new Log().Info(this, "Model saved to "+ModelName);
			Ranker AttributeEval = Pre.RankAttributes();
			FeatureImportance featureImportance=Pre.featureImportance();
			SaveEvaluation(TestEvaluation, ModelName, Pre, AttributeEval,TrainEvaluation,featureImportance);
		}
		if (Parameters.getMultipleModels().equals("T")) {
			for (int I = Integer.valueOf(Parameters.getStartNumberOfTrees()); I < Integer
					.valueOf(Parameters.getMaxNumberOfTrees()); I = I * Integer.valueOf(Parameters.getIncreaseNumberOfTrees())) {
				Parameters.setNumberOfTrees ( String.valueOf(I));

				Pre.ModelReset();
				Pre.Train();

				TestEvaluation = Pre.Evaluate(true);
				TrainEvaluation= Pre.Evaluate(false);
				
				Pre.SaveModel(ModelName,true);
				
				Ranker AttributeEval = Pre.RankAttributes();
				FeatureImportance featureImportance=Pre.featureImportance();
				SaveEvaluation(TestEvaluation, ModelName, Pre, AttributeEval,TrainEvaluation,featureImportance);
			}
		}

		// evaluations.put(Name, evaluation);

	}

	void SaveEvaluation(MLEvaluation TestEvaluation, String Name, MLModel Pre, Ranker AttributeEval,MLEvaluation TrainEvaluation,FeatureImportance featureImportance) {
		HashMap<String, Vector<Object>> Temp;
		if (evaluations.containsKey(Name)) {
			Temp = evaluations.get(Name);
		} else {
			Temp = new HashMap<String, Vector<Object>>();
		}
		Vector<Object> Evaluations = new Vector<Object>();
		
		Evaluations.add(TestEvaluation);
		Evaluations.add(AttributeEval);
		Evaluations.add(Pre.getTest());
		Evaluations.add(Pre.getTrain());
		Evaluations.add(TrainEvaluation);
		Evaluations.add(featureImportance);
		Temp.put(Parameters.getNumberOfTrees(), Evaluations);
		evaluations.put(Name, Temp);
	}

	void PrintEvaluation(HashMap<String, HashMap<String, Vector<Object>>> evaluations1) throws Exception {
		
		XML xml = new XML();
		xml.CreateDocument();
		Element rootElement = xml.getDocument().createElement("Models");
		xml.getDocument().appendChild(rootElement);
		for (String model : evaluations1.keySet()) {
			Element elm = xml.getDocument().createElement("Model");
			
			rootElement.appendChild(elm);
			Element name = xml.getDocument().createElement("Name");
			name.setTextContent(model);
			elm.appendChild(name);
			for (String NumberOfTrees : evaluations1.get(model).keySet()) {
				Element elm2 = xml.getDocument().createElement("NumberOfIteration");

				elm2.setAttribute("NumberOfIteration", NumberOfTrees);
				elm.appendChild(elm2);

				for (int i = 0; i < evaluations1.get(model).get(NumberOfTrees).size(); i++) {

					if (evaluations1.get(model).get(NumberOfTrees).get(i) instanceof MLEvaluation) {

						Element Evaluationelm3 = xml.getDocument().createElement("Evaluation");
						MLEvaluation dataset = (MLEvaluation)evaluations1.get(model).get(NumberOfTrees).get(i);
						Evaluationelm3.setAttribute("Dataset", dataset.getDataset());
						elm2.appendChild(Evaluationelm3);
						Evaluationelm3=invokeEvaluationMethods(Evaluationelm3,dataset,xml);
						MLEvaluation TempEvaluation = (MLEvaluation) evaluations1.get(model).get(NumberOfTrees).get(i);
						if(dataset.getDataset().equals("Test")) {
						Element TestDataAndPrediction = xml.getDocument().createElement("TestData");
						elm2.appendChild(TestDataAndPrediction);
						int Ins = 1;
						for (Prediction p : TempEvaluation.predictions()) {

							Element Instance = xml.getDocument().createElement("Instance" + String.valueOf(Ins));
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
					}

					if (evaluations1.get(model).get(NumberOfTrees).get(i) instanceof Ranker) {

						Element Rankerelm3 = xml.getDocument().createElement("Ranker");
						elm2.appendChild(Rankerelm3);
						Ranker ranker = (Ranker) evaluations1.get(model).get(NumberOfTrees).get(i);
						Instances testorTrain = null;
						for (int t = 0; t < evaluations1.get(model).get(NumberOfTrees).size(); ++t) {
							if (evaluations1.get(model).get(NumberOfTrees).get(t) instanceof Instances)
								testorTrain = (Instances) evaluations1.get(model).get(NumberOfTrees).get(t);
						}
						for (int r = 0; r < ranker.rankedAttributes().length; r++) {

							Element elm3 = xml.getDocument()
									.createElement(testorTrain.attribute((int) ranker.rankedAttributes()[r][0]).name());
							elm3.setTextContent(String.valueOf(ranker.rankedAttributes()[r][1]));
							Rankerelm3.appendChild(elm3);

						}
					}
					
					if (evaluations1.get(model).get(NumberOfTrees).get(i) instanceof FeatureImportance) {
						
						FeatureImportance FI=(FeatureImportance)evaluations1.get(model).get(NumberOfTrees).get(i);
						Element FeatureImportanceElm = xml.getDocument().createElement("FeatureImportance");
						elm2.appendChild(FeatureImportanceElm);
						for(String feature : FI.keySet()) {
							Element fea = xml.getDocument().createElement(feature);
							FeatureImportanceElm.appendChild(fea);
							fea=invokeEvaluationMethods(fea,FI.get(feature),xml);
						}
					}
					
				}
			}
		}

		String XMLName = new File(Parameters.getModelFolderName()).getName() + "Performance.xml";
		if (new File(XMLName).exists())
			FileUtils.deleteQuietly(new File(XMLName));

		xml.WriteDocument(xml.getDocument(), XMLName);

	}

	void isValid(String PathToCSV) {
		if (!new File(PathToCSV).exists()) {
			new Log().Error(this,
					new File(PathToCSV).getName() + " folder is not found  (Maybe it is wrong directory!)");

		}

		if (new FilesUtilities().ReadFilesList(PathToCSV).length == 0) {
			new Log().Error(this, new File(PathToCSV).getName() + " folder is empty!");

		}

	}
	Element invokeEvaluationMethods(Element Evaluationelm3, MLEvaluation dataset,XML xml ){
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.HALF_UP);
		for (Method method : dataset.getClass()
				.getMethods()) {
			try {
				
				method.invoke(dataset, null);
				
				Element elm3 = xml.getDocument().createElement(method.getName());
			//	elm3.setTextContent(df.format((Double) method.invoke(dataset, null)));
				elm3.setTextContent(String.valueOf(method.invoke(dataset, null)));
				Evaluationelm3.appendChild(elm3);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		}
		return Evaluationelm3;
	}
}
