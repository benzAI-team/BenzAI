package view.generator.boxes;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import modelProperty.ModelProperty;
import utils.Utils;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;

public abstract class ClassicalHBoxCriterion extends HBoxModelCriterion {

	public ClassicalHBoxCriterion(GeneratorPane generatorPane, ChoiceBoxCriterion choiceBoxCriterion) {
		super(generatorPane, choiceBoxCriterion);
	}

	protected ChoiceBox<String> operatorChoiceBox;
	protected TextField fieldValue;

	@Override
	protected void checkValidity() {

		if (!Utils.isNumber(fieldValue.getText()) || operatorChoiceBox.getValue() == null) {
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

		if (isValid())
			getGeneratorPane().refreshGenerationPossibility();
	}

	@Override
	protected void initialize() {

		setValid(false);

		operatorChoiceBox = new ChoiceBox<String>();
		operatorChoiceBox.getItems().addAll("<=", "<", "=", ">", ">=");
		fieldValue = new TextField();

		operatorChoiceBox.getSelectionModel().select(2);

		fieldValue.setOnKeyReleased(e -> {
			checkValidity();
		});

		operatorChoiceBox.setOnAction(e -> {
			checkValidity();
		});

		this.getChildren().addAll(operatorChoiceBox, fieldValue, getWarningIcon(), getDeleteButton());

	}

}
