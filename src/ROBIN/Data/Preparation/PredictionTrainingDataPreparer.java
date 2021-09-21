package ROBIN.Data.Preparation;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Vector;

import org.json.simple.parser.ParseException;

import Comparison.Analyser.ExcelContents;
import Comparison.Analyser.ExcelLoader;
import ROBIN.Log.Log;
import ROBIN.ML.Model.ROBIN;
import ROBIN.ML.Model.Parameters;
import ROBIN.Utilities.CSVWriter;
import ROBIN.Utilities.FilesUtilities;

/*
 * Reading the data from excel and save what we need in CSV
 */

public class PredictionTrainingDataPreparer {

	public static void main(String[] args) throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, IOException, NumberFormatException, IllegalArgumentException, ParseException {

		// Example

		// experimental phases
		/*
		String[] arg = { "/noncs", "/Datasets/NO-NCS/", "CSV" };

		new PredictionTrainingDataPreparer().Prepare(arg);
*/
		// MR
		
		Parameters.setPhases("model.HLA,model.HLB,model.HLC,model.HLD");
		Parameters.setFeatures("RMSD,Skew,Resolution,Max,Min,SequenceIdentity,Fmap");
		Parameters.setMR("T"); String [] arg = {"/Volumes/PhDHardDrive/PMBPP/FinalTraining/PMBPPResults/MR/noncs/","/Volumes/PhDHardDrive/MRDatasets/DatasetsForBuilding/"}; new
		PredictionTrainingDataPreparer().Prepare(arg);
		 
	}

	public void Prepare(String[] args) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException,
			IOException, NumberFormatException, IllegalArgumentException, ParseException {
		// TODO Auto-generated method stub

		new Log().TxtInRectangle("Prediction training data preparer");

		isValid(args[0], args[1]);

		File[] files = new FilesUtilities().ReadFilesList(args[0]);
		String PathToDataset = args[1];
		Vector<ExcelContentsWithFeatures> Excel2 = new Vector<ExcelContentsWithFeatures>();
		for (File e : files) {
			ExcelLoader f = new ExcelLoader();
			Vector<ExcelContents> Excel = new Vector<ExcelContents>();

			Excel = f.ReadExcel(e.getAbsolutePath());

			Excel2 = new ExcelContentsWithFeatures().Addall(Excel);
			Collections.sort(Excel2, ExcelContentsWithFeatures.SortingByPDB); // Sorting here to get the same test/train
																				// data across all pipelines. Because of
																				// using same seed to randomise the
																				// data, we will get same test/train
																				// every time we create a new model
			new Log().TxtInRectangle("Excel: " + e.getName());

			for (ExcelContentsWithFeatures EE : Excel2) {
				EE.CM = new GetFeatures().Get(PathToDataset + EE.PDB_ID + ".mtz");

				new Log().Info(this, "Get data from the excel file: " + EE.PDB_ID);
			}

			CSVWriter CW = new CSVWriter();
			if (args.length > 2) {
				CW.PathToSaveCSV = args[2];

				ROBIN.CheckDirAndFile(CW.PathToSaveCSV);
			}
			CW.WriteToCSV(Excel2, e.getName());
		}

	}

	void isValid(String files, String PathToDatasets) {

		if (!new File(files).exists()) {
			new Log().Error(this, "Excel files are not found (Maybe it is wrong directory!)");

		}
		if (!new File(PathToDatasets).exists()) {
			new Log().Error(this, "Datasets directory is not found  (Maybe it is wrong directory!)");

		}

	}
}
