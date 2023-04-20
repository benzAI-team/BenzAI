package generator.properties.model;

import constraints.CatacondensedConstraint2;
import generator.properties.model.filters.CatacondensedFilter;
import constraints.CatacondensedConstraint;
import view.generator.ChoiceBoxCriterion;
import view.generator.boxes.HBoxCatacondensedCriterion;
import view.generator.boxes.HBoxModelCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class CatacondensedProperty extends ModelProperty {

	public CatacondensedProperty() {
		super("catacondensed", "Catacondensed", new CatacondensedConstraint2(), new CatacondensedFilter());
	}

	@Override
	public HBoxModelCriterion getHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxCatacondensedCriterion(parent, choiceBoxCriterion);
	}
}
