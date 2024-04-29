package view.generator.boxes;

import properties.ModelProperty;
import properties.PropertySet;
import properties.expression.PropertyExpression;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import properties.expression.BinaryNumericalExpression;
import utils.Utils;
import view.generator.ChoiceBoxCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class HBoxCoronoidCriterion extends HBoxModelCriterion {

	private ChoiceBox<String> operatorChoiceBox;
	private TextField fieldValue;

	public HBoxCoronoidCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	public void updateValidity() {

		String operatorValue = operatorChoiceBox.getValue();
		String textValue = fieldValue.getText();

		if ("Unspecified".equals(operatorValue)) {
			setValid(true);
			setBounding(false);
			this.getChildren().remove(fieldValue);
			this.getChildren().remove(getWarningIcon());
		}
		else {
			if (operatorValue == null || !Utils.isNumber(textValue)) {
				setValid(false);
				setBounding(false);
				removeWarningIconAndDeleteButton();
				addFieldIfMissing();
				addWarningIconAndDeleteButton();
			}
			else {
				setValid(true);
				setBounding(ModelProperty.isBoundingOperator(operatorValue));
				removeWarningIconAndDeleteButton();
				addFieldIfMissing();
				addDeleteButton();
			}
		}
		getPane().refreshGlobalValidity();
	}

	private void addFieldIfMissing() {
		if (!this.getChildren().contains(fieldValue))
			this.getChildren().add(fieldValue);
	}

	@Override
	protected void initialize() {
		Label nbHolesLabel = new Label("Number of holes: ");
		operatorChoiceBox = new ChoiceBox<>();
		operatorChoiceBox.getItems().addAll("Unspecified", "<=", "<", "=", ">", ">=");
		operatorChoiceBox.getSelectionModel().selectFirst();
		fieldValue = new TextField();
		this.getChildren().addAll(nbHolesLabel, operatorChoiceBox, fieldValue, getWarningIcon(), getDeleteButton());
		//updateValidity();
	}

	public void assign(PropertyExpression propertyExpression) {
		BinaryNumericalExpression expression = (BinaryNumericalExpression) propertyExpression;
		operatorChoiceBox.getSelectionModel().select(expression.getOperator());
		fieldValue.setText(String.valueOf(expression.getValue()));
	}

	@Override
	public void initEventHandling() {
		operatorChoiceBox.setOnAction(e -> updateValidity());
		fieldValue.setOnKeyReleased(e -> updateValidity());
	}

	@Override
	public void addPropertyExpression(PropertySet modelPropertySet) {
		if (isValid()) {
			int nbHoles = "Unspecified".equals(operatorChoiceBox.getValue()) ? -1 : Integer.decode(fieldValue.getText());
			modelPropertySet.getById("coronoid").addExpression(new BinaryNumericalExpression("coronoid", operatorChoiceBox.getValue(), nbHoles));
		}
	}

}
