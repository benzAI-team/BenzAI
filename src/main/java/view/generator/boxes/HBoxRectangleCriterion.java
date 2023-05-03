package view.generator.boxes;

import generator.properties.model.ModelPropertySet;
import generator.properties.model.expression.RectangleExpression;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import utils.Utils;
import view.generator.ChoiceBoxCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class HBoxRectangleCriterion extends HBoxModelCriterion {

	private ChoiceBox<String> heightChoiceBox;
	private TextField heightTextField;

	private ChoiceBox<String> widthChoiceBox;
	private TextField widthTextField;

	private HBox hBoxHeight;
	private HBox hBoxWidth;

	private boolean validHeight;
	private boolean validWidth;

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

		if ("Unspecified".equals(heightChoice))
			validHeight = true;

		else {

			if (heightChoice == null || !Utils.isNumber(heightTextField.getText())) {
				validHeight = false;
				hBoxHeight.getChildren().addAll(heightTextField);
			}

			else {
				validHeight = true;
				hBoxHeight.getChildren().addAll(heightTextField);
			}
		}

		/*
		 * Nb Columns
		 */

		hBoxWidth.getChildren().remove(widthTextField);
		hBoxWidth.getChildren().remove(getWarningIcon());
		hBoxWidth.getChildren().remove(getDeleteButton());

		if (widthChoice != null && "Unspecified".equals(widthChoice))
			validWidth = true;

		else {

			if (widthChoice == null || !Utils.isNumber(widthTextField.getText())) {
				validWidth = false;
				hBoxWidth.getChildren().addAll(widthTextField);
			}

			else {
				validWidth = true;
				hBoxWidth.getChildren().addAll(widthTextField);
			}
		}

		setValid(validHeight && validWidth);

		if (!isValid())
			hBoxHeight.getChildren().add(getWarningIcon());

		hBoxHeight.getChildren().add(getDeleteButton());
	}

	@Override
	protected void initialize() {

		validHeight = false;
		validWidth = false;

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

		GridPane gridPane = new GridPane();

		gridPane.add(hBoxHeight, 0, 0);
		gridPane.add(hBoxWidth, 0, 1);

		checkValidity();
		this.getChildren().add(gridPane);
	}

	@Override
	public void addPropertyExpression(ModelPropertySet modelPropertySet) {
		if (isValid()) {
			int height = "Unspecified".equals(heightTextField.getText()) ? -1 : Integer.decode(heightTextField.getText());
			int width = "Unspecified".equals(widthTextField.getText()) ? -1 : Integer.decode(widthTextField.getText());
			modelPropertySet.getById("rectangle").addExpression(new RectangleExpression("rectangle", heightChoiceBox.getValue(), height, widthChoiceBox.getValue(), width));
		}
	}

}
