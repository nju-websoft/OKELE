package model;

import java.util.Vector;

import com.alibaba.fastjson.JSONObject;

public class Claim {
	    private int id;
	    private String entitymid;
	    private String entitylable;
	    private JSONObject entityobject;  

	    private String relationmid;
	    private String relationlable;
	    private JSONObject relationobject;

	    private Vector<Value> values;

	    public Claim(int id, String entitymid, String entitylable, JSONObject entityobject, 
	    		String relationmid, String relationlable, JSONObject relationobject, Vector<Value> values) {
	        this.id = id;
	        this.entitymid = entitymid;
	        this.entitylable = entitylable;
	        this.entityobject = entityobject;
	        this.relationmid = relationmid;
	        this.relationlable = relationlable;
	        this.relationobject = relationobject;
	        this.values = values;
	    }

	    public int getId() {
	        return id;
	    }

	    public void setId(int id) {
	        this.id = id;
	    }

	    public String getEntitymid() {
	        return entitymid;
	    }

	    public void setEntitymid(String entitymid) {
	        this.entitymid = entitymid;
	    }

	    public String getEntitylable() {
	        return entitylable;
	    }

	    public void setEntitylable(String entitylable) {
	        this.entitylable = entitylable;
	    }

	    public JSONObject getEntityobject() {
	        return entityobject;
	    }

	    public void setEntityobject(JSONObject entityobject) {
	        this.entityobject = entityobject;
	    }

	    public String getRelationmid() {
	        return relationmid;
	    }

	    public void setRelationmid(String relationmid) {
	        this.relationmid = relationmid;
	    }

	    public String getRelationlable() {
	        return relationlable;
	    }

	    public void setRelationlable(String relationlable) {
	        this.relationlable = relationlable;
	    }

	    public JSONObject getRelationobject() {
	        return relationobject;
	    }

	    public void setRelationobject(JSONObject relationobject) {
	        this.relationobject = relationobject;
	    }

	    public Vector<Value> getValues() {
	        return values;
	    }

	    public void setValues(Vector<Value> values) {
	        this.values = values;
	    }

}
