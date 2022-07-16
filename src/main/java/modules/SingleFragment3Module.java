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
import org.chocosolver.util.objects.setDataStructures.iterable.IntIterableRangeSet;
import org.chocosolver.util.iterators.DisposableValueIterator;

import generator.GeneralModel;
import generator.OrderStrategy;
import generator.ValueStrategy;
import generator.VariableStrategy;
import generator.fragments.Fragment;

public class SingleFragment3Module extends Module{

	private Fragment fragment;
	
	ArrayList<Integer> absentHexagons;
	ArrayList<Integer> presentHexagons;
	ArrayList<Integer> unknownHexagons;
	
	private IntVar[] coronenoidCorrespondances;
	
	private VariableStrategy variableStrategy;
	private ValueStrategy valueStrategy;
	private OrderStrategy orderStrategy;
	
	public SingleFragment3Module(GeneralModel generalModel, Fragment fragment, VariableStrategy variableStrategy, ValueStrategy valueStrategy, OrderStrategy orderStrategy) {
		super(generalModel);
		this.fragment = fragment;
		this.variableStrategy = variableStrategy;
		this.valueStrategy = valueStrategy;
		this.orderStrategy = orderStrategy;
	}

	@Override
	public void buildVariables() {
				
		generalModel.buildNeighborGraphWithOutterHexagons(fragment.getOrder());
		generalModel.buildAdjacencyMatrixWithOutterHexagons();
		
		presentHexagons = new ArrayList<Integer>();
		absentHexagons = new ArrayList<Integer>();
		unknownHexagons = new ArrayList<Integer>();
		
		coronenoidCorrespondances = new IntVar[fragment.getNbNodes()];
	
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
		
		for (int i = 0 ; i < fragment.getNbNodes() ; i++) {
		
			if (fragment.getLabel(i) == 2) 
				coronenoidCorrespondances[i] = generalModel.getProblem().intVar("coronenoid_correspondances_" + i, domain);
			
			else  
				coronenoidCorrespondances[i] = generalModel.getProblem().intVar("coronenoid_correspondances_" + i, domainWithOutterHexagons);
				
		}
		
	}

	@Override
	public void postConstraints() {
	
		//Setting labels	
		for (int i = 0 ; i < fragment.getNbNodes() ; i++) {
			
			int labelI = fragment.getLabel(i);
			
			if (labelI == 1) 
				unknownHexagons.add(i);
			
			else if (labelI == 2)
				presentHexagons.add(i);
			
			else if (labelI == 3)
				absentHexagons.add(i);
		}
		
		//all-different constraint
		generalModel.getProblem().allDifferent(coronenoidCorrespondances).post();
		
		//table constraint for edges integrity
		Tuples table = buildTable();
		
		for (int i = 0 ; i < fragment.getNbNodes() ; i++) {
			for (int j = (i + 1) ; j < fragment.getNbNodes() ; j++) {
				
				if (fragment.getMatrix()[i][j] == 1) {
					
					IntVar si = coronenoidCorrespondances[i];
					IntVar sj = coronenoidCorrespondances[j];
					
					generalModel.getProblem().table(new IntVar[] {si, sj}, table, "CT+").post();
				}
			}
		}
		
		//label integrity constraints
		
		IntVar[] varClause;
		IntIterableRangeSet[] valClause;
	
		
		for (Integer i : presentHexagons) {	
			for (int j = 0 ; j < generalModel.getDiameter() * generalModel.getDiameter() ; j++) {
			
				if (generalModel.getGraphVertices()[j] != null) {
					//(i)

					varClause = new IntVar[coronenoidCorrespondances[i].getDomainSize()];
					valClause = new IntIterableRangeSet[coronenoidCorrespondances[i].getDomainSize()];
					
					int index = 0;
					
					DisposableValueIterator vit = coronenoidCorrespondances[i].getValueIterator(true);
					while(vit.hasNext()){
					    int v = vit.next();
					    if (v != j)
					    {
					    	varClause[index] = coronenoidCorrespondances[i];
					    	valClause[index] = new IntIterableRangeSet(v);
					    	index++;
					    }
					}
					vit.dispose();

					varClause[index] = generalModel.getGraphVertices()[j];
					valClause[index] = new IntIterableRangeSet(1);
					
					generalModel.getProblem().getClauseConstraint().addClause(varClause, valClause);

				}
			}
		}
		
		for (Integer i : absentHexagons ) {
			for (int j = 0 ; j < generalModel.getDiameter() * generalModel.getDiameter() ; j++) {
			
				if (generalModel.getGraphVertices()[j] != null) {
					
					varClause = new IntVar[coronenoidCorrespondances[i].getDomainSize()];
					valClause = new IntIterableRangeSet[coronenoidCorrespondances[i].getDomainSize()];
					
					int index = 0;
					
					DisposableValueIterator vit = coronenoidCorrespondances[i].getValueIterator(true);
					while(vit.hasNext()){
					    int v = vit.next();
					    if (v != j)
					    {
					    	varClause[index] = coronenoidCorrespondances[i];
					    	valClause[index] = new IntIterableRangeSet(v);
					    	index++;
					    }
					}
					vit.dispose();

					varClause[index] = generalModel.getGraphVertices()[j];
					valClause[index] = new IntIterableRangeSet(0);
					
					generalModel.getProblem().getClauseConstraint().addClause(varClause, valClause);

				}
			}
		}
		
	}

	@Override
	public void addVariables() {
		//generalModel.addWatchedVariable(coronenoidCorrespondances);
	}

	@Override
	public void changeSolvingStrategy() {

		IntVar [] branchingVariables = new IntVar[generalModel.getChanneling().length + coronenoidCorrespondances.length];
		
		int index = 0;
		
		switch(orderStrategy) {
		
			case CHANNELING_FIRST:
				
				for (BoolVar x : generalModel.getChanneling()) {
					branchingVariables[index] = x;
					index ++;
				}
				
				for (IntVar x : coronenoidCorrespondances) {
					branchingVariables[index] = x;
					index ++;
				}
				
				break;
				
			case CHANNELING_LAST:
				
				for (IntVar x : coronenoidCorrespondances) {
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
	
	private Tuples buildTable() {
		
		Tuples table = new Tuples(true);
		
		int [][] matrix = generalModel.getAdjacencyMatrixOutterHexagons();
		
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
