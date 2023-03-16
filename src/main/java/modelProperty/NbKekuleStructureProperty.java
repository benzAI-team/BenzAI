package modelProperty;

import modelProperty.checkers.Checker;
import modelProperty.checkers.NbKekuleStructureChecker;
import modelProperty.testers.NbKekuleStructureTester;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.generator.boxes.HBoxModelCriterion;
import view.generator.boxes.HBoxNbKekuleStructuresCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class NbKekuleStructureProperty extends ModelProperty {

	public NbKekuleStructureProperty() {
		super("kekule", "Number of Kekule structures", new NbKekuleStructureChecker(), new NbKekuleStructureTester());
	}

	@Override
	public HBoxModelCriterion getHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxNbKekuleStructuresCriterion(parent, choiceBoxCriterion);
	}

}
