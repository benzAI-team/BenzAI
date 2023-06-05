package parsers;

import benzenoid.Benzenoid;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.search.strategy.strategy.IntStrategy;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.UndirectedGraphVar;
import org.chocosolver.util.objects.graphs.UndirectedGraph;
import org.chocosolver.util.objects.setDataStructures.SetType;
import parsers.ComConverter.ComType;
import utils.Couple;
import utils.Triplet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public enum COMConverter2 {
    ;

    public static int isInvalid(ArrayList<Couple<Integer, Integer>> invalidsCarbons, int carbon) {

		for (Couple<Integer, Integer> carbons : invalidsCarbons) {

			if (carbons.getX() == carbon)
				return carbons.getY();

			else if (carbons.getY() == carbon)
				return carbons.getX();
		}

		return -1;
	}
	
	public static ArrayList<ArrayList<Integer>> countCycles(Triplet<Double, Double, Double>[] carbons) {

		ArrayList<ArrayList<Integer>> cycles = new ArrayList<>();

		int[][] matrix = new int[carbons.length][carbons.length];
		int nbEdges = 0;

		for (int i = 0; i < carbons.length; i++) {
			Triplet<Double, Double, Double> c1 = carbons[i];
			for (int j = (i + 1); j < carbons.length; j++) {
				Triplet<Double, Double, Double> c2 = carbons[j];
				double distance = distance(c1, c2);

				if (distance <= 1.4) {
					matrix[i][j] = 1;
					matrix[j][i] = 1;
					nbEdges++;
				}
			}
		}

		Model model = new Model("Cycles");

		int nbCarbons = carbons.length;

		UndirectedGraph GLB = new UndirectedGraph(model, nbCarbons, SetType.BITSET, false);
		UndirectedGraph GUB = new UndirectedGraph(model, nbCarbons, SetType.BITSET, false);

		for (int i = 0; i < nbCarbons; i++) {
			GUB.addNode(i);
			for (int j = (i + 1); j < nbCarbons; j++) {
				if (matrix[i][j] == 1)
					GUB.addEdge(i, j);
			}
		}

		int[] firstVertices = new int[nbEdges];
		int[] secondVertices = new int[nbEdges];

		UndirectedGraphVar g = model.graphVar("g", GLB, GUB);
		BoolVar[] boolEdges = new BoolVar[nbEdges];

		int index = 0;
		for (int i = 0; i < nbCarbons; i++) {
			for (int j = (i + 1); j < nbCarbons; j++) {

				if (matrix[i][j] == 1) {
					boolEdges[index] = model.boolVar("(" + i + "--" + j + ")");
					model.edgeChanneling(g, boolEdges[index], i, j).post();
					firstVertices[index] = i;
					secondVertices[index] = j;
					index++;
				}
			}
		}

		IntVar nbNodes = model.intVar("nb_nodes", 0, nbCarbons);
		model.nbNodes(g, nbNodes).post();
		model.arithm(nbNodes, "=", 6).post();

		model.minDegree(g, 2).post();
		model.maxDegree(g, 2).post();
		model.connected(g).post();

		model.getSolver().setSearch(new IntStrategy(boolEdges, new FirstFail(model), new IntDomainMin()));
		Solver solver = model.getSolver();

		Solution solution;

		while (solver.solve()) {
			solution = new Solution(model);
			solution.record();

			ArrayList<Integer> cycle = new ArrayList<>();

			for (int i = 0; i < boolEdges.length; i++) {
				if (solution.getIntVal(boolEdges[i]) == 1) {
					if (!cycle.contains(firstVertices[i]))
						cycle.add(firstVertices[i]);
					if (!cycle.contains(secondVertices[i]))
						cycle.add(secondVertices[i]);
				}
			}

			cycles.add(cycle);

		}

		return cycles;

	}

	public static ArrayList<Integer> getCarbonsWithHydrogens(Benzenoid molecule) {

		ArrayList<Integer> carbons = new ArrayList<>();

		for (int c = 0; c < molecule.getNbNodes(); c++) {
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

	// sqrt[(Xa-Xb)²+(Ya-Yb)²+(Za-Zb)²]
	public static double distance(Triplet<Double, Double, Double> p1, Triplet<Double, Double, Double> p2) {

		double x1 = p1.getX();
		double y1 = p1.getY();
		double z1 = p1.getZ();

		double x2 = p2.getX();
		double y2 = p2.getY();
		double z2 = p2.getZ();

		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) + (z1 - z2) * (z1 - z2));
	}

	public static ArrayList<Couple<Integer, Integer>> checkInvalidCarbons(Benzenoid molecule,
                                                                          Triplet<Double, Double, Double>[] carbons) {

		ArrayList<Couple<Integer, Integer>> invalidsCarbons = new ArrayList<>();

		for (int i = 0; i < carbons.length; i++) {
			Triplet<Double, Double, Double> c1 = carbons[i];
			for (int j = (i + 1); j < carbons.length; j++) {
				if (molecule.getEdgeMatrix()[i][j] == 0) {
					Triplet<Double, Double, Double> c2 = carbons[j];
					double distance = distance(c1, c2);

					if (distance <= 1.4)
						invalidsCarbons.add(new Couple<>(i, j));
				}
			}
		}

		return invalidsCarbons;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	public static void generateComFile(Benzenoid molecule, File file, int nbElectronsDiff, ComType type, String title)
			throws IOException {

		System.out.println("Treating " + title + ".log");
		
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

		ArrayList<Integer> hexagonsOrder = new ArrayList<>();
		hexagonsOrder.add(hexa);

		ArrayList<Integer> candidates = new ArrayList<>();
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

				if (carbons[u] == null) {
					xu = molecule.getNodesRefs()[u].getX();
					yu = molecule.getNodesRefs()[u].getY();
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
		ArrayList<Couple<Integer, Integer>> badCarbons = checkInvalidCarbons(molecule, carbons);
		ArrayList<Integer> carbonsWithHydrogens = getCarbonsWithHydrogens(molecule);

		ArrayList<ArrayList<Integer>> cycles = countCycles(carbons);

		System.out.println("nb_carbons\texpected: " + molecule.getNbNodes() + "\t got: " + carbons.length);
		System.out.println("nb_cycles\texpected: " + molecule.getNbHexagons() + "\t got: " + cycles.size());

//		for (Integer u : carbonsWithHydrogens) {
//			
//			double xu = carbons[u].getX();
//			double yu = carbons[u].getY();
//
//			int xur = molecule.getNodeRef(u).getX();
//			int yur = molecule.getNodeRef(u).getY();
//
//			double xv = 0, yv = 0;
//			int xvr = 0, yvr = 0;
//
//			Couple<Integer, Integer> couple = findHexagon(molecule, u);
//			int position = couple.getY();
//
//			if (position == 0) {
//				xv = xu;
//				yv = yu - 1.0;
//
//				xvr = xur;
//				yvr = yur - 1;
//			}
//
//			else if (position == 1) {
//
//				xv = xu + 0.8675;
//				yv = yu - 0.4957;
//
//				xvr = xur + 1;
//				yvr = yur - 1;
//			}
//
//			else if (position == 2) {
//
//				xv = xu + 0.8675;
//				yv = yu + 0.4957;
//
//				xvr = xur + 1;
//				yvr = yur + 1;
//			}
//
//			else if (position == 3) {
//
//				xv = xu;
//				yv = yu + 1.0;
//
//				xvr = xur;
//				yvr = yur + 1;
//			}
//
//			else if (position == 4) {
//
//				xv = xu - 0.8675;
//				yv = yu + 0.4957;
//
//				xvr = xur - 1;
//				yvr = yur + 1;
//			}
//
//			else if (position == 5) {
//				xv = xu - 0.8675;
//				yv = yu - 0.4957;
//
//				xvr = xur - 1;
//				yvr = yur - 1;
//			}
//
//			// if (molecule.getCoords().get(xvr, yvr) == -1)
//			hydrogens.add(new Triplet<>(xv, yv, 0.0));
//			
//		}
		
		//System.out.println(hydrogens.size() + " hydrogens");
		
		//ArrayList<Integer> carbonsWithHydrogens = getCarbonsWithHydrogens(molecule);
		//ArrayList<Couple<Integer, Integer>> invalidsCarbons = checkGeometry(molecule, carbonsWithHydrogens);

		//ArrayList<Couple<Integer, Integer>> invalidsCarbons = checkInvalidCarbons()
		
		
		
		int[] treatedCarbons = new int[molecule.getNbNodes()];

		for (Integer u : carbonsWithHydrogens) {

			if (treatedCarbons[u] == 0) {

				int carbonPair = isInvalid(badCarbons, u);

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

		System.out.println(hydrogens.size() + " hydrogens");
		
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

		StringBuilder s = new StringBuilder();

		for (Triplet<Double, Double, Double> carbon : carbons) {
			writer.write(" C " + carbon.getX() + " " + carbon.getY() + " " + carbon.getZ() + "\n");
			s.append(" C ").append(carbon.getX()).append(" ").append(carbon.getY()).append(" ").append(carbon.getZ()).append("\n");
		}

		for (Triplet<Double, Double, Double> hydrogen : hydrogens) {
			writer.write(" H " + hydrogen.getX() + " " + hydrogen.getY() + " " + hydrogen.getZ() + "\n");
			s.append(" H ").append(hydrogen.getX()).append(" ").append(hydrogen.getY()).append(" ").append(hydrogen.getZ()).append("\n");
		}

		writer.write("\n");

		writer.close();
	}

	public static void main(String[] args) throws IOException {
		
		File file = new File("9_hexagons5196.graph_coord");
		
		Benzenoid m = GraphParser.parseUndirectedGraph(file);

		generateComFile(m, new File("9_hexagons5196.com"), 0, ComType.IR, file.getName());
	}
}
