package view.generator.boxes;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import generator.properties.model.ModelPropertySet;
import generator.properties.model.expression.BinaryNumericalExpression;
import utils.Utils;
import view.generator.ChoiceBoxCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class HBoxCoronoidCriterion extends HBoxModelCriterion {

	public HBoxCoronoidCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	private ChoiceBox<String> operatorChoiceBox;
	private TextField fieldValue;

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

		Label nbHolesLabel = new Label("Number of holes: ");

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
			int nbHoles = operatorChoiceBox.getValue().equals("Unspecified") ? -1 : Integer.decode(fieldValue.getText());
			modelPropertySet.getById("coronoid").addExpression(new BinaryNumericalExpression("coronoid", operatorChoiceBox.getValue(), nbHoles));
		}
	}

}
