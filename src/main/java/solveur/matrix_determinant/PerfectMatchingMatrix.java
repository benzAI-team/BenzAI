package solveur.matrix_determinant;

import Jama.Matrix;
import benzenoid.Benzenoid;
import parsers.GraphParser;

import java.util.ArrayList;

public class PerfectMatchingMatrix {

	private final ArrayList<Integer> lines;

	private final double[][] matrix;

	public PerfectMatchingMatrix(ArrayList<Integer> lines, double[][] matrix) {
		super();
		this.lines = lines;
		this.matrix = matrix;
	}

	public ArrayList<Integer> getLines() {
		return lines;
	}

	public double[][] getMatrix() {
		return matrix;
	}

	public static PerfectMatchingMatrix buildMatrix(int[][] adjacencyMatrix, int nbNodes) {

		/*
		 * building lines/columns
		 */

		ArrayList<Integer> lines = new ArrayList<>();
		ArrayList<Integer> columns = new ArrayList<>();

		int state = 1;
		ArrayList<Integer> candidats = new ArrayList<>();
		candidats.add(0);

		lines.add(0);

		int[] checkedCarbons = new int[nbNodes];
		checkedCarbons[0] = 1;

		while (true) {
			ArrayList<Integer> newCandidats = new ArrayList<>();

			for (Integer i : candidats) {

				for (int j = 0; j < nbNodes; j++) {

					if (adjacencyMatrix[i][j] == 1 && checkedCarbons[j] == 0) {
						checkedCarbons[j] = 1;

						if (state == 0)
							lines.add(j);
						else
							columns.add(j);

						newCandidats.add(j);
					}
				}
			}

			candidats.clear();
			candidats.addAll(newCandidats);

			state = 1 - state;

			boolean end = true;
			for (Integer i : checkedCarbons)
				if (i == 0) {
					end = false;
					break;
				}

			if (end)
				break;
		}

		System.out.println(lines);
		System.out.println(columns);

		double[][] matrix = new double[lines.size()][columns.size()];

		for (int i = 0; i < lines.size(); i++) {
			for (int j = 0; j < columns.size(); j++) {

				int u = lines.get(i);
				int v = columns.get(j);

				if (adjacencyMatrix[u][v] == 1) {
					System.out.println(u + " -- " + v);
					matrix[i][j] = 1;
				}
			}
		}

		return new PerfectMatchingMatrix(columns, matrix);
	}

	public static void main(String[] args) {

		Benzenoid molecule = GraphParser
				.parseUndirectedGraph("C:\\Users\\adrie\\Desktop\\molecules_test\\coro_4.graph_coord", null, true);
		assert molecule != null;
		PerfectMatchingMatrix matrix = PerfectMatchingMatrix.buildMatrix(molecule.getEdgeMatrix(),
				molecule.getNbCarbons());

		System.out.print("[");
		for (int i = 0; i < matrix.getMatrix().length; i++) {
			for (int j = 0; j < matrix.getMatrix().length; j++) {
				System.out.print(matrix.getMatrix()[i][j]);
				if (j < matrix.getMatrix().length - 1)
					System.out.print(", ");
			}
			System.out.print("; ");
		}
		System.out.println("]");

		int n;

		long begin;
		// n = PerfectMatchingSolver.computeNbPerfectMatching(subMolecule);
		long end;
		long time;

		// System.out.print(n + " matchings (" + time + " ms.) [CHOCO]");

		Matrix m = new Matrix(matrix.getMatrix());
		begin = System.currentTimeMillis();
		double det = m.det();
		end = System.currentTimeMillis();
		time = end - begin;

		n = (int) Math.abs(det);
		System.out.print(n + " matchings (" + time + " ms.) [JAMA]");
	}
}
