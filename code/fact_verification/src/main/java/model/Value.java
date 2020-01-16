package model;

import java.util.Vector;

import com.alibaba.fastjson.JSONObject;

public class Value {
    private String lable;
    private Vector<String> source;
    private Vector<String> extract_type;
    private String relation;
    private JSONObject valueobject;
    public boolean flag = false;

    public Value(String lable, Vector<String> source, Vector<String> extract_type, JSONObject valueobject,String relation) {
        this.lable = lable;
        this.source = source;
        this.valueobject = valueobject;
        this.extract_type = extract_type;
        this.relation = relation;
    }

    public Vector<String> getExtract_type() {
        return extract_type;
    }

    public void setExtract_type(Vector<String> extract_type) {
        this.extract_type = extract_type;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public Vector<String> getSource() {
        return source;
    }

    public void setSource(Vector<String> source) {
        this.source = source;
    }

    public JSONObject getValueobject() {
        return valueobject;
    }

    public void setValueobject(JSONObject valueobject) {
        this.valueobject = valueobject;
    }

    public String getLable() {
        return lable;
    }

    public void setLable(String lable) {
        this.lable = lable;
    }

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}
    
}
