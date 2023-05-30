package constraints;

import generator.GeneralModel;
import generator.patterns.Pattern;
import generator.patterns.PatternLabel;
import generator.patterns.PatternOccurences;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.search.strategy.strategy.IntStrategy;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.util.objects.setDataStructures.iterable.IntIterableRangeSet;

import java.util.ArrayList;

public class ForbiddenPatternConstraint extends BenzAIConstraint {

	private final int mode = 3;
	
	private final Pattern pattern;

	private ArrayList<Integer> presentHexagons;
	private ArrayList<Integer> absentHexagons;
	private BoolVar [] presences;
	
	private PatternOccurences patternOccurences;
	
	public ForbiddenPatternConstraint(Pattern pattern) {
		super();
		this.pattern = pattern;
	}

	@Override
	public void buildVariables() {
		
		computePatternOccurences();
		
		if (mode < 3){
			presences = new BoolVar[patternOccurences.getOccurences().size()];
			for (int i = 0 ; i < presences.length ; i++)
				presences[i] = getGeneralModel().getProblem().boolVar("presence_" + i);
		}
		
		presentHexagons = new ArrayList<>();
		absentHexagons = new ArrayList<>();
		ArrayList<Integer> unknownHexagons = new ArrayList<>();
		
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
			
			//version contrainte
			
			if (mode == 1) {
				Constraint ifCstr = generalModel.getProblem().arithm(presences[i], "=", 1);
				
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
				
				Constraint thenCstr = generalModel.getProblem().and(andCstr);
				generalModel.getProblem().ifThen(ifCstr, thenCstr);
				generalModel.getProblem().ifOnlyIf(ifCstr, thenCstr);
				
				generalModel.getProblem().sum(presences, "=", 0).post();
			}
			else if (mode == 2) {
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
			else {
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
	}

	@Override
	public void addVariables() {
		//generalModel.addWatchedVariable(presences);
	}

	@Override
	public void changeSolvingStrategy() {
		GeneralModel generalModel = getGeneralModel();
		//generalModel.getProblem().getSolver().setSearch(new IntStrategy(generalModel.getBenzenoidVerticesBVArray(), new FirstFail(generalModel.getProblem()), new IntDomainMin()), new IntStrategy(presences, new FirstFail(generalModel.getProblem()), new IntDomainMin()));
		if (mode < 3)
			generalModel.getProblem().getSolver().setSearch(new IntStrategy(generalModel.getHexBoolVars(), new FirstFail(generalModel.getProblem()), new IntDomainMin()), new IntStrategy(presences, new FirstFail(generalModel.getProblem()), new IntDomainMin()));
		else generalModel.getProblem().getSolver().setSearch(new IntStrategy(generalModel.getHexBoolVars(), new FirstFail(generalModel.getProblem()), new IntDomainMin()));
	}

	@Override
	public void changeGraphVertices() { }
	
	private void computePatternOccurences() {

		ArrayList<Pattern> symmetricPatterns = pattern.computeRotations();
		patternOccurences = new PatternOccurences();
		
		for (Pattern f : symmetricPatterns)
			patternOccurences.addAll(getGeneralModel().computeTranslations(f));
	}

}
