package view.patterns;


import generator.patterns.Pattern;
import java.util.ArrayList;

abstract class PatternPropertyInteraction extends PatternProperty {
    PatternGroup pattern2;
    ArrayList<Pattern> patterns = new ArrayList<>();
    Interaction interaction;

    PatternPropertyInteraction(PatternGroup pattern, PatternGroup pattern2, Interaction interaction) {
        super (pattern);
        this.pattern2 = pattern2;
        this.interaction = interaction;

        ArrayList<Pattern> patterns = new ArrayList<>();
        patterns.add(pattern.exportPattern());
        patterns.add(pattern2.exportPattern());
    }

    PatternGroup getPattern2 () {
        return pattern2;
    }

    ArrayList<Pattern> getPatterns () {
        return patterns;
    }
}
