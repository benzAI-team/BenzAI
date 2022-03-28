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

		symmetriesChoiceBox.getItems().add("C_2v(a) \"face-mirror\"");
		symmetriesChoiceBox.getItems().add("C_6h \"(face)-60-rotation\"");
		symmetriesChoiceBox.getItems().add("C_3h(i) \"face-120-rotation\"");
		symmetriesChoiceBox.getItems().add("C_2h(i) \"vertex_180-rotation\"");
		symmetriesChoiceBox.getItems().add("C_2v(b) \"edge-mirror\"");
		symmetriesChoiceBox.getItems().add("C_3h(ii) \"vertex-120-rotation\"");
		symmetriesChoiceBox.getItems().add("C_2h(ii) \"edge-180-rotation\"");

		symmetriesChoiceBox.getItems().add("D_6h \"(vertex)-60-rotation+(edge)-mirror\"");
		symmetriesChoiceBox.getItems().add("D_3h(ii) \"vertex-120-rotation+(edge)-mirror\"");
		symmetriesChoiceBox.getItems().add("D_3h(ia) \"face-120-rotation+face-mirror\"");
		symmetriesChoiceBox.getItems().add("D_3h(ib) \"face-120-rotation+edge-mirror\"");
		symmetriesChoiceBox.getItems().add("D_2h(ii) \"edge-180-rotation+edge-mirror\"");
		symmetriesChoiceBox.getItems().add("D_2h(i) \"face-180-rotation+edge-mirror\"");

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
