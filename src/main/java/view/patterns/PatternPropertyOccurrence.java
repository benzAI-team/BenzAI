package view.patterns;

import constraints.OccurrencePatternConstraint;


class PatternPropertyOccurrence extends PatternProperty {
    int minOccurrence;
    int maxOccurrence;
    Interaction interaction;

    PatternPropertyOccurrence (PatternGroup pattern, Interaction interaction, int minOccurrence, int maxOccurrence) {
        super (pattern, 2, new OccurrencePatternConstraint(pattern, interaction, minOccurrence, maxOccurrence));
        this.interaction = interaction;
        this.minOccurrence = minOccurrence;
        this.maxOccurrence = maxOccurrence;
    }

    @Override
    String getLabel () {
        return "Occurr " + interaction.getLabel() + getPattern().getLabel().getText() + " " + minOccurrence + "-" + maxOccurrence;
    }

    int getMinOccurrence () {
        return minOccurrence;
    }

    int getMaxOccurrence() {
        return maxOccurrence;
    }

    Interaction getInteraction() {
        return interaction;
    }
}

