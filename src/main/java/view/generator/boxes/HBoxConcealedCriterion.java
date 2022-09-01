package view.generator.boxes;

import modelProperty.ModelPropertySet;
import modelProperty.expression.SubjectExpression;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;

public class HBoxConcealedCriterion extends HBoxCriterion {

	public HBoxConcealedCriterion(GeneratorPane generatorPane, ChoiceBoxCriterion choiceBoxCriterion) {
		super(generatorPane, choiceBoxCriterion);
		setValid(true);
	}

	@Override
	protected void checkValidity() {
		setValid(true);
		getGeneratorPane().refreshGenerationPossibility();
	}

	@Override
	protected void initialize() {
		this.getChildren().add(getDeleteButton());
	}

	@Override
	public void addPropertyExpression(ModelPropertySet modelPropertySet) {
		modelPropertySet.getBySubject("concealed").addExpression(new SubjectExpression("concealed"));
	}

}
