package view.filtering.boxes;

import java.util.ArrayList;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import utils.Utils;
import view.filtering.ChoiceBoxFilteringCriterion;
import view.filtering.FilteringPane;
import view.filtering.criterions.FilteringCriterion;
import view.filtering.criterions.RhombusCriterion;

public class HBoxRhombusFilteringCriterion extends HBoxFilteringCriterion {

	private ChoiceBox<String> dimensionChoiceBox;
	private TextField dimensionTextField;

	public HBoxRhombusFilteringCriterion(FilteringPane parent, ChoiceBoxFilteringCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	public ArrayList<FilteringCriterion> buildCriterions() {

		ArrayList<FilteringCriterion> criterions = new ArrayList<>();

		if (valid) {

			if (dimensionChoiceBox.getValue().equals("Unspecified"))
				criterions.add(new RhombusCriterion());

			else {
				String operatorDimension = dimensionChoiceBox.getValue();
				int dimension = Integer.parseInt(dimensionTextField.getText());
				criterions.add(new RhombusCriterion(operatorDimension, dimension));
			}

		}

		return criterions;
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

}
