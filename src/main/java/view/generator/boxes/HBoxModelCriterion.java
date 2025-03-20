package view.generator.boxes;

import generator.properties.model.ModelPropertySet;
import javafx.scene.control.Tooltip;
import view.generator.ChoiceBoxCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

@SuppressWarnings("unused")
public abstract class HBoxModelCriterion extends HBoxCriterion {


	public HBoxModelCriterion(ScrollPaneWithPropertyList pane, ChoiceBoxCriterion choiceBoxCriterion) {
		super(pane, choiceBoxCriterion);
		Tooltip.install(getDeleteButton(), new Tooltip("Delete criterion"));
		getDeleteButton().setOnAction(e -> pane.removeCriterion(choiceBoxCriterion, this));
	}

	public abstract void addPropertyExpression(ModelPropertySet propertySet);


	protected void addDeleteButton() {
		this.getChildren().add(getDeleteButton());
	}

	protected void removeWarningIconAndDeleteButton() {
		removeWarningIcon();
		this.getChildren().remove(getDeleteButton());
	}

	protected void addWarningIconAndDeleteButton() {
		addWarningIcon();
		addDeleteButton();
	}

}
