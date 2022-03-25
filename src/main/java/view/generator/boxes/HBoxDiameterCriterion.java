package view.generator.boxes;

import java.util.ArrayList;

import generator.GeneratorCriterion;
import generator.GeneratorCriterion.Operator;
import generator.GeneratorCriterion.Subject;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;

public class HBoxDiameterCriterion extends ClassicalHBoxCriterion{

	public HBoxDiameterCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	public ArrayList<GeneratorCriterion> buildCriterions() {
		
		ArrayList<GeneratorCriterion> criterions = new ArrayList<>();
		
		if (valid) {
			Subject subject = Subject.DIAMETER;
			Operator operator = GeneratorCriterion.getOperator(operatorChoiceBox.getValue());
			String value = fieldValue.getText();
			
			criterions.add(new GeneratorCriterion(subject, operator, value));
		}
		
		return criterions;
	}

}
