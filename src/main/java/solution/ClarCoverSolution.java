package solution;

import java.util.ArrayList;

public class ClarCoverSolution {

	private int[] circles;
	private int[][] bonds;
	private int[] singleElectrons;

	/*
	 * Constructors
	 */

	public ClarCoverSolution(int[] circles, int[][] bonds, int[] singleElectrons) {
		this.circles = circles;
		this.bonds = bonds;
		this.singleElectrons = singleElectrons;
	}

	/*
	 * Getters & Setters
	 */

	public int[] getCircles() {
		return circles;
	}

	public int[][] getBonds() {
		return bonds;
	}

	public int[] getSingleElectrons() {
		return singleElectrons;
	}

	public int getNbCarbons() {
		return singleElectrons.length;
	}

	public int getNbHexagons() {
		return circles.length;
	}

	/*
	 * Class methods
	 */

	public boolean isCircle(int i) {
		return circles[i] == 1;
	}

	public boolean isDoubleBond(int i, int j) {
		return bonds[i][j] == 1;
	}

	public boolean isSingle(int i) {
		return singleElectrons[i] == 1;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("circles: {");
		for (int i = 0; i < circles.length; i++) {
			if (isCircle(i))
				builder.append(i + ", ");
		}
		builder.append("}\n");

		builder.append("single_electrons: {");
		for (int i = 0; i < singleElectrons.length; i++)
			if (isSingle(i))
				builder.append(i + ", ");
		builder.append("}\n");

		builder.append("double_bonds: {\n");
		for (int i = 0; i < bonds.length; i++) {
			for (int j = (i + 1); j < bonds.length; j++) {
				if (isDoubleBond(i, j))
					builder.append("\t(" + i + ", " + j + "), \n");
			}
		}
		builder.append("}\n");

		return builder.toString();
	}

	public static double [] getRadicalarStatistics(ArrayList<ClarCoverSolution> solutions) {
		
		if (solutions.size() == 0)
			return null;
		
		int nbCarbons = solutions.get(0).getNbCarbons();
		
		double [] radicalarStatistics = new double[nbCarbons];
		
		for (int i = 0 ; i < nbCarbons ; i ++) {
			double avg = 0.0;
			for (ClarCoverSolution solution : solutions) {
				avg += solution.getSingleElectrons()[i];
			}
			avg = avg / (double) solutions.size();
			radicalarStatistics[i] = avg;
		}
		
		return radicalarStatistics;	
	}
}
