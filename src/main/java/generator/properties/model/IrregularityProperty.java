package generator.properties.model;

import generator.properties.model.filters.IrregularityFilter;
import constraints.IrregularityConstraint;
import view.generator.ChoiceBoxCriterion;
import view.generator.boxes.HBoxModelCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;
import view.generator.boxes.HBoxIrregularityCriterion;

public class IrregularityProperty extends ModelProperty {

	public IrregularityProperty() {
		super("irregularity", "Irregularity", new IrregularityConstraint(), new IrregularityFilter());
	}

	@Override
	public HBoxModelCriterion getHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxIrregularityCriterion(parent, choiceBoxCriterion);
	}


}
