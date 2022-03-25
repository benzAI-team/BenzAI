package view.generator.boxes;

import java.util.ArrayList;

import generator.GeneratorCriterion;
import generator.GeneratorCriterion.Operator;
import generator.GeneratorCriterion.Subject;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import utils.Utils;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;

public class HBoxCoronenoidCriterion extends HBoxCriterion {

	private ChoiceBox<String> operatorChoiceBox;
	private TextField fieldValue;

	public HBoxCoronenoidCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
		// TODO Auto-generated constructor stub
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
	public ArrayList<GeneratorCriterion> buildCriterions() {

		ArrayList<GeneratorCriterion> criterions = new ArrayList<>();

		if (valid) {
			criterions.add(new GeneratorCriterion(Subject.CORONENOID, Operator.NONE, ""));
			if (!operatorChoiceBox.getValue().equals("Unspecified"))
				criterions.add(new GeneratorCriterion(Subject.NB_CROWNS,
						GeneratorCriterion.getOperator(operatorChoiceBox.getValue()), fieldValue.getText()));
		}

		return criterions;
	}

}
