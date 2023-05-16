package view.generator.boxes;

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
	protected void updateValidity() {

		if (!Utils.isNumber(timeField.getText()) && timeUnitBox.getValue() != null) {
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
		timeField = new TextField();
		timeUnitBox = new ChoiceBox<String>();
		timeUnitBox.getItems().addAll("milliseconds", "seconds", "minutes", "hours");
		timeUnitBox.getSelectionModel().select(2);

		timeField.setOnKeyReleased(e -> {
			this.updateValidity();
		});

		timeUnitBox.setOnAction(e -> {
			this.updateValidity();
		});

		this.getChildren().addAll(timeField, timeUnitBox, getWarningIcon(), getDeleteButton());
		this.updateValidity();
	}


	@Override
	public void addPropertyExpression(SolverPropertySet propertySet) {
		if (isValid()) {
			double time = Double.parseDouble(timeField.getText());

			if ("milliseconds".equals(timeUnitBox.getValue()))
				time = time;

			else if ("seconds".equals(timeUnitBox.getValue()))
				time = time * 1000;

			else if ("minutes".equals(timeUnitBox.getValue()))
				time = time * 60000;

			else if ("hours".equals(timeUnitBox.getValue()))
				time = time * 360000;

			String value = time + "s";

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
