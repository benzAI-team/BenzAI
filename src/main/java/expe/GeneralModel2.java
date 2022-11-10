package expe;

import java.util.ArrayList;
import java.util.List;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.extension.Tuples;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMax;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.search.strategy.strategy.IntStrategy;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.objects.setDataStructures.iterable.IntIterableRangeSet;

import generator.GeneralModel;
import generator.fragments.Fragment;
import molecules.Node;
import utils.Couple;
import utils.Utils;

public class GeneralModel2 {

	private static Model model;

	private static int nbHexagons;
	private static int nbCrowns;
	private static int diameter;
	private static int nbHexagonsCoronenoid;

	private static int[][] coordsMatrix;

	private static final int star = -1;

	/*/
	 * 
	 */
	
	private static Couple<Integer, Integer>[] coordsCorrespondance;
	private static int [][] adjacencyMatrix;	
	private static ArrayList<Integer> border;
	
	private static void buildAdjacencyMatrix() {

		adjacencyMatrix = new int[diameter * diameter][diameter * diameter];

		for (int x = 0; x < diameter; x++) {
			for (int y = 0; y < diameter; y++) {

				if (coordsMatrix[x][y] != -1) {

					int u = coordsMatrix[x][y];

					if (x > 0 && y > 0) {

						int v = coordsMatrix[x - 1][y - 1];
						if (v != -1) {
							if (adjacencyMatrix[u][v] == 0) {
								adjacencyMatrix[u][v] = 1;
								adjacencyMatrix[v][u] = 1;
							}
						}
					}

					if (y > 0) {

						int v = coordsMatrix[x][y - 1];
						if (v != -1) {
							if (adjacencyMatrix[u][v] == 0) {
								adjacencyMatrix[u][v] = 1;
								adjacencyMatrix[v][u] = 1;
							}
						}
					}

					if (x + 1 < diameter) {

						int v = coordsMatrix[x + 1][y];
						if (v != -1) {
							if (adjacencyMatrix[u][v] == 0) {
								adjacencyMatrix[u][v] = 1;
								adjacencyMatrix[v][u] = 1;
							}
						}
					}

					if (x + 1 < diameter && y + 1 < diameter) {

						int v = coordsMatrix[x + 1][y + 1];
						if (v != -1) {
							if (adjacencyMatrix[u][v] == 0) {
								adjacencyMatrix[u][v] = 1;
								adjacencyMatrix[v][u] = 1;
							}
						}
					}

					if (y + 1 < diameter) {

						int v = coordsMatrix[x][y + 1];
						if (v != -1) {
							if (adjacencyMatrix[u][v] == 0) {
								adjacencyMatrix[u][v] = 1;
								adjacencyMatrix[v][u] = 1;
							}
						}
					}

					if (x > 0) {

						int v = coordsMatrix[x - 1][y];
						if (v != -1) {
							if (adjacencyMatrix[u][v] == 0) {
								adjacencyMatrix[u][v] = 1;
								adjacencyMatrix[v][u] = 1;
							}
						}
					}
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private static void buildCoordsCorrespondance() {

		coordsCorrespondance = new Couple[diameter * diameter];

		for (int i = 0; i < diameter; i++) {
			for (int j = 0; j < diameter; j++) {

				if (coordsMatrix[i][j] != -1)
					coordsCorrespondance[coordsMatrix[i][j]] = new Couple<>(i, j);
			}
		}
	}
	
	private static Fragment convertToPattern(IntVar [] X) {

		ArrayList<Integer> hexagonsSolutions = new ArrayList<>();

		int[] correspondance = new int[diameter * diameter];

		for (int i = 0; i < correspondance.length; i++)
			correspondance[i] = -1;

		for (int index = 0; index < X.length; index++) {
			if (X[index] != null) {
				if (X[index].getValue() == 1) {
					hexagonsSolutions.add(index);
					correspondance[index] = hexagonsSolutions.size() - 1;
				}
			}
		}

		int nbNodes = hexagonsSolutions.size();

		/*
		 * nodes
		 */

		Node[] nodes = new Node[nbNodes];

		for (int i = 0; i < hexagonsSolutions.size(); i++) {

			int hexagon = hexagonsSolutions.get(i);

			Couple<Integer, Integer> couple = coordsCorrespondance[hexagon];

			nodes[i] = new Node(couple.getY(), couple.getX(), i);
		}

		/*
		 * matrix
		 */

		int[][] matrix = new int[nbNodes][nbNodes];
		int[][] neighbors = new int[nbNodes][6];

		for (int i = 0; i < nbNodes; i++)
			for (int j = 0; j < 6; j++)
				neighbors[i][j] = -1;

		for (int i = 0; i < nbNodes; i++) {

			int u = hexagonsSolutions.get(i);
			Node n1 = nodes[i];

			for (int j = (i + 1); j < nbNodes; j++) {

				int v = hexagonsSolutions.get(j);
				Node n2 = nodes[j];

				if (adjacencyMatrix[u][v] == 1) {

					// Setting matrix
					matrix[i][j] = 1;
					matrix[j][i] = 1;

					// Setting neighbors
					int x1 = n1.getX();
					int y1 = n1.getY();
					int x2 = n2.getX();
					int y2 = n2.getY();

					if (x2 == x1 && y2 == y1 - 1) {
						neighbors[correspondance[u]][0] = correspondance[v];
						neighbors[correspondance[v]][3] = correspondance[u];
					}

					else if (x2 == x1 + 1 && y2 == y1) {
						neighbors[correspondance[u]][1] = correspondance[v];
						neighbors[correspondance[v]][4] = correspondance[u];
					}

					else if (x2 == x1 + 1 && y2 == y1 + 1) {
						neighbors[correspondance[u]][2] = correspondance[v];
						neighbors[correspondance[v]][5] = correspondance[u];
					}

					else if (x2 == x1 && y2 == y1 + 1) {
						neighbors[correspondance[u]][3] = correspondance[v];
						neighbors[correspondance[v]][0] = correspondance[u];
					}

					else if (x2 == x1 - 1 && y2 == y1) {
						neighbors[correspondance[u]][4] = correspondance[v];
						neighbors[correspondance[v]][1] = correspondance[u];
					}

					else if (x2 == x1 - 1 && y2 == y1 - 1) {
						neighbors[correspondance[u]][5] = correspondance[v];
						neighbors[correspondance[v]][2] = correspondance[u];
					}
				}
			}
		}

		/*
		 * Label
		 */

		int[] labels = new int[nbNodes];

		for (int i = 0; i < nbNodes; i++)
			labels[i] = 2;

		return new Fragment(matrix, labels, nodes, null, null, neighbors, 0);
	}
	
	private static Tuples buildTable(int nbNeighbors) {

		Tuples table = new Tuples(true);
		table.setUniversalValue(star);

		int[] tuple0 = new int[nbNeighbors + 1];
		int[] tuple1 = new int[nbNeighbors + 1];

		tuple0[0] = 0;
		tuple1[0] = 1;

		for (int i = 1; i <= nbNeighbors; i++) {
			tuple0[i] = star;
			tuple1[i] = star;
		}

		table.add(tuple0);
		table.add(tuple1);

		for (int n = 2; n <= nbHexagons; n++) {
			for (int position = 1; position <= nbNeighbors; position++) {
				int[] tuple = new int[nbNeighbors + 1];
				tuple[0] = n;
				for (int i = 1; i <= nbNeighbors; i++) {
					tuple[i] = star;
				}
				tuple[position] = n - 1;
				table.add(tuple);
			}
		}

		return table;
	}

	private static void buildCoordsMatrix() {

		nbHexagonsCoronenoid = 0;

		coordsMatrix = new int[diameter][diameter];
		for (int i = 0; i < diameter; i++) {
			for (int j = 0; j < diameter; j++) {
				coordsMatrix[i][j] = -1;
			}
		}

		int index = 0;
		int m = (diameter - 1) / 2;

		int shift = diameter - nbCrowns;

		for (int i = 0; i < m; i++) {

			for (int j = 0; j < diameter - shift; j++) {
				coordsMatrix[i][j] = index;
				index++;
				nbHexagonsCoronenoid++;
			}

			shift--;
		}

		for (int j = 0; j < diameter; j++) {
			coordsMatrix[m][j] = index;
			index++;
			nbHexagonsCoronenoid++;
		}

		shift = 1;

		for (int i = m + 1; i < diameter; i++) {

			for (int j = shift; j < diameter; j++) {
				coordsMatrix[i][j] = index;
				index++;
				nbHexagonsCoronenoid++;
			}

			shift++;
		}
	}

	private static List<IntVar> getNeighbors(int i, int j, IntVar[] Y) {

		List<IntVar> neighbors = new ArrayList<>();

		if (i > 0)
			if (coordsMatrix[i - 1][j] != -1)
				neighbors.add(Y[coordsMatrix[i - 1][j]]);

		if (j < diameter - 1)
			if (coordsMatrix[i][j + 1] != -1)
				neighbors.add(Y[coordsMatrix[i][j + 1]]);

		if (i < diameter - 1 && j < diameter - 1)
			if (coordsMatrix[i + 1][j + 1] != -1)
				neighbors.add(Y[coordsMatrix[i + 1][j + 1]]);

		if (i < diameter - 1)
			if (coordsMatrix[i + 1][j] != -1)
				neighbors.add(Y[coordsMatrix[i + 1][j]]);

		if (j > 0)
			if (coordsMatrix[i][j - 1] != -1)
				neighbors.add(Y[coordsMatrix[i][j - 1]]);

		if (i > 0 && j > 0)
			if (coordsMatrix[i - 1][j - 1] != -1)
				neighbors.add(Y[coordsMatrix[i - 1][j - 1]]);

		return neighbors;
	}
	
	private static boolean has6Neighbors(BoolVar[] neighbors) {
		for (BoolVar x : neighbors)
			if (x == null)
				return false;
		return true;
	}

	public static void postNoHolesOfSize1Constraint(BoolVar [] X) {

		BoolVar[] varClause = new BoolVar[7];
		IntIterableRangeSet[] valClause = new IntIterableRangeSet[7];

		for (int i = 0; i < 6; i++)
			valClause[i] = new IntIterableRangeSet(0);
		valClause[6] = new IntIterableRangeSet(1);

		for (int i = 1; i < diameter - 1; i++) {
			for (int j = 1; j < diameter - 1; j++) {
				if (coordsMatrix[i][j] != -1) {

					BoolVar vertex = X[coordsMatrix[i][j]];
					BoolVar[] neighbors = getNeighbors(i, j, X);

					if (has6Neighbors(neighbors)) {
						for (int l = 0; l < 6; l++)
							varClause[l] = neighbors[l];
						varClause[6] = vertex;
						model.getClauseConstraint().addClause(varClause, valClause);
					}
				}
			}
		}
	}
	
	public static void postBordersConstraints(BoolVar [] X, Model model) {

		if (nbHexagons > 1) {
			
			border = new ArrayList<>();
			BoolVar[] borderVars = new BoolVar[diameter];
			
			IntIterableRangeSet [] valClause = new IntIterableRangeSet[diameter];
			
			for (int i = 0 ; i < diameter ; i++) {
				int j = 0;
				while(coordsMatrix[i][j] == -1)
					j ++;
				borderVars[i] = X[coordsMatrix[i][j]];
				border.add(coordsMatrix[i][j]);
				valClause[i] = new IntIterableRangeSet(1);
			}
			
			model.getClauseConstraint().addClause(borderVars, valClause);

			
		}
		
		else {
			model.arithm(X[0], "=", 1).post();
		}
		
	}
	
	private static void solve() {
		model = new Model("GeneralModel #2");

		BoolVar[] X = new BoolVar[nbHexagonsCoronenoid];
		for (int i = 0; i < nbHexagonsCoronenoid; i++)
			X[i] = model.boolVar("x_" + i);

		IntVar[] Y = new IntVar[nbHexagonsCoronenoid];
		for (int i = 0; i < nbHexagonsCoronenoid; i++)
			Y[i] = model.intVar("y_" + i, 0, nbHexagons);

		IntVar nbHexagonsVar = model.intVar("nbHexagons", nbHexagons);
		model.sum(X, "=", nbHexagonsVar).post();
		model.arithm(nbHexagonsVar, "=", nbHexagons).post();
		
		postNoHolesOfSize1Constraint(X);
		postBordersConstraints(X, model);
		
		model.count(1, Y, model.intVar("limit_count", 1)).post();

		
		for (int i = 0; i < nbHexagonsCoronenoid; i++)
			model.ifOnlyIf(model.arithm(X[i], "=", 0), model.arithm(Y[i], "=", 0));

		for (int i = 0; i < diameter; i++) {
			for (int j = 0; j < diameter; j++) {
				if (coordsMatrix[i][j] != -1) {

					List<IntVar> neighbors = getNeighbors(i, j, Y);
					Tuples table = buildTable(neighbors.size());
					IntVar[] scope = new IntVar[1 + neighbors.size()];
					scope[0] = Y[coordsMatrix[i][j]];

					for (int k = 0; k < neighbors.size(); k++)
						scope[k + 1] = neighbors.get(k);

					model.table(scope, table, "CT+").post();
				}
			}
		}

		Solver solver = model.getSolver();
		
		
		IntVar[] branching = new IntVar[X.length + Y.length];
		int index = 0;
		for (IntVar x : X) {
			branching[index] = x;
			index ++;
		}
		for (IntVar y : Y) {
			branching[index] = y;
			index ++;
		}
		
		solver.setSearch(new IntStrategy(branching, new FirstFail(model), new IntDomainMax()));

		int nbSolutions = 0;
		while (solver.solve()) {
			
			/*
			 * Displaying solution
			 */
			for (int i = 0; i < nbHexagonsCoronenoid; i++) {
				if (X[i].getValue() == 1) {
					System.out.print(i + " ");
				}
			}
			
			System.out.println("(" + nbSolutions + ")");
			
			/*
			 * Nogood
			 */
			
			ArrayList<Integer> vertices = new ArrayList<>();
			for (int i = 0; i < X.length; i++)
				if (X[i].getValue() == 1)
					vertices.add(i);
			
			Fragment pattern = convertToPattern(X);
			ArrayList<Fragment> rotations = pattern.computeRotations();
			
			ArrayList<IntVar[]> nogoods = computeNogoods(model, rotations, nbHexagons, X, nbHexagonsVar);
			
			for (IntVar [] nogood : nogoods) {
				IntIterableRangeSet [] valClause = new IntIterableRangeSet[nogood.length];
				for (int i = 0 ; i < valClause.length ; i++)
					valClause[i] = new IntIterableRangeSet(0);
				model.getClauseConstraint().addClause(nogood, valClause);
			}
		
			
			nbSolutions++;
		}
		
		System.out.println(nbSolutions + " solutions found");
	}

	private static ArrayList<IntVar[]> computeNogoods(Model model, ArrayList<Fragment> rotations, int nbHexagons, BoolVar[] X, IntVar nbHexagonsVar) {
		
		ArrayList<IntVar []> nogoods = new ArrayList<>();
		
		for (Fragment rotation : rotations) {
			for (int xShift = 0 ; xShift <= diameter ; xShift ++) {
				for (int yShift = 0 ; yShift <= diameter ; yShift ++) {
					boolean valid = true;
					for (Node node : rotation.getNodesRefs()) {
						
						if (node == null)
							System.out.println("");
						
						int x = node.getX() + xShift;
						int y = node.getY() + yShift;
						

						
						if (x < 0 || x >= diameter || y < 0 || y >= diameter || coordsMatrix[x][y] == -1) {
							valid = false;
							break;
						}
						/*
						int index = coordsMatrix[x][y];
						
						if (border.contains(index)) {
							valid = false;
							break;
						}*/
					}
					if (valid) {
						
						boolean touchBorder = false;
						//for ()
						
						IntVar [] nogood = new IntVar[nbHexagons + 1];
						for (int i = 0 ; i < nbHexagons ; i++) {
							int x = rotation.getNode(i).getX() + xShift;
							int y = rotation.getNode(i).getY() + yShift;
							
							BoolVar var = X[coordsMatrix[x][y]];
							nogood[i] = var;
						}
						nogood[nogood.length - 1] = model.arithm(nbHexagonsVar, "=", nbHexagons).reify();
						
						nogoods.add(nogood);
					}
				}
			}
		}
		
		return nogoods;
	}
	
	private static void initializeValues(String[] args) {
		nbHexagons = 5;

		nbCrowns = (int) Math.floor((((double) ((double) nbHexagons + 1)) / 2.0) + 1.0);

		if (nbHexagons % 2 == 1)
			nbCrowns--;

		diameter = (2 * nbCrowns) - 1;

		buildCoordsMatrix();
		buildCoordsCorrespondance();
		buildAdjacencyMatrix();
		
	}

	public static BoolVar[] getNeighbors(int i, int j, BoolVar [] X) {

		if (coordsMatrix[i][j] != -1) {

			BoolVar[] N = new BoolVar[6];

			for (int k = 0; k < 6; k++)
				N[k] = null;

			if (i > 0) {
				if (coordsMatrix[i - 1][j] != -1)
					N[0] = X[coordsMatrix[i - 1][j]];
			}

			if (j + 1 < diameter) {
				if (coordsMatrix[i][j + 1] != -1)
					N[1] = X[coordsMatrix[i][j + 1]];
			}

			if (i + 1 < diameter && j + 1 < diameter) {
				if (coordsMatrix[i + 1][j + 1] != -1) {
					N[2] = X[coordsMatrix[i + 1][j + 1]];
				}
			}

			if (i + 1 < diameter) {
				if (coordsMatrix[i + 1][j] != -1)
					N[3] = X[coordsMatrix[i + 1][j]];
			}

			if (j > 0) {
				if (coordsMatrix[i][j - 1] != -1)
					N[4] = X[coordsMatrix[i][j - 1]];
			}

			if (i > 0 && j > 0) {
				if (coordsMatrix[i - 1][j - 1] != -1)
					N[5] = X[coordsMatrix[i - 1][j - 1]];
			}

			return N;
		}

		return null;
	}
	
	private static String displayTable(Tuples table) {
		return table.toString().replace("][", "]\n[").replace("Allowed tuples: {", "").replace("}", "");
	}

	public static void main(String[] args) {

		initializeValues(args);
		solve();

	}
}
