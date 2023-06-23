package view.generator.boxes;

import generator.properties.model.expression.PropertyExpression;
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
	public void updateValidity() {

		if (Utils.isNumber(nbSolutionsField.getText())) {
			setValid(true);
			removeWarningIconAndDeleteButton();
			addDeleteButton();
		} else {
			setValid(false);
			removeWarningIconAndDeleteButton();
			addWarningIconAndDeleteButton();
		}
	}

	@Override
	protected void initialize() {
		setValid(false);
		nbSolutionsField = new TextField();
		this.getChildren().addAll(nbSolutionsField, getWarningIcon(), getDeleteButton());
	}

	@Override
	public void assign(PropertyExpression expression) {
		//TODO useless
	}

	@Override
	public void initEventHandling() {
		nbSolutionsField.setOnKeyReleased(e -> updateValidity());
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
