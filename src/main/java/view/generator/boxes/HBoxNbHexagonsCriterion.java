package view.generator.boxes;

import java.util.ArrayList;
import generator.GeneratorCriterion;
import generator.GeneratorCriterion.Operator;
import generator.GeneratorCriterion.Subject;
import utils.Utils;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;

public class HBoxNbHexagonsCriterion extends ClassicalHBoxCriterion {
	
	public HBoxNbHexagonsCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	protected void checkValidity() {
		
		if (! Utils.isNumber(fieldValue.getText()) || operatorChoiceBox.getValue() == null) {
			valid = false;
			this.getChildren().remove(warningIcon);
			this.getChildren().remove(deleteButton);
			this.getChildren().addAll(warningIcon, deleteButton);
		}
		
		else {
			valid = true;
			this.getChildren().remove(warningIcon);
			this.getChildren().remove(deleteButton);
			this.getChildren().add(deleteButton);
		}
		
		parent.refreshGenerationPossibility();
	}
	
	@Override
	public ArrayList<GeneratorCriterion> buildCriterions() {
		
		ArrayList<GeneratorCriterion> criterions = new ArrayList<>();
		
		if (valid) {
			Subject subject = Subject.NB_HEXAGONS;
			Operator operator = GeneratorCriterion.getOperator(operatorChoiceBox.getValue());
			String value = fieldValue.getText();
			
			criterions.add(new GeneratorCriterion(subject, operator, value));
		}
		
		return criterions;
	}

}
