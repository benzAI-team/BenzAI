package modelProperty;

import modules.IrregularityModule;
import modules.Module;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.generator.boxes.HBoxCriterion;
import view.generator.boxes.HBoxIrregularityCriterion;

public class IrregularityProperty extends ModelProperty {

	public IrregularityProperty() {
		super("irregularity", new IrregularityModule());
	}

	@Override
	public HBoxCriterion getHBoxCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxIrregularityCriterion(parent, choiceBoxCriterion);
	}


}
