package view.patterns;

import constraints.OccurrencePatternConstraint;


class PatternPropertyOccurrence extends PatternProperty {
    int minOccurrence;
    Interaction interaction;

    PatternPropertyOccurrence (PatternGroup pattern, Interaction interaction, int minOccurrence) {
        super (pattern, 2, new OccurrencePatternConstraint(pattern, interaction, minOccurrence));
        this.interaction = interaction;
        this.minOccurrence = minOccurrence;
    }

    @Override
    String getLabel () {
        return "# " + getPattern().getLabel().getText() + " ≥ " + minOccurrence + " / " + interaction.getLabel();
    }

    int getMinOccurrence () {
        return minOccurrence;
    }

    Interaction getInteraction() {
        return interaction;
    }
}

