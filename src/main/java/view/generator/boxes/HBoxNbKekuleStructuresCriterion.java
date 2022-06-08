package view.generator.boxes;

import java.util.ArrayList;

import generator.GeneratorCriterion;
import generator.GeneratorCriterion.Operator;
import generator.GeneratorCriterion.Subject;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import utils.Utils;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;

public class HBoxNbKekuleStructuresCriterion extends ClassicalHBoxCriterion {

	
	public HBoxNbKekuleStructuresCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
		operatorChoiceBox.getItems().addAll("Min", "Max");
	}

	@Override
	protected void checkValidity() {
		
		if (operatorChoiceBox.getValue().equals("Min") || operatorChoiceBox.getValue().equals("Max")) {
			//System.out.println("1");
			valid = true;
			this.getChildren().remove(fieldValue);
			this.getChildren().remove(warningIcon);
			this.getChildren().remove(deleteButton);
			this.getChildren().add(deleteButton);
		}
		
		else if (!Utils.isNumber(fieldValue.getText()) || operatorChoiceBox.getValue() == null) {
			//System.out.println("2");
			valid = false;
			this.getChildren().remove(fieldValue);
			this.getChildren().remove(warningIcon);
			this.getChildren().remove(deleteButton);
			this.getChildren().addAll(fieldValue, warningIcon, deleteButton);
		}

		else {
			//System.out.println("3");
			valid = true;
			this.getChildren().remove(warningIcon);
			this.getChildren().remove(deleteButton);
			this.getChildren().addAll(fieldValue, deleteButton);
		}
		
	}

	@Override
	public ArrayList<GeneratorCriterion> buildCriterions() {
		ArrayList<GeneratorCriterion> criterions = new ArrayList<>();
		
		if (valid) {
			
			Subject subject = Subject.NB_KEKULE_STRUCTURES;
			Operator operator = GeneratorCriterion.getOperator(operatorChoiceBox.getValue());
			
			if (operator != Operator.MIN && operator != Operator.MAX) {
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
