package constraints;

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
import org.chocosolver.util.iterators.DisposableValueIterator;
import org.chocosolver.util.objects.setDataStructures.iterable.IntIterableRangeSet;

import generator.GeneralModel;
import generator.OrderStrategy;
import generator.ValueStrategy;
import generator.VariableStrategy;
import generator.patterns.Pattern;

public class SinglePattern3Constraint extends BenzAIConstraint {

	private final Pattern pattern;
	
	ArrayList<Integer> absentHexagons;
	ArrayList<Integer> presentHexagons;
	ArrayList<Integer> unknownHexagons;

	private IntVar[] coronenoidCorrespondances;

	private final VariableStrategy variableStrategy;
	private final ValueStrategy valueStrategy;
	private final OrderStrategy orderStrategy;
	
	public SinglePattern3Constraint(Pattern pattern, VariableStrategy variableStrategy, ValueStrategy valueStrategy, OrderStrategy orderStrategy) {
		this.pattern = pattern;
		this.variableStrategy = variableStrategy;
		this.valueStrategy = valueStrategy;
		this.orderStrategy = orderStrategy;
	}

	@Override
	public void buildVariables() {
		GeneralModel generalModel = getGeneralModel();
		generalModel.buildNeighborGraphWithOutterHexagons(pattern.getOrder());
		generalModel.buildAdjacencyMatrixWithOutterHexagons();

		presentHexagons = new ArrayList<Integer>();
		absentHexagons = new ArrayList<Integer>();
		unknownHexagons = new ArrayList<Integer>();

		coronenoidCorrespondances = new IntVar[pattern.getNbNodes()];

		ArrayList<Integer> domainList = new ArrayList<Integer>();
		domainList.add(-1);

		for (int i = 0; i < generalModel.getDiameter(); i++) {
			for (int j = 0; j < generalModel.getDiameter(); j++) {
				if (generalModel.getHexagonIndices()[i][j] != -1)
					domainList.add(generalModel.getHexagonIndices()[i][j]);
			}
		}

		int[] domain = new int[domainList.size()];
		for (int i = 0; i < domain.length; i++)
			domain[i] = domainList.get(i);

		for (int i = generalModel.getDiameter() * generalModel.getDiameter(); i < generalModel
				.getNeighborGraphOutterHexagons().size(); i++) {
			domainList.add(i);
		}

		int[] domainWithOutterHexagons = new int[domainList.size()];
		for (int i = 0; i < domainWithOutterHexagons.length; i++)
			domainWithOutterHexagons[i] = domainList.get(i);
		
		for (int i = 0 ; i < pattern.getNbNodes() ; i++) {
		
			if (pattern.getLabel(i) == 2) 
				coronenoidCorrespondances[i] = generalModel.getProblem().intVar("coronenoid_correspondances_" + i, domain);
			
			else  
				coronenoidCorrespondances[i] = generalModel.getProblem().intVar("coronenoid_correspondances_" + i, domainWithOutterHexagons);
		}

	}

	@Override
	public void postConstraints() {
		GeneralModel generalModel = getGeneralModel();

		//Setting labels	
		for (int i = 0 ; i < pattern.getNbNodes() ; i++) {
			
			int labelI = pattern.getLabel(i);
			
			if (labelI == 1) 
				unknownHexagons.add(i);

			else if (labelI == 2)
				presentHexagons.add(i);

			else if (labelI == 3)
				absentHexagons.add(i);
		}

		// all-different constraint
		generalModel.getProblem().allDifferent(coronenoidCorrespondances).post();

		// table constraint for edges integrity
		Tuples table = buildTable();
		
		for (int i = 0 ; i < pattern.getNbNodes() ; i++) {
			for (int j = (i + 1) ; j < pattern.getNbNodes() ; j++) {
				
				if (pattern.getMatrix()[i][j] == 1) {
					IntVar si = coronenoidCorrespondances[i];
					IntVar sj = coronenoidCorrespondances[j];

					generalModel.getProblem().table(new IntVar[] { si, sj }, table, "CT+").post();

					generalModel.increaseDegree(si.getName());
					generalModel.increaseDegree(sj.getName());
				}
			}
		}

		// label integrity constraints

		IntVar[] varClause;
		IntIterableRangeSet[] valClause;

		for (Integer i : presentHexagons) {
			for (int j = 0; j < generalModel.getDiameter() * generalModel.getDiameter(); j++) {

				if (generalModel.getBenzenoidVerticesBVArray(j) != null) {
					// (i)

					varClause = new IntVar[coronenoidCorrespondances[i].getDomainSize()];
					valClause = new IntIterableRangeSet[coronenoidCorrespondances[i].getDomainSize()];

					int index = 0;

					DisposableValueIterator vit = coronenoidCorrespondances[i].getValueIterator(true);
					while (vit.hasNext()) {
						int v = vit.next();
						if (v != j) {
							varClause[index] = coronenoidCorrespondances[i];
							valClause[index] = new IntIterableRangeSet(v);
							index++;
						}
					}
					vit.dispose();

					varClause[index] = generalModel.getBenzenoidVerticesBVArray(j);
					valClause[index] = new IntIterableRangeSet(1);

					generalModel.getProblem().getClauseConstraint().addClause(varClause, valClause);

				}
			}
		}

		for (Integer i : absentHexagons) {
			for (int j = 0; j < generalModel.getDiameter() * generalModel.getDiameter(); j++) {

				if (generalModel.getBenzenoidVerticesBVArray(j) != null) {

					varClause = new IntVar[coronenoidCorrespondances[i].getDomainSize()];
					valClause = new IntIterableRangeSet[coronenoidCorrespondances[i].getDomainSize()];

					int index = 0;

					DisposableValueIterator vit = coronenoidCorrespondances[i].getValueIterator(true);
					while (vit.hasNext()) {
						int v = vit.next();
						if (v != j) {
							varClause[index] = coronenoidCorrespondances[i];
							valClause[index] = new IntIterableRangeSet(v);
							index++;
						}
					}
					vit.dispose();

					varClause[index] = generalModel.getBenzenoidVerticesBVArray(j);
					valClause[index] = new IntIterableRangeSet(0);

					generalModel.getProblem().getClauseConstraint().addClause(varClause, valClause);

				}
			}
		}

	}

	@Override
	public void addVariables() {
		// generalModel.addWatchedVariable(coronenoidCorrespondances);
	}

	@Override
	public void changeSolvingStrategy() {
		GeneralModel generalModel = getGeneralModel();

		IntVar[] branchingVariables = new IntVar[generalModel.getHexBoolVars().length
				+ coronenoidCorrespondances.length];

		int index = 0;

		switch (orderStrategy) {

		case CHANNELING_FIRST:

			for (BoolVar x : generalModel.getHexBoolVars()) {
				branchingVariables[index] = x;
				index++;
			}

			for (IntVar x : coronenoidCorrespondances) {
				branchingVariables[index] = x;
				index++;
			}

			break;

		case CHANNELING_LAST:

			for (IntVar x : coronenoidCorrespondances) {
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

	private Tuples buildTable() {

		Tuples table = new Tuples(true);		
		int [][] matrix = getGeneralModel().getAdjacencyMatrixOutterHexagons();
		
		for (int i = 0 ; i < matrix.length ; i++) {
			for (int j = (i + 1) ; j < matrix.length ; j++) {
				if (matrix[i][j] == 1) {
					table.add(i, j);
					table.add(j, i);
				}
			}
		}

		return table;
	}
}
