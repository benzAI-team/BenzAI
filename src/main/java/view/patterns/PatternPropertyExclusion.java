package view.patterns;

import constraints.ExclusionPatternConstraint;

public class PatternPropertyExclusion extends PatternProperty {

    PatternPropertyExclusion(PatternGroup pattern) {
        super (pattern, 1, new ExclusionPatternConstraint(pattern));
    }

    @Override
    String getLabel () {
        return "∄ " + getPattern().getLabel().getText();
    }

}
