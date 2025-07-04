package view.patterns;

import view.generator.boxes.HBoxPatternCriterion;

public class PatternPropertyOccurrence3 extends PatternPropertyOccurrence {

    PatternPropertyOccurrence3 (PatternGroup pattern, int minOccurrence, int maxOccurrence) {
        super(pattern, minOccurrence, maxOccurrence);
    }

    @Override
    String getLabel () {
        return "Occurr 3 " + getPattern().getLabel().getText() + " " + minOccurrence + "-" + maxOccurrence;
    }

    @Override
    void setConstraint(HBoxPatternCriterion patternConstraintHBox) {
    }

    @Override
    int getPropertyType() { return 5; }

}
