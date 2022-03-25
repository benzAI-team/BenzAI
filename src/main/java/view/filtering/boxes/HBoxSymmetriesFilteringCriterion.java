package view.filtering.boxes;

import java.util.ArrayList;

import javafx.scene.control.ChoiceBox;
import view.filtering.ChoiceBoxFilteringCriterion;
import view.filtering.FilteringPane;
import view.filtering.criterions.FilteringCriterion;
import view.filtering.criterions.SymmetriesCriterion;

public class HBoxSymmetriesFilteringCriterion extends HBoxFilteringCriterion {

	private ChoiceBox<String> symmetriesChoiceBox;

	public HBoxSymmetriesFilteringCriterion(FilteringPane parent, ChoiceBoxFilteringCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	protected void checkValidity() {
		this.getChildren().remove(warningIcon);
		this.getChildren().remove(deleteButton);

		if (symmetriesChoiceBox.getValue() != null) {
			valid = true;
			this.getChildren().addAll(deleteButton);
		}

		else {
			valid = false;
			this.getChildren().addAll(warningIcon, deleteButton);
		}
	}

	@Override
	protected void initialize() {
		valid = false;

		symmetriesChoiceBox = new ChoiceBox<>();

		symmetriesChoiceBox.getItems().add("Mirror symmetry");
		symmetriesChoiceBox.getItems().add("Rotation of 60°");
		symmetriesChoiceBox.getItems().add("Rotation of 120°");
		symmetriesChoiceBox.getItems().add("Rotation of 180°");
		symmetriesChoiceBox.getItems().add("Vertical symmetry");
		symmetriesChoiceBox.getItems().add("Rotation of 120° (vertex)");
		symmetriesChoiceBox.getItems().add("Rotation of 180° (edges)");

		symmetriesChoiceBox.getItems().add("Rotation of 60° + Mirror");
		symmetriesChoiceBox.getItems().add("Rotation of 120° (V) + Mirror");
		symmetriesChoiceBox.getItems().add("Rotation of 120° + Mirror (H)");
		symmetriesChoiceBox.getItems().add("Rotation of 120° + Mirror (E)");
		symmetriesChoiceBox.getItems().add("Rotation of 180° + Mirror (E)");
		symmetriesChoiceBox.getItems().add("Rotation of 180° + Mirror");

		symmetriesChoiceBox.getSelectionModel().selectFirst();

		symmetriesChoiceBox.setOnAction(e -> {
			checkValidity();
		});

		this.getChildren().addAll(symmetriesChoiceBox, warningIcon, deleteButton);
		checkValidity();
	}

	@Override
	public ArrayList<FilteringCriterion> buildCriterions() {
		ArrayList<FilteringCriterion> criterions = new ArrayList<>();

		if (valid) {
			criterions.add(new SymmetriesCriterion(symmetriesChoiceBox.getValue()));
		}

		return criterions;
	}

}
