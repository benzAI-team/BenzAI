package constraints;

import generator.GeneralModel;
import org.chocosolver.solver.variables.Variable;
import view.patterns.PatternGroup;

import java.util.ArrayList;

public class ExistencePatternConstraint extends PatternConstraint {

    public ExistencePatternConstraint (PatternGroup pattern) {
        super(pattern);
    }

    public void postConstraints() {
        super.postConstraints();

        GeneralModel generalModel = getGeneralModel();

        ArrayList<Integer[]> occurrences = getPatternOccurrences().getOccurrences();

        if (occurrences.isEmpty()) {
            generalModel.getProblem().sum(generalModel.getHexBoolVars(), "=", 0).post();
            for (Variable x : generalModel.getHexBoolVars()) {
                generalModel.increaseDegree(x.getName());
            }
        } else {
            generalModel.getProblem().sum(getPresenceVariables(), "=", 1).post();
            for (Variable x : getPresenceVariables()) {
                generalModel.increaseDegree(x.getName());
            }
        }
    }
}
