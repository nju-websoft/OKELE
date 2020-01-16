package util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import model.DataItemValue;

public class MethodUtil {
	
	public static String strip(String rdfUri) {
		return rdfUri.replace("<http://rdf.freebase.com/ns/", "")
				.replace(">", "");
	}
	public static boolean isDigit(String s) {
		boolean flag = true;
		Pattern pattern = Pattern.compile("^[-\\+]?([\\d]+.)?[\\d]+$");
		flag = pattern.matcher(s).matches();
	    return flag;   
	}
	public static boolean isDate(String s) {
		boolean flag = true;
		Pattern pattern = Pattern.compile("[\\d]{4}([-/][\\d]{1,2})?([-/][\\d]{1,2})?");
		flag = pattern.matcher(s).matches();
		return flag;
	}
	public static boolean isDigitStr(String s) {
		boolean flag = true;
		Pattern pattern1 = Pattern.compile(".*[\\d]+.*[A-Za-z]+.*");
		Pattern pattern2 = Pattern.compile(".*[A-Za-z]+.*[\\d]+.*");
		flag = pattern1.matcher(s).matches() || pattern2.matcher(s).matches();
		return flag;
	}
	public static boolean isStr(String s) {
		if(isDigitStr(s))
			return false;
		boolean flag = true;
		Pattern pattern = Pattern.compile(".*[A-Za-z]+.*");
		flag = pattern.matcher(s).matches();
		return flag;
	}
	public static boolean checkPath(String path) {
		File file = new File(path);
		boolean flag = true;
		if(!file.exists()) {
			flag = file.mkdirs();
		}
		return flag;
	}
	public static List<DataItemValue> sortDoubleMap(Map<DataItemValue,Double> map,int size) {
		List<DataItemValue> rs = new ArrayList<DataItemValue>();
		List<Map.Entry<DataItemValue, Double>> infoIds = new ArrayList<Map.Entry<DataItemValue, Double>>(map.entrySet());
		Collections.sort(infoIds, new Comparator<Map.Entry<DataItemValue, Double>>() {   
		    public int compare(Map.Entry<DataItemValue, Double> o1, Map.Entry<DataItemValue, Double> o2) {      
		        return (o2.getValue().compareTo(o1.getValue())); 
		    }
		}); 
		size = (infoIds.size()>size)?size:infoIds.size();
		for (int i = 0; i < size; i++) {
		    rs.add(infoIds.get(i).getKey());
		}
		return rs;
	}
}
