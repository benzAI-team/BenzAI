package view.filtering.boxes;

import java.util.ArrayList;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import utils.Utils;
import view.filtering.ChoiceBoxFilteringCriterion;
import view.filtering.FilteringPane;
import view.filtering.criterions.CoronenoidFilteringCriterion;
import view.filtering.criterions.FilteringCriterion;

public class HBoxCoronenoidFilteringCriterion extends HBoxFilteringCriterion{

	private ChoiceBox<String> operatorChoiceBox;
	private TextField fieldValue;
	
	public HBoxCoronenoidFilteringCriterion(FilteringPane parent, ChoiceBoxFilteringCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	protected void checkValidity() {
		String operatorValue = operatorChoiceBox.getValue();
		String textValue = fieldValue.getText();

		if (operatorValue != null && operatorValue.equals("Unspecified")) {

			valid = true;
			this.getChildren().remove(fieldValue);
			this.getChildren().remove(warningIcon);
		}

		else {

			if (operatorValue == null || !Utils.isNumber(textValue)) {

				valid = false;

				this.getChildren().remove(warningIcon);
				this.getChildren().remove(deleteButton);

				if (!this.getChildren().contains(fieldValue))
					this.getChildren().add(fieldValue);

				this.getChildren().addAll(warningIcon, deleteButton);
			}

			else {

				valid = true;

				this.getChildren().remove(warningIcon);
				this.getChildren().remove(deleteButton);

				if (!this.getChildren().contains(fieldValue))
					this.getChildren().add(fieldValue);

				this.getChildren().addAll(deleteButton);
			}
		}
	}

	@Override
	protected void initialize() {
		Label nbHolesLabel = new Label("Number of crowns: ");

		operatorChoiceBox = new ChoiceBox<>();
		operatorChoiceBox.getItems().addAll("Unspecified", "<=", "<", "=", ">", ">=");
		operatorChoiceBox.getSelectionModel().selectFirst();

		operatorChoiceBox.setOnAction(e -> {
			checkValidity();
		});

		fieldValue = new TextField();

		fieldValue.setOnKeyReleased(e -> {
			checkValidity();
		});

		this.getChildren().addAll(nbHolesLabel, operatorChoiceBox, fieldValue, warningIcon, deleteButton);
		checkValidity();
	}

	@Override
	public ArrayList<FilteringCriterion> buildCriterions() {
		ArrayList<FilteringCriterion> criterions = new ArrayList<>();
		
		String operatorNbCrowns = null;
		int nbCrowns = -1;
		
		if (! operatorChoiceBox.getValue().equals("Unspecified")) {
			operatorNbCrowns = operatorChoiceBox.getValue();
			nbCrowns = Integer.parseInt(fieldValue.getText());
		}
		
		criterions.add(new CoronenoidFilteringCriterion(operatorNbCrowns, nbCrowns));
		
		
		return criterions;
	}

}
