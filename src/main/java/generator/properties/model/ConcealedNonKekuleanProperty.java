package generator.properties.model;

import generator.properties.model.checkers.ConcealedNonKekuleanChecker;
import generator.properties.model.filters.ConcealedNonKekuleanFilter;
import view.generator.ChoiceBoxCriterion;
import view.generator.boxes.HBoxConcealedCriterion;
import view.generator.boxes.HBoxModelCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class ConcealedNonKekuleanProperty extends ModelProperty {

	public ConcealedNonKekuleanProperty() {
		super("concealed", "Concealed non Kekulean", new ConcealedNonKekuleanChecker(), new ConcealedNonKekuleanFilter());
	}

	@Override
	public HBoxModelCriterion makeHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxConcealedCriterion(parent, choiceBoxCriterion); 
	}

}
