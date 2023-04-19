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

public class OccurencePatternConstraint extends BenzAIConstraint {

	private Pattern pattern;
	private int nbOccurences;
	
	private ArrayList<Pattern> symmetricPatterns;
	private ArrayList<Integer> presentHexagons, unknownHexagons, absentHexagons;
	private BoolVar [] presences;
	
	private IntVar occurenceVar;
	private BoolVar [] presences2;
	
	private PatternOccurences patternOccurences;
	
	private VariableStrategy variablesStrategy;
	private ValueStrategy valueStrategy;
	private OrderStrategy orderStrategy;
	
	public OccurencePatternConstraint(Pattern pattern, int nbOccurences, VariableStrategy variablesStrategy, ValueStrategy valueStrategy, OrderStrategy orderStrategy) {
		this.pattern = pattern;
		this.nbOccurences = nbOccurences;
		this.variablesStrategy = variablesStrategy;
		this.valueStrategy = valueStrategy;
		this.orderStrategy = orderStrategy;
	}

	@Override
	public void buildVariables() {
		GeneralModel generalModel = getGeneralModel();

		computePatternOccurences();
		
		occurenceVar = generalModel.getProblem().intVar("n_e", 0, nbOccurences);
		
		presences = new BoolVar[patternOccurences.getOccurences().size()];
		for (int i = 0 ; i < presences.length ; i++)
			presences[i] = generalModel.getProblem().boolVar("presence_" + i);
		
		presences2 = new BoolVar[patternOccurences.getOccurences().size()];
		for (int i = 0 ; i < presences2.length ; i++)
			presences2[i] = generalModel.getProblem().boolVar("presence2_" + i);
		
		presentHexagons = new ArrayList<>();
		absentHexagons = new ArrayList<>();
		unknownHexagons = new ArrayList<>();
		
		for (int i = 0 ; i < pattern.getNbNodes() ; i++) {
			
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

		ArrayList<Integer []> occurences = patternOccurences.getOccurences();
		
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
			
			ArrayList<Integer> patterns = new ArrayList<>();
			
			for (int i = 0 ; i < patternOccurences.size() ; i++) {
				
				ArrayList<Integer> present = patternOccurences.getAllPresentHexagons().get(i);
				ArrayList<Integer> absent = patternOccurences.getAllAbsentHexagons().get(i);
				ArrayList<Integer> unknown = patternOccurences.getAllUnknownHexagons().get(i);
				ArrayList<Integer> outter = patternOccurences.getAllOutterHexagons().get(i);
			
				if (present.contains(hexagon) || absent.contains(hexagon) || unknown.contains(hexagon) || outter.contains(hexagon))
					patterns.add(i);
			}
			
			BoolVar [] e1 = new BoolVar[patterns.size()];
			BoolVar [] e2 = new BoolVar[patterns.size()];
			
			for (int i = 0 ; i < patterns.size() ; i++) {
				e1[i] = presences[patterns.get(i)];
				e2[i] = presences2[patterns.get(i)];
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
	
	private void computePatternOccurences() {
		
		symmetricPatterns = pattern.computeRotations();
		
		patternOccurences = new PatternOccurences();
		
		for (Pattern f : symmetricPatterns)
			patternOccurences.addAll(getGeneralModel().computeTranslations(f));	
	}

}
