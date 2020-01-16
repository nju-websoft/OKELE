package algorithm.mult;

public class LTMSourceData {

	/**
	 * truth vs observation
	 * false - false 
	 */
	int n00;
	/**
	 * truth vs observation
	 * false - true
	 */
	int n01;
	/**
	 * truth vs observation
	 * true - false
	 */
	int n10;
	/**
	 * truth vs observation
	 * true - true
	 */
	int n11;
	
	double e00;
	double e01;
	double e10;
	double e11;
	
	public LTMSourceData() {
		
		n00 = 0;
		n01 = 0;
		n10 = 0;
		n11 = 0;
		
		e00 = 0;
		e01 = 0;
		e10 = 0;
		e11 = 0;
		
	}
	public void incrementN00() {
		n00++;
	}
	public void incrementN01() {
		n01++;
	}
	public void incrementN10() {
		n10++;
	}
	public void incrementN11() {
		n11++;
	}
	public void decrementN00() {
		n00--;
	}
	public void decrementN01() {
		n01--;
	}
	public void decrementN10() {
		n10--;
	}
	public void decrementN11() {
		n11--;
	}
	public int getN00() {
		return n00;
	}
	public int getN01() {
		return n01;
	}
	public int getN10() {
		return n10;
	}
	public int getN11() {
		return n11;
	}
	public double getE00() {
		return e00;
	}
	public double getE01() {
		return e01;
	}
	public double getE10() {
		return e10;
	}
	public double getE11() {
		return e11;
	}
	public void setE00(double value) {
		e00 = value;
	}
	public void setE01(double value) {
		e01 = value;
	}
	public void setE10(double value) {
		e10 = value;
	}
	public void setE11(double value) {
		e11 = value;
	}
	@Override
	public String toString() {
		return "LTMSourceData [q00=" + e00 + ", q01=" + e01 + ", q10=" + e10 + ", q11=" + e11 + ", s=" + e01/(e01+e00)
				+ ", r=" + e11/(e11+e10) + "]";
	}
}
