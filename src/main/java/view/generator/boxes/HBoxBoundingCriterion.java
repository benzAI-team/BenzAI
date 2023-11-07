package view.generator.boxes;

import generator.properties.model.ModelProperty;
import generator.properties.model.expression.BinaryNumericalExpression;
import generator.properties.model.expression.PropertyExpression;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import utils.Utils;
import view.generator.ChoiceBoxCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public abstract class HBoxBoundingCriterion extends HBoxModelCriterion {
	HBoxBoundingCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	private ChoiceBox<String> operatorChoiceBox;
	private TextField fieldValue;

	@Override
	public void updateValidity() {
		if (!Utils.isNumber(getFieldValue().getText()) || getOperatorChoiceBox().getValue() == null) {
			setValid(false);
			removeWarningIconAndDeleteButton();
			addWarningIconAndDeleteButton();
		} else {
			setValid(true);
			removeWarningIconAndDeleteButton();
			addDeleteButton();
		}
		setBounding(isValid() && ModelProperty.isBoundingOperator(getOperatorChoiceBox().getValue()));
		getPane().refreshGenerationPossibility();
	}
	@Override
	protected void initialize() {
		setValid(false);
		operatorChoiceBox = new ChoiceBox<>();
		operatorChoiceBox.getItems().addAll("<=", "<", "=", ">", ">=");
		operatorChoiceBox.getSelectionModel().select("=");
		fieldValue = new TextField();
		this.getChildren().addAll(operatorChoiceBox, fieldValue, getWarningIcon(), getDeleteButton());
	}

	@Override
	public void initEventHandling(){
		getFieldValue().setOnKeyReleased(e -> updateValidity());
		getOperatorChoiceBox().setOnAction(e -> updateValidity());
	}

	@Override
	public void assign(PropertyExpression propertyExpression) {
		BinaryNumericalExpression expression = (BinaryNumericalExpression) propertyExpression;
		fieldValue.setText(String.valueOf(expression.getValue()));
		getOperatorChoiceBox().getSelectionModel().select(expression.getOperator());
		//System.out.println(getOperatorChoiceBox().getSelectionModel().getSelectedItem() + fieldValue.getText());
		//updateValidity();
	}

	public ChoiceBox<String> getOperatorChoiceBox() {
		return operatorChoiceBox;
	}

	TextField getFieldValue() {
		return fieldValue;
	}
}
