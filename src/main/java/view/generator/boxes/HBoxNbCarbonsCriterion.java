package view.generator.boxes;

import java.util.ArrayList;
import generator.GeneratorCriterion;
import generator.GeneratorCriterion.Operator;
import generator.GeneratorCriterion.Subject;
import utils.Utils;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;

public class HBoxNbCarbonsCriterion extends ClassicalHBoxCriterion{

	public HBoxNbCarbonsCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
		operatorChoiceBox.getItems().addAll("EVEN", "ODD");
	}

	@Override
	public void checkValidity() {
		
		if (operatorChoiceBox.getValue().equals("EVEN") || operatorChoiceBox.getValue().equals("ODD")) {
			valid = true;
			this.getChildren().remove(fieldValue);
			this.getChildren().remove(warningIcon);
			this.getChildren().remove(deleteButton);
			this.getChildren().add(deleteButton);
		}
		
		else if (! Utils.isNumber(fieldValue.getText()) || operatorChoiceBox.getValue() == null) {
			valid = false;
			this.getChildren().remove(warningIcon);
			this.getChildren().remove(deleteButton);
			this.getChildren().remove(fieldValue);
			this.getChildren().addAll(fieldValue, warningIcon, deleteButton);
		}
		
		else {
			valid = true;
			this.getChildren().remove(warningIcon);
			this.getChildren().remove(deleteButton);
			this.getChildren().remove(fieldValue);
			this.getChildren().addAll(fieldValue, deleteButton);
		}
		
		parent.refreshGenerationPossibility();
		
	}
	
	@Override
	public ArrayList<GeneratorCriterion> buildCriterions() {
		
		ArrayList<GeneratorCriterion> criterions = new ArrayList<>();
		
		if (valid) {
			Subject subject = Subject.NB_CARBONS;
			Operator operator = GeneratorCriterion.getOperator(operatorChoiceBox.getValue());
			
			if (operator != Operator.EVEN && operator != Operator.ODD) {
				String value = fieldValue.getText();
				criterions.add(new GeneratorCriterion(subject, operator, value));
			}
			
			else {
				String value = "";
				criterions.add(new GeneratorCriterion(subject, operator, value));
			}
		}
		
		return criterions;
	}

}
