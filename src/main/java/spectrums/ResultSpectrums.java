package spectrums;

import java.util.ArrayList;
import java.util.HashMap;

import classifier.PAHClass;
import utils.Couple;

public class ResultSpectrums {

	private final Parameter parameter;
	private final PAHClass PAHClass;

	private HashMap<String, Double> irregularities;
	private HashMap<String, Double> finalEnergies;
	private HashMap<String, Couple<ArrayList<Double>, ResultLogFile>> spectres;

	private ArrayList<Double> energies;

	private String moleculeEref;
	private double ErefValue;

	public ResultSpectrums(PAHClass PAHClass, Parameter parameter) {
		this.parameter = parameter;
		this.PAHClass = PAHClass;
	}

	/*
	 * Getters & setters
	 */

	public Parameter getParameter() {
		return parameter;
	}

	public PAHClass getPAHClass() {
		return PAHClass;
	}

	public HashMap<String, Couple<ArrayList<Double>, ResultLogFile>> getSpectres() {
		return spectres;
	}

	public void setSpectres(HashMap<String, Couple<ArrayList<Double>, ResultLogFile>> spectres) {
		this.spectres = spectres;
	}

	public ArrayList<Double> getEnergies() {
		return energies;
	}

	public void setEnergies(ArrayList<Double> energies) {
		this.energies = energies;
	}

	public String getMoleculeEref() {
		return moleculeEref;
	}

	public void setMoleculeEref(String moleculeEref) {
		this.moleculeEref = moleculeEref;
	}

	public double getErefValue() {
		return ErefValue;
	}

	public void setErefValue(double ErefValue) {
		this.ErefValue = ErefValue;
	}

	public String intensitiesToString() {

		StringBuilder builder = new StringBuilder();

		builder.append(PAHClass.getTitle() + "\n");

		int index = 0;
		for (int V = parameter.getVMin(); V <= parameter.getVMax(); V += parameter.getStep()) {

			builder.append(V + "\t" + energies.get(index) + "\n");
			index++;
		}

		return builder.toString();
	}

	public HashMap<String, Double> getFinalEnergies() {
		return finalEnergies;
	}

	public void setFinalEnergies(HashMap<String, Double> finalEnergies) {
		this.finalEnergies = finalEnergies;
	}

	public HashMap<String, Double> getIrregularities() {
		return irregularities;
	}

	public void setIrregularities(HashMap<String, Double> irregularities) {
		this.irregularities = irregularities;
	}
}
