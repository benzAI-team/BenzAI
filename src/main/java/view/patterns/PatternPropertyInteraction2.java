package view.patterns;

import view.generator.boxes.HBoxPatternCriterion;

public class PatternPropertyInteraction2 extends PatternPropertyInteraction {

    PatternPropertyInteraction2(PatternGroup pattern, PatternGroup pattern2) {
        super (pattern, pattern2);
    }

    @Override
    String getLabel () {
        return "Inter 2" + getPattern().getLabel().getText()  + getPattern2().getLabel().getText();
    }

    @Override
    void setConstraint(HBoxPatternCriterion patternConstraintHBox) {
//        patternConstraintHBox.setConstraint(new MultiplePatterns3Constraint(getPatterns(),
//                VariableStrategy.FIRST_FAIL, ValueStrategy.INT_MAX, OrderStrategy.CHANNELING_FIRST));
    }

    @Override
    int getPropertyType() { return 7; }
}
