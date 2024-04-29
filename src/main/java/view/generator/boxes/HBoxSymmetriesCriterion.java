package view.generator.boxes;

import properties.PropertySet;
import properties.expression.PropertyExpression;
import javafx.scene.control.ChoiceBox;
import properties.expression.ParameterizedExpression;
import view.generator.ChoiceBoxCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class HBoxSymmetriesCriterion extends HBoxModelCriterion {
	private ChoiceBox<String> symmetriesChoiceBox;

	public HBoxSymmetriesCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	public void updateValidity() {
		removeWarningIconAndDeleteButton();

		if (symmetriesChoiceBox.getValue() != null) {
			setValid(true);
			addDeleteButton();
		}
		else {
			setValid(false);
			addWarningIconAndDeleteButton();
		}
		getPane().refreshGlobalValidity();
	}

	@Override
	protected void initialize() {
		setValid(false);
		symmetriesChoiceBox = new ChoiceBox<>();
		symmetriesChoiceBox.getItems().add("C_2v(a)=\"face-mirror\"");
		symmetriesChoiceBox.getItems().add("C_6h=\"(face)-60-rotation\"");
		symmetriesChoiceBox.getItems().add("C_3h(i)=\"face-120-rotation\"");
		symmetriesChoiceBox.getItems().add("C_2h(i)=\"face-180-rotation\"");
		symmetriesChoiceBox.getItems().add("C_2v(b)=\"edge-mirror\"");
		symmetriesChoiceBox.getItems().add("C_3h(ii)=\"vertex-120-rotation\"");
		symmetriesChoiceBox.getItems().add("C_2h(ii)=\"edge-180-rotation\"");

		symmetriesChoiceBox.getItems().add("D_6h=\"(face)-60-rotation+(edge)-mirror\"");
		symmetriesChoiceBox.getItems().add("D_3h(ii)=\"vertex-120-rotation+(edge)-mirror\"");
		symmetriesChoiceBox.getItems().add("D_3h(ia)=\"face-120-rotation+face-mirror\"");
		symmetriesChoiceBox.getItems().add("D_3h(ib)=\"face-120-rotation+edge-mirror\"");
		symmetriesChoiceBox.getItems().add("D_2h(ii)=\"edge-180-rotation+edge-mirror\"");
		symmetriesChoiceBox.getItems().add("D_2h(i)=\"face-180-rotation+edge-mirror\"");
		symmetriesChoiceBox.getItems().add("C_s=\"no-symmetry\"");

		symmetriesChoiceBox.getSelectionModel().selectFirst();

		this.getChildren().addAll(symmetriesChoiceBox, getWarningIcon(), getDeleteButton());
		updateValidity();
	}

	@Override
	public void assign(PropertyExpression propertyExpression) {
		symmetriesChoiceBox.getSelectionModel().select(((ParameterizedExpression)propertyExpression).getOperator());
	}

	@Override
	public void initEventHandling() {
		symmetriesChoiceBox.setOnAction(e -> updateValidity());
	}

	@Override
	public void addPropertyExpression(PropertySet modelPropertySet) {
		modelPropertySet.getById("symmetry").addExpression(new ParameterizedExpression("symmetry", symmetriesChoiceBox.getValue()));
	}

}
