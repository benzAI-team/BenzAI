package view.patterns;


import constraints.InteractionPatternConstraint;
import view.generator.boxes.HBoxPatternCriterion;

public class InteractionItem {
    Interaction interaction;
    private PatternProperty patternProperty1;
    private PatternProperty patternProperty2;

    InteractionItem(PatternProperty patternProperty1, PatternProperty patternProperty2, Interaction interaction) {
        this.patternProperty1 = patternProperty1;
        this.patternProperty2 = patternProperty2;
        this.interaction = interaction;
    }

    public String getLabel() {
        return interaction.getLabel() + " " + patternProperty1.getPattern().getLabel().getText()+ " " + patternProperty2.getPattern().getLabel().getText();
    }

    void addInteraction(HBoxPatternCriterion patternConstraintHBox) {
        patternConstraintHBox.addInteraction(new InteractionPatternConstraint(patternProperty1.getPatternConstraint(), patternProperty2.getPatternConstraint(), interaction));
    }

    public PatternProperty getPatternProperty1() {
        return patternProperty1;
    }

    public PatternProperty getPatternProperty2() {
        return patternProperty2;
    }

    public Interaction getInteraction() {
        return interaction;
    }
}
