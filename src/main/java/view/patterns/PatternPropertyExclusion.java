package view.patterns;

import constraints.ExclusionPatternConstraint;
import view.generator.boxes.HBoxPatternCriterion;

public class PatternPropertyExclusion extends PatternProperty {

    PatternPropertyExclusion(PatternGroup pattern) {
        super (pattern);
    }

    @Override
    String getLabel () {
        return "Excl " + getPattern().getLabel().getText();
    }

    @Override
    void setConstraint(HBoxPatternCriterion patternConstraintHBox) {
        patternConstraintHBox.addConstraint(new ExclusionPatternConstraint(getPattern().exportPattern()));
    }

    @Override
    int getPropertyType() { return 1; }
}
