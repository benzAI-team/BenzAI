package view.patterns;

import constraints.PatternConstraint;
import view.generator.boxes.HBoxPatternCriterion;

abstract class PatternProperty {
    private PatternGroup pattern;
    private PatternConstraint patternConstraint;
    private int type;

    PatternProperty(PatternGroup pattern, int type, PatternConstraint patternConstraint){
        this.pattern = pattern;
        this.type = type;
        this.patternConstraint = patternConstraint;
    }

    public PatternGroup getPattern() {
        return pattern;
    }

    abstract String getLabel ();

    public void addConstraint(HBoxPatternCriterion patternConstraintHBox) {
        patternConstraintHBox.addConstraint(patternConstraint);
    }

    int getType() {
        return type;
    }

    public PatternConstraint getPatternConstraint() {
        return patternConstraint;
    }
}
