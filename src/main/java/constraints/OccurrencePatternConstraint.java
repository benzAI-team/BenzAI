package constraints;

import generator.patterns.Pattern;
import org.chocosolver.solver.variables.IntVar;

public class OccurrencePatternConstraint extends PatternConstraint {
    private int minOccurrence;
    private int maxOccurrence;
    private IntVar occurrenceNumber;

    public OccurrencePatternConstraint(Pattern pattern, int minOccurrence, int maxOccurrence) {
        super(pattern);
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
        System.out.println("Post ctr occur"+ getPresenceVariables() + " " + getGeneralModel() );
        getGeneralModel().getProblem().sum(getPresenceVariables(), "=", occurrenceNumber).post();
    }
}
