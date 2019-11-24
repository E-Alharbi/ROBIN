package Weka.Predictor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;



import org.apache.commons.io.FileUtils;

import Data.Preparation.CfftMeasures;
import Data.Preparation.cfft;
import Data.Preparation.mtzinfo;
import table.draw.Block;
import table.draw.Board;
import table.draw.Table;

public class Predict {
	public String PredictionTable="";
	public String[][] RowData;
	public  CfftMeasures cfftM;
	public double ResoToUseInGUI=-1;
	public  void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		String Path=args[0];
		
		
		String ModelsPath=Parameters.ModelsPath;
		if(ModelsPath.equals("./TrainedModels"))
			CopyModelsFromResources();
		
			
		
		
		CfftMeasures CM = new cfft().Cfft(Path);
		
		cfftM=CM; // to use in the GUI
		double Reso=new mtzinfo().GetReso(Path);
		ResoToUseInGUI=Reso;
		Predictor Pre = new Predictor(Parameters.PredictorType);
		//Pre.ReadModel("/Users/emadalharbi/eclipse-workspace/ProteinModelBuildingPipelinePredictor/models/ArpPhenix-Completeness.model", Parameters.PredictorType);
		double[] instanceValue1=null;
		if(Parameters.Usecfft==true)
		 instanceValue1 = new double[]{CM.RMSD, CM.Skew, Reso,CM.Max,CM.Min};
		else
			instanceValue1=Parameters.instanceValue1;
		File [] Models = new File(ModelsPath).listFiles();
		HashMap<String, String> Comp = new HashMap<String, String>();
		HashMap<String, String> Rfree = new HashMap<String, String>();
		HashMap<String, String> Rwork = new HashMap<String, String>();
		if(Parameters.AttCSV.equals("None")) {
			Parameters.AttCSV=this.getClass().getClassLoader().getResource("att.csv").toURI().toString();
		}
		String Att=Parameters.AttCSV;
		//String Att="/Users/emadalharbi/eclipse-workspace/ProteinModelBuildingPipelinePredictor/target/classes/att.csv";
		System.out.println("Att "+Att);
		
		for(File m : Models) {
			//System.out.println(m.getName());
			Pre.ReadModel(m.getAbsolutePath(), Parameters.PredictorType);
			//System.out.println(Pre.Predicte(instanceValue1,Att));
			DecimalFormat df = new DecimalFormat("#.##");
			df.setRoundingMode(RoundingMode.HALF_UP);
            if(m.getName().contains("Rfree")) {
            	
            	Rfree.put( m.getName().substring(0, m.getName().indexOf("-")),df.format(BigDecimal.valueOf(Pre.Predicte(instanceValue1,Att))));
}
            if(m.getName().contains("Completeness")) {
            	Comp.put( m.getName().substring(0, m.getName().indexOf("-")),df.format(BigDecimal.valueOf(Pre.Predicte(instanceValue1,Att))));
}
            if(m.getName().contains("Rwork")) {
            	Rwork.put( m.getName().substring(0, m.getName().indexOf("-")),df.format(BigDecimal.valueOf(Pre.Predicte(instanceValue1,Att))));
}
		}
		Vector<HashMap<String, String>> Measures= new Vector<HashMap<String, String>>();
		// order is important 
		Measures.add(Rfree);
		Measures.add(Rwork);
		Measures.add(Comp);
		Print(Measures);
		
	}

	void Print(Vector<HashMap<String, String>> Measures) {
		//List<String> headersList = Arrays.asList("Pipeline");
		List<String> headersList = new ArrayList<String>();
        List<List<String>> rowsList = new ArrayList<List<String>>();
        headersList.add("Pipeline");
		//String Table="\t";
		//if(Measures.get(0).size()!=0) {
			//Table+="R-free\t";
			 headersList.add("R-free");
		//}
		//if(Measures.get(1).size()!=0) {
		//	Table+="R-work\t";
			headersList.add("R-work");
		//}
		//if(Measures.get(2).size()!=0) {
		//	Table+="Completeness\t";
			headersList.add("Completeness");
		//}
		//Table+="\n";
		//System.out.println(Table);
		
		Vector<String> PipelinesNames= new Vector<String>();
		for(int i=0 ; i < Measures.size();++i) {
			
				for (String Pipe : Measures.get(i).keySet()) {
					
				if(!PipelinesNames.contains(Pipe))
					PipelinesNames.add(Pipe);
				}
		}
		 
		for(int i=0 ; i < PipelinesNames.size();++i) {
			String Rfree="-";
			String Rwork="-";
			String Completeness="-";
			 ArrayList<String> list = new ArrayList<String>();
			for (String Pipe : Measures.get(0).keySet()) 
				if(PipelinesNames.get(i).equals(Pipe)) {
					Rfree=Measures.get(0).get(Pipe).toString();
				}
			for (String Pipe : Measures.get(1).keySet()) 
				if(PipelinesNames.get(i).equals(Pipe)) {
					Rwork=Measures.get(1).get(Pipe).toString();
				}
			for (String Pipe : Measures.get(2).keySet()) 
				if(PipelinesNames.get(i).equals(Pipe)) {
					Completeness=Measures.get(2).get(Pipe).toString();
				}
			list.add(PipelinesNames.get(i));
			list.add(Rfree);
			list.add(Rwork);
			list.add(Completeness);
			rowsList.add(list);
			//Table+=PipelinesNames.get(i)+"\t"+Rfree+"\t"+Rwork+"\t"+Completeness+"\n";
		}
		
		//System.out.println(Table);
		 List<Integer> colWidthsListEdited = Arrays.asList(15, 15, 15, 35);
		 List<Integer> colAlignList = Arrays.asList(
				 Block.DATA_CENTER,
				    Block.DATA_CENTER, 
				    Block.DATA_CENTER, 
				    Block.DATA_CENTER);
				
		 Board board = new Board(250);
		
		 Table table = new Table(board, 200, headersList, rowsList);
		 table.setGridMode(Table.GRID_FULL).setColWidthsList(colWidthsListEdited);
		 table.setColAlignsList(colAlignList);
		 Block tableBlock = table.tableToBlocks();
		 board.setInitialBlock(tableBlock);
		 board.build();
		 String tableString = board.getPreview();
	System.out.println(tableString);
	PredictionTable=tableString;
	
	//convert to array for GUI
	//https://stackoverflow.com/questions/371839/java-nested-list-to-array-conversion
	String[][] array = new String[rowsList.size()][];
	String[] blankArray = new String[0];
	for(int i=0; i < rowsList.size(); i++) {
	    array[i] = rowsList.get(i).toArray(blankArray);
	}
	RowData=array;
	}
	
	void CopyModelsFromResources() throws IOException, URISyntaxException {
		FileUtils.deleteDirectory(new File("TrainedModels")); 
		PMBPP.CheckDirAndFile("TrainedModels");
		// Files must compress without the folder itself for example zip -r ../zipped_dir.zip *
		//following code obtained from http://zetcode.com/java/zipinputstream/	
		   Path outDir = Paths.get("./TrainedModels");
	       
	        InputStream is = this.getClass().getResourceAsStream("/Models.zip");
	        byte[] buffer = new byte[is.available()];
	        try (
	                BufferedInputStream bis = new BufferedInputStream(is);
	                ZipInputStream stream = new ZipInputStream(bis)) {

	            ZipEntry entry;
	            while ((entry = stream.getNextEntry()) != null) {

	                Path filePath = outDir.resolve(entry.getName());

	                try (FileOutputStream fos = new FileOutputStream(filePath.toFile());
	                        BufferedOutputStream bos = new BufferedOutputStream(fos, buffer.length)) {

	                    int len;
	                    while ((len = stream.read(buffer)) > 0) {
	                        bos.write(buffer, 0, len);
	                    }
	                }
	            }
	        }

	}
	
	
}
