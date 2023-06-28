package view.patterns;

import constraints.ForbiddenPatternConstraint1;
import constraints.SinglePattern2Constraint;
import generator.OrderStrategy;
import generator.ValueStrategy;
import generator.VariableStrategy;
import generator.patterns.*;
import generator.properties.model.expression.BinaryNumericalExpression;
import generator.properties.model.expression.PropertyExpression;
import generator.properties.model.expression.SubjectExpression;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import benzenoid.Node;
import utils.Utils;
import view.generator.boxes.HBoxPatternCriterion;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PatternsEditionPane extends BorderPane {

	private final HBoxPatternCriterion patternConstraintHBox;
	private final PatternPropertyMenu patternPropertyMenu = new PatternPropertyMenu();
	private BorderPane borderPane;
	private ListView<GridPane> listView;
	private ArrayList<GridPane> boxItems;
	private ArrayList<PatternGroup> patternGroups;

	private PatternGroup selectedPatternGroup;
	private int selectedIndex;
	private TextField fieldName;
	private PatternLabel lastLabel;   // the label of the last color assign to a hexagon

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
		buildPatternMenu(menuBar);
		this.setTop(menuBar);
	}

	private void buildPatternMenu(MenuBar menuBar) {
		Menu patternMenu = patternPropertyMenu.build();
		menuBar.getMenus().add(patternMenu);
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
			GridPane gridPane = boxItems.get(index);

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

					int index = boxItems.size();
					addEntry();

					PatternGroup group = new PatternGroup(this, maxColumn, index);
					group.importPattern(pattern);
					patternGroups.set(index, group);
					select(index);
				}
				else
					Utils.alert("Error while importing the pattern");
			}
		});
		return importItem;
	}

	private void select(int index) {
		selectedPatternGroup = patternGroups.get(index);
		selectedIndex = index;

		Label label = (Label) boxItems.get(index).getChildren().get(0);
		fieldName.setText(label.getText());

		borderPane.setCenter(selectedPatternGroup);
	}

	private void initializePane() {
		this.setPrefSize(1500, 500);
		this.setPadding(new Insets(15.0));

		patternGroups = new ArrayList<>();
		boxItems = new ArrayList<>();

		VBox patternListBox = buildPatternListBox();
		addEntry();

		borderPane = new BorderPane();
		borderPane.setCenter(selectedPatternGroup);
		borderPane.setRight(patternListBox);
		this.setCenter(borderPane);
		select(0);
	}

	private VBox buildPatternListBox() {
		HBox buttonBox = buildButtonBox();
		buildListView();

		VBox patternListBox = new VBox(5.0);
		patternListBox.getChildren().addAll(listView, buttonBox);
		patternListBox.setPrefHeight(this.getHeight());
		return patternListBox;
	}

	private HBox buildButtonBox() {
		Button addPatternButton = buildAddPatternButton();
		Button applyPatternButton = buildApplyPatternButton();

		HBox buttonBox = new HBox(3.0);
		buttonBox.getChildren().addAll(addPatternButton, applyPatternButton);
		return buttonBox;
	}

	private void buildListView() {
		listView = new ListView<>();
		listView.setOnMouseClicked(event -> {
			GridPane selection = listView.getSelectionModel().getSelectedItem();
			if (selection != null) {
				PatternCloseButton button = (PatternCloseButton) selection.getChildren().get(1);
				select(button.getIndex());
			}
		});
	}

	private Button buildApplyPatternButton() {
		Button applyPatternButton = new Button("Apply");
		applyPatternButton.setPrefWidth(125);
		applyPatternButton.setOnAction(e -> {
			ArrayList<Pattern> patterns = new ArrayList<>();
			for (PatternGroup group : patternGroups) {
				patterns.add(buildPattern(group));
			}

			PatternGenerationType type = null;
			PropertyExpression expression = null;
			PatternResolutionInformations patternInformations;

			if (boxItems.size() == 1) {
				if (patternPropertyMenu.getDisableItem().isSelected()) {
					patternConstraintHBox.refreshPatternInformations("FORBIDDEN_PATTERN");
					type = PatternGenerationType.FORBIDDEN_PATTERN;
					expression = new SubjectExpression("FORBIDDEN_PATTERN");
					patternInformations = new PatternResolutionInformations(type, patterns);
					patternConstraintHBox.getPatternProperty().setConstraint(new ForbiddenPatternConstraint1(patternInformations.getPatterns().get(0),
							VariableStrategy.FIRST_FAIL, ValueStrategy.INT_MAX, OrderStrategy.CHANNELING_FIRST));
				} else {
					if (Utils.isNumber(patternPropertyMenu.getOccurencesField().getText())) {
						type = PatternGenerationType.PATTERN_OCCURENCES;
						patternConstraintHBox.refreshPatternInformations("OCCURENCES_PATTERN: " + patternPropertyMenu.getOccurencesField().getText());
						expression = new BinaryNumericalExpression("OCCURENCE_PATTERN", "=",
								Integer.parseInt(patternPropertyMenu.getOccurencesField().getText()));
					} else {
						type = PatternGenerationType.SINGLE_PATTERN_2;
						patternConstraintHBox.refreshPatternInformations("SINGLE_PATTERN");
						expression = new SubjectExpression("SINGLE_PATTERN");
						patternInformations = new PatternResolutionInformations(type, patterns);
						patternConstraintHBox.getPatternProperty().setConstraint(new SinglePattern2Constraint(patternInformations.getPatterns().get(0), false,
								VariableStrategy.FIRST_FAIL, ValueStrategy.INT_MAX, OrderStrategy.CHANNELING_FIRST));
					}
				}
			}
			else if (patternPropertyMenu.getItemUndisjunct().isSelected() || patternPropertyMenu.getItemDisjunct().isSelected() || patternPropertyMenu.getItemNNDisjunct().isSelected()) {
				patternConstraintHBox.refreshPatternInformations("MULTIPLE_PATTERNS");
				type = PatternGenerationType.MULTIPLE_PATTERN_1;
				expression = new SubjectExpression("MULTIPLE_PATTERNS");
			}

			patternInformations = new PatternResolutionInformations(type, patterns);

			if (patternPropertyMenu.getItemUndisjunct().isSelected())
				patternInformations.setInterraction(PatternsInterraction.UNDISJUNCT);

			else if (patternPropertyMenu.getItemDisjunct().isSelected())
				patternInformations.setInterraction(PatternsInterraction.DISJUNCT);

			else if (patternPropertyMenu.getItemNNDisjunct().isSelected())
				patternInformations.setInterraction(PatternsInterraction.DISJUNCT_NN);
			patternConstraintHBox.setPatternResolutionInformations(patternInformations);
			patternConstraintHBox.setExpression(expression);
			hide();
		});
		return applyPatternButton;
	}

	private Button buildAddPatternButton() {
		Button addPatternButton = new Button("Add pattern");
		addPatternButton.setPrefWidth(125);
		addPatternButton.setOnAction(e -> addEntry());
		return addPatternButton;
	}

	private void addEntry() {

		Label label = new Label("unknown_pattern");

		PatternCloseButton button = new PatternCloseButton(this, patternGroups.size());

		GridPane pane = new GridPane();
		pane.setPadding(new Insets(1));

		pane.add(label, 0, 0);
		label.setAlignment(Pos.BASELINE_CENTER);

		pane.add(button, 1, 0);
		button.setAlignment(Pos.BASELINE_RIGHT);

		boxItems.add(pane);
		ObservableList<GridPane> items = FXCollections.observableArrayList(boxItems);
		listView.setItems(items);

		PatternGroup patternGroup = new PatternGroup(this, 3, patternGroups.size());
		patternGroups.add(patternGroup);

		if (patternGroups.size() > 1)
			patternPropertyMenu.getItemDisjunct().fire();
	}

	private void removePane(int index) {
		patternGroups.remove(index);
	}

	void removeEntry(int index) {

		boxItems.remove(index);
		ObservableList<GridPane> items = FXCollections.observableArrayList(boxItems);
		listView.setItems(items);

		removePane(index);

		if (index == selectedIndex)
			select(0);

		for (int i = 0; i < boxItems.size(); i++) {
			boxItems.get(i).getChildren().remove(1);
			boxItems.get(i).add(new PatternCloseButton(this, i), 1, 0);
		}

		if (patternGroups.size() == 1) {
			unselectAllMenus(patternPropertyMenu.getItemDisjunct(), patternPropertyMenu.getItemUndisjunct(), patternPropertyMenu.getItemNNDisjunct());
		}
	}

	int getNbItems() {
		return boxItems.size();
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

		PatternGroup newPatternGroup = new PatternGroup(this, nbCrowns, selectedPatternGroup.getIndex());

		PatternHexagon[][] hexagonMatrix = selectedPatternGroup.getHexagonsMatrix();

		for (int i = 0; i < hexagonMatrix.length; i++) {
			for (int j = 0; j < hexagonMatrix.length; j++) {
				if (hexagonMatrix[i][j] != null) {
					newPatternGroup.getHexagonsMatrix()[i + 1][j + 1].setLabel(hexagonMatrix[i][j].getLabel());
				}
			}
		}

		patternGroups.set(newPatternGroup.getIndex(), newPatternGroup);
		borderPane.getChildren().remove(selectedPatternGroup);
		select(newPatternGroup.getIndex());
	}

	private void removeCrown() {

		int nbCrowns = selectedPatternGroup.getNbCrowns();

		if (nbCrowns > 2) {

			nbCrowns--;
			PatternGroup newPatternGroup = new PatternGroup(this, nbCrowns, selectedPatternGroup.getIndex());

			PatternHexagon[][] hexagonMatrix = selectedPatternGroup.getHexagonsMatrix();
			PatternHexagon[][] newHexagonMatrix = newPatternGroup.getHexagonsMatrix();

			for (int i = 1; i < hexagonMatrix.length; i++) {
				for (int j = 1; j < hexagonMatrix.length; j++) {
					if (i - 1 < newHexagonMatrix.length && j - 1 < newHexagonMatrix.length
							&& newHexagonMatrix[i - 1][j - 1] != null)
						newHexagonMatrix[i - 1][j - 1].setLabel(hexagonMatrix[i][j].getLabel());
				}
			}

			patternGroups.set(newPatternGroup.getIndex(), newPatternGroup);
			borderPane.getChildren().remove(selectedPatternGroup);
			select(newPatternGroup.getIndex());
		}
	}

	private static Pattern buildPattern(PatternGroup group) {
		return group.exportPattern();
	}

	private void unselectAllMenus(CheckMenuItem... items) {

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
}
