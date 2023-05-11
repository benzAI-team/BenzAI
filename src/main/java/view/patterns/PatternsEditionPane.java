package view.patterns;

import constraints.ForbiddenPatternConstraint1;
import constraints.MultiplePatterns1Constraint;
import constraints.SinglePattern2Constraint;
import generator.OrderStrategy;
import generator.ValueStrategy;
import generator.VariableStrategy;
import generator.patterns.*;
import generator.properties.model.ModelPropertySet;
import generator.properties.model.expression.PatternExpression;
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
import molecules.Node;
import utils.Utils;
import view.generator.boxes.HBoxPatternCriterion;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PatternsEditionPane extends BorderPane {

	private final HBoxPatternCriterion parent;
	private BorderPane borderPane;
	private ListView<GridPane> listView;
	private ArrayList<GridPane> boxItems;
	private ArrayList<PatternGroup> patternGroups;

	private PatternGroup selectedPatternGroup;
	private int selectedIndex;

	private CheckMenuItem itemUndisjunct;
	private CheckMenuItem itemDisjunct;
	private CheckMenuItem itemNNDisjunct;
	private CheckMenuItem disableItem;
	private TextField occurencesField;
	private TextField fieldName;
	private int colorLabel;   // the label of the last color assign to a hexagon

	public PatternsEditionPane(HBoxPatternCriterion parent) {
		super();
		this.parent = parent;
		initialize();
		colorLabel = 0;
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
		Menu patternMenu = new Menu("Patterns properties");
		Menu multipleMenu = buildMultipleMenu();
		buildDisableItem();
		//buildOccurenceItem(); TODO to complete
		patternMenu.getItems().addAll(multipleMenu, disableItem);
		menuBar.getMenus().add(patternMenu);
	}

	private void buildDisableItem() {
		disableItem = new CheckMenuItem("Exclude pattern");
		disableItem.setOnAction(e -> {
			if (disableItem.isSelected())
				unselectAllMenus(itemUndisjunct, itemDisjunct, itemNNDisjunct);
		});
	}

	private void buildOccurenceItem() { // TODO to complete
		//CheckMenuItem occurencesItem = new CheckMenuItem();
		Label occurencesLabel = new Label("Pattern's occurences: ");
		occurencesField = new TextField();
		//HBox occurencesBox = new HBox(3.0);
		//occurencesBox.getChildren().addAll(occurencesLabel, occurencesField);
		//occurencesItem.setGraphic(occurencesBox);
	}

	private Menu buildMultipleMenu() {
		Menu multipleMenu = new Menu("Multiple patterns interaction");
		buildItemUndisjunct();
		buildItemDisjunct();
		buildItemNNDisjunct();
		multipleMenu.getItems().addAll(itemUndisjunct, itemDisjunct, itemNNDisjunct);
		return multipleMenu;
	}

	private void buildItemNNDisjunct() {
		itemNNDisjunct = new CheckMenuItem("Disjunct on neutral/negative hexagons");
		itemNNDisjunct.setOnAction(e -> {
			itemNNDisjunct.setSelected(true);
			if (itemNNDisjunct.isSelected())
				unselectAllMenus(itemUndisjunct, itemDisjunct, disableItem);
		});
		itemNNDisjunct.setSelected(true);
	}

	private void buildItemDisjunct() {
		itemDisjunct = new CheckMenuItem("Disjunct patterns");
		itemDisjunct.setOnAction(e -> {
			itemDisjunct.setSelected(true);
			if (itemDisjunct.isSelected())
				unselectAllMenus(itemUndisjunct, itemNNDisjunct, disableItem);
		});
	}

	private void buildItemUndisjunct() {
		itemUndisjunct = new CheckMenuItem("Undisjunct patterns");
		itemUndisjunct.setOnAction(e -> {
			itemUndisjunct.setSelected(true);
			if (itemUndisjunct.isSelected())
				unselectAllMenus(itemDisjunct, itemNNDisjunct, disableItem);
		});
	}

	private void buildCrownMenu(MenuBar menuBar) {
		Menu nbCrownsMenu = new Menu();
		HBox nbCrownsBox = new HBox(3.0);
		Label nbCrownsLabel = new Label("Number of crowns: ");
		Button minusButton = new Button("-");
		minusButton.setOnAction(e -> removeCrown());
		Button plusButton = new Button("+");
		plusButton.setOnAction(e -> addCrown());
		nbCrownsBox.getChildren().addAll(nbCrownsLabel, minusButton, plusButton);
		nbCrownsMenu.setGraphic(nbCrownsBox);
		menuBar.getMenus().add(nbCrownsMenu);
	}

	private void buildNameMenu(MenuBar menuBar) {
		Label labelName = new Label("Name: ");
		fieldName = new TextField("default name");
		HBox boxName = new HBox(3.0);
		boxName.getChildren().addAll(labelName, fieldName);
		Menu nameMenu = new Menu();
		nameMenu.setGraphic(boxName);
		menuBar.getMenus().add(nameMenu);
		fieldName.setOnKeyReleased(e -> {

			int index = selectedPatternGroup.getIndex();
			GridPane gridPane = boxItems.get(index);

			if ("".equals(fieldName.getText()))
				((Label) gridPane.getChildren().get(0)).setText("default name");
			else
				((Label) gridPane.getChildren().get(0)).setText(fieldName.getText());
		});
	}

	private void buildDrawingMenu(MenuBar menuBar) {
		Menu drawingMenu = new Menu("Drawing");
		MenuItem clearItem = new MenuItem("Clear");
		MenuItem neutralItem = new MenuItem("Set all neutral");
		MenuItem positiveItem = new MenuItem("Set all positive");
		MenuItem negativeItem = new MenuItem("Set all negative");
		drawingMenu.getItems().addAll(clearItem, neutralItem, positiveItem, negativeItem);
		menuBar.getMenus().add(drawingMenu);

		clearItem.setOnAction(e -> selectedPatternGroup.setAllLabels(0));

		neutralItem.setOnAction(e -> selectedPatternGroup.setAllLabels(1));

		positiveItem.setOnAction(e -> selectedPatternGroup.setAllLabels(2));

		negativeItem.setOnAction(e -> selectedPatternGroup.setAllLabels(3));
	}

	private void buildFileMenu(MenuBar menuBar) {
		Menu fileMenu = new Menu("File");
		MenuItem importItem = new MenuItem("Import pattern");
		MenuItem saveItem = new MenuItem("Save pattern as");
		fileMenu.getItems().addAll(importItem, saveItem);
		menuBar.getMenus().add(fileMenu);

		saveItem.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();
			File file = fileChooser.showSaveDialog(parent.getApplication().getStage());

			if (file != null) {
				Pattern pattern = selectedPatternGroup.exportPattern();
				try {
					pattern.export(file);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		importItem.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();
			File file = fileChooser.showOpenDialog(parent.getApplication().getStage());

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
		borderPane = new BorderPane();

		listView = new ListView<>();
		boxItems = new ArrayList<>();

		Button addButton = new Button("Add pattern");
		Button applyButton = new Button("Apply");
		HBox buttonBox = new HBox(3.0);
		buttonBox.getChildren().addAll(addButton, applyButton);

		applyButton.setPrefWidth(125);
		addButton.setPrefWidth(125);

		applyButton.setOnAction(e -> {
			ArrayList<Pattern> patterns = new ArrayList<>();
			for (PatternGroup group : patternGroups) {
				patterns.add(buildPattern(group));
			}

			PatternGenerationType type;
			String subject = "";
			PatternResolutionInformations patternInformations = null; 
			
			if (boxItems.size() == 1) {

				if (!disableItem.isSelected()) {
					if (!Utils.isNumber(occurencesField.getText())) {
						type = PatternGenerationType.SINGLE_PATTERN_1;
						subject = "SINGLE_PATTERN";
						patternInformations = new PatternResolutionInformations(type, patterns);
						parent.getPatternProperty().setConstraint(new SinglePattern2Constraint(patternInformations.getPatterns().get(0), false,
								VariableStrategy.FIRST_FAIL, ValueStrategy.INT_MAX, OrderStrategy.CHANNELING_FIRST));
					}

					else {
						type = PatternGenerationType.PATTERN_OCCURENCES;
						subject = "OCCURENCES_PATTERN: " + occurencesField.getText();
//TODO
						//expression = new BinaryNumericalExpression("OCCURENCE_PATTERN", "=", Integer.parseInt(occurencesField.getText()));
					}
				}

				else {
					type = PatternGenerationType.FORBIDDEN_PATTERN;
					subject = "FORBIDDEN_PATTERN";
					patternInformations = new PatternResolutionInformations(type, patterns);
					parent.getPatternProperty().setConstraint(new ForbiddenPatternConstraint1(patternInformations.getPatterns().get(0),
							VariableStrategy.FIRST_FAIL, ValueStrategy.INT_MAX, OrderStrategy.CHANNELING_FIRST));
				}
			}

			else if (itemUndisjunct.isSelected() || itemDisjunct.isSelected() || itemNNDisjunct.isSelected()) {
				type = PatternGenerationType.MULTIPLE_PATTERN_1;
				subject = "MULTIPLE_PATTERNS";
				patternInformations = new PatternResolutionInformations(type, patterns);
				if (itemUndisjunct.isSelected()) 
					patternInformations.setInterraction(PatternsInterraction.UNDISJUNCT);
				else if (itemDisjunct.isSelected())
					patternInformations.setInterraction(PatternsInterraction.DISJUNCT);
				else if (itemNNDisjunct.isSelected())
					patternInformations.setInterraction(PatternsInterraction.DISJUNCT_NN);

				parent.getPatternProperty().setConstraint(new MultiplePatterns1Constraint(patternInformations.getPatterns(),
						VariableStrategy.FIRST_FAIL, ValueStrategy.INT_MAX, OrderStrategy.CHANNELING_FIRST, patternInformations.getInterraction()));
			}

			parent.setPatternResolutionInformations(patternInformations);
			parent.setExpression(new PatternExpression(subject, patternInformations));

			
			
			parent.addPropertyExpression((ModelPropertySet) parent.getPatternProperty().getPropertySet());
			parent.refreshPatternInformations(subject);
			hide();

		});

		addButton.setOnAction(e -> addEntry());

		VBox vBox = new VBox(5.0);
		vBox.getChildren().addAll(listView, buttonBox);
		vBox.setPrefHeight(this.getHeight());

		listView.setOnMouseClicked(event -> {
			GridPane selection = listView.getSelectionModel().getSelectedItem();
			if (selection != null) {
				PatternCloseButton button = (PatternCloseButton) selection.getChildren().get(1);
				select(button.getIndex());
			}
		});

		addEntry();
		select(0);

		borderPane.setCenter(selectedPatternGroup);
		borderPane.setRight(vBox);
		this.setCenter(borderPane);
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
			itemDisjunct.fire();

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
			unselectAllMenus(itemDisjunct, itemUndisjunct, itemNNDisjunct);
		}
	}

	int getNbItems() {
		return boxItems.size();
	}

	void checkBorder() {
		boolean borderUsed = false;
		for (PatternHexagon hexagon : selectedPatternGroup.getExtendedBorder()) {
			if (hexagon.getLabel() >= 1) {
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

  void setColorLabel(int label) {
    colorLabel = label;
  }
  
  int getColorLabel() {
    return (colorLabel);
  }

	private void hide() {
		parent.hidePatternStage();
	}
}
