package view.generator.boxes;

import generator.properties.model.expression.PropertyExpression;
import generator.properties.solver.SolverPropertySet;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import generator.properties.model.expression.BinaryNumericalExpression;
import utils.Utils;
import view.generator.ChoiceBoxCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class HBoxTimeoutCriterion extends HBoxSolverCriterion {

	private TextField timeField;
	private ChoiceBox<String> timeUnitBox;

	public HBoxTimeoutCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	public void updateValidity() {
		if (Utils.isNumber(timeField.getText()) && timeUnitBox.getValue() != null) {
			setValid(true);
			removeWarningIcon();
		}

		else {
			setValid(false);
			removeWarningIcon();
			addWarningIcon();
		}
	}

	@Override
	protected void initialize() {

		setValid(false);
		timeField = new TextField();
		timeUnitBox = new ChoiceBox<>();
		timeUnitBox.getItems().addAll("milliseconds", "seconds", "minutes", "hours");
		timeUnitBox.getSelectionModel().select(2);


		this.getChildren().addAll(timeField, timeUnitBox, getWarningIcon());
		this.updateValidity();
	}

	@Override
	public void assign(PropertyExpression expression) { // TODO useless
	}

	@Override
	public void initEventHandling() {
		timeField.setOnKeyReleased(e -> this.updateValidity());
		timeUnitBox.setOnAction(e -> this.updateValidity());
	}


	@Override
	public void addPropertyExpression(SolverPropertySet propertySet) {
		if (isValid()) {
			double time = Double.parseDouble(timeField.getText());

			if ("seconds".equals(timeUnitBox.getValue()))
				time = time * 1000;
			else if ("minutes".equals(timeUnitBox.getValue()))
				time = time * 60000;
			else if ("hours".equals(timeUnitBox.getValue()))
				time = time * 360000;
			propertySet.getById("timeout").addExpression(new BinaryNumericalExpression("timeout", "=", (int)time));
		}
	}

	public void setTime(String time) {
		timeField.setText(time);
		this.updateValidity();
	}

	public void setTimeUnit(String timeUnit) {
		timeUnitBox.getSelectionModel().select(timeUnit);
		this.updateValidity();
	}
}
