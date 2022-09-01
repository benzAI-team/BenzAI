package view.generator.boxes;

import java.util.ArrayList;

import generator.GeneratorCriterion;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import modelProperty.ModelPropertySet;
import utils.Utils;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;

public class HBoxIrregularityCriterion extends HBoxCriterion {

	private ChoiceBox<String> irregularityChoiceBox;
	private ChoiceBox<String> operatorChoiceBox;
	private TextField fieldValue;

	public HBoxIrregularityCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	protected void checkValidity() {

		String irregularityValue = irregularityChoiceBox.getValue();
		String operatorValue = operatorChoiceBox.getValue();
		String fieldStr = fieldValue.getText();

		if (irregularityValue != null && irregularityValue.equals("Compute irregularity")) {

			setValid(true);
			this.getChildren().remove(operatorChoiceBox);
			this.getChildren().remove(fieldValue);
			this.getChildren().remove(getWarningIcon());
		}

		else {

			String [] split = fieldStr.split(",");
			
			System.out.println(split.length == 2 && Utils.isNumber(split[0]) && Utils.isNumber(split[1]));
			
			if ((split.length == 2 && Utils.isNumber(split[0]) && Utils.isNumber(split[1]))) {
				
				setValid(true);
				this.getChildren().remove(getWarningIcon());
				this.getChildren().remove(deleteButton);

				if (!this.getChildren().contains(operatorChoiceBox))
					this.getChildren().add(operatorChoiceBox);

				if (!this.getChildren().contains(fieldValue))
					this.getChildren().add(fieldValue);

				fieldValue.setText(split[0] + "." + split[1]);
				
				this.getChildren().addAll(deleteButton);
			}
			
			else {
			
				if (irregularityValue == null || operatorValue == null || !Utils.isNumber(fieldStr)) {

					setValid(false);
					this.getChildren().remove(getWarningIcon());
					this.getChildren().remove(deleteButton);

					if (!this.getChildren().contains(operatorChoiceBox))
						this.getChildren().add(operatorChoiceBox);

					if (!this.getChildren().contains(fieldValue))
						this.getChildren().add(fieldValue);

					this.getChildren().addAll(getWarningIcon(), deleteButton);
				}

				else if (!irregularityValue.equals("Compute irregularity")) {
				
					setValid(true);
					this.getChildren().remove(getWarningIcon());
					this.getChildren().remove(deleteButton);

					if (!this.getChildren().contains(operatorChoiceBox))
						this.getChildren().add(operatorChoiceBox);

					if (!this.getChildren().contains(fieldValue))
						this.getChildren().add(fieldValue);

					this.getChildren().addAll(deleteButton);
				}
			}
		}
	}

	@Override
	protected void initialize() {

		irregularityChoiceBox = new ChoiceBox<>();
		irregularityChoiceBox.getItems().addAll("XI", "N0", "N1", "N2", "N3", "N4");
		irregularityChoiceBox.getSelectionModel().selectFirst();

		irregularityChoiceBox.setOnAction(e -> {
			checkValidity();
		});

		operatorChoiceBox = new ChoiceBox<>();
		operatorChoiceBox.getItems().addAll("<=", "<", "=", ">", ">=");
		operatorChoiceBox.getSelectionModel().select(2);

		operatorChoiceBox.setOnAction(e -> {
			checkValidity();
		});

		fieldValue = new TextField();

		fieldValue.setOnKeyReleased(e -> {
			checkValidity();
		});

		//deleteButton = new DeleteButton(this);

		setWarningIcon(new ImageView(new Image("/resources/graphics/icon-warning.png")));

		this.getChildren().addAll(irregularityChoiceBox, operatorChoiceBox, fieldValue, getWarningIcon(), deleteButton);
		checkValidity();
	}

	@Override
	public void addPropertyExpression(ModelPropertySet modelPropertySet) {

		ArrayList<GeneratorCriterion> criterions = new ArrayList<>();

		if (isValid()) {

			String choiceBoxIrregValue = irregularityChoiceBox.getValue();

			criterions.add(new GeneratorCriterion(Subject.VIEW_IRREG, Operator.NONE, ""));

			if (!choiceBoxIrregValue.equals("Compute irregularity")) {

				Subject subject;

				if (choiceBoxIrregValue.equals("XI"))
					subject = Subject.XI;

				else if (choiceBoxIrregValue.equals("N0"))
					subject = Subject.N0;

				else if (choiceBoxIrregValue.equals("N1"))
					subject = Subject.N1;

				else if (choiceBoxIrregValue.equals("N2"))
					subject = Subject.N2;

				else if (choiceBoxIrregValue.equals("N3"))
					subject = Subject.N3;

				else
					subject = Subject.N4;

				Operator operator = GeneratorCriterion.getOperator(operatorChoiceBox.getValue());

				String value = fieldValue.getText();

				criterions.add(new GeneratorCriterion(subject, operator, value));
			}
		}

		return criterions;
	}

}
