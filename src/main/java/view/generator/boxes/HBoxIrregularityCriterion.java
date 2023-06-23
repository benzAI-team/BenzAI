package view.generator.boxes;

import generator.properties.model.expression.PropertyExpression;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import generator.properties.model.ModelPropertySet;
import generator.properties.model.expression.IrregularityExpression;
import utils.Utils;
import view.generator.ChoiceBoxCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

import java.util.Objects;

public class HBoxIrregularityCriterion extends HBoxModelCriterion {

	private ChoiceBox<String> irregularityChoiceBox;
	private ChoiceBox<String> operatorChoiceBox;
	private TextField fieldValue;

	public HBoxIrregularityCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	public void updateValidity() {

		String irregularityValue = irregularityChoiceBox.getValue();
		String operatorValue = operatorChoiceBox.getValue();
		String fieldStr = fieldValue.getText();

		setBounding(false);
		if ("Compute irregularity".equals(irregularityValue)) {

			setValid(true);
			this.getChildren().remove(operatorChoiceBox);
			this.getChildren().remove(fieldValue);
			this.getChildren().remove(getWarningIcon());
		}
		else {
			String [] split = fieldStr.split(",");
			
			System.out.println("#" + (split.length == 2 && Utils.isNumber(split[0]) && Utils.isNumber(split[1])));
			
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
				else {
					setValid(true);
					removeWarningIconAndDeleteButton();
					addOperatorChoiceBoxIfMissing();
					addFieldValueIfMissing();
					addDeleteButton();
				}
			}
		}
		getPane().refreshGenerationPossibility();
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

		operatorChoiceBox = new ChoiceBox<>();
		operatorChoiceBox.getItems().addAll("<=", "<", "=", ">", ">=");
		operatorChoiceBox.getSelectionModel().select(2);

		fieldValue = new TextField();
		setWarningIcon(new ImageView(new Image("/resources/graphics/icon-warning.png")));

		this.getChildren().addAll(irregularityChoiceBox, operatorChoiceBox, fieldValue, getWarningIcon(), getDeleteButton());
		updateValidity();
	}

	public void assign(PropertyExpression propertyExpression) {
		IrregularityExpression expression = (IrregularityExpression) propertyExpression;
		irregularityChoiceBox.getSelectionModel().select(expression.getParameter());
		operatorChoiceBox.getSelectionModel().select(expression.getOperator());
		fieldValue.setText("" + (expression.getParameter().equals("XI") ? ((double)expression.getValue()) / 100.0 : expression.getValue()));
	}

	@Override
	public void initEventHandling() {
		irregularityChoiceBox.setOnAction(e -> updateValidity());
		operatorChoiceBox.setOnAction(e -> updateValidity());
		fieldValue.setOnKeyReleased(e -> updateValidity());
	}

	@Override
	public void addPropertyExpression(ModelPropertySet modelPropertySet) {
		if (isValid()) {
			String parameter = irregularityChoiceBox.getValue();
			String operator = operatorChoiceBox.getValue();
			// 0 =< Xi =< 1 must be multiplied by 100 and converted to an int 
			int value = Objects.equals(parameter, "XI") ? (int)(Double.parseDouble(fieldValue.getText()) * 100) : Integer.parseInt(fieldValue.getText());
			modelPropertySet.getById("irregularity").addExpression(new IrregularityExpression("irregularity", parameter, operator, value));
		}
	}

}
