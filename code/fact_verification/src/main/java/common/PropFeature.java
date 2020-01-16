package common;

import java.io.Serializable;

public enum PropFeature implements Serializable {
	SINGLE_MULTI(1),
	IS_DIGIT_VALUE(2),
	IS_DATE_VALUE(3),
	IS_DIGIT_STR_VALUE(4),
	IS_PURE_STR_VALUE(5),
	AVERAGE_VALUE_CNT(6),
	MAX_VALUE_CNT(7),
	MIN_VALUE_CNT(8);
	
	private final int index;
	
    private PropFeature(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
