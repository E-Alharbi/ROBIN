package ROBIN.Utilities;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.json.simple.JSONObject;
/*
 * Reading JSON into HashMap
 */
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONReader {

	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
		// TODO Auto-generated method stub

		new JSONReader().JSONToHashMap("");

	}

	public HashMap<String, String> JSONToHashMap(String JSONPath)
			throws FileNotFoundException, IOException, ParseException {
		Object obj = new JSONParser().parse(new FileReader(JSONPath));

		JSONObject json = (JSONObject) obj;

		Set<String> keySet = json.keySet();

		HashMap<String, String> JSON = new HashMap<String, String>();
		for (String keys : keySet) {
			JSON.put(keys, json.get(keys).toString());
		}
		return JSON;
	}
}
