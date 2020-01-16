package model;

public class SourceClaim { 
	
	private String sourceClaimIdentifier; 
	private String dataItemIdentifier; 
	private int valueIdentifier; 
	private String sourceIdentifier; 
	private boolean trueByFusioner;
	
	public SourceClaim(String sourceClaimIdentifier, String dataItemIdentifier, int valueIdentifier,
			String sourceIdentifier) {
		super();
		this.sourceClaimIdentifier = sourceClaimIdentifier;
		this.dataItemIdentifier = dataItemIdentifier;
		this.valueIdentifier = valueIdentifier;
		this.sourceIdentifier = sourceIdentifier;
	}
	
	public String getSourceClaimIdentifier() {
		return sourceClaimIdentifier;
	}
	public String getDataItemIdentifier() {
		return dataItemIdentifier;
	}
	public int getValueIdentifier() {
		return valueIdentifier;
	}
	public String getSourceIdentifier() {
		return sourceIdentifier;
	}
	public boolean isTrueByFusioner() {
		return trueByFusioner;
	}
	public void setTrueByFusioner(boolean trueByFusioner) {
		this.trueByFusioner = trueByFusioner;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sourceClaimIdentifier == null) ? 0 : sourceClaimIdentifier.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SourceClaim other = (SourceClaim) obj;
		if (sourceClaimIdentifier == null) {
			if (other.sourceClaimIdentifier != null)
				return false;
		} else if (!sourceClaimIdentifier.equals(other.sourceClaimIdentifier))
			return false;
		return true;
	}
	
}
