package generator;

import generator.patterns.Pattern;
import generator.patterns.PatternOccurences;
import generator.properties.Property;
import generator.properties.model.ModelProperty;
import generator.properties.model.ModelPropertySet;
import generator.properties.model.expression.ParameterizedExpression;
import generator.properties.solver.SolverProperty;
import generator.properties.solver.SolverPropertySet;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import molecules.Molecule;
import molecules.Node;
import nogood.*;
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
import solution.BenzenoidSolution;
import utils.Couple;
import utils.HexNeighborhood;
import utils.Triplet;
import view.generator.Stopper;
import view.generator.boxes.HBoxCriterion;
import view.generator.boxes.HBoxSolverCriterion;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class GeneralModel {
    private Solver chocoSolver;
    private SolverResults solverResults;

    private final GeneratorRun generatorRun = new GeneratorRun(this);

    private final HashMap<String, Integer> variablesDegrees = new HashMap<>();

    /*
     * Application parameters
     */

    private final int nbMaxHexagons;

    private int[][] neighborIndices;

    private final ArrayList<Couple<Integer, Integer>> outterHexagons = new ArrayList<>();
    private final ArrayList<Integer> outterHexagonsIndexes = new ArrayList<>();

    private ArrayList<ArrayList<Integer>> neighborGraphOutterHexagons;

    private Couple<Integer, Integer>[] coordsCorrespondance;

    /*
     * Parameters
     */

    // Don't use for regular solving
    private final boolean applySymmetriesConstraints;

    private final int nbCrowns;
    //private int nbHexagons;
    private final int diameter;

    private int nbEdges;
    private int nbClausesLexLead = 0;

    boolean verbose = false;

    private int indexOutterHexagon;

    /*
     * Constraint programming variables
     */

    private final Model chocoModel = new Model("Benzenoides");

    private Node[] nodesRefs;

    private int[][] hexagonIndices;
    private int[] hexagonsCorrespondances;
    private int[] correspondancesHexagons;

    private int nbHexagonsCoronenoid;

    private int[][] adjacencyMatrix;
    private int[][] adjacencyMatrixWithOutterHexagons;

    private UndirectedGraph GUB;

    private UndirectedGraphVar benzenoidGraphVar;
    private BoolVar[] hexBoolVars;
    private BoolVar[] benzenoidVerticesBVArray;
    private BoolVar[][] benzenoidEdges;

    private final ArrayList<Variable> variables = new ArrayList<>();

    private IntVar nbVertices;
    //private BoolVar[] edges;

    private BoolVar[] nbHexagonsReifies;

    private final ArrayList<ArrayList<Integer>> nogoods = new ArrayList<>();

    private final SimpleIntegerProperty nbTotalSolutions = new SimpleIntegerProperty(0);
    private int indexSolution;

    private ArrayList<Integer> topBorder;
    private ArrayList<Integer> leftBorder;

    /*
     * Properties
     */

    private ModelPropertySet modelPropertySet;
    private static final SolverPropertySet solverPropertySet = new SolverPropertySet();


    private boolean isInTestMode = false;

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


    /*
     * Initialization methods
     */

    private void initialize() {
        initializeHexagonIndices();
        initializeVariables();
        initializeConstraints();
        buildNodesRefs();
        System.out.print("");
    }

    private void initializeHexagonIndices() {
        hexagonIndices = new int[diameter][diameter];
        for (int lineIndex = 0; lineIndex < diameter; lineIndex++) {
            Arrays.fill(hexagonIndices[lineIndex], -1);
        }
    }

    private void initializeVariables() {
        System.out.println("00 " + nbMaxHexagons);

        nbHexagonsReifies = new BoolVar[nbMaxHexagons + 1];
        System.out.println("1");

        buildHexagonIndices();
        System.out.println("2");

        UndirectedGraph GLB = BoundsBuilder.buildGLB2(this);
        GUB = BoundsBuilder.buildGUB2(this);

        indexOutterHexagon = diameter * diameter;
        System.out.println("3");

        buildAdjacencyMatrix();


        benzenoidGraphVar = chocoModel.graphVar("g", GLB, GUB);

        System.out.println("4");
        buildBenzenoidVertices();
        System.out.println("5");
        buildBenzenoidEdges();
        System.out.println("6");
        buildCoordsCorrespondance();
        System.out.println("7");
        buildNeighborIndices();

        nbVertices = chocoModel.intVar("nbVertices", 1, nbHexagonsCoronenoid);

        leftBorder = new ArrayList<>();
        topBorder = new ArrayList<>();

        for (int columnIndex = 0; columnIndex < diameter; columnIndex++) {

            if (validHexagonIndex(0, columnIndex))
                topBorder.add(correspondancesHexagons[hexagonIndices[0][columnIndex]]);
        }

        for (int lineIndex = 0; lineIndex < diameter; lineIndex++) {
            for (int columnIndex = 0; columnIndex < diameter; columnIndex++) {
                if (validHexagonIndex(lineIndex, columnIndex)) {
                    leftBorder.add(correspondancesHexagons[hexagonIndices[lineIndex][columnIndex]]);
                    break;
                }
            }
        }
    }

    private void initializeConstraints() {
        chocoModel.connected(benzenoidGraphVar).post();
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
     * Apply the model property constraint to the model
     * @param modelProperty : the model property
     */
    public void applyModelConstraint(ModelProperty modelProperty) {
        modelProperty.getConstraint().build(this, modelProperty.getExpressions());
    }

    /***
     * Apply all the model constraints to the model
     */
    private void applyModelConstraints() {
        for (Property modelProperty : modelPropertySet)
            if (modelPropertySet.has(modelProperty.getId())) {
                applyModelConstraint((ModelProperty) modelProperty);
            }

        if (!modelPropertySet.has("symmetry") && !modelPropertySet.has("rectangle"))
            ConstraintBuilder.postBordersConstraints(this);
        //TODO deplacer l'instruction ci-dessous
        chocoModel.nbNodes(benzenoidGraphVar, nbVertices).post();
    }


    /*
     * Getters & Setters
     */

    public int getNbCrowns() {
        return nbCrowns;
    }

    public int getDiameter() {
        return diameter;
    }

    public Model getProblem() {
        return chocoModel;
    }

    public int[][] getHexagonIndices() {
        return hexagonIndices;
    }

    public int getHexagonIndex(int lineIndex, int columnIndex){
        return hexagonIndices[lineIndex][columnIndex];
    }

    public int[][] getAdjacencyMatrix() {
        return adjacencyMatrix;
    }

    public BoolVar[] getBenzenoidVerticesBVArray() {
        return benzenoidVerticesBVArray;
    }

    public BoolVar getBenzenoidVerticesBVArray(int index) {
        return benzenoidVerticesBVArray[index];
    }

    public UndirectedGraphVar getGraphVar() {
        return benzenoidGraphVar;
    }

    public BoolVar[][] getBenzenoidEdges() {
        return benzenoidEdges;
    }


    /*
     * Solving methods
     */

    public void displaySolution(Solver solver) {

        for (int index = 0; index < hexBoolVars.length; index++) {
            if (hexBoolVars[index].getValue() == 1)
                System.out.print(index + " ");
        }

        System.out.println();

        for (Variable x : variables)
            if ("XI".equals(x.getName()))
                System.out.println(x.getName() + " = " + (double) ((((IntVar) x).getValue())) / 100);
            else
                System.out.println(x.getName() + " = " + (((IntVar) x).getValue()));

        System.out.println(solver.getDecisionPath());

        if (!verbose)
            System.out.println();

        System.out.println(this.getProblem().getSolver().getDecisionPath());
        System.out.println(this.getProblem().getSolver().getFailCount() + " fails");
    }

    public String buildDescription(int index) {

        StringBuilder builder = new StringBuilder();
        builder.append("solution ").append(index).append("\n");

        for (Variable x : variables)

            if ("XI".equals(x.getName())) {

                double value = (double) ((((IntVar) x).getValue())) / 100;
                NumberFormat formatter = new DecimalFormat("#0.00");
                builder.append(x.getName()).append(" = ").append(formatter.format(value).replace(",", ".")).append("\n");

            } else
                builder.append(x.getName()).append(" = ").append(((IntVar) x).getValue()).append("\n");

        return builder.toString();
    }

    @SuppressWarnings("unchecked")
    private void buildCoordsCorrespondance() {

        coordsCorrespondance = new Couple[diameter * diameter];

        for (int lineIndex = 0; lineIndex < diameter; lineIndex++) {
            for (int columnIndex = 0; columnIndex < diameter; columnIndex++) {

                if (validHexagonIndex(lineIndex, columnIndex))
                    coordsCorrespondance[hexagonIndices[lineIndex][columnIndex]] = new Couple<>(lineIndex, columnIndex);
            }
        }
    }

    private Pattern convertToPattern() {

        ArrayList<Integer> hexagonsSolutions = new ArrayList<>();

        int[] correspondance = new int[diameter * diameter];

        Arrays.fill(correspondance, -1);

        for (int index = 0; index < benzenoidVerticesBVArray.length; index++) {
            if (benzenoidVerticesBVArray[index] != null && benzenoidVerticesBVArray[index].getValue() == 1) {
                    hexagonsSolutions.add(index);
                    correspondance[index] = hexagonsSolutions.size() - 1;
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

        for (int nodeIndex = 0; nodeIndex < nbNodes; nodeIndex++)
            Arrays.fill(neighbors[nodeIndex], -1);

        for (int nodeIndex = 0; nodeIndex < nbNodes; nodeIndex++) {

            int u = hexagonsSolutions.get(nodeIndex);
            Node n1 = nodes[nodeIndex];

            for (int j = (nodeIndex + 1); j < nbNodes; j++) {

                int v = hexagonsSolutions.get(j);
                Node n2 = nodes[j];

                if (adjacencyMatrix[u][v] == 1) {

                    // Setting matrix
                    matrix[nodeIndex][j] = 1;
                    matrix[j][nodeIndex] = 1;

                    // Setting neighbors
                    int x1 = n1.getX();
                    int y1 = n1.getY();
                    int x2 = n2.getX();
                    int y2 = n2.getY();

                    if (x2 == x1 && y2 == y1 - 1) {
                        neighbors[correspondance[u]][0] = correspondance[v];
                        neighbors[correspondance[v]][3] = correspondance[u];
                    } else if (x2 == x1 + 1 && y2 == y1) {
                        neighbors[correspondance[u]][1] = correspondance[v];
                        neighbors[correspondance[v]][4] = correspondance[u];
                    } else if (x2 == x1 + 1 && y2 == y1 + 1) {
                        neighbors[correspondance[u]][2] = correspondance[v];
                        neighbors[correspondance[v]][5] = correspondance[u];
                    } else if (x2 == x1 && y2 == y1 + 1) {
                        neighbors[correspondance[u]][3] = correspondance[v];
                        neighbors[correspondance[v]][0] = correspondance[u];
                    } else if (x2 == x1 - 1 && y2 == y1) {
                        neighbors[correspondance[u]][4] = correspondance[v];
                        neighbors[correspondance[v]][1] = correspondance[u];
                    } else if (x2 == x1 - 1 && y2 == y1 - 1) {
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

        Arrays.fill(labels, 2);

        return new Pattern(matrix, labels, nodes, null, neighbors, 0);
    }

    private void recordNoGoods() {

        ArrayList<Integer> vertices = new ArrayList<>();
        for (int i = 0; i < hexBoolVars.length; i++)
            if (hexBoolVars[i].getValue() == 1)
                vertices.add(i);

        int center = correspondancesHexagons[hexagonIndices[(diameter - 1) / 2][(diameter - 1) / 2]];

        Solution solution = new Solution(nodesRefs, correspondancesHexagons, hexagonsCorrespondances, hexagonIndices,
                center, nbCrowns, vertices);

        NoGoodRecorder noGoodRecorder;// = null;

        if (!modelPropertySet.has("symmetry")) {

            solution.setPattern(convertToPattern());
            noGoodRecorder = new NoGoodBorderRecorder(this, solution, topBorder, leftBorder);
        } else {

            noGoodRecorder = new NoGoodNoneRecorder(this, solution);
            //TODO changer tests
            if (Objects.equals(((ParameterizedExpression) modelPropertySet.getById("symmetry").getExpressions().get(0)).getOperator(), "SYMM_MIRROR"))
                noGoodRecorder = new NoGoodHorizontalAxisRecorder(this, solution);
            else if (Objects.equals(((ParameterizedExpression) modelPropertySet.getById("symmetry").getExpressions().get(0)).getOperator(), "SYMM_VERTICAL"))
                //noGoodRecorder = new NoGoodHorizontalAxisRecorder(this, solution);
                noGoodRecorder = new NoGoodVerticalAxisRecorder(this, solution);

            else {
                if (modelPropertySet.has("pattern"))
                    noGoodRecorder = new NoGoodUniqueRecorder(this, solution);
            }
        }
        solution.setPattern(convertToPattern());
        noGoodRecorder = new NoGoodAllRecorder(this, solution);
        noGoodRecorder.record();

    }

    public SolverResults solve() {

        applyModelConstraints();
        chocoModel.getSolver().setSearch(new IntStrategy(hexBoolVars, new FirstFail(chocoModel), new IntDomainMax()));

        for (Property modelProperty : modelPropertySet) {
            if (modelProperty.hasExpressions())
                ((ModelProperty) modelProperty).getConstraint().changeSolvingStrategy();
        }

        chocoSolver = chocoModel.getSolver();
        chocoSolver.limitSearch(() -> Stopper.STOP);

        //applySolverProperties();
        for (Property solverProperty : solverPropertySet)
            if (solverProperty.hasExpressions())
                ((SolverProperty) solverProperty).getSpecifier().apply(chocoSolver, solverProperty.getExpressions().get(0));

        solverResults = new SolverResults();

        indexSolution = 0;

        long begin = System.currentTimeMillis();

        chocoSolver.limitSearch(() -> Stopper.STOP);
        Stopper.STOP = false;

        while (chocoSolver.solve() && !generatorRun.isPaused()) {
            ArrayList<Integer> verticesSolution = buildVerticesSolution();
            String description = buildDescription(indexSolution);
            Molecule molecule = Molecule.buildMolecule(description, nbCrowns, indexSolution, verticesSolution);

            if (molecule.respectPostProcessing(modelPropertySet)) {
                solverResults.addMolecule(molecule);
                if (inTestMode())
                    nbTotalSolutions.set(nbTotalSolutions.get() + 1);
                else
                    Platform.runLater(() -> nbTotalSolutions.set(nbTotalSolutions.get() + 1));

                recordNoGoods();

                BenzenoidSolution solverSolution = new BenzenoidSolution(GUB, nbCrowns,
                        chocoModel.getName() + indexSolution, hexagonsCorrespondances);

                solverResults.addSolution(solverSolution, description, nbCrowns);
                solverResults.addVerticesSolution(verticesSolution);

                displaySolution(chocoSolver);

                if (verbose) {

                    System.out.println("NO-GOOD");

                    for (ArrayList<Integer> ng : nogoods) {
                        for (Integer v : ng)
                            System.out.print(v + " ");
                        System.out.println();
                    }
                } else
                    verticesSolution.add(0);

                indexSolution++;
            }
        }

        long end = System.currentTimeMillis();
        long time = end - begin;

        solverResults.setTime(time);
        solverResults.setNbTotalSolution(nbTotalSolutions.get());
        solverResults.setSolver(chocoSolver);

        System.out.println(nbCrowns + " crowns");
        System.out.println(nogoods.size() + " no-good clauses");
        System.out.println(nbClausesLexLead + " lex-lead clauses");
        chocoSolver.printStatistics();

        solverResults.setNogoodsFragments();
        System.out.println("------");
        displayDegrees();
        return solverResults;


    }

    private boolean inTestMode() {
        return isInTestMode;
    }


    private ArrayList<Integer> buildVerticesSolution() {
        ArrayList<Integer> verticesSolution = new ArrayList<>();

        for (BoolVar benzenoidVertex : benzenoidVerticesBVArray) {

            if (benzenoidVertex != null) {
                verticesSolution.add(benzenoidVertex.getValue());
            } else
                verticesSolution.add(0);
        }
        return verticesSolution;
    }

    private void buildAdjacencyMatrix() {

        nbEdges = 0;
        adjacencyMatrix = new int[diameter * diameter][diameter * diameter];

        for (int lineIndex = 0; lineIndex < diameter; lineIndex++) {
            for (int columnIndex = 0; columnIndex < diameter; columnIndex++) {

                if (validHexagonIndex(lineIndex, columnIndex)) {

                    int u = hexagonIndices[lineIndex][columnIndex];

                    if (lineIndex > 0 && columnIndex > 0) {

                        int v = hexagonIndices[lineIndex - 1][columnIndex - 1];
                        if (v != -1) {
                            if (adjacencyMatrix[u][v] == 0) {
                                adjacencyMatrix[u][v] = 1;
                                adjacencyMatrix[v][u] = 1;
                                nbEdges++;
                            }
                        }
                    }

                    if (columnIndex > 0) {

                        int v = hexagonIndices[lineIndex][columnIndex - 1];
                        if (v != -1) {
                            if (adjacencyMatrix[u][v] == 0) {
                                adjacencyMatrix[u][v] = 1;
                                adjacencyMatrix[v][u] = 1;
                                nbEdges++;
                            }
                        }
                    }

                    if (lineIndex + 1 < diameter) {

                        int v = hexagonIndices[lineIndex + 1][columnIndex];
                        if (v != -1) {
                            if (adjacencyMatrix[u][v] == 0) {
                                adjacencyMatrix[u][v] = 1;
                                adjacencyMatrix[v][u] = 1;
                                nbEdges++;
                            }
                        }
                    }

                    if (lineIndex + 1 < diameter && columnIndex + 1 < diameter) {

                        int v = hexagonIndices[lineIndex + 1][columnIndex + 1];
                        if (v != -1) {
                            if (adjacencyMatrix[u][v] == 0) {
                                adjacencyMatrix[u][v] = 1;
                                adjacencyMatrix[v][u] = 1;
                                nbEdges++;
                            }
                        }
                    }

                    if (columnIndex + 1 < diameter) {

                        int v = hexagonIndices[lineIndex][columnIndex + 1];
                        if (v != -1) {
                            if (adjacencyMatrix[u][v] == 0) {
                                adjacencyMatrix[u][v] = 1;
                                adjacencyMatrix[v][u] = 1;
                                nbEdges++;
                            }
                        }
                    }

                    if (lineIndex > 0) {

                        int v = hexagonIndices[lineIndex - 1][columnIndex];
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

    public ArrayList<Integer> getOutterHexagonsIndexes() {
        return outterHexagonsIndexes;
    }

    public void buildNeighborGraphWithOutterHexagons(int order) {

        if (order == 0) {
            // buildNeighborIndices();
            neighborGraphOutterHexagons = new ArrayList<>();

            for (int[] ints : neighborIndices) {
                ArrayList<Integer> neighbors = new ArrayList<>();
                for (int neighborIndex = 0; neighborIndex < 6; neighborIndex++)
                    neighbors.add(ints[neighborIndex]);
                neighborGraphOutterHexagons.add(neighbors);
            }
        } else {

            ArrayList<Integer> hexagons = new ArrayList<>();
            ArrayList<Couple<Integer, Integer>> coords = new ArrayList<>();

            for (int lineIndex = 0; lineIndex < diameter; lineIndex++) {
                if (lineIndex == 0 || lineIndex == diameter - 1) {
                    for (int columnIndex = 0; columnIndex < diameter; columnIndex++) {
                        if (validHexagonIndex(lineIndex, columnIndex)) {
                            hexagons.add(hexagonIndices[lineIndex][columnIndex]);
                            coords.add(new Couple<>(columnIndex, lineIndex));
                        }
                    }
                } else {

                    ArrayList<Integer> c1 = getFirstColumns(lineIndex, 1);
                    ArrayList<Integer> c2 = getLastColumns(lineIndex, 1);

                    for (int i = 0; i < c1.size(); i++) {

                        hexagons.add(hexagonIndices[lineIndex][c1.get(i)]);
                        coords.add(new Couple<>(c1.get(i), lineIndex));
                        hexagons.add(hexagonIndices[lineIndex][c2.get(i)]);
                        coords.add(new Couple<>(c2.get(i), lineIndex));
                    }
                }
            }

            // buildNeighborIndices();

            neighborGraphOutterHexagons = new ArrayList<>();

            for (int[] ints : neighborIndices) {
                ArrayList<Integer> neighbors = new ArrayList<>();
                for (int j = 0; j < 6; j++)
                    neighbors.add(ints[j]);
                neighborGraphOutterHexagons.add(neighbors);
            }

            ArrayList<Triplet<Integer, Integer, Integer>> coordsOutterHexagons = new ArrayList<>();

            for (int i = 0; i < hexagons.size(); i++) {

                int hexagon = hexagons.get(i);
                Couple<Integer, Integer> coord = coords.get(i);

                int[] neighbors = neighborIndices[hexagon];

                for (HexNeighborhood neighbor : HexNeighborhood.values()) {
                    if (neighbors[neighbor.getIndex()] == -1) {
                        int x = coord.getX() + neighbor.dx();
                        int y = coord.getY() + neighbor.dy();

                        int indexOutter = -1;

                        for (Triplet<Integer, Integer, Integer> coordOutter : coordsOutterHexagons)
                            if (coordOutter.getX() == x && coordOutter.getY() == y)
                                indexOutter = coordOutter.getZ();

                        if (indexOutter == -1) {

                            indexOutter = neighborGraphOutterHexagons.size();

                            coordsOutterHexagons.add(new Triplet<>(x, y, indexOutter));

                            ArrayList<Integer> newNeighbor = new ArrayList<>();
                            for (int k = 0; k < 6; k++)
                                newNeighbor.add(-1);

                            neighborGraphOutterHexagons.add(newNeighbor);
                        }

                        neighborGraphOutterHexagons.get(hexagon).set(neighbor.getIndex(), indexOutter);
                        neighborGraphOutterHexagons.get(indexOutter).set((neighbor.getIndex() + 3) % 6, hexagon);
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
                        } else if (x2 == x1 + 1 && y2 == y1) {
                            neighborGraphOutterHexagons.get(index1).set(1, index2);
                            neighborGraphOutterHexagons.get(index2).set(4, index1);
                        } else if (x2 == x1 + 1 && y2 == y1 + 1) {
                            neighborGraphOutterHexagons.get(index1).set(2, index2);
                            neighborGraphOutterHexagons.get(index2).set(5, index1);
                        } else if (x2 == x1 && y2 == y1 + 1) {
                            neighborGraphOutterHexagons.get(index1).set(3, index2);
                            neighborGraphOutterHexagons.get(index2).set(0, index1);
                        } else if (x2 == x1 - 1 && y2 == y1) {
                            neighborGraphOutterHexagons.get(index1).set(4, index2);
                            neighborGraphOutterHexagons.get(index2).set(1, index1);
                        } else if (x2 == x1 - 1 && y2 == y1 - 1) {
                            neighborGraphOutterHexagons.get(index1).set(5, index2);
                            neighborGraphOutterHexagons.get(index2).set(2, index1);
                        }
                    }

                }
            }
        }
    }

    private ArrayList<Integer> getFirstColumns(int lineIndex, int order) {

        ArrayList<Integer> columns = new ArrayList<>();

        int nbColumns = 0;
        int columnIndex = 0;

        while (nbColumns < order && columnIndex < diameter) {

            if (validHexagonIndex(lineIndex, columnIndex)) {
                columns.add(columnIndex);
                nbColumns++;
            }

            columnIndex++;
        }

        return columns;
    }

    private ArrayList<Integer> getLastColumns(int lineIndex, int order) {

        ArrayList<Integer> columns = new ArrayList<>();

        int nbColumns = 0;
        int columnIndex = diameter - 1;

        while (nbColumns < order && columnIndex >= 0) {

            if (validHexagonIndex(lineIndex, columnIndex)) {
                columns.add(columnIndex);
                nbColumns++;
            }

            columnIndex--;
        }

        return columns;
    }

    private void buildNeighborIndices() {

        neighborIndices = new int[diameter * diameter][6];

        for (int[] ints : neighborIndices) {
            Arrays.fill(ints, -1);
        }

        for (int lineIndex = 0; lineIndex < hexagonIndices.length; lineIndex++) {
            for (int columnIndex = 0; columnIndex < hexagonIndices[lineIndex].length; columnIndex++) {

                if (validHexagonIndex(lineIndex, columnIndex)) {

                    int hexagonIndex = hexagonIndices[lineIndex][columnIndex];

                    // Top-Right
                    if (lineIndex > 0)
                        neighborIndices[hexagonIndex][0] = hexagonIndices[lineIndex - 1][columnIndex];

                    // Right
                    if (columnIndex < hexagonIndices[lineIndex].length - 1)
                        neighborIndices[hexagonIndex][1] = hexagonIndices[lineIndex][columnIndex + 1];

                    // Bottom-Right
                    if (lineIndex < hexagonIndices[lineIndex].length - 1 && columnIndex < hexagonIndices[lineIndex].length - 1)
                        neighborIndices[hexagonIndex][2] = hexagonIndices[lineIndex + 1][columnIndex + 1];

                    // Bottom-Left
                    if (lineIndex < hexagonIndices[lineIndex].length - 1)
                        neighborIndices[hexagonIndex][3] = hexagonIndices[lineIndex + 1][columnIndex];

                    // Left
                    if (columnIndex > 0)
                        neighborIndices[hexagonIndex][4] = hexagonIndices[lineIndex][columnIndex - 1];

                    // Top-Left
                    if (lineIndex > 0 && columnIndex > 0)
                        neighborIndices[hexagonIndex][5] = hexagonIndices[lineIndex - 1][columnIndex - 1];
                }
            }
        }
    }

    public void buildAdjacencyMatrixWithOutterHexagons() {

        adjacencyMatrixWithOutterHexagons = new int[neighborGraphOutterHexagons.size()][neighborGraphOutterHexagons
                .size()];

        for (int nodeIndex = 0; nodeIndex < neighborGraphOutterHexagons.size(); nodeIndex++) {

            for (int neighborIndex = 0; neighborIndex < 6; neighborIndex++) {

                int v = neighborGraphOutterHexagons.get(nodeIndex).get(neighborIndex);

                if (v != -1) {
                    adjacencyMatrixWithOutterHexagons[nodeIndex][v] = 1;
                    adjacencyMatrixWithOutterHexagons[v][nodeIndex] = 1;
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

        hexBoolVars = new BoolVar[nbHexagonsCoronenoid];
        for (int i = 0; i < hexBoolVars.length; i++)
            hexBoolVars[i] = chocoModel.boolVar("vertex[" + i + "]");

        chocoModel.nodesChanneling(benzenoidGraphVar, hexBoolVars).post();

        benzenoidVerticesBVArray = new BoolVar[diameter * diameter];

        int index = 0;

        for (int lineIndex = 0; lineIndex < diameter; lineIndex++) {
            for (int columnIndex = 0; columnIndex < diameter; columnIndex++) {
                if (validHexagonIndex(lineIndex, columnIndex)) {

                    BoolVar hexBV = hexBoolVars[correspondancesHexagons[hexagonIndices[lineIndex][columnIndex]]];
                    benzenoidVerticesBVArray[index] = hexBV;
                }
                index++;
            }
        }
    }

    private void buildBenzenoidEdges() {

        benzenoidEdges = new BoolVar[nbHexagonsCoronenoid][nbHexagonsCoronenoid];

        for (int lineIndex = 0; lineIndex < adjacencyMatrix.length; lineIndex++) {
            for (int columnIndex = (lineIndex + 1); columnIndex < adjacencyMatrix.length; columnIndex++) {

                if (adjacencyMatrix[lineIndex][columnIndex] == 1) {

                    int u = correspondancesHexagons[lineIndex];
                    int v = correspondancesHexagons[columnIndex];

                    BoolVar x = chocoModel.boolVar("e_" + u + "_" + v);
                    benzenoidEdges[u][v] = x;
                    benzenoidEdges[v][u] = x;

                    chocoModel.edgeChanneling(benzenoidGraphVar, x, u, v).post();
                    // chocoModel.edgeChanneling(benzenoid, x, v, u).post();
                }
            }
        }
    }

    private void buildHexagonIndices() {

        hexagonIndices = new int[diameter][diameter];
        nbHexagonsCoronenoid = 0;

        initializeHexagonIndices();

        int hexagonIndex = 0;
        int centerIndex = (diameter - 1) / 2;

        int shift = diameter - nbCrowns;

        for (int lineIndex = 0; lineIndex < centerIndex; lineIndex++) {

            for (int columnIndex = 0; columnIndex < diameter - shift; columnIndex++) {
                hexagonIndices[lineIndex][columnIndex] = hexagonIndex;
                hexagonIndex++;
                nbHexagonsCoronenoid++;
            }
            hexagonIndex += shift;
            shift--;
        }

        for (int columnIndex = 0; columnIndex < diameter; columnIndex++) {
            hexagonIndices[centerIndex][columnIndex] = hexagonIndex;
            hexagonIndex++;
            nbHexagonsCoronenoid++;
        }

        shift = 1;

        for (int lineIndex = centerIndex + 1; lineIndex < diameter; lineIndex++) {
            hexagonIndex += shift;
            for (int columnIndex = shift; columnIndex < diameter; columnIndex++) {
                hexagonIndices[lineIndex][columnIndex] = hexagonIndex;
                hexagonIndex++;
                nbHexagonsCoronenoid++;
            }
            shift++;
        }

        hexagonsCorrespondances = new int[nbHexagonsCoronenoid];
        hexagonIndex = 0;

        for (int lineIndex = 0; lineIndex < diameter; lineIndex++) {
            for (int columnIndex = 0; columnIndex < diameter; columnIndex++) {
                if (validHexagonIndex(lineIndex, columnIndex)) {
                    hexagonsCorrespondances[hexagonIndex] = hexagonIndices[lineIndex][columnIndex];
                    hexagonIndex++;
                }
            }
        }

        correspondancesHexagons = new int[diameter * diameter];

        Arrays.fill(correspondancesHexagons, -1);

        for (int i = 0; i < hexagonsCorrespondances.length; i++)
            correspondancesHexagons[hexagonsCorrespondances[i]] = i;

    }

    private boolean validHexagonIndex(int lineIndex, int columnIndex) {
        return hexagonIndices[lineIndex][columnIndex] != -1;
    }

    public int getNbHexagonsCoronenoid() {
        return nbHexagonsCoronenoid;
    }

    public int[] getCorrespondancesHexagons() {
        return correspondancesHexagons;
    }

    public BoolVar[] getNeighbors(int lineIndex, int columnIndex) {

        if (validHexagonIndex(lineIndex, columnIndex)) {

            BoolVar[] N = new BoolVar[6];

            for (int k = 0; k < 6; k++)
                N[k] = null;

            for(HexNeighborhood neighbor : HexNeighborhood.values()){
                int lineIndex2 = lineIndex + neighbor.dy();
                int columnIndex2 = columnIndex + neighbor.dx();
                if(lineIndex2 >= 0 && lineIndex2 <= diameter - 1 && columnIndex2 >= 0 && columnIndex2 <= diameter - 1)
                    if(validHexagonIndex(lineIndex2, columnIndex2))
                        N[neighbor.getIndex()] = benzenoidVerticesBVArray[hexagonIndices[lineIndex2][columnIndex2]];
            }
            return N;
        }
        return null;
    }

    public BoolVar[] getHexBoolVars() {
        return hexBoolVars;
    }

    public int[][] getNeighborIndices() {
        return neighborIndices;
    }

    public int getNbMaxHexagons() {
        return nbMaxHexagons;
    }

    public IntVar getNbVerticesVar() {
        return nbVertices;
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

    public boolean isPaused() {
        return generatorRun.isPaused();
    }

    public void pause() {
        generatorRun.pause();
    }

    public void resume() {

        while (chocoSolver.solve() && !generatorRun.isPaused()) {

            Platform.runLater(() -> nbTotalSolutions.set(nbTotalSolutions.get() + 1));

            recordNoGoods();

            BenzenoidSolution solution = new BenzenoidSolution(GUB, nbCrowns, chocoModel.getName() + indexSolution,
                    hexagonsCorrespondances);
            String description = buildDescription(indexSolution);
            solverResults.addSolution(solution, description, nbCrowns);

            ArrayList<BoolVar> presentHexagons = new ArrayList<>();
            ArrayList<Integer> verticesSolution = new ArrayList<>();

            for (BoolVar benzenoidVertex : benzenoidVerticesBVArray) {

                if (benzenoidVertex != null) {
                    verticesSolution.add(benzenoidVertex.getValue());

                    if (benzenoidVertex.getValue() == 1) {
                        presentHexagons.add(benzenoidVertex);
                    }

                } else
                    verticesSolution.add(0);
            }

            solverResults.addVerticesSolution(verticesSolution);

            displaySolution(chocoSolver);
            //System.out.println(solver.getDecisionPath());

            if (verbose) {

                System.out.println("NO-GOOD");

                for (ArrayList<Integer> ng : nogoods) {
                    for (Integer v : ng)
                        System.out.println(v + " ");
                }

                System.out.println();
            }

            indexSolution++;
        }

    }

    private void buildNodesRefs() {
        nodesRefs = new Node[nbHexagonsCoronenoid];

        for (int lineIndex = 0; lineIndex < diameter; lineIndex++) {
            for (int columnIndex = 0; columnIndex < diameter; columnIndex++) {
                if (validHexagonIndex(lineIndex, columnIndex)) {
                    int index = correspondancesHexagons[hexagonIndices[lineIndex][columnIndex]];
                    nodesRefs[index] = new Node(lineIndex, columnIndex, index);
                }
            }
        }
    }

    private boolean isValid(Couple<Integer, Integer> coord, Pattern fragment, int index) {
        if (coord.getX() < 0 || coord.getX() >= diameter || coord.getY() < 0 || coord.getY() >= diameter
                || hexagonIndices[coord.getY()][coord.getX()] == -1) {
            return fragment.getLabel(index) != 2;
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

        for (int lineIndex = 0; lineIndex < diameter; lineIndex++) {
            for (int columnIndex = 0; columnIndex < diameter; columnIndex++) {
                int hexagonIndex = hexagonIndices[lineIndex][columnIndex];
                if (hexagonIndex != -1) {
                    /*
                     * On place le fragment dans le coron�no�de de telle sorte que firstNode
                     * corresponde � hexagon
                     */

                    int[] checkedHexagons = new int[fragment.getNbNodes()];
                    Couple<Integer, Integer>[] coords = new Couple[fragment.getNbNodes()];

                    int candidat = nodeIndex;
                    checkedHexagons[nodeIndex] = 1;
                    coords[nodeIndex] = new Couple<>(columnIndex, lineIndex);

                    ArrayList<Integer> candidats = new ArrayList<>();

                    for (HexNeighborhood neighbor : HexNeighborhood.values()) {
                        if (fragment.getNeighbor(candidat, neighbor.getIndex()) != -1) {
                            int neighborIndex = fragment.getNeighbor(candidat, neighbor.getIndex());
                            candidats.add(neighborIndex);
                            coords[neighborIndex] = new Couple<>(columnIndex + neighbor.dx(), lineIndex + neighbor.dy());
                            checkedHexagons[neighborIndex] = 1;
                        }
                    }

                    while (candidats.size() > 0) {

                        candidat = candidats.get(0);

                        for (HexNeighborhood neighbor : HexNeighborhood.values()) {
                            if (fragment.getNeighbor(candidat, neighbor.getIndex()) != -1) {

                                int neighborIndex = fragment.getNeighbor(candidat, neighbor.getIndex());

                                if (checkedHexagons[neighborIndex] == 0) {
                                    candidats.add(neighborIndex);
                                    coords[neighborIndex] = new Couple<>(coords[candidat].getX() + neighbor.dx(), coords[candidat].getY() + neighbor.dy());
                                    checkedHexagons[neighborIndex] = 1;
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
                                occurence[i] = hexagonIndices[coord.getY()][coord.getX()];

                                if (hexagonIndices[coord.getY()][coord.getX()] == -1 && !outterHexagons.contains(coord)) {
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
                                    if (hexagonIndices[coord.getY()][coord.getX()] == -1) {

                                        int index = findIndex(outterHexagons, coord);
                                        outter.add(outterHexagonsIndexes.get(index));
                                    } else {
                                        unknown.add(hexagonIndices[coord.getY()][coord.getX()]);
                                    }
                                } else {
                                    int index = findIndex(outterHexagons, coord);
                                    outter.add(outterHexagonsIndexes.get(index));
                                }
                            } else if (fragment.getLabel(i) == 2) {
                                Couple<Integer, Integer> coord = coords[i];
                                present.add(hexagonIndices[coord.getY()][coord.getX()]);
                            } else if (fragment.getLabel(i) == 3) {
                                Couple<Integer, Integer> coord = coords[i];

                                if (coord.getX() >= 0 && coord.getX() < diameter && coord.getY() >= 0
                                        && coord.getY() < diameter) {
                                    if (hexagonIndices[coord.getY()][coord.getX()] == -1) {

                                        int index = findIndex(outterHexagons, coord);
                                        outter.add(outterHexagonsIndexes.get(index));
                                    } else {
                                        absent.add(hexagonIndices[coord.getY()][coord.getX()]);
                                    }
                                } else {
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

    public static void buildSolverPropertySet(ArrayList<HBoxCriterion> hBoxesSolverCriterions) {
        solverPropertySet.clearPropertyExpressions();
        for (HBoxCriterion box : hBoxesSolverCriterions) {
            ((HBoxSolverCriterion) box).addPropertyExpression(solverPropertySet);
        }
    }

    public void increaseDegree(String variableName) {
        variablesDegrees.putIfAbsent(variableName, 0);
        int curentDegree = variablesDegrees.get(variableName);
        variablesDegrees.remove(variableName);
        variablesDegrees.put(variableName, curentDegree + 1);
    }

    public void displayDegrees() {
        for (Map.Entry<String, Integer> entry : variablesDegrees.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            System.out.println("d(" + key + ") = " + value);
        }
    }

    public void setInTestMode(boolean inTestMode) {
        isInTestMode = inTestMode;
    }

}
