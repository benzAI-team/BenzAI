package modelProperty;

import modelProperty.testers.PatternTester;
import modules.PatternModule;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.generator.boxes.HBoxModelCriterion;
import view.generator.boxes.HBoxPatternCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class PatternProperty extends ModelProperty {

	public PatternProperty() {
		super("pattern", "Pattern properties", new PatternModule(), new PatternTester());
	}

	@Override
	public HBoxModelCriterion getHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxPatternCriterion(parent, choiceBoxCriterion, this);
	}

}
