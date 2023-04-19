package view.generator.boxes;

import javafx.scene.control.ChoiceBox;
import generator.properties.model.ModelProperty;
import generator.properties.model.ModelPropertySet;
import generator.properties.model.expression.ParameterizedExpression;
import view.generator.ChoiceBoxCriterion;
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
		symmetriesChoiceBox.getItems().add("C_2h(i) \"face-180-rotation\"");
		symmetriesChoiceBox.getItems().add("C_2v(b) \"edge-mirror\"");
		symmetriesChoiceBox.getItems().add("C_3h(ii) \"vertex-120-rotation\"");
		symmetriesChoiceBox.getItems().add("C_2h(ii) \"edge-180-rotation\"");

		symmetriesChoiceBox.getItems().add("D_6h \"(face)-60-rotation+(edge)-mirror\"");
		symmetriesChoiceBox.getItems().add("D_3h(ii) \"vertex-120-rotation+(edge)-mirror\"");
		symmetriesChoiceBox.getItems().add("D_3h(ia) \"face-120-rotation+face-mirror\"");
		symmetriesChoiceBox.getItems().add("D_3h(ib) \"face-120-rotation+edge-mirror\"");
		symmetriesChoiceBox.getItems().add("D_2h(ii) \"edge-180-rotation+edge-mirror\"");
		symmetriesChoiceBox.getItems().add("D_2h(i) \"face-180-rotation+edge-mirror\"");
		symmetriesChoiceBox.getItems().add("C_s \"no-symmetry\"");

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
	}

}
