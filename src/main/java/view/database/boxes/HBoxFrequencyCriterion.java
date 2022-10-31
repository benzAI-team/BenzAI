package view.database.boxes;

import java.util.ArrayList;

import database.BenzenoidCriterion;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import view.database.ChoiceBoxDatabaseCriterion;
import view.database.DatabasePane;

public class HBoxFrequencyCriterion extends HBoxDatabaseCriterion{

	protected ChoiceBox<String> operatorChoiceBox;
	protected TextField fieldValue1;
	protected TextField fieldValue2;
	
	public HBoxFrequencyCriterion(DatabasePane parent, ChoiceBoxDatabaseCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void checkValidity() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initialize() {
		valid = false;

		operatorChoiceBox = new ChoiceBox<String>();
		operatorChoiceBox.getItems().addAll("<=", "<", "=", ">", ">=", "!=", "IN");
		
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

		this.getChildren().addAll(operatorChoiceBox, fieldValue1, warningIcon, deleteButton);
		
	}

	@Override
	public ArrayList<BenzenoidCriterion> buildCriterions() {
		ArrayList<BenzenoidCriterion> criterions = new ArrayList<>();
		return criterions;
	}

}
