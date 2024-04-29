package properties;

import properties.ModelProperty;
import properties.checkers.NbKekuleStructureChecker;
import properties.filters.NbKekuleStructureFilter;
import view.generator.ChoiceBoxCriterion;
import view.generator.boxes.HBoxModelCriterion;
import view.generator.boxes.HBoxNbKekuleStructuresCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class NbKekuleStructureProperty extends ModelProperty {

	public NbKekuleStructureProperty() {
		super("kekule", "Number of Kekule structures", new NbKekuleStructureChecker(), new NbKekuleStructureFilter());
	}

	@Override
	public HBoxModelCriterion makeHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxNbKekuleStructuresCriterion(parent, choiceBoxCriterion);
	}

}
