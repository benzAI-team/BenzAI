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
import org.chocosolver.util.iterators.DisposableValueIterator;
import org.chocosolver.util.objects.setDataStructures.iterable.IntIterableRangeSet;

import generator.GeneralModel;
import generator.OrderStrategy;
import generator.ValueStrategy;
import generator.VariableStrategy;
import generator.fragments.Fragment;

public class MultipleFragments3Module extends Module{

	private ArrayList<Fragment> fragments;
	
	ArrayList<ArrayList<Integer>> allAbsentHexagons;
	ArrayList<ArrayList<Integer>> allPresentHexagons;
	ArrayList<ArrayList<Integer>> allUnknownHexagons;
	
	private ArrayList<IntVar []> allCoronenoidCorrespondances;
	
	private int maxOrder;
	
	private Tuples table;
	
	private VariableStrategy variableStrategy;
	private ValueStrategy valueStrategy;
	private OrderStrategy orderStrategy;
	
	public MultipleFragments3Module(GeneralModel generalModel, ArrayList<Fragment> fragments, VariableStrategy variableStrategy, ValueStrategy valueStrategy, OrderStrategy orderStrategy) {
		super(generalModel);
		this.fragments = fragments;
		this.variableStrategy = variableStrategy;
		this.valueStrategy = valueStrategy;
		this.orderStrategy = orderStrategy;
		computeMaxOrder();
	}

	@Override
	public void setPriority() {
		priority = 1;
	}

	@Override
	public void buildVariables() {
		
		generalModel.buildNeighborGraphWithOutterHexagons(maxOrder);
		generalModel.buildAdjacencyMatrixWithOutterHexagons();
			
		buildTable();
		
		allPresentHexagons = new ArrayList<>();
		allAbsentHexagons = new ArrayList<>();
		allUnknownHexagons = new ArrayList<>();
		
		for (int i = 0 ; i < fragments.size() ; i++) {
			allPresentHexagons.add(new ArrayList<Integer>());
			allAbsentHexagons.add(new ArrayList<Integer>());
			allUnknownHexagons.add(new ArrayList<Integer>());
		}
		
		allCoronenoidCorrespondances = new ArrayList<IntVar []>();
		
		ArrayList<Integer> domainList = new ArrayList<Integer>();
		domainList.add(-1);
		
		for (int i = 0 ; i < generalModel.getDiameter() ; i++) {
			for (int j = 0 ; j < generalModel.getDiameter() ; j++) {
				if (generalModel.getCoordsMatrix()[i][j] != -1) 
					domainList.add(generalModel.getCoordsMatrix()[i][j]);
			}
		}

		int [] domain = new int[domainList.size()];
		for (int i = 0 ; i < domain.length ; i++)
			domain[i] = domainList.get(i);
		
		for (int i = generalModel.getDiameter() * generalModel.getDiameter() ; i < generalModel.getNeighborGraphOutterHexagons().size() ; i++) {
			domainList.add(i);
		}
		
		int [] domainWithOutterHexagons = new int[domainList.size()];
		for (int i = 0 ; i < domainWithOutterHexagons.length ; i++)
			domainWithOutterHexagons[i] = domainList.get(i);
		
		for (int i = 0 ; i < fragments.size() ; i++) {
			
			Fragment fragment = fragments.get(i);
			
			IntVar[] coronenoidCorrespondances = new IntVar[fragment.getNbNodes()];
			
			for (int j = 0 ; j < fragment.getNbNodes() ; j++) {
				
				if (fragment.getLabel(j) == 2) 
					coronenoidCorrespondances[j] = generalModel.getProblem().intVar("cc_" + i + "_" + j, domain);
				
				else 
					coronenoidCorrespondances[j] = generalModel.getProblem().intVar("cc_" + i + "_" + j, domainWithOutterHexagons);
					
			}
			
			allCoronenoidCorrespondances.add(coronenoidCorrespondances);
		}
		
	}

	@Override
	public void postConstraints() {
		
		for (int i = 0 ; i < fragments.size() ; i++) {
			
			Fragment fragment = fragments.get(i);
			
			ArrayList<Integer> unknownHexagons = allUnknownHexagons.get(i);
			ArrayList<Integer> presentHexagons = allPresentHexagons.get(i);
			ArrayList<Integer> absentHexagons = allAbsentHexagons.get(i);
			
			IntVar [] coronenoidCorrespondances = allCoronenoidCorrespondances.get(i);

			/*
			 * Settings fragments's labels
			 */

			for (int j = 0 ; j < fragment.getNbNodes() ; j++) {
				
				int labelI = fragment.getLabel(j);
				
				if (labelI == 1)
					unknownHexagons.add(j);
				
				else if (labelI == 2)
					presentHexagons.add(j);
				
				else if (labelI == 3)
					absentHexagons.add(j);
			}
			
			/*
			 * All-Different constraint
			 */
			
			generalModel.getProblem().allDifferent(coronenoidCorrespondances).post();
		
			/*
			 * Table constraints for edges integrity
			 */
		
			for (int j = 0 ; j < fragment.getNbNodes() ; j++) {
				for (int k = (j + 1) ; k < fragment.getNbNodes() ; k++) {
					
					if (fragment.getMatrix()[j][k] == 1) {
						
						IntVar si = coronenoidCorrespondances[j];
						IntVar sj = coronenoidCorrespondances[k];
						
						generalModel.getProblem().table(new IntVar[] {si, sj}, table, "CT+").post();
					}
				}
				
			}
			
			/*
			 * Label integrity constraints
			 */
			
			IntVar[] varClause;
			IntIterableRangeSet[] valClause;
			
			for (Integer j : presentHexagons) {
				for (int k = 0 ; k < generalModel.getDiameter() * generalModel.getDiameter() ; k++) {
					
					if (generalModel.getWatchedGraphVertices()[k] != null) {
						//(i)
						
						varClause = new IntVar[coronenoidCorrespondances[j].getDomainSize()];
						valClause = new IntIterableRangeSet[coronenoidCorrespondances[j].getDomainSize()];
						
						int index = 0;
						
						DisposableValueIterator vit = coronenoidCorrespondances[j].getValueIterator(true);
						while(vit.hasNext()){
						    int v = vit.next();
						    if (v != k)
						    {
						    	varClause[index] = coronenoidCorrespondances[j];
						    	valClause[index] = new IntIterableRangeSet(v);
						    	index++;
						    }
						}
						vit.dispose();

						varClause[index] = generalModel.getWatchedGraphVertices()[k];
						valClause[index] = new IntIterableRangeSet(1);
						
						generalModel.getProblem().getClauseConstraint().addClause(varClause, valClause);
						
					}
				}
			}
			
			for (Integer j : absentHexagons) {
				for (int k = 0 ; k < generalModel.getDiameter() * generalModel.getDiameter() ; k++) {
					
					if (generalModel.getWatchedGraphVertices()[k] != null) {

						varClause = new IntVar[coronenoidCorrespondances[j].getDomainSize()];
						valClause = new IntIterableRangeSet[coronenoidCorrespondances[j].getDomainSize()];
						
						int index = 0;
						
						DisposableValueIterator vit = coronenoidCorrespondances[j].getValueIterator(true);
						while(vit.hasNext()){
						    int v = vit.next();
						    if (v != k)
						    {
						    	varClause[index] = coronenoidCorrespondances[j];
						    	valClause[index] = new IntIterableRangeSet(v);
						    	index++;
						    }
						}
						vit.dispose();

						varClause[index] = generalModel.getWatchedGraphVertices()[k];
						valClause[index] = new IntIterableRangeSet(0);
						
						generalModel.getProblem().getClauseConstraint().addClause(varClause, valClause);
					}
				}
			}
			
			/*
			 * Disjoints fragments
			 */
			
			int nbCorrespondances = 0;
			for (IntVar [] correspondance : allCoronenoidCorrespondances) 
				nbCorrespondances += correspondance.length;
			
			IntVar [] allCorrespondances = new IntVar[nbCorrespondances];
			
			int index = 0;
			for (IntVar [] correspondance : allCoronenoidCorrespondances) {
				for (int j = 0 ; j < correspondance.length ; j++) {
					allCorrespondances[index] = correspondance[j];
					index ++;
				}
					
			}
			
			generalModel.getProblem().allDifferent(allCorrespondances).post();
		}
	}

	@Override
	public void addWatchedVariables() {	
	//	for (IntVar [] coronenoidCorrespondances : allCoronenoidCorrespondances)
	//		generalModel.addWatchedVariable(coronenoidCorrespondances);
	}

	@Override
	public void changeSolvingStrategy() {
		
		int nbCorrespondances = 0;
		
		for (IntVar [] correspondances : allCoronenoidCorrespondances)
			nbCorrespondances += correspondances.length;
		
		IntVar [] correspondances = new IntVar[nbCorrespondances];
		
		int index = 0;
		for (IntVar [] coronenoidCorrespondances : allCoronenoidCorrespondances) {
			for (IntVar x : coronenoidCorrespondances) {
				correspondances[index] = x;
				index ++;
			}
		}
		
		IntVar [] branchingVariables = new IntVar[generalModel.getChanneling().length + correspondances.length];
		index = 0;
		
		switch(orderStrategy) {
		
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
	public void changeWatchedGraphVertices() {
		
	}
	
	private void computeMaxOrder() {
		
		maxOrder = 0;
		
		for (Fragment fragment : fragments) {
			
			if (fragment.getOrder() > maxOrder)
				maxOrder = fragment.getOrder();
		}
	}
	
	private void buildTable() {
		
		table = new Tuples(true);
		
		int [][] matrix = generalModel.getAdjacencyMatrixOutterHexagons();
		
		for (int i = 0 ; i < matrix.length ; i++) {
			for (int j = (i + 1) ; j < matrix.length ; j++) {
				
				if (matrix[i][j] == 1) {
					table.add(i, j);
					table.add(j, i);
				}
			}
		}
	}
}
