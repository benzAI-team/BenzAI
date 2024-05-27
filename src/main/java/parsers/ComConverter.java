package parsers;

import benzenoid.Benzenoid;
import benzenoid.Node;
import solution.ClarCoverSolution;
import utils.Couple;
import utils.Triplet;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public enum ComConverter {
    ;

    public enum ComType {
		ER, IR
	}

	public static ArrayList<Integer> getCarbonsWithHydrogens(Benzenoid molecule) {

		ArrayList<Integer> carbons = new ArrayList<>();

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
                                                                    ArrayList<Integer> carbonsWithHydrogens) {

		ArrayList<Couple<Integer, Integer>> invalidsCarbons = new ArrayList<>();

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
							invalidsCarbons.add(new Couple<>(i, j));
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

		ArrayList<Integer> hexagons = new ArrayList<>();

		for (int i = 0; i < molecule.getNbHexagons(); i++) {

			for (int j = 0; j < 6; j++) {

				if (molecule.getHexagons()[i][j] == node)
					hexagons.add(i);
			}
		}

		return hexagons;
	}


	public static void generateComFile(Benzenoid molecule, int coverIndex, File file, int nbElectronsDiff, ComType type, String title)
			throws IOException {

		int hexagonIndex = -1, yMin = Integer.MAX_VALUE;

		for (int i = 0; i < molecule.getNbHexagons(); i++) {
			int[] hexagonCarbonTab = molecule.getHexagons()[i];
			int y = molecule.getNodesCoordinates()[hexagonCarbonTab[0]].getY();

			if (y < yMin) {
				yMin = y;
				hexagonIndex = i;
			}
		}

		int[] checkedHexagons = new int[molecule.getNbHexagons()];

		ArrayList<Integer> hexagonsOrder = new ArrayList<>();
		hexagonsOrder.add(hexagonIndex);

		ArrayList<Integer> candidates = new ArrayList<>();
		candidates.add(hexagonIndex);

		int[][] dualGraph = molecule.getDualGraph();

		checkedHexagons[hexagonIndex] = 1;

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

				double xu, yu, xv = 0, yv = 0;

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

		ArrayList<Triplet<Double, Double, Double>> hydrogens = new ArrayList<>();

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
					assert couple != null;
					int position = couple.getY();

					if (position == 0) {
						xv = xu;
						yv = yu - 1.0;

					}

					else if (position == 1) {

						xv = xu + 0.8675;
						yv = yu - 0.4957;

					}

					else if (position == 2) {

						xv = xu + 0.8675;
						yv = yu + 0.4957;

					}

					else if (position == 3) {

						xv = xu;
						yv = yu + 1.0;

					}

					else if (position == 4) {

						xv = xu - 0.8675;
						yv = yu + 0.4957;

					}

					else if (position == 5) {
						xv = xu - 0.8675;
						yv = yu - 0.4957;

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

					assert uResult != null;
					int uHexagon = uResult.getX();
					assert vResult != null;
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

		writeMolecule(file, nbElectronsDiff, type, title, carbons, hydrogens, molecule, coverIndex);
	}

	/***
	 * Write Molecule to a COM file
	 */
	private static void writeMolecule(File file, int nbElectronsDiff, ComType type, String title, Triplet<Double, Double, Double>[] carbons, ArrayList<Triplet<Double, Double, Double>> hydrogens, Benzenoid molecule, int coverIndex) throws IOException {
		int nbCarbons = carbons.length;
		int nbHydrogens = hydrogens.size();
		int spin = (6 * nbCarbons) + nbHydrogens + nbElectronsDiff;
		int charge = -1 * nbElectronsDiff;

		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writeHeader(type, title, writer);
		writeCharge(spin, charge, writer);
		writeCarbons(carbons, writer);
		writeHydrogens(hydrogens, writer);
		writer.write("\n"); // mandatory in a COM file: never remove
		System.out.println("######################" + coverIndex);
		if(molecule.getClarCoverSolutions() != null)
			writeClarCoverInfo(molecule, coverIndex, writer);
		writer.close();
	}

	private static void writeHeader(ComType type, String title, BufferedWriter writer) throws IOException {
		switch (type) {
		case ER:
			writeERHeader(title, writer);
			break;
		case IR:
			writeIRHeader(title, writer);
			break;
		}
	}

	private static void writeERHeader(String title, BufferedWriter writer) throws IOException {
		writer.write("%mem=1Gb" + "\n");
		writer.write("# opt b3lyp/6-31G" + "\n");
		writer.write("\n");
		writer.write(title + "\n");
		writer.write("\n");
	}

	private static void writeIRHeader(String title, BufferedWriter writer) throws IOException {
		writer.write("%nproc=32" + "\n");
		writer.write("%chk=" + title + ".chk" + "\n");
		writer.write("%mem=1Gb" + "\n");
		writer.write("# b3lyp/6-31g opt freq" + "\n");
		writer.write("\n");
		writer.write(title + "\n");
		writer.write("\n");
	}

	private static void writeCharge(int spin, int charge, BufferedWriter writer) throws IOException {
		if (spin % 2 == 0)
			writer.write(charge + " 1" + "\n");
		else
			writer.write(charge + " 2" + "\n");
	}

	private static void writeCarbons(Triplet<Double, Double, Double>[] carbons, BufferedWriter writer) throws IOException {
		for (Triplet<Double, Double, Double> carbon : carbons) {
			writer.write(" C " + carbon.getX() + " " + carbon.getY() + " " + carbon.getZ() + "\n");
		}
	}

	private static void writeHydrogens(ArrayList<Triplet<Double, Double, Double>> hydrogens, BufferedWriter writer) throws IOException {
		for (Triplet<Double, Double, Double> hydrogen : hydrogens) {
			writer.write(" H " + hydrogen.getX() + " " + hydrogen.getY() + " " + hydrogen.getZ() + "\n");
		}
	}

	private static void  writeClarCoverInfo(Benzenoid molecule, int coverIndex, BufferedWriter writer) throws IOException {
		writeBlockProlog(writer);
		writeBonds(writer, molecule, coverIndex);
		writeBlockEpilog(writer);
	}


	private static void writeBlockProlog(BufferedWriter writer) throws IOException {
		writer.write("$nbo nrt $end\n" +
				"$NRTSTR ! Trust factor = 22,88% (HLP)\n" +
				"STR ! HuLiS Wgt = 100,00% (HLP) S1\n" +
				"LONE END ! pas de Paire libre dans ces familles de système\n");
	}

	private static void writeBonds(BufferedWriter writer, Benzenoid molecule, int coverIndex) throws IOException {
		writeBondList(writer, molecule);
		writeBLWDAT(writer, molecule, coverIndex);
	}


	private static void writeBondList(BufferedWriter writer, Benzenoid molecule) throws IOException {
		writer.write("BOND ");
		for (int i = 0; i < molecule.getNbCarbons(); i++) {
			for (int j = (i + 1); j < molecule.getNbCarbons(); j++) {
				if (molecule.getEdgeMatrix()[i][j] == 1)
					writer.write("S " + (i + 1) + " " + (j + 1) +  " ");
			}
		}
		writer.write("END\n");
	}

	private static void writeBLWDAT(BufferedWriter writer, Benzenoid molecule, int coverIndex) throws IOException {
		ClarCoverSolution clarCoverSolution = molecule.getClarCoverSolutions().get(coverIndex);
		int nbCarbons = clarCoverSolution.getNbCarbons();
		int nbHexagons = clarCoverSolution.getNbHexagons();

		long nbCircles = IntStream.range(0, nbHexagons).filter(clarCoverSolution::isCircle).count();
		long nbDoubleBonds = 0;
		for (int i = 0; i < nbCarbons; i++)
			for (int j = (i + 1); j < nbCarbons; j++)
				if(clarCoverSolution.isDoubleBond(i,j))
					nbDoubleBonds++;
		long nbSingles = IntStream.range(0, nbCarbons).filter(clarCoverSolution::isSingle).count();

		writer.write("$BLW\n" +
				(nbCircles + nbDoubleBonds + nbSingles) + "\n" +
				"$END\n" +
				"$BLWDAT\n");
		System.out.println(clarCoverSolution);
		writeCircleBlocks(writer, clarCoverSolution, molecule);
		writeDoubleBoundBlocks(writer, clarCoverSolution);
		writeSingleBlocks(writer, clarCoverSolution);
	}

	private static void writeCircleBlocks(BufferedWriter writer, ClarCoverSolution clarCoverSolution, Benzenoid molecule) throws IOException {
		for (int i = 0; i < clarCoverSolution.getNbHexagons(); i++)
			if (clarCoverSolution.isCircle(i)) {
				writer.write("6 6\n"); // six carbons (ring)
				for (int j = 0; j < clarCoverSolution.getNbCarbons(); j++)
					if (molecule.getHexagonsInvolved(j).contains(i))
						writer.write((j + 1) + " ");
				writer.write("\n0\n");
			}
	}
	private static void writeDoubleBoundBlocks(BufferedWriter writer, ClarCoverSolution clarCoverSolution) throws IOException {
		int nbCarbons = clarCoverSolution.getNbCarbons();
		for (int i = 0; i < nbCarbons; i++)
			for (int j = (i + 1); j < nbCarbons; j++)
				if(clarCoverSolution.isDoubleBond(i, j)){
					writer.write("2 2\n"); // two carbons
					writer.write((i+1) + " " + (j+1) + "\n");
					writer.write("0\n");
				}
	}

	private static void writeSingleBlocks(BufferedWriter writer, ClarCoverSolution clarCoverSolution) throws IOException {
		for(int i = 0; i < clarCoverSolution.getNbCarbons(); i++)
			if(clarCoverSolution.isSingle(i)){
				writer.write("1 1\n"); // one carbon
				writer.write((i+1) + "\n");
				writer.write("0\n");
			}
	}

	private static void writeBlockEpilog(BufferedWriter writer) {
		try {
			writer.write("END\n" +
					"$END");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/***
	 * Converter as a separate app
	 */
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
			assert molecule != null;
			generateComFile(molecule, 0 - 1, new File(line.split(Pattern.quote("."))[0] + ".com"), nbElectronsDiff, type,
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
