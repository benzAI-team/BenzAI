package solution;

import java.util.ArrayList;
import java.util.List;

public class ClarCoverSolution {

	private final int[] circles;
	private final int[][] bonds;
	private final int[] singleElectrons;

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
				builder.append(i).append(", ");
		}
		builder.append("}\n");

		builder.append("single_electrons: {");
		for (int i = 0; i < singleElectrons.length; i++)
			if (isSingle(i))
				builder.append(i).append(", ");
		builder.append("}\n");

		builder.append("double_bonds: {\n");
		for (int i = 0; i < bonds.length; i++) {
			for (int j = (i + 1); j < bonds.length; j++) {
				if (isDoubleBond(i, j))
					builder.append("\t(").append(i).append(", ").append(j).append("), \n");
			}
		}
		builder.append("}\n");

		return builder.toString();
	}

	public static double [] getRadicalarStatistics(List<ClarCoverSolution> solutions) {
		
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
