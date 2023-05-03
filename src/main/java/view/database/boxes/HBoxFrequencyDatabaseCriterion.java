package view.database.boxes;

import java.util.ArrayList;

import database.BenzenoidCriterion;
import database.BenzenoidCriterion.Subject;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import utils.Utils;
import view.database.ChoiceBoxDatabaseCriterion;
import view.database.DatabasePane;

public class HBoxFrequencyDatabaseCriterion extends HBoxInDatabase {

	private ChoiceBox<String> formatChoiceBox;

	public HBoxFrequencyDatabaseCriterion(DatabasePane parent, ChoiceBoxDatabaseCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	protected void checkValidity() {
		if ("IN".equals(operatorChoiceBox.getSelectionModel().getSelectedItem())) {
			this.getChildren().remove(warningIcon);
			this.getChildren().remove(deleteButton);
			this.getChildren().remove(fieldValue2);
			this.getChildren().add(fieldValue2);
			this.getChildren().remove(formatChoiceBox);
			this.getChildren().add(formatChoiceBox);

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
		operatorChoiceBox.getItems().addAll("<=", "<", "=", ">", ">=", "!=", "IN");

		formatChoiceBox = new ChoiceBox<String>();
		formatChoiceBox.getItems().addAll("wave number", "micron");
		formatChoiceBox.getSelectionModel().select(0);

		fieldValue1 = new TextField();
		fieldValue2 = new TextField();

		operatorChoiceBox.getSelectionModel().select(2);

		fieldValue1.setOnKeyReleased(e -> {
			checkValidity();
		});

		fieldValue2.setOnKeyReleased(e -> {
			checkValidity();
		});

		operatorChoiceBox.setOnAction(e -> {
			checkValidity();
		});

		formatChoiceBox.setOnAction(e -> {
			checkValidity();
		});

		this.getChildren().addAll(operatorChoiceBox, fieldValue1, formatChoiceBox, warningIcon, deleteButton);

	}

	@Override
	public ArrayList<BenzenoidCriterion> buildCriterions() {
		ArrayList<BenzenoidCriterion> criterions = new ArrayList<>();

		if (valid) {
			String value = fieldValue1.getText() + " " + fieldValue2.getText();
			criterions.add(new BenzenoidCriterion(Subject.FREQUENCY,
					BenzenoidCriterion.getOperator(operatorChoiceBox.getValue()), value));
			System.out.println(criterions.get(0));
		}

		return criterions;
	}

}
