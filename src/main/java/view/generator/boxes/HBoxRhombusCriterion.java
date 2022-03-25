package view.generator.boxes;

import java.util.ArrayList;

import generator.GeneratorCriterion;
import generator.GeneratorCriterion.Operator;
import generator.GeneratorCriterion.Subject;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import utils.Utils;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;

public class HBoxRhombusCriterion extends HBoxCriterion {

	private ChoiceBox<String> dimensionChoiceBox;
	private TextField dimensionTextField;

	public HBoxRhombusCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	protected void checkValidity() {

		String dimensionChoice = dimensionChoiceBox.getValue();

		this.getChildren().remove(dimensionTextField);
		this.getChildren().remove(warningIcon);
		this.getChildren().remove(deleteButton);

		if (dimensionChoice != null && dimensionChoice.contentEquals("Unspecified"))
			valid = true;

		else {

			if (dimensionChoice == null || !Utils.isNumber(dimensionTextField.getText())) {
				valid = false;
				this.getChildren().add(dimensionTextField);
			}

			else {
				valid = true;
				this.getChildren().add(dimensionTextField);
			}
		}

		if (!valid)
			this.getChildren().add(warningIcon);

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
		});

		dimensionTextField = new TextField();

		dimensionTextField.setOnKeyReleased(e -> {
			checkValidity();
		});

		this.getChildren().addAll(dimensionLabel, dimensionChoiceBox, dimensionTextField, warningIcon, deleteButton);
		checkValidity();
	}

	@Override
	public ArrayList<GeneratorCriterion> buildCriterions() {

		ArrayList<GeneratorCriterion> criterions = new ArrayList<>();

		if (valid) {
			criterions.add(new GeneratorCriterion(Subject.RHOMBUS, Operator.NONE, ""));
			if (!dimensionChoiceBox.getValue().equals("Unspecified")) {
				criterions.add(new GeneratorCriterion(Subject.RHOMBUS_DIMENSION,
						GeneratorCriterion.getOperator(dimensionChoiceBox.getValue()), dimensionTextField.getText()));
			}
		}

		return criterions;
	}

}
