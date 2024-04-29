package view.generator.boxes;

import properties.PropertySet;
import view.generator.ChoiceBoxCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

@SuppressWarnings("unused")
public abstract class HBoxModelCriterion extends HBoxCriterion {
	
	public HBoxModelCriterion(ScrollPaneWithPropertyList pane, ChoiceBoxCriterion choiceBoxCriterion) {
		super(pane, choiceBoxCriterion);
	}

	public abstract void addPropertyExpression(PropertySet propertySet);

}
