package spectrums;

public class Parameter {

	private int VMin;
	private int VMax;
	private int step;

	private int FWHM;
	private double ZT;
	private double gi;
	private double N;
	private double T;
	private double kb;

	/*
	 * Getters & Setters
	 */

	public int getVMin() {
		return VMin;
	}

	public void setVMin(int vMin) {
		VMin = vMin;
	}

	public int getVMax() {
		return VMax;
	}

	public void setVMax(int VMax) {
		this.VMax = VMax;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public int getFWHM() {
		return FWHM;
	}

	public void setFWHM(int fWHM) {
		FWHM = fWHM;
	}

	public double getZT() {
		return ZT;
	}

	public void setZT(double zT) {
		ZT = zT;
	}

	public double getGi() {
		return gi;
	}

	public void setGi(double gi) {
		this.gi = gi;
	}

	public double getN() {
		return N;
	}

	public void setN(double n) {
		N = n;
	}

	public double getT() {
		return T;
	}

	public void setT(double t) {
		T = t;
	}

	public double getKb() {
		return kb;
	}

	public void setKb(double kb) {
		this.kb = kb;
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();

		builder.append("VMin" + " " + VMin + "\n");
		builder.append("VMax" + " " + VMax + "\n");
		builder.append("step" + " " + step + "\n");
		builder.append("FWHM" + " " + FWHM + "\n");
		builder.append("ZT" + " " + ZT + "\n");
		builder.append("gi" + " " + gi + "\n");
		builder.append("N" + " " + N + "\n");
		builder.append("T" + " " + T + "\n");
		builder.append("Kb" + " " + kb + "\n");

		return builder.toString();
	}

	public static Parameter defaultParameter() {
		Parameter parameter = new Parameter();

		parameter.setVMin(600);
		parameter.setVMax(1700);
		parameter.setStep(1);
		parameter.setFWHM(30);
		parameter.setZT(1.0);
		parameter.setGi(1.0);
		parameter.setN(100.0);
		parameter.setT(100.0);
		parameter.setKb(1.380649E-23);

		return parameter;
	}
}
