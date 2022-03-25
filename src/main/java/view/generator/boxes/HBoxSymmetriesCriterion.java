package view.generator.boxes;

import java.util.ArrayList;

import generator.GeneratorCriterion;
import generator.GeneratorCriterion.Operator;
import generator.GeneratorCriterion.Subject;
import javafx.scene.control.ChoiceBox;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;

public class HBoxSymmetriesCriterion extends HBoxCriterion {

	private ChoiceBox<String> symmetriesChoiceBox;

	public HBoxSymmetriesCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion) {
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

		symmetriesChoiceBox.getItems().add("face-mirror");
		symmetriesChoiceBox.getItems().add("(face)-60-rotation");
		symmetriesChoiceBox.getItems().add("face-120-rotation");
		symmetriesChoiceBox.getItems().add("Rotation of 180°");
		symmetriesChoiceBox.getItems().add("edge-mirror");
		symmetriesChoiceBox.getItems().add("vertex-120-rotation");
		symmetriesChoiceBox.getItems().add("edge-180-rotation");

		symmetriesChoiceBox.getItems().add("(vertex)-60-rotation+(edge)-mirror");
		symmetriesChoiceBox.getItems().add("vertex-120-rotation+(edge)-mirror");
		symmetriesChoiceBox.getItems().add("face-120-rotation+face-mirror");
		symmetriesChoiceBox.getItems().add("face-120-rotation+edge-mirror");
		symmetriesChoiceBox.getItems().add("edge-180-rotation+edge-mirror");
		symmetriesChoiceBox.getItems().add("face-180-rotation+edge-mirror");

		symmetriesChoiceBox.getSelectionModel().selectFirst();

		symmetriesChoiceBox.setOnAction(e -> {
			checkValidity();
		});

		this.getChildren().addAll(symmetriesChoiceBox, warningIcon, deleteButton);
		checkValidity();
	}

	@Override
	public ArrayList<GeneratorCriterion> buildCriterions() {

		ArrayList<GeneratorCriterion> criterions = new ArrayList<>();

		if (symmetriesChoiceBox.getValue().equals("face-mirror"))
			criterions.add(new GeneratorCriterion(Subject.SYMM_MIRROR, Operator.NONE, ""));

		else if (symmetriesChoiceBox.getValue().equals("(face)-60-rotation"))
			criterions.add(new GeneratorCriterion(Subject.SYMM_ROT_60, Operator.NONE, ""));

		else if (symmetriesChoiceBox.getValue().equals("face-120-rotation"))
			criterions.add(new GeneratorCriterion(Subject.SYMM_ROT_120, Operator.NONE, ""));

		else if (symmetriesChoiceBox.getValue().equals("vertex-180-rotation"))
			criterions.add(new GeneratorCriterion(Subject.SYMM_ROT_180, Operator.NONE, ""));

		else if (symmetriesChoiceBox.getValue().equals("edge-mirror"))
			criterions.add(new GeneratorCriterion(Subject.SYMM_VERTICAL, Operator.NONE, ""));

		else if (symmetriesChoiceBox.getValue().equals("vertex-120-rotation"))
			criterions.add(new GeneratorCriterion(Subject.SYMM_ROT_120_V, Operator.NONE, ""));

		else if (symmetriesChoiceBox.getValue().equals("edge-180-rotation"))
			criterions.add(new GeneratorCriterion(Subject.SYMM_ROT_180_E, Operator.NONE, ""));

		else if (symmetriesChoiceBox.getValue().equals("(vertex)-60-rotation+(edge)-mirror"))
			criterions.add(new GeneratorCriterion(Subject.SYMM_ROT_60, Operator.NONE, ""));

		else if (symmetriesChoiceBox.getValue().equals("vertex-120-rotation+(edge)-mirror"))
			criterions.add(new GeneratorCriterion(Subject.ROT_120_VERTEX_MIRROR, Operator.NONE, ""));

		else if (symmetriesChoiceBox.getValue().equals("face-120-rotation+face-mirror"))
			criterions.add(new GeneratorCriterion(Subject.ROT_120_MIRROR_H, Operator.NONE, ""));

		else if (symmetriesChoiceBox.getValue().equals("face-120-rotation+edge-mirror"))
			criterions.add(new GeneratorCriterion(Subject.ROT_120_MIRROR_E, Operator.NONE, ""));

		else if (symmetriesChoiceBox.getValue().equals("edge-180-rotation+edge-mirror"))
			criterions.add(new GeneratorCriterion(Subject.ROT_180_EDGE_MIRROR, Operator.NONE, ""));

		else if (symmetriesChoiceBox.getValue().equals("face-180-rotation+edge-mirror"))
			criterions.add(new GeneratorCriterion(Subject.ROT_180_MIRROR, Operator.NONE, ""));

		return criterions;
	}

}
