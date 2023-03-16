package view.generator.boxes;

import java.util.ArrayList;

import generator.GeneratorCriterion;
import generator.properties.PropertySet;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import modelProperty.ModelProperty;
import modelProperty.ModelPropertySet;
import modelProperty.expression.RectangleExpression;
import utils.Utils;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.primaryStage.ScrollPaneWithPropertyList;

public class HBoxRectangleCriterion extends HBoxModelCriterion {

	private GridPane gridPane;

	protected ChoiceBox<String> heightChoiceBox;
	protected TextField heightTextField;

	protected ChoiceBox<String> widthChoiceBox;
	protected TextField widthTextField;

	private HBox hBoxHeight;
	private HBox hBoxWidth;

	protected boolean valid1;
	protected boolean valid2;

	public HBoxRectangleCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	protected void checkValidity() {

		String heightChoice = heightChoiceBox.getValue();
		String widthChoice = widthChoiceBox.getValue();

		/*
		 * Nb Lines
		 */

		hBoxHeight.getChildren().remove(heightTextField);
		hBoxHeight.getChildren().remove(getWarningIcon());
		hBoxHeight.getChildren().remove(getDeleteButton());

		if (heightChoice != null && heightChoice.equals("Unspecified"))
			valid1 = true;

		else {

			if (heightChoice == null || !Utils.isNumber(heightTextField.getText())) {
				valid1 = false;
				hBoxHeight.getChildren().addAll(heightTextField);
			}

			else {
				valid1 = true;
				hBoxHeight.getChildren().addAll(heightTextField);
			}
		}

		/*
		 * Nb Columns
		 */

		hBoxWidth.getChildren().remove(widthTextField);
		hBoxWidth.getChildren().remove(getWarningIcon());
		hBoxWidth.getChildren().remove(getDeleteButton());

		if (widthChoice != null && widthChoice.equals("Unspecified"))
			valid2 = true;

		else {

			if (widthChoice == null || !Utils.isNumber(widthTextField.getText())) {
				valid2 = false;
				hBoxWidth.getChildren().addAll(widthTextField);
			}

			else {
				valid2 = true;
				hBoxWidth.getChildren().addAll(widthTextField);
			}
		}

		setValid(valid1 && valid2);

		if (!isValid())
			hBoxHeight.getChildren().add(getWarningIcon());

		hBoxHeight.getChildren().add(getDeleteButton());
	}

	@Override
	protected void initialize() {

		valid1 = false;
		valid2 = false;

		Label heightLabel = new Label("Height:   ");
		heightChoiceBox = new ChoiceBox<>();
		heightChoiceBox.getItems().addAll("Unspecified", "<=", "<", "=", ">", ">=");
		heightChoiceBox.getSelectionModel().selectFirst();

		heightTextField = new TextField();

		heightChoiceBox.setOnAction(e -> {
			checkValidity();
			getPane().refreshGenerationPossibility();
		});

		heightTextField.setOnKeyReleased(e -> {
			checkValidity();
			getPane().refreshGenerationPossibility();
		});

		hBoxHeight = new HBox(5.0);
		hBoxHeight.getChildren().addAll(heightLabel, heightChoiceBox, heightTextField, getWarningIcon(), getDeleteButton());

		Label widthLabel = new Label("Width: ");
		widthChoiceBox = new ChoiceBox<>();
		widthChoiceBox.getItems().addAll("Unspecified", "<=", "<", "=", ">", ">=");
		widthChoiceBox.getSelectionModel().selectFirst();
		widthTextField = new TextField();

		widthChoiceBox.setOnAction(e -> {
			checkValidity();
			getPane().refreshGenerationPossibility();
		});

		widthTextField.setOnKeyReleased(e -> {
			checkValidity();
			getPane().refreshGenerationPossibility();
		});

		hBoxWidth = new HBox(5.0);
		hBoxWidth.getChildren().addAll(widthLabel, widthChoiceBox, widthTextField);

		gridPane = new GridPane();

		gridPane.add(hBoxHeight, 0, 0);
		gridPane.add(hBoxWidth, 0, 1);

		checkValidity();
		this.getChildren().add(gridPane);
	}

	@Override
	public void addPropertyExpression(ModelPropertySet modelPropertySet) {
		if (isValid()) {
			int height = heightTextField.getText().equals("Unspecified") ? -1 : Integer.decode(heightTextField.getText());
			int width = widthTextField.getText().equals("Unspecified") ? -1 : Integer.decode(widthTextField.getText());
			((ModelProperty) modelPropertySet.getById("rectangle")).addExpression(new RectangleExpression("rectangle", heightChoiceBox.getValue(), height, widthChoiceBox.getValue(), width));
		}
	}

}
