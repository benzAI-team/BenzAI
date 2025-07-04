package view.patterns;

import view.generator.boxes.HBoxPatternCriterion;


class PatternPropertyOccurrence extends PatternProperty {
    int minOccurrence;
    int maxOccurrence;

    PatternPropertyOccurrence (PatternGroup pattern, int minOccurrence, int maxOccurrence) {
        super (pattern);
        this.minOccurrence = minOccurrence;
        this.maxOccurrence = maxOccurrence;
    }

    @Override
    String getLabel () {
        return "Occurr " + getPattern().getLabel().getText() + " " + minOccurrence + "-" + maxOccurrence;
    }

    @Override
    void setConstraint(HBoxPatternCriterion patternConstraintHBox) {
    }

    @Override
    int getPropertyType() { return 2; }

    int getMinOccurrence () {
        return minOccurrence;
    }

    int getMaxOccurrence() {
        return maxOccurrence;
    }
}

