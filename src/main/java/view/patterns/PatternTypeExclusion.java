package view.patterns;

import constraints.ForbiddenPatternConstraint3;
import generator.ValueStrategy;
import generator.VariableStrategy;
import view.generator.boxes.HBoxPatternCriterion;

public class PatternTypeExclusion extends PatternType {

    PatternTypeExclusion (PatternGroup pattern) {
        super (pattern);
    }

    @Override
    String getLabel () {
        return "Excl " + getPattern().getLabel().getText();
    }

    @Override
    void setConstraint(HBoxPatternCriterion patternConstraintHBox) {
        patternConstraintHBox.setConstraint(new ForbiddenPatternConstraint3(getPattern().exportPattern(), VariableStrategy.FIRST_FAIL, ValueStrategy.INT_MAX));
    }
}
