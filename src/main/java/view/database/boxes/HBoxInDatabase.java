package view.database.boxes;

import database.BenzenoidCriterion.Subject;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import utils.Utils;
import view.database.ChoiceBoxDatabaseCriterion;
import view.database.DatabasePane;

public abstract class HBoxInDatabase extends HBoxDatabaseCriterion {

	protected ChoiceBox<String> operatorChoiceBox;
	protected TextField fieldValue1;
	protected TextField fieldValue2;

	public HBoxInDatabase(DatabasePane parent, ChoiceBoxDatabaseCriterion choiceBoxCriterion, Subject subject, String possible_operators) {
		super(parent, choiceBoxCriterion, subject, possible_operators);
		checkValidity();
	}

	@Override
	protected void checkValidity() {
		if ("IN".equals(operatorChoiceBox.getSelectionModel().getSelectedItem())) {
			this.getChildren().remove(warningIcon);
			this.getChildren().remove(deleteButton);
			this.getChildren().remove(fieldValue2);
			this.getChildren().add(fieldValue2);

			if (!Utils.isNumber(fieldValue1.getText()) || !Utils.isNumber(fieldValue2.getText())
					|| operatorChoiceBox.getValue() == null) {

				valid = false;
				this.getChildren().addAll(warningIcon, deleteButton);
			}

			else {
				valid = true;
				this.getChildren().add(deleteButton);
			}
		}
		else {

			this.getChildren().remove(fieldValue2);

			if (!Utils.isNumber(fieldValue1.getText()) || operatorChoiceBox.getValue() == null) {
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
	protected void initialize() {
		valid = false;

		operatorChoiceBox = new ChoiceBox<String>();
		for (String op : get_possible_operators()) {
		  operatorChoiceBox.getItems().add(op);
		}

		fieldValue1 = new TextField();
		fieldValue2 = new TextField();

		operatorChoiceBox.getSelectionModel().select(0);

		fieldValue1.setOnKeyReleased(e -> {
			checkValidity();
		});

		fieldValue2.setOnKeyReleased(e -> {
			checkValidity();
		});

		operatorChoiceBox.setOnAction(e -> {
			checkValidity();
		});

		this.getChildren().addAll(operatorChoiceBox, fieldValue1, warningIcon, deleteButton);

	}

}
