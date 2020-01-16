package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import common.Constants;
import model.Claim;
import model.Truth;
import model.Value;
import model.PropertyFeature.ValueType;
import util.DataCleaner;
import util.MethodUtil;

public class KnowledgeDataLoader extends DataLoader{
	
	private Vector<Claim> claims;
	private FeatureExtractor featureExtractor;

	private String priorClaimDir;
	private String priortruthFile;

	private Vector<Claim> prior_claims;
	private HashMap<String,Truth> prior_truths;
	
	private static String PROPERTY_FEATURE_LOOSE_FILE;
	
	public static void setFeatureFile(String domain,String type) {
		PROPERTY_FEATURE_LOOSE_FILE = Constants.POPULAR_FILE_PATH + domain +  "/" + type + ".property.value.features.txt";
	}
	
	public KnowledgeDataLoader(String claimDir,String truthFile) throws IOException {
		super(claimDir,truthFile);
		this.featureExtractor = new FeatureExtractor(PROPERTY_FEATURE_LOOSE_FILE);
		this.claims = before_handle(this.claimDir);
		this.truths = load_truth(this.truthFile);
	}

    public KnowledgeDataLoader(String claimDir,String truthFile,String priorClaimDir, String priortruthFile) throws IOException {
        super(claimDir,truthFile);
        this.priorClaimDir = priorClaimDir;
        this.priortruthFile = priortruthFile;
        this.featureExtractor = new FeatureExtractor(PROPERTY_FEATURE_LOOSE_FILE);
        this.claims = before_handle(this.claimDir);
        this.prior_claims = before_handle(this.priorClaimDir);

        this.truths = load_truth(this.truthFile);
        this.prior_truths = load_truth(this.priortruthFile);
    }
	
	public Vector<Claim> getClaims() {
		return claims;
	}

    public Vector<Claim> getPrior_claims() {
        return prior_claims;
    }

    public HashMap<String, Truth> getPrior_truths() {
        return prior_truths;
    }

    public FeatureExtractor getFeatureExtractor() {
		return featureExtractor;
	}

	public Vector<Claim> before_handle(String claimDir){
        int id=0;
        Vector<Claim> results = new Vector<Claim>();
        try{
            BufferedReader br = new BufferedReader(new FileReader(claimDir));
            String line = null;
            int i = 0;
            List<String> valueLists = new ArrayList<String>();
            while((line=br.readLine())!=null){
                JSONObject jsonObject = JSONObject.parseObject(line);
                JSONObject entity = jsonObject.getJSONObject("entity");
                JSONObject relation = jsonObject.getJSONObject("relation");
             //   System.out.println(relation.toString());
                JSONArray values = jsonObject.getJSONArray("values");
                String propertyName = relation.getString("mid");
             //   System.out.println(propertyName);
                ValueType valueType = featureExtractor.getPropertyDataType(propertyName);
                
                Vector<Value> values1 = new Vector<Value>();
                for(Object value:values){
                	i++;
                    JSONObject jsonObject1 = (JSONObject) value;
                    String lable = jsonObject1.getString("value");
                    valueLists.add("clean before: " + lable);
                    if(!Executor.isTail)
                    	lable = DataCleaner.clean(lable,valueType);  
                    jsonObject1.put("value",lable);
                    valueLists.add("clean after: " + lable);
                    Vector<String> source = new Vector<String>();
                    for(String s:jsonObject1.getString("source").replace("wikitable\t","wikitable").split("\t")){
                    	source.add(s);
                    }
                    Vector<String> extrac = new Vector<String>();
                    for(String s:jsonObject1.getString("extract_type").split("\t")){
                        extrac.add(s);
                    }
                    if(source.size() != extrac.size()) {
                    	System.out.println(i + "\t" + source.size() + "\t" + extrac.size());
                    }
                    Value temp = new Value(lable,source,extrac,jsonObject1,jsonObject1.getString("relation"));
                    values1.add(temp);
                }
                Claim temp_instance = new Claim(id,entity.getString("mid"),entity.getString("lable"),entity,
                        relation.getString("mid"),relation.getString("lable"),relation,values1);
                results.add(temp_instance);
                id++;
            }
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return results;
    }
	public HashMap<String,Truth> load_truth(String truthFile) throws IOException{
		HashMap<String,Truth> truths = new HashMap<String,Truth>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(truthFile)));
		String line;
		while((line=br.readLine())!=null){
		    if(line.split("\t").length < 4) 
		        continue;
              String entity = MethodUtil.strip(line.split("\t")[0]);
              String relation = MethodUtil.strip(line.split("\t")[2]);
              String value = line.split("\t")[3];
//              if(value.contains("("))
//                  System.out.println(value);
              if(!Executor.isTail) {
            	  value = value.replaceAll("\\(.*\\)", "").trim();
            	  if(value.contains("^^"))
            		  value = value.substring(0,value.indexOf("^^"));
              }
              String dataItem = entity + Constants.dataItemIdentifierDelimiter + relation;
              if(truths.containsKey(dataItem)){
                  Truth t = truths.get(dataItem);
                  t.addTrueValue(value);
              }else{
            	  Truth t = new Truth();
            	  t.addTrueValue(value);
            	  truths.put(dataItem, t);
              }
          }
		br.close();
		return truths;
	}
    public static void after_handle(Vector<Claim> instances,String file){
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(file)));
            int i = 0,j=0,k=0;
            for (Claim temp : instances) {
            	i++;
         //   	System.out.println(temp.getValues().size());
            	if(!temp.getValues().isEmpty()) {
            	j++;
                JSONObject line = new JSONObject();
                line.put("entity", temp.getEntityobject());
                line.put("relation", temp.getRelationobject());
                JSONArray jsonArray = new JSONArray();
                for (Value t : temp.getValues()) {
                	if(!t.flag)
                		continue;
                    JSONObject jo = t.getValueobject();
                    jo.put("flag_valid", t.flag);
                    jsonArray.add(jo);
                    k++;
                }
                line.put("values", jsonArray);
                bw.write(line+"\n");
            	}
            }
            bw.close();
            System.out.println("after_handle: " + i);
            System.out.println("after_handle: " + j);
            System.out.println("after_handle:(the number of true values after validation) " + k);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
