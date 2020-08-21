package PMBPP.Utilities;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
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

	}

	public void UpdateMLModelNames(String Dir, String Extension) throws IOException {
		
		File [] files = new FilesUtilities().FilesByExtensionRecursively(Dir, Extension);
	HashMap<String,String> names = new HashMap<String,String>();
	names.put("ARPwARP#Buccaneeri1I5", "ARPwARP|Buccaneer");
	names.put("ARPwARP#Phenix", "ARPwARP|Phenix AutoBuild(P)");
	names.put("ARPwARP#PhenixHLA", "ARPwARP|Phenix AutoBuild");
	names.put("ARPwARP", "ARPwARP");
	names.put("Buccaneeri1I5#ARPwARP", "Buccaneer|ARPwARP");
	names.put("Buccaneeri1I5#Phenix", "Buccaneer|Phenix AutoBuild(P)");
	names.put("Buccaneeri1I5#PhenixHLA", "Buccaneer|Phenix AutoBuild");
	names.put("Buccaneeri1I5", "Buccaneer");
	names.put("Phenix#ARPwARP", "Phenix AutoBuild(P)|ARPwARP");
	names.put("Phenix#Buccaneeri1I5", "Phenix AutoBuild(P)|Buccaneer");
	names.put("Phenix", "Phenix AutoBuild(P)");
	names.put("PhenixHAL#ARPwARP", "Phenix AutoBuild|ARPwARP");
	names.put("PhenixHAL#Buccaneeri1I5", "Phenix AutoBuild|Buccaneer");
	names.put("PhenixHAL", "Phenix AutoBuild");
	names.put("ShelxeWithTFlag#Arp", "SHELXE|ARPwARP");
	names.put("ShelxeWithTFlag#Buccaneeri1I5", "SHELXE|Buccaneer");
	names.put("ShelxeWithTFlag#Phenix", "SHELXE|Phenix AutoBuild(P)");
	names.put("ShelxeWithTFlag#PhenixHLA", "SHELXE|Phenix AutoBuild");
	names.put("ShelxeWithTFlag", "SHELXE");
	names.put("ShelxeWithTFlagChFomPhi#Arp", "SHELXE(P)|ARPwARP");
	names.put("ShelxeWithTFlagChFomPhi#Buccaneeri1I5", "SHELXE(P)|Buccaneer");
	names.put("ShelxeWithTFlagChFomPhi#Phenix", "SHELXE(P)|Phenix AutoBuild(P)");
	names.put("ShelxeWithTFlagChFomPhi#PhenixHLA", "SHELXE(P)|Phenix AutoBuild");
	names.put("ShelxeWithTFlagChFomPhi", "SHELXE(P)");
	
	names.put("Buccaneeri1I5ModelSeed", "Buccaneer");
	
	
	for(File f : files) {
		
		String FileName = f.getName().replaceAll("." + FilenameUtils.getExtension(f.getName()), "");
		
		String NewName=names.get(FileName);
		
		f.renameTo(new File(f.getParent()+"/"+NewName+Extension));
		
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
