package constraints;

import generator.GeneralModel;
import generator.patterns.PatternOccurrences;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.objects.setDataStructures.iterable.IntIterableRangeSet;
import view.patterns.Interaction;

public class InteractionPatternConstraint extends BenzAIConstraint {
    private IntVar occurrenceNumber;
    private PatternConstraint patternConstraint1;
    private PatternConstraint patternConstraint2;
    private Interaction interaction;

    public InteractionPatternConstraint(PatternConstraint patternConstraint1, PatternConstraint patternConstraint2, Interaction interaction) {
        this.patternConstraint1 = patternConstraint1;
        this.patternConstraint2 = patternConstraint2;
        this.interaction = interaction;
    }

    @Override
    public void postConstraints() {
        GeneralModel generalModel = getGeneralModel();

        // we post interaction constraints (if any)
        BoolVar[] presences = patternConstraint1.getPresenceVariables();
        BoolVar[] presences2 = patternConstraint2.getPresenceVariables();
        PatternOccurrences patternOccurrences = patternConstraint1.getPatternOccurrences();
        PatternOccurrences patternOccurrences2 = patternConstraint2.getPatternOccurrences();

        for (int i=0 ; i < patternOccurrences.size(); i++) {
            for (int j = 0; j < patternOccurrences2.size(); j++) {
                if (interaction.interact(patternOccurrences, patternOccurrences2, i, j)) {
                    BoolVar e1 = presences[i];
                    BoolVar e2 = presences2[j];

                    BoolVar[] clauseVariables = new BoolVar[]{e1, e2};
                    IntIterableRangeSet[] clauseValues = new IntIterableRangeSet[]{new IntIterableRangeSet(0), new IntIterableRangeSet(0)};

                    generalModel.getProblem().getClauseConstraint().addClause(clauseVariables, clauseValues);
                }
            }
        }

    }

    @Override
    public void buildVariables() {
    }

    public void addVariables() {
    }

    @Override
    public void changeSolvingStrategy() {
    }

    @Override
    public void changeGraphVertices() {
        // TODO Auto-generated method stub
    }

}
