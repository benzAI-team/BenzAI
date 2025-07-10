package view.patterns;

import generator.patterns.PatternOccurrences;

import java.util.HashSet;
import java.util.Set;

public class NoHexagonInteraction extends Interaction {

    @Override
    public String getLabel () {
        return "no hex ";
    }

    @Override
    public boolean interact (PatternOccurrences patternOccurrences1, PatternOccurrences patternOccurrences2, int i, int j ) {

        if ((i < patternOccurrences1.size()) && (j < patternOccurrences2.size())) {
            Set<Integer> set1 = new HashSet<>(patternOccurrences1.getAllPresentHexagons().get(i));
            set1.addAll(patternOccurrences1.getAllAbsentHexagons().get(i));
            set1.addAll(patternOccurrences1.getAllOutterHexagons().get(i));
            set1.addAll(patternOccurrences1.getAllUnknownHexagons().get(i));

            Set<Integer> set2 = new HashSet<>(patternOccurrences2.getAllPresentHexagons().get(j));
            set2.addAll(patternOccurrences2.getAllAbsentHexagons().get(j));
            set2.addAll(patternOccurrences2.getAllOutterHexagons().get(j));
            set2.addAll(patternOccurrences2.getAllUnknownHexagons().get(j));

            set1.retainAll(set2);

            return ! set1.isEmpty();
        }
        else {
            return false;
        }
    }
}
