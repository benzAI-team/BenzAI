package view.filtering.boxes;

import java.util.ArrayList;

import utils.Utils;
import view.filtering.ChoiceBoxFilteringCriterion;
import view.filtering.FilteringPane;
import view.filtering.criterions.FilteringCriterion;
import view.filtering.criterions.FilteringOperator;
import view.filtering.criterions.NbHydrogensCriterion;

public class HBoxNbHydrogensFilteringCriterion extends HBoxClassicalFilteringCriterion {

	public HBoxNbHydrogensFilteringCriterion(FilteringPane parent, ChoiceBoxFilteringCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
		operatorChoiceBox.getItems().addAll("EVEN", "ODD");
	}

	@Override
	protected void checkValidity() {

		if (operatorChoiceBox.getValue().equals("EVEN") || operatorChoiceBox.getValue().equals("ODD")) {
			valid = true;
			this.getChildren().remove(warningIcon);
			this.getChildren().remove(deleteButton);
			this.getChildren().remove(fieldValue);
			this.getChildren().add(deleteButton);
		}

		else if (!Utils.isNumber(fieldValue.getText()) || operatorChoiceBox.getValue() == null) {
			valid = false;
			this.getChildren().remove(warningIcon);
			this.getChildren().remove(deleteButton);
			this.getChildren().remove(fieldValue);
			this.getChildren().addAll(fieldValue, warningIcon, deleteButton);
		}

		else {
			valid = true;
			this.getChildren().remove(warningIcon);
			this.getChildren().remove(deleteButton);
			this.getChildren().remove(fieldValue);
			this.getChildren().addAll(fieldValue, deleteButton);
		}

	}

	@Override
	public ArrayList<FilteringCriterion> buildCriterions() {
		ArrayList<FilteringCriterion> criterions = new ArrayList<>();

		if (isValid()) {

			NbHydrogensCriterion criterion;
			if (!operatorChoiceBox.getValue().equals("EVEN") && !operatorChoiceBox.getValue().equals("ODD")) {

				criterion = new NbHydrogensCriterion(FilteringOperator.getOperator(operatorChoiceBox.getValue()),
						Integer.parseInt(fieldValue.getText()));
			}

			else {
				criterion = new NbHydrogensCriterion(FilteringOperator.getOperator(operatorChoiceBox.getValue()), -1);
			}

			criterions.add(criterion);
		}

		return criterions;
	}

}
