package model;

import java.util.HashMap;
import java.util.Map;

import main.DataLoader;

public class DataSet {
	private String name;
	private boolean onlyMaxValueIsTrue;
	private Map<String,SourceClaim> sourceClaimMap;
	private Map<String,Source> sourceMap;
	private Map<String,DataItem> dataItemMap; 
	private Map<Integer,DataItemValue> dataItemValueMap;
	private DataLoader dataLoader;
	
	public DataSet(String name,boolean onlyMaxValueIsTrue) {
		super();
		this.name = name;
		this.onlyMaxValueIsTrue = onlyMaxValueIsTrue;
		this.sourceClaimMap = new HashMap<String,SourceClaim>();
		this.sourceMap =new HashMap<String,Source>();
		this.dataItemMap = new HashMap<String,DataItem>();
		this.dataItemValueMap = new HashMap<Integer,DataItemValue>();
	}

	public String getName() {
		return name;
	}

	public boolean isOnlyMaxValueIsTrue() {
		return onlyMaxValueIsTrue;
	}
	public Map<String, SourceClaim> getSourceClaimMap() {
		return sourceClaimMap;
	}
	public Map<String, Source> getSourceMap() {
		return sourceMap;
	}
	public Map<String, DataItem> getDataItemMap() {
		return dataItemMap;
	}
	
	public Map<Integer, DataItemValue> getDataItemValueMap() {
		return dataItemValueMap;
	}

	public SourceClaim getSourceClaim(String sourceClaimIdentifier) {
		return this.sourceClaimMap.get(sourceClaimIdentifier);
	}
	public Source getSource(String sourceIdentifier) {
		return this.sourceMap.get(sourceIdentifier);
	}
	public DataItem getDataItem(String dataItemIdentifier) {
		return this.dataItemMap.get(dataItemIdentifier);
	}
	public DataItemValue getDataItemValue(int valueIdentifier) {
		return this.dataItemValueMap.get(valueIdentifier);
	}
	public void addSourceClaim(SourceClaim sourceClaim) {
		this.sourceClaimMap.put(sourceClaim.getSourceClaimIdentifier(),sourceClaim);
	}
	public void addSource(Source source) {
		this.sourceMap.put(source.getSourceIdentifier(), source);
	}
	public void addDataItem(DataItem dataItem) {
		this.dataItemMap.put(dataItem.getDataItemIdentifier(), dataItem);
	}
	public void addDataItemValue(DataItemValue dataItemValue) {
		this.dataItemValueMap.put(dataItemValue.getValueIdentifier(), dataItemValue);
	}
	public DataLoader getDataLoader() {
		return dataLoader;
	}
	public void setDataLoader(DataLoader dataLoader) {
		this.dataLoader = dataLoader;
	}
	public void resetDataSet(double sourceTrustworthiness, double claimConfidence) {
		for (Source source : this.sourceMap.values()) {
			source.setTrustworthiness(sourceTrustworthiness);
			source.setOldTrustworthiness(sourceTrustworthiness);
		}
		for (DataItemValue value: this.dataItemValueMap.values()) {
			value.setTrueByFusioner(false);
			value.setConfidence(claimConfidence);
		}
	}
	public void resetDataSet() {
		for (DataItemValue value: this.dataItemValueMap.values()) {
			value.setTrueByFusioner(false);
		}
		for(SourceClaim claim:this.sourceClaimMap.values()){
			claim.setTrueByFusioner(false);
		}
	}
	
	public void setOnlyMaxValueIsTrue(boolean b) {
		this.onlyMaxValueIsTrue = b;
	}
	
	@Override
	public String toString() {
		return "DataSet [single Truth=" + this.onlyMaxValueIsTrue
				+ ",Sources size=" + sourceMap.size() + ", dataItems size=" + dataItemMap.size()
				+", dataItemValues size=" + dataItemValueMap.size() + ", sourceClaims size=" + sourceClaimMap.size()
				+ "]";
	}
}
