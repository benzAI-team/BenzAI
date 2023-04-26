package constraints;

import java.util.ArrayList;
import java.util.Arrays;

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
import org.chocosolver.util.objects.setDataStructures.iterable.IntIterableRangeSet;
import generator.GeneralModel;
import generator.OrderStrategy;
import generator.ValueStrategy;
import generator.VariableStrategy;
import generator.patterns.Pattern;

public class MultiplePatterns2Constraint extends BenzAIConstraint {

	private final ArrayList<Pattern> patterns;

	private ArrayList<ArrayList<Integer>> allAbsentHexagons;
	private ArrayList<ArrayList<Integer>> allPresentHexagons;
	private ArrayList<ArrayList<Integer>> allUnknownHexagons;

	private ArrayList<IntVar[]> patternCorrespondancesAllHexagons;
	private ArrayList<IntVar[]> patternCorrespondancesInternalCrowns;

	private ArrayList<Integer> additionalCrowns;

	private int[] T1;

	private int maxOrder;

	private BoolVar[] symmetries;

	private ArrayList<Integer[][]> matricesTables;

	private final VariableStrategy variableStrategy;
	private final ValueStrategy valueStrategy;
	private final OrderStrategy orderStrategy;
	
	public MultiplePatterns2Constraint(ArrayList<Pattern> patterns, VariableStrategy variableStrategy, ValueStrategy valueStrategy, OrderStrategy orderStrategy) {
		this.patterns = patterns;
		this.variableStrategy = variableStrategy;
		this.valueStrategy = valueStrategy;
		this.orderStrategy = orderStrategy;
		computeMaxOrder();
	}

	@Override
	public void buildVariables() {
		GeneralModel generalModel = getGeneralModel();

		additionalCrowns = new ArrayList<>();

		generalModel.buildNeighborGraphWithOutterHexagons(maxOrder);
		generalModel.buildAdjacencyMatrixWithOutterHexagons();

		allPresentHexagons = new ArrayList<>();
		allAbsentHexagons = new ArrayList<>();
		allUnknownHexagons = new ArrayList<>();

		for (int i = 0; i < patterns.size(); i++) {
			allPresentHexagons.add(new ArrayList<>());
			allAbsentHexagons.add(new ArrayList<>());
			allUnknownHexagons.add(new ArrayList<>());
		}

		symmetries = new BoolVar[patterns.size()];
		for (int i = 0; i < symmetries.length; i++)
			symmetries[i] = generalModel.getProblem().boolVar("symm_" + i);

		int nbInternalHexagons = 0;
		for (int line = 0; line < generalModel.getDiameter(); line++) {
			for (int column = 0; column < generalModel.getDiameter(); column++) {
				if (generalModel.getCoordsMatrix()[line][column] != -1)
					nbInternalHexagons++;
			}
		}

		int nbTotalHexagons = 0;

		for (int i = 0; i < generalModel.getNeighborGraphOutterHexagons().size(); i++) {
			boolean hasNeighbor = false;
			for (int j = 0; j < 6; j++)
				if (generalModel.getNeighborGraphOutterHexagons().get(i).get(j) != -1) {
					hasNeighbor = true;
					break;
				}

			if (hasNeighbor)
				nbTotalHexagons++;
		}

		int nbExternalHexagons = generalModel.getNeighborGraphOutterHexagons().size()
				- (generalModel.getDiameter() * generalModel.getDiameter());

		patternCorrespondancesAllHexagons = new ArrayList<>();
		ArrayList<IntVar[]> patternCorrespondancesAdditionalCrowns = new ArrayList<>();
		patternCorrespondancesInternalCrowns = new ArrayList<>();

		for (int i = 0; i < patterns.size(); i++) {
			patternCorrespondancesAllHexagons.add(new IntVar[nbTotalHexagons]);
			patternCorrespondancesAdditionalCrowns.add(new IntVar[nbExternalHexagons]);
			patternCorrespondancesInternalCrowns.add(new IntVar[nbInternalHexagons]);
		}

		T1 = new int[generalModel.getDiameter() * generalModel.getDiameter() + nbExternalHexagons];
		Arrays.fill(T1, -1);

		int index = 0;
		for (int line = 0; line < generalModel.getDiameter(); line++) {
			for (int column = 0; column < generalModel.getDiameter(); column++) {
				if (generalModel.getCoordsMatrix()[line][column] != -1) {
					T1[generalModel.getCoordsMatrix()[line][column]] = index;
					index++;
				}
			}
		}

		for (int i = generalModel.getDiameter() * generalModel.getDiameter(); i < generalModel
				.getNeighborGraphOutterHexagons().size(); i++) {
			T1[i] = index;
			additionalCrowns.add(index);
			index++;
		}

		for (int i = 0; i < patterns.size(); i++) {

			int coroIndex = 0;
			Pattern pattern = patterns.get(i);

			ArrayList<Integer> presentHexagons = allPresentHexagons.get(i);
			ArrayList<Integer> absentHexagons = allAbsentHexagons.get(i);
			ArrayList<Integer> unknownHexagons = allUnknownHexagons.get(i);

			for (int j = 0; j < pattern.getNbNodes(); j++) {

				if (pattern.getLabel(j) == 1)
					unknownHexagons.add(j);

				else if (pattern.getLabel(j) == 2)
					presentHexagons.add(j);

				else if (pattern.getLabel(j) == 3)
					absentHexagons.add(j);
			}

			int[] domainInternalHexagons = new int[1 + presentHexagons.size() + absentHexagons.size()
					+ unknownHexagons.size()];
			int[] domainExternalHexagons = new int[1 + absentHexagons.size() + unknownHexagons.size()];

			index = 1;
			domainInternalHexagons[0] = -1;

			for (Integer h : presentHexagons) {
				domainInternalHexagons[index] = h;
				index++;
			}

			for (Integer h : absentHexagons) {
				domainInternalHexagons[index] = h;
				index++;
			}

			for (Integer h : unknownHexagons) {
				domainInternalHexagons[index] = h;
				index++;
			}

			index = 1;
			domainExternalHexagons[0] = -1;

			for (Integer h : absentHexagons) {
				domainExternalHexagons[index] = h;
				index++;
			}

			for (Integer h : unknownHexagons) {
				domainExternalHexagons[index] = h;
				index++;
			}

			index = 0;

			for (int line = 0; line < generalModel.getDiameter(); line++) {
				for (int column = 0; column < generalModel.getDiameter(); column++) {
					if (generalModel.getCoordsMatrix()[line][column] != -1) {

						patternCorrespondancesAllHexagons.get(i)[index] = generalModel.getProblem()
								.intVar("fc_" + i + "_" + coroIndex, domainInternalHexagons);
						index++;
					}

					coroIndex++;
				}
			}

			for (int j = generalModel.getDiameter() * generalModel.getDiameter(); j < generalModel
					.getNeighborGraphOutterHexagons().size(); j++) {

				patternCorrespondancesAllHexagons.get(i)[index] = generalModel.getProblem()
						.intVar("fc_" + i + "_" + coroIndex, domainExternalHexagons);
				index++;
				coroIndex++;
			}

			IntVar[] correspondances = patternCorrespondancesAllHexagons.get(i);

			int index2 = 0;
			for (Integer hexagon : additionalCrowns) {

				IntVar x = correspondances[hexagon];
				patternCorrespondancesAdditionalCrowns.get(i)[index2] = x;
				index2++;
			}

			index2 = 0;
			for (int j = 0; j < generalModel.getDiameter() * generalModel.getDiameter(); j++) {
				if (T1[j] != -1) {
					IntVar x = correspondances[T1[j]];
					patternCorrespondancesInternalCrowns.get(i)[index2] = x;
					index2++;
				}
			}
		}

		buildNeighborGraph();
	}

	@Override
	public void postConstraints() {
		GeneralModel generalModel = getGeneralModel();

		for (int i = 0; i < patterns.size(); i++) {

			Pattern pattern = patterns.get(i);

			ArrayList<Integer> presentHexagons = allPresentHexagons.get(i);
			@SuppressWarnings("unused")
			ArrayList<Integer> unknownHexagons = allUnknownHexagons.get(i);
			ArrayList<Integer> absentHexagons = allAbsentHexagons.get(i);

			IntVar[] correspondancesAllHexagons = patternCorrespondancesAllHexagons.get(i);
			IntVar[] correspondancesInternalCrowns = patternCorrespondancesInternalCrowns.get(i);

			for (int j = 0; j < pattern.getNbNodes(); j++) {

				int label = pattern.getLabel(j);

				IntVar countAllHexagons = generalModel.getProblem().intVar("count_all_" + j, 0, 1);				
				generalModel.getProblem().count(j, correspondancesAllHexagons, countAllHexagons).post();

				IntVar countInternalHexagons = generalModel.getProblem().intVar("count_internal_" + j, 0, 1);
				generalModel.getProblem().count(j, correspondancesInternalCrowns, countInternalHexagons).post();

				if (label == 1) { // UNKNOWN_HEXAGON
					generalModel.getProblem().arithm(countAllHexagons, "=", 1).post();
				}

				else if (label == 2) { // PRESENT_HEXAGONS
					generalModel.getProblem().arithm(countInternalHexagons, "=", 1).post();
				}

				else if (label == 3) { // ABSENT_HEXAGONS
					generalModel.getProblem().arithm(countAllHexagons, "=", 1).post();
				}
			}

			IntVar[] varClause;
			IntIterableRangeSet[] valClause;
			
			for (int j = 0; j < generalModel.getDiameter() * generalModel.getDiameter(); j++) {

				if (T1[j] != -1) {

					/*
					 * (i) - Si un hexagone du coron�no�de est li� � un hexagon pr�sent du
					 * pattern i, alors il doit �tre pr�sent dans la solution
					 */

					if (presentHexagons.size() > 0) {
						
						varClause = new IntVar[pattern.getNbNodes()+2-presentHexagons.size()];
						valClause = new IntIterableRangeSet[pattern.getNbNodes()+2-presentHexagons.size()];
						
						int index = 0;
						
						for (int val = 0; val < pattern.getNbNodes(); val++) {
							if (! presentHexagons.contains(val)) {
						    	varClause[index] = correspondancesAllHexagons[T1[j]];
						    	valClause[index] = new IntIterableRangeSet(val);
						    	index++;
							}
						}
		
						varClause[index] = correspondancesAllHexagons[T1[j]];
						valClause[index] = new IntIterableRangeSet(-1);
						index++;
						varClause[index] = generalModel.getGraphVertices()[j];
						valClause[index] = new IntIterableRangeSet(1);
						
						generalModel.getProblem().getClauseConstraint().addClause(varClause, valClause);
					}

					/*
					 * (ii) - Si un hexagone du coron�no�de est li� � un hexagone absent du
					 * pattern i, alors il ne doit pas �tre pr�sent dans la solutions
					 */

					if (absentHexagons.size() > 0) {		
						
						varClause = new IntVar[pattern.getNbNodes()+2-absentHexagons.size()];
						valClause = new IntIterableRangeSet[pattern.getNbNodes()+2-absentHexagons.size()];
						
						int index = 0;
						
						for (int val = 0; val < pattern.getNbNodes(); val++) {
							if (! absentHexagons.contains(val)) {
						    	varClause[index] = correspondancesAllHexagons[T1[j]];
						    	valClause[index] = new IntIterableRangeSet(val);
						    	index++;
							}
						}
		
						varClause[index] = correspondancesAllHexagons[T1[j]];
						valClause[index] = new IntIterableRangeSet(-1);
						index++;
						varClause[index] = generalModel.getGraphVertices()[j];
						valClause[index] = new IntIterableRangeSet(0);
						
						generalModel.getProblem().getClauseConstraint().addClause(varClause, valClause);
					}

				}
			}

			/*
			 * Table constraints
			 */

			buildMatricesTables();

			Tuples completeTable = buildCompleteTable(i);

			if (maxOrder > 0) {

				for (int j = 0; j < T1.length; j++) {

					if (T1[j] != -1) {

						int index = T1[j];

						if (additionalCrowns.contains(index)) {

							ArrayList<IntVar> tupleList = new ArrayList<>();
							tupleList.add(correspondancesAllHexagons[index]);

							ArrayList<Integer> okColumns = new ArrayList<>();
							okColumns.add(0);

							ArrayList<Integer> nokColumns = new ArrayList<>();

							for (int k = 0; k < 6; k++) {

								int neighbor = generalModel.getNeighborGraphOutterHexagons().get(j).get(k);

								if (neighbor == -1) {
									nokColumns.add(k + 1);
								}

								else {
									tupleList.add(correspondancesAllHexagons[T1[neighbor]]);
									okColumns.add(k + 1);
								}
							}

							tupleList.add(symmetries[i]);
							okColumns.add(7);

							IntVar[] tuple = new IntVar[tupleList.size()];
							for (int k = 0; k < tuple.length; k++)
								tuple[k] = tupleList.get(k);

							Tuples tableAdditionalCrowns = buildSubTableWithAbsentAndUnknownHexagons(okColumns,
									nokColumns, i);

							generalModel.getProblem().table(tuple, tableAdditionalCrowns, "CT+").post();
							
						}

						else {

							IntVar[] tuple = new IntVar[8];

							tuple[0] = correspondancesAllHexagons[index];

							for (int k = 0; k < 6; k++) {

								int neighbor = generalModel.getNeighborGraphOutterHexagons().get(j).get(k);
								tuple[k + 1] = correspondancesAllHexagons[T1[neighbor]];
							}

							tuple[7] = symmetries[i];
						
							generalModel.getProblem().table(tuple, completeTable, "CT+").post();
						}
					}
				}
			}

			else {
				for (int j = 0; j < T1.length; j++) {

					if (T1[j] != -1) {

						int index = T1[j];

						ArrayList<IntVar> tupleList = new ArrayList<>();
						tupleList.add(correspondancesAllHexagons[index]);

						ArrayList<Integer> okColumns = new ArrayList<>();
						okColumns.add(0);

						ArrayList<Integer> nokColumns = new ArrayList<>();

						for (int k = 0; k < 6; k++) {

							int neighbor = generalModel.getNeighborGraph()[j][k];

							if (neighbor == -1) {
								nokColumns.add(k + 1);
							}

							else {
								tupleList.add(correspondancesAllHexagons[T1[neighbor]]);
								okColumns.add(k + 1);
							}
						}

						tupleList.add(symmetries[i]);
						okColumns.add(7);

						IntVar[] tuple = new IntVar[tupleList.size()];
						for (int k = 0; k < tuple.length; k++)
							tuple[k] = tupleList.get(k);

						Tuples tableAdditionalCrowns = buildSubTable(okColumns,
								nokColumns, i);
						
						generalModel.getProblem().table(tuple, tableAdditionalCrowns, "CT+").post();

					}
				}
			}
		}

		/*
		 * Contraintes de relations entre les motifs
		 */

		int maxNbNodes = 0;
		for (Pattern pattern : patterns)
			if (pattern.getNbNodes() > maxNbNodes)
				maxNbNodes = pattern.getNbNodes();

		/*
		 * Motifs disjoints
		 */

		for (int j : T1) {
			if (j != -1) {

				IntVar count = generalModel.getProblem().intVar("count_" + j, 0, patterns.size());
				IntVar[] set = new IntVar[patterns.size()];

				for (int f = 0; f < patterns.size(); f++) {
					set[f] = patternCorrespondancesAllHexagons.get(f)[j];
				}

				generalModel.getProblem().count(-1, set, count).post();
				generalModel.getProblem().arithm(count, ">=", patterns.size() - 1).post();
			}
		}
	}

	private void buildMatricesTables() {

		matricesTables = new ArrayList<>();

		for (Pattern pattern : patterns) {

			Integer[][] matrixTable = new Integer[2 * 6 * pattern.getNbNodes()][8];

			/*
			 * Rotations simples
			 */

			int index = 0;

			for (int shift = 0; shift < 6; shift++) {

				int[][] newNeighborGraph = new int[pattern.getNbNodes()][6];

				for (int i = 0; i < pattern.getNbNodes(); i++) {
					for (int j = 0; j < 6; j++) {
						newNeighborGraph[i][(j + shift) % 6] = pattern.getNeighbor(i, j);
					}
				}

				for (int i = 0; i < pattern.getNbNodes(); i++) {

					int[] tuple = new int[8];
					tuple[0] = i;

					System.arraycopy(newNeighborGraph[i], 0, tuple, 1, 6);

					tuple[7] = 0;

					for (int j = 0; j < tuple.length; j++) {
						matrixTable[index][j] = tuple[j];
					}

					index++;
				}
			}

			/*
			 * Rotations sym�tries axiales
			 */

			int[][] neighborGraphSymmetry = new int[pattern.getNbNodes()][6];

			for (int i = 0; i < pattern.getNbNodes(); i++) {
				neighborGraphSymmetry[i][0] = pattern.getNeighbor(i, 2);
				neighborGraphSymmetry[i][1] = pattern.getNeighbor(i, 1);
				neighborGraphSymmetry[i][2] = pattern.getNeighbor(i, 0);
				neighborGraphSymmetry[i][3] = pattern.getNeighbor(i, 5);
				neighborGraphSymmetry[i][4] = pattern.getNeighbor(i, 4);
				neighborGraphSymmetry[i][5] = pattern.getNeighbor(i, 3);

			}

			for (int shift = 0; shift < 6; shift++) {

				int[][] newNeighborGraph = new int[pattern.getNbNodes()][6];

				for (int i = 0; i < pattern.getNbNodes(); i++) {
					for (int j = 0; j < 6; j++) {
						newNeighborGraph[i][(j + shift) % 6] = neighborGraphSymmetry[i][j];
					}
				}

				for (int i = 0; i < pattern.getNbNodes(); i++) {

					int[] tuple = new int[8];
					tuple[0] = i;

					System.arraycopy(newNeighborGraph[i], 0, tuple, 1, 6);

					tuple[7] = 1;

					for (int j = 0; j < tuple.length; j++) {
						matrixTable[index][j] = tuple[j];
					}

					index++;
				}
			}

			matricesTables.add(matrixTable);
		}
	}

	private Tuples buildCompleteTable(int indexPattern) {

		Pattern pattern = patterns.get(indexPattern);
		Integer[][] matrixTable = matricesTables.get(indexPattern);
		
		
		
		Tuples table = new Tuples(true);

		int [] tuple = new int[matrixTable[0].length];
		table.setUniversalValue(-2);
		tuple[0] = -1;
		for (int i = 1 ; i < matrixTable[0].length; i++)
			tuple[i] = -2;
		table.add(tuple);

		for (Integer[] integers : matrixTable) {

			for (int j = 0; j < integers.length; j++) {
				tuple[j] = integers[j];
			}

			table.add(tuple);
		}

		if (table.nbTuples() == 0) {
			for (int i = 0; i < pattern.getNbNodes(); i++)
				table.add(i, 0);
		}

		return table;
	}

	private Tuples buildSubTable(ArrayList<Integer> okColumns, ArrayList<Integer> nokColumns, int indexPattern) {

		Pattern pattern = patterns.get(indexPattern);
		Integer[][] matrixTable = matricesTables.get(indexPattern);

		Tuples table = new Tuples(true);
		
		ArrayList<Integer> okLines = new ArrayList<>();
		
		for (int i = 0 ; i < matrixTable.length ; i++) {
			
			boolean ok = true;
			
			for (int column : nokColumns) {
				
				int v = matrixTable[i][column];
				
				if (v != -1) {
					
					if (!(pattern.getLabel(v) == 1 || pattern.getLabel(v) == 3)) {
						ok = false;
					}
				}
			}
			
			if (ok)
				okLines.add(i);
		}
		
		int [] tuple = new int[okColumns.size()];
		table.setUniversalValue(-2);
		tuple[0] = -1;
		for (int i = 1 ; i < okColumns.size(); i++)
			tuple[i] = -2;
		table.add(tuple);
		
		for (int i : okLines) {
			for (int j = 0 ; j < okColumns.size() ; j++) {
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

	private Tuples buildSubTableWithAbsentAndUnknownHexagons(ArrayList<Integer> okColumns,
			ArrayList<Integer> nokColumns, int indexPattern) {

		Pattern pattern = patterns.get(indexPattern);
		ArrayList<Integer> presentHexagons = allPresentHexagons.get(indexPattern);
		Integer[][] matrixTable = matricesTables.get(indexPattern);

		Tuples table = new Tuples(true);

		ArrayList<Integer> okLines = new ArrayList<>();

		for (int i = 0; i < matrixTable.length; i++) {

			int hexagon = matrixTable[i][0];
			if (!presentHexagons.contains(hexagon))
				okLines.add(i);

		}

		int [] tuple = new int[okColumns.size()];
		table.setUniversalValue(-2);
		tuple[0] = -1;
		for (int i = 1 ; i < okColumns.size(); i++)
			tuple[i] = -2;
		table.add(tuple);
		
		for (int i : okLines) {

			for (int j = 0; j < okColumns.size(); j++) {
				tuple[j] = matrixTable[i][okColumns.get(j)];
			}

			table.add(tuple);
		}

		if (table.nbTuples() == 0) {
			for (int i = 0; i < pattern.getNbNodes(); i++)
				table.add(i, 0);
		}

		return table;
	}

	@Override
	public void addVariables() {
		// for (IntVar [] array : patternCorrespondancesAllHexagons)
		// generalModel.addWatchedVariable(array);
	}

	@Override
	public void changeSolvingStrategy() {
		GeneralModel generalModel = getGeneralModel();

		int nbFC = 0;

		for (IntVar[] fc : patternCorrespondancesAllHexagons)
			nbFC += fc.length;

		IntVar[] correspondances = new IntVar[nbFC];
		int index = 0;
		for (IntVar[] fc : patternCorrespondancesAllHexagons) {
			for (IntVar x : fc) {
				correspondances[index] = x;
				index++;
			}
		}

		IntVar [] branchingVariables = new IntVar[generalModel.getChanneling().length + correspondances.length];
		index = 0;
		
		switch (orderStrategy) {
		
			case CHANNELING_FIRST:
				
				for (BoolVar x : generalModel.getChanneling()) {
					branchingVariables[index] = x;
					index ++;
				}
				
				for (IntVar x : correspondances) {
					branchingVariables[index] = x;
					index ++;
				}
				
				break;
				
			case CHANNELING_LAST:
				
				for (IntVar x : correspondances) {
					branchingVariables[index] = x;
					index ++;
				}
				
				for (BoolVar x : generalModel.getChanneling()) {
					branchingVariables[index] = x;
					index ++;
				}				

				break;
		}
		
		
		
		VariableSelector<IntVar> variableSelector = null;
		
		switch(variableStrategy) {
		
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
		
		switch(valueStrategy) {
		
			case INT_MIN:
				valueSelector = new IntDomainMin();
				break;
			case INT_MAX:
				valueSelector = new IntDomainMax();
				break;
		}
		
		generalModel.getProblem().getSolver().setSearch(new IntStrategy(branchingVariables, variableSelector, valueSelector));
	}

	@Override
	public void changeGraphVertices() {
		// TODO Auto-generated method stub
	}

	private void buildNeighborGraph() {
		GeneralModel generalModel = getGeneralModel();

		int[][] neighborGraph = new int[generalModel.getDiameter() * generalModel.getDiameter()][6];

		for (int[] ints : neighborGraph) {
			Arrays.fill(ints, -1);
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

	private void computeMaxOrder() {

		maxOrder = 0;

		for (Pattern pattern : patterns) {

			if (pattern.getOrder() > maxOrder)
				maxOrder = pattern.getOrder();
		}
	}
}