package modelProperty;

import modelProperty.expression.BinaryNumericalExpression;
import modelProperty.expression.PropertyExpression;
import modules.CoronoidModule2;
import modules.Module;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.generator.boxes.HBoxCoronoidCriterion;
import view.generator.boxes.HBoxCriterion;

public class CoronoidProperty extends ModelProperty {

	public CoronoidProperty() {
		super("coronoid", "Coronoid", new CoronoidModule2());
	}

	@Override
	public HBoxCriterion getHBoxCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxCoronoidCriterion(parent, choiceBoxCriterion) ;
	}

}
