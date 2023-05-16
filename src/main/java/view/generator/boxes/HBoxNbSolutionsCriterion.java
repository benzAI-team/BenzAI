package view.generator.boxes;

import generator.properties.solver.SolverPropertySet;
import javafx.scene.control.TextField;
import generator.properties.model.expression.BinaryNumericalExpression;
import utils.Utils;
import view.generator.ChoiceBoxCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class HBoxNbSolutionsCriterion extends HBoxSolverCriterion {

	private TextField nbSolutionsField;

	public HBoxNbSolutionsCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	protected void updateValidity() {

		if (!Utils.isNumber(nbSolutionsField.getText())) {
			setValid(false);
			removeWarningIconAndDeleteButton();
			addWarningIconAndDeleteButton();
		}
		else {
			setValid(true);
			removeWarningIconAndDeleteButton();
			addDeleteButton();
		}
	}

	@Override
	protected void initialize() {

		setValid(false);

		nbSolutionsField = new TextField();
		nbSolutionsField.setOnKeyReleased(e -> {
			updateValidity();
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
		updateValidity();
	}
}
