package view.patterns;

import generator.patterns.PatternOccurrences;

import java.util.HashSet;
import java.util.Set;

public class NoPositiveInteraction extends Interaction {

    @Override
    public String getLabel () {
        return "no pos ";
    }

    @Override
    public boolean interact (PatternOccurrences patternOccurrences1, PatternOccurrences patternOccurrences2, int i, int j ) {
        if ((i < patternOccurrences1.size()) && (j < patternOccurrences2.size())) {
            Set<Integer> intersection = new HashSet<>(patternOccurrences1.getAllPresentHexagons().get(i));

            intersection.retainAll(patternOccurrences2.getAllPresentHexagons().get(j));

            return !intersection.isEmpty();
        }
        else {
            return false;
        }
    }
}
