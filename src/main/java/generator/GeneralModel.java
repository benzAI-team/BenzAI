package generator;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMax;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.search.strategy.strategy.IntStrategy;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.UndirectedGraphVar;
import org.chocosolver.solver.variables.Variable;
import org.chocosolver.util.objects.graphs.UndirectedGraph;

import generator.patterns.Pattern;
import generator.patterns.PatternOccurences;
import generator.patterns.PatternResolutionInformations;
import generator.properties.Property;
import generator.properties.solver.SolverProperty;
import generator.properties.solver.SolverPropertySet;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import modelProperty.ModelProperty;
import modelProperty.ModelPropertySet;
import modelProperty.expression.ParameterizedExpression;
import molecules.Molecule;
import molecules.Node;
import nogood.NoGoodBorderRecorder;
import nogood.NoGoodHorizontalAxisRecorder;
import nogood.NoGoodNoneRecorder;
import nogood.NoGoodRecorder;
import nogood.NoGoodUniqueRecorder;
import nogood.NoGoodVerticalAxisRecorder;
import solution.BenzenoidSolution;
import utils.Couple;
import utils.Triplet;
import view.generator.GeneratorPane;
import view.generator.Stopper;
import view.generator.boxes.HBoxCriterion;
import view.generator.boxes.HBoxModelCriterion;
import view.generator.boxes.HBoxSolverCriterion;

public class GeneralModel {

	private Solver solver;
	private SolverResults solverResults;

	private GeneratorRun generatorRun = new GeneratorRun(this);

	private HashMap<String, Integer> variablesDegrees = new HashMap<>();

	/*
	 * Application parameters
	 */

	private int nbMaxHexagons;

	private int[][] neighborGraph;

	private ArrayList<Couple<Integer, Integer>> outterHexagons = new ArrayList<>();
	private ArrayList<Integer> outterHexagonsIndexes = new ArrayList<>();

	private ArrayList<ArrayList<Integer>> neighborGraphOutterHexagons;

	private Couple<Integer, Integer>[] coordsCorrespondance;

	private PatternResolutionInformations patternsInformations;

	/*
	 * Parameters
	 */

	// Don't used for regular solving
	private boolean applySymmetriesConstraints = true;
	private boolean applyBorderConstraints = true;

	private int nbCrowns;
	private int nbHexagons;
	private int diameter;

	private int nbEdges;
	private int nbClausesLexLead = 0;

	boolean verbose = false;

	private int indexOutterHexagon;

	/*
	 * Constraint programming variables
	 */

	private Model chocoModel = new Model("Benzenoides");

	private Node[] nodesRefs;

	private int[][] coordsMatrix;
	private int[] hexagonsCorrespondances;
	private int[] correspondancesHexagons;

	private int nbHexagonsCoronenoid;

	private int[][] adjacencyMatrix;
	private int[][] adjacencyMatrixWithOutterHexagons;

	private UndirectedGraph GUB;
	private UndirectedGraph GLB;

	private UndirectedGraphVar benzenoid;
	private BoolVar[] channeling;
	private BoolVar[] benzenoidVertices;
	private BoolVar[][] benzenoidEdges;

	private ArrayList<Variable> variables = new ArrayList<Variable>();

	private IntVar nbVertices;
	private BoolVar[] edges;
	private int[][] matrixEdges;

	private BoolVar[] nbHexagonsReifies;

	ArrayList<BenzenoidSolution> benzenoidSolutions = new ArrayList<>();

	private ArrayList<Pattern> nogoodsFragments = new ArrayList<>();
	private ArrayList<ArrayList<Integer>> nogoods = new ArrayList<>();

	private SimpleIntegerProperty nbTotalSolutions = new SimpleIntegerProperty(0);
	private int indexSolution;

	private ArrayList<Integer> topBorder;
	private ArrayList<Integer> leftBorder;

	/*
	 * Properties
	 */

	private ModelPropertySet modelPropertySet = new ModelPropertySet();
	private static SolverPropertySet solverPropertySet = new SolverPropertySet();

	/*
	 * Constructors
	 */

	public GeneralModel(ModelPropertySet modelPropertySet) {
		this.modelPropertySet = modelPropertySet;
		nbMaxHexagons = modelPropertySet.computeHexagonNumberUpperBound();
		nbCrowns = modelPropertySet.computeNbCrowns();
		diameter = (2 * nbCrowns) - 1;
		applySymmetriesConstraints = modelPropertySet.symmetryConstraintsAppliable();
		initialize();
	}

	public GeneralModel(ModelPropertySet modelPropertySet, int nbCrowns) {
		this.modelPropertySet = modelPropertySet;
		nbMaxHexagons = modelPropertySet.computeHexagonNumberUpperBound();
		this.nbCrowns = nbCrowns;
		diameter = (2 * nbCrowns) - 1;
		applySymmetriesConstraints = modelPropertySet.symmetryConstraintsAppliable();
		initialize();
	}


//	public GeneralModel(NbCrowns nbCrowns) {
//		this.nbCrowns = nbCrowns.getValue();
//		nbHexagons = -1;		
//		diameter = (2 * this.nbCrowns) - 1;
//		initialize();
//	}
//
//	public GeneralModel(NbHexagons nbHexagons) {
//		this.nbHexagons = nbHexagons.getValue();		
//		this.nbCrowns = (this.nbHexagons + 1) / 2;
//		diameter = this.nbHexagons;
//		initialize();
//	}

	
	/*
	 * Initialization methods
	 */

	private void initialize() {
		initializeMatrix();
		initializeVariables();
		initializeConstraints();
		buildNodesRefs();
		System.out.print("");
	}

	private void initializeMatrix() {
		coordsMatrix = new int[diameter][diameter];
		for (int i = 0; i < diameter; i++) {
			for (int j = 0; j < diameter; j++) {
				coordsMatrix[i][j] = -1;
			}
		}
	}

	private void initializeVariables() {
		System.out.println("00 "+nbMaxHexagons);

		nbHexagonsReifies = new BoolVar[nbMaxHexagons + 1];
		System.out.println("1");

		buildCoordsMatrix();
		System.out.println("2");

		GLB = BoundsBuilder.buildGLB2(this);
		GUB = BoundsBuilder.buildGUB2(this);

		indexOutterHexagon = diameter * diameter;
		System.out.println("3");

		buildAdjacencyMatrix();


		benzenoid = chocoModel.graphVar("g", GLB, GUB);

		System.out.println("4");
		buildBenzenoidVertices();
		System.out.println("5");
		buildBenzenoidEdges();
		System.out.println("6");
		buildCoordsCorrespondance();
		System.out.println("7");
		buildNeighborGraph();

		nbVertices = chocoModel.intVar("nbVertices", 1, nbHexagonsCoronenoid);

		leftBorder = new ArrayList<>();
		topBorder = new ArrayList<>();

		for (int y = 0; y < diameter; y++) {

			if (coordsMatrix[0][y] != -1)
				topBorder.add(correspondancesHexagons[coordsMatrix[0][y]]);
		}

		for (int i = 0; i < diameter; i++) {
			for (int j = 0; j < diameter; j++) {
				if (coordsMatrix[i][j] != -1) {
					leftBorder.add(correspondancesHexagons[coordsMatrix[i][j]]);
					break;
				}
			}
		}
	}

	private void initializeConstraints() {

		chocoModel.connected(benzenoid).post();
		ConstraintBuilder.postFillNodesConnection(this);
		ConstraintBuilder.postNoHolesOfSize1Constraint(this);
		if (applySymmetriesConstraints)
			nbClausesLexLead = ConstraintBuilder.postSymmetryBreakingConstraints(this);
	}

	public void addVariable(Variable variable) {
		variables.add(variable);
	}

	public void addVariable(Variable... variables) {
		for (Variable variable : variables)
			addVariable(variable);
	}

	/***
	 * Apply the model property to the model
	 * @param modelProperty
	 */
	public void applyModelProperty(ModelProperty modelProperty) {
		modelProperty.getModule().build(this, modelProperty.getExpressions());
	}

	/***
	 * Apply all the model properties to the model
	 */
	private void applyModelProperties() {
		for (Property modelProperty : modelPropertySet)
			if(modelPropertySet.has(modelProperty.getId())) {
				applyModelProperty((ModelProperty) modelProperty);
			}


		if (modelPropertySet.has("symmetry") || modelPropertySet.has("rectangle"))
			applyBorderConstraints = false;

		if (applyBorderConstraints)
			ConstraintBuilder.postBordersConstraints(this);
		//TODO deplacer l'instruction ci-dessous
		chocoModel.nbNodes(benzenoid, nbVertices).post();

	}
	
	/***
	 * Apply the solver property to the solver
	 * @param modelProperty
	 */
	public void applySolverProperty(SolverProperty solverProperty) {
		solverProperty.getSpecifier().apply(solver, solverProperty.getExpressions().get(0));
	}

	/***
	 * Apply all the model properties to the model
	 */
	private void applySolverProperties() {
		for (Property solverProperty : solverPropertySet) {
			System.out.println(solverProperty.getId() + ":" + solverPropertySet.has(solverProperty.getId()));
			if(solverPropertySet.has(solverProperty.getId())) {
				applySolverProperty((SolverProperty) solverProperty);
			}
		}
	}
	

	/*
	 * Getters & Setters
	 */

	public int getNbCrowns() {
		return nbCrowns;
	}

	public int getNbHexagons() {
		return nbHexagons;
	}

	public int getDiameter() {
		return diameter;
	}

	public Model getProblem() {
		return chocoModel;
	}

	public int[][] getCoordsMatrix() {
		return coordsMatrix;
	}

	public int[][] getAdjacencyMatrix() {
		return adjacencyMatrix;
	}

	public BoolVar[] getEdges() {
		return edges;
	}

	public BoolVar getEdge(int u, int v) {
		return edges[matrixEdges[u][v]];
	}

	public BoolVar[] getVG() {
		return benzenoidVertices;
	}

	public UndirectedGraphVar getXG() {
		return benzenoid;
	}

	public UndirectedGraphVar getGraphVar() {
		return benzenoid;
	}

	public BoolVar[][] getBenzenoidEdges() {
		return benzenoidEdges;
	}

	public BoolVar[] getGraphVertices() {
		return benzenoidVertices;
	}

	public void setGraphVertices(BoolVar[] graphVertices) {
		benzenoidVertices = graphVertices;
	}

	public void setGUB(UndirectedGraph GUB) {
		this.GUB = GUB;
	}

	public UndirectedGraphVar benzenoidGraphVar(String name) {

		UndirectedGraph GLB = BoundsBuilder.buildGLB2(this);
		UndirectedGraph GUB = BoundsBuilder.buildGUB2(this);

		return chocoModel.graphVar(name, GLB, GUB);
	}

	public UndirectedGraphVar benzenoidGraphVar(String name, UndirectedGraph GLB, UndirectedGraph GUB) {
		return chocoModel.graphVar(name, GLB, GUB);
	}

	/*
	 * Solving methods
	 */

	public void displaySolution(Solver solver) {

		for (int index = 0; index < channeling.length; index++) {
			if (channeling[index].getValue() == 1)
				System.out.print(index + " ");
		}

		System.out.println("");

		for (Variable x : variables)
			if (x.getName().equals("XI"))
				System.out.println(x.getName() + " = " + (double) ((((IntVar) x).getValue())) / 100);
			else
				System.out.println(x.getName() + " = " + (((IntVar) x).getValue()));

		System.out.println(solver.getDecisionPath());
		
		if (!verbose)
			System.out.println("");

		System.out.println(this.getProblem().getSolver().getDecisionPath());
		System.out.println(this.getProblem().getSolver().getFailCount() + " fails");
	}

	public String buildDescription(int index) {

		StringBuilder builder = new StringBuilder();
		builder.append("solution " + index + "\n");

		for (Variable x : variables)

			if (x.getName().equals("XI")) {

				double value = (double) ((((IntVar) x).getValue())) / 100;
				NumberFormat formatter = new DecimalFormat("#0.00");
				builder.append(x.getName() + " = " + formatter.format(value).replace(",", ".") + "\n");

			} else
				builder.append(x.getName() + " = " + (((IntVar) x).getValue()) + "\n");

		return builder.toString();
	}

	@SuppressWarnings("unchecked")
	private void buildCoordsCorrespondance() {

		coordsCorrespondance = new Couple[diameter * diameter];

		for (int i = 0; i < diameter; i++) {
			for (int j = 0; j < diameter; j++) {

				if (coordsMatrix[i][j] != -1)
					coordsCorrespondance[coordsMatrix[i][j]] = new Couple<>(i, j);
			}
		}
	}

	private Pattern convertToPattern() {

		ArrayList<Integer> hexagonsSolutions = new ArrayList<>();

		int[] correspondance = new int[diameter * diameter];

		for (int i = 0; i < correspondance.length; i++)
			correspondance[i] = -1;

		for (int index = 0; index < benzenoidVertices.length; index++) {
			if (benzenoidVertices[index] != null) {
				if (benzenoidVertices[index].getValue() == 1) {
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

		return new Pattern(matrix, labels, nodes, null, null, neighbors, 0);
	}

	private void recordNoGoods() {

		ArrayList<Integer> vertices = new ArrayList<>();
		for (int i = 0; i < channeling.length; i++)
			if (channeling[i].getValue() == 1)
				vertices.add(i);

		int center = correspondancesHexagons[coordsMatrix[(diameter - 1) / 2][(diameter - 1) / 2]];

		Solution solution = new Solution(nodesRefs, correspondancesHexagons, hexagonsCorrespondances, coordsMatrix,
				center, nbCrowns, vertices);

		NoGoodRecorder noGoodRecorder = null;

		if (!modelPropertySet.has("symmetry")) {

			solution.setPattern(convertToPattern());
			noGoodRecorder = new NoGoodBorderRecorder(this, solution, topBorder, leftBorder);
		}

		else {

			noGoodRecorder = new NoGoodNoneRecorder(this, solution);

			if (((ParameterizedExpression)((ModelProperty) modelPropertySet.getById("symmetry")).getExpressions().get(0)).getOperator() == "SYMM_MIRROR")
				noGoodRecorder = new NoGoodHorizontalAxisRecorder(this, solution);
			else if (((ParameterizedExpression)((ModelProperty) modelPropertySet.getById("symmetry")).getExpressions().get(0)).getOperator() == "SYMM_VERTICAL")
				//noGoodRecorder = new NoGoodHorizontalAxisRecorder(this, solution);
				noGoodRecorder = new NoGoodVerticalAxisRecorder(this, solution);

			else {
				if (modelPropertySet.has("pattern"))
					noGoodRecorder = new NoGoodUniqueRecorder(this, solution);
			}

		}

		noGoodRecorder.record();

	}

	public SolverResults solve() {

		applyModelProperties();
		chocoModel.getSolver().setSearch(new IntStrategy(channeling, new FirstFail(chocoModel), new IntDomainMax()));

		for (Property modelProperty : modelPropertySet) {
			if(((ModelProperty) modelProperty).hasExpressions())
				((ModelProperty) modelProperty).getModule().changeSolvingStrategy();
		}

		solver = chocoModel.getSolver();
		solver.limitSearch(() -> {
			return Stopper.STOP;
		});

		//applySolverProperties();
		for(Property solverProperty : solverPropertySet)
			if(((SolverProperty) solverProperty).hasExpressions())
				((SolverProperty) solverProperty).getSpecifier().apply(solver, ((SolverProperty) solverProperty).getExpressions().get(0));

		solverResults = new SolverResults();

		indexSolution = 0;

		long begin = System.currentTimeMillis();

		solver.limitSearch(() -> {
			return Stopper.STOP;
		});
		Stopper.STOP = false;

		while (solver.solve() && !generatorRun.isPaused()) {
			ArrayList<Integer> verticesSolution = buildVerticesSolution();
			String description = buildDescription(indexSolution);
			Molecule molecule = GeneratorPane.buildMolecule(description, nbCrowns, indexSolution, verticesSolution);

			if(molecule.respectPostProcessing(modelPropertySet)) {
				solverResults.addMolecule(molecule);
				Platform.runLater(()->{
					nbTotalSolutions.set(nbTotalSolutions.get() + 1);
				});
				recordNoGoods();

				BenzenoidSolution solverSolution = new BenzenoidSolution(GUB, nbCrowns,
						chocoModel.getName() + indexSolution, hexagonsCorrespondances);

				solverResults.addSolution(solverSolution, description, nbCrowns);
				solverResults.addVerticesSolution(verticesSolution);

				displaySolution(solver);

				if (verbose) {

					System.out.println("NO-GOOD");

					for (ArrayList<Integer> ng : nogoods) {
						for (Integer v : ng)
							System.out.println(v + " ");
					}
				} else
					verticesSolution.add(0);
				
				indexSolution++;
			}

			//displaySolution(solver);
//			System.out.println(solver.getDecisionPath());

		}

		long end = System.currentTimeMillis();
		long time = end - begin;

		solverResults.setTime(time);
		solverResults.setNbTotalSolution(nbTotalSolutions.get());
		solverResults.setSolver(solver);

		System.out.println(nbCrowns + " crowns");
		System.out.println(nogoods.size() + " no-good clauses");
		System.out.println(nbClausesLexLead + " lex-lead clauses");
		solver.printStatistics();

		solverResults.setNogoodsFragments(nogoodsFragments);
		System.out.println("------");
		displayDegrees();
		return solverResults;


	}

	private ArrayList<Integer> buildVerticesSolution() {
		ArrayList<Integer> verticesSolution = new ArrayList<>();

		for (int index = 0; index < benzenoidVertices.length; index++) {

			if (benzenoidVertices[index] != null) {
				verticesSolution.add(benzenoidVertices[index].getValue());
			} else
				verticesSolution.add(0);
		}
		return verticesSolution;
	}

	private void buildAdjacencyMatrix() {

		nbEdges = 0;
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
								nbEdges++;
							}
						}
					}

					if (y > 0) {

						int v = coordsMatrix[x][y - 1];
						if (v != -1) {
							if (adjacencyMatrix[u][v] == 0) {
								adjacencyMatrix[u][v] = 1;
								adjacencyMatrix[v][u] = 1;
								nbEdges++;
							}
						}
					}

					if (x + 1 < diameter) {

						int v = coordsMatrix[x + 1][y];
						if (v != -1) {
							if (adjacencyMatrix[u][v] == 0) {
								adjacencyMatrix[u][v] = 1;
								adjacencyMatrix[v][u] = 1;
								nbEdges++;
							}
						}
					}

					if (x + 1 < diameter && y + 1 < diameter) {

						int v = coordsMatrix[x + 1][y + 1];
						if (v != -1) {
							if (adjacencyMatrix[u][v] == 0) {
								adjacencyMatrix[u][v] = 1;
								adjacencyMatrix[v][u] = 1;
								nbEdges++;
							}
						}
					}

					if (y + 1 < diameter) {

						int v = coordsMatrix[x][y + 1];
						if (v != -1) {
							if (adjacencyMatrix[u][v] == 0) {
								adjacencyMatrix[u][v] = 1;
								adjacencyMatrix[v][u] = 1;
								nbEdges++;
							}
						}
					}

					if (x > 0) {

						int v = coordsMatrix[x - 1][y];
						if (v != -1) {
							if (adjacencyMatrix[u][v] == 0) {
								adjacencyMatrix[u][v] = 1;
								adjacencyMatrix[v][u] = 1;
								nbEdges++;
							}
						}
					}
				}
			}
		}
	}

	public int getNbEdges() {
		return nbEdges;
	}

	public UndirectedGraphVar getBenzenoid() {
		return benzenoid;
	}

	public void setApplyBorderConstraints(boolean apply) {
		applyBorderConstraints = apply;
	}

	public ArrayList<Couple<Integer, Integer>> getOutterHexagons() {
		return outterHexagons;
	}

	public ArrayList<Integer> getOutterHexagonsIndexes() {
		return outterHexagonsIndexes;
	}

//	public ArrayList<Module> getModules() {
//		return modules;
//	}

	public void buildNeighborGraphWithOutterHexagons(int order) {

		if (order == 0) {
			// buildNeighborGraph();
			neighborGraphOutterHexagons = new ArrayList<ArrayList<Integer>>();

			for (int i = 0; i < neighborGraph.length; i++) {
				ArrayList<Integer> neighbors = new ArrayList<Integer>();
				for (int j = 0; j < 6; j++)
					neighbors.add(neighborGraph[i][j]);
				neighborGraphOutterHexagons.add(neighbors);
			}
		}

		else {

			ArrayList<Integer> hexagons = new ArrayList<Integer>();
			ArrayList<Couple<Integer, Integer>> coords = new ArrayList<>();

			int index = -1;

			for (int i = 0; i < diameter; i++) {
				for (int j = 0; j < diameter; j++) {
					if (coordsMatrix[i][j] > index)
						index = coordsMatrix[i][j];
				}
			}

			index++;

			for (int line = 0; line < diameter; line++) {

				if (line == 0 || line == diameter - 1) {

					for (int column = 0; column < diameter; column++) {
						if (coordsMatrix[line][column] != -1) {
							hexagons.add(coordsMatrix[line][column]);
							coords.add(new Couple<>(column, line));
						}
					}
				}

				else {

					ArrayList<Integer> c1 = getFirstColumns(line, 1);
					ArrayList<Integer> c2 = getLastColumns(line, 1);

					for (int i = 0; i < c1.size(); i++) {

						hexagons.add(coordsMatrix[line][c1.get(i)]);
						coords.add(new Couple<>(c1.get(i), line));
						hexagons.add(coordsMatrix[line][c2.get(i)]);
						coords.add(new Couple<>(c2.get(i), line));
					}
				}
			}

			// buildNeighborGraph();

			neighborGraphOutterHexagons = new ArrayList<>();

			for (int i = 0; i < neighborGraph.length; i++) {
				ArrayList<Integer> neighbors = new ArrayList<>();
				for (int j = 0; j < 6; j++)
					neighbors.add(neighborGraph[i][j]);
				neighborGraphOutterHexagons.add(neighbors);
			}

			ArrayList<Triplet<Integer, Integer, Integer>> coordsOutterHexagons = new ArrayList<>();

			for (int i = 0; i < hexagons.size(); i++) {

				int hexagon = hexagons.get(i);
				Couple<Integer, Integer> coord = coords.get(i);

				int[] neighbors = neighborGraph[hexagon];

				for (int j = 0; j < 6; j++) {
					if (neighbors[j] == -1) {

						int x, y;

						if (j == 0) {
							x = coord.getX();
							y = coord.getY() - 1;
						}

						else if (j == 1) {
							x = coord.getX() + 1;
							y = coord.getY();
						}

						else if (j == 2) {
							x = coord.getX() + 1;
							y = coord.getY() + 1;
						}

						else if (j == 3) {
							x = coord.getX();
							y = coord.getY() + 1;
						}

						else if (j == 4) {
							x = coord.getX() - 1;
							y = coord.getY();
						}

						else {
							x = coord.getX() - 1;
							y = coord.getY() - 1;
						}

						int indexOutter = -1;

						for (Triplet<Integer, Integer, Integer> coordOutter : coordsOutterHexagons)
							if (coordOutter.getX() == x && coordOutter.getY() == y)
								indexOutter = coordOutter.getZ();

						if (indexOutter == -1) {

							indexOutter = neighborGraphOutterHexagons.size();

							coordsOutterHexagons.add(new Triplet<Integer, Integer, Integer>(x, y, indexOutter));

							ArrayList<Integer> newNeighbor = new ArrayList<>();
							for (int k = 0; k < 6; k++)
								newNeighbor.add(-1);

							neighborGraphOutterHexagons.add(newNeighbor);
						}

						neighborGraphOutterHexagons.get(hexagon).set(j, indexOutter);
						neighborGraphOutterHexagons.get(indexOutter).set((j + 3) % 6, hexagon);
					}
				}
			}

			for (int i = 0; i < coordsOutterHexagons.size(); i++) {

				Triplet<Integer, Integer, Integer> coord1 = coordsOutterHexagons.get(i);

				int x1 = coord1.getX();
				int y1 = coord1.getY();

				int index1 = coord1.getZ();

				for (int j = 0; j < coordsOutterHexagons.size(); j++) {

					if (i != j) {

						Triplet<Integer, Integer, Integer> coord2 = coordsOutterHexagons.get(j);

						int x2 = coord2.getX();
						int y2 = coord2.getY();

						int index2 = coord2.getZ();

						if (x2 == x1 && y2 == y1 - 1) {
							neighborGraphOutterHexagons.get(index1).set(0, index2);
							neighborGraphOutterHexagons.get(index2).set(3, index1);
						}

						else if (x2 == x1 + 1 && y2 == y1) {
							neighborGraphOutterHexagons.get(index1).set(1, index2);
							neighborGraphOutterHexagons.get(index2).set(4, index1);
						}

						else if (x2 == x1 + 1 && y2 == y1 + 1) {
							neighborGraphOutterHexagons.get(index1).set(2, index2);
							neighborGraphOutterHexagons.get(index2).set(5, index1);
						}

						else if (x2 == x1 && y2 == y1 + 1) {
							neighborGraphOutterHexagons.get(index1).set(3, index2);
							neighborGraphOutterHexagons.get(index2).set(0, index1);
						}

						else if (x2 == x1 - 1 && y2 == y1) {
							neighborGraphOutterHexagons.get(index1).set(4, index2);
							neighborGraphOutterHexagons.get(index2).set(1, index1);
						}

						else if (x2 == x1 - 1 && y2 == y1 - 1) {
							neighborGraphOutterHexagons.get(index1).set(5, index2);
							neighborGraphOutterHexagons.get(index2).set(2, index1);
						}
					}

				}
			}
		}
	}

	private ArrayList<Integer> getFirstColumns(int line, int order) {

		ArrayList<Integer> columns = new ArrayList<>();

		int nbColumns = 0;
		int column = 0;

		while (nbColumns < order && column < diameter) {

			if (coordsMatrix[line][column] != -1) {
				columns.add(column);
				nbColumns++;
			}

			column++;
		}

		return columns;
	}

	private ArrayList<Integer> getLastColumns(int line, int order) {

		ArrayList<Integer> columns = new ArrayList<>();

		int nbColumns = 0;
		int column = diameter - 1;

		while (nbColumns < order && column >= 0) {

			if (coordsMatrix[line][column] != -1) {
				columns.add(column);
				nbColumns++;
			}

			column--;
		}

		return columns;
	}

	private void buildNeighborGraph() {

		neighborGraph = new int[diameter * diameter][6];

		for (int i = 0; i < neighborGraph.length; i++) {
			for (int j = 0; j < neighborGraph[i].length; j++) {
				neighborGraph[i][j] = -1;
			}
		}

		for (int line = 0; line < coordsMatrix.length; line++) {
			for (int column = 0; column < coordsMatrix[line].length; column++) {

				if (coordsMatrix[line][column] != -1) {

					int index = coordsMatrix[line][column];

					// High-Right
					if (line > 0)
						neighborGraph[index][0] = coordsMatrix[line - 1][column];

					// Right
					if (column < coordsMatrix[line].length - 1)
						neighborGraph[index][1] = coordsMatrix[line][column + 1];

					// Down-Right
					if (line < coordsMatrix[line].length - 1 && column < coordsMatrix[line].length - 1)
						neighborGraph[index][2] = coordsMatrix[line + 1][column + 1];

					// Down-Left
					if (line < coordsMatrix[line].length - 1)
						neighborGraph[index][3] = coordsMatrix[line + 1][column];

					// Left
					if (column > 0)
						neighborGraph[index][4] = coordsMatrix[line][column - 1];

					// High-Left
					if (line > 0 && column > 0)
						neighborGraph[index][5] = coordsMatrix[line - 1][column - 1];
				}
			}
		}
	}

	public void buildAdjacencyMatrixWithOutterHexagons() {

		adjacencyMatrixWithOutterHexagons = new int[neighborGraphOutterHexagons.size()][neighborGraphOutterHexagons
				.size()];

		for (int i = 0; i < neighborGraphOutterHexagons.size(); i++) {

			int u = i;

			for (int j = 0; j < 6; j++) {

				int v = neighborGraphOutterHexagons.get(u).get(j);

				if (v != -1) {
					adjacencyMatrixWithOutterHexagons[u][v] = 1;
					adjacencyMatrixWithOutterHexagons[v][u] = 1;
				}
			}
		}
	}

	public ArrayList<ArrayList<Integer>> getNeighborGraphOutterHexagons() {
		return neighborGraphOutterHexagons;
	}

	public int[][] getAdjacencyMatrixOutterHexagons() {
		return adjacencyMatrixWithOutterHexagons;
	}

	private void buildBenzenoidVertices() {

		channeling = new BoolVar[nbHexagonsCoronenoid];
		for (int i = 0; i < channeling.length; i++)
			channeling[i] = chocoModel.boolVar("vertex[" + i + "]");

		chocoModel.nodesChanneling(benzenoid, channeling).post();

		benzenoidVertices = new BoolVar[diameter * diameter];

		int index = 0;

		for (int i = 0; i < diameter; i++) {
			for (int j = 0; j < diameter; j++) {
				if (coordsMatrix[i][j] != -1) {

					BoolVar x = channeling[correspondancesHexagons[coordsMatrix[i][j]]];
					benzenoidVertices[index] = x;
				}

				index++;
			}
		}
	}

	private void buildBenzenoidEdges() {

		benzenoidEdges = new BoolVar[nbHexagonsCoronenoid][nbHexagonsCoronenoid];

		for (int i = 0; i < adjacencyMatrix.length; i++) {
			for (int j = (i + 1); j < adjacencyMatrix.length; j++) {

				if (adjacencyMatrix[i][j] == 1) {

					int u = correspondancesHexagons[i];
					int v = correspondancesHexagons[j];

					BoolVar x = chocoModel.boolVar("e_" + u + "_" + v);
					benzenoidEdges[u][v] = x;
					benzenoidEdges[v][u] = x;

					chocoModel.edgeChanneling(benzenoid, x, u, v).post();
					// chocoModel.edgeChanneling(benzenoid, x, v, u).post();
				}
			}
		}
	}

	private void buildCoordsMatrix() {

		coordsMatrix = new int[diameter][diameter];
		nbHexagonsCoronenoid = 0;

		for (int i = 0; i < diameter; i++)
			for (int j = 0; j < diameter; j++)
				coordsMatrix[i][j] = -1;

		int index = 0;
		int m = (diameter - 1) / 2;

		int shift = diameter - nbCrowns;

		for (int i = 0; i < m; i++) {

			for (int j = 0; j < diameter - shift; j++) {
				coordsMatrix[i][j] = index;
				index++;
				nbHexagonsCoronenoid++;
			}

			for (int j = diameter - shift; j < diameter; j++)
				index++;

			shift--;
		}

		for (int j = 0; j < diameter; j++) {
			coordsMatrix[m][j] = index;
			index++;
			nbHexagonsCoronenoid++;
		}

		shift = 1;

		for (int i = m + 1; i < diameter; i++) {

			for (int j = 0; j < shift; j++)
				index++;

			for (int j = shift; j < diameter; j++) {
				coordsMatrix[i][j] = index;
				index++;
				nbHexagonsCoronenoid++;
			}

			shift++;
		}

		hexagonsCorrespondances = new int[nbHexagonsCoronenoid];
		index = 0;

		for (int i = 0; i < diameter; i++) {
			for (int j = 0; j < diameter; j++) {
				if (coordsMatrix[i][j] != -1) {
					hexagonsCorrespondances[index] = coordsMatrix[i][j];
					index++;
				}
			}
		}

		correspondancesHexagons = new int[diameter * diameter];

		for (int i = 0; i < correspondancesHexagons.length; i++)
			correspondancesHexagons[i] = -1;

		for (int i = 0; i < hexagonsCorrespondances.length; i++)
			correspondancesHexagons[hexagonsCorrespondances[i]] = i;

	}

	public int getNbHexagonsCoronenoid() {
		return nbHexagonsCoronenoid;
	}

	public int[] getHexagonsCorrespondances() {
		return hexagonsCorrespondances;
	}

	public int[] getCorrespondancesHexagons() {
		return correspondancesHexagons;
	}

	public BoolVar[] getNeighbors(int i, int j) {

		if (coordsMatrix[i][j] != -1) {

			BoolVar[] N = new BoolVar[6];

			for (int k = 0; k < 6; k++)
				N[k] = null;

			if (i > 0) {
				if (coordsMatrix[i - 1][j] != -1)
					N[0] = benzenoidVertices[coordsMatrix[i - 1][j]];
			}

			if (j + 1 < diameter) {
				if (coordsMatrix[i][j + 1] != -1)
					N[1] = benzenoidVertices[coordsMatrix[i][j + 1]];
			}

			if (i + 1 < diameter && j + 1 < diameter) {
				if (coordsMatrix[i + 1][j + 1] != -1) {
					N[2] = benzenoidVertices[coordsMatrix[i + 1][j + 1]];
				}
			}

			if (i + 1 < diameter) {
				if (coordsMatrix[i + 1][j] != -1)
					N[3] = benzenoidVertices[coordsMatrix[i + 1][j]];
			}

			if (j > 0) {
				if (coordsMatrix[i][j - 1] != -1)
					N[4] = benzenoidVertices[coordsMatrix[i][j - 1]];
			}

			if (i > 0 && j > 0) {
				if (coordsMatrix[i - 1][j - 1] != -1)
					N[5] = benzenoidVertices[coordsMatrix[i - 1][j - 1]];
			}

			return N;
		}

		return null;
	}

	public BoolVar[] getChanneling() {
		return channeling;
	}

	public void setChanneling(BoolVar[] channeling) {
		this.channeling = channeling;
	}

	public void setGraphVar(UndirectedGraphVar graphVar) {
		benzenoid = graphVar;
	}

	public int[][] getNeighborGraph() {
		return neighborGraph;
	}

	public int getNbMaxHexagons() {
		return nbMaxHexagons;
	}

	public IntVar getNbVerticesVar() {
		return nbVertices;
	}

	public void setPatternsInformations(PatternResolutionInformations patternsInformations) {
		this.patternsInformations = patternsInformations;
	}

	public void setApplySymmetriesConstraints(boolean applySymmetriesConstraints) {
		this.applySymmetriesConstraints = applySymmetriesConstraints;
	}

	public SolverResults getResultSolver() {
		return solverResults;
	}

	public GeneratorRun getGeneratorRun() {
		return generatorRun;
	}

	public void stop() {
		generatorRun.stop();
	}

	public boolean isStopped() {
		return generatorRun.isStopped();
	}

	public boolean isPaused() {
		return generatorRun.isPaused();
	}

	public void pause() {
		generatorRun.pause();
	}

	public void resume() {

		while (solver.solve() && !generatorRun.isPaused()) {

			Platform.runLater(()->{
				nbTotalSolutions.set(nbTotalSolutions.get() + 1);
			});

			recordNoGoods();

			BenzenoidSolution solution = new BenzenoidSolution(GUB, nbCrowns, chocoModel.getName() + indexSolution,
					hexagonsCorrespondances);
			String description = buildDescription(indexSolution);
			solverResults.addSolution(solution, description, nbCrowns);

			ArrayList<BoolVar> presentHexagons = new ArrayList<>();
			ArrayList<Integer> verticesSolution = new ArrayList<>();

			for (int index = 0; index < benzenoidVertices.length; index++) {

				if (benzenoidVertices[index] != null) {
					verticesSolution.add(benzenoidVertices[index].getValue());

					if (benzenoidVertices[index].getValue() == 1) {
						presentHexagons.add(benzenoidVertices[index]);
					}

				} else
					verticesSolution.add(0);
			}

			solverResults.addVerticesSolution(verticesSolution);

			displaySolution(solver);
			//System.out.println(solver.getDecisionPath());

			if (verbose) {

				System.out.println("NO-GOOD");

				for (ArrayList<Integer> ng : nogoods) {
					for (Integer v : ng)
						System.out.println(v + " ");
				}

				System.out.println("");
			}

			indexSolution++;
		}

	}

	private void buildNodesRefs() {
		nodesRefs = new Node[nbHexagonsCoronenoid];

		for (int i = 0; i < diameter; i++) {
			for (int j = 0; j < diameter; j++) {
				if (coordsMatrix[i][j] != -1) {
					int index = correspondancesHexagons[coordsMatrix[i][j]];
					nodesRefs[index] = new Node(i, j, index);
					index++;
				}
			}
		}

	}

	private boolean isValid(Couple<Integer, Integer> coord, Pattern fragment, int index) {

		if (coord.getX() < 0 || coord.getX() >= diameter || coord.getY() < 0 || coord.getY() >= diameter
				|| coordsMatrix[coord.getY()][coord.getX()] == -1) {
			if (fragment.getLabel(index) == 2)
				return false;
		}

		return true;
	}

	private int findIndex(ArrayList<Couple<Integer, Integer>> coords, Couple<Integer, Integer> coord) {

		for (int i = 0; i < coords.size(); i++)
			if (coords.get(i).equals(coord))
				return i;

		return -1;
	}

	@SuppressWarnings("unchecked")
	public PatternOccurences computeTranslations(Pattern fragment) {

		PatternOccurences fragmentOccurences = new PatternOccurences();

		/*
		 * Trouver l'hexagone pr�sent du fragment le plus en haut � gauche
		 */

		int minY = Integer.MAX_VALUE;
		for (Node node : fragment.getNodesRefs())
			if (node.getY() < minY)
				minY = node.getY();

		while (true) {

			boolean containsPresentHexagon = false;
			for (int i = 0; i < fragment.getNbNodes(); i++) {
				Node node = fragment.getNodesRefs()[i];
				if (node.getY() == minY && fragment.getLabel(i) == 2)
					containsPresentHexagon = true;
			}

			if (containsPresentHexagon)
				break;

			minY++;
		}

		int nodeIndex = -1;
		int minX = Integer.MAX_VALUE;
		for (int i = 0; i < fragment.getNbNodes(); i++) {
			Node node = fragment.getNodesRefs()[i];
			if (node.getY() == minY && node.getX() < minX && fragment.getLabel(i) == 2) {
				minX = node.getX();
				nodeIndex = i;
			}
		}

		/*
		 * Trouver les positions ou le fragment peut �tre plac�
		 */

		for (int y = 0; y < diameter; y++) {
			for (int x = 0; x < diameter; x++) {
				int hexagon = coordsMatrix[y][x];
				if (hexagon != -1) {

					/*
					 * On place le fragment dans le coron�no�de de telle sorte que firstNode
					 * corresponde � hexagon
					 */

					int[] checkedHexagons = new int[fragment.getNbNodes()];
					Couple<Integer, Integer>[] coords = new Couple[fragment.getNbNodes()];

					int candidat = nodeIndex;
					checkedHexagons[nodeIndex] = 1;
					coords[nodeIndex] = new Couple<>(x, y);

					ArrayList<Integer> candidats = new ArrayList<Integer>();

					for (int i = 0; i < 6; i++) {
						if (fragment.getNeighbor(candidat, i) != -1) {

							int neighbor = fragment.getNeighbor(candidat, i);
							candidats.add(neighbor);
							Couple<Integer, Integer> coord;

							if (i == 0)
								coord = new Couple<>(x, y - 1);

							else if (i == 1)
								coord = new Couple<>(x + 1, y);

							else if (i == 2)
								coord = new Couple<>(x + 1, y + 1);

							else if (i == 3)
								coord = new Couple<>(x, y + 1);

							else if (i == 4)
								coord = new Couple<>(x - 1, y);

							else
								coord = new Couple<>(x - 1, y - 1);

							coords[neighbor] = coord;
							checkedHexagons[neighbor] = 1;
						}
					}

					while (candidats.size() > 0) {

						candidat = candidats.get(0);

						for (int i = 0; i < 6; i++) {
							if (fragment.getNeighbor(candidat, i) != -1) {

								int neighbor = fragment.getNeighbor(candidat, i);

								if (checkedHexagons[neighbor] == 0) {

									candidats.add(neighbor);
									Couple<Integer, Integer> coord;

									if (i == 0)
										coord = new Couple<>(coords[candidat].getX(), coords[candidat].getY() - 1);

									else if (i == 1)
										coord = new Couple<>(coords[candidat].getX() + 1, coords[candidat].getY());

									else if (i == 2)
										coord = new Couple<>(coords[candidat].getX() + 1, coords[candidat].getY() + 1);

									else if (i == 3)
										coord = new Couple<>(coords[candidat].getX(), coords[candidat].getY() + 1);

									else if (i == 4)
										coord = new Couple<>(coords[candidat].getX() - 1, coords[candidat].getY());

									else
										coord = new Couple<>(coords[candidat].getX() - 1, coords[candidat].getY() - 1);

									coords[neighbor] = coord;
									checkedHexagons[neighbor] = 1;
								}
							}
						}

						candidats.remove(candidats.get(0));
					}

					/*
					 * On teste si le fragment obtenu est valide
					 */

					boolean valid = true;
					for (int i = 0; i < coords.length; i++) {
						Couple<Integer, Integer> coord = coords[i];

						if (!isValid(coord, fragment, i))
							valid = false;

					}

					if (valid) {

						Integer[] occurence = new Integer[fragment.getNbNodes()];

						for (int i = 0; i < coords.length; i++) {

							Couple<Integer, Integer> coord = coords[i];

							if (coord.getX() >= 0 && coord.getX() < diameter && coord.getY() >= 0
									&& coord.getY() < diameter) {
								occurence[i] = coordsMatrix[coord.getY()][coord.getX()];

								if (coordsMatrix[coord.getY()][coord.getX()] == -1 && !outterHexagons.contains(coord)) {
									outterHexagons.add(coord);
									outterHexagonsIndexes.add(indexOutterHexagon);
									indexOutterHexagon++;
								}
							} else {
								occurence[i] = -1;

								if (!outterHexagons.contains(coord)) {
									outterHexagons.add(coord);
									outterHexagonsIndexes.add(indexOutterHexagon);
									indexOutterHexagon++;
								}
							}
						}

						ArrayList<Integer> present = new ArrayList<>();
						ArrayList<Integer> absent = new ArrayList<>();
						ArrayList<Integer> unknown = new ArrayList<>();
						ArrayList<Integer> outter = new ArrayList<>();

						for (int i = 0; i < fragment.getNbNodes(); i++) {

							if (fragment.getLabel(i) == 1) {
								Couple<Integer, Integer> coord = coords[i];

								if (coord.getX() >= 0 && coord.getX() < diameter && coord.getY() >= 0
										&& coord.getY() < diameter) {
									if (coordsMatrix[coord.getY()][coord.getX()] == -1) {

										int index = findIndex(outterHexagons, coord);
										outter.add(outterHexagonsIndexes.get(index));
									}

									else {
										unknown.add(coordsMatrix[coord.getY()][coord.getX()]);
									}
								}

								else {
									int index = findIndex(outterHexagons, coord);
									outter.add(outterHexagonsIndexes.get(index));
								}
							}

							else if (fragment.getLabel(i) == 2) {
								Couple<Integer, Integer> coord = coords[i];
								present.add(coordsMatrix[coord.getY()][coord.getX()]);
							}

							else if (fragment.getLabel(i) == 3) {
								Couple<Integer, Integer> coord = coords[i];

								if (coord.getX() >= 0 && coord.getX() < diameter && coord.getY() >= 0
										&& coord.getY() < diameter) {
									if (coordsMatrix[coord.getY()][coord.getX()] == -1) {

										int index = findIndex(outterHexagons, coord);
										outter.add(outterHexagonsIndexes.get(index));
									}

									else {
										absent.add(coordsMatrix[coord.getY()][coord.getX()]);
									}
								}

								else {
									int index = findIndex(outterHexagons, coord);
									outter.add(outterHexagonsIndexes.get(index));
								}
							}
						}

						fragmentOccurences.addOccurence(occurence);
						fragmentOccurences.addCoordinate(coords);
						fragmentOccurences.addOutterHexagons(outter);
						fragmentOccurences.addPresentHexagons(present);
						fragmentOccurences.addAbsentHexagons(absent);
						fragmentOccurences.addUnknownHexagons(unknown);
					}
				}
			}
		}

		return fragmentOccurences;
	}

	public ArrayList<ArrayList<Integer>> getNoGoods() {
		return nogoods;
	}

	public BoolVar getNbHexagonsReified(int index) {
		return nbHexagonsReifies[index];
	}

	public void setNbHexagonsReified(int index, BoolVar value) {
		nbHexagonsReifies[index] = value;
	}

//	@Override
//	public String toString() {
//		return hexagonsCriterions.toString();
//	}

	public Model getChocoModel() {
		return chocoModel;
	}

	public SimpleIntegerProperty getNbTotalSolutions() {
		return nbTotalSolutions;
	}

	public ModelPropertySet getModelPropertySet() {
		return this.modelPropertySet;
	}

	public void setModelPropertySet(ModelPropertySet modelPropertySet) {
		this.modelPropertySet = modelPropertySet;
	}

	public static SolverPropertySet getSolverPropertySet() {
		return GeneralModel.solverPropertySet;
	}

	public static void setSolverPropertySet(SolverPropertySet solverPropertySet) {
		GeneralModel.solverPropertySet = solverPropertySet;
	}

	public static boolean buildSolverPropertySet(ArrayList<HBoxCriterion> hBoxesSolverCriterions) {
		solverPropertySet.clearPropertyExpressions();;
		for (HBoxCriterion box : hBoxesSolverCriterions) {
			((HBoxSolverCriterion)box).addPropertyExpression(solverPropertySet);
		}
		return true;
	}

	public void increaseDegree(String variableName) {
		if (variablesDegrees.get(variableName) == null)
			variablesDegrees.put(variableName, 0);
		int curentDegree = variablesDegrees.get(variableName);
		variablesDegrees.remove(variableName);
		variablesDegrees.put(variableName, curentDegree + 1);
	}

	public int getVariableDegree(String variableName) {
		return variablesDegrees.get(variableName);
	}

	public void displayDegrees() {
		for (Map.Entry<String, Integer> entry : variablesDegrees.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();

			System.out.println("d(" + key + ") = " + value);
		}
	}
}
