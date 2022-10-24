package view.generator.boxes;

import generator.properties.Property;
import generator.properties.PropertySet;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import modelProperty.ModelProperty;
import modelProperty.ModelPropertySet;
import modelProperty.expression.PropertyExpression;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;

@SuppressWarnings("unused")
public abstract class HBoxModelCriterion extends HBoxCriterion {
	
	public HBoxModelCriterion(GeneratorPane generatorPane, ChoiceBoxCriterion choiceBoxCriterion) {
		super(generatorPane, choiceBoxCriterion);
	}
	
	public abstract void addPropertyExpression(ModelPropertySet propertySet);

}
