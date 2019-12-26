package PMBPP.Utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import PMBPP.Log.Log;
import PMBPP.ML.Model.Parameters;

public class CSVReader {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		HashMap <String,Boolean> a = new CSVReader().Headers("/Users/emadalharbi/Downloads/PMBPP/CSV/");
		
		for(String key : a.keySet()) {
			System.out.println("key "+ key + " "+a.get(key));
		}
		/*
		for (CSVRecord record : csvParser) {
		    String field_1 = record.get(0);
		    String field_2 = record.get(1);
		    System.out.println(field_1);
		    
		}
		*/
	}

	public HashMap <String,Boolean> Headers(String CSV) throws IOException{
		// True if the column is a feature 
		HashMap <String,Boolean> Features = new HashMap <String , Boolean>();
		File [] files = new File[0];
		if(new File(CSV).isFile()) {
			File [] temp = {new File(CSV)};
			files=temp;
		}
		else {
			files =new FilesUtilities().ReadFilesList(CSV);
			
		}
		for(File csv : files) {
		Reader in = new FileReader(csv);
		CSVParser csvParser = CSVFormat.DEFAULT.withHeader().parse(in);
		List<String> a = csvParser.getHeaderNames();
		
		
		for(String h : a) {
			if(Features.containsKey(h)==false) {
				if(Parameters.Featuers.contains(h)) {
					Features.put(h, true);
				}
				else
					Features.put(h, false);
			}
		}
		
		
		}
		
		
		return Features;
	}
	
	public HashMap <String,Boolean> FilterByFeatures (String CSV, boolean ByFeatures) throws IOException{
		HashMap <String,Boolean>  Temp = Headers(CSV);
		HashMap <String,Boolean>  Features =  new HashMap <String,Boolean>();
		for(String K : Temp.keySet()) {
			if(Temp.get(K)==ByFeatures)
				Features.put(K, Temp.get(K));
			new Log().Info(this, " \""+K+"\" "+ (Temp.get(K) == true ? "it is  a feature ": " Not a feature"));
			
		}
		return Features;
	}
	
	public String GetRecordByHeaderName(String CSV,String  Header, int Index ) throws IOException {
		Reader in = new FileReader(CSV);
		
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader().parse(in);
		int index=0;
		for (CSVRecord record : records) {
		    String HeaderValue = record.get(Header);
		    if(Index==index) return HeaderValue;
		    index++;
		}
		return null;
	}
	
	public HashMap<String, Vector<HashMap<String,String>>> ReadIntoHashMap(String CSV, String IDHeader) throws IOException{
        Reader in = new FileReader(CSV);
		
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader().parse(in);
		HashMap <String,Boolean>  Headers = Headers(CSV);
		HashMap<String, Vector<HashMap<String,String>>> CSVInTable= new HashMap<String, Vector<HashMap<String,String>>>();
		for (CSVRecord record : records) {
		   
		    HashMap <String,String> map = new HashMap <String,String>();
		    for(String Key : Headers.keySet()) {
		    	if(!Key.equals(IDHeader))
		    	map.put(Key, record.get(Key));
		    }
		    if(CSVInTable.containsKey(record.get(IDHeader))) {
		    	Vector<HashMap<String,String>> Temp = new Vector<HashMap<String,String>>();
		    	for(int i=0 ; i < CSVInTable.get(record.get(IDHeader)).size() ; ++i) {
		    		Temp.add( CSVInTable.get(record.get(IDHeader)).get(i));
		    	}
		    	Temp.add(map);
		    	CSVInTable.put(record.get(IDHeader), Temp);
		    }
		    else {
		    	Vector<HashMap<String,String>> Temp = new Vector<HashMap<String,String>>();
		    	Temp.add(map);
		    	CSVInTable.put(record.get(IDHeader), Temp);
		    }
		}
		return CSVInTable;
	}
	
	public HashMap<String, Vector<HashMap<String,String>>> ReadIntoHashMapWithFilterdHeaders(String CSV ,String IDHeader, Vector<String> Headers) throws IOException{
		HashMap<String, Vector<HashMap<String,String>>>  map = ReadIntoHashMap(CSV,IDHeader);
		HashMap<String, Vector<HashMap<String,String>>> FilterdMap= new HashMap<String, Vector<HashMap<String,String>>>();
		
		for(String ID : map.keySet()) {
			Vector<HashMap<String,String>> headers = new Vector<HashMap<String,String>>();
			for(int i = 0 ; i < map.get(ID).size();++i) {
				HashMap<String,String> FilterdHeaders = new HashMap<String,String>();
				for(String Mapheaders :  map.get(ID).get(i).keySet()) {
					if(Headers.contains(Mapheaders)) {
						FilterdHeaders.put(Mapheaders, map.get(ID).get(i).get(Mapheaders));
					}
				}
				headers.add(FilterdHeaders);
			}
			FilterdMap.put(ID, headers);
		}
		return FilterdMap;
		
	}
}
