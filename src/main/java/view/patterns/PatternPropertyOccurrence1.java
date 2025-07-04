package view.patterns;

import view.generator.boxes.HBoxPatternCriterion;

public class PatternPropertyOccurrence1 extends PatternPropertyOccurrence {

    PatternPropertyOccurrence1 (PatternGroup pattern, int minOccurrence, int maxOccurrence) {
        super(pattern, minOccurrence, maxOccurrence);
    }

    @Override
    String getLabel () {
        return "Occurr 1 " + getPattern().getLabel().getText() + " " + minOccurrence + "-" + maxOccurrence;
    }

    @Override
    void setConstraint(HBoxPatternCriterion patternConstraintHBox) {
    }

    @Override
    int getPropertyType() { return 3; }

}
