package view.generator.boxes;

import java.util.ArrayList;

import generator.GeneratorCriterion;
import modelProperty.ModelPropertySet;
import modelProperty.expression.SubjectExpression;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;

public class HBoxCatacondensedCriterion extends HBoxCriterion {

	public HBoxCatacondensedCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
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
		 modelPropertySet.getBySubject("catacondensed").addExpression(new SubjectExpression("catacondensed"));
	}

}
