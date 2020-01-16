package algorithm.single;

import algorithm.Fusioner;
import algorithm.FusionerParameters;
import model.DataItemValue;
import model.DataSet;

public class Original extends Fusioner{

	public Original(DataSet dataSet, FusionerParameters params) {
		super(dataSet, params);
	}

	@Override
	protected int runFusioner() {
		System.out.println(this.getClass().getSimpleName() + ">>>>");
		for (DataItemValue value : dataSet.getDataItemValueMap().values()) {
			value.setConfidence(1.0);
			value.setTrueByFusioner(true);
		}
		return 1;
	}

}
