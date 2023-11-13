package constraints;

import generator.GeneralModel;
import generator.OrderStrategy;
import generator.ValueStrategy;
import generator.VariableStrategy;
import generator.patterns.Pattern;
import generator.patterns.PatternLabel;
import org.chocosolver.solver.constraints.extension.Tuples;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMax;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin;
import org.chocosolver.solver.search.strategy.selectors.values.IntValueSelector;
import org.chocosolver.solver.search.strategy.selectors.variables.*;
import org.chocosolver.solver.search.strategy.strategy.IntStrategy;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.iterators.DisposableValueIterator;
import org.chocosolver.util.objects.setDataStructures.iterable.IntIterableRangeSet;

import java.util.ArrayList;

public class MultiplePatterns3Constraint extends BenzAIConstraint {

	private final ArrayList<Pattern> patterns;
	
	ArrayList<ArrayList<Integer>> allAbsentHexagons;
	ArrayList<ArrayList<Integer>> allPresentHexagons;
	ArrayList<ArrayList<Integer>> allUnknownHexagons;
	
	private ArrayList<IntVar []> allCoronenoidCorrespondances;
	
	private int maxOrder;
	
	private Tuples table;
	
	private final VariableStrategy variableStrategy;
	private final ValueStrategy valueStrategy;
	private final OrderStrategy orderStrategy;
	
	public MultiplePatterns3Constraint(ArrayList<Pattern> patterns, VariableStrategy variableStrategy, ValueStrategy valueStrategy, OrderStrategy orderStrategy) {
		this.patterns = patterns;
		this.variableStrategy = variableStrategy;
		this.valueStrategy = valueStrategy;
		this.orderStrategy = orderStrategy;
		computeMaxOrder();
	}

	@Override
	public void buildVariables() {
		GeneralModel generalModel = getGeneralModel();

		generalModel.buildNeighborGraphWithOutterHexagons(maxOrder);
		generalModel.buildAdjacencyMatrixWithOutterHexagons();
			
		buildTable();
		
		allPresentHexagons = new ArrayList<>();
		allAbsentHexagons = new ArrayList<>();
		allUnknownHexagons = new ArrayList<>();
		
		for (int i = 0 ; i < patterns.size() ; i++) {
			allPresentHexagons.add(new ArrayList<Integer>());
			allAbsentHexagons.add(new ArrayList<Integer>());
			allUnknownHexagons.add(new ArrayList<Integer>());
		}
		
		allCoronenoidCorrespondances = new ArrayList<IntVar []>();
		
		ArrayList<Integer> domainList = new ArrayList<Integer>();
		domainList.add(-1);
		
		for (int i = 0 ; i < generalModel.getDiameter() ; i++) {
			for (int j = 0 ; j < generalModel.getDiameter() ; j++) {
				if (generalModel.getHexagonIndicesMatrix()[i][j] != -1)
					domainList.add(generalModel.getHexagonIndicesMatrix()[i][j]);
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
		
		for (int i = 0 ; i < patterns.size() ; i++) {
			Pattern pattern = patterns.get(i);
			IntVar[] coronenoidCorrespondances = new IntVar[pattern.getNbNodes()];
			for (int j = 0 ; j < pattern.getNbNodes() ; j++) {
				if (pattern.getLabel(j) == PatternLabel.POSITIVE)
					coronenoidCorrespondances[j] = generalModel.getProblem().intVar("cc_" + i + "_" + j, domain);
				else 
					coronenoidCorrespondances[j] = generalModel.getProblem().intVar("cc_" + i + "_" + j, domainWithOutterHexagons);
			}
			allCoronenoidCorrespondances.add(coronenoidCorrespondances);
		}
	}

	@Override
	public void postConstraints() {
		GeneralModel generalModel = getGeneralModel();

		for (int i = 0 ; i < patterns.size() ; i++) {
			
			Pattern pattern = patterns.get(i);
			
			ArrayList<Integer> unknownHexagons = allUnknownHexagons.get(i);
			ArrayList<Integer> presentHexagons = allPresentHexagons.get(i);
			ArrayList<Integer> absentHexagons = allAbsentHexagons.get(i);
			
			IntVar [] coronenoidCorrespondances = allCoronenoidCorrespondances.get(i);

			/*
			 * Settings patterns's labels
			 */

			for (int j = 0 ; j < pattern.getNbNodes() ; j++) {
				PatternLabel labelI = pattern.getLabel(j);
				if (labelI == PatternLabel.NEUTRAL)
					unknownHexagons.add(j);
				else if (labelI == PatternLabel.POSITIVE)
					presentHexagons.add(j);
				else if (labelI == PatternLabel.NEGATIVE)
					absentHexagons.add(j);
			}
			
			/*
			 * All-Different constraint
			 */
			
			generalModel.getProblem().allDifferent(coronenoidCorrespondances).post();
		
			/*
			 * Table constraints for edges integrity
			 */
		
			for (int j = 0 ; j < pattern.getNbNodes() ; j++) {
				for (int k = (j + 1) ; k < pattern.getNbNodes() ; k++) {
					
					if (pattern.getMatrix()[j][k] == 1) {
						
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
					
					if (generalModel.getBenzenoidVerticesBVArray(k) != null) {
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

						varClause[index] = generalModel.getBenzenoidVerticesBVArray(k);
						valClause[index] = new IntIterableRangeSet(1);
						
						generalModel.getProblem().getClauseConstraint().addClause(varClause, valClause);
						
					}
				}
			}
			
			for (Integer j : absentHexagons) {
				for (int k = 0 ; k < generalModel.getDiameter() * generalModel.getDiameter() ; k++) {
					
					if (generalModel.getBenzenoidVerticesBVArray(k) != null) {

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

						varClause[index] = generalModel.getBenzenoidVerticesBVArray(k);
						valClause[index] = new IntIterableRangeSet(0);
						
						generalModel.getProblem().getClauseConstraint().addClause(varClause, valClause);
					}
				}
			}
			
			/*
			 * Disjoints patterns
			 */
			
			int nbCorrespondances = 0;
			for (IntVar [] correspondance : allCoronenoidCorrespondances) 
				nbCorrespondances += correspondance.length;
			
			IntVar [] allCorrespondances = new IntVar[nbCorrespondances];
			
			int index = 0;
			for (IntVar [] correspondance : allCoronenoidCorrespondances) {
				for (IntVar integers : correspondance) {
					allCorrespondances[index] = integers;
					index++;
				}
					
			}
			
			generalModel.getProblem().allDifferent(allCorrespondances).post();
		}
	}

	@Override
	public void addVariables() {	
	//	for (IntVar [] coronenoidCorrespondances : allCoronenoidCorrespondances)
	//		generalModel.addWatchedVariable(coronenoidCorrespondances);
	}

	@Override
	public void changeSolvingStrategy() {
		GeneralModel generalModel = getGeneralModel();

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
		
		IntVar [] branchingVariables = new IntVar[generalModel.getHexBoolVars().length + correspondances.length];
		index = 0;
		
		switch(orderStrategy) {
		
			case CHANNELING_FIRST:
				
				for (BoolVar x : generalModel.getHexBoolVars()) {
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
				
				for (BoolVar x : generalModel.getHexBoolVars()) {
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
		
	}
	
	private void computeMaxOrder() {
		
		maxOrder = 0;
		
		for (Pattern pattern : patterns) {
			
			if (pattern.getOrder() > maxOrder)
				maxOrder = pattern.getOrder();
		}
	}
	
	private void buildTable() {
		
		table = new Tuples(true);
		
		int [][] matrix = getGeneralModel().getAdjacencyMatrixOutterHexagons();
		
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
