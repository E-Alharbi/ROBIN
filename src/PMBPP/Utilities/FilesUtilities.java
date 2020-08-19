package PMBPP.Utilities;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

import PMBPP.Log.Log;
import PMBPP.ML.Model.Parameters;

/*
 * methods to handle files. 
 */
public class FilesUtilities {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		File[] files = new FilesUtilities().FilesByExtensionRecursively("/Users/emadalharbi/Downloads/PMBPP/PredictionModelsMR",".csv");
		for (File f : files) {
			System.out.println(f.getAbsolutePath());
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

	public File[] FilesByExtension(String Dir, String Extension) {
		// https://stackoverflow.com/questions/30486404/java-list-files-in-a-folder-avoiding-ds-store/30486678
		File[] files = new File(Dir).listFiles(new FileFilter() {
			@Override
			public boolean accept(File dir) {
				return dir.getName().endsWith(Extension);
			}
		});
		return files;
	}
	
	

	
	
	
	public File[] ReadFilteredModels(String Dir) {

		//File[] AllModels = ReadFilesList(Dir);
		File[] AllModels = FilesByExtension(Dir,".model");
		Vector<File> FilteredModels = new Vector<File>();
		for (File m : AllModels) {

			String modelName = m.getName().replaceAll("." + FilenameUtils.getExtension(m.getName()), "");

			if (Parameters.getFilteredModels().contains(modelName)) {

				FilteredModels.add(m);
			}

		}
		return FilteredModels.toArray(new File[FilteredModels.size()]);
	}

	public File[] FilesByExtensionRecursively(String Dir, String Extension) throws IOException {
		Collection<File> files= FileUtils.listFiles(
				new File(Dir), 
				  new RegexFileFilter("^(.*?)"+Extension), 
				  DirectoryFileFilter.DIRECTORY
				);
		return files.toArray(new File[files.size()]);
	}
	
	//https://stackoverflow.com/questions/30507653/how-to-check-whether-file-is-gzip-or-not-in-java
		public  boolean isGZipped(File f) {
			  int magic = 0;
			  try {
			   RandomAccessFile raf = new RandomAccessFile(f, "r");
			   magic = raf.read() & 0xff | ((raf.read() << 8) & 0xff00);
			   raf.close();
			  } catch (Throwable e) {
			   e.printStackTrace(System.err);
			  }
			  return magic == GZIPInputStream.GZIP_MAGIC;
			 }
	
}
