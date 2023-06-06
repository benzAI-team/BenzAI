package generator.properties.model;

import constraints.RhombusConstraint;
import generator.properties.model.filters.RhombusFilter;
import view.generator.ChoiceBoxCriterion;
import view.generator.boxes.HBoxModelCriterion;
import view.generator.boxes.HBoxRhombusCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class RhombusProperty extends RectangleProperty {

	RhombusProperty() {
		super("rhombus", "Rhombus", new RhombusConstraint(), new RhombusFilter());
	}

	@Override
	public HBoxModelCriterion getHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxRhombusCriterion(parent, choiceBoxCriterion);
	}
}
