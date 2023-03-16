package view.filtering.patterns;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.mysql.cj.x.protobuf.MysqlxExpr.Operator;

import database.BenzenoidCriterion.Subject;
import generator.GeneratorCriterion;
import generator.patterns.Pattern;
import generator.patterns.PatternGenerationType;
import generator.patterns.PatternResolutionInformations;
import generator.patterns.PatternsInterraction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import modelProperty.expression.BinaryNumericalExpression;
import modelProperty.expression.PropertyExpression;
import modelProperty.expression.SubjectExpression;
import molecules.Node;
import utils.Utils;
import view.filtering.boxes.HBoxPatternFilteringCriterion;

public class PatternsEditionPane extends BorderPane {

	private HBoxPatternFilteringCriterion parent;
	private MenuBar menuBar;
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

	public PatternsEditionPane(HBoxPatternFilteringCriterion parent) {
		super();
		this.parent = parent;
		initialize();
	}

	private void initialize() {
		initializeMenu();
		initializePane();
	}

	private void initializeMenu() {

		menuBar = new MenuBar();

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
					pattern = Pattern.importPattern(file);
				} catch (Exception e1) {
					e1.printStackTrace();
					ok = false;
				}

				if (ok && pattern != null) {

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

		Menu drawingMenu = new Menu("Drawing");
		MenuItem clearItem = new MenuItem("Clear");
		MenuItem neutralItem = new MenuItem("Set all neutral");
		MenuItem positiveItem = new MenuItem("Set all positive");
		MenuItem negativeItem = new MenuItem("Set all negative");
		drawingMenu.getItems().addAll(clearItem, neutralItem, positiveItem, negativeItem);
		menuBar.getMenus().add(drawingMenu);

		clearItem.setOnAction(e -> {
			selectedPatternGroup.setAllLabels(0);
		});

		neutralItem.setOnAction(e -> {
			selectedPatternGroup.setAllLabels(1);
		});

		positiveItem.setOnAction(e -> {
			selectedPatternGroup.setAllLabels(2);
		});

		negativeItem.setOnAction(e -> {
			selectedPatternGroup.setAllLabels(3);
		});

		Label labelName = new Label("Name: ");
		fieldName = new TextField("default name");
		HBox boxName = new HBox(3.0);
		boxName.getChildren().addAll(labelName, fieldName);
		Menu nameMenu = new Menu();
		nameMenu.setGraphic(boxName);
		menuBar.getMenus().add(nameMenu);

		Menu nbCrownsMenu = new Menu();
		HBox nbCrownsBox = new HBox(3.0);
		Label nbCrownsLabel = new Label("Number of crowns: ");
		Button minusButton = new Button("-");
		Button plusButton = new Button("+");
		nbCrownsBox.getChildren().addAll(nbCrownsLabel, minusButton, plusButton);
		nbCrownsMenu.setGraphic(nbCrownsBox);
		menuBar.getMenus().add(nbCrownsMenu);

		fieldName.setOnKeyReleased(e -> {

			int index = selectedPatternGroup.getIndex();
			GridPane gridPane = boxItems.get(index);

			if (!fieldName.getText().equals(""))
				((Label) gridPane.getChildren().get(0)).setText(fieldName.getText());
			else
				((Label) gridPane.getChildren().get(0)).setText("default name");
		});

		minusButton.setOnAction(e -> {
			removeCrown();
		});

		plusButton.setOnAction(e -> {
			addCrown();
		});

		Menu patternMenu = new Menu("Patterns properties");
		Menu multipleMenu = new Menu("Multiple patterns interaction");
		itemUndisjunct = new CheckMenuItem("Undisjunct patterns");
		itemDisjunct = new CheckMenuItem("Disjunct patterns");
		itemNNDisjunct = new CheckMenuItem("Disjunct on neutral/negative hexagons");
		multipleMenu.getItems().addAll(itemUndisjunct, itemDisjunct, itemNNDisjunct);
		disableItem = new CheckMenuItem("Exclude pattern");
		// CheckMenuItem occurencesItem = new CheckMenuItem();
		Label occurencesLabel = new Label("Pattern's occurences: ");
		occurencesField = new TextField();
		// HBox occurencesBox = new HBox(3.0);
		// occurencesBox.getChildren().addAll(occurencesLabel, occurencesField);
		// occurencesItem.setGraphic(occurencesBox);
		patternMenu.getItems().addAll(multipleMenu, disableItem);
		menuBar.getMenus().add(patternMenu);

		itemNNDisjunct.setSelected(true);

		itemUndisjunct.setOnAction(e -> {
			itemUndisjunct.setSelected(true);
			if (itemUndisjunct.isSelected())
				unselectAllMenus(itemDisjunct, itemNNDisjunct, disableItem);
		});

		itemDisjunct.setOnAction(e -> {
			itemDisjunct.setSelected(true);
			if (itemDisjunct.isSelected())
				unselectAllMenus(itemUndisjunct, itemNNDisjunct, disableItem);
		});

		itemNNDisjunct.setOnAction(e -> {
			itemNNDisjunct.setSelected(true);
			if (itemNNDisjunct.isSelected())
				unselectAllMenus(itemUndisjunct, itemDisjunct, disableItem);
		});

		disableItem.setOnAction(e -> {
			if (disableItem.isSelected())
				unselectAllMenus(itemUndisjunct, itemDisjunct, itemNNDisjunct);
		});

		this.setTop(menuBar);
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

			PatternGenerationType type = null;
			PropertyExpression expression = null;

			if (boxItems.size() == 1) {

				if (!disableItem.isSelected()) {
					if (!Utils.isNumber(occurencesField.getText())) {
						type = PatternGenerationType.SINGLE_PATTERN_1;
						parent.refreshPatternInformations("SINGLE_PATTERN");
						expression = new SubjectExpression("SINGLE_PATTERN");
					}

					else {
						type = PatternGenerationType.PATTERN_OCCURENCES;
						parent.refreshPatternInformations("OCCURENCES_PATTERN: " + occurencesField.getText());
						expression = new BinaryNumericalExpression("OCCURENCE_PATTERN", "=",
								Integer.parseInt(occurencesField.getText()));
					}
				}

				else {
					parent.refreshPatternInformations("FORBIDDEN_PATTERN");
					type = PatternGenerationType.FORBIDDEN_PATTERN;
					expression = new SubjectExpression("FORBIDDEN_PATTERN");
				}
			}

			else if (itemUndisjunct.isSelected() || itemDisjunct.isSelected() || itemNNDisjunct.isSelected()) {
				parent.refreshPatternInformations("MULTIPLE_PATTERNS");
				type = PatternGenerationType.MULTIPLE_PATTERN_1;
				expression = new SubjectExpression("MULTIPLE_PATTERNS");
			}

			PatternResolutionInformations patternInformations = new PatternResolutionInformations(type, patterns);
			parent.setPatternResolutionInformations(patternInformations);
			parent.setExpression(expression);

			if (itemUndisjunct.isSelected())
				patternInformations.setInterraction(PatternsInterraction.UNDISJUNCT);

			else if (itemDisjunct.isSelected())
				patternInformations.setInterraction(PatternsInterraction.DISJUNCT);

			else if (itemNNDisjunct.isSelected())
				patternInformations.setInterraction(PatternsInterraction.DISJUNCT_NN);

			hide();

		});

		addButton.setOnAction(e -> {
			addEntry();
		});

		VBox vBox = new VBox(5.0);
		vBox.getChildren().addAll(listView, buttonBox);
		vBox.setPrefHeight(this.getHeight());

		listView.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				GridPane selection = listView.getSelectionModel().getSelectedItem();
				if (selection != null) {
					PatternCloseButton button = (PatternCloseButton) selection.getChildren().get(1);
					select(button.getIndex());
				}
			}
		});

		addEntry();
		select(0);

		borderPane.setCenter(selectedPatternGroup);
		borderPane.setRight(vBox);
		this.setCenter(borderPane);
	}

	public void addEntry() {

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

	public void removePane(int index) {
		patternGroups.remove(index);
	}

	public void removeEntry(int index) {

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

	public int getNbItems() {
		return boxItems.size();
	}

	public void checkBorder() {
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

	public void addCrown() {
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

	public void removeCrown() {

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

	public static Pattern buildPattern(PatternGroup group) {
		return group.exportPattern();
	}

	private void unselectAllMenus(CheckMenuItem... items) {

		for (CheckMenuItem item : items) {
			item.setSelected(false);
		}
	}

	private void hide() {
		parent.hidePatternStage();
	}
}
