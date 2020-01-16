package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import info.debatty.java.stringsimilarity.JaroWinkler;
import info.debatty.java.stringsimilarity.NormalizedLevenshtein;
import model.DataItemValue;
import model.PropertyFeature;
import model.PropertyFeature.ValueType;

public class DataComparator {
	
	private static HashMap<String, Set<Integer>> synonym_evalute = new HashMap<>();
	
	static {
		init();
	}

	private static void init(){
		try{
			BufferedReader br = new BufferedReader(new FileReader("./src/main/java/util/Synonym_evalute.txt"));
			String line = null;
			br.readLine();
			int cnt = 0;
			while((line= br.readLine())!=null){
				String[] words = line.split("##");
				if(words.length>=2){
					cnt+=1;
					for(String word:words){
						word = word.toLowerCase().replaceAll("[�~!！^()_+=|\\[\\]{}【】;\"、‘’“”,，<《。>》?？：:\\-\t·'\\\\]"," ")
								.replaceAll(" & "," and ")
								.replaceAll("[ ]+"," ")
								.trim();
						if(synonym_evalute.containsKey(word))
							synonym_evalute.get(word).add(cnt);
						else {
							Set<Integer> t = new HashSet<>();
							t.add(cnt);
							synonym_evalute.put(word, t);
						}
//						System.out.println(word);
					}
				}
			}
			br.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}


	public static boolean isEqual(String v1, String v2,String relamid){
		v1 = v1.toLowerCase().replaceAll("[�~!！^()_+=|\\[\\]{}【】;\"、↓‘’“”,，<《。>》?？：:\t·'\\\\/]"," ")
				.replaceAll(" & "," and ")
				.replaceAll("[ ]+"," ")
				.replaceAll(" #[1-9]$","")
				.trim();
				
		v2 = v2.toLowerCase().replaceAll("[�~!！^()_+=|\\[\\]{}【】;\"、↓‘’“”,，<《。>》?？：:\t·'\\\\/]"," ")
				.replaceAll(" & "," and ")
				.replaceAll("[ ]+"," ")
				.replaceAll(" #[1-9]$","")
				.trim();

		if(relamid.contains("<http://rdf.freebase.com/ns/location.geocode.latitude>")||relamid.contains("<http://rdf.freebase.com/ns/location.geocode.longitude>")
		        ||relamid.contains("/location.geocode.latitude")||relamid.contains("/location.geocode.longitude")){
			try{
				double a = Double.parseDouble(v1);
				double b = Double.parseDouble(v2);
				if(Math.abs(a-b)<0.1)
					return true;
				else
					return false;
			}catch (Exception e){
				return false;
			}
		}
		if(relamid.contains("award.award_nominee.award_nominations")
				||relamid.contains("<http://rdf.freebase.com/ns/award.award_winner.awards_won>")
				||relamid.contains("<http://rdf.freebase.com/ns/award.award_winning_work.awards_won>")
				||relamid.contains("award")){ 
			v1 = v1.replaceAll("[0-9]{1,2}(st|th) ","").replaceAll("[0-9]{4} ","").trim();
			v2 = v2.replaceAll("[0-9]{1,2}(st|th) ","").replaceAll("[0-9]{4} ","").trim();
		}
		if(relamid.equals("<http://rdf.freebase.com/ns/food.ingredient.recipes>-<http://rdf.freebase.com/ns/food.recipe_ingredient.quantity>")
		||relamid.equals("<http://rdf.freebase.com/ns/food.ingredient.recipes>-<http://rdf.freebase.com/ns/food.recipe_ingredient.unit>")
		||relamid.equals("<http://rdf.freebase.com/ns/food.food.energy>")
		||relamid.equals("<http://rdf.freebase.com/ns/food.food.nutrients>-<http://rdf.freebase.com/ns/food.nutrition_fact.quantity>")
		||relamid.equals("<http://rdf.freebase.com/ns/food.food.nutrients>-<http://rdf.freebase.com/ns/food.nutrition_fact.ius>")){
			try{
//				System.out.println(v1 + "\t" + v2);
				v1 = v1.replaceAll("[a-zA-Z ]","");
				v2 = v2.replaceAll("[a-zA-Z ]","");
				double a = Double.parseDouble(v1);
				double b = Double.parseDouble(v2);
				if(Math.abs(a-b)<=1e-8)
					return true;
				else
					return false;
			}catch (Exception e){
				return false;
			}
		}

		if(relamid.equals("<http://rdf.freebase.com/ns/chemistry.chemical_compound.average_molar_mass>")
		||relamid.equals("<http://rdf.freebase.com/ns/people.person.weight_kg>")||relamid.contains("location.location.area")
				||relamid.contains("/boats.ship.displacement")||relamid.contains("/boats.ship.length_overall")||relamid.contains("/boats.ship.beam")
				||relamid.contains("/boats.ship.draught")
				||relamid.contains("/geography.mountain.prominence")
				||relamid.contains("ship.length_at_waterline")){ 
			try{
//				System.out.println(v1 + "\t" + v2);
				v1 = v1.replaceAll("[a-zA-Z ]","");
				v2 = v2.replaceAll("[a-zA-Z ]","");
				double a = Double.parseDouble(v1);
				double b = Double.parseDouble(v2);
				if(Math.abs(a-b)<=1.0)
					return true;
				else
					return false;
			}catch (Exception e){
				return false;
			}
		}

		if(relamid.equals("<http://rdf.freebase.com/ns/people.person.height_meters>")){ 
			try{
//				System.out.println(v1 + "\t" + v2);
				v1 = v1.replaceAll("[a-zA-Z ]","");
				v2 = v2.replaceAll("[a-zA-Z ]","");
				double a = Double.parseDouble(v1);
				double b = Double.parseDouble(v2);
				if(Math.abs(a-b)<=0.1)
					return true;
				else
					return false;
			}catch (Exception e){
				return false;
			}
		}

		if(synonym_evalute.get(v1)!=null && synonym_evalute.get(v2)!=null){
			boolean fll = false;
			for(int a: synonym_evalute.get(v1)){
				if(synonym_evalute.get(v2).contains(a)){
					fll = true;
					break;
				}
			}
			if(fll){
				return true;
			}
		}
		return v1.equals(v2);
	}
	
	// used in TruthFinder
	public static double computeImplication(DataItemValue value1, DataItemValue value2, ValueType valueType) {
		if (PropertyFeature.savedAsString(valueType)) {
			return normLevenSimilarity(value1.getValueString(), value2.getValueString());
		} 
		return 0.0;
	}
	
	public static double jaroSimilarity(String s1, String s2) {
		JaroWinkler similarityMetric = new JaroWinkler();
		return similarityMetric.similarity(s1.toLowerCase(), s2.toLowerCase());
	}
	public static double normLevenSimilarity(String s1, String s2) {
		NormalizedLevenshtein similarityMetric = new NormalizedLevenshtein();
		return similarityMetric.similarity(s1.toLowerCase(), s2.toLowerCase());
	}
	public static boolean nameCompare(String name1,String name2) {
		name1 = name1.replaceAll("\\.",". ").replaceAll("[ ]+"," ").toLowerCase();
		name2 = name2.replaceAll("\\.",". ").replaceAll("[ ]+"," ").toLowerCase();
		List<String> l1 = Arrays.asList(name1.split(" "));
		List<String> l2 = Arrays.asList(name2.split(" "));
		return l1.containsAll(l2) && l2.containsAll(l1);
		
	}
}