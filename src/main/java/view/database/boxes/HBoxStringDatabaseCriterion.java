package view.database.boxes;

import java.util.ArrayList;

import database.BenzenoidCriterion;
import database.BenzenoidCriterion.Subject;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import utils.Utils;
import view.database.ChoiceBoxDatabaseCriterion;
import view.database.DatabasePane;

public class HBoxStringDatabaseCriterion extends HBoxInDatabase {

	public HBoxStringDatabaseCriterion(DatabasePane parent, ChoiceBoxDatabaseCriterion choiceBoxCriterion, Subject subject, String possible_operators) {
		super(parent, choiceBoxCriterion,subject, possible_operators);
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

			criterions.add(new BenzenoidCriterion(getSubject(),
					BenzenoidCriterion.getOperator(operatorChoiceBox.getValue()), value));
			System.out.println(criterions.get(0));
		}

		return criterions;
	}

}
