package generator.properties.model;

import constraints.CatacondensedConstraint;
import generator.properties.model.filters.CatacondensedFilter;
import view.generator.ChoiceBoxCriterion;
import view.generator.boxes.HBoxCatacondensedCriterion;
import view.generator.boxes.HBoxModelCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class CatacondensedProperty extends ModelProperty {

	public CatacondensedProperty() {
		super("catacondensed", "Catacondensed", new CatacondensedConstraint(), new CatacondensedFilter());
	}

	@Override
	public HBoxModelCriterion makeHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxCatacondensedCriterion(parent, choiceBoxCriterion);
	}
}
