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

public class OccurenceFragmentModule extends Module{

	private Fragment fragment;
	private int nbOccurences;
	
	private ArrayList<Fragment> symmetricFragments;
	private ArrayList<Integer> presentHexagons, unknownHexagons, absentHexagons;
	private BoolVar [] presences;
	
	private IntVar occurenceVar;
	private BoolVar [] presences2;
	
	private FragmentOccurences fragmentOccurences;
	
	private VariableStrategy variablesStrategy;
	private ValueStrategy valueStrategy;
	private OrderStrategy orderStrategy;
	
	public OccurenceFragmentModule(Fragment fragment, int nbOccurences, VariableStrategy variablesStrategy, ValueStrategy valueStrategy, OrderStrategy orderStrategy) {
		this.fragment = fragment;
		this.nbOccurences = nbOccurences;
		this.variablesStrategy = variablesStrategy;
		this.valueStrategy = valueStrategy;
		this.orderStrategy = orderStrategy;
	}

	@Override
	public void buildVariables() {
		GeneralModel generalModel = getGeneralModel();

		computeFragmentOccurences();
		
		occurenceVar = generalModel.getProblem().intVar("n_e", 0, nbOccurences);
		
		presences = new BoolVar[fragmentOccurences.getOccurences().size()];
		for (int i = 0 ; i < presences.length ; i++)
			presences[i] = generalModel.getProblem().boolVar("presence_" + i);
		
		presences2 = new BoolVar[fragmentOccurences.getOccurences().size()];
		for (int i = 0 ; i < presences2.length ; i++)
			presences2[i] = generalModel.getProblem().boolVar("presence2_" + i);
		
		presentHexagons = new ArrayList<>();
		absentHexagons = new ArrayList<>();
		unknownHexagons = new ArrayList<>();
		
		for (int i = 0 ; i < fragment.getNbNodes() ; i++) {
			
			int label = fragment.getLabel(i);
			
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

		ArrayList<Integer []> occurences = fragmentOccurences.getOccurences();
		
		for (int i = 0 ; i < occurences.size() ; i++) {
			
			Integer [] occurence = occurences.get(i);
			
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
		
			IntVar[] varClause = new IntVar [2];
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
			
			/*
			 * gestion des presences2
			 */
			
			varClause = new IntVar [absent.size() + present.size() + 1];
			valClause = new IntIterableRangeSet[absent.size() + present.size() + 1];
			
			int index = 0;
			
			for (Integer j : absent) {
				varClause[index] = generalModel.getGraphVertices()[j];
				valClause[index] = new IntIterableRangeSet(1);
				index ++;
			}
			
			for (Integer j : present) {
				varClause[index] = generalModel.getGraphVertices()[j];
				valClause[index] = new IntIterableRangeSet(0);
				index ++;
			}
			
			varClause[index] = presences2[i];
			valClause[index] = new IntIterableRangeSet(1);
			
			generalModel.getProblem().getClauseConstraint().addClause(varClause, valClause);
		
		}
		
		//generalModel.getProblem().sum(presences, "=", 1).post();
		
		/*
		 * Exclusions mutuelles
		 */
		
		ArrayList<Integer> hexagons = new ArrayList<>();
		
		for (int i = 0 ; i < generalModel.getDiameter() ; i ++) {
			for (int j = 0 ; j < generalModel.getDiameter() ; j++) {
				if (generalModel.getCoordsMatrix()[i][j] != -1)
					hexagons.add(generalModel.getCoordsMatrix()[i][j]);
			}
		}
		
		for (int hexagon : generalModel.getOutterHexagonsIndexes())
			hexagons.add(hexagon);
		
		for (int hexagon : hexagons) {
			
			ArrayList<Integer> fragments = new ArrayList<>();
			
			for (int i = 0 ; i < fragmentOccurences.size() ; i++) {
				
				ArrayList<Integer> present = fragmentOccurences.getAllPresentHexagons().get(i);
				ArrayList<Integer> absent = fragmentOccurences.getAllAbsentHexagons().get(i);
				ArrayList<Integer> unknown = fragmentOccurences.getAllUnknownHexagons().get(i);
				ArrayList<Integer> outter = fragmentOccurences.getAllOutterHexagons().get(i);
			
				if (present.contains(hexagon) || absent.contains(hexagon) || unknown.contains(hexagon) || outter.contains(hexagon))
					fragments.add(i);
			}
			
			BoolVar [] e1 = new BoolVar[fragments.size()];
			BoolVar [] e2 = new BoolVar[fragments.size()];
			
			for (int i = 0 ; i < fragments.size() ; i++) {
				e1[i] = presences[fragments.get(i)];
				e2[i] = presences2[fragments.get(i)];
			}
			
			
			BoolVar ifVar = generalModel.getProblem().sum(e2, ">=", 1).reify();
			BoolVar thenVar = generalModel.getProblem().sum(e1, "=", 1).reify();
			
			IntVar [] varClause = new IntVar [] {
					ifVar,
					thenVar
			};
			
			IntIterableRangeSet [] valClause = new IntIterableRangeSet[] {
					new IntIterableRangeSet(0),
					new IntIterableRangeSet(1)
			};
			
			generalModel.getProblem().getClauseConstraint().addClause(varClause, valClause);
			
			
			//generalModel.getProblem().ifThen(generalModel.getProblem().sum(e2, ">=", 1), generalModel.getProblem().sum(e1, "=", 1));
		}
		
		generalModel.getProblem().sum(presences, "=", occurenceVar).post();
		
		/*
		 * Test (à supprimer)
		 */
		
		//generalModel.getProblem().arithm(occurenceVar, "=", 2).post();
	}

	@Override
	public void addVariables() {
		GeneralModel generalModel = getGeneralModel();
		generalModel.addVariable(occurenceVar);
		generalModel.addVariable(presences);
		generalModel.addVariable(presences2);
	}

	@Override
	public void changeSolvingStrategy() {
		GeneralModel generalModel = getGeneralModel();

		IntVar[] branchingVariables = new IntVar[generalModel.getChanneling().length + presences.length + presences2.length];
		int index = 0;
		
		switch (orderStrategy) {
			case CHANNELING_FIRST:
				
				for (BoolVar x : generalModel.getChanneling()) {
					branchingVariables[index] = x;
					index ++;
				}
				
				for (BoolVar x : presences) {
					branchingVariables[index] = x;
					index ++;
				}
				
				for (BoolVar x : presences2) {
					branchingVariables[index] = x;
					index ++;
				}
				
				break;
				
			case CHANNELING_LAST:
				
				for (BoolVar x : presences) {
					branchingVariables[index] = x;
					index ++;
				}
				
				for (BoolVar x : presences2) {
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
		
		switch(variablesStrategy) {
		
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
	
	private void computeFragmentOccurences() {
		
		symmetricFragments = fragment.computeRotations();
		
		fragmentOccurences = new FragmentOccurences();
		
		for (Fragment f : symmetricFragments)
			fragmentOccurences.addAll(getGeneralModel().computeTranslations(f));	
	}

}
