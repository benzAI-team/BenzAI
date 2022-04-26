package view.generator.boxes;

import java.util.ArrayList;

import generator.GeneratorCriterion;
import generator.GeneratorCriterion.Operator;
import generator.GeneratorCriterion.Subject;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import utils.Utils;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;

public class HBoxRectangleCriterion extends HBoxCriterion {

	private GridPane gridPane;

	protected ChoiceBox<String> heightChoiceBox;
	protected TextField heightTextField;

	protected ChoiceBox<String> widthChoiceBox;
	protected TextField widthTextField;

	private HBox hBoxHeight;
	private HBox hBoxWidth;

	protected boolean valid1;
	protected boolean valid2;

	public HBoxRectangleCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion) {
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
		hBoxHeight.getChildren().remove(warningIcon);
		hBoxHeight.getChildren().remove(deleteButton);

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
		hBoxWidth.getChildren().remove(warningIcon);
		hBoxWidth.getChildren().remove(deleteButton);

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

		valid = valid1 && valid2;

		if (!valid)
			hBoxHeight.getChildren().add(warningIcon);

		hBoxHeight.getChildren().add(deleteButton);
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
			parent.refreshValidity();
		});

		heightTextField.setOnKeyReleased(e -> {
			checkValidity();
			parent.refreshValidity();
		});

		hBoxHeight = new HBox(5.0);
		hBoxHeight.getChildren().addAll(heightLabel, heightChoiceBox, heightTextField, warningIcon, deleteButton);

		Label widthLabel = new Label("Width: ");
		widthChoiceBox = new ChoiceBox<>();
		widthChoiceBox.getItems().addAll("Unspecified", "<=", "<", "=", ">", ">=");
		widthChoiceBox.getSelectionModel().selectFirst();
		widthTextField = new TextField();

		widthChoiceBox.setOnAction(e -> {
			checkValidity();
			parent.refreshValidity();
		});

		widthTextField.setOnKeyReleased(e -> {
			checkValidity();
			parent.refreshValidity();
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
	public ArrayList<GeneratorCriterion> buildCriterions() {

		ArrayList<GeneratorCriterion> criterions = new ArrayList<>();

		if (valid) {

			criterions.add(new GeneratorCriterion(Subject.RECTANGLE, Operator.NONE, ""));

			if (!heightChoiceBox.getValue().equals("Unspecified"))
				criterions.add(new GeneratorCriterion(Subject.RECT_HEIGHT,
						GeneratorCriterion.getOperator(heightChoiceBox.getValue()), heightTextField.getText()));

			if (!widthChoiceBox.getValue().equals("Unspecified"))
				criterions.add(new GeneratorCriterion(Subject.RECT_WIDTH,
						GeneratorCriterion.getOperator(widthChoiceBox.getValue()), widthTextField.getText()));
		}

		return criterions;
	}

}
