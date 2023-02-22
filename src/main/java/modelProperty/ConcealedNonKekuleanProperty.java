package modelProperty;

import modelProperty.checkers.Checker;
import modelProperty.checkers.ConcealedNonKekuleanChecker;
import modelProperty.testers.ConcealedNonKekuleanTester;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.generator.boxes.HBoxConcealedCriterion;
import view.generator.boxes.HBoxModelCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class ConcealedNonKekuleanProperty extends ModelProperty {

	public ConcealedNonKekuleanProperty() {
		super("concealed", "Concealed non Kekulean", new ConcealedNonKekuleanChecker(), new ConcealedNonKekuleanTester());
	}

	@Override
	public HBoxModelCriterion getHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxConcealedCriterion(parent, choiceBoxCriterion); 
	}

}
