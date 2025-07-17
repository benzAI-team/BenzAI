package view.patterns;

import constraints.ExistencePatternConstraint;

public class PatternPropertyExistence extends PatternProperty {

    PatternPropertyExistence(PatternGroup pattern) {
        super(pattern, 0, new ExistencePatternConstraint(pattern));
    }

    @Override
    String getLabel () {
        return "∃ " + getPattern().getLabel().getText();
    }
}
