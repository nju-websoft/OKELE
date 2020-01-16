package algorithm.single;

import algorithm.Fusioner;
import algorithm.FusionerParameters;
import model.DataItem;
import model.DataItemValue;
import model.DataSet;

import java.util.Vector;

public class MajorityVoting extends Fusioner{
	
	public MajorityVoting(DataSet dataSet, FusionerParameters params) {
		super(dataSet, params);
	}

	@Override
	protected int runFusioner() {
		System.out.println(this.getClass().getSimpleName() + ">>>>");
		int numOfSrc;
		Vector<DataItemValue> maxVoteValue; 
		int maxsource = 0; 
		for (DataItem dataItem: dataSet.getDataItemMap().values()) {
			maxVoteValue = new Vector<DataItemValue>();
			maxsource = 0;
			for (DataItemValue value : dataItem.getValues()) {
				numOfSrc = value.getSources().size();
				if(numOfSrc > maxsource){
					maxVoteValue.clear();
					maxVoteValue.add(value);
					maxsource = numOfSrc;
				}else if(numOfSrc == maxsource){
					maxVoteValue.add(value);
				}
			}
			for(DataItemValue v:maxVoteValue) {
				v.setConfidence(1.0);
				v.setTrueByFusioner(true);
			}
		}
		return 1;
	}
}
