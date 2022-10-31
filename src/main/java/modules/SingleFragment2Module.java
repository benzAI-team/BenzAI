package modules;

import java.util.ArrayList;

import org.chocosolver.solver.constraints.extension.Tuples;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMax;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin;
import org.chocosolver.solver.search.strategy.selectors.values.IntValueSelector;
import org.chocosolver.solver.search.strategy.selectors.variables.ConflictHistorySearch;
import org.chocosolver.solver.search.strategy.selectors.variables.DomOverWDeg;
import org.chocosolver.solver.search.strategy.selectors.variables.DomOverWDegRef;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.search.strategy.selectors.variables.VariableSelector;
import org.chocosolver.solver.search.strategy.strategy.IntStrategy;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Variable;
import org.chocosolver.util.objects.setDataStructures.iterable.IntIterableRangeSet;

import generator.GeneralModel;
import generator.OrderStrategy;
import generator.ValueStrategy;
import generator.VariableStrategy;
import generator.fragments.Fragment;

public class SingleFragment2Module extends Module {

	private boolean useSymmetries;

	private Fragment fragment;
	private int[][] neighborGraph;

	ArrayList<Integer> absentHexagons;
	ArrayList<Integer> presentHexagons;
	ArrayList<Integer> unknownHexagons;

	private IntVar[] fragmentCorrespondances;
	private int[] T1, T2;

	private BoolVar symmetry;

	private boolean verbose = false;

	private int[][] matrixTable;

	private VariableStrategy variableStrategy;
	private ValueStrategy valueStrategy;
	private OrderStrategy orderStrategy;

	public SingleFragment2Module(GeneralModel generalModel, Fragment fragment, boolean useSymmetries,
			VariableStrategy variableStrategy, ValueStrategy strategy, OrderStrategy orderStrategy) {
		super(generalModel);
		this.fragment = fragment;
		this.useSymmetries = useSymmetries;
		this.variableStrategy = variableStrategy;
		this.valueStrategy = strategy;
		this.orderStrategy = orderStrategy;
	}

	@Override
	public void buildVariables() {

		presentHexagons = new ArrayList<Integer>();
		absentHexagons = new ArrayList<Integer>();
		unknownHexagons = new ArrayList<Integer>();

		symmetry = generalModel.getProblem().boolVar("axial_symmetry");

		int nbHexagonsCoronenoid = 0;
		for (int line = 0; line < generalModel.getDiameter(); line++) {
			for (int column = 0; column < generalModel.getDiameter(); column++) {
				if (generalModel.getCoordsMatrix()[line][column] != -1)
					nbHexagonsCoronenoid++;
			}
		}

		fragmentCorrespondances = new IntVar[nbHexagonsCoronenoid];
		T1 = new int[generalModel.getDiameter() * generalModel.getDiameter()];
		T2 = new int[nbHexagonsCoronenoid];

		for (int i = 0; i < T1.length; i++)
			T1[i] = -1;

		int index = 0;
		int coroIndex = 0;
		for (int line = 0; line < generalModel.getDiameter(); line++) {
			for (int column = 0; column < generalModel.getDiameter(); column++) {
				if (generalModel.getCoordsMatrix()[line][column] != -1) {

					T1[generalModel.getCoordsMatrix()[line][column]] = index;
					System.out.println(generalModel.getCoordsMatrix()[line][column] + " " + index);
					T2[index] = coroIndex;
					fragmentCorrespondances[index] = generalModel.getProblem().intVar("f_c_" + coroIndex, -1,
							fragment.getNbNodes() - 1); // attention
					index++;

				}

				coroIndex++;
			}
		}

		buildNeighborGraph();
	}

	@Override
	public void postConstraints() {
		System.out.println("Scope");
		for (IntVar x : fragmentCorrespondances)
			System.out.println(x.toString());

		for (int i = 0; i < fragment.getNbNodes(); i++) {

			int label = fragment.getLabel(i);

			IntVar count;
			if (label == 2)
				count = generalModel.getProblem().intVar("count_" + i, 1, 1);
			else
				count = generalModel.getProblem().intVar("count_" + i, 0, 1);

			generalModel.getProblem().count(i, fragmentCorrespondances, count).post();

			if (label == 1)
				unknownHexagons.add(i);

			else if (label == 2) {
				presentHexagons.add(i);
			}

			else if (label == 3)
				absentHexagons.add(i);
		}

		IntVar[] varClause;
		IntIterableRangeSet[] valClause;

		for (int j = 0; j < generalModel.getDiameter() * generalModel.getDiameter(); j++) {

			if (T1[j] != -1) {

				/*
				 * Si un hexagone du coronénoïde est lié à un hexagon présent du fragment, alors
				 * il doit être présent dans la solution
				 */

				if (presentHexagons.size() > 0) {

					varClause = new IntVar[fragment.getNbNodes() + 2 - presentHexagons.size()];
					valClause = new IntIterableRangeSet[fragment.getNbNodes() + 2 - presentHexagons.size()];

					int index = 0;

					for (int i = 0; i < fragment.getNbNodes(); i++) {
						if (!presentHexagons.contains(i)) {
							varClause[index] = fragmentCorrespondances[T1[j]];
							valClause[index] = new IntIterableRangeSet(i);
							index++;
						}
					}

					varClause[index] = fragmentCorrespondances[T1[j]];
					valClause[index] = new IntIterableRangeSet(-1);
					index++;
					varClause[index] = generalModel.getGraphVertices()[j];
					valClause[index] = new IntIterableRangeSet(1);

					generalModel.getProblem().getClauseConstraint().addClause(varClause, valClause);
				}

				/*
				 * Si un hexagone du coronénoïde est lié à un hexagone absent du fragment i,
				 * alors il ne doit pas être présent dans la solutions
				 */

				if (absentHexagons.size() > 0) {

					varClause = new IntVar[fragment.getNbNodes() + 2 - absentHexagons.size()];
					valClause = new IntIterableRangeSet[fragment.getNbNodes() + 2 - absentHexagons.size()];

					int index = 0;

					for (int i = 0; i < fragment.getNbNodes(); i++) {
						if (!absentHexagons.contains(i)) {
							varClause[index] = fragmentCorrespondances[T1[j]];
							valClause[index] = new IntIterableRangeSet(i);
							index++;
						}
					}

					varClause[index] = fragmentCorrespondances[T1[j]];
					valClause[index] = new IntIterableRangeSet(-1);
					index++;
					varClause[index] = generalModel.getGraphVertices()[j];
					valClause[index] = new IntIterableRangeSet(0);

					generalModel.getProblem().getClauseConstraint().addClause(varClause, valClause);
				}

				varClause = new IntVar[presentHexagons.size() + unknownHexagons.size() + 2];
				valClause = new IntIterableRangeSet[presentHexagons.size() + unknownHexagons.size() + 2];

				int index = 0;

				for (int i = 0; i < presentHexagons.size(); i++) {
					varClause[index] = fragmentCorrespondances[T1[j]];
					valClause[index] = new IntIterableRangeSet(presentHexagons.get(i));
					index++;
				}

				for (int i = 0; i < unknownHexagons.size(); i++) {
					varClause[index] = fragmentCorrespondances[T1[j]];
					valClause[index] = new IntIterableRangeSet(unknownHexagons.get(i));
					index++;
				}

				varClause[index] = fragmentCorrespondances[T1[j]];
				valClause[index] = new IntIterableRangeSet(-1);
				index++;
				varClause[index] = generalModel.getGraphVertices()[j];
				valClause[index] = new IntIterableRangeSet(0);

			}
		}

		StringBuilder tableConstraints = new StringBuilder();
		tableConstraints.append("TUPLES\n");

		if (useSymmetries)
			buildMatrixTable();
		else
			buildMatrixTableWithoutSymmetries();

		for (int line = 0; line < generalModel.getCoordsMatrix().length; line++) {
			for (int column = 0; column < generalModel.getCoordsMatrix()[line].length; column++) {
				if (generalModel.getCoordsMatrix()[line][column] != -1) {

					int index = generalModel.getCoordsMatrix()[line][column];

					ArrayList<IntVar> tupleList = new ArrayList<>();

					tupleList.add(fragmentCorrespondances[T1[index]]);

					ArrayList<Integer> okColumns = new ArrayList<Integer>();
					okColumns.add(0);

					ArrayList<Integer> nokColumns = new ArrayList<Integer>();

					for (int i = 0; i < 6; i++) {

						int neighbor = neighborGraph[index][i];

						if (neighbor == -1) {
							nokColumns.add(i + 1);
						}

						else {
							tupleList.add(fragmentCorrespondances[T1[neighbor]]);
							okColumns.add(i + 1);
						}
					}

					if (useSymmetries) {
						System.out.println("Symétrie");
						tupleList.add(symmetry);
						okColumns.add(7);
					}

					Tuples subTable = buildSubTable(okColumns, nokColumns);

					IntVar[] tuple = new IntVar[tupleList.size()];
					for (int i = 0; i < tuple.length; i++)
						tuple[i] = tupleList.get(i);

					for (Variable x : tuple)
						generalModel.increaseDegree(x.getName());

					generalModel.getProblem().table(tuple, subTable, "CT+").post();
				}
			}
		}

		if (verbose)
			System.out.println(tableConstraints.toString());
	}

	@Override
	public void addVariables() {
//		generalModel.addWatchedVariable(fragmentCorrespondances);
	}

	@Override
	public void changeSolvingStrategy() {

		IntVar[] branchingVariables = new IntVar[generalModel.getChanneling().length + fragmentCorrespondances.length];

		int index = 0;

		switch (orderStrategy) {

		case CHANNELING_FIRST:

			for (BoolVar x : generalModel.getChanneling()) {
				branchingVariables[index] = x;
				index++;
			}

			for (IntVar x : fragmentCorrespondances) {
				System.out.println(index + " " + x.toString());
				branchingVariables[index] = x;
				index++;
			}

			break;

		case CHANNELING_LAST:

			for (IntVar x : fragmentCorrespondances) {
				branchingVariables[index] = x;
				index++;
			}

			for (BoolVar x : generalModel.getChanneling()) {
				branchingVariables[index] = x;
				index++;
			}

			break;
		}

		VariableSelector<IntVar> variableSelector = null;

		switch (variableStrategy) {

		case FIRST_FAIL:
			variableSelector = new FirstFail(generalModel.getProblem());
			break;

		case DOM_WDEG:
			variableSelector = new DomOverWDeg(branchingVariables, 0L);
			break;

		case DOM_WDEG_REF:
			variableSelector = new DomOverWDegRef(branchingVariables, 0L);
			break;

		case CHS:
			variableSelector = new ConflictHistorySearch(branchingVariables, 0L);
			break;
		}

		IntValueSelector valueSelector = null;

		switch (valueStrategy) {

		case INT_MIN:
			valueSelector = new IntDomainMin();
			break;
		case INT_MAX:
			valueSelector = new IntDomainMax();
			break;
		}

		generalModel.getProblem().getSolver()
				.setSearch(new IntStrategy(branchingVariables, variableSelector, valueSelector));
	}

	@Override
	public void changeGraphVertices() {
	}

	private Tuples buildSubTable(ArrayList<Integer> okColumns, ArrayList<Integer> nokColumns) {

		Tuples table = new Tuples(true);

		ArrayList<Integer> okLines = new ArrayList<Integer>();

		for (int i = 0; i < matrixTable.length; i++) {

			boolean ok = true;

			for (int column : nokColumns) {

				int v = matrixTable[i][column];

				if (v != -1) {

					if (!(fragment.getLabel(v) == 1 || fragment.getLabel(v) == 3)) {
						ok = false;
					}
				}
			}

			if (ok)
				okLines.add(i);
		}

		int[] tuple = new int[okColumns.size()];
		table.setUniversalValue(-2);
		tuple[0] = -1;
		for (int i = 1; i < okColumns.size(); i++)
			tuple[i] = -2;
		table.add(tuple);

		for (int i : okLines) {
			for (int j = 0; j < okColumns.size(); j++) {
				tuple[j] = matrixTable[i][okColumns.get(j)];
			}

			table.add(tuple);
		}

		if (table.nbTuples() == 0) {
			for (int i = 0; i < fragment.getNbNodes(); i++)
				table.add(new int[] { i, 0 });
		}

		return table;
	}

	private void buildMatrixTableWithoutSymmetries() {

		matrixTable = new int[6 * fragment.getNbNodes()][8];

		StringBuilder tableString = new StringBuilder();
		tableString.append("TABLE\n");

		Tuples table = new Tuples(true);

		/*
		 * Rotations simples
		 */

		int index = 0;

		for (int shift = 0; shift < 6; shift++) {

			int[][] newNeighborGraph = new int[fragment.getNbNodes()][6];

			for (int i = 0; i < fragment.getNbNodes(); i++) {
				for (int j = 0; j < 6; j++) {
					newNeighborGraph[i][(j + shift) % 6] = fragment.getNeighbor(i, j);
				}
			}

			for (int i = 0; i < fragment.getNbNodes(); i++) {

				int[] tuple = new int[7];
				tuple[0] = i;

				for (int j = 0; j < 6; j++) {
					tuple[j + 1] = newNeighborGraph[i][j];
				}

				// tuple[7] = 0;

				for (int j = 0; j < 7; j++)
					tableString.append(tuple[j] + "\t\t");
				tableString.append("\n");

				table.add(tuple);

				for (int j = 0; j < tuple.length; j++) {
					matrixTable[index][j] = tuple[j];
				}

				index++;
			}
		}

		if (verbose)
			System.out.println(tableString.toString() + "\n\n");
	}

	private void buildMatrixTable() {

		matrixTable = new int[2 * 6 * fragment.getNbNodes()][8];

		StringBuilder tableString = new StringBuilder();
		tableString.append("TABLE\n");

		Tuples table = new Tuples(true);

		/*
		 * Rotations simples
		 */

		int index = 0;

		for (int shift = 0; shift < 6; shift++) {

			int[][] newNeighborGraph = new int[fragment.getNbNodes()][6];

			for (int i = 0; i < fragment.getNbNodes(); i++) {
				for (int j = 0; j < 6; j++) {
					newNeighborGraph[i][(j + shift) % 6] = fragment.getNeighbor(i, j);
				}
			}

			for (int i = 0; i < fragment.getNbNodes(); i++) {

				int[] tuple = new int[8];
				tuple[0] = i;

				for (int j = 0; j < 6; j++) {
					tuple[j + 1] = newNeighborGraph[i][j];
				}

				tuple[7] = 0;

				for (int j = 0; j < 7; j++)
					tableString.append(tuple[j] + "\t\t");
				tableString.append("\n");

				table.add(tuple);

				for (int j = 0; j < tuple.length; j++) {
					matrixTable[index][j] = tuple[j];
				}

				index++;
			}
		}

		/*
		 * Rotations symétries axiales
		 */

		tableString.append("\n");

		int[][] neighborGraphSymmetry = new int[fragment.getNbNodes()][6];

		for (int i = 0; i < fragment.getNbNodes(); i++) {
			neighborGraphSymmetry[i][0] = fragment.getNeighbor(i, 2);
			neighborGraphSymmetry[i][1] = fragment.getNeighbor(i, 1);
			neighborGraphSymmetry[i][2] = fragment.getNeighbor(i, 0);
			neighborGraphSymmetry[i][3] = fragment.getNeighbor(i, 5);
			neighborGraphSymmetry[i][4] = fragment.getNeighbor(i, 4);
			neighborGraphSymmetry[i][5] = fragment.getNeighbor(i, 3);

		}

		for (int shift = 0; shift < 6; shift++) {

			int[][] newNeighborGraph = new int[fragment.getNbNodes()][6];

			for (int i = 0; i < fragment.getNbNodes(); i++) {
				for (int j = 0; j < 6; j++) {
					newNeighborGraph[i][(j + shift) % 6] = neighborGraphSymmetry[i][j];
				}
			}

			for (int i = 0; i < fragment.getNbNodes(); i++) {

				int[] tuple = new int[8];
				tuple[0] = i;

				for (int j = 0; j < 6; j++) {
					tuple[j + 1] = newNeighborGraph[i][j];
				}

				tuple[7] = 1;

				for (int j = 0; j < 7; j++)
					tableString.append(tuple[j] + "\t\t");
				tableString.append("\n");

				table.add(tuple);

				matrixTable[index] = tuple;
				index++;
			}
		}

		if (verbose)
			System.out.println(tableString.toString() + "\n\n");
	}

	private void buildNeighborGraph() {

		neighborGraph = new int[generalModel.getDiameter() * generalModel.getDiameter()][6];

		for (int i = 0; i < neighborGraph.length; i++) {
			for (int j = 0; j < neighborGraph[i].length; j++) {
				neighborGraph[i][j] = -1;
			}
		}

		for (int line = 0; line < generalModel.getCoordsMatrix().length; line++) {
			for (int column = 0; column < generalModel.getCoordsMatrix()[line].length; column++) {

				if (generalModel.getCoordsMatrix()[line][column] != -1) {

					int index = generalModel.getCoordsMatrix()[line][column];

					// High-Right
					if (line > 0)
						neighborGraph[index][0] = generalModel.getCoordsMatrix()[line - 1][column];

					// Right
					if (column < generalModel.getCoordsMatrix()[line].length - 1)
						neighborGraph[index][1] = generalModel.getCoordsMatrix()[line][column + 1];

					// Down-Right
					if (line < generalModel.getCoordsMatrix()[line].length - 1
							&& column < generalModel.getCoordsMatrix()[line].length - 1)
						neighborGraph[index][2] = generalModel.getCoordsMatrix()[line + 1][column + 1];

					// Down-Left
					if (line < generalModel.getCoordsMatrix()[line].length - 1)
						neighborGraph[index][3] = generalModel.getCoordsMatrix()[line + 1][column];

					// Left
					if (column > 0)
						neighborGraph[index][4] = generalModel.getCoordsMatrix()[line][column - 1];

					// High-Left
					if (line > 0 && column > 0)
						neighborGraph[index][5] = generalModel.getCoordsMatrix()[line - 1][column - 1];
				}
			}
		}
	}
}