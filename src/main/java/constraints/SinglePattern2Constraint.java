package constraints;

import generator.GeneralModel;
import generator.OrderStrategy;
import generator.ValueStrategy;
import generator.VariableStrategy;
import generator.patterns.Pattern;
import generator.patterns.PatternLabel;
import generator.properties.model.expression.BinaryNumericalExpression;
import org.chocosolver.solver.constraints.extension.Tuples;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMax;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin;
import org.chocosolver.solver.search.strategy.selectors.values.IntValueSelector;
import org.chocosolver.solver.search.strategy.selectors.variables.*;
import org.chocosolver.solver.search.strategy.strategy.IntStrategy;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Variable;
import org.chocosolver.util.objects.setDataStructures.iterable.IntIterableRangeSet;

import java.util.ArrayList;
import java.util.Arrays;

public class SinglePattern2Constraint extends BenzAIConstraint {

	private final boolean useSymmetries;
	
	private final Pattern pattern;
	private int [][] neighborGraph;

	private ArrayList<Integer> absentHexagons;
	private ArrayList<Integer> presentHexagons;
	private ArrayList<Integer> unknownHexagons;
	
	private IntVar[] patternCorrespondances;
	private int [] T1;

	private BoolVar symmetry;

	private final boolean verbose = false;

	private int[][] matrixTable;

	private final VariableStrategy variableStrategy;
	private final ValueStrategy valueStrategy;
	private final OrderStrategy orderStrategy;
	
	public SinglePattern2Constraint(Pattern pattern, boolean useSymmetries, VariableStrategy variableStrategy, ValueStrategy strategy, OrderStrategy orderStrategy) {
		this.pattern = pattern;
		this.useSymmetries = useSymmetries;
		this.variableStrategy = variableStrategy;
		this.valueStrategy = strategy;
		this.orderStrategy = orderStrategy;
	}

	@Override
	public void buildVariables() {
		GeneralModel generalModel = getGeneralModel();

		presentHexagons = new ArrayList<>();
		absentHexagons = new ArrayList<>();
		unknownHexagons = new ArrayList<>();

		symmetry = generalModel.getProblem().boolVar("axial_symmetry");

		int nbHexagonsCoronenoid = 0;
		for (int line = 0; line < generalModel.getDiameter(); line++) {
			for (int column = 0; column < generalModel.getDiameter(); column++) {
				if (generalModel.getHexagonIndicesMatrix()[line][column] != -1)
					nbHexagonsCoronenoid++;
			}
		}
		
		patternCorrespondances = new IntVar[nbHexagonsCoronenoid];
		T1 = new int [generalModel.getDiameter() * generalModel.getDiameter()];
		int[] t2 = new int[nbHexagonsCoronenoid];

		Arrays.fill(T1, -1);

		int index = 0;
		int coroIndex = 0;
		for (int line = 0; line < generalModel.getDiameter(); line++) {
			for (int column = 0; column < generalModel.getDiameter(); column++) {
				if (generalModel.getHexagonIndicesMatrix()[line][column] != -1) {
					T1[generalModel.getHexagonIndicesMatrix()[line][column]] = index;
					System.out.println(generalModel.getHexagonIndicesMatrix()[line][column] + " " + index);
					t2[index] = coroIndex;
					patternCorrespondances[index] = generalModel.getProblem().intVar("f_c_" + coroIndex, -1, pattern.getNbNodes()-1);  	// attention
					index ++;					
				}
				coroIndex++;
			}
		}
		buildNeighborGraph();
	}

	@Override
	public void postConstraints() {
		GeneralModel generalModel = getGeneralModel();

		generalModel.getModelPropertySet().getById("diameter").addExpression(new BinaryNumericalExpression("diameter", "<", 45));//TODO ???
		System.out.println("Scope");
		for (IntVar x : patternCorrespondances)
			System.out.println(x.toString());
		
		for (int i = 0 ; i < pattern.getNbNodes() ; i++) {
			
			PatternLabel label = pattern.getLabel(i);

			IntVar count;
			if (label == PatternLabel.POSITIVE)
				count = generalModel.getProblem().intVar("count_" + i, 1, 1);
			else count = generalModel.getProblem().intVar("count_" + i, 0, 1);
			
			generalModel.getProblem().count(i, patternCorrespondances, count).post();
					
			if (label == PatternLabel.NEUTRAL)
				unknownHexagons.add(i);
			else if (label == PatternLabel.POSITIVE)
				presentHexagons.add(i);
			else if (label == PatternLabel.NEGATIVE)
				absentHexagons.add(i);
		}

		IntVar[] varClause;
		IntIterableRangeSet[] valClause;

		for (int j = 0; j < generalModel.getDiameter() * generalModel.getDiameter(); j++) {

			if (T1[j] != -1) {
			
			/*
			 * Si un hexagone du coronénoïde est lié à un hexagon présent du pattern, alors il doit être présent dans la solution
			 */	
				
				if (!presentHexagons.isEmpty()) {
					
					varClause = new IntVar[pattern.getNbNodes()+2-presentHexagons.size()];
					valClause = new IntIterableRangeSet[pattern.getNbNodes()+2-presentHexagons.size()];
					
					int index = 0;
					
					for (int i = 0; i < pattern.getNbNodes(); i++) {
						if (! presentHexagons.contains(i)) {
					    	varClause[index] = patternCorrespondances[T1[j]];
					    	valClause[index] = new IntIterableRangeSet(i);
					    	index++;
						}
					}
	
					varClause[index] = patternCorrespondances[T1[j]];
					valClause[index] = new IntIterableRangeSet(-1);
					index++;
					varClause[index] = generalModel.getBenzenoidVerticesBVArray(j);
					valClause[index] = new IntIterableRangeSet(1);

					generalModel.getProblem().getClauseConstraint().addClause(varClause, valClause);
				}

				/*
				 * Si un hexagone du coronénoïde est lié à un hexagone absent du pattern i, alors il ne doit pas être présent dans la solutions
				 */

				if (!absentHexagons.isEmpty()) {
					varClause = new IntVar[pattern.getNbNodes()+2-absentHexagons.size()];
					valClause = new IntIterableRangeSet[pattern.getNbNodes()+2-absentHexagons.size()];
					
					int index = 0;
					
					for (int i = 0; i < pattern.getNbNodes(); i++) {
						if (! absentHexagons.contains(i)) {
					    	varClause[index] = patternCorrespondances[T1[j]];
					    	valClause[index] = new IntIterableRangeSet(i);
					    	index++;
						}
					}
	
					varClause[index] = patternCorrespondances[T1[j]];
					valClause[index] = new IntIterableRangeSet(-1);
					index++;
					varClause[index] = generalModel.getBenzenoidVerticesBVArray(j);
					valClause[index] = new IntIterableRangeSet(0);

					generalModel.getProblem().getClauseConstraint().addClause(varClause, valClause);
				}

				varClause = new IntVar[presentHexagons.size() + unknownHexagons.size() + 2];
				valClause = new IntIterableRangeSet[presentHexagons.size() + unknownHexagons.size() + 2];

				int index = 0;
				for (Integer presentHexagon : presentHexagons) {
					varClause[index] = patternCorrespondances[T1[j]];
					valClause[index] = new IntIterableRangeSet(presentHexagon);
					index++;
				}

				for (Integer unknownHexagon : unknownHexagons) {
					varClause[index] = patternCorrespondances[T1[j]];
					valClause[index] = new IntIterableRangeSet(unknownHexagon);
					index++;
				}

			varClause[index] = patternCorrespondances[T1[j]];
			valClause[index] = new IntIterableRangeSet(-1);
			index++;
			varClause[index] = generalModel.getBenzenoidVerticesBVArray(j);
			valClause[index] = new IntIterableRangeSet(0);			
			}
		}

		StringBuilder tableConstraints = new StringBuilder();
		tableConstraints.append("TUPLES\n");

		if (useSymmetries)
			buildMatrixTable();
		else
			buildMatrixTableWithoutSymmetries();

		for (int line = 0; line < generalModel.getHexagonIndicesMatrix().length; line++) {
			for (int column = 0; column < generalModel.getHexagonIndicesMatrix()[line].length; column++) {
				if (generalModel.getHexagonIndicesMatrix()[line][column] != -1) {

					int index = generalModel.getHexagonIndicesMatrix()[line][column];

					ArrayList<IntVar> tupleList = new ArrayList<>();
				
					tupleList.add(patternCorrespondances[T1[index]]);
					
					ArrayList<Integer> okColumns = new ArrayList<>();
					okColumns.add(0);

					ArrayList<Integer> nokColumns = new ArrayList<>();

					for (int i = 0; i < 6; i++) {

						int neighbor = neighborGraph[index][i];

						if (neighbor == -1) {
							nokColumns.add(i + 1);
						}

						else {
							tupleList.add(patternCorrespondances[T1[neighbor]]);
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
			System.out.println(tableConstraints);
	}

	@Override
	public void addVariables() {
//		generalModel.addWatchedVariable(patternCorrespondances);
	}

	@Override
	public void changeSolvingStrategy() {
		GeneralModel generalModel = getGeneralModel();

		IntVar[] branchingVariables = new IntVar[generalModel.getHexBoolVars().length + patternCorrespondances.length];

		int index = 0;

		switch (orderStrategy) {

		case CHANNELING_FIRST:

			for (BoolVar x : generalModel.getHexBoolVars()) {
				branchingVariables[index] = x;
				index++;
			}

			for (IntVar x : patternCorrespondances) {
				System.out.println(index + " " + x.toString());
				branchingVariables[index] = x;
				index++;
			}

			break;

		case CHANNELING_LAST:

			for (IntVar x : patternCorrespondances) {
				branchingVariables[index] = x;
				index++;
			}

			for (BoolVar x : generalModel.getHexBoolVars()) {
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

		ArrayList<Integer> okLines = new ArrayList<>();

		for (int i = 0; i < matrixTable.length; i++) {

			boolean ok = true;

			for (int column : nokColumns) {

				int v = matrixTable[i][column];

				if (v != -1) {
					if (!(pattern.getLabel(v) == PatternLabel.NEUTRAL || pattern.getLabel(v) == PatternLabel.NEGATIVE)) {
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
			for (int i = 0 ; i < pattern.getNbNodes() ; i++)
				table.add(i, 0);
		}

		return table;
	}

	private void buildMatrixTableWithoutSymmetries() {
		matrixTable = new int [6 * pattern.getNbNodes()][8];

		StringBuilder tableString = new StringBuilder();
		tableString.append("TABLE\n");

		Tuples table = new Tuples(true);

		/*
		 * Rotations simples
		 */

		int index = 0;
		
		for (int shift = 0 ; shift < 6 ; shift ++) {
			
			int [][] newNeighborGraph = new int [pattern.getNbNodes()][6]; 
			
			for (int i = 0 ; i < pattern.getNbNodes() ; i++) {
				for (int j = 0 ; j < 6 ; j++) {
					newNeighborGraph[i][(j + shift) % 6] = pattern.getNeighbor(i, j);
				}
			}
			
			for (int i = 0 ; i < pattern.getNbNodes() ; i++) {	
			
				int [] tuple = new int[7];
				tuple[0] = i;

				System.arraycopy(newNeighborGraph[i], 0, tuple, 1, 6);

				// tuple[7] = 0;

				for (int j = 0; j < 7; j++)
					tableString.append(tuple[j]).append("\t\t");
				tableString.append("\n");

				table.add(tuple);

				System.arraycopy(tuple, 0, matrixTable[index], 0, tuple.length);

				index++;
			}
		}

		if (verbose)
			System.out.println(tableString + "\n\n");
	}

	private void buildMatrixTable() {		
		matrixTable = new int [2 * 6 * pattern.getNbNodes()][8];
		StringBuilder tableString = new StringBuilder();
		tableString.append("TABLE\n");

		Tuples table = new Tuples(true);

		/*
		 * Rotations simples
		 */

		int index = 0;
		
		for (int shift = 0 ; shift < 6 ; shift ++) {
			
			int [][] newNeighborGraph = new int [pattern.getNbNodes()][6]; 
			
			for (int i = 0 ; i < pattern.getNbNodes() ; i++) {
				for (int j = 0 ; j < 6 ; j++) {
					newNeighborGraph[i][(j + shift) % 6] = pattern.getNeighbor(i, j);
				}
			}
			
			for (int i = 0 ; i < pattern.getNbNodes() ; i++) {	
			
				int [] tuple = new int[8];
				tuple[0] = i;

				System.arraycopy(newNeighborGraph[i], 0, tuple, 1, 6);

				tuple[7] = 0;

				for (int j = 0; j < 7; j++)
					tableString.append(tuple[j]).append("\t\t");
				tableString.append("\n");

				table.add(tuple);

				System.arraycopy(tuple, 0, matrixTable[index], 0, tuple.length);

				index++;
			}
		}

		/*
		 * Rotations symétries axiales
		 */

		tableString.append("\n");
		
		int [][] neighborGraphSymmetry = new int[pattern.getNbNodes()][6];
		
		for (int i = 0 ; i < pattern.getNbNodes() ; i++) {
			neighborGraphSymmetry[i][0] = pattern.getNeighbor(i, 2);
			neighborGraphSymmetry[i][1] = pattern.getNeighbor(i, 1);
			neighborGraphSymmetry[i][2] = pattern.getNeighbor(i, 0);
			neighborGraphSymmetry[i][3] = pattern.getNeighbor(i, 5);
			neighborGraphSymmetry[i][4] = pattern.getNeighbor(i, 4);
			neighborGraphSymmetry[i][5] = pattern.getNeighbor(i, 3);
			
		}
		
		for (int shift = 0 ; shift < 6 ; shift ++) {
			
			int [][] newNeighborGraph = new int [pattern.getNbNodes()][6]; 
			
			for (int i = 0 ; i < pattern.getNbNodes() ; i++) {
				for (int j = 0 ; j < 6 ; j++) {
					newNeighborGraph[i][(j + shift) % 6] = neighborGraphSymmetry[i][j];
				}
			}
			
			for (int i = 0 ; i < pattern.getNbNodes() ; i++) {	
			
				int [] tuple = new int[8];
				tuple[0] = i;

				System.arraycopy(newNeighborGraph[i], 0, tuple, 1, 6);

				tuple[7] = 1;

				for (int j = 0; j < 7; j++)
					tableString.append(tuple[j]).append("\t\t");
				tableString.append("\n");

				table.add(tuple);

				matrixTable[index] = tuple;
				index++;
			}
		}

		if (verbose)
			System.out.println(tableString + "\n\n");
	}
	
	private  void buildNeighborGraph() {
		GeneralModel generalModel = getGeneralModel();

		neighborGraph = new int [generalModel.getDiameter() * generalModel.getDiameter()][6];

		for (int[] ints : neighborGraph) {
			Arrays.fill(ints, -1);
		}

		for (int line = 0; line < generalModel.getHexagonIndicesMatrix().length; line++) {
			for (int column = 0; column < generalModel.getHexagonIndicesMatrix()[line].length; column++) {

				if (generalModel.getHexagonIndicesMatrix()[line][column] != -1) {

					int index = generalModel.getHexagonIndicesMatrix()[line][column];

					// High-Right
					if (line > 0)
						neighborGraph[index][0] = generalModel.getHexagonIndicesMatrix()[line - 1][column];

					// Right
					if (column < generalModel.getHexagonIndicesMatrix()[line].length - 1)
						neighborGraph[index][1] = generalModel.getHexagonIndicesMatrix()[line][column + 1];

					// Down-Right
					if (line < generalModel.getHexagonIndicesMatrix()[line].length - 1
							&& column < generalModel.getHexagonIndicesMatrix()[line].length - 1)
						neighborGraph[index][2] = generalModel.getHexagonIndicesMatrix()[line + 1][column + 1];

					// Down-Left
					if (line < generalModel.getHexagonIndicesMatrix()[line].length - 1)
						neighborGraph[index][3] = generalModel.getHexagonIndicesMatrix()[line + 1][column];

					// Left
					if (column > 0)
						neighborGraph[index][4] = generalModel.getHexagonIndicesMatrix()[line][column - 1];

					// High-Left
					if (line > 0 && column > 0)
						neighborGraph[index][5] = generalModel.getHexagonIndicesMatrix()[line - 1][column - 1];
				}
			}
		}
	}
}