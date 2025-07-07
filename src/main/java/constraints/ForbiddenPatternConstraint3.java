package constraints;

import generator.GeneralModel;
import generator.ValueStrategy;
import generator.VariableStrategy;
import generator.patterns.Pattern;
import generator.patterns.PatternLabel;
import generator.patterns.PatternOccurrences;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMax;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin;
import org.chocosolver.solver.search.strategy.selectors.values.IntValueSelector;
import org.chocosolver.solver.search.strategy.selectors.variables.*;
import org.chocosolver.solver.search.strategy.strategy.IntStrategy;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.objects.setDataStructures.iterable.IntIterableRangeSet;

import java.util.ArrayList;

public class ForbiddenPatternConstraint3 extends BenzAIConstraint {
	
	private final Pattern pattern;
	
	private ArrayList<Pattern> symmetricPatterns;
	
	private ArrayList<Integer> presentHexagons, unknownHexagons, absentHexagons;
	
	private PatternOccurrences patternOccurences;
	
	private final VariableStrategy variableStrategy;
	private final ValueStrategy valueStrategy;
	
	public ForbiddenPatternConstraint3(Pattern pattern, VariableStrategy variableStrategy, ValueStrategy valueStrategy) {
		super();
		this.pattern = pattern;
		this.variableStrategy = variableStrategy;
		this.valueStrategy = valueStrategy;
	}

	@Override
	public void buildVariables() {
		
		computePatternOccurences();
		
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

		ArrayList<Integer []> occurences = patternOccurences.getOccurrences();
		
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
			
		 	BoolVar [] varClause = new BoolVar[present.size() + absent.size()];
		 	IntIterableRangeSet[] valClause = new IntIterableRangeSet[present.size() + absent.size()];
		 	int index = 0;
		 		
			for (Integer j : absent) {
				varClause[index] = generalModel.getBenzenoidVerticesBVArray(j);
				valClause[index] = new IntIterableRangeSet(1);
				index ++;
			}
				
			for (Integer j : present) {
				varClause[index] = generalModel.getBenzenoidVerticesBVArray(j);
				valClause[index] = new IntIterableRangeSet(0);
				index ++;
			}
		 		
		 	generalModel.getProblem().getClauseConstraint().addClause(varClause,valClause);			
			
		}
	}

	@Override
	public void addVariables() {
		//generalModel.addWatchedVariable(presences);
	}

	@Override
	public void changeSolvingStrategy() {
		GeneralModel generalModel = getGeneralModel();

		VariableSelector<IntVar> variableSelector = null;
		
		switch(variableStrategy) {
		
			case FIRST_FAIL:
				variableSelector = new FirstFail(generalModel.getProblem());
				break;
				
			case DOM_WDEG:
				variableSelector = new DomOverWDeg(generalModel.getHexBoolVars(), 0L);
				break;
				
			case DOM_WDEG_REF:
				variableSelector = new DomOverWDegRef(generalModel.getHexBoolVars(), 0L);
				break;
				
			case CHS:
				variableSelector = new ConflictHistorySearch(generalModel.getHexBoolVars(), 0L);
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
		
		generalModel.getProblem().getSolver().setSearch(new IntStrategy(generalModel.getHexBoolVars(), variableSelector, valueSelector));
	}

	@Override
	public void changeGraphVertices() { }
	
	private void computePatternOccurences() {
		
		symmetricPatterns = pattern.computeRotations();	
		patternOccurences = new PatternOccurrences();
		
		for (Pattern f : symmetricPatterns)
			patternOccurences.addAll(getGeneralModel().computeTranslations(f));
	}

}
