package view.patterns;

import constraints.ExistencePatternConstraint;
import view.generator.boxes.HBoxPatternCriterion;

public class PatternPropertyExistence extends PatternProperty {

    PatternPropertyExistence(PatternGroup pattern) {
        super(pattern);
    }

    @Override
    String getLabel () {
        return "Exist " + getPattern().getLabel().getText();
    }

    @Override
    void setConstraint(HBoxPatternCriterion patternConstraintHBox) {
//        patternConstraintHBox.setConstraint(new SinglePattern3Constraint(getPattern().exportPattern(),
//                VariableStrategy.FIRST_FAIL, ValueStrategy.INT_MAX, OrderStrategy.CHANNELING_FIRST));
        patternConstraintHBox.addConstraint(new ExistencePatternConstraint(getPattern().exportPattern()));
    }

    @Override
    int getPropertyType() { return 0; }
}
