package constraints;

import generator.GeneralModel;
import generator.patterns.Pattern;
import generator.patterns.PatternLabel;
import generator.patterns.PatternOccurrences;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.objects.setDataStructures.iterable.IntIterableRangeSet;
import view.patterns.PatternGroup;

import java.util.ArrayList;

public class PatternConstraint extends BenzAIConstraint {
	private Pattern pattern;
	private final PatternGroup patternGroup;

	private ArrayList<Integer> presentHexagons;
	private ArrayList<Integer> absentHexagons;
	private ArrayList<Integer> edgeHexagons;
	private BoolVar[] presences;

	private PatternOccurrences patternOccurrences;

	public PatternConstraint (PatternGroup patternGroup) {
		// pb for the initial empty pattern
		this.patternGroup = patternGroup;
		this.pattern = null;
	}

	@Override
	public void buildVariables() {
		pattern = patternGroup.exportPattern();

		System.out.println("Build Variable PatternConstraints");
		computePatternOccurrences();
		System.out.println("# Occurrences "+ patternOccurrences.getOccurrences().size());
		presences = new BoolVar[patternOccurrences.getOccurrences().size()];
		for (int i = 0; i < presences.length; i++) {
			presences[i] = getGeneralModel().getProblem().boolVar("presence_" + i);
		}

		presentHexagons = new ArrayList<>();
		absentHexagons = new ArrayList<>();
		edgeHexagons = new ArrayList<>();
		ArrayList<Integer> unknownHexagons = new ArrayList<>();
		for (int i = 0; i < pattern.getNbNodes(); i++) {
			PatternLabel label = pattern.getLabel(i);
			if (label == PatternLabel.NEUTRAL)
				unknownHexagons.add(i);
			else if (label == PatternLabel.POSITIVE)
				presentHexagons.add(i);
			else if (label == PatternLabel.NEGATIVE)
				absentHexagons.add(i);
			else if (label == PatternLabel.EDGE)
				edgeHexagons.add(i);
		}
	}

	@Override
	public void postConstraints() {
		System.out.println("Build Constraint PatternConstraints");
		GeneralModel generalModel = getGeneralModel();

		ArrayList<Integer[]> occurrences = patternOccurrences.getOccurrences();

		System.out.println("Size "+occurrences.size());

		for (int i = 0; i < occurrences.size(); i++) {

			Integer[] occurrence = occurrences.get(i);

			ArrayList<Integer> present = new ArrayList<>();
			ArrayList<Integer> absent = new ArrayList<>();

			for (Integer hexagon : presentHexagons) {
				if (occurrence[hexagon] != -1)
					present.add(occurrence[hexagon]);
			}

			for (Integer hexagon : absentHexagons) {
				if (occurrence[hexagon] != -1)
					absent.add(occurrence[hexagon]);
			}

			for (Integer hexagon : edgeHexagons) {
				if (occurrence[hexagon] != -1)
					absent.add(occurrence[hexagon]);
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

		patternOccurrences = new PatternOccurrences();

		for (Pattern f : symmetricPatterns) {
			patternOccurrences.addAll(getGeneralModel().computeTranslations(f));
		}
		pattern.setPatternOccurrences(patternOccurrences);
	}

	BoolVar[] getPresenceVariables() {
		return presences;
	}

	public PatternOccurrences getPatternOccurrences() {
		return patternOccurrences;
	}

	public ArrayList<Integer> getPresentHexagons() {
		return presentHexagons;
	}

	public ArrayList<Integer> getAbsentHexagons() {
		return absentHexagons;
	}

	public ArrayList<Integer> getEdgeHexagons() {
		return edgeHexagons;
	}

	public Pattern getPattern() {
		return pattern;
	}
}
