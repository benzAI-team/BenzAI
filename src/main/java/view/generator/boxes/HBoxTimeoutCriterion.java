package view.generator.boxes;

import java.util.ArrayList;

import generator.GeneratorCriterion;
import generator.properties.PropertySet;
import generator.properties.solver.SolverProperty;
import generator.properties.solver.SolverPropertySet;
import generator.properties.solver.TimeLimitProperty;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import modelProperty.ModelPropertySet;
import modelProperty.expression.BinaryNumericalExpression;
import modelProperty.expression.SubjectExpression;
import utils.Utils;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.primaryStage.ScrollPaneWithPropertyList;

public class HBoxTimeoutCriterion extends HBoxSolverCriterion {

	private TextField timeField;
	private ChoiceBox<String> timeUnitBox;

	public HBoxTimeoutCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	protected void checkValidity() {

		if (!Utils.isNumber(timeField.getText()) && timeUnitBox.getValue() != null) {
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
		timeField = new TextField();
		timeUnitBox = new ChoiceBox<String>();
		timeUnitBox.getItems().addAll("milliseconds", "seconds", "minutes", "hours");
		timeUnitBox.getSelectionModel().select(2);

		timeField.setOnKeyReleased(e -> {
			checkValidity();
		});

		timeUnitBox.setOnAction(e -> {
			checkValidity();
		});

		this.getChildren().addAll(timeField, timeUnitBox, getWarningIcon(), getDeleteButton());
		checkValidity();
	}


	@Override
	public void addPropertyExpression(SolverPropertySet propertySet) {
		if (isValid()) {
			double time = Double.parseDouble(timeField.getText());

			if (timeUnitBox.getValue().equals("milliseconds"))
				time = time;

			else if (timeUnitBox.getValue().equals("seconds"))
				time = time * 1000;

			else if (timeUnitBox.getValue().equals("minutes"))
				time = time * 60000;

			else if (timeUnitBox.getValue().equals("hours"))
				time = time * 360000;

			String value = Double.toString(time) + "s";

			((SolverProperty)propertySet.getById("timeout")).addExpression(new BinaryNumericalExpression("timeout", "=", (int)time));

		}
	}

	public void setTime(String time) {
		timeField.setText(time);
		checkValidity();
	}

	public void setTimeUnit(String timeUnit) {
		timeUnitBox.getSelectionModel().select(timeUnit);
		checkValidity();
	}
}
