package view.generator.boxes;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import generator.properties.model.ModelPropertySet;
import generator.properties.model.expression.BinaryNumericalExpression;
import utils.Utils;
import view.generator.ChoiceBoxCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class HBoxRhombusCriterion extends HBoxModelCriterion {

	private ChoiceBox<String> dimensionChoiceBox;
	private TextField dimensionTextField;

	public HBoxRhombusCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	protected void updateValidity() {

		String dimensionChoice = dimensionChoiceBox.getValue();

		this.getChildren().remove(dimensionTextField);
		removeWarningIconAndDeleteButton();
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
		addDeleteButton();
	}

	@Override
	protected void initialize() {

		Label dimensionLabel = new Label("Dimension: ");

		dimensionChoiceBox = new ChoiceBox<>();
		dimensionChoiceBox.getItems().addAll("Unspecified", "<=", "<", "=", ">", ">=");
		dimensionChoiceBox.getSelectionModel().selectFirst();

		dimensionChoiceBox.setOnAction(e -> {
			updateValidity();
			getPane().refreshGenerationPossibility();
		});

		dimensionTextField = new TextField();

		dimensionTextField.setOnKeyReleased(e -> {
			updateValidity();
			getPane().refreshGenerationPossibility();
		});

		this.getChildren().addAll(dimensionLabel, dimensionChoiceBox, dimensionTextField, getWarningIcon(), getDeleteButton());
		updateValidity();
	}

	@Override
	public void addPropertyExpression(ModelPropertySet modelPropertySet) {
		if (isValid()) {
			int size = "Unspecified".equals(dimensionChoiceBox.getValue()) ? -1 : Integer.decode(dimensionTextField.getText());
			modelPropertySet.getById("rhombus").addExpression(new BinaryNumericalExpression("rhombus", dimensionChoiceBox.getValue(), size));
		}
	}

}
