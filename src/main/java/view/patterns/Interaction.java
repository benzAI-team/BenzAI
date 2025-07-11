package view.patterns;

import generator.patterns.PatternOccurrences;

public abstract class Interaction {

    abstract String getLabel();

    public abstract boolean interact(PatternOccurrences patternOccurrences1, PatternOccurrences patternOccurrences2, int i, int j);

    abstract int getType();
}
