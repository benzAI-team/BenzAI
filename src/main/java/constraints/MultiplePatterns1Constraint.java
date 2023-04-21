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
import org.chocosolver.util.objects.setDataStructures.iterable.IntIterableRangeSet;

import generator.GeneralModel;
import generator.OrderStrategy;
import generator.ValueStrategy;
import generator.VariableStrategy;
import generator.patterns.Pattern;
import generator.patterns.PatternOccurences;
import generator.patterns.PatternsInterraction;
import utils.Utils;

public class MultiplePatterns1Constraint extends BenzAIConstraint {

	private final ArrayList<Pattern> patterns;
	private ArrayList<PatternOccurences> patternsOccurences;

	private ArrayList<BoolVar[]> allPresences;

	private ArrayList<ArrayList<Integer>> allPresentHexagons, allAbsentHexagons, allUnknownHexagons;

	private final VariableStrategy variableStrategy;
	private final ValueStrategy valueStrategy;
	private final OrderStrategy orderStrategy;

	private PatternsInterraction interraction;
	
	public MultiplePatterns1Constraint(ArrayList<Pattern> patterns,
									   VariableStrategy variableStrategy, ValueStrategy valueStrategy, OrderStrategy orderStrategy) {
		this.patterns = patterns;
		this.variableStrategy = variableStrategy;
		this.valueStrategy = valueStrategy;
		this.orderStrategy = orderStrategy;
	}

	public MultiplePatterns1Constraint(ArrayList<Pattern> patterns,
									   VariableStrategy variableStrategy, ValueStrategy valueStrategy, OrderStrategy orderStrategy, PatternsInterraction interraction) {
		this.patterns = patterns;
		this.variableStrategy = variableStrategy;
		this.valueStrategy = valueStrategy;
		this.orderStrategy = orderStrategy;
		this.interraction = interraction;
	}

	@Override
	public void buildVariables() {

		computePatternOccurences();

		if (interraction == null)
			interraction = PatternsInterraction.DISJUNCT;
		
		allPresences = new ArrayList<>();
		allUnknownHexagons = new ArrayList<>();
		allPresentHexagons = new ArrayList<>();
		allAbsentHexagons = new ArrayList<>();

		for (int i = 0; i < patterns.size(); i++) {

			Pattern pattern = patterns.get(i);
			PatternOccurences patternOccurences = patternsOccurences.get(i);

			BoolVar[] presences = new BoolVar[patternOccurences.getOccurences().size()];
			for (int j = 0; j < presences.length; j++)
				presences[j] = getGeneralModel().getProblem().boolVar("presence_" + i + "_" + j);

			allPresences.add(presences);

			ArrayList<Integer> presentHexagons = new ArrayList<>();
			ArrayList<Integer> absentHexagons = new ArrayList<>();
			ArrayList<Integer> unknownHexagons = new ArrayList<>();

			for (int j = 0; j < pattern.getNbNodes(); j++) {

				int label = pattern.getLabel(j);

				if (label == 1)
					unknownHexagons.add(j);

				else if (label == 2)
					presentHexagons.add(j);

				else if (label == 3)
					absentHexagons.add(j);
			}

			allPresentHexagons.add(presentHexagons);
			allAbsentHexagons.add(absentHexagons);
			allUnknownHexagons.add(unknownHexagons);

		}

	}

	@Override
	public void postConstraints() {
		GeneralModel generalModel = getGeneralModel();

		for (int f = 0; f < patterns.size(); f++) {

			PatternOccurences patternOccurences = patternsOccurences.get(f);
			BoolVar[] presences = allPresences.get(f);
			ArrayList<Integer[]> occurences = patternOccurences.getOccurences();

			for (int i = 0; i < occurences.size(); i++) {

				ArrayList<Integer> present = patternOccurences.getAllPresentHexagons().get(i);
				ArrayList<Integer> absent = patternOccurences.getAllAbsentHexagons().get(i);

				IntVar[] varClause = new IntVar[2];
				IntIterableRangeSet[] valClause = new IntIterableRangeSet[2];

				varClause[0] = presences[i];
				valClause[0] = new IntIterableRangeSet(0);

				for (Integer j : absent) {
					varClause[1] = generalModel.getGraphVertices()[j];
					valClause[1] = new IntIterableRangeSet(0);
					generalModel.getProblem().getClauseConstraint().addClause(varClause, valClause);
				}

				for (Integer j : present) {
					varClause[1] = generalModel.getGraphVertices()[j];
					valClause[1] = new IntIterableRangeSet(1);
					generalModel.getProblem().getClauseConstraint().addClause(varClause, valClause);
				}

			}
			generalModel.getProblem().sum(presences, "=", 1).post();
		}

		for (int i = 0; i < patternsOccurences.size(); i++) {
			for (int j = (i + 1); j < patternsOccurences.size(); j++) {

				if (i != j) {

					PatternOccurences patternOccurences1 = patternsOccurences.get(i);
					PatternOccurences patternOccurences2 = patternsOccurences.get(j);

					BoolVar[] presences1 = allPresences.get(i);
					BoolVar[] presences2 = allPresences.get(j);

					for (int k = 0; k < patternOccurences1.size(); k++) {
						for (int l = 0; l < patternOccurences2.size(); l++) {

							ArrayList<Integer> presentHexagons1 = patternOccurences1.getAllPresentHexagons().get(k);
							ArrayList<Integer> absentHexagons1 = patternOccurences1.getAllAbsentHexagons().get(k);
							ArrayList<Integer> unknownHexagons1 = patternOccurences1.getAllUnknownHexagons().get(k);
							ArrayList<Integer> outterHexagons1 = patternOccurences1.getAllOutterHexagons().get(k);

							ArrayList<Integer> presentHexagons2 = patternOccurences2.getAllPresentHexagons().get(l);
							ArrayList<Integer> absentHexagons2 = patternOccurences2.getAllAbsentHexagons().get(l);
							ArrayList<Integer> unknownHexagons2 = patternOccurences2.getAllUnknownHexagons().get(l);
							ArrayList<Integer> outterHexagons2 = patternOccurences2.getAllOutterHexagons().get(l);

							ArrayList<Integer> set1 = new ArrayList<>();
							set1.addAll(presentHexagons1);
							set1.addAll(absentHexagons1);
							set1.addAll(unknownHexagons1);
							set1.addAll(outterHexagons1);

							ArrayList<Integer> set2 = new ArrayList<>();
							set2.addAll(presentHexagons2);
							set2.addAll(absentHexagons2);
							set2.addAll(unknownHexagons2);
							set2.addAll(outterHexagons2);

							BoolVar e1 = presences1[k];
							BoolVar e2 = presences2[l];

							switch(interraction) {
							
								case DISJUNCT:
									if (!Utils.areDisjoint(set1, set2)) {
										
										BoolVar [] clauseVariables = new BoolVar[] {e1, e2};
										IntIterableRangeSet[] clauseValues = new IntIterableRangeSet [] {new IntIterableRangeSet(0), new IntIterableRangeSet(0)};

										generalModel.getProblem().getClauseConstraint().addClause(clauseVariables, clauseValues);
									}
									break;
									
								case DISJUNCT_NN:
									ArrayList<Integer> intersection = Utils.intersection(set1, set2);
									if (intersection.size() > 0) {

										boolean onlyNegativeAndNeutrals = true;
										for (Integer h : intersection) {
											if (presentHexagons1.contains(h) || presentHexagons2.contains(h)) {
												onlyNegativeAndNeutrals = false;
												break;
											}
										}

										if (!onlyNegativeAndNeutrals) {
											BoolVar[] clauseVariables = new BoolVar[] { e1, e2 };
											IntIterableRangeSet[] clauseValues = new IntIterableRangeSet[] {
													new IntIterableRangeSet(0), new IntIterableRangeSet(0) };

											generalModel.getProblem().getClauseConstraint().addClause(clauseVariables,
													clauseValues);
										}
									}
									break;
									
								case UNDISJUNCT:
									//DO_NOTHING
									break;
									
							}
							
							// Enti�rements disjoints
//							if (!Utils.areDisjoint(set1, set2)) {
//								
//								BoolVar [] clauseVariables = new BoolVar[] {e1, e2};
//								IntIterableRangeSet[] clauseValues = new IntIterableRangeSet [] {new IntIterableRangeSet(0), new IntIterableRangeSet(0)};
//
//								generalModel.getProblem().getClauseConstraint().addClause(clauseVariables, clauseValues);
//							}

							// Disjoints sur N�gatifs/Neutres
//							ArrayList<Integer> intersection = Utils.intersection(set1, set2);
//							if (intersection.size() > 0) {
//
//								boolean onlyNegativeAndNeutrals = true;
//								for (Integer h : intersection) {
//									if (presentHexagons1.contains(h) || presentHexagons2.contains(h)) {
//										onlyNegativeAndNeutrals = false;
//										break;
//									}
//								}
//
//								if (!onlyNegativeAndNeutrals) {
//									BoolVar[] clauseVariables = new BoolVar[] { e1, e2 };
//									IntIterableRangeSet[] clauseValues = new IntIterableRangeSet[] {
//											new IntIterableRangeSet(0), new IntIterableRangeSet(0) };
//
//									generalModel.getProblem().getClauseConstraint().addClause(clauseVariables,
//											clauseValues);
//								}
//							}

						}

					}
				}
			}
		}
	}

	@Override
	public void addVariables() {
		// for (BoolVar [] presences : allPresences)
		// generalModel.addWatchedVariable(presences);
	}

	@Override
	public void changeSolvingStrategy() {
		GeneralModel generalModel = getGeneralModel();

		int nbPresencesVariables = 0;

		for (BoolVar[] presences : allPresences)
			nbPresencesVariables += presences.length;

		BoolVar[] presences = new BoolVar[nbPresencesVariables];
		int index = 0;
		for (BoolVar[] p : allPresences) {
			for (BoolVar x : p) {
				presences[index] = x;
				index++;
			}
		}

		BoolVar[] branchingVariables = new BoolVar[generalModel.getChanneling().length + presences.length];
		index = 0;

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

	private void computePatternOccurences() {
		GeneralModel generalModel = getGeneralModel();

		patternsOccurences = new ArrayList<>();

		for (Pattern pattern : patterns) {

			ArrayList<Pattern> symmetricPatterns = pattern.computeRotations();

			PatternOccurences patternOccurences = new PatternOccurences();

			for (Pattern f : symmetricPatterns) {
				PatternOccurences translations = generalModel.computeTranslations(f);
				patternOccurences.addAll(translations);
			}

			patternsOccurences.add(patternOccurences);

		}
	}
}
