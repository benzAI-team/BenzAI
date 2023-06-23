package view.generator.boxes;

import generator.properties.model.ModelPropertySet;
import view.generator.ChoiceBoxCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

@SuppressWarnings("unused")
public abstract class HBoxModelCriterion extends HBoxCriterion {
	
	public HBoxModelCriterion(ScrollPaneWithPropertyList pane, ChoiceBoxCriterion choiceBoxCriterion) {
		super(pane, choiceBoxCriterion);
	}

	public abstract void addPropertyExpression(ModelPropertySet propertySet);

}
