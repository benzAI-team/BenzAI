package constraints;

import generator.GeneralModel;
import generator.patterns.PatternOccurrences;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.objects.setDataStructures.iterable.IntIterableRangeSet;
import view.patterns.Interaction;
import view.patterns.PatternGroup;

public class OccurrencePatternConstraint extends PatternConstraint {
    private int minOccurrence;
    private int maxOccurrence;
    private IntVar occurrenceNumber;
    private Interaction interaction;

    public OccurrencePatternConstraint(PatternGroup pattern, Interaction interaction, int minOccurrence, int maxOccurrence) {
        super(pattern);
        this.interaction = interaction;
        this.minOccurrence = minOccurrence;
        this.maxOccurrence = maxOccurrence;
    }

    @Override
    public void buildVariables() {
        System.out.println("Build var occur");
        super.buildVariables();
        occurrenceNumber = getGeneralModel().getProblem().intVar("occurrence", minOccurrence, maxOccurrence);
    }

    @Override
    public void postConstraints() {
        super.postConstraints();

        GeneralModel generalModel = getGeneralModel();
        generalModel.getProblem().sum(getPresenceVariables(), "=", occurrenceNumber).post();

        // we post interaction constraints (if any)
        BoolVar[] presences = getPresenceVariables();
        PatternOccurrences patternOccurrences = getPattern().getPatternOccurrences();

        for (int i=0 ; i < patternOccurrences.size(); i++) {
            for (int j = i + 1; j < patternOccurrences.size(); j++) {
                if (interaction.interact(patternOccurrences, patternOccurrences, i, j)) {
                    BoolVar e1 = presences[i];
                    BoolVar e2 = presences[j];

                    BoolVar[] clauseVariables = new BoolVar[]{e1, e2};
                    IntIterableRangeSet[] clauseValues = new IntIterableRangeSet[]{new IntIterableRangeSet(0), new IntIterableRangeSet(0)};

                    generalModel.getProblem().getClauseConstraint().addClause(clauseVariables, clauseValues);
                }
            }
        }
    }
}
