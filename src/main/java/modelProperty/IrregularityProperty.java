package modelProperty;

import modelProperty.testers.IrregularityTester;
import modules.IrregularityModule;
import modules.Module;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.generator.boxes.HBoxModelCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;
import view.generator.boxes.HBoxIrregularityCriterion;

public class IrregularityProperty extends ModelProperty {

	public IrregularityProperty() {
		super("irregularity", "Irregularity", new IrregularityModule(), new IrregularityTester());
	}

	@Override
	public HBoxModelCriterion getHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxIrregularityCriterion(parent, choiceBoxCriterion);
	}


}
