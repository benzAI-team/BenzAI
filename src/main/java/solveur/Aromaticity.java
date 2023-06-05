package solveur;

import benzenoid.Benzenoid;

import java.util.Arrays;

public class Aromaticity {

	public enum RIType {
		NORMAL, OPTIMIZED
	}
	private double[] Ri;
	private final double[] optimizedRi = new double[] { 0.869, 0.246, 0.100, 0.041 };

	private Benzenoid molecule;

	private final double[][] localCircuits;
	private double[] globalCircuits;

	private double[] localAromaticity;
	private double globalAromaticity;

	private RIType type;

	public Aromaticity(Benzenoid molecule, double[][] localCircuits, RIType type) {

		initializeRi();

		this.type = type;
		this.molecule = molecule;
		this.localCircuits = localCircuits;

		computeGlobalCircuits();
		computeAromaticity();
	}

	private void initializeRi() {
		final int MAX_CYCLE_SIZE = 20;
		Ri = new double[MAX_CYCLE_SIZE];

		for (int i = 0; i < MAX_CYCLE_SIZE; i++) {
			Ri[i] = 1.0 / ((double) (i + 1) * (double) (i + 1));
		}
	}

	private void computeGlobalCircuits() {

		globalCircuits = new double[localCircuits[0].length];

		for (double[] localCircuit : localCircuits) {
			for (int j = 0; j < localCircuit.length; j++) {
				globalCircuits[j] += localCircuit[j];
			}
		}
	}

	private void computeAromaticity() {

		double[] chosenRi = null;

		switch (type) {
		case NORMAL:
			chosenRi = Ri;
			break;

		case OPTIMIZED:
			chosenRi = optimizedRi;
			break;
		}

		localAromaticity = new double[localCircuits.length];
		globalAromaticity = 0.0;

		for (int i = 0; i < localCircuits.length; i++) {
			for (int j = 0; j < localCircuits[i].length; j++) {
				localAromaticity[i] += localCircuits[i][j] * chosenRi[j];
				globalAromaticity += localCircuits[i][j] * chosenRi[j];
			}
		}

		System.out.print("");
	}

	public Benzenoid getMolecule() {
		return molecule;
	}

	public void setMolecule(Benzenoid molecule) {
		this.molecule = molecule;
	}

	public double[][] getLocalCircuits() {
		return localCircuits;
	}

	public double[] getLocalAromaticity() {
		return localAromaticity;
	}

	public double getGlobalAromaticity() {
		return globalAromaticity;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (double r : globalCircuits) {
			builder.append(r).append(" ");
		}
		return builder.toString();
	}

	public void normalize(double nbKekuleStructures) {

		if (nbKekuleStructures != 0) {
			globalAromaticity = globalAromaticity / nbKekuleStructures;
			for (int i = 0; i < localAromaticity.length; i++) {
				localAromaticity[i] = localAromaticity[i] / nbKekuleStructures;
			}
		}

		else {
			globalAromaticity = 0;
			Arrays.fill(localAromaticity, 0);
		}
	}

	public void setType(RIType type) {

		if (type != this.type) {
			this.type = type;
			computeAromaticity();
			normalize(molecule.getNbKekuleStructures());
		}
	}
}
