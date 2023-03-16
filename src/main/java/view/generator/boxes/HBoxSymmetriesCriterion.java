package view.generator.boxes;

import java.util.ArrayList;

import generator.GeneratorCriterion;
import generator.properties.PropertySet;
import javafx.scene.control.ChoiceBox;
import modelProperty.ModelProperty;
import modelProperty.ModelPropertySet;
import modelProperty.expression.ParameterizedExpression;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.primaryStage.ScrollPaneWithPropertyList;

public class HBoxSymmetriesCriterion extends HBoxModelCriterion {

	private ChoiceBox<String> symmetriesChoiceBox;

	public HBoxSymmetriesCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	protected void checkValidity() {

		this.getChildren().remove(getWarningIcon());
		this.getChildren().remove(getDeleteButton());

		if (symmetriesChoiceBox.getValue() != null) {
			setValid(true);
			this.getChildren().addAll(getDeleteButton());
		}

		else {
			setValid(false);
			this.getChildren().addAll(getWarningIcon(), getDeleteButton());
		}
	}

	@Override
	protected void initialize() {

		setValid(false);

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

		this.getChildren().addAll(symmetriesChoiceBox, getWarningIcon(), getDeleteButton());
		checkValidity();
	}

	@Override
	public void addPropertyExpression(ModelPropertySet modelPropertySet) {

		((ModelProperty) modelPropertySet.getById("symmetry")).addExpression(new ParameterizedExpression("symmetry", symmetriesChoiceBox.getValue()));
//		if (symmetriesChoiceBox.getValue().equals("C_2v(a) \"face-mirror\""))
//			criterions.add(new GeneratorCriterion(Subject.SYMM_MIRROR, Operator.NONE, ""));
//
//		else if (symmetriesChoiceBox.getValue().equals("C_6h \"(face)-60-rotation\""))
//			criterions.add(new GeneratorCriterion(Subject.SYMM_ROT_60, Operator.NONE, ""));
//
//		else if (symmetriesChoiceBox.getValue().equals("C_3h(i) \"face-120-rotation\""))
//			criterions.add(new GeneratorCriterion(Subject.SYMM_ROT_120, Operator.NONE, ""));
//
//		else if (symmetriesChoiceBox.getValue().equals("C_2h(i) \"vertex_180-rotation\""))
//			criterions.add(new GeneratorCriterion(Subject.SYMM_ROT_180, Operator.NONE, ""));
//
//		else if (symmetriesChoiceBox.getValue().equals("C_2v(b) \"edge-mirror\""))
//			criterions.add(new GeneratorCriterion(Subject.SYMM_VERTICAL, Operator.NONE, ""));
//
//		else if (symmetriesChoiceBox.getValue().equals("C_3h(ii) \"vertex-120-rotation\""))
//			criterions.add(new GeneratorCriterion(Subject.SYMM_ROT_120_V, Operator.NONE, ""));
//
//		else if (symmetriesChoiceBox.getValue().equals("C_2h(ii) \"edge-180-rotation\""))
//			criterions.add(new GeneratorCriterion(Subject.SYMM_ROT_180_E, Operator.NONE, ""));
//
//		else if (symmetriesChoiceBox.getValue().equals("D_6h \"(vertex)-60-rotation+(edge)-mirror\""))
//			criterions.add(new GeneratorCriterion(Subject.ROT_60_MIRROR, Operator.NONE, ""));
//
//		else if (symmetriesChoiceBox.getValue().equals("D_3h(ii) \"vertex-120-rotation+(edge)-mirror\""))
//			criterions.add(new GeneratorCriterion(Subject.ROT_120_VERTEX_MIRROR, Operator.NONE, ""));
//
//		else if (symmetriesChoiceBox.getValue().equals("D_3h(ia) \"face-120-rotation+face-mirror\""))
//			criterions.add(new GeneratorCriterion(Subject.ROT_120_MIRROR_H, Operator.NONE, ""));
//
//		else if (symmetriesChoiceBox.getValue().equals("D_3h(ib) \"face-120-rotation+edge-mirror\""))
//			criterions.add(new GeneratorCriterion(Subject.ROT_120_MIRROR_E, Operator.NONE, ""));
//
//		else if (symmetriesChoiceBox.getValue().equals("D_2h(ii) \"edge-180-rotation+edge-mirror\""))
//			criterions.add(new GeneratorCriterion(Subject.ROT_180_EDGE_MIRROR, Operator.NONE, ""));
//
//		else if (symmetriesChoiceBox.getValue().equals("D_2h(i) \"face-180-rotation+edge-mirror\""))
//			criterions.add(new GeneratorCriterion(Subject.ROT_180_MIRROR, Operator.NONE, ""));
	}

}
