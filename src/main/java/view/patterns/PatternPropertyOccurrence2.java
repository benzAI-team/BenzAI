package view.patterns;

import view.generator.boxes.HBoxPatternCriterion;

public class PatternPropertyOccurrence2 extends PatternPropertyOccurrence {

    PatternPropertyOccurrence2 (PatternGroup pattern, int minOccurrence, int maxOccurrence) {
        super(pattern, minOccurrence, maxOccurrence);
    }

    @Override
    String getLabel () {
        return "Occurr 2 " + getPattern().getLabel().getText() + " " + minOccurrence + "-" + maxOccurrence;
    }

    @Override
    void setConstraint(HBoxPatternCriterion patternConstraintHBox) {
    }

    @Override
    int getPropertyType() { return 4; }

}
