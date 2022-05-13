package utils;

import java.util.ArrayList;

import classifier.Irregularity;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import molecules.Molecule;
import solveur.LinAlgorithm;

public class Utils {

	public static Integer [] toArray(ArrayList<Integer> list) {
		Integer [] array = new Integer[list.size()];
		
		for (int i = 0 ; i < list.size() ; i++)
			array[i] = list.get(i);
		
		return array;
	}
	
	public static void showAlertWithoutHeaderText(String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information");

		// Header Text: null
		alert.setHeaderText(null);
		alert.setContentText(message);

		alert.showAndWait();
	}

	public static boolean isNumber(String string) {

		try {
			Double.parseDouble(string);
		}

		catch (NumberFormatException e) {
			return false;
		}

		return true;
	}

	public static Irregularity computeParameterOfIrregularity(Molecule molecule) {

		if (molecule.getNbHexagons() == 1)
			return null;

		int[] N = new int[4];
		int[] checkedNodes = new int[molecule.getNbNodes()];

		ArrayList<Integer> V = new ArrayList<Integer>();

		for (int u = 0; u < molecule.getNbNodes(); u++) {
			int degree = molecule.degree(u);
			if (degree == 2 && !V.contains(u)) {
				V.add(u);
				checkedNodes[u] = 0;
			}

			else if (degree != 2)
				checkedNodes[u] = -1;
		}

		ArrayList<Integer> candidats = new ArrayList<Integer>();

		while (true) {

			int firstVertice = -1;
			for (Integer u : V) {
				if (checkedNodes[u] == 0) {
					firstVertice = u;
					break;
				}
			}

			if (firstVertice == -1)
				break;

			candidats.add(firstVertice);
			checkedNodes[firstVertice] = 1;

			int nbNeighbors = 1;

			while (candidats.size() > 0) {

				int candidat = candidats.get(0);

				for (int i = 0; i < molecule.getNbNodes(); i++) {
					if (molecule.getAdjacencyMatrix()[candidat][i] == 1 && checkedNodes[i] == 0) {

						checkedNodes[i] = 1;
						nbNeighbors++;
						candidats.add(i);
					}
				}

				candidats.remove(candidats.get(0));
			}

			N[nbNeighbors - 1] += nbNeighbors;
		}

		double XI = ((double) N[2] + (double) N[3]) / ((double) N[0] + (double) N[1] + (double) N[2] + (double) N[3]);
		return new Irregularity(N, XI);
	}

	public static void alert(String message) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Error");
		alert.setHeaderText("");
		alert.setContentText(message);
		alert.showAndWait();
	}

	public static boolean onWindows() {

		String[] property = System.getProperty("os.name").split(" ");

		if (contains(property, "Windows"))
			return true;

		return false;
	}

	public static boolean contains(Integer[] T, Integer v) {
		for (Integer s : T)
			if (s.equals(v))
				return true;

		return false;
	}

	public static boolean contains(int[] T, int v) {
		for (Integer s : T)
			if (s.equals(v))
				return true;

		return false;
	}

	public static boolean contains(String[] T, String v) {
		for (String s : T)
			if (s.equals(v))
				return true;

		return false;
	}

	public static boolean areDisjoint(ArrayList<Integer> set1, ArrayList<Integer> set2) {

		boolean disjoint = true;

		for (Integer i : set1) {
			if (set2.contains(i)) {
				disjoint = false;
				break;
			}
		}

		return disjoint;
	}

	public static ArrayList<Integer> intersection(ArrayList<Integer> set1, ArrayList<Integer> set2) {

		ArrayList<Integer> intersection = new ArrayList<>();

		for (Integer i : set1) {
			if (set2.contains(i))
				intersection.add(i);
		}

		return intersection;
	}

	public static void displayMatrix(int[][] M) {

		for (int i = 0; i < M.length; i++) {
			for (int j = 0; j < M[i].length; j++) {
				System.out.print(M[i][j] + "\t");
			}
			System.out.println("");
		}
	}

	public static int getHexagonID(int x, int y, int diameter) {
		return x + y * diameter;
	}

	public static Couple<Integer, Integer> getHexagonCoords(int hexagon, int diameter) {

		for (int x = 0; x < diameter; x++) {
			for (int y = 0; y < diameter; y++) {

				if (getHexagonID(x, y, diameter) == hexagon)
					return new Couple<Integer, Integer>(x, y);
			}
		}

		return null;
	}

	/*
	 * Checking size for intervals sets
	 */

	public static boolean checkSize(ArrayList<Interval> intervals, int s1) {
		if (intervals.size() == 1) {
			if (intervals.get(0).size() == s1)
				return true;
		}
		return false;
	}

	public static boolean checkSize(ArrayList<Interval> intervals, int s1, int s2) {
		if (intervals.size() == 2) {
			if (intervals.get(0).size() == s1 && intervals.get(1).size() == s2)
				return true;
		}
		return false;
	}

	public static boolean checkSize(ArrayList<Interval> intervals, int s1, int s2, int s3) {
		if (intervals.size() == 3) {
			if (intervals.get(0).size() == s1 && intervals.get(1).size() == s2 && intervals.get(2).size() == s3)
				return true;
		}
		return false;
	}

	public static boolean checkSize(ArrayList<Interval> intervals, int s1, int s2, int s3, int s4) {
		if (intervals.size() == 4) {
			if (intervals.get(0).size() == s1 && intervals.get(1).size() == s2 && intervals.get(2).size() == s3
					&& intervals.get(3).size() == s4)
				return true;
		}
		return false;
	}

	public static boolean checkSize(ArrayList<Interval> intervals, int s1, int s2, int s3, int s4, int s5) {
		if (intervals.size() == 5) {
			if (intervals.get(0).size() == s1 && intervals.get(1).size() == s2 && intervals.get(2).size() == s3
					&& intervals.get(3).size() == s4 && intervals.get(4).size() == s5)
				return true;
		}
		return false;
	}

	/*
	 * Identify a cycle
	 */

	@SuppressWarnings("unused")
	public static int identifyDependantCycle(Molecule molecule, ArrayList<Interval> intervals) {

		Interval i0 = null;
		Interval i1 = null;
		Interval i2 = null;
		Interval i3 = null;
		Interval i4 = null;

		for (int i = 0; i < intervals.size(); i++) {
			if (i == 0)
				i0 = intervals.get(i);
			if (i == 1)
				i1 = intervals.get(i);
			if (i == 2)
				i2 = intervals.get(i);
			if (i == 3)
				i3 = intervals.get(i);
			if (i == 4)
				i4 = intervals.get(i);
		}

		if (checkSize(intervals, 2, 4, 2)) {

			if (i0.x1() == i1.x1() - 1 && i1.x2() == i2.x2() - 1)
				return 133;

			else if (i0.x2() == i1.x2() + 1 && i1.x1() == i2.x1() + 1)
				return 134;
		}

		if (checkSize(intervals, 4, 4)) {

			if (i0.x1() == i1.x1() - 3)
				return 135;

			else if (i0.x2() == i1.x2() + 3)
				return 136;
		}

		if (checkSize(intervals, 4, 2, 2) && LinAlgorithm.intervalsOnSameLine(i1, i2)) {

			if (i0.x1() == i1.x1() + 1 && i0.x2() == i2.x2() - 1)
				return 137;

		}

		if (checkSize(intervals, 2, 2, 4)) {

			if (i0.x1() == i2.x1() - 1 && i1.x2() == i2.x2() + 1)
				return 138;
		}

		return -1;
	}

	public static int identifyCycle(Molecule molecule, ArrayList<Interval> intervals) {

		Interval i0 = null;
		Interval i1 = null;
		Interval i2 = null;
		Interval i3 = null;
		Interval i4 = null;

		for (int i = 0; i < intervals.size(); i++) {
			if (i == 0)
				i0 = intervals.get(i);
			if (i == 1)
				i1 = intervals.get(i);
			if (i == 2)
				i2 = intervals.get(i);
			if (i == 3)
				i3 = intervals.get(i);
			if (i == 4)
				i4 = intervals.get(i);
		}

		/*
		 * 0
		 */

		if (checkSize(intervals, 2))
			return 0;

		/*
		 * 1
		 */

		if (checkSize(intervals, 4))
			return 1;

		if (checkSize(intervals, 2, 2)) {

			if (i0.x1() == i1.x1() - 1)
				return 2;

			if (i0.x2() == i1.x2() + 1)
				return 3;
		}

		/*
		 * 2
		 */

		if (checkSize(intervals, 6))
			return 4;

		if (checkSize(intervals, 2, 2, 2)) {

			if (i0.x1() == i1.x1() + 1 && i1.x1() == i2.x1() + 1)
				return 5;

			if (i0.x2() == i1.x2() - 1 && i1.x2() == i2.x2() - 1)
				return 6;
		}

		/*
		 * 3
		 */

		if (checkSize(intervals, 4, 2)) {

			if (i0.x2() == i1.x2() - 1)
				return 7;

			if (i0.x1() == i1.x1() + 1)
				return 8;
		}

		if (checkSize(intervals, 2, 4)) {

			if (i0.x2() == i1.x2() + 1)
				return 9;

			if (i0.x1() == i1.x1() - 1)
				return 10;
		}

		if (checkSize(intervals, 2, 2, 2)) {

			if (i0.x1() == i2.x1() && i1.x1() == i0.x1() - 1)
				return 11;

			if (i0.x2() == i2.x2() && i1.x2() == i0.x2() + 1)
				return 12;
		}

		/*
		 * 4
		 */

		if (checkSize(intervals, 4, 4)) {

			if (i0.x1() == i1.x1() + 1)
				return 13;

			if (i0.x2() == i1.x2() - 1)
				return 14;
		}

		if (checkSize(intervals, 2, 4, 2)) {

			if (i0.x1() == i2.x1() && i1.x1() == i0.x1() - 1)
				return 15;
		}

		/*
		 * 5
		 */

		if (checkSize(intervals, 8))
			return 16;

		if (checkSize(intervals, 2, 2, 2, 2)) {

			if (i0.x1() == i1.x1() - 1 && i1.x1() == i2.x1() - 1 && i2.x1() == i3.x1() - 1)
				return 17;

			if (i0.x2() == i1.x2() + 1 && i1.x2() == i2.x2() + 1 && i2.x2() == i3.x2() + 1)
				return 18;
		}

		/*
		 * 6
		 */

		if (checkSize(intervals, 2, 6)) {

			if (i0.x2() == i1.x2() + 1)
				return 19;

			if (i0.x1() == i1.x1() - 1)
				return 20;

		}

		if (checkSize(intervals, 6, 2)) {

			if (i0.x2() == i1.x2() - 1)
				return 21;

			if (i0.x1() == i1.x1() + 1)
				return 22;
		}

		if (checkSize(intervals, 2, 2, 2, 2)) {

			if (i0.x1() == i1.x1() - 1 && i1.x1() == i2.x1() - 1 && i1.x1() == i3.x1())
				return 23;

			if (i0.x2() == i1.x2() + 1 && i1.x2() == i2.x2() + 1 && i1.x2() == i3.x2())
				return 24;

			if (i0.x1() == i2.x1() && i1.x1() == i0.x1() + 1 && i3.x1() == i2.x1() - 1)
				return 25;

			if (i0.x2() == i2.x2() && i1.x2() == i0.x2() - 1 && i3.x2() == i2.x2() + 1)
				return 26;
		}

		if (checkSize(intervals, 4, 2, 2)) {

			if (i0.x2() == i1.x2() - 1 && i1.x2() == i2.x2() - 1)
				return 27;

			if (i0.x1() == i1.x1() + 1 && i1.x1() == i2.x1() + 1)
				return 28;
		}

		if (checkSize(intervals, 2, 2, 4)) {

			if (i0.x2() == i1.x2() + 1 && i1.x2() == i2.x2() + 1)
				return 29;

			if (i0.x1() == i1.x1() - 1 && i1.x1() == i2.x1() - 1)
				return 30;

		}

		/*
		 * 7
		 */

		if (checkSize(intervals, 2, 4, 2)) {

			if (i0.x1() == i2.x1() && i1.x1() == i0.x1() + 1)
				return 31;

			if (i0.x2() == i2.x2() && i1.x2() == i0.x2() - 1)
				return 32;
		}

		/*
		 * 8
		 */

		if (checkSize(intervals, 4, 4, 2)) {

			if (i0.x2() == i2.x2() && i1.x2() == i0.x2() - 1)
				return 33;

			if (i0.x1() == i2.x1() && i1.x1() == i0.x1() + 1)
				return 34;
		}

		if (checkSize(intervals, 2, 4, 4)) {

			if (i0.x1() == i2.x1() && i1.x1() == i0.x1() + 1)
				return 35;

			if (i0.x2() == i2.x2() && i1.x2() == i0.x2() - 1)
				return 36;
		}

		if (checkSize(intervals, 2, 6, 2)) {

			if (i0.x1() == i2.x1() && i1.x1() == i0.x1() - 1)
				return 37;

			if (i0.x2() == i2.x2() && i1.x2() == i0.x2() + 1)
				return 38;
		}

		/*
		 * 9
		 */

		if (checkSize(intervals, 4, 6)) {

			if (i0.x2() == i1.x2() + 1)
				return 39;

			if (i0.x1() == i1.x1() - 1)
				return 40;
		}

		if (checkSize(intervals, 6, 4)) {

			if (i0.x2() == i1.x2() - 1)
				return 41;

			if (i0.x1() == i1.x1() + 1)
				return 42;
		}

		if (checkSize(intervals, 2, 2, 4, 2)) {

			if (i0.x2() == i2.x2() && i1.x2() == i3.x2() && i1.x2() == i0.x2() - 1)
				return 43;

			if (i0.x1() == i2.x1() && i1.x1() == i3.x1() && i1.x1() == i0.x1() + 1)
				return 44;
		}

		if (checkSize(intervals, 2, 4, 2, 2)) {

			if (i0.x2() == i2.x2() && i1.x2() == i3.x2() && i1.x2() == i0.x2() + 1)
				return 45;

			if (i0.x1() == i2.x1() && i1.x1() == i3.x1() && i1.x1() == i0.x1() - 1)
				return 46;
		}

		if (checkSize(intervals, 2, 4, 4)) {

			if (i0.x2() == i1.x2() + 1 && i1.x2() == i2.x2() + 1)
				return 47;

			if (i0.x1() == i1.x1() - 1 && i1.x1() == i2.x1() - 1)
				return 48;
		}

		if (checkSize(intervals, 4, 4, 2)) {

			if (i0.x1() == i1.x1() + 1 && i1.x1() == i2.x1() + 1)
				return 49;

			if (i0.x2() == i1.x2() - 1 && i1.x2() == i2.x2() - 1)
				return 50;
		}

		/*
		 * 10
		 */

		if (checkSize(intervals, 4, 4, 4)) {

			if (i0.x2() == i2.x2() && i1.x2() == i0.x2() - 1)
				return 51;

			if (i0.x1() == i2.x1() && i1.x1() == i0.x1() + 1)
				return 52;
		}

		if (checkSize(intervals, 2, 6, 4)) {

			if (i0.x1() == i2.x1() && i1.x1() == i0.x1() - 1)
				return 53;

			if (i0.x2() == i2.x2() && i1.x2() == i0.x2() + 1)
				return 54;
		}

		if (checkSize(intervals, 4, 6, 2)) {

			if (i0.x2() == i2.x2() && i1.x2() == i0.x2() + 1)
				return 55;

			if (i0.x1() == i2.x1() && i1.x1() == i0.x1() - 1)
				return 56;
		}

		/*
		 * 12
		 */

		if (checkSize(intervals, 4, 6, 4)) {

			if (i0.x1() == i2.x1() && i1.x1() == i0.x1() - 1)
				return 57;
		}

		/*
		 * 15
		 */

		if (checkSize(intervals, 6, 6)) {

			if (i0.x1() == i1.x1() + 1)
				return 58;

			if (i0.x2() == i1.x2() - 1)
				return 59;
		}

		if (checkSize(intervals, 4, 4, 4)) {

			if (i0.x1() == i1.x1() - 1 && i1.x1() == i2.x1() - 1)
				return 60;

			if (i0.x2() == i1.x2() + 1 && i1.x2() == i2.x2() + 1)
				return 61;
		}

		if (checkSize(intervals, 2, 4, 4, 2)) {

			if (i0.x1() == i2.x1() && i1.x2() == i3.x2() && i1.x1() == i0.x1() - 1)
				return 62;

			if (i0.x2() == i2.x2() && i1.x1() == i3.x1() && i1.x2() == i0.x2() + 1)
				return 63;
		}

		/*
		 * Redundant circuits
		 */

		/*
		 * 16
		 */

		if (checkSize(intervals, 4, 8, 4)) {

			if (i0.x1() == i2.x1() && i1.x1() == i0.x1() - 1)
				return 64;

			if (i0.x2() == i2.x2() && i1.x2() == i0.x2() + 1)
				return 65;
		}

		/*
		 * 17
		 */

		if (checkSize(intervals, 2, 4, 6, 4)) {

			if (i0.x2() == i2.x2() && i1.x2() == i3.x2() && i1.x2() == i0.x2() - 1)
				return 66;

			if (i0.x1() == i2.x1() && i1.x1() == i3.x1() && i1.x1() == i0.x1() + 1)
				return 67;
		}

		/*
		 * 18
		 */

		if (checkSize(intervals, 4, 6, 4, 2)) {

			if (i0.x2() == i2.x2() && i1.x2() == i3.x2() && i0.x2() == i1.x2() - 1)
				return 68;

			if (i0.x1() == i2.x1() && i1.x1() == i3.x1() && i0.x1() == i1.x1() + 1)
				return 69;
		}

		/*
		 * 19
		 */

		if (checkSize(intervals, 4, 6, 6, 2)) {

			if (i0.x1() == i2.x1() && i1.x1() == i2.x1() - 1 && i3.x2() == i1.x2())
				return 70;

			if (i0.x2() == i2.x2() && i1.x2() == i2.x2() + 1 && i3.x1() == i1.x1())
				return 71;
		}

		/*
		 * 20
		 */

		if (checkSize(intervals, 4, 6, 4, 4)) {

			if (i0.x2() == i2.x2() && i1.x2() == i3.x2() && i2.x2() == i1.x2() - 1)
				return 72;

			if (i0.x1() == i2.x1() && i1.x1() == i3.x1() && i2.x1() == i1.x1() + 1)
				return 73;
		}

		/*
		 * 21
		 */

		if (checkSize(intervals, 4, 8, 6)) {

			if (i0.x1() == i2.x1() && i1.x1() == i0.x1() - 1)
				return 74;

			if (i0.x2() == i2.x2() && i1.x2() == i0.x2() + 1)
				return 75;
		}

		/*
		 * 22
		 */

		if (checkSize(intervals, 6, 8, 4)) {

			if (i0.x1() == i2.x1() && i1.x1() == i0.x1() - 1)
				return 76;

			if (i0.x2() == i2.x2() && i1.x2() == i0.x2() + 1)
				return 77;
		}

		/*
		 * 23
		 */

		if (checkSize(intervals, 2, 6, 6, 4)) {

			if (i0.x2() == i2.x2() && i1.x2() == i0.x2() + 1 && i3.x2() == i2.x2() - 1)
				return 78;

			if (i0.x1() == i2.x1() && i1.x1() == i0.x1() - 1 && i3.x1() == i2.x1() + 1)
				return 79;
		}

		/*
		 * 24
		 */

		if (checkSize(intervals, 4, 4, 6, 4)) {

			if (i0.x2() == i2.x2() && i1.x2() == i3.x2() && i3.x2() == i2.x2() - 1)
				return 80;

			if (i0.x1() == i2.x1() && i1.x1() == i3.x1() && i3.x1() == i2.x1() + 1)
				return 81;

		}

		/*
		 * 25
		 */

		if (checkSize(intervals, 4, 10, 4)) {

			if (i0.x2() == i2.x2() && i1.x2() == i0.x2() + 3)
				return 82;
		}

		/*
		 * 26, 27
		 */

		if (checkSize(intervals, 2, 4, 6, 4, 2)) {

			if (i0.x2() == i2.x2() && i1.x2() == i3.x2() && i3.x2() == i2.x2() - 1 && i4.x1() == i2.x1())
				return 83;

			if (i0.x1() == i2.x1() && i1.x1() == i3.x1() && i3.x1() == i2.x1() + 1 && i4.x2() == i2.x2())
				return 84;
		}

		/*
		 * 28
		 */

		if (checkSize(intervals, 4, 6, 4, 2, 2) && LinAlgorithm.intervalsOnSameLine(i3, i4)) {

			if (i0.x1() == i2.x1() && i1.x1() == i3.x1() && i2.x1() == i1.x1() + 1 && i4.x2() == i1.x2())
				return 85;
		}

		/*
		 * 29
		 */

		if (checkSize(intervals, 2, 2, 4, 6, 4) && LinAlgorithm.intervalsOnSameLine(i0, i1)) {

			if (i0.x1() == i3.x1() && i2.x1() == i4.x1() && i2.x1() == i0.x1() + 1 && i1.x2() == i3.x2())
				return 86;
		}

		/*
		 * 49
		 */

		if (checkSize(intervals, 2, 4, 8, 4)) {

			if (i1.x2() == i3.x2() && i2.x2() == i1.x2() + 3 && i0.x2() == i1.x2() + 1)
				return 87;

			if (i1.x1() == i3.x1() && i2.x1() == i1.x1() - 3 && i0.x1() == i1.x1() - 1)
				return 88;

		}

		/*
		 * 50
		 */

		if (checkSize(intervals, 4, 8, 4, 2)) {

			if (i0.x1() == i2.x1() && i1.x1() == i0.x1() - 1 && i3.x1() == i2.x1() + 3)
				return 89;

			if (i0.x2() == i2.x2() && i1.x2() == i0.x2() + 1 && i3.x2() == i2.x2() - 3)
				return 90;
		}

		/*
		 * 30
		 */

		if (checkSize(intervals, 4, 10, 6)) {

			if (i0.x1() == i2.x1() && i1.x1() == i2.x1() - 3)
				return 91;

			if (i0.x2() == i2.x2() && i1.x2() == i2.x2() + 3)
				return 92;
		}

		/*
		 * 31
		 */

		if (checkSize(intervals, 6, 10, 4)) {

			if (i0.x1() == i2.x1() && i1.x1() == i2.x1() - 3)
				return 93;

			if (i0.x2() == i2.x2() && i1.x2() == i2.x2() + 3)
				return 94;
		}

		/*
		 * 32
		 */

		if (checkSize(intervals, 2, 4, 6, 4, 4)) {

			if (i0.x1() == i2.x1() && i1.x1() == i3.x1() && i1.x1() == i0.x1() + 1 && i4.x1() == i3.x1() + 1)
				return 95;

			if (i0.x2() == i2.x2() && i1.x2() == i3.x2() && i1.x2() == i0.x2() - 1 && i4.x2() == i3.x2() - 1)
				return 96;
		}

		/*
		 * 33
		 */

		if (checkSize(intervals, 2, 4, 6, 6, 2)) {

			if (i0.x1() == i2.x1() && i1.x1() == i3.x1() && i1.x1() == i0.x1() + 1 && i4.x2() == i2.x2())
				return 97;

			if (i0.x2() == i2.x2() && i1.x2() == i3.x2() && i1.x2() == i0.x2() - 1 && i4.x1() == i2.x1())
				return 98;
		}

		/*
		 * 34
		 */

		if (checkSize(intervals, 2, 6, 6, 4, 2)) {

			if (i1.x1() == i3.x1() && i2.x1() == i4.x1() && i1.x1() == i2.x1() + 1 && i0.x1() == i1.x1() + 3)
				return 99;

			if (i1.x2() == i3.x2() && i2.x2() == i4.x2() && i1.x2() == i2.x2() - 1 && i0.x2() == i1.x2() - 3)
				return 100;
		}

		/*
		 * 35
		 */

		if (checkSize(intervals, 4, 4, 6, 4, 2)) {

			if (i0.x2() == i2.x2() && i1.x2() == i3.x2() && i1.x2() == i2.x2() - 1 && i4.x1() == i2.x1())
				return 101;

			if (i0.x1() == i2.x1() && i1.x1() == i3.x1() && i1.x1() == i2.x1() + 1 && i4.x2() == i2.x2())
				return 102;
		}

		/*
		 * 36
		 */

		if (checkSize(intervals, 2, 2, 6, 6, 4) && LinAlgorithm.intervalsOnSameLine(i0, i1)) {

			if (i0.x1() == i3.x1() && i2.x1() == i0.x1() - 1 && i4.x1() == i3.x1() + 1 && i1.x2() == i3.x2())
				return 103;

			if (i0.x1() == i3.x1() && i2.x1() == i4.x1() && i2.x1() == i0.x1() + 1 && i1.x2() == i3.x2())
				return 104;
		}

		/*
		 * 37
		 */

		if (checkSize(intervals, 4, 6, 6, 2, 2) && LinAlgorithm.intervalsOnSameLine(i3, i4)) {

			if (i0.x2() == i2.x2() && i1.x2() == i4.x2() && i2.x2() == i4.x2() - 1 && i3.x1() == i1.x1())
				return 105;

			if (i0.x1() == i2.x1() && i1.x1() == i3.x1() && i0.x1() == i1.x1() + 1 && i4.x2() == i1.x2())
				return 106;
		}

		/*
		 * 38
		 */

		if (checkSize(intervals, 2, 4, 8, 6)) {

			if (i1.x2() == i3.x2() && i2.x2() == i1.x2() + 1 && i0.x2() == i1.x2() - 3)
				return 107;

			if (i1.x1() == i3.x1() && i2.x1() == i1.x1() - 1 && i0.x1() == i1.x1() + 3)
				return 108;
		}

		/*
		 * 39
		 */

		if (checkSize(intervals, 6, 8, 4, 2)) {

			if (i0.x2() == i2.x2() && i1.x2() == i2.x2() + 1 && i3.x2() == i2.x2() - 3)
				return 109;

			if (i0.x1() == i2.x1() && i1.x1() == i2.x1() - 1 && i3.x1() == i2.x1() + 3)
				return 110;
		}

		/*
		 * 40
		 */

		if (checkSize(intervals, 4, 8, 4, 4)) {

			if (i0.x1() == i2.x1() && i1.x1() == i2.x1() - 1 && i3.x1() == i2.x1() + 1)
				return 111;

			if (i0.x2() == i2.x2() && i1.x2() == i2.x2() + 1 && i3.x2() == i2.x2() - 1)
				return 112;
		}

		/*
		 * 41
		 */

		if (checkSize(intervals, 4, 4, 8, 4)) {

			if (i1.x1() == i3.x1() && i0.x1() == i1.x1() + 1 && i2.x1() == i3.x1() - 1)
				return 113;

			if (i1.x2() == i3.x2() && i0.x2() == i1.x2() - 1 && i2.x2() == i3.x2() + 1)
				return 114;
		}

		/*
		 * 42
		 */

		if (checkSize(intervals, 6, 10, 6)) {

			if (i0.x2() == i1.x2() - 1 && i2.x2() == i1.x2() - 3)
				return 115;

			if (i0.x1() == i1.x1() + 1 && i2.x1() == i1.x1() + 3)
				return 116;
		}

		/*
		 * 43
		 */

		if (checkSize(intervals, 4, 4, 6, 4, 4)) {

			if (i0.x2() == i2.x2() && i1.x2() == i3.x2() && i1.x2() == i0.x2() - 1 && i4.x2() == i3.x2() - 1)
				return 117;

			if (i0.x1() == i2.x1() && i1.x1() == i3.x1() && i1.x1() == i0.x1() + 1 && i4.x1() == i3.x1() + 1)
				return 118;
		}

		/*
		 * 44
		 */

		if (checkSize(intervals, 2, 6, 6, 6, 2)) {

			if (i0.x2() == i2.x2() && i1.x2() == i0.x2() + 1 && i3.x2() == i2.x2() - 1 && i4.x2() == i3.x2() - 3)
				return 119;

			if (i0.x1() == i2.x1() && i1.x1() == i0.x1() - 1 && i3.x1() == i2.x1() + 1 && i4.x1() == i3.x1() + 3)
				return 120;
		}

		/*
		 * 45
		 */

		if (checkSize(intervals, 2, 2, 8, 6, 4) && LinAlgorithm.intervalsOnSameLine(i0, i1)) {

			if (i0.x1() == i3.x1() && i2.x1() == i0.x1() - 1 && i4.x1() == i3.x1() + 1 && i1.x2() == i3.x2())
				return 121;
		}

		/*
		 * 46
		 */

		if (checkSize(intervals, 4, 6, 8, 2, 2) && LinAlgorithm.intervalsOnSameLine(i3, i4)) {

			if (i1.x1() == i3.x1() && i2.x1() == i3.x1() - 1 && i0.x1() == i1.x1() + 1 && i4.x2() == i1.x2())
				return 122;
		}

		/*
		 * 47
		 */

		if (checkSize(intervals, 6, 8, 4, 4)) {

			if (i0.x1() == i2.x1() && i1.x1() == i2.x1() - 1 && i3.x1() == i2.x1() + 1)
				return 123;

			if (i0.x2() == i2.x2() && i1.x2() == i2.x2() + 1 && i3.x2() == i2.x2() - 1)
				return 124;
		}

		/*
		 * 48
		 */

		if (checkSize(intervals, 4, 4, 8, 6)) {

			if (i0.x1() == i1.x1() + 1 && i1.x1() == i3.x1() && i2.x1() == i1.x1() - 1)
				return 125;

			if (i0.x2() == i1.x2() - 1 && i1.x2() == i3.x2() && i2.x2() == i1.x2() + 1)
				return 126;
		}

		/*
		 * 49
		 */

		if (checkSize(intervals, 4, 10, 4)) {

			if (i0.x1() == i2.x1() && i1.x1() == i0.x1() - 1)
				return 127;

			if (i0.x2() == i2.x2() && i1.x2() == i0.x2() + 1)
				return 128;
		}

		/*
		 * 50
		 */

		if (checkSize(intervals, 2, 2, 4, 6, 4)) {

			if (i2.x1() == i4.x1() && i3.x1() == i2.x1() - 1 && i1.x2() == i3.x2() && i0.x2() == i1.x2() + 1)
				return 129;

			if (i2.x1() == i4.x1() && i3.x1() == i2.x1() - 1 && i1.x1() == i3.x1() && i0.x1() == i1.x1() - 1)
				return 130;
		}

		/*
		 * 51
		 */

		if (checkSize(intervals, 4, 6, 4, 2, 2)) {

			if (i0.x1() == i2.x1() && i1.x1() == i0.x1() - 1 && i1.x1() == i3.x1() && i4.x1() == i3.x1() - 1)
				return 131;

			if (i0.x2() == i2.x2() && i1.x2() == i0.x2() + 1 && i1.x2() == i3.x2() && i4.x2() == i3.x2() + 1)
				return 132;
		}

		/*
		 * Default
		 */

		return -1;
	}

	/*
	 * Initialize energies array
	 */

	public static int[][][] initEnergies() {

		int[][][] energies = new int[133][11][4];

		energies[0][0][0] = 2;

		energies[1][0][1] = 1;
		energies[1][1][1] = 1;

		energies[2][0][1] = 1;
		energies[2][1][1] = 1;

		energies[3][0][1] = 1;
		energies[3][1][1] = 1;

		energies[4][0][2] = 1;
		energies[4][2][2] = 1;

		energies[5][0][2] = 1;
		energies[5][2][2] = 1;

		energies[6][0][2] = 1;
		energies[6][2][2] = 1;

		energies[7][1][2] = 1;

		energies[8][0][2] = 1;

		energies[9][2][2] = 1;

		energies[10][1][2] = 1;

		energies[11][1][2] = 1;

		energies[12][1][2] = 1;

		energies[13][0][2] = 1;
		energies[13][3][2] = 1;

		energies[14][2][2] = 1;
		energies[14][1][2] = 1;

		energies[15][2][2] = 1;
		energies[15][1][2] = 1;

		energies[16][0][3] = 1;
		energies[16][3][3] = 1;

		energies[17][0][3] = 1;
		energies[17][3][3] = 1;

		energies[18][0][3] = 1;
		energies[18][3][3] = 1;

		energies[19][3][3] = 1;

		energies[20][1][3] = 1;

		energies[21][2][3] = 1;

		energies[22][0][3] = 1;

		energies[23][2][3] = 1;

		energies[24][2][3] = 1;

		energies[25][1][3] = 1;

		energies[26][1][3] = 1;

		energies[27][1][3] = 1;

		energies[28][0][3] = 1;

		energies[29][3][3] = 1;

		energies[30][2][3] = 1;

		energies[31][1][3] = 1;

		energies[32][2][3] = 1;

		energies[33][3][3] = 1;

		energies[34][2][3] = 1;

		energies[35][1][3] = 1;

		energies[36][2][3] = 1;

		energies[37][2][3] = 1;

		energies[38][2][3] = 1;

		energies[39][4][3] = 1;

		energies[40][2][3] = 1;

		energies[41][2][3] = 1;

		energies[42][0][3] = 1;

		energies[43][2][3] = 1;

		energies[44][3][3] = 1;

		energies[45][1][3] = 1;

		energies[46][2][3] = 1;

		energies[47][4][3] = 1;

		energies[48][3][3] = 1;

		energies[49][0][3] = 1;

		energies[50][1][3] = 1;

		energies[51][3][3] = 1;

		energies[52][2][3] = 1;

		energies[53][2][3] = 1;

		energies[54][2][3] = 1;

		energies[55][3][3] = 1;

		energies[56][3][3] = 1;

		energies[57][3][2] = -3;
		energies[57][3][3] = -13;// -13 avant, � voir

		energies[58][0][3] = 1;
		energies[58][5][3] = 1;

		energies[59][2][3] = 1;
		energies[59][3][3] = 1;

		energies[60][1][3] = 1;
		energies[60][4][3] = 1;

		energies[61][0][3] = 1;
		energies[61][5][3] = 1;

		energies[62][1][3] = 1;
		energies[62][4][3] = 1;

		energies[63][2][3] = 1;
		energies[63][3][3] = 1;

		// 64 - 69 : -4 avant

		energies[64][3][3] = -5;

		energies[65][4][3] = -5;

		energies[66][4][3] = -5;

		energies[67][4][3] = -5;

		energies[68][3][3] = -5;

		energies[69][3][3] = -5;

		energies[70][3][3] = -1;

		energies[71][3][3] = -1;

		energies[72][3][3] = -1;

		energies[73][3][3] = -1;

		energies[74][3][3] = -1;

		energies[75][4][3] = -1;

		energies[76][4][3] = -1;

		energies[77][5][3] = -1;

		energies[78][5][3] = -1;

		energies[79][5][3] = -1;

		energies[80][5][3] = -1;

		energies[81][5][3] = -1;

		energies[82][4][3] = -2;

		energies[83][4][3] = -2;

		energies[84][4][3] = -2;

		energies[85][3][3] = -1;

		energies[86][5][3] = -1;

		energies[87][4][3] = -1;

		energies[88][5][3] = -1;

		energies[89][3][3] = -1;

		energies[90][4][3] = -1;

		energies[91][4][3] = -1;

		energies[92][4][3] = -1;

		energies[93][5][3] = -1;

		energies[94][5][3] = -1;

		energies[95][4][3] = -1;

		energies[96][4][3] = -1;

		energies[97][4][3] = -1;

		energies[98][4][3] = -1;

		energies[99][5][3] = -1;

		energies[100][5][3] = -1;

		energies[101][5][3] = -1;

		energies[102][5][3] = -1;

		energies[103][6][3] = -1;

		energies[104][6][3] = -1;

		energies[105][3][3] = -1;

		energies[106][3][3] = -1;

		energies[107][5][3] = -1;

		energies[108][4][3] = -1;

		energies[109][5][3] = -1;

		energies[110][4][3] = -1;

		energies[111][3][3] = -1;

		energies[112][4][3] = -1;

		energies[113][5][3] = -1;

		energies[114][6][3] = -1;

		energies[115][5][3] = -1;

		energies[116][5][3] = -1;

		energies[117][5][3] = -1;

		energies[118][5][3] = -1;

		energies[119][5][3] = -1;

		energies[120][5][3] = -1;

		energies[121][7][3] = -1;

		energies[122][3][3] = -1;

		energies[123][4][3] = -1;

		energies[124][5][3] = -1;

		energies[125][5][3] = -1;

		energies[126][6][3] = -1;

		energies[127][3][3] = -1;

		energies[128][5][3] = -1;

		energies[129][5][3] = -1;

		energies[130][5][3] = -1;

		energies[131][3][3] = -1;

		energies[132][3][3] = -1;

		return energies;
	}
}
