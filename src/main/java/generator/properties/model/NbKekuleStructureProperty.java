package generator.properties.model;

import generator.properties.model.checkers.NbKekuleStructureChecker;
import generator.properties.model.filters.NbKekuleStructureFilter;
import view.generator.ChoiceBoxCriterion;
import view.generator.boxes.HBoxModelCriterion;
import view.generator.boxes.HBoxNbKekuleStructuresCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class NbKekuleStructureProperty extends ModelProperty {

	public NbKekuleStructureProperty() {
		super("kekule", "Number of Kekule structures", new NbKekuleStructureChecker(), new NbKekuleStructureFilter());
	}

	@Override
	public HBoxModelCriterion getHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxNbKekuleStructuresCriterion(parent, choiceBoxCriterion);
	}

}
