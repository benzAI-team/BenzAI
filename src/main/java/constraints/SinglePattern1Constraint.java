package constraints;

import java.util.ArrayList;

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
import generator.patterns.Pattern;
import generator.patterns.PatternOccurences;

public class SinglePattern1Constraint extends BenzAIConstraint {

	private final Pattern pattern;

	private ArrayList<Integer> presentHexagons;
	private ArrayList<Integer> absentHexagons;
	private BoolVar[] presences;

	private PatternOccurences patternOccurences;

	private final VariableStrategy variablesStrategy;
	private final ValueStrategy valueStrategy;
	private final OrderStrategy orderStrategy;

	public SinglePattern1Constraint(Pattern pattern, VariableStrategy variablesStrategy,
                                    ValueStrategy valueStrategy, OrderStrategy orderStrategy) {
		this.pattern = pattern;
		this.variablesStrategy = variablesStrategy;
		this.valueStrategy = valueStrategy;
		this.orderStrategy = orderStrategy;
	}

	@Override
	public void buildVariables() {

		computePatternOccurences();

		presences = new BoolVar[patternOccurences.getOccurences().size()];
		for (int i = 0; i < presences.length; i++)
			presences[i] = getGeneralModel().getProblem().boolVar("presence_" + i);

		presentHexagons = new ArrayList<>();
		absentHexagons = new ArrayList<>();
		ArrayList<Integer> unknownHexagons = new ArrayList<>();

		for (int i = 0; i < pattern.getNbNodes(); i++) {

			int label = pattern.getLabel(i);

			if (label == 1)
				unknownHexagons.add(i);

			else if (label == 2)
				presentHexagons.add(i);

			else if (label == 3)
				absentHexagons.add(i);
		}
	}

	@Override
	public void postConstraints() {
		GeneralModel generalModel = getGeneralModel();

		ArrayList<Integer[]> occurences = patternOccurences.getOccurences();

		for (int i = 0; i < occurences.size(); i++) {

			Integer[] occurence = occurences.get(i);

			ArrayList<Integer> present = new ArrayList<>();
			ArrayList<Integer> absent = new ArrayList<>();

			for (Integer hexagon : presentHexagons) {
				if (occurence[hexagon] != -1)
					present.add(occurence[hexagon]);
			}

			for (Integer hexagon : absentHexagons) {
				if (occurence[hexagon] != -1)
					absent.add(occurence[hexagon]);
			}

			IntVar[] varClause = new IntVar[2];
			IntIterableRangeSet[] valClause = new IntIterableRangeSet[2];

			varClause[0] = presences[i];
			valClause[0] = new IntIterableRangeSet(0);

			for (Integer j : absent) {
				varClause[1] = generalModel.getBenzenoidVerticesBVArray(j);
				valClause[1] = new IntIterableRangeSet(0);
				generalModel.getProblem().getClauseConstraint().addClause(varClause, valClause);
			}

			for (Integer j : present) {
				varClause[1] = generalModel.getBenzenoidVerticesBVArray(j);
				valClause[1] = new IntIterableRangeSet(1);
				generalModel.getProblem().getClauseConstraint().addClause(varClause, valClause);
			}
		}

		if (occurences.size() > 0) {
			generalModel.getProblem().sum(presences, "=", 1).post();
			for (Variable x : presences) {
				generalModel.increaseDegree(x.getName());
			}

		} else {
			generalModel.getProblem().sum(generalModel.getChanneling(), "=", 0).post();
			for (Variable x : generalModel.getChanneling()) {
				generalModel.increaseDegree(x.getName());
			}
		}
	}

	@Override
	public void addVariables() {
		// generalModel.addWatchedVariable(presences);
	}

	@Override
	public void changeSolvingStrategy() {
		GeneralModel generalModel = getGeneralModel();

		IntVar[] branchingVariables = new IntVar[generalModel.getChanneling().length + presences.length];
		int index = 0;

		switch (orderStrategy) {
		case CHANNELING_FIRST:

			for (BoolVar x : generalModel.getChanneling()) {
				branchingVariables[index] = x;
				index++;
			}

			for (BoolVar x : presences) {
				branchingVariables[index] = x;
				index++;
			}

			break;

		case CHANNELING_LAST:

			for (BoolVar x : presences) {
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

		switch (variablesStrategy) {

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

	private void computePatternOccurences() {

		ArrayList<Pattern> symmetricPatterns = pattern.computeRotations();

		patternOccurences = new PatternOccurences();

		for (Pattern f : symmetricPatterns)
			patternOccurences.addAll(getGeneralModel().computeTranslations(f));
	}

}
