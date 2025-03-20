package view.database.boxes;

import java.util.ArrayList;

import database.BenzenoidCriterion;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import utils.Utils;
import view.database.ChoiceBoxDatabaseCriterion;
import view.database.DatabasePane;

public class HBoxClassicDatabaseCriterion extends HBoxDatabaseCriterion {

	protected ChoiceBox<String> operatorChoiceBox;
	protected TextField fieldValue;

	public HBoxClassicDatabaseCriterion(DatabasePane parent, ChoiceBoxDatabaseCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion, null, null);
	}

	@Override
	protected void checkValidity() {
		if (!Utils.isNumber(fieldValue.getText()) || operatorChoiceBox.getValue() == null) {
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

	@Override
	protected void initialize() {
		valid = false;

		operatorChoiceBox = new ChoiceBox<String>();
		operatorChoiceBox.getItems().addAll("<=", "<", "=", ">", ">=", "!=");
		fieldValue = new TextField();

		operatorChoiceBox.getSelectionModel().select(2);

		fieldValue.setOnKeyReleased(e -> {
			checkValidity();
		});

		operatorChoiceBox.setOnAction(e -> {
			checkValidity();
		});

		this.getChildren().addAll(operatorChoiceBox, fieldValue, warningIcon, deleteButton);
	}

	@Override
	public ArrayList<BenzenoidCriterion> buildCriterions() {
		// TODO Auto-generated method stub
		return null;
	}

}
