package view.generator.boxes;

import java.util.ArrayList;

import generator.GeneratorCriterion;
import generator.GeneratorCriterion.Operator;
import generator.GeneratorCriterion.Subject;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import modelProperty.ModelPropertySet;
import utils.Utils;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;

public class HBoxTimeoutCriterion extends HBoxCriterion {

	private TextField timeField;
	private ChoiceBox<String> timeUnitBox;

	public HBoxTimeoutCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	protected void checkValidity() {

		if (!Utils.isNumber(timeField.getText()) && timeUnitBox.getValue() != null) {
			valid = false;
			this.getChildren().remove(getWarningIcon());
			this.getChildren().remove(getDeleteButton());
			this.getChildren().addAll(getWarningIcon(), getDeleteButton());
		}

		else {
			valid = true;
			this.getChildren().remove(getWarningIcon());
			this.getChildren().remove(getDeleteButton());
			this.getChildren().add(getDeleteButton());
		}
	}

	@Override
	protected void initialize() {

		valid = false;
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
	public void addPropertyExpression(ModelPropertySet modelPropertySet) {

		ArrayList<GeneratorCriterion> criterions = new ArrayList<>();
		Operator operator = Operator.EQ;

		if (valid) {
			Subject subject = Subject.TIMEOUT;

			double time = Double.parseDouble(timeField.getText());

			if (timeUnitBox.getValue().equals("milliseconds"))
				time = time / 1000.0;

			else if (timeUnitBox.getValue().equals("seconds"))
				time = time * 1.0;

			else if (timeUnitBox.getValue().equals("minutes"))
				time = time * 60.0;

			else if (timeUnitBox.getValue().equals("hours"))
				time = time * 3600;

			String value = Double.toString(time) + "s";

			criterions.add(new GeneratorCriterion(subject, operator, value));

		}

		return criterions;
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
