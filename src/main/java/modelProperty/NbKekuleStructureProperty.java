package modelProperty;

import modelProperty.checkers.Checker;
import modelProperty.checkers.NbKekuleStructureChecker;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.generator.boxes.HBoxModelCriterion;
import view.generator.boxes.HBoxNbKekuleStructuresCriterion;

public class NbKekuleStructureProperty extends ModelProperty {

	public NbKekuleStructureProperty() {
		super("kekule", "Number of Kekule structures", new NbKekuleStructureChecker());
	}

	@Override
	public HBoxModelCriterion getHBoxCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxNbKekuleStructuresCriterion(parent, choiceBoxCriterion);
	}

}
