package view.generator.boxes;

import generator.properties.solver.SolverPropertySet;
import javafx.scene.control.TextField;
import modelProperty.expression.BinaryNumericalExpression;
import modelProperty.expression.SubjectExpression;
import utils.Utils;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.primaryStage.ScrollPaneWithPropertyList;

public class HBoxNbSolutionsCriterion extends HBoxSolverCriterion {

	private TextField nbSolutionsField;

	public HBoxNbSolutionsCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	protected void checkValidity() {

		if (!Utils.isNumber(nbSolutionsField.getText())) {
			setValid(false);
			this.getChildren().remove(getWarningIcon());
			this.getChildren().remove(getDeleteButton());
			this.getChildren().addAll(getWarningIcon(), getDeleteButton());
		}

		else {
			setValid(true);
			this.getChildren().remove(getWarningIcon());
			this.getChildren().remove(getDeleteButton());
			this.getChildren().add(getDeleteButton());
		}
	}

	@Override
	protected void initialize() {

		setValid(false);

		nbSolutionsField = new TextField();
		nbSolutionsField.setOnKeyReleased(e -> {
			checkValidity();
		});

		this.getChildren().addAll(nbSolutionsField, getWarningIcon(), getDeleteButton());
	}

	@Override
	public void addPropertyExpression(SolverPropertySet propertySet) {

		if (isValid()) {
			String value = nbSolutionsField.getText();
			propertySet.getById("solution_number").addExpression(new BinaryNumericalExpression("solution_number", "=", Integer.parseInt(value)));
		}
	}

	public void setNbSolutions(String nbSolutions) {
		nbSolutionsField.setText(nbSolutions);
		checkValidity();
	}
}
