package PMBPP.Prediction.Analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import PMBPP.Log.Log;
import PMBPP.ML.Model.PMBPP;
import PMBPP.Utilities.TxtFiles;
import PMBPP.ML.Model.Parameters;
/*
 * Generating latex tables for ML evaluation metrics. MAE, RMSD and others
 */

public class NumberOfTreesImpact {

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		// TODO Auto-generated method stub
	
		
		Vector<String> EvaluationMatrices = new Vector<String>();
				EvaluationMatrices.add("rootMeanSquaredError");
				EvaluationMatrices.add("meanAbsoluteError");
				//EvaluationMatrices.add("correlationCoefficient");
		new NumberOfTreesImpact().NumberOfTreesTable("/Volumes/PhDHardDrive/PMBPP/FinalTraining/PMBPPResults/Experimental/Parrot/PredictionModelsPerformance.xml",EvaluationMatrices);

		
		/*
		Vector<String> EvaluationMatrices = new Vector<String>();
		EvaluationMatrices.add("weightedPrecision");
		EvaluationMatrices.add("weightedRecall");
		EvaluationMatrices.add("weightedFMeasure");
new NumberOfTreesImpact().NumberOfTreesTable("ClassificationModelsPerformance.xml",EvaluationMatrices);
*/
		
	}

	public void NumberOfTreesTable(String PathToXML , Vector<String> EvaluationMatrices) throws ParserConfigurationException, SAXException, IOException {

		String TestDataCSV = "Pipeline,Structure evaluation,Value,Value type,Iteration\n";
		File fXmlFile = new File(PathToXML);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);

		// optional, but recommended
		// read this -
		// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
		doc.getDocumentElement().normalize();

		//Vector<String> EvaluationMatrices = new Vector<String>();
		//EvaluationMatrices.add("rootMeanSquaredError");
		//EvaluationMatrices.add("meanAbsoluteError");
		//EvaluationMatrices.add("correlationCoefficient");
		//EvaluationMatrices.add("errorRate");
		TreeMap<Integer, Integer> numberofNumberOfIterations = new TreeMap<Integer, Integer>();
		TreeMap<String, TreeMap<String, TreeMap<String, String>>> Pipelines = new TreeMap<String, TreeMap<String, TreeMap<String, String>>>();
		NodeList nList = doc.getElementsByTagName("Model");
		for (int temp = 0; temp < nList.getLength(); temp++) { // loop on models

			Node nNode = nList.item(temp);
			NodeList nodes = nNode.getChildNodes();
			String PipelineName = "";

			TreeMap<String, TreeMap<String, String>> EvaluationMatrix = new TreeMap<String, TreeMap<String, String>>();

			for (int a = 0; a < nodes.getLength(); ++a) {
				if (nodes.item(a).getNodeName().equals("Name")) {

					PipelineName = nodes.item(a).getTextContent();

				}
			}

			for (int a = 0; a < nodes.getLength(); ++a) {

				if (nodes.item(a).getNodeName().equals("NumberOfIteration")) {
					int NumberOfIterationInThisModel = Integer
							.parseInt(nodes.item(a).getAttributes().getNamedItem("NumberOfIteration").getNodeValue());
					numberofNumberOfIterations.put(NumberOfIterationInThisModel, NumberOfIterationInThisModel);
					;
					NodeList NumberOfIteration = nodes.item(a).getChildNodes();
					for (int n = 0; n < NumberOfIteration.getLength(); ++n) {
						if (NumberOfIteration.item(n).getNodeName().equals("Evaluation")) {

							NodeList Evaluation = NumberOfIteration.item(n).getChildNodes();
							for (int m = 0; m < EvaluationMatrices.size(); ++m) {
								for (int e = 0; e < Evaluation.getLength(); ++e) {
									if (Evaluation.item(e).getNodeName().equals(EvaluationMatrices.get(m))) {
										// System.out.println("\nCurrent Element :" +
										// Evaluation.item(e).getTextContent());
										TreeMap<String, String> EvaluationMatrixTemp = new TreeMap<String, String>();
										// EvaluationMatrixTemp.put(String.valueOf(NumberOfIterationInThisModel),
										// Evaluation.item(e).getTextContent());

										if (EvaluationMatrix.containsKey(EvaluationMatrices.get(m))) {
											EvaluationMatrixTemp = EvaluationMatrix.get(EvaluationMatrices.get(m));
											EvaluationMatrixTemp.put(String.valueOf(NumberOfIterationInThisModel),
													Evaluation.item(e).getTextContent());
										} else {
											EvaluationMatrixTemp.put(String.valueOf(NumberOfIterationInThisModel),
													Evaluation.item(e).getTextContent());

										}
										EvaluationMatrix.put(EvaluationMatrices.get(m), EvaluationMatrixTemp);
									}
								}
							}
						}
						if (NumberOfIteration.item(n).getNodeName().equals("TestData")) {
							NodeList TestData = NumberOfIteration.item(n).getChildNodes();
							for (int e = 0; e < TestData.getLength(); ++e) {
								// System.out.println(TestData.item(e).getNodeName());
								if (!TestData.item(e).getNodeName().equals("#text")) {
									NodeList ValueType = TestData.item(e).getChildNodes();
									String actual = "";
									String predicted = "";
									for (int v = 0; v < ValueType.getLength(); ++v) {
										if (TestData.item(e).getChildNodes().item(v).getNodeName().equals("actual"))
											actual = TestData.item(e).getChildNodes().item(v).getTextContent();
										if (TestData.item(e).getChildNodes().item(v).getNodeName().equals("predicted"))
											predicted = TestData.item(e).getChildNodes().item(v).getTextContent();
									}
									TestDataCSV += PipelineName.split("/")[PipelineName.split("/").length - 1] + ","
											+ PipelineName.split("/")[PipelineName.split("/").length - 2] + "," + actual
											+ ",actual," + NumberOfIterationInThisModel + "\n";
									TestDataCSV += PipelineName.split("/")[PipelineName.split("/").length - 1] + ","
											+ PipelineName.split("/")[PipelineName.split("/").length - 2] + ","
											+ predicted + ",predicted," + NumberOfIterationInThisModel + "\n";

								}
							}
						}
					}
				}
				// if(nodes.item(a).getNodeName().equals("Name")) {

				// PipelineName=nodes.item(a).getTextContent();

				// }
			}

			Pipelines.put(PipelineName, EvaluationMatrix);

		}
		String numofIterations = "&&&";
		for (int key : numberofNumberOfIterations.keySet()) {
			numofIterations += key + "&";
		}

		String Table = "\\begin{table} \\resizebox{\\textwidth}{!}{\n \\tiny \n"
				+ "\\begin{tabular}{lcccccccccccccccccccccc}  Pipeline variant & Evaluation matrix & Structure evaluation & \\multicolumn{7}{c}{Number of trees}  \\\\ \\hline "
				+ numofIterations + "\\\\ \\hline \n";
		Vector<String> PrintedPipelines = new Vector<String>();
		String CSV = "Pipeline,EvaluationMatrix,StructureEvaluation,Iteration,Value\n";
		for (String Pipe : Pipelines.keySet()) { // loop on Pipelines
			String PipelineAndEvaluationmatrix = Pipe.split("/")[Pipe.split("/").length - 1];
			if (!PrintedPipelines.contains(PipelineAndEvaluationmatrix)) {
				PrintedPipelines.add(PipelineAndEvaluationmatrix);
				String Row = "\\multirow{6}{*}{" + Pipe.split("/")[Pipe.split("/").length - 1] + "}&";
				for (int m = 0; m < EvaluationMatrices.size(); ++m) {

					Row += "\\multirow{ 3}{*}{" + EvaluationMatrices.get(m) + "}&";
					for (String StructureEvaluation : Pipelines.keySet()) {
						String PipelineName = StructureEvaluation.split("/")[StructureEvaluation.split("/").length - 1];
						if (PipelineName.equals(PipelineAndEvaluationmatrix))
							for (String Evaluationmatrix : Pipelines.get(StructureEvaluation).keySet()) {
								if (Evaluationmatrix.equals(EvaluationMatrices.get(m))) {
									Row += StructureEvaluation.split("/")[StructureEvaluation.split("/").length - 2]
											+ "&";
									for (int key : numberofNumberOfIterations.keySet()) {

										for (String Iteration : Pipelines.get(StructureEvaluation).get(Evaluationmatrix)
												.keySet()) {

											if (Iteration.equals(String.valueOf(numberofNumberOfIterations.get(key)))) {
												Row += Pipelines.get(StructureEvaluation).get(Evaluationmatrix)
														.get(Iteration) + "&";
												CSV += PipelineName + "," + EvaluationMatrices.get(m) + ","
														+ StructureEvaluation
																.split("/")[StructureEvaluation.split("/").length - 2]
														+ "," + Iteration + "," + Pipelines.get(StructureEvaluation)
																.get(Evaluationmatrix).get(Iteration)
														+ "\n";

											}

										}

									}

									Row = Row.substring(0, Row.lastIndexOf('&'));

									Row += "\\\\&&";
								}
							}
					}
					Row = Row.substring(0, Row.lastIndexOf('&'));
				}
				Row = Row.substring(0, Row.lastIndexOf('&'));
				Table += Row + " \\hline  \n";
			}
		}
		Table += "\\end{tabular}}\n" + "\n" + "\n" + "\\end{table}";

		PMBPP.CheckDirAndFile("EvaluationTablesAndPlots");
		String XMLName = new File(PathToXML).getName().replaceAll("." + FilenameUtils.getExtension(new File(PathToXML).getName()), "");

		new TxtFiles().WriteStringToTxtFile("EvaluationTablesAndPlots/"+XMLName+"ErrorTableByNumberOfTrees.tex", Table);
		new TxtFiles().WriteStringToTxtFile("EvaluationTablesAndPlots/"+XMLName+"Error.csv", CSV);
		new TxtFiles().WriteStringToTxtFile("EvaluationTablesAndPlots/"+XMLName+"TestData.csv", TestDataCSV);
		
		if(numberofNumberOfIterations.size()==1) // only for one time training 
		ErrorTable(Pipelines,XMLName);
		
	}

	void ErrorTable(TreeMap<String, TreeMap<String, TreeMap<String, String>>> Pipelines, String XMLName) throws FileNotFoundException {
		
		
		String ErrorMeasuresRow="&";
		String StructureEvaluationRow="&";
		String ModelsType="";
		Vector<String> ErrorMeasures = new Vector<String>();
		for(String Measure : Pipelines.get(Pipelines.firstKey()).keySet()) {// Error measures are repeated for all pipelines
			
			
			ErrorMeasures.add(Measure);
		}
		Vector<String> PipelineNames = new Vector<String>();
		Vector<String> StructureEvaluation = new Vector<String>();
		
		for(String Pipe : Pipelines.keySet()) {
			
			ModelsType=Pipe.split("/")[Pipe.split("/").length-3];
			if(!PipelineNames.contains(Pipe.split("/")[Pipe.split("/").length-1]))
				PipelineNames.add(Pipe.split("/")[Pipe.split("/").length-1]);
			
			if(!StructureEvaluation.contains(Pipe.split("/")[Pipe.split("/").length-2]))
				StructureEvaluation.add(Pipe.split("/")[Pipe.split("/").length-2]);
		}
		for(String Measure : ErrorMeasures) {
			ErrorMeasuresRow+="\\multicolumn{3}{c}{"+Measure+"}&";
		}
		ErrorMeasuresRow+="\\\\  ";
		
		for(int i=0 ; i < ErrorMeasures.size() ; ++i)
		for(String Measure : StructureEvaluation) {
			StructureEvaluationRow+=Measure+"&";
		}
		StructureEvaluationRow+="\\\\ \\hline ";
		
		String Table="\\begin{table*}[t] \n \\center \n" + 
				" \\scriptsize \n" + 
				"\\begin{tabular}{lcccccccccccccccccccccc} \n" + 
				"Pipeline variant &\\multicolumn{6}{c}{Evaluation matrix}    \\\\ \\hline\n" + 
				"";
		Table+=ErrorMeasuresRow+"\n";
		Table+=StructureEvaluationRow+"\n";
		HashMap<String,String> ChoseColors=SetColor(ErrorMeasures);
		String NormalizationNote=""; 
		for(int i=0 ; i <PipelineNames.size();++i ) {
			String Row=PipelineNames.get(i)+"&";
			for(int e=0; e < ErrorMeasures.size();++e) {
				for(int ev=0;ev<StructureEvaluation.size();++ev) {
					//System.out.println(Pipelines.get("./"+ModelsType+"/"+StructureEvaluation.get(ev)+"/"+PipelineNames.get(i)).get(ErrorMeasures.get(0)).firstKey());
					String Val= new BigDecimal(Pipelines.get("./"+ModelsType+"/"+StructureEvaluation.get(ev)+"/"+PipelineNames.get(i)).get(ErrorMeasures.get(e)).get(Pipelines.get("./"+ModelsType+"/"+StructureEvaluation.get(ev)+"/"+PipelineNames.get(i)).get(ErrorMeasures.get(e)).firstKey())).toString();

					if(Parameters.getNormalizeStructureEvaluationInErrorTable().equals("T")) {
						if(Parameters.getStructureEvaluationToBeNormalized().contains(StructureEvaluation.get(ev))) {
							 Val= new BigDecimal(Val).divide(new BigDecimal("100")).setScale(2,  RoundingMode.HALF_UP).toString();
							 NormalizationNote="* "+Parameters.getStructureEvaluationToBeNormalized()+" was normalized.";
						}
					}
					Row+="\\cellcolor{"+ChoseColors.get(ErrorMeasures.get(e))+"!25}"+ Val;

					//Row+="\\cellcolor{"+ChoseColors.get(ErrorMeasures.get(e))+"!25}"+ Pipelines.get("./"+ModelsType+"/"+StructureEvaluation.get(ev)+"/"+PipelineNames.get(i)).get(ErrorMeasures.get(e)).get(Pipelines.get("./"+ModelsType+"/"+StructureEvaluation.get(ev)+"/"+PipelineNames.get(i)).get(ErrorMeasures.get(e)).firstKey());
					Row+="&";
				}
			}
			
			Table+=Row+"\\\\ \n";
		}
		Table+="\\hline\n" + 
				"\n" + 
				"\\end{tabular}\n" + 
				"\n" + 
				"\n" + NormalizationNote+ "\n"+
				"\\end{table*}";
		new TxtFiles().WriteStringToTxtFile("EvaluationTablesAndPlots/"+XMLName+"ErrorTable.tex", Table);

	}
	
	HashMap<String,String> SetColor(Vector<String> ErrorMeasures){
		
		
		HashMap<String,String> ChoseColors= new HashMap<String,String>();
		String[] colors = {"red","brown","gray","green","orange","blue","yellow"};
		if(ErrorMeasures.size()>colors.length)
			new Log().Error(this, "Can not  set cell colors for more than 7 columns");
		for(int i=0 ; i < ErrorMeasures.size();++i) {
			ChoseColors.put(ErrorMeasures.get(i), colors[i]);
		}
		return ChoseColors;
	}
}
