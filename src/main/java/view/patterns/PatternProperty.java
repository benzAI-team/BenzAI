package view.patterns;

import view.generator.boxes.HBoxPatternCriterion;

abstract class PatternProperty {
    private PatternGroup pattern;

    PatternProperty(PatternGroup pattern){
        this.pattern = pattern;
    }

    public PatternGroup getPattern() {
        return pattern;
    }


    abstract String getLabel ();

    abstract void setConstraint(HBoxPatternCriterion patternConstraintHBox);
}
