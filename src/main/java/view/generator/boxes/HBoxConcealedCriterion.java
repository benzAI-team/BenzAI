package view.generator.boxes;

import generator.properties.model.ModelPropertySet;
import generator.properties.model.expression.PropertyExpression;
import generator.properties.model.expression.SubjectExpression;
import view.generator.ChoiceBoxCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class HBoxConcealedCriterion extends HBoxModelCriterion {

	public HBoxConcealedCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
		setValid(true);
	}

	@Override
	public void updateValidity() {
		setValid(true);
		setBounding(false);
		getPane().refreshGenerationPossibility();
	}

	@Override
	protected void initialize() {
		addDeleteButton();
	}


	@Override
	public void assign(PropertyExpression propertyExpression) {}

	@Override
	public void initEventHandling() {}

	@Override
	public void addPropertyExpression(ModelPropertySet modelPropertySet) {
		modelPropertySet.getById("concealed").addExpression(new SubjectExpression("concealed"));
	}

}
