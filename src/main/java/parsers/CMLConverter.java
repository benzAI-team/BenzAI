package parsers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import molecules.Molecule;
import molecules.Node;
import utils.Couple;
import utils.Triplet;

public class CMLConverter {

	public static boolean validConfiguration(Node u, Node v) {

		if (u.getX() == v.getX() && Math.abs(u.getY() - v.getY()) == 1)
			return true;

		else if ((u.getX() == v.getX() - 1 && u.getY() == v.getY() - 1)
				|| (u.getX() == v.getX() + 1 && u.getY() == v.getY() + 1))
			return true;

		else if ((u.getX() == v.getX() - 1 && u.getY() == v.getY() + 1)
				|| (u.getX() == v.getX() + 1 && u.getY() == v.getY() - 1))
			return true;

		return false;
	}

	public static ArrayList<Integer> getCarbonsWithHydrogens(Molecule molecule) {

		ArrayList<Integer> carbons = new ArrayList<Integer>();

		for (int c = 0; c < molecule.getNbNodes(); c++) {
			if (molecule.degree(c) == 2)
				carbons.add(c);
		}

		return carbons;
	}

	public static Couple<Integer, Integer> findHexagon(Molecule molecule, int carbon) {

		for (int i = 0; i < molecule.getNbHexagons(); i++) {

			int[] hexagon = molecule.getHexagons()[i];

			for (int j = 0; j < 6; j++) {
				if (hexagon[j] == carbon)
					return new Couple<>(i, j);
			}
		}

		return null;
	}

	public static boolean areOnSameHexagon(Molecule molecule, int u, int v) {

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

	public static int isInvalid(ArrayList<Couple<Integer, Integer>> invalidsCarbons, int carbon) {

		for (Couple<Integer, Integer> carbons : invalidsCarbons) {

			if (carbons.getX() == carbon)
				return carbons.getY();

			else if (carbons.getY() == carbon)
				return carbons.getX();
		}

		return -1;
	}

	public static ArrayList<Couple<Integer, Integer>> checkGeometry(Molecule molecule) throws IOException {

		ArrayList<Couple<Integer, Integer>> invalidsCarbons = new ArrayList<Couple<Integer, Integer>>();

		for (int i = 0; i < molecule.getNbNodes(); i++) {

			Node u = molecule.getNodeRef(i);

			for (int j = 0; j < molecule.getNbNodes(); j++) {

				if (i != j) {

					Node v = molecule.getNodeRef(j);

					// if (u.getX() == v.getX() && Math.abs(u.getY() - v.getY()) == 1) {
					if (validConfiguration(u, v)) {

						boolean alreadyExists = false;

						for (Couple<Integer, Integer> couple : invalidsCarbons) {

							if ((couple.getX() == i && couple.getY() == j)
									|| (couple.getX() == j && couple.getY() == i))
								alreadyExists = true;

						}

						if (!alreadyExists && !areOnSameHexagon(molecule, i, j)) {
							invalidsCarbons.add(new Couple<Integer, Integer>(i, j));
						}
					}
				}
			}
		}

		return invalidsCarbons;
	}

	@SuppressWarnings("unused")
	public static void generateCmlFile(Molecule molecule, File file) throws IOException {

		int hexa = -1, yMin = Integer.MAX_VALUE;

		for (int i = 0; i < molecule.getNbHexagons(); i++) {
			int[] hexagon = molecule.getHexagons()[i];
			int y = molecule.getNodesRefs()[hexagon[0]].getY();

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

		@SuppressWarnings("unchecked")
		Triplet<Double, Double, Double>[] carbons = new Triplet[molecule.getNbNodes()];

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

				if (index == 0) {
					if (carbons[u] == null) {
						xu = (double) molecule.getNodesRefs()[u].getX();
						yu = (double) molecule.getNodesRefs()[u].getY();
					} else {
						xu = (double) carbons[u].getX();
						yu = (double) carbons[u].getY();
					}

					xv = xu + 1.2145;
					yv = yu + 0.694;
				}

				else if (index == 1) {

					if (carbons[u] == null) {
						xu = (double) molecule.getNodesRefs()[u].getX();
						yu = (double) molecule.getNodesRefs()[u].getY();
					} else {
						xu = (double) carbons[u].getX();
						yu = (double) carbons[u].getY();
					}

					xv = xu;
					yv = yu + 1.388;

				}

				else if (index == 2) {
					if (carbons[u] == null) {
						xu = (double) molecule.getNodesRefs()[u].getX();
						yu = (double) molecule.getNodesRefs()[u].getY();
					} else {
						xu = (double) carbons[u].getX();
						yu = (double) carbons[u].getY();
					}

					xv = xu - 1.2145;
					yv = yu + 0.694;

				}

				else if (index == 3) {

					if (carbons[u] == null) {
						xu = (double) molecule.getNodesRefs()[u].getX();
						yu = (double) molecule.getNodesRefs()[u].getY();
					} else {
						xu = (double) carbons[u].getX();
						yu = (double) carbons[u].getY();
					}

					xv = xu - 1.2145;
					yv = yu - 0.694;

				}

				else if (index == 4) {

					if (carbons[u] == null) {
						xu = (double) molecule.getNodesRefs()[u].getX();
						yu = (double) (molecule.getNodesRefs()[u].getY());
					} else {
						xu = (double) carbons[u].getX();
						yu = (double) carbons[u].getY();
					}

					xv = xu;
					yv = yu - 1.388;
				}

				else if (index == 5) {

					if (carbons[u] == null) {
						xu = (double) molecule.getNodesRefs()[u].getX();
						yu = (double) molecule.getNodesRefs()[u].getY();
					} else {
						xu = (double) carbons[u].getX();
						yu = (double) carbons[u].getY();
					}

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
		ArrayList<Couple<Integer, Integer>> invalidsCarbons = checkGeometry(molecule);

		int[] hydrogensCorrespondances = new int[molecule.getNbNodes()];
		for (int i = 0; i < molecule.getNbNodes(); i++)
			hydrogensCorrespondances[i] = -1;

		int[] treatedCarbons = new int[molecule.getNbNodes()];

		for (Integer u : carbonsWithHydrogens) {

			if (treatedCarbons[u] == 0) {

				int carbonPair = isInvalid(invalidsCarbons, u);

				// Si le carbone n'est pas problématique
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
					hydrogensCorrespondances[u] = hydrogens.size();
					hydrogens.add(new Triplet<>(xv, yv, 0.0));
				}

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

					hydrogensCorrespondances[u] = hydrogens.size();
					hydrogens.add(new Triplet<>(uHX, uHY, uHZ));

					hydrogensCorrespondances[carbonPair] = hydrogens.size();
					hydrogens.add(new Triplet<>(vHX, vHY, vHZ));

					treatedCarbons[carbonPair] = 1;
				}

				treatedCarbons[u] = 1;
			}
		}

		BufferedWriter writer = new BufferedWriter(new FileWriter(file));

		writer.write("<molecule>" + "\n");

		writer.write("\t<atomArray>\n");

		int idCarbon = 1;
		for (Triplet<Double, Double, Double> carbon : carbons) {

			String atomName = "c" + idCarbon;
			writer.write("\t\t<atom id=\"" + atomName + "\" elementType=\"C\" x3=\"" + carbon.getX() + "\" y3=\""
					+ carbon.getY() + "\" z3=\"" + carbon.getZ() + "\"/>\n");
			idCarbon++;
		}

		int idHydrogen = 1;
		for (Triplet<Double, Double, Double> hydrogen : hydrogens) {

			String atomName = "h" + idHydrogen;
			writer.write("\t\t<atom id=\"" + atomName + "\" elementType=\"H\" x3=\"" + hydrogen.getX() + "\" y3=\""
					+ hydrogen.getY() + "\" z3=\"" + hydrogen.getZ() + "\"/>\n");
			idHydrogen++;
		}

		writer.write("\t</atomArray>\n");

		writer.write("\t<bondArray>\n");

		for (int i = 0; i < molecule.getNbNodes(); i++) {
			for (int j = (i + 1); j < molecule.getNbNodes(); j++) {
				if (molecule.getEdgeMatrix()[i][j] == 1)
					writer.write("\t\t<bond atomRefs2=\"" + "c" + (i + 1) + " c" + (j + 1) + "\" order=\"A\" />\n");
			}
		}

		for (Integer u : carbonsWithHydrogens) {

			String carbonName = "c" + (u + 1);
			String hydrogenName = "h" + (hydrogensCorrespondances[u] + 1);

			writer.write("\t\t<bond atomRefs2=\"" + carbonName + " " + hydrogenName + "\" order=\"S\" />\n");
		}

		writer.write("\t</bondArray>\n");

		writer.write("</molecule>\n");

		writer.close();
	}

}
