package modelProperty;

import modules.IrregularityModule;
import modules.Module;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.generator.boxes.HBoxModelCriterion;
import view.generator.boxes.HBoxIrregularityCriterion;

public class IrregularityProperty extends ModelProperty {

	public IrregularityProperty() {
		super("irregularity", "Irregularity", new IrregularityModule());
	}

	@Override
	public HBoxModelCriterion getHBoxCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxIrregularityCriterion(parent, choiceBoxCriterion);
	}


}
