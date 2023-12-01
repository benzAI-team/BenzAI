package parsers;

import java.util.Locale;
import benzenoid.Benzenoid;
import benzenoid.Node;
import utils.Couple;
import utils.Triplet;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Pattern;

public enum ComConverter {
    ;

    public enum ComType {
		ER, IR
	}

	public static ArrayList<Integer> getCarbonsWithHydrogens(Benzenoid molecule) {

		ArrayList<Integer> carbons = new ArrayList<Integer>();

		for (int c = 0; c < molecule.getNbCarbons(); c++) {
			if (molecule.degree(c) == 2)
				carbons.add(c);
		}

		return carbons;
	}

	public static Couple<Integer, Integer> findHexagon(Benzenoid molecule, int carbon) {

		for (int i = 0; i < molecule.getNbHexagons(); i++) {

			int[] hexagon = molecule.getHexagons()[i];

			for (int j = 0; j < 6; j++) {
				if (hexagon[j] == carbon)
					return new Couple<>(i, j);
			}
		}

		return null;
	}

	public static boolean areOnSameHexagon(Benzenoid molecule, int u, int v) {

		for (int i = 0; i < molecule.getNbHexagons(); i++) {

			boolean uPresent = false;
			boolean vPresent = false;

			int[] hexagon = molecule.getHexagons()[i];

			for (int j = 0; j < 6; j++) {

				if (u == hexagon[j])
					uPresent = true;

				if (v == hexagon[j])
					vPresent = true;
			}

			if (uPresent && vPresent) {
				return true;
			}
		}

		return false;
	}

	public static boolean validConfiguration(Benzenoid molecule, Node u, Node v) {

		if (u.getX() == 1 && u.getY() == 2 && v.getX() == 2 && v.getY() == 4)
			System.out.print("");

		if (u.getX() == v.getX() && Math.abs(u.getY() - v.getY()) == 1)
			return true;

		else if ((u.getX() == v.getX() - 1 && u.getY() == v.getY() - 1)
				|| (u.getX() == v.getX() + 1 && u.getY() == v.getY() + 1))
			return true;

		else if ((u.getX() == v.getX() - 1 && u.getY() == v.getY() + 1)
				|| (u.getX() == v.getX() + 1 && u.getY() == v.getY() - 1))
			return true;

		boolean validHexaConfig = false;

		ArrayList<Integer> hexagonsU = getHexagons(molecule, u.getIndex());
		ArrayList<Integer> hexagonsV = getHexagons(molecule, v.getIndex());

		for (int h1 : hexagonsU) {

			Couple<Integer, Integer> uCoords = molecule.getHexagonsCoords()[h1];

			for (int h2 : hexagonsV) {

				Couple<Integer, Integer> vCoords = molecule.getHexagonsCoords()[h2];

				if (Math.abs(uCoords.getX() - vCoords.getX()) == 2 && Math.abs(uCoords.getX() - vCoords.getY()) == 2) {
					// System.out.println(h1 + " & " + h2);
					validHexaConfig = true;
				}
			}
		}

		/*
		 * else if ( (Math.abs(u.getX() - v.getX()) == 1 && Math.abs(u.getY() -
		 * v.getY()) == 2) || (Math.abs(u.getX() - v.getX()) == 2 && u.getY() ==
		 * v.getY())) {
		 * 
		 * System.out.println(u + " & " + v); return true; }
		 */

		// System.out.println(u + " & " + v);
		return (validHexaConfig && u.getX() == v.getX() + 1 && u.getY() == v.getY() - 2)
				|| (validHexaConfig && u.getX() == v.getX() - 1 && u.getY() == v.getY() - 2);
	}

	public static ArrayList<Couple<Integer, Integer>> checkGeometry(Benzenoid molecule,
                                                                    ArrayList<Integer> carbonsWithHydrogens) throws IOException {

		ArrayList<Couple<Integer, Integer>> invalidsCarbons = new ArrayList<Couple<Integer, Integer>>();

		for (int i = 0; i < molecule.getNbCarbons(); i++) {

			Node u = molecule.getNodeRef(i);

			for (int j = 0; j < molecule.getNbCarbons(); j++) {

				if (i != j) {

					Node v = molecule.getNodeRef(j);

					// if (u.getX() == v.getX() && Math.abs(u.getY() - v.getY()) == 1) {
					if (validConfiguration(molecule, u, v)) {

						boolean alreadyExists = false;

						for (Couple<Integer, Integer> couple : invalidsCarbons) {

							if ((couple.getX() == i && couple.getY() == j)
									|| (couple.getX() == j && couple.getY() == i)) {
								alreadyExists = true;
								break;
							}

						}

						if (!alreadyExists && !areOnSameHexagon(molecule, i, j) && carbonsWithHydrogens.contains(i)
								&& carbonsWithHydrogens.contains(j)) {
							invalidsCarbons.add(new Couple<Integer, Integer>(i, j));
						}
					}
				}
			}
		}

		return invalidsCarbons;
	}

	public static int isInvalid(ArrayList<Couple<Integer, Integer>> invalidsCarbons, int carbon) {

		for (Couple<Integer, Integer> carbons : invalidsCarbons) {

			if (carbons.getX() == carbon)
				return carbons.getY();

			else if (carbons.getY() == carbon)
				return carbons.getX();
		}

		return -1;
	}

	private static ArrayList<Integer> getHexagons(Benzenoid molecule, int node) {

		ArrayList<Integer> hexagons = new ArrayList<Integer>();

		for (int i = 0; i < molecule.getNbHexagons(); i++) {

			for (int j = 0; j < 6; j++) {

				if (molecule.getHexagons()[i][j] == node)
					hexagons.add(i);
			}
		}

		return hexagons;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	public static void generateComFile(Benzenoid molecule, File file, int nbElectronsDiff, ComType type, String title)
			throws IOException {

		int hexa = -1, yMin = Integer.MAX_VALUE;

		for (int i = 0; i < molecule.getNbHexagons(); i++) {
			int[] hexagon = molecule.getHexagons()[i];
			int y = molecule.getNodesCoordinates()[hexagon[0]].getY();

			if (y < yMin) {
				yMin = y;
				hexa = i;
			}
		}

		int[] checkedHexagons = new int[molecule.getNbHexagons()];

		ArrayList<Integer> hexagonsOrder = new ArrayList<Integer>();
		hexagonsOrder.add(hexa);

		ArrayList<Integer> candidates = new ArrayList<Integer>();
		candidates.add(hexa);

		int[][] dualGraph = molecule.getDualGraph();

		checkedHexagons[hexa] = 1;

		while (candidates.size() > 0) {

			int candidat = candidates.get(0);

			for (int i = 0; i < 6; i++) {
				if (dualGraph[candidat][i] != -1) {

					int newCandidat = dualGraph[candidat][i];

					if (checkedHexagons[newCandidat] == 0) {
						checkedHexagons[newCandidat] = 1;
						candidates.add(newCandidat);
						hexagonsOrder.add(newCandidat);
					}
				}
			}

			candidates.remove(candidates.get(0));
		}

		Triplet<Double, Double, Double>[] carbons = new Triplet[molecule.getNbCarbons()];

		for (Integer h : hexagonsOrder) {

			int[] hexagon = molecule.getHexagons()[h];

			int index = 0;

			for (int i = 0; i < 6; i++) {
				int u = hexagon[i];
				if (carbons[u] != null) {
					index = i;
					break;
				}
			}

			for (int i = 0; i < 6; i++) {

				int u = hexagon[index];
				int v = hexagon[(index + 1) % 6];

				double xu = 0, yu = 0, xv = 0, yv = 0;

				if (carbons[u] == null) {
					xu = molecule.getNodesCoordinates()[u].getX();
					yu = molecule.getNodesCoordinates()[u].getY();
				} else {
					xu = carbons[u].getX();
					yu = carbons[u].getY();
				}

				if (index == 0) {
					xv = xu + 1.2145;
					yv = yu + 0.694;
				}
				else if (index == 1) {
					xv = xu;
					yv = yu + 1.388;
				}
				else if (index == 2) {
					xv = xu - 1.2145;
					yv = yu + 0.694;
				}
				else if (index == 3) {
					xv = xu - 1.2145;
					yv = yu - 0.694;
				}
				else if (index == 4) {
					xv = xu;
					yv = yu - 1.388;
				}
				else if (index == 5) {
					xv = xu + 1.2145;
					yv = yu - 0.694;
				}

				index = (index + 1) % 6;

				if (carbons[u] == null)
					carbons[u] = new Triplet<>(xu, yu, 0.0);

				if (carbons[v] == null)
					carbons[v] = new Triplet<>(xv, yv, 0.0);
			}
		}

		ArrayList<Triplet<Double, Double, Double>> hydrogens = new ArrayList<Triplet<Double, Double, Double>>();

		ArrayList<Integer> carbonsWithHydrogens = getCarbonsWithHydrogens(molecule);
		ArrayList<Couple<Integer, Integer>> invalidsCarbons = checkGeometry(molecule, carbonsWithHydrogens);

		int[] treatedCarbons = new int[molecule.getNbCarbons()];

		for (Integer u : carbonsWithHydrogens) {

			if (treatedCarbons[u] == 0) {

				int carbonPair = isInvalid(invalidsCarbons, u);

				// Si le carbone n'est pas probl�matique
				if (carbonPair == -1) {

					double xu = carbons[u].getX();
					double yu = carbons[u].getY();

					int xur = molecule.getNodeRef(u).getX();
					int yur = molecule.getNodeRef(u).getY();

					double xv = 0, yv = 0;
					int xvr = 0, yvr = 0;

					Couple<Integer, Integer> couple = findHexagon(molecule, u);
					int position = couple.getY();

					if (position == 0) {
						xv = xu;
						yv = yu - 1.0;

						xvr = xur;
						yvr = yur - 1;
					}

					else if (position == 1) {

						xv = xu + 0.8675;
						yv = yu - 0.4957;

						xvr = xur + 1;
						yvr = yur - 1;
					}

					else if (position == 2) {

						xv = xu + 0.8675;
						yv = yu + 0.4957;

						xvr = xur + 1;
						yvr = yur + 1;
					}

					else if (position == 3) {

						xv = xu;
						yv = yu + 1.0;

						xvr = xur;
						yvr = yur + 1;
					}

					else if (position == 4) {

						xv = xu - 0.8675;
						yv = yu + 0.4957;

						xvr = xur - 1;
						yvr = yur + 1;
					}

					else if (position == 5) {
						xv = xu - 0.8675;
						yv = yu - 0.4957;

						xvr = xur - 1;
						yvr = yur - 1;
					}

					// if (molecule.getCoords().get(xvr, yvr) == -1)
					hydrogens.add(new Triplet<>(xv, yv, 0.0));
				}

				/*
				 * TODO: modifier ces valeurs
				 */

				// Si le carbone est "invalide" , (u et carbonPair sont trop proches)
				else {

					double uHX, uHY, uHZ;
					double vHX, vHY, vHZ;

					Triplet<Double, Double, Double> uCoords = carbons[u];
					Triplet<Double, Double, Double> vCoords = carbons[carbonPair];

					Couple<Integer, Integer> uResult = findHexagon(molecule, u);
					Couple<Integer, Integer> vResult = findHexagon(molecule, carbonPair);

					int uHexagon = uResult.getX();
					int vHexagon = vResult.getX();

					int uPosition = uResult.getY();
					int vPosition = vResult.getY();

					if (uPosition == 0) {

						uHX = uCoords.getX();
						uHY = uCoords.getY() - 0.5;
						uHZ = 0.9;
					}

					// 0.8XX -> 0.807 avant

					else if (uPosition == 1) {

						uHX = uCoords.getX() + 0.807;
						uHY = uCoords.getY() - 0.2103;
						uHZ = 0.7;
					}

					else if (uPosition == 2) {

						uHX = uCoords.getX() + 0.807;
						uHY = uCoords.getY() + 0.2103;
						uHZ = 0.7;
					}

					else if (uPosition == 3) {

						uHX = uCoords.getX();
						uHY = uCoords.getY() + 0.5;
						uHZ = 0.9;
					}

					else if (uPosition == 4) {

						uHX = uCoords.getX() - 0.807;
						uHY = uCoords.getY() + 0.2103;
						uHZ = 0.7;
					}

					else {

						uHX = uCoords.getX() - 0.807;
						uHY = uCoords.getY() - 0.2103;
						uHZ = 0.7;
					}

					if (vPosition == 0) {

						vHX = vCoords.getX();
						vHY = vCoords.getY() - 0.5;
						vHZ = -0.9;
					}

					else if (vPosition == 1) {

						vHX = vCoords.getX() + 0.807;
						vHY = vCoords.getY() - 0.2103;
						vHZ = -0.7;
					}

					else if (vPosition == 2) {

						vHX = vCoords.getX() + 0.807;
						vHY = vCoords.getY() + 0.2103;
						vHZ = -0.7;
					}

					else if (vPosition == 3) {

						vHX = vCoords.getX();
						vHY = vCoords.getY() + 0.5;
						vHZ = -0.9;
					}

					else if (vPosition == 4) {

						vHX = vCoords.getX() - 0.807;
						vHY = vCoords.getY() + 0.2103;
						vHZ = -0.7;
					}

					else {

						vHX = vCoords.getX() - 0.807;
						vHY = vCoords.getY() - 0.2103;
						vHZ = -0.7;
					}

					hydrogens.add(new Triplet<>(uHX, uHY, uHZ));
					hydrogens.add(new Triplet<>(vHX, vHY, vHZ));

					treatedCarbons[carbonPair] = 1;
				}

				treatedCarbons[u] = 1;
			}
		}

		/*
		 * ligne multiplicit� : charge " " spin charge : +1 si on enl�ve un electron
		 */
		int nbCarbons = carbons.length;
		int nbHydrogens = hydrogens.size();
		int spin = (6 * nbCarbons) + nbHydrogens + nbElectronsDiff;
		int charge = -1 * nbElectronsDiff;

		BufferedWriter writer = new BufferedWriter(new FileWriter(file));

		switch (type) {

		case ER:
			writer.write("%mem=1Gb" + "\n");
			writer.write("# opt b3lyp/6-31G" + "\n");
			writer.write("\n");
			writer.write(title + "\n");
			writer.write("\n");
			if (spin % 2 == 0)
				writer.write(charge + " 1" + "\n");
			else
				writer.write(charge + " 2" + "\n");
			break;

		case IR:
			writer.write("%nproc=32" + "\n");
			writer.write("%chk=" + title + ".chk" + "\n");
			writer.write("%mem=1Gb" + "\n");
			writer.write("# b3lyp/6-31g opt freq" + "\n");
			writer.write("\n");
			writer.write(title + "\n");
			writer.write("\n");

			if (spin % 2 == 0)
				writer.write(charge + " 1" + "\n");
			else
				writer.write(charge + " 2" + "\n");
			break;
		}

    Locale locale = new Locale( "en", "US" );

		for (Triplet<Double, Double, Double> carbon : carbons) {
			writer.write(" C " + String.format(locale,"%1.8f",carbon.getX()) + " " + String.format(locale,"%1.8f",carbon.getY()) + " " + String.format(locale,"%1.8f",carbon.getZ()) + "\n");
		}

		for (Triplet<Double, Double, Double> hydrogen : hydrogens) {
			writer.write(" H " + String.format(locale,"%1.8f",hydrogen.getX()) + " " + String.format(locale,"%1.8f",hydrogen.getY()) + " " + String.format(locale,"%1.8f",hydrogen.getZ()) + "\n");
		}


		writer.write("\n");

		writer.close();
	}

	private static void displayUsage() {
		System.out.println("USAGE : java -jar ComConverter.jar ${list_file} ${nbElectronsDiff} ${type}");
	}

	public static void generateAll(File file, int nbElectronsDiff, ComType type) throws IOException {

		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;

		while ((line = reader.readLine()) != null) {

			String titleTmp = line.split(Pattern.quote("."))[0];
			String[] split = titleTmp.split(Pattern.quote("/"));
			String title = split[split.length - 1];

			System.out.println("HELOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");

			Benzenoid molecule = GraphParser.parseUndirectedGraph(line, null, false);
			System.out.println(line.split(Pattern.quote("."))[0] + ".com generated");
			generateComFile(molecule, new File(line.split(Pattern.quote("."))[0] + ".com"), nbElectronsDiff, type,
					title/* line.split(Pattern.quote("."))[0] */);
		}

		reader.close();
	}

	public static void main(String[] args) throws IOException {

		if (args.length != 3) {
			displayUsage();
			System.exit(1);
		}

		File listFile = new File(args[0]);

		int nbElectronsDiff = Integer.parseInt(args[1]);

		ComType type;

		if ("1".equals(args[2]))
			type = ComType.ER;

		else
			type = ComType.IR;

		generateAll(listFile, nbElectronsDiff, type);
	}
}
