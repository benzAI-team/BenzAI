package view.patterns;

import view.generator.boxes.HBoxPatternCriterion;

public class PatternPropertyInteraction1 extends PatternPropertyInteraction {

    PatternPropertyInteraction1(PatternGroup pattern, PatternGroup pattern2) {
        super (pattern, pattern2);
    }

    @Override
    String getLabel () {
        return "Inter 1 " + getPattern().getLabel().getText() + " " + getPattern2().getLabel().getText();
    }

    @Override
    void setConstraint(HBoxPatternCriterion patternConstraintHBox) {
//        patternConstraintHBox.setConstraint(new MultiplePatterns1Constraint(getPatterns(),
//                VariableStrategy.FIRST_FAIL, ValueStrategy.INT_MAX, OrderStrategy.CHANNELING_FIRST));
    }

    @Override
    int getPropertyType() { return 6; }
}
