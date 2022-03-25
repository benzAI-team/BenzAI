package view.filtering.boxes;

import javafx.application.Platform;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import utils.Utils;
import view.filtering.ChoiceBoxFilteringCriterion;
import view.filtering.FilteringPane;

public abstract class HBoxClassicalFilteringCriterion extends HBoxFilteringCriterion {

	protected ChoiceBox<String> operatorChoiceBox;
	protected TextField fieldValue;

	public HBoxClassicalFilteringCriterion(FilteringPane parent, ChoiceBoxFilteringCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
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
		operatorChoiceBox.getItems().addAll("<=", "<", "=", ">", ">=");
		fieldValue = new TextField();

		operatorChoiceBox.setOnAction(e -> {
			checkValidity();
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					fieldValue.requestFocus();
					fieldValue.selectAll();
				}
			});
		});

		operatorChoiceBox.getSelectionModel().select(2);

		fieldValue.setOnKeyReleased(e -> {
			checkValidity();
		});

		this.getChildren().clear();
		this.getChildren().addAll(operatorChoiceBox, fieldValue, warningIcon, deleteButton);
		checkValidity();
	}
}
