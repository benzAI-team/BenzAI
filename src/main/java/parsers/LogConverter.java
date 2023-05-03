package parsers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import utils.Couple;
import utils.Triplet;

public enum LogConverter {
    ;

    private static ArrayList<String> retrieveGeometry(File file) throws IOException {

		ArrayList<ArrayList<String>> geometries = new ArrayList<>();

		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;

		while (true) {

			line = reader.readLine();
			if (line == null)
				break;

			if (line.contains("Standard orientation")) {

				line = reader.readLine();
				ArrayList<String> geometry = new ArrayList<>();

				do {

					geometry.add(line);

					line = reader.readLine();
				} while (!line.contains("Rotational constants"));

				geometries.add(geometry);
			}
		}

		reader.close();

		ArrayList<String> geometry = geometries.get(geometries.size() - 1);

		for (int i = 0; i < 4; i++)
			geometry.remove(0);
		geometry.remove(geometry.size() - 1);

		return geometry;
	}

	private static Couple<ArrayList<Triplet<Double, Double, Double>>, ArrayList<Triplet<Double, Double, Double>>> readGeometry(
			ArrayList<String> geometry) {

		ArrayList<Triplet<Double, Double, Double>> carbons = new ArrayList<>();
		ArrayList<Triplet<Double, Double, Double>> hydrogens = new ArrayList<>();

		for (String line : geometry) {

			String[] splittedLine = line.split("\\s+");

			int atomType = Integer.parseInt(splittedLine[2]);

			Double x = Double.parseDouble(splittedLine[4]);
			Double y = Double.parseDouble(splittedLine[5]);
			Double z = Double.parseDouble(splittedLine[6]);

			Triplet<Double, Double, Double> atom = new Triplet<>(x, y, z);

			if (atomType == 6)
				carbons.add(atom);

			else if (atomType == 1)
				hydrogens.add(atom);

		}

		return new Couple<>(carbons, hydrogens);
	}

	// sqrt[(Xa-Xb)²+(Ya-Yb)²+(Za-Zb)²]
	private static Double distance(Triplet<Double, Double, Double> u, Triplet<Double, Double, Double> v) {

		double x1 = u.getX();
		double y1 = u.getY();
		double z1 = u.getZ();

		double x2 = v.getX();
		double y2 = v.getY();
		double z2 = v.getZ();

		return Math.sqrt(((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2)) + ((z1 - z2) * (z1 - z2)));
	}

	private static Couple<Integer, Integer> getClosestHydrogens(ArrayList<Triplet<Double, Double, Double>> hydrogens) {

		Double minDistance = Double.MAX_VALUE;
		Couple<Integer, Integer> closestHydrogens = null;

		for (int i = 0; i < hydrogens.size(); i++) {

			Triplet<Double, Double, Double> u = hydrogens.get(i);

			for (int j = (i + 1); j < hydrogens.size(); j++) {

				Triplet<Double, Double, Double> v = hydrogens.get(j);

				Double distance = distance(u, v);
				if (distance < minDistance) {
					minDistance = distance;
					closestHydrogens = new Couple<>(i, j);
				}
			}
		}

		return closestHydrogens;
	}

	private static void shiftHydrogens(
			Couple<ArrayList<Triplet<Double, Double, Double>>, ArrayList<Triplet<Double, Double, Double>>> geometry) {

		ArrayList<Triplet<Double, Double, Double>> hydrogens = geometry.getY();

		Couple<Integer, Integer> closestHydrogens = getClosestHydrogens(hydrogens);
		Triplet<Double, Double, Double> u = hydrogens.get(closestHydrogens.getX());
		Triplet<Double, Double, Double> v = hydrogens.get(closestHydrogens.getY());

		u.setZ(u.getZ() + 0.001);
		v.setZ(v.getZ() - 0.001);
	}

	private static void exportGeometry(File file,
			Couple<ArrayList<Triplet<Double, Double, Double>>, ArrayList<Triplet<Double, Double, Double>>> geometry)
			throws IOException {

		ArrayList<Triplet<Double, Double, Double>> carbons = geometry.getX();
		ArrayList<Triplet<Double, Double, Double>> hydrogens = geometry.getY();

		BufferedWriter writer = new BufferedWriter(new FileWriter(file));

		for (Triplet<Double, Double, Double> carbon : carbons)
			writer.write(" C " + carbon.getX() + " " + carbon.getY() + " " + carbon.getZ() + "\n");

		for (Triplet<Double, Double, Double> hydrogen : hydrogens)
			writer.write(" H " + hydrogen.getX() + " " + hydrogen.getY() + " " + hydrogen.getZ() + "\n");

		writer.close();
	}

	public static void readLogFile(File file) throws IOException {

		ArrayList<String> geometryString = retrieveGeometry(file);

		Couple<ArrayList<Triplet<Double, Double, Double>>, ArrayList<Triplet<Double, Double, Double>>> geometry = readGeometry(
				geometryString);

		shiftHydrogens(geometry);

		File output = new File(file.getAbsoluteFile() + "_geo");
		exportGeometry(output, geometry);
	}

	public static void main(String[] args) throws IOException {
		File file = new File("C:\\Users\\adrie\\Desktop\\5_hexagons10_irreg.log");
		readLogFile(file);
	}
}
