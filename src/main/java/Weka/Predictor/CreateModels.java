package Weka.Predictor;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Vector;

public class CreateModels {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		//File [] files = new File(args[0]).listFiles();
		File [] files = new File("/Users/emadalharbi/Downloads/PMBPP/CSV2").listFiles();
	for(File F : files) {
		Vector<String> att = new Vector<String>();
	   //Completeness 
		
		att.add("R-free"); // R-free
		att.add("R-work"); // -R-work
		
		new CreateModels().CreateModel(F.getName().substring(0,F.getName().indexOf(".")) +"-Completeness", F.getAbsolutePath(), "Completeness" ,att );
		
		att.clear();
		
		att.add("Completeness"); // Completeness
		att.add("R-work"); // R-work
		
		new CreateModels().CreateModel(F.getName().substring(0,F.getName().indexOf(".")) +"-Rfree", F.getAbsolutePath(), "R-free" ,att );
		
        att.clear();
		
		att.add("Completeness"); // Completeness
		att.add("R-free"); // R-free
		
		new CreateModels().CreateModel(F.getName().substring(0,F.getName().indexOf(".")) +"-Rwork", F.getAbsolutePath(), "R-work" ,att );
		
		
	}
	}

	void CreateModel(String Name , String Data , String Label , Vector<String> RemovedAtt) throws Exception {
		
		//System.out.print(Name.substring(0, Name.indexOf("-"))+"\t\t\t");
		System.out.print(Name+"\t\t\t");
		Predictor Pre = new Predictor(Parameters.PredictorType);
		Pre.LoadData(Data, 3, Label); // Label here start from zero
		
		
		Pre.RemoveAttribute(RemovedAtt , Label); // here starts from 1. First att is 1
		//Pre.Normalize();
		Pre.Split();
		Pre.Train();
		Pre.Evaluate();
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.HALF_UP);
		if(Name.contains("Completeness"))
		System.out.println(df.format(BigDecimal.valueOf(Pre.Accuracy(true))));
		else
		System.out.println(df.format(BigDecimal.valueOf(Pre.Accuracy(false))));
		Pre.SaveModel(Name);
	}
}
