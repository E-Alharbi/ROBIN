package ROBIN.Utilities;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import ROBIN.Log.Log;
import ROBIN.ML.Model.ROBIN;

/*
 * Splitting the job into small jobs. A one for each pipeline when we run on cluster server. 
 */
public class Cluster {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		new Cluster().Jobs();
	}

	public void Jobs() throws IOException {
		int count = 0;
		File sh = new File("");
		File Jar = new File("");
		for (File shell : new FilesUtilities().ReadFilesList("./")) {

			if (FilenameUtils.getExtension(shell.getName()).equals("sh")) {
				count++;
				sh = shell;
			}
			if (FilenameUtils.getExtension(shell.getName()).equals("jar")) {

				Jar = shell;
			}
		}

		if (count > 1) {
			new Log().Error(this, "We found more than 1 shell script. We cannot identify which one to use! ");
		}
		if (count == 0) {
			new Log().Error(this, "We did not find the shell script.");

		}
		String shcontent = new TxtFiles().readFileAsString(sh.getAbsolutePath());
		if (!shcontent.contains("ExcelFolder="))
			new Log().Error(this, "We did not find ExcelFolder= in the shell script.");

		String ExcelFolder = shcontent.split("ExcelFolder=")[1].split(" ")[0];
		ROBIN.CheckDirAndFile("ExcelFolder");
		HashMap<String, String> ExcelAndTheirFolders = new HashMap<String, String>();
		for (File Excel : new FilesUtilities().ReadFilesList(ExcelFolder)) {

			String ExcelName = Excel.getName().replaceAll("." + FilenameUtils.getExtension(Excel.getName()), "");

			// Create a folder for each
			ROBIN.CheckDirAndFile("ExcelFolder/" + ExcelName);
			FileUtils.copyFileToDirectory(Excel, new File("ExcelFolder/" + ExcelName));
			ExcelAndTheirFolders.put(ExcelName, new File("ExcelFolder/" + ExcelName).getAbsolutePath());
		}

		// Now create a shell script for each
		for (String Excel : ExcelAndTheirFolders.keySet()) {
			ROBIN.CheckDirAndFile(Excel);

			new TxtFiles().WriteStringToTxtFile(Excel + "/" + Excel + ".sh",
					shcontent.replace("ExcelFolder=" + ExcelFolder, "ExcelFolder=" + ExcelAndTheirFolders.get(Excel)));
			FileUtils.copyFileToDirectory(Jar, new File(Excel));

			Runtime.getRuntime().exec("sbatch " + Excel + ".sh", null, new File(Excel));

		}

	}
}
