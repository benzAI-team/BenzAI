package view.patterns;

import view.generator.boxes.HBoxPatternCriterion;

abstract class PatternType {
    private PatternGroup pattern;

    PatternType (PatternGroup pattern){
        this.pattern = pattern;
    }

    public PatternGroup getPattern() {
        return pattern;
    }


    abstract String getLabel ();

    abstract void setConstraint(HBoxPatternCriterion patternConstraintHBox);
}
