package view.generator.boxes;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import utils.Utils;
import view.generator.ChoiceBoxCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public abstract class ClassicalHBoxCriterion extends HBoxModelCriterion {

	ClassicalHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	protected ChoiceBox<String> operatorChoiceBox;
	protected TextField fieldValue;

	@Override
	protected void updateValidity() {
		if (!Utils.isNumber(fieldValue.getText()) || operatorChoiceBox.getValue() == null) {
			setValid(false);
			removeWarningIconAndDeleteButton();
			addWarningIconAndDeleteButton();
		}

		else {
			setValid(true);
			removeWarningIconAndDeleteButton();
			addDeleteButton();
		}

		if (isValid())
			getPane().refreshGenerationPossibility();
	}

	@Override
	protected void initialize() {
		setValid(false);
		operatorChoiceBox = new ChoiceBox<>();
		operatorChoiceBox.getItems().addAll("<=", "<", "=", ">", ">=");
		fieldValue = new TextField();

		operatorChoiceBox.getSelectionModel().select(2);
		fieldValue.setOnKeyReleased(e -> updateValidity());
		operatorChoiceBox.setOnAction(e -> updateValidity());
		this.getChildren().addAll(operatorChoiceBox, fieldValue, getWarningIcon(), getDeleteButton());
	}

}
