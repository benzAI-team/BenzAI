package view.generator.boxes;

import java.util.ArrayList;

import generator.GeneratorCriterion;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import modelProperty.ModelPropertySet;
import modelProperty.expression.BinaryNumericalExpression;
import utils.Utils;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;

public class HBoxRhombusCriterion extends HBoxCriterion {

	private ChoiceBox<String> dimensionChoiceBox;
	private TextField dimensionTextField;

	public HBoxRhombusCriterion(GeneratorPane generatorPane, ChoiceBoxCriterion choiceBoxCriterion) {
		super(generatorPane, choiceBoxCriterion);
	}

	@Override
	protected void checkValidity() {

		String dimensionChoice = dimensionChoiceBox.getValue();

		this.getChildren().remove(dimensionTextField);
		this.getChildren().remove(getWarningIcon());
		this.getChildren().remove(deleteButton);

		if (dimensionChoice != null && dimensionChoice.contentEquals("Unspecified"))
			setValid(true);

		else {

			if (dimensionChoice == null || !Utils.isNumber(dimensionTextField.getText())) {
				setValid(false);
				this.getChildren().add(dimensionTextField);
			}

			else {
				setValid(true);
				this.getChildren().add(dimensionTextField);
			}
		}

		if (!isValid())
			this.getChildren().add(getWarningIcon());

		this.getChildren().add(deleteButton);
	}

	@Override
	protected void initialize() {

		Label dimensionLabel = new Label("Dimension: ");

		dimensionChoiceBox = new ChoiceBox<>();
		dimensionChoiceBox.getItems().addAll("Unspecified", "<=", "<", "=", ">", ">=");
		dimensionChoiceBox.getSelectionModel().selectFirst();

		dimensionChoiceBox.setOnAction(e -> {
			checkValidity();
			getGeneratorPane().refreshGenerationPossibility();
		});

		dimensionTextField = new TextField();

		dimensionTextField.setOnKeyReleased(e -> {
			checkValidity();
			getGeneratorPane().refreshGenerationPossibility();
		});

		this.getChildren().addAll(dimensionLabel, dimensionChoiceBox, dimensionTextField, getWarningIcon(), deleteButton);
		checkValidity();
	}

	@Override
	public void addPropertyExpression(ModelPropertySet modelPropertySet) {
		if (isValid()) {
			int size = dimensionChoiceBox.getValue().equals("Unspecified") ? -1 : Integer.decode(dimensionTextField.getText());
			modelPropertySet.getBySubject("rhombus").addExpression(new BinaryNumericalExpression("rhombus", dimensionChoiceBox.getValue(), size));
		}
	}

}
