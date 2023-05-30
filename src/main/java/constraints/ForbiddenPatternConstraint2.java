package constraints;

import generator.GeneralModel;
import generator.OrderStrategy;
import generator.ValueStrategy;
import generator.VariableStrategy;
import generator.patterns.Pattern;
import generator.patterns.PatternLabel;
import generator.patterns.PatternOccurences;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMax;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin;
import org.chocosolver.solver.search.strategy.selectors.values.IntValueSelector;
import org.chocosolver.solver.search.strategy.selectors.variables.*;
import org.chocosolver.solver.search.strategy.strategy.IntStrategy;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

import java.util.ArrayList;

public class ForbiddenPatternConstraint2 extends BenzAIConstraint {
	
	private final Pattern pattern;
	
	private ArrayList<Pattern> symmetricPatterns;
	
	private ArrayList<Integer> presentHexagons, unknownHexagons, absentHexagons;
	private BoolVar [] presences;
	
	private PatternOccurences patternOccurences;
	
	private final VariableStrategy variableStrategy;
	private final ValueStrategy valueStrategy;
	private final OrderStrategy orderStrategy;
	
	public ForbiddenPatternConstraint2(Pattern pattern, VariableStrategy variableStrategy, ValueStrategy valueStrategy, OrderStrategy orderStrategy) {
		super();
		this.pattern = pattern;
		this.variableStrategy = variableStrategy;
		this.valueStrategy = valueStrategy;
		this.orderStrategy = orderStrategy;
	}

	@Override
	public void buildVariables() {
		
		computePatternOccurences();
		
		presences = new BoolVar[patternOccurences.getOccurences().size()];
		for (int i = 0 ; i < presences.length ; i++)
			presences[i] = getGeneralModel().getProblem().boolVar("presence_" + i);
		
		presentHexagons = new ArrayList<>();
		absentHexagons = new ArrayList<>();
		unknownHexagons = new ArrayList<>();
		
		for (int i = 0 ; i < pattern.getNbNodes() ; i++) {
			PatternLabel label = pattern.getLabel(i);

			if (label == PatternLabel.NEUTRAL)
				unknownHexagons.add(i);
			else if (label == PatternLabel.POSITIVE)
				presentHexagons.add(i);
			else if (label == PatternLabel.NEGATIVE)
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

			Constraint [] andCstr = new Constraint[present.size() + absent.size()];
				
			int index = 0;
				
			for (Integer j : absent) {
				andCstr[index] = generalModel.getProblem().arithm(generalModel.getBenzenoidVerticesBVArray(j), "=", 0);
				index ++;
			}
				
			for (Integer j : present) {
				andCstr[index] = generalModel.getProblem().arithm(generalModel.getBenzenoidVerticesBVArray(j), "=", 1);
				index ++;
			}
				
			generalModel.getProblem().and(andCstr).reifyWith(presences[i]);
				
			generalModel.getProblem().sum(presences, "=", 0).post();
					
		}
	}

	@Override
	public void addVariables() {
		//generalModel.addWatchedVariable(presences);
	}

	@Override
	public void changeSolvingStrategy() {
		GeneralModel generalModel = getGeneralModel();

		IntVar [] branchingVariables = new IntVar[generalModel.getHexBoolVars().length + presences.length];
		int index = 0;
		
		switch(orderStrategy) {
			
			case CHANNELING_FIRST:
				
				for (BoolVar x : generalModel.getHexBoolVars()) {
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
	public void changeGraphVertices() { }
	
	private void computePatternOccurences() {
		
		symmetricPatterns = pattern.computeRotations();	
		patternOccurences = new PatternOccurences();
		
		for (Pattern f : symmetricPatterns)
			patternOccurences.addAll(getGeneralModel().computeTranslations(f));
	}

}