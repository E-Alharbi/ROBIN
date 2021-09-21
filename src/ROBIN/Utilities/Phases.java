package ROBIN.Utilities;

import java.util.HashMap;

public class Phases {

	
	
	public boolean Parrot(String Pipeline) {
		HashMap<String,Boolean> names = new HashMap<String,Boolean>();
		names.put("ARPwARP|Buccaneer",true);
		names.put("ARPwARP|Phenix AutoBuild(P)",true);
		names.put("ARPwARP|Phenix AutoBuild",false);
		names.put("ARPwARP",true);
		names.put("Buccaneer|ARPwARP",true);
		names.put("Buccaneer|Phenix AutoBuild(P)",true);
		names.put("Buccaneer|Phenix AutoBuild",false);
		names.put("Buccaneer",true);
		names.put("Phenix AutoBuild(P)|ARPwARP",true);
		names.put("Phenix AutoBuild(P)|Buccaneer",true);
		names.put("Phenix AutoBuild(P)",true);
		names.put("Phenix AutoBuild|ARPwARP",true);
		names.put("Phenix AutoBuild|Buccaneer",true);
		names.put("Phenix AutoBuild",false);
		names.put("SHELXE|ARPwARP",true);
		names.put("SHELXE|Buccaneer",true);
		names.put("SHELXE|Phenix AutoBuild(P)",true);
		names.put("SHELXE|Phenix AutoBuild",false);
		names.put("SHELXE",false);
		names.put("SHELXE(P)|ARPwARP",true);
		names.put("SHELXE(P)|Buccaneer",true);
		names.put("SHELXE(P)|Phenix AutoBuild(P)",true);
		names.put("SHELXE(P)|Phenix AutoBuild",false);
		names.put("SHELXE(P)",true);
		return names.get(Pipeline);
	}
}
