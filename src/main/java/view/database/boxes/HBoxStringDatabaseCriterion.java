package view.database.boxes;

import database.BenzenoidCriterion;
import view.database.ChoiceBoxDatabaseCriterion;
import view.database.DatabasePane;

import java.util.ArrayList;

public class HBoxStringDatabaseCriterion extends HBoxInDatabase {

	public HBoxStringDatabaseCriterion(DatabasePane parent, ChoiceBoxDatabaseCriterion choiceBoxCriterion, String name, String possible_operators) {
		super(parent, choiceBoxCriterion, name, possible_operators);
	}

	@Override
	protected void checkValidity() {
		if ("IN".equals(operatorChoiceBox.getSelectionModel().getSelectedItem())) {
			valid = false;
		}
		else {

			this.getChildren().remove(fieldValue2);

			if (fieldValue1.getText().length() == 0 || operatorChoiceBox.getValue() == null) {
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
		}
	}


	@Override
	public ArrayList<BenzenoidCriterion> buildCriterions() {
		ArrayList<BenzenoidCriterion> criterions = new ArrayList<>();

		if (valid) {

			String value = fieldValue1.getText();
			if ("IN".equals(operatorChoiceBox.getSelectionModel().getSelectedItem()))
				value = value + " " + fieldValue2.getText();

			criterions.add(new BenzenoidCriterion(getName(),
					BenzenoidCriterion.getOperator(operatorChoiceBox.getValue()), value));
			System.out.println(criterions.get(0));
		}

		return criterions;
	}

}
