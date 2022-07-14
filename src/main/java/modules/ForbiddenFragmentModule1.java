package modules;

import java.util.ArrayList;

import org.chocosolver.solver.constraints.Constraint;
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
import generator.GeneralModel;
import generator.OrderStrategy;
import generator.ValueStrategy;
import generator.VariableStrategy;
import generator.fragments.Fragment;
import generator.fragments.FragmentOccurences;

public class ForbiddenFragmentModule1 extends Module{
	
	private Fragment fragment;
	
	private ArrayList<Fragment> symmetricFragments;
	
	private ArrayList<Integer> presentHexagons, unknownHexagons, absentHexagons;
	private BoolVar [] presences;
	
	private FragmentOccurences fragmentOccurences;
	
	private VariableStrategy variableStrategy;
	private ValueStrategy valueStrategy;
	private OrderStrategy orderStrategy;
	
	public ForbiddenFragmentModule1(GeneralModel generalModel, Fragment fragment, VariableStrategy variableStrategy, ValueStrategy valueStrategy, OrderStrategy orderStrategy) {
		super(generalModel);
		this.fragment = fragment;
		this.variableStrategy = variableStrategy;
		this.valueStrategy = valueStrategy;
		this.orderStrategy = orderStrategy;
	}

	@Override
	public void setPriority() {
		priority = 1;
	}

	@Override
	public void buildVariables() {
		
		computeFragmentOccurences();
		

		presences = new BoolVar[fragmentOccurences.getOccurences().size()];
		for (int i = 0 ; i < presences.length ; i++)
			presences[i] = generalModel.getProblem().boolVar("presence_" + i);
		
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
			
			Constraint ifCstr = generalModel.getProblem().arithm(presences[i], "=", 1);
			
			Constraint [] andCstr = new Constraint[present.size() + absent.size()];
			
			int index = 0;
			
			for (Integer j : absent) {
				andCstr[index] = generalModel.getProblem().arithm(generalModel.getGraphVertices()[j], "=", 0);
				index ++;
			}
			
			for (Integer j : present) {
				andCstr[index] = generalModel.getProblem().arithm(generalModel.getGraphVertices()[j], "=", 1);
				index ++;
			}	
			
			Constraint thenCstr = generalModel.getProblem().and(andCstr);
			generalModel.getProblem().ifThen(ifCstr, thenCstr);
			generalModel.getProblem().ifOnlyIf(ifCstr, thenCstr);
			
			generalModel.getProblem().sum(presences, "=", 0).post();
			
			

/*			
			//Sens 1
			
			IntVar[] varClause;
			IntIterableRangeSet[] valClause;
			int index;
			

			
			varClause = new IntVar [present.size() + absent.size() + 1];
			valClause = new IntIterableRangeSet[present.size() + absent.size() + 1];
			
			index = 0;
			
			for (Integer j : absent) {
				varClause[index] = generalModel.getWatchedGraphVertices()[j];
				valClause[index] = new IntIterableRangeSet(1);
				index ++;
			}
			
			for (Integer j : present) {
				varClause[index] = generalModel.getWatchedGraphVertices()[j];
				valClause[index] = new IntIterableRangeSet(0);
				index ++;
			}
			
			varClause[index] = presences[i];
			valClause[index] = new IntIterableRangeSet(1);
			
			generalModel.getProblem().getClauseConstraint().addClause(varClause, valClause);
		
			
		
			//Sens 2
			
			BoolVar pi = presences[i];
			
			index = 0;
			
			for (Integer j : absent) {
				
				varClause = new IntVar [] {
						pi,
						generalModel.getWatchedGraphVertices()[j]
				};
				
				valClause = new IntIterableRangeSet[] {
						new IntIterableRangeSet(0), 
						new IntIterableRangeSet(0)
				};
				
				generalModel.getProblem().getClauseConstraint().addClause(varClause, valClause);
			}
			
			for (Integer j : present) {
				
				varClause = new IntVar [] {
						pi,
						generalModel.getWatchedGraphVertices()[j]
				};
				
				valClause = new IntIterableRangeSet[] {
						new IntIterableRangeSet(0), 
						new IntIterableRangeSet(1)
				};
				
				generalModel.getProblem().getClauseConstraint().addClause(varClause, valClause);
			}
*/
		}
	}

	@Override
	public void addVariables() {
		//generalModel.addWatchedVariable(presences);
	}

	@Override
	public void changeSolvingStrategy() {	
		
		IntVar [] branchingVariables = new IntVar[generalModel.getChanneling().length + presences.length];
		int index = 0;
		
		switch(orderStrategy) {
			
			case CHANNELING_FIRST:
				
				for (BoolVar x : generalModel.getChanneling()) {
					branchingVariables[index] = x;
					index ++;
				}
				
				for (BoolVar x : presences) {
					branchingVariables[index] = x;
					index ++;
				}
				
				break;
				
			case CHANNELING_LAST:
				
				for (BoolVar x : presences) {
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
	public void changeGraphVertices() { }
	
	private void computeFragmentOccurences() {
		
		symmetricFragments = fragment.computeRotations();	
		fragmentOccurences = new FragmentOccurences();
		
		for (Fragment f : symmetricFragments)
			fragmentOccurences.addAll(generalModel.computeTranslations(f));
	}

}