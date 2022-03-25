package view.filtering.boxes;

import java.util.ArrayList;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import utils.Utils;
import view.filtering.ChoiceBoxFilteringCriterion;
import view.filtering.FilteringPane;
import view.filtering.criterions.FilteringCriterion;
import view.filtering.criterions.RectangleCriterion;

public class HBoxRectangleFilteringCriterion extends HBoxFilteringCriterion {

	private GridPane gridPane;

	protected ChoiceBox<String> nbLinesChoiceBox;
	protected TextField nbLinesTextField;

	protected ChoiceBox<String> nbColumnsChoiceBox;
	protected TextField nbColumnsTextField;

	private HBox hBoxNbLines;
	private HBox hBoxNbColumns;

	protected boolean valid1;
	protected boolean valid2;

	public HBoxRectangleFilteringCriterion(FilteringPane parent, ChoiceBoxFilteringCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	protected void checkValidity() {
		String nbLinesChoice = nbLinesChoiceBox.getValue();
		String nbColumnsChoice = nbColumnsChoiceBox.getValue();

		/*
		 * Nb Lines
		 */

		hBoxNbLines.getChildren().remove(nbLinesTextField);
		hBoxNbLines.getChildren().remove(warningIcon);
		hBoxNbLines.getChildren().remove(deleteButton);

		if (nbLinesChoice != null && nbLinesChoice.equals("Unspecified"))
			valid1 = true;

		else {

			if (nbLinesChoice == null || !Utils.isNumber(nbLinesTextField.getText())) {
				valid1 = false;
				hBoxNbLines.getChildren().addAll(nbLinesTextField);
			}

			else {
				valid1 = true;
				hBoxNbLines.getChildren().addAll(nbLinesTextField);
			}
		}

		/*
		 * Nb Columns
		 */

		hBoxNbColumns.getChildren().remove(nbColumnsTextField);
		hBoxNbColumns.getChildren().remove(warningIcon);
		hBoxNbColumns.getChildren().remove(deleteButton);

		if (nbColumnsChoice != null && nbColumnsChoice.equals("Unspecified"))
			valid2 = true;

		else {

			if (nbColumnsChoice == null || !Utils.isNumber(nbColumnsTextField.getText())) {
				valid2 = false;
				hBoxNbColumns.getChildren().addAll(nbColumnsTextField);
			}

			else {
				valid2 = true;
				hBoxNbColumns.getChildren().addAll(nbColumnsTextField);
			}
		}

		valid = valid1 && valid2;

		if (!valid)
			hBoxNbLines.getChildren().add(warningIcon);

		hBoxNbLines.getChildren().add(deleteButton);
	}

	@Override
	protected void initialize() {

		valid1 = false;
		valid2 = false;

		Label nbLinesLabel = new Label("Number of lines:   ");
		nbLinesChoiceBox = new ChoiceBox<>();
		nbLinesChoiceBox.getItems().addAll("Unspecified", "<=", "<", "=", ">", ">=");
		nbLinesChoiceBox.getSelectionModel().selectFirst();

		nbLinesTextField = new TextField();

		nbLinesChoiceBox.setOnAction(e -> {
			checkValidity();
		});

		nbLinesTextField.setOnKeyReleased(e -> {
			checkValidity();
		});

		hBoxNbLines = new HBox(5.0);
		hBoxNbLines.getChildren().addAll(nbLinesLabel, nbLinesChoiceBox, nbLinesTextField, warningIcon, deleteButton);

		Label nbColumnsLabel = new Label("Number of columns: ");
		nbColumnsChoiceBox = new ChoiceBox<>();
		nbColumnsChoiceBox.getItems().addAll("Unspecified", "<=", "<", "=", ">", ">=");
		nbColumnsChoiceBox.getSelectionModel().selectFirst();
		nbColumnsTextField = new TextField();

		nbColumnsChoiceBox.setOnAction(e -> {
			checkValidity();
		});

		nbColumnsTextField.setOnKeyReleased(e -> {
			checkValidity();
		});

		hBoxNbColumns = new HBox(5.0);
		hBoxNbColumns.getChildren().addAll(nbColumnsLabel, nbColumnsChoiceBox, nbColumnsTextField);

		gridPane = new GridPane();

		gridPane.add(hBoxNbLines, 0, 0);
		gridPane.add(hBoxNbColumns, 0, 1);

		checkValidity();
		this.getChildren().add(gridPane);
	}

	@Override
	public ArrayList<FilteringCriterion> buildCriterions() {
		ArrayList<FilteringCriterion> criterions = new ArrayList<>();

		RectangleCriterion rectangleCriterion;

		if (nbLinesChoiceBox.getValue().equals("Unspecified") && nbColumnsChoiceBox.getValue().equals("Unspecified")) {
			rectangleCriterion = new RectangleCriterion();
		}

		else {

			String operatorNbLines = null;
			int nbLines = 0;

			String operatorNbColumns = null;
			int nbColumns = 0;

			if (!nbLinesChoiceBox.getValue().equals("Unspecified")) {
				operatorNbLines = nbLinesChoiceBox.getValue();
				nbLines = Integer.parseInt(nbLinesTextField.getText());
			}

			if (!nbColumnsChoiceBox.getValue().equals("Unspecified")) {
				operatorNbColumns = nbColumnsChoiceBox.getValue();
				nbColumns = Integer.parseInt(nbColumnsTextField.getText());
			}

			rectangleCriterion = new RectangleCriterion(operatorNbLines, nbLines, operatorNbColumns, nbColumns);
		}

		criterions.add(rectangleCriterion);
		return criterions;
	}

}
