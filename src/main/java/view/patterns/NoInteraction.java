package view.patterns;

import generator.patterns.PatternOccurrences;

public class NoInteraction extends Interaction {

    @Override
    public String getLabel () {
        return "";
    }

    @Override
    public boolean interact(PatternOccurrences patternOccurrences1, PatternOccurrences patternOccurrences2, int i, int j) {
        return false;
    }

    @Override
    public int getType() {
        return 0;
    }
}
