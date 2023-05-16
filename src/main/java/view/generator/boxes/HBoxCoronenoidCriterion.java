package view.generator.boxes;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import generator.properties.model.ModelPropertySet;
import generator.properties.model.expression.BinaryNumericalExpression;
import utils.Utils;
import view.generator.ChoiceBoxCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class HBoxCoronenoidCriterion extends HBoxModelCriterion {

	private ChoiceBox<String> operatorChoiceBox;
	private TextField fieldValue;

	public HBoxCoronenoidCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void updateValidity() {

		String operatorValue = operatorChoiceBox.getValue();
		String textValue = fieldValue.getText();

		if (operatorValue != null && "Unspecified".equals(operatorValue)) {

			setValid(true);
			this.getChildren().remove(fieldValue);
			this.getChildren().remove(getWarningIcon());
		}

		else {
			if (operatorValue == null || !Utils.isNumber(textValue)) {
				setValid(false);
				removeWarningIconAndDeleteButton();
				addFieldIfMissing();
				addWarningIconAndDeleteButton();
			}
			else {
				setValid(true);
				removeWarningIconAndDeleteButton();
				addFieldIfMissing();
				addDeleteButton();
			}
		}
	}

	private void addFieldIfMissing() {
		if (!this.getChildren().contains(fieldValue))
			this.getChildren().add(fieldValue);
	}

	@Override
	protected void initialize() {

		Label nbHolesLabel = new Label("Number of crowns: ");

		operatorChoiceBox = new ChoiceBox<>();
		operatorChoiceBox.getItems().addAll("Unspecified", "<=", "<", "=", ">", ">=");
		operatorChoiceBox.getSelectionModel().selectFirst();

		operatorChoiceBox.setOnAction(e -> {
			updateValidity();
		});

		fieldValue = new TextField();

		fieldValue.setOnKeyReleased(e -> {
			updateValidity();
		});

		this.getChildren().addAll(nbHolesLabel, operatorChoiceBox, fieldValue, getWarningIcon(), getDeleteButton());
		updateValidity();
	}

	@Override
	public void addPropertyExpression(ModelPropertySet modelPropertySet) {
		if (isValid()) {
			int nbCrowns = "Unspecified".equals(operatorChoiceBox.getValue()) ? - 1 : Integer.decode(fieldValue.getText());
			modelPropertySet.getById("coronenoid").addExpression(new BinaryNumericalExpression("coronenoid", operatorChoiceBox.getValue(), nbCrowns));
		}
	}

}
