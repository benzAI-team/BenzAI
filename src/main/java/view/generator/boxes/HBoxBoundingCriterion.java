package view.generator.boxes;

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
	protected void updateValidity() {
		if (!Utils.isNumber(getFieldValue().getText()) || getOperatorChoiceBox().getValue() == null) {
			setValid(false);
			removeWarningIconAndDeleteButton();
			addWarningIconAndDeleteButton();
		}
		else {
			setValid(true);
			removeWarningIconAndDeleteButton();
			addDeleteButton();
			getPane().refreshGenerationPossibility();
		}
	}

	@Override
	protected void initialize() {
		setValid(false);
		operatorChoiceBox = new ChoiceBox<>();
		getOperatorChoiceBox().getItems().addAll("<=", "<", "=", ">", ">=");
		fieldValue = new TextField();

		getOperatorChoiceBox().getSelectionModel().select(2);
		getFieldValue().setOnKeyReleased(e -> updateValidity());
		getOperatorChoiceBox().setOnAction(e -> updateValidity());
		this.getChildren().addAll(getOperatorChoiceBox(), getFieldValue(), getWarningIcon(), getDeleteButton());
	}

	ChoiceBox<String> getOperatorChoiceBox() {
		return operatorChoiceBox;
	}

	TextField getFieldValue() {
		return fieldValue;
	}
}
