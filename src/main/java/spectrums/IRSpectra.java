package spectrums;

import java.util.ArrayList;
import java.util.HashMap;

import molecules.Molecule;
import utils.Couple;

public class IRSpectra {

	/*
	 * Attributs
	 */

	private Parameter parameter;
	private String className;
	private ArrayList<Molecule> molecules;

	/*
	 * Données spectres
	 */

	private HashMap<String, Double> irregularities;
	private HashMap<String, Double> finalEnergies;
	private HashMap<String, Couple<ArrayList<Double>, ResultLogFile>> spectres;

	private ArrayList<Double> energies;

	private String moleculeEref;
	private double ErefValue;

	/*
	 * Constructeurs
	 */

	public IRSpectra(Parameter parameter, String className, ArrayList<Molecule> molecules) {
		this.parameter = parameter;
		this.className = className;
		this.molecules = molecules;
	}

	/*
	 * Getters & Setters
	 */

	public Parameter getParameter() {
		return parameter;
	}

	public void setParameter(Parameter parameter) {
		this.parameter = parameter;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public ArrayList<Molecule> getMolecules() {
		return molecules;
	}

	public void setMolecules(ArrayList<Molecule> molecules) {
		this.molecules = molecules;
	}

	public HashMap<String, Double> getIrregularities() {
		return irregularities;
	}

	public void setIrregularities(HashMap<String, Double> irregularities) {
		this.irregularities = irregularities;
	}

	public HashMap<String, Double> getFinalEnergies() {
		return finalEnergies;
	}

	public void setFinalEnergies(HashMap<String, Double> finalEnergies) {
		this.finalEnergies = finalEnergies;
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

	public void setErefValue(double erefValue) {
		ErefValue = erefValue;
	}

	public String intensitiesToString() {

		StringBuilder builder = new StringBuilder();

		builder.append(getClassName() + "\n");

		int index = 0;
		for (int V = parameter.getVMin(); V <= parameter.getVMax(); V += parameter.getStep()) {

			builder.append(V + "\t" + energies.get(index) + "\n");
			index++;
		}

		return builder.toString();
	}
}
