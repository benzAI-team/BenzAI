package modelProperty;

import modelProperty.checkers.Checker;
import modelProperty.checkers.ConcealedNonKekuleanChecker;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.generator.boxes.HBoxConcealedCriterion;
import view.generator.boxes.HBoxModelCriterion;

public class ConcealedNonKekuleanProperty extends ModelProperty {

	public ConcealedNonKekuleanProperty() {
		super("concealed", "Concealed non Kekulean", new ConcealedNonKekuleanChecker());
	}

	@Override
	public HBoxModelCriterion getHBoxCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxConcealedCriterion(parent, choiceBoxCriterion); 
	}

}
