package modelProperty;

import modules.CatacondensedModule;
import modules.Module;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.generator.boxes.HBoxCatacondensedCriterion;
import view.generator.boxes.HBoxCriterion;

public class CatacondensedProperty extends ModelProperty {

	public CatacondensedProperty() {
		super("catacondensed", "Catacondensed", new CatacondensedModule());
	}

	@Override
	public HBoxCriterion getHBoxCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxCatacondensedCriterion(parent, choiceBoxCriterion);
	}

	@Override
	public int computeHexagonNumberUpperBound() {
		return Integer.MAX_VALUE;
	}

}
