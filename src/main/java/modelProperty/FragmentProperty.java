package modelProperty;

import modules.FragmentModule;
import modules.Module;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.generator.boxes.HBoxCriterion;

public class FragmentProperty extends ModelProperty {

	public FragmentProperty() {
		super("fragment", new FragmentModule());
	}

	@Override
	public HBoxCriterion getHBoxCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion) {
		// TODO Auto-generated method stub
		return null;
	}

}
