package view.generator.boxes;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import utils.Utils;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;

public abstract class ClassicalHBoxCriterion extends HBoxCriterion {

	public ClassicalHBoxCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	protected ChoiceBox<String> operatorChoiceBox;
	protected TextField fieldValue;
	
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
	}

	@Override
	protected void initialize() {
		
		valid = false;
		
		operatorChoiceBox = new ChoiceBox<String>();
		operatorChoiceBox.getItems().addAll("<=", "<", "=", ">", ">=");
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
	
}
