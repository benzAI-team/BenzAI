package view.generator.boxes;

import generator.properties.model.ModelPropertySet;
import generator.properties.model.expression.PropertyExpression;
import generator.properties.model.expression.RhombusExpression;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
	public void updateValidity() {

		String dimensionChoice = dimensionChoiceBox.getValue();

		this.getChildren().remove(dimensionTextField);
		removeWarningIconAndDeleteButton();
		if (dimensionChoice != null && dimensionChoice.contentEquals("Unspecified")) {
			setValid(true);
			setBounding(false);
		}
		else {

			if (dimensionChoice == null || !Utils.isNumber(dimensionTextField.getText())) {
				setValid(false);
				setBounding(false);
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

		getPane().refreshGenerationPossibility();
	}

	@Override
	protected void initialize() {
		Label dimensionLabel = new Label("Dimension: ");

		dimensionChoiceBox = new ChoiceBox<>();
		dimensionChoiceBox.getItems().addAll("Unspecified", "<=", "<", "=", ">", ">=");
		dimensionChoiceBox.getSelectionModel().selectFirst();

		dimensionTextField = new TextField();

		this.getChildren().addAll(dimensionLabel, dimensionChoiceBox, dimensionTextField, getWarningIcon(), getDeleteButton());
		updateValidity();
	}

	@Override
	public void assign(PropertyExpression propertyExpression) {
		RhombusExpression expression = (RhombusExpression) propertyExpression;
		dimensionChoiceBox.getSelectionModel().select(expression.getHeightOperator());
		dimensionTextField.setText(String.valueOf(expression.getHeight()));
	}

	@Override
	public void initEventHandling() {
		dimensionChoiceBox.setOnAction(e -> {
			updateValidity();
			getPane().refreshGenerationPossibility();
		});
		dimensionTextField.setOnKeyReleased(e -> {
			updateValidity();
			getPane().refreshGenerationPossibility();
		});
	}

	@Override
	public void addPropertyExpression(ModelPropertySet modelPropertySet) {
		if (isValid()) {
			int size = "Unspecified".equals(dimensionChoiceBox.getValue()) ? -1 : Integer.decode(dimensionTextField.getText());
			modelPropertySet.getById("rhombus").addExpression(new RhombusExpression("rhombus", dimensionChoiceBox.getValue(), size));
		}
	}

}
