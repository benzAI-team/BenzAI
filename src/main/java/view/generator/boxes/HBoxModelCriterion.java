package view.generator.boxes;

import generator.properties.Property;
import generator.properties.PropertySet;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import modelProperty.ModelProperty;
import modelProperty.ModelPropertySet;
import modelProperty.expression.PropertyExpression;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.primaryStage.ScrollPaneWithPropertyList;

@SuppressWarnings("unused")
public abstract class HBoxModelCriterion extends HBoxCriterion {
	
	public HBoxModelCriterion(ScrollPaneWithPropertyList pane, ChoiceBoxCriterion choiceBoxCriterion) {
		super(pane, choiceBoxCriterion);
	}
	
	public abstract void addPropertyExpression(ModelPropertySet propertySet);

}
