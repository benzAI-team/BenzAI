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
import java.util.ArrayList;
import java.util.Optional;

public class PatternsEditionPane extends BorderPane {

	private final HBoxPatternCriterion patternConstraintHBox;
	private BorderPane borderPane;
	private PatternGroup selectedPatternGroup;
	private TextField fieldName;
	private PatternLabel lastLabel;   // the label of the last color assign to a hexagon
	private PatternListBox patternListBox;
	private PropertyListBox propertyListBox;
	private InteractionListBox interactionListBox;

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
					propertyListBox.addEntry(new PatternPropertyExistence(newPattern));

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
		propertyListBox = new PropertyListBox(this);
		interactionListBox = new InteractionListBox(this);

		borderPane = new BorderPane();
		borderPane.setCenter(selectedPatternGroup);

		VBox rightPanel = new VBox (5);
		Separator sep1 = new Separator();
		sep1.setStyle("-fx-background-color: #0000ff;");
		Separator sep2 = new Separator();
		sep2.setStyle("-fx-background-color: #0000ff;");
		rightPanel.getChildren().addAll(patternListBox, sep1, propertyListBox, interactionListBox, sep2,buildApplyButton());
		borderPane.setRight(rightPanel);
		this.setCenter(borderPane);

		PatternGroup newPattern = new PatternGroup(this, 3, null);
		patternListBox.addEntry(newPattern);
		propertyListBox.addEntry(new PatternPropertyExistence(newPattern));
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
			patternConstraintHBox.reset();
			for (PatternProperty type : propertyListBox.getPatternProperties()) {
				type.addConstraint(patternConstraintHBox);
			}

			for (InteractionItem type : interactionListBox.getInteractions()) {
				type.addInteraction(patternConstraintHBox);
			}

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

	Optional<PatternProperty> getPropertyDialogBox (int index) {
		// we take into account the current property
		PatternProperty property = PropertyListBox.getPatternProperties().get(index);
		int type = property.getType();

		// we create the dialog box
		Dialog<PatternProperty> dialog = new Dialog<>();
		dialog.setTitle("Property");
		dialog.setHeaderText("Select the desired property for "+property.getPattern().getLabel().getText());

		// we create the property list
		ArrayList<String> propertyList = new ArrayList<>();
		propertyList.add("Existence");
		propertyList.add("Exclusion");
		propertyList.add("Occurrence");
		propertyList.add("Occurrence with no positive hexagon sharing");
		propertyList.add("Occurrence with no positive edge sharing");
		propertyList.add("Occurrence with no hexagon sharing");

		// we create the combo box for property
		ComboBox propertyBox = new ComboBox();
		propertyBox.getItems().addAll(propertyList);


		if (type == 2) {
			type += ((PatternPropertyOccurrence) property).getInteraction().getType();
		}

		propertyBox.getSelectionModel().select(propertyList.get(type));

		// we define the buttons of the dialog box
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		// we create the form as a grid
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

		TextField minOccurrenceField = new TextField();
		TextField maxOccurrenceField = new TextField();
		if (property.getType() > 1) {
			minOccurrenceField.setText(Integer.toString(((PatternPropertyOccurrence) property).getMinOccurrence()));
			maxOccurrenceField.setText(Integer.toString(((PatternPropertyOccurrence) property).getMaxOccurrence()));
		}
		else {
			minOccurrenceField.setPromptText("min");
			maxOccurrenceField.setPromptText("max");
		}
		HBox occurrenceBox = new HBox();
		occurrenceBox.getChildren().addAll (minOccurrenceField, maxOccurrenceField);
		minOccurrenceField.setPrefWidth(100);
		maxOccurrenceField.setPrefWidth(100);
		grid.add(new Label("Property:"), 0, 0);
		grid.add(propertyBox,1,0);
		grid.add(new Label("# occurrences"), 0, 1);
		grid.add(occurrenceBox, 1, 1);

		dialog.getDialogPane().setContent(grid);

		minOccurrenceField.setDisable(propertyBox.getSelectionModel().getSelectedIndex() <= 1 || propertyBox.getSelectionModel().getSelectedIndex() >= 6);
		maxOccurrenceField.setDisable(propertyBox.getSelectionModel().getSelectedIndex() <= 1 || propertyBox.getSelectionModel().getSelectedIndex() >= 6);

		// event management
		propertyBox.setOnAction(event -> {
			minOccurrenceField.setDisable(propertyBox.getSelectionModel().getSelectedIndex() <= 1 || propertyBox.getSelectionModel().getSelectedIndex() >= 6);
			maxOccurrenceField.setDisable(propertyBox.getSelectionModel().getSelectedIndex() <= 1 || propertyBox.getSelectionModel().getSelectedIndex() >= 6);
		});

		// computes the result
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == ButtonType.OK) {
				int propertyNum = propertyBox.getSelectionModel().getSelectedIndex();

				PatternGroup pattern1 = property.getPattern();
				PatternProperty newProperty = null;

				switch (propertyNum) {
					case 0: newProperty = new PatternPropertyExistence(pattern1); break;
					case 1: newProperty = new PatternPropertyExclusion(pattern1); break;
					case 2: newProperty = new PatternPropertyOccurrence(pattern1, new NoInteraction(), Integer.valueOf(minOccurrenceField.getText()), Integer.valueOf(maxOccurrenceField.getText())); break;
					case 3: newProperty = new PatternPropertyOccurrence(pattern1, new NoPositiveInteraction(), Integer.valueOf(minOccurrenceField.getText()), Integer.valueOf(maxOccurrenceField.getText())); break;
					case 4: newProperty = new PatternPropertyOccurrence(pattern1, new NoInteraction(), Integer.valueOf(minOccurrenceField.getText()), Integer.valueOf(maxOccurrenceField.getText())); break;
					case 5: newProperty = new PatternPropertyOccurrence(pattern1, new NoHexagonInteraction(), Integer.valueOf(minOccurrenceField.getText()), Integer.valueOf(maxOccurrenceField.getText())); break;
				}

				return newProperty;
			}
			return null;
		});

		return dialog.showAndWait();
	}

	Optional<InteractionItem> getInteractionDialogBox (int index) {
		Dialog<InteractionItem> dialog = new Dialog<>();
		dialog.setTitle("Interaction");
		dialog.setHeaderText("Select the desired interaction");

		// we create the property list
		ArrayList<String> interactionList = new ArrayList<>();

		interactionList.add("Interaction with no positive hexagon sharing");
		interactionList.add("Interaction with no positive edge sharing");
		interactionList.add("Interaction with no hexagon sharing");

		// we create the combo box for property
		ComboBox interactionBox = new ComboBox();
		interactionBox.getItems().addAll(interactionList);

		// we create the combo box for possible patterns
		ComboBox patternBox = new ComboBox();
		ComboBox patternBox2 = new ComboBox();
		ArrayList<String> patternList = new ArrayList<>();
		for (PatternGroup pattern : patternListBox.getPatternGroups()) {
			patternList.add(pattern.getLabel().getText());
		}
		patternBox.getItems().addAll(patternList);
		patternBox2.getItems().addAll(patternList);


		// we take into account the current property (if any)
		if (index != -1) {
			InteractionItem item = InteractionListBox.getInteractions().get(index);
			int type = item.getInteraction().getType();
			interactionBox.getSelectionModel().select(interactionList.get(type));

			patternBox.getSelectionModel().select(item.getPatternProperty1().getPattern().getLabel().getText());
			patternBox2.setValue(item.getPatternProperty2().getPattern().getLabel().getText());
		}

		// we define the buttons of the dialog box
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		// we create the form as a grid
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

		grid.add(new Label("Property:"), 0, 0);
		grid.add(interactionBox,1,0);
		grid.add(new Label("Pattern"), 0, 1);
		grid.add(patternBox, 1, 1);
		grid.add(new Label("Pattern 2"), 0, 2);
		grid.add(patternBox2, 1, 2);

		dialog.getDialogPane().setContent(grid);

		// computes the result
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == ButtonType.OK) {
				int num = interactionBox.getSelectionModel().getSelectedIndex();

				InteractionItem item = null;

				if (num != -1) {
					PatternProperty pattern1 = getPropertyListBox().getPatternProperties().get(patternBox.getSelectionModel().getSelectedIndex());
					PatternProperty pattern2 = getPropertyListBox().getPatternProperties().get(patternBox2.getSelectionModel().getSelectedIndex());;

					switch (num) {
						case 0: item = new InteractionItem(pattern1, pattern2, new NoPositiveInteraction()); break;
//						case 1: item = new InteractionItem(pattern1, pattern2, new NoPositiveInteraction()); break;
						case 2: item = new InteractionItem(pattern1, pattern2, new NoHexagonInteraction()); break;
					}
				}
				return item;
			}
			return null;
		});

		return dialog.showAndWait();
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

	public PropertyListBox getPropertyListBox() {
		return propertyListBox;
	}

	public InteractionListBox getInteractionListBox() {
		return interactionListBox;
	}
}
