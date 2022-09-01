package modules;

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
import generator.fragments.Fragment;
import generator.fragments.FragmentOccurences;
import generator.fragments.PatternsInterraction;
import utils.Utils;

public class MultipleFragments1Module extends Module {

	private ArrayList<Fragment> fragments;
	private ArrayList<FragmentOccurences> fragmentsOccurences;

	private ArrayList<BoolVar[]> allPresences;

	private ArrayList<ArrayList<Integer>> allPresentHexagons, allAbsentHexagons, allUnknownHexagons;

	private VariableStrategy variableStrategy;
	private ValueStrategy valueStrategy;
	private OrderStrategy orderStrategy;

	private PatternsInterraction interraction;
	
	public MultipleFragments1Module(ArrayList<Fragment> fragments,
			VariableStrategy variableStrategy, ValueStrategy valueStrategy, OrderStrategy orderStrategy) {
		this.fragments = fragments;
		this.variableStrategy = variableStrategy;
		this.valueStrategy = valueStrategy;
		this.orderStrategy = orderStrategy;
	}

	public MultipleFragments1Module(ArrayList<Fragment> fragments,
			VariableStrategy variableStrategy, ValueStrategy valueStrategy, OrderStrategy orderStrategy, PatternsInterraction interraction) {
		this.fragments = fragments;
		this.variableStrategy = variableStrategy;
		this.valueStrategy = valueStrategy;
		this.orderStrategy = orderStrategy;
		this.interraction = interraction;
	}

	@Override
	public void buildVariables() {

		computeFragmentOccurences();

		if (interraction == null)
			interraction = PatternsInterraction.DISJUNCT;
		
		allPresences = new ArrayList<>();
		allUnknownHexagons = new ArrayList<>();
		allPresentHexagons = new ArrayList<>();
		allAbsentHexagons = new ArrayList<>();

		for (int i = 0; i < fragments.size(); i++) {

			Fragment fragment = fragments.get(i);
			FragmentOccurences fragmentOccurences = fragmentsOccurences.get(i);

			BoolVar[] presences = new BoolVar[fragmentOccurences.getOccurences().size()];
			for (int j = 0; j < presences.length; j++)
				presences[j] = getGeneralModel().getProblem().boolVar("presence_" + i + "_" + j);

			allPresences.add(presences);

			ArrayList<Integer> presentHexagons = new ArrayList<>();
			ArrayList<Integer> absentHexagons = new ArrayList<>();
			ArrayList<Integer> unknownHexagons = new ArrayList<>();

			for (int j = 0; j < fragment.getNbNodes(); j++) {

				int label = fragment.getLabel(j);

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

		for (int f = 0; f < fragments.size(); f++) {

			FragmentOccurences fragmentOccurences = fragmentsOccurences.get(f);
			BoolVar[] presences = allPresences.get(f);
			ArrayList<Integer[]> occurences = fragmentOccurences.getOccurences();

			for (int i = 0; i < occurences.size(); i++) {

				ArrayList<Integer> present = fragmentOccurences.getAllPresentHexagons().get(i);
				ArrayList<Integer> absent = fragmentOccurences.getAllAbsentHexagons().get(i);

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

		for (int i = 0; i < fragmentsOccurences.size(); i++) {
			for (int j = (i + 1); j < fragmentsOccurences.size(); j++) {

				if (i != j) {

					FragmentOccurences fragmentOccurences1 = fragmentsOccurences.get(i);
					FragmentOccurences fragmentOccurences2 = fragmentsOccurences.get(j);

					BoolVar[] presences1 = allPresences.get(i);
					BoolVar[] presences2 = allPresences.get(j);

					for (int k = 0; k < fragmentOccurences1.size(); k++) {
						for (int l = 0; l < fragmentOccurences2.size(); l++) {

							ArrayList<Integer> presentHexagons1 = fragmentOccurences1.getAllPresentHexagons().get(k);
							ArrayList<Integer> absentHexagons1 = fragmentOccurences1.getAllAbsentHexagons().get(k);
							ArrayList<Integer> unknownHexagons1 = fragmentOccurences1.getAllUnknownHexagons().get(k);
							ArrayList<Integer> outterHexagons1 = fragmentOccurences1.getAllOutterHexagons().get(k);

							ArrayList<Integer> presentHexagons2 = fragmentOccurences2.getAllPresentHexagons().get(l);
							ArrayList<Integer> absentHexagons2 = fragmentOccurences2.getAllAbsentHexagons().get(l);
							ArrayList<Integer> unknownHexagons2 = fragmentOccurences2.getAllUnknownHexagons().get(l);
							ArrayList<Integer> outterHexagons2 = fragmentOccurences2.getAllOutterHexagons().get(l);

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

	private void computeFragmentOccurences() {
		GeneralModel generalModel = getGeneralModel();

		fragmentsOccurences = new ArrayList<>();

		for (Fragment fragment : fragments) {

			ArrayList<Fragment> symmetricFragments = fragment.computeRotations();

			FragmentOccurences fragmentOccurences = new FragmentOccurences();

			for (Fragment f : symmetricFragments) {
				FragmentOccurences translations = generalModel.computeTranslations(f);
				fragmentOccurences.addAll(translations);
			}

			fragmentsOccurences.add(fragmentOccurences);

		}
	}
}
