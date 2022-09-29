package view.generator.boxes;

import java.util.ArrayList;

import generator.GeneratorCriterion;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import modelProperty.ModelProperty;
import modelProperty.ModelPropertySet;
import modelProperty.expression.BinaryNumericalExpression;
import utils.Utils;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;

public class HBoxCoronenoidCriterion extends HBoxCriterion {

	private ChoiceBox<String> operatorChoiceBox;
	private TextField fieldValue;

	public HBoxCoronenoidCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void checkValidity() {

		String operatorValue = operatorChoiceBox.getValue();
		String textValue = fieldValue.getText();

		if (operatorValue != null && operatorValue.equals("Unspecified")) {

			setValid(true);
			this.getChildren().remove(fieldValue);
			this.getChildren().remove(getWarningIcon());
		}

		else {

			if (operatorValue == null || !Utils.isNumber(textValue)) {

				setValid(false);

				this.getChildren().remove(getWarningIcon());
				this.getChildren().remove(getDeleteButton());

				if (!this.getChildren().contains(fieldValue))
					this.getChildren().add(fieldValue);

				this.getChildren().addAll(getWarningIcon(), getDeleteButton());
			}

			else {

				setValid(true);

				this.getChildren().remove(getWarningIcon());
				this.getChildren().remove(getDeleteButton());

				if (!this.getChildren().contains(fieldValue))
					this.getChildren().add(fieldValue);

				this.getChildren().addAll(getDeleteButton());
			}
		}
	}

	@Override
	protected void initialize() {

		Label nbHolesLabel = new Label("Number of crowns: ");

		operatorChoiceBox = new ChoiceBox<>();
		operatorChoiceBox.getItems().addAll("Unspecified", "<=", "<", "=", ">", ">=");
		operatorChoiceBox.getSelectionModel().selectFirst();

		operatorChoiceBox.setOnAction(e -> {
			checkValidity();
		});

		fieldValue = new TextField();

		fieldValue.setOnKeyReleased(e -> {
			checkValidity();
		});

		this.getChildren().addAll(nbHolesLabel, operatorChoiceBox, fieldValue, getWarningIcon(), getDeleteButton());
		checkValidity();
	}

	@Override
	public void addPropertyExpression(ModelPropertySet modelPropertySet) {
		if (isValid()) {
			int nbCrowns = operatorChoiceBox.getValue().equals("Unspecified") ? - 1 : Integer.decode(fieldValue.getText());
			modelPropertySet.getById("coronenoid").addExpression(new BinaryNumericalExpression("coronenoid", operatorChoiceBox.getValue(), nbCrowns));
		}
	}

}
