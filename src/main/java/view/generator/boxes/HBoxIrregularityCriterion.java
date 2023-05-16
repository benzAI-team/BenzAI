package view.generator.boxes;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import generator.properties.model.ModelPropertySet;
import generator.properties.model.expression.IrregularityExpression;
import utils.Utils;
import view.generator.ChoiceBoxCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class HBoxIrregularityCriterion extends HBoxModelCriterion {

	private ChoiceBox<String> irregularityChoiceBox;
	private ChoiceBox<String> operatorChoiceBox;
	private TextField fieldValue;

	public HBoxIrregularityCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	protected void updateValidity() {

		String irregularityValue = irregularityChoiceBox.getValue();
		String operatorValue = operatorChoiceBox.getValue();
		String fieldStr = fieldValue.getText();

		if (irregularityValue != null && "Compute irregularity".equals(irregularityValue)) {

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
				removeWarningIconAndDeleteButton();
				addOperatorChoiceBoxIfMissing();
				addFieldValueIfMissing();
				fieldValue.setText(split[0] + "." + split[1]);
				addDeleteButton();
			}
			else {
				if (irregularityValue == null || operatorValue == null || !Utils.isNumber(fieldStr)) {
					setValid(false);
					removeWarningIconAndDeleteButton();
					addOperatorChoiceBoxIfMissing();
					addFieldValueIfMissing();
					addWarningIconAndDeleteButton();
				}

				else if (!"Compute irregularity".equals(irregularityValue)) {
				
					setValid(true);
					removeWarningIconAndDeleteButton();
					addOperatorChoiceBoxIfMissing();
					addFieldValueIfMissing();
					addDeleteButton();
				}
			}
		}
	}

	private void addFieldValueIfMissing() {
		if (!this.getChildren().contains(fieldValue))
			this.getChildren().add(fieldValue);
	}

	private void addOperatorChoiceBoxIfMissing() {
		if (!this.getChildren().contains(operatorChoiceBox))
			this.getChildren().add(operatorChoiceBox);
	}

	@Override
	protected void initialize() {

		irregularityChoiceBox = new ChoiceBox<>();
		irregularityChoiceBox.getItems().addAll("XI", "N0", "N1", "N2", "N3", "N4");
		irregularityChoiceBox.getSelectionModel().selectFirst();

		irregularityChoiceBox.setOnAction(e -> {
			updateValidity();
		});

		operatorChoiceBox = new ChoiceBox<>();
		operatorChoiceBox.getItems().addAll("<=", "<", "=", ">", ">=");
		operatorChoiceBox.getSelectionModel().select(2);

		operatorChoiceBox.setOnAction(e -> {
			updateValidity();
		});

		fieldValue = new TextField();

		fieldValue.setOnKeyReleased(e -> {
			updateValidity();
		});

		//deleteButton = new DeleteButton(this);

		setWarningIcon(new ImageView(new Image("/resources/graphics/icon-warning.png")));

		this.getChildren().addAll(irregularityChoiceBox, operatorChoiceBox, fieldValue, getWarningIcon(), getDeleteButton());
		updateValidity();
	}

	@Override
	public void addPropertyExpression(ModelPropertySet modelPropertySet) {
		if (isValid()) {
			String parameter = irregularityChoiceBox.getValue();
			String operator = operatorChoiceBox.getValue();
			// 0 =< Xi =< 1 must be multiplied by 100 and converted to an int 
			int value = parameter == "XI" ? (int)(Double.parseDouble(fieldValue.getText()) * 100) : Integer.parseInt(fieldValue.getText());
			modelPropertySet.getById("irregularity").addExpression(new IrregularityExpression("irregularity", parameter, operator, value));
		}
	}

}
