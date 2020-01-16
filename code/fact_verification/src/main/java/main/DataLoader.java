package main;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import model.Claim;
import model.Truth;

public class DataLoader {
	protected String claimDir;
	protected String truthFile;
	protected HashMap<String,Truth> truths;
	
	public DataLoader(String claimDir) {
		super();
		this.claimDir = claimDir;
	}
	public DataLoader(String claimDir, String truthFile) {
		super();
		this.claimDir = claimDir;
		this.truthFile = truthFile;
	}

	public Map<String, Truth> getTruths() {
		return truths;
	}

	public void setTruths(HashMap<String, Truth> truths) {
		this.truths = truths;
	}
	public Vector<Claim> getClaims() {
		return null;
	}
}
