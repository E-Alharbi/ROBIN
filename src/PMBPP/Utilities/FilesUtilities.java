package PMBPP.Utilities;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.Vector;

import org.apache.commons.io.FilenameUtils;

import PMBPP.Log.Log;
import PMBPP.ML.Model.Parameters;

/*
 * methods to handle files. 
 */
public class FilesUtilities {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		File[] files = new FilesUtilities().ReadFilesList("/PredictionModels");
		for (File f : files) {
			System.out.println(f.getName());
		}
	}

	public File[] ReadFilesList(String Dir) {
		// https://stackoverflow.com/questions/30486404/java-list-files-in-a-folder-avoiding-ds-store/30486678
		if (!new File(Dir).isDirectory())
			new Log().Error(this, Dir + " not found or not a directory");

		File[] files = new File(Dir).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return !name.equals(".DS_Store");
			}
		});
		return files;
	}

	public File[] ReadMtzList(String Dir) {
		// https://stackoverflow.com/questions/30486404/java-list-files-in-a-folder-avoiding-ds-store/30486678
		File[] files = new File(Dir).listFiles(new FileFilter() {
			@Override
			public boolean accept(File dir) {
				return dir.getName().endsWith(".mtz");
			}
		});
		return files;
	}

	public File[] ReadFilteredModels(String Dir) {

		File[] AllModels = ReadFilesList(Dir);
		Vector<File> FilteredModels = new Vector<File>();
		for (File m : AllModels) {

			String modelName = m.getName().replaceAll("." + FilenameUtils.getExtension(m.getName()), "");

			if (Parameters.FilteredModels.contains(modelName)) {

				FilteredModels.add(m);
			}

		}
		return FilteredModels.toArray(new File[FilteredModels.size()]);
	}

}
