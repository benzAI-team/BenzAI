package modelProperty;

import modules.SymmetriesModule;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.generator.boxes.HBoxCriterion;
import view.generator.boxes.HBoxSymmetriesCriterion;

public class SymmetryProperty extends ModelProperty {

	public SymmetryProperty() {
		super("symmetry", "Symmetries", new SymmetriesModule());
	}

	@Override
	public HBoxCriterion getHBoxCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxSymmetriesCriterion(parent, choiceBoxCriterion);
	}
}
