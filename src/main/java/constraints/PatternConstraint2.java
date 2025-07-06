package constraints;

import generator.GeneralModel;
import generator.patterns.Pattern;
import generator.patterns.PatternLabel;
import generator.patterns.PatternOccurences;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.objects.setDataStructures.iterable.IntIterableRangeSet;

import java.util.ArrayList;

public class PatternConstraint2 extends PatternConstraint {
	private final Pattern pattern;

	private ArrayList<Integer> presentHexagons;
	private ArrayList<Integer> absentHexagons;
	private BoolVar[] presences;

	private PatternOccurences patternOccurences;

	public PatternConstraint2 (Pattern pattern) {
		this.pattern = pattern;
	}

	@Override
	public void buildVariables() {
		System.out.println("Build Variable PatternConstraints");
		computePatternOccurrences();
		System.out.println("START");
		presences = new BoolVar[patternOccurences.getOccurences().size()];
		for (int i = 0; i < presences.length; i++) {
			System.out.println("Creer "+ i);
			presences[i] = getGeneralModel().getProblem().boolVar("presence_" + i);
		}

		presentHexagons = new ArrayList<>();
		absentHexagons = new ArrayList<>();
		ArrayList<Integer> unknownHexagons = new ArrayList<>();
		System.out.println("ICI");
		for (int i = 0; i < pattern.getNbNodes(); i++) {
			PatternLabel label = pattern.getLabel(i);
			if (label == PatternLabel.NEUTRAL)
				unknownHexagons.add(i);
			else if (label == PatternLabel.POSITIVE)
				presentHexagons.add(i);
			else if (label == PatternLabel.NEGATIVE)
				absentHexagons.add(i);
		}
		System.out.println("LA");
	}

	@Override
	public void postConstraints() {
		System.out.println("Build Constraint PatternConstraints");
		GeneralModel generalModel = getGeneralModel();

		ArrayList<Integer[]> occurrences = patternOccurences.getOccurences();

		for (int i = 0; i < occurrences.size(); i++) {

			Integer[] occurence = occurrences.get(i);

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

			IntVar[] varClause = new IntVar[2];
			IntIterableRangeSet[] valClause = new IntIterableRangeSet[2];

			varClause[0] = presences[i];
			valClause[0] = new IntIterableRangeSet(0);

			for (Integer j : absent) {
				varClause[1] = generalModel.getBenzenoidVerticesBVArray(j);
				valClause[1] = new IntIterableRangeSet(0);
				generalModel.getProblem().getClauseConstraint().addClause(varClause, valClause);
			}

			for (Integer j : present) {
				varClause[1] = generalModel.getBenzenoidVerticesBVArray(j);
				valClause[1] = new IntIterableRangeSet(1);
				generalModel.getProblem().getClauseConstraint().addClause(varClause, valClause);
			}
		}
	}

	@Override
	public void addVariables() {
		// generalModel.addWatchedVariable(presences);
	}

	@Override
	public void changeSolvingStrategy() {
	}

	@Override
	public void changeGraphVertices() {
		// TODO Auto-generated method stub
	}

	private void computePatternOccurrences() {

		ArrayList<Pattern> symmetricPatterns = pattern.computeRotations();

		patternOccurences = new PatternOccurences();

		for (Pattern f : symmetricPatterns) {
			System.out.println("FOR ");
			PatternOccurences x;
			System.out.println("FOR 2");
			x = getGeneralModel().computeTranslations(f);
			System.out.println("FOR 3");
			patternOccurences.addAll(x);
		}

	}

	BoolVar[] getPresenceVariables() {
		return presences;
	}

	public PatternOccurences getPatternOccurences() {
		return patternOccurences;
	}
}
