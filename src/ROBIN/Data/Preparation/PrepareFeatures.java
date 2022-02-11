package ROBIN.Data.Preparation;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.json.simple.parser.ParseException;

import ROBIN.Log.Log;
import ROBIN.ML.Model.Parameters;
import ROBIN.Utilities.CSVWriter;
import ROBIN.Utilities.FilesUtilities;

/*
 * Saving all the datasets features into CSV to use by others components to speed up models training 
 */
public class PrepareFeatures {
	public static void main(String[] args)
			throws IOException, IllegalArgumentException, IllegalAccessException, ParseException {
		// Example

		// experimental phases
		/*
		Parameters.Phases="HLA,HLB,HLC,HLD";
		new PrepareFeatures().Prepare("/Volumes/PhDHardDrive/EditorsRevision-2/Datasets/NO-NCS");
*/
		
		  Parameters.setPhases("model.HLA,model.HLB,model.HLC,model.HLD");
		  Parameters.setFeatures("RMSD,Skew,Resolution,Max,Min,SequenceIdentity");
		  Parameters.setMR("T"); new PrepareFeatures().Prepare(
		  "/Volumes/PhDHardDrive/MRDatasets/DatasetsForBuilding/");
		 
	}

	public void Prepare(String PathToDatasets)
			throws IOException, IllegalArgumentException, IllegalAccessException, ParseException {
		// TODO Auto-generated method stub
		new Log().TxtInRectangle("Features prepare");

		if (isValid(PathToDatasets) == false)
			System.exit(-1);

		String MTZFileName = "";
		if (new File(PathToDatasets).isFile()) {

			MTZFileName = new File(PathToDatasets).getName()
					.replaceAll("." + FilenameUtils.getExtension(new File(PathToDatasets).getName()), "");

			if (new File(MTZFileName + Parameters.getFeaturesInCSV()).exists()) {

				new Log().Warning(this, MTZFileName + Parameters.getFeaturesInCSV() + " has found and deleted to create new one");
				FileUtils.deleteQuietly(new File(MTZFileName + Parameters.getFeaturesInCSV()));
			}
		}

		File[] mtz = new File[1];// assuming there is a one mtz
		if (new File(PathToDatasets).isFile()) {
			mtz[0] = new File(PathToDatasets);
		} else {
			mtz = new FilesUtilities().FilesByExtension(PathToDatasets,".mtz");
		}
		HashMap<String, LinkedHashMap<String, String>> PDB = new HashMap<String, LinkedHashMap<String, String>>();

		for (File F : mtz) {

			Features fea = new GetFeatures().Get(F.getAbsolutePath());
			LinkedHashMap<String, String> FeatureInMap = new LinkedHashMap<String, String>();
			for (Field field : fea.getClass().getDeclaredFields()) {
				if (Parameters.getFeatures().contains(field.getName())) // if this feature is using
					FeatureInMap.put(field.getName(), String.valueOf((Double) field.get(fea)));

			}

			String MTZ = F.getName().replaceAll("." + FilenameUtils.getExtension(F.getName()), "");

			PDB.put(MTZ, FeatureInMap);

		}

		if (!new File(PathToDatasets).isFile())
			new CSVWriter().WriteFromHashMap(PDB, "features.csv","PDB");// do not use Parameters.getFeaturesInCSV() here because csv writer will add the prefix to csv name
		else {
			new CSVWriter().WriteFromHashMap(PDB, MTZFileName + "features.csv","PDB");
		}
	}

	boolean isValid(String PathToDatasets) {

		if (!FilenameUtils.getExtension(new File(PathToDatasets).getName()).isEmpty()) {
			if (!new File(PathToDatasets).exists()) {
				new Log().Error(this, "mtz file is not found (Maybe it is wrong directory!)");

				return false;
			}
		}

		if (!new File(PathToDatasets).isFile()) {// if not a file then check how many mtz files are there
			if (!new File(PathToDatasets).exists()) {
				new Log().Error(this,
						" No mtz files are found in " + PathToDatasets + " (Maybe it is wrong directory!)");

				return false;
			}
			if (new FilesUtilities().FilesByExtension(PathToDatasets,".mtz").length == 0) {
				new Log().Error(this,
						" No mtz files are found in " + PathToDatasets + " (Maybe it is wrong directory!)");

				return false;
			}
		}

		if (new File(Parameters.getFeaturesInCSV()).exists()) {

			new Log().Warning(this, Parameters.getFeaturesInCSV()+"features.csv has found and deleted to create new one");
			FileUtils.deleteQuietly(new File(Parameters.getFeaturesInCSV()));
		}
		return true;
	}

}
