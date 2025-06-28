package view.patterns;

import benzenoid.Node;
import generator.patterns.Pattern;
import generator.patterns.PatternFileImport;
import generator.patterns.PatternLabel;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import utils.Utils;
import view.generator.boxes.HBoxPatternCriterion;

import java.io.File;
import java.io.IOException;

public class PatternsEditionPane extends BorderPane {

	private final HBoxPatternCriterion patternConstraintHBox;
	private BorderPane borderPane;
	private PatternGroup selectedPatternGroup;
	private TextField fieldName;
	private PatternLabel lastLabel;   // the label of the last color assign to a hexagon
	private PatternListBox patternListBox;
	private PatternTypeListBox patternTypeListBox;

	public PatternsEditionPane(HBoxPatternCriterion patternConstraintHBox) {
		super();
		this.patternConstraintHBox = patternConstraintHBox;
		initialize();
		lastLabel = PatternLabel.VOID;
	}

	private void initialize() {
		initializeMenuBar();
		initializePane();
	}

	private void initializeMenuBar() {
		MenuBar menuBar = new MenuBar();
		buildFileMenu(menuBar);
		buildDrawingMenu(menuBar);
		buildNameMenu(menuBar);
		buildCrownMenu(menuBar);
		this.setTop(menuBar);
	}



	private void buildCrownMenu(MenuBar menuBar) {
		Button minusButton = new Button("-");
		minusButton.setOnAction(e -> removeCrown());

		Button plusButton = new Button("+");
		plusButton.setOnAction(e -> addCrown());

		Label nbCrownsLabel = new Label("Number of crowns: ");

		HBox nbCrownsBox = new HBox(3.0);
		nbCrownsBox.getChildren().addAll(nbCrownsLabel, minusButton, plusButton);

		Menu nbCrownsMenu = new Menu();
		nbCrownsMenu.setGraphic(nbCrownsBox);
		menuBar.getMenus().add(nbCrownsMenu);
	}

	private void buildNameMenu(MenuBar menuBar) {
		Label labelName = new Label("Name: ");
		fieldName = new TextField("default name");
		fieldName.setOnKeyReleased(e -> {
			int index = selectedPatternGroup.getIndex();
			GridPane gridPane = PatternListBox.getBoxItems().get(index);

			if ("".equals(fieldName.getText())) ((Label) gridPane.getChildren().get(0)).setText("default name");
			else ((Label) gridPane.getChildren().get(0)).setText(fieldName.getText());
		});

		HBox boxName = new HBox(3.0);
		boxName.getChildren().addAll(labelName, fieldName);

		Menu nameMenu = new Menu();
		nameMenu.setGraphic(boxName);
		menuBar.getMenus().add(nameMenu);
	}

	private void buildDrawingMenu(MenuBar menuBar) {
		Menu drawingMenu = new Menu("Drawing");

		MenuItem clearItem = new MenuItem("Clear");
		clearItem.setOnAction(e -> selectedPatternGroup.setAllLabels(PatternLabel.VOID));

		MenuItem neutralItem = new MenuItem("Set all neutral");
		neutralItem.setOnAction(e -> selectedPatternGroup.setAllLabels(PatternLabel.NEUTRAL));

		MenuItem positiveItem = new MenuItem("Set all positive");
		positiveItem.setOnAction(e -> selectedPatternGroup.setAllLabels(PatternLabel.POSITIVE));

		MenuItem negativeItem = new MenuItem("Set all negative");
		negativeItem.setOnAction(e -> selectedPatternGroup.setAllLabels(PatternLabel.NEGATIVE));

		drawingMenu.getItems().addAll(clearItem, neutralItem, positiveItem, negativeItem);
		menuBar.getMenus().add(drawingMenu);
	}

	private void buildFileMenu(MenuBar menuBar) {
		MenuItem saveItem = buildSaveItem();
		MenuItem importItem = buildImportItem();

		Menu fileMenu = new Menu("File");
		fileMenu.getItems().addAll(importItem, saveItem);

		menuBar.getMenus().add(fileMenu);
	}

	private MenuItem buildSaveItem() {
		MenuItem saveItem = new MenuItem("Save pattern as");

		saveItem.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();
			File file = fileChooser.showSaveDialog(patternConstraintHBox.getApplication().getStage());

			if (file != null) {
				Pattern pattern = selectedPatternGroup.exportPattern();
				try {
					pattern.export(file);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		return saveItem;
	}

	private MenuItem buildImportItem() {
		MenuItem importItem = new MenuItem("Import pattern");
		importItem.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();
			File file = fileChooser.showOpenDialog(patternConstraintHBox.getApplication().getStage());

			if (file != null) {
				boolean ok = true;
				Pattern pattern = null;
				try {
					pattern = PatternFileImport.importPattern(file);
				} catch (Exception e1) {
					e1.printStackTrace();
					ok = false;
				}

				if (ok) {
					int yMax = -1;
					for (Node node : pattern.getNodesRefs())
						if (node.getY() > yMax)
							yMax = node.getX();

					int maxColumn = -1;

					for (int i = 0; i <= yMax; i++) {
						int column = 0;
						for (Node node : pattern.getNodesRefs()) {
							if (node.getY() == i) {
								column++;
							}
						}
						if (column > maxColumn)
							maxColumn = column;
					}

					PatternGroup newPattern = new PatternGroup(this, maxColumn, null);
					newPattern.importPattern(pattern);
					patternListBox.addEntry(newPattern);
					patternTypeListBox.addEntry(new PatternTypeExistence(newPattern));

//					patternListBox.getPatternGroups().set(index, group);
//					patternListBox.select(index);
				}
				else
					Utils.alert("Error while importing the pattern");
			}
		});
		return importItem;
	}


	private void initializePane() {
		this.setPrefSize(1500, 500);
		this.setPadding(new Insets(15.0));

		patternListBox = new PatternListBox(this);
		patternTypeListBox = new PatternTypeListBox(this);

		borderPane = new BorderPane();
		borderPane.setCenter(selectedPatternGroup);

		VBox rightPanel = new VBox (5);
		rightPanel.getChildren().addAll(patternListBox, patternTypeListBox, buildApplyButton());
		borderPane.setRight(rightPanel);
		this.setCenter(borderPane);

		PatternGroup newPattern = new PatternGroup(this, 3, null);
		patternListBox.addEntry(newPattern);
		patternTypeListBox.addEntry(new PatternTypeExistence(newPattern));
	}

	private VBox buildApplyBox() {
		Button buttonBox = buildApplyButton();
		VBox applyBox = new VBox(3.0);
		applyBox.getChildren().add(buttonBox);
//		applyBox.setPrefHeight(this.getHeight());
		return applyBox;
	}

	private Button buildApplyButton() {
		Button applyPatternButton = new Button("Apply");
		applyPatternButton.setPrefWidth(250);
		applyPatternButton.setOnAction(e -> {
			for (PatternType type : patternTypeListBox.getPatternTypes())
				type.setConstraint(patternConstraintHBox);

//			ArrayList<Pattern> patterns = new ArrayList<>();
//			for (PatternGroup group : patternListBox.getPatternGroups()) {
//				patterns.add(buildPattern(group));
//			}
//
//
//			PatternGenerationType type = null;
//			PropertyExpression expression = null;
//
//			if (PatternListBox.getBoxItems().size() == 1) {
//				if (patternPropertyMenu.getDisableItem().isSelected()) {
//					patternConstraintHBox.refreshPatternInformations("FORBIDDEN_PATTERN");
//					type = PatternGenerationType.FORBIDDEN_PATTERN;
//					expression = new SubjectExpression("FORBIDDEN_PATTERN");
//					patternConstraintHBox.getPatternProperty().setConstraint(new ForbiddenPatternConstraint3(patternInformations.getPatterns().get(0),
//							VariableStrategy.FIRST_FAIL, ValueStrategy.INT_MAX));
//				} else {
//					if (Utils.isNumber(patternPropertyMenu.getOccurencesField().getText())) {
//						type = PatternGenerationType.PATTERN_OCCURENCES;
//						patternConstraintHBox.refreshPatternInformations("OCCURRENCES_PATTERN: " + patternPropertyMenu.getOccurencesField().getText());
//						expression = new BinaryNumericalExpression("OCCURENCE_PATTERN", "=",
//								Integer.parseInt(patternPropertyMenu.getOccurencesField().getText()));
//					} else {
//						type = PatternGenerationType.SINGLE_PATTERN_3;
//						patternConstraintHBox.refreshPatternInformations("SINGLE_PATTERN");
//						expression = new SubjectExpression("SINGLE_PATTERN");
//						patternConstraintHBox.getPatternProperty().setConstraint(new SinglePattern3Constraint(patternInformations.getPatterns().get(0),
//								VariableStrategy.FIRST_FAIL, ValueStrategy.INT_MAX, OrderStrategy.CHANNELING_FIRST));
//					}
//				}
//			}
//			else {
//				if (patternPropertyMenu.getItemUndisjunct().isSelected() || patternPropertyMenu.getItemDisjunct().isSelected() || patternPropertyMenu.getItemNNDisjunct().isSelected()) {
//					patternConstraintHBox.refreshPatternInformations("MULTIPLE_PATTERNS");
//					type = PatternGenerationType.MULTIPLE_PATTERN_3;
//					expression = new SubjectExpression("MULTIPLE_PATTERNS");
//					patternConstraintHBox.getPatternProperty().setConstraint(new MultiplePatterns3Constraint(patternInformations.getPatterns(),
//							VariableStrategy.FIRST_FAIL, ValueStrategy.INT_MAX, OrderStrategy.CHANNELING_FIRST));
//				}
//			}

//			if (patternPropertyMenu.getItemUndisjunct().isSelected())
//				patternInformations.setInterraction(PatternsInterraction.UNDISJUNCT);
//
//			else if (patternPropertyMenu.getItemDisjunct().isSelected())
//				patternInformations.setInterraction(PatternsInterraction.DISJUNCT);
//
//			else if (patternPropertyMenu.getItemNNDisjunct().isSelected())
//				patternInformations.setInterraction(PatternsInterraction.DISJUNCT_NN);
//			patternConstraintHBox.setPatternResolutionInformations(patternInformations);
//			patternConstraintHBox.setExpression(expression);
			hide();
		});
		return applyPatternButton;
	}

	int getNbItems() {
		return PatternListBox.getBoxItems().size();
	}

	void checkBorder() {
		boolean borderUsed = false;
		for (PatternHexagon hexagon : selectedPatternGroup.getExtendedBorder()) {
			if (hexagon.getLabel() != PatternLabel.VOID) {
				borderUsed = true;
				break;
			}
		}
		if (!borderUsed)
			removeCrown();
	}

	void addCrown() {
		int nbCrowns = selectedPatternGroup.getNbCrowns() + 1;

		PatternGroup newPatternGroup = new PatternGroup(this, nbCrowns, selectedPatternGroup.getIndex(),selectedPatternGroup.getLabel());

		PatternHexagon[][] hexagonMatrix = selectedPatternGroup.getHexagonsMatrix();

		for (int i = 0; i < hexagonMatrix.length; i++) {
			for (int j = 0; j < hexagonMatrix.length; j++) {
				if (hexagonMatrix[i][j] != null) {
					newPatternGroup.getHexagonsMatrix()[i + 1][j + 1].setLabel(hexagonMatrix[i][j].getLabel());
				}
			}
		}

		borderPane.getChildren().remove(selectedPatternGroup);
		patternListBox.getPatternGroups().set(newPatternGroup.getIndex(), newPatternGroup);
		patternListBox.select(newPatternGroup.getIndex());
	}

	private void removeCrown() {

		int nbCrowns = selectedPatternGroup.getNbCrowns();

		if (nbCrowns > 3) {

			nbCrowns--;
			PatternGroup newPatternGroup = new PatternGroup(this, nbCrowns, selectedPatternGroup.getIndex(),selectedPatternGroup.getLabel());

			PatternHexagon[][] hexagonMatrix = selectedPatternGroup.getHexagonsMatrix();
			PatternHexagon[][] newHexagonMatrix = newPatternGroup.getHexagonsMatrix();

			for (int i = 1; i < hexagonMatrix.length; i++) {
				for (int j = 1; j < hexagonMatrix.length; j++) {
					if (i - 1 < newHexagonMatrix.length && j - 1 < newHexagonMatrix.length
							&& newHexagonMatrix[i - 1][j - 1] != null)
						newHexagonMatrix[i - 1][j - 1].setLabel(hexagonMatrix[i][j].getLabel());
				}
			}

			borderPane.getChildren().remove(selectedPatternGroup);
			patternListBox.getPatternGroups().set(newPatternGroup.getIndex(), newPatternGroup);
			patternListBox.select(newPatternGroup.getIndex());
		}
	}

	private static Pattern buildPattern(PatternGroup group) {
		return group.exportPattern();
	}

	public void unselectAllMenus(CheckMenuItem... items) {

		for (CheckMenuItem item : items) {
			item.setSelected(false);
		}
	}

  void setLastLabel(PatternLabel label) {
    lastLabel = label;
  }
  
  PatternLabel getLastLabel() {
    return lastLabel;
  }

	private void hide() {
		patternConstraintHBox.hidePatternStage();
	}

	BorderPane getBorderPane () {
		return borderPane;
	}

	TextField getFieldName() {
		return fieldName;
	}

	void selectPatternGroup (PatternGroup patternGroup) {
		selectedPatternGroup = patternGroup;
	}

	PatternGroup getSelectedPatternGroup() {
		return selectedPatternGroup;
	}

	public PatternListBox getPatternListBox() {
		return patternListBox;
	}

	public PatternTypeListBox getPatternTypeListBox() {
		return patternTypeListBox;
	}
}
