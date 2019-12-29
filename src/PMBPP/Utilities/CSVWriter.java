package PMBPP.Utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;

import org.apache.commons.io.FileUtils;

import Comparison.Analyser.ExcelContents;
import Comparison.Analyser.ExcelSheet;
import PMBPP.Data.Preparation.ExcelContentsWithFeatures;
import PMBPP.Data.Preparation.PredictionTrainingDataPreparer;
import PMBPP.ML.Model.PMBPP;
import PMBPP.ML.Model.Parameters;
import weka.core.Instances;

public class CSVWriter {

	public String PathToSaveCSV="./";
	// Ex HashMap {1o6a,{R-free,0.20,R-work,0.10,Com,20}}
	public void WriteFromHashMap(HashMap<String,LinkedHashMap<String,String>> CSVContents , String Name) throws FileNotFoundException {
		Vector<String> MeasurementUnitsHeaders = new Vector<String> ();
		for(String PDB : CSVContents.keySet()) {
			//System.out.print(PDB+"\t");
			for(String Key : CSVContents.get(PDB).keySet()) { // give each an index R-free index 0 R-work 1 ... etc 
				//System.out.print(Key+"\t"+CSVContents.get(PDB).get(Key));
				if(!MeasurementUnitsHeaders.contains(Key))
					MeasurementUnitsHeaders.add(Key);
			}
			
			//System.out.println();
		}
		String CSV="PDB";
		
		for(int  i=0; i < MeasurementUnitsHeaders.size() ; ++i)// add all headers 
			CSV+=","+MeasurementUnitsHeaders.get(i);
		CSV+="\n";
		for(String PDB : CSVContents.keySet()) {
			int HeaderIndex=0;
			
			String Record1=PDB; 
			
			for(String Key : CSVContents.get(PDB).keySet()) {
				if(MeasurementUnitsHeaders.get(HeaderIndex).equals(Key)) { // check the headers order because hashmaps are unsorted  
						
					Record1+=","+CSVContents.get(PDB).get(Key);	
					HeaderIndex++;
				}
				else {// very rare to happen 
					System.out.println("Error: Can not continue because there is a change in the headers order!  ");
				System.exit(-1);
				}
			}
			
			
			
			
			CSV+=Record1;
			CSV+="\n";
			
		}
		try(  PrintWriter out = new PrintWriter(Name)){
		    out.println( CSV );
		}
	}
	public void WriteToCSV(Vector<ExcelContentsWithFeatures> Excel , String Pipeline) throws FileNotFoundException, IllegalArgumentException, IllegalAccessException {
		
		String [] features = Parameters.Featuers.split(",");
		String [] MeasurementUnitsToPredict = Parameters.MeasurementUnitsToPredict.split(",");
	
		String CSV=Parameters.Featuers+","+Parameters.MeasurementUnitsToPredict+",PDB\n";// headers
		
		
		
		
		for(ExcelContentsWithFeatures E : Excel) {

			for(int i= 0 ; i < features.length ; ++i) {
				if(i+1< features.length)
				CSV+=E.CM.GetFeatureByName(features[i])+",";
				else
					CSV+=E.CM.GetFeatureByName(features[i]);
			}
			for(int i= 0 ; i < MeasurementUnitsToPredict.length ; ++i) {
				if(MeasurementUnitsToPredict[i].equals("Completeness"))
					CSV+=","+E.Completeness;
				if(MeasurementUnitsToPredict[i].equals("R-free"))
					CSV+=","+E.R_free0Cycle;
				if(MeasurementUnitsToPredict[i].equals("R-work"))
					CSV+=","+E.R_factor0Cycle;
				
			}
			CSV+=","+E.PDB_ID+"\n";
			//CSV+=E.CM.RMSD+","+E.CM.Skew+","+E.Resolution+","+E.CM.Max+","+E.CM.Min+","+E.Completeness+","+E.R_free0Cycle+","+E.R_factor0Cycle+"\n";
		}
		try(  PrintWriter out = new PrintWriter( PathToSaveCSV+"/"+Pipeline.substring(0,Pipeline.indexOf("."))+".csv")){
		    out.println( CSV );
		}
		
	}
	
	public void WriteInstancesToCSV(Instances dataset , String Name) throws FileNotFoundException {
		String CSV="";
		for(int i=0 ; i < dataset.numAttributes() ; ++i) {
			if(i+1 < dataset.numAttributes())
			CSV+=dataset.attribute(i).name()+",";
			else
				CSV+=dataset.attribute(i).name();
		}
		CSV+="\n";
		for(int n=0 ; n < dataset.numInstances() ; ++n) {
			String Record="";
			for(int i=0 ; i < dataset.numAttributes() ; ++i) {
				String Val="";
				if(dataset.attribute(i).name().equals("PDB")){
					Val=dataset.get(n).stringValue(i);
				}
				else {
					Val=String.valueOf(dataset.get(n).value(i));
				}
				
				if(i+1 < dataset.numAttributes()) {
				Record+=Val+",";
				}
				else {
					Record+=Val;
				}
			}
			CSV+=Record+"\n";
		}
		try(  PrintWriter out = new PrintWriter( Name+".csv")){
		    out.println( CSV );
		}
	}
}
