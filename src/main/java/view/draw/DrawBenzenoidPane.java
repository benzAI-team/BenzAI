package view.draw;

import application.BenzenoidApplication;
import generator.patterns.PatternLabel;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import benzenoid.Benzenoid;
import benzenoid.BenzenoidParser;
import parsers.CMLConverter;
import parsers.ComConverter;
import parsers.ComConverter.ComType;
import parsers.GraphParser;
import utils.Couple;
import utils.Utils;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionPane.DisplayType;
import view.collections.BenzenoidCollectionsManagerPane;
import view.collections.CollectionMenuItem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class DrawBenzenoidPane extends BorderPane {

	private enum ExportType {
		GRAPH, CML, COM
	}

	private final BenzenoidApplication application;
	private final BenzenoidCollectionsManagerPane collectionsPane;
	
	private MenuBar menuBar;
	private HBox buttonBar;
	private BorderPane borderPane;

	private MoleculeGroup moleculeGroup;
	private TextField nameField;

	private int nbCrowns;

	public DrawBenzenoidPane(BenzenoidApplication application, BenzenoidCollectionsManagerPane collectionsPane) throws Exception, IOException {

		this.application = application;
		this.collectionsPane = collectionsPane;
		initialize();
	}

	private void initialize() throws Exception, IOException {

		nbCrowns = 3;

		refreshMenuBar();

		this.setTop(menuBar);

		moleculeGroup = new MoleculeGroup(nbCrowns, this);
		moleculeGroup.resize(500, 500);

		borderPane = new BorderPane();
		borderPane.setTop(buttonBar);
		borderPane.setCenter(moleculeGroup);

		this.setCenter(borderPane);

	}

	public void refreshMenuBar() throws IOException {

		menuBar = new MenuBar();

		Menu fileMenu = new Menu("File");
		MenuItem openItem = new MenuItem("Open benzenoid");
		Menu saveItem = new Menu("Save as");
		MenuItem graphItem = new MenuItem(".graph");
		MenuItem cmlItem = new MenuItem(".cml");
		MenuItem comItem = new MenuItem(".com");

		openItem.setOnAction(e -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Import benzenoid");
			File file = fileChooser.showOpenDialog(application.getStage());

			if (file != null) {
				Benzenoid molecule = GraphParser.parseUndirectedGraph(file);
				importBenzenoid(molecule);
			}
		});

		graphItem.setOnAction(e -> {
			export(ExportType.GRAPH);
		});

		cmlItem.setOnAction(e -> {
			export(ExportType.CML);
		});

		comItem.setOnAction(e -> {
			export(ExportType.COM);
		});

		Menu clearItem = new Menu();
		Label clearLabel = new Label("Clear");
		clearLabel.setOnMouseClicked(e -> {
			for (HexagonDraw hexagon : moleculeGroup.getHexagons())
				hexagon.setLabel(PatternLabel.VOID);
		});
		clearItem.setGraphic(clearLabel);

		Menu selectAllItem = new Menu();
		Label selectAllLabel = new Label("Select all");
		selectAllLabel.setOnMouseClicked(e -> {
			for (HexagonDraw hexagon : moleculeGroup.getHexagons())
				hexagon.setLabel(PatternLabel.NEUTRAL);
		});
		selectAllItem.setGraphic(selectAllLabel);

		Menu revertItem = new Menu();
		Label revertLabel = new Label("Reverse");
		revertLabel.setOnMouseClicked(e -> {
			for (HexagonDraw hexagon : moleculeGroup.getHexagons())
				if(hexagon.getLabel() == PatternLabel.POSITIVE)
					hexagon.setLabel(PatternLabel.NEGATIVE);
				else if(hexagon.getLabel() == PatternLabel.NEGATIVE)
					hexagon.setLabel(PatternLabel.POSITIVE);
		});
		revertItem.setGraphic(revertLabel);

		fileMenu.getItems().addAll(openItem, saveItem);
		saveItem.getItems().addAll(graphItem, cmlItem, comItem);

		Menu addMenu = new Menu("Add to collection");

		ArrayList<BenzenoidCollectionPane> collectionPanes = collectionsPane.getBenzenoidSetPanes();

		for (int i = 0; i < collectionPanes.size() - 1; i++) {

			BenzenoidCollectionPane collectionPane = collectionPanes.get(i);
			CollectionMenuItem menuItem = new CollectionMenuItem(i, collectionPane.getName());

			menuItem.setOnAction(e -> {

				Benzenoid molecule = null;

				try {
					molecule = moleculeGroup.exportMolecule();
				} catch (Exception e1) {
					molecule = null;
					Utils.alert("Invalid draw");
				}

				if (molecule != null) {
					molecule.setDescription(nameField.getText());
					
					collectionPane.unselectAll();

          try {
            molecule = GraphParser.parseBenzenoidCode(molecule.getNames().get(0));
          }
          catch(IOException ex) {
            ex.printStackTrace();
          }
					collectionPane.addBenzenoid(molecule, DisplayType.BASIC);
					collectionPane.refresh();

					Utils.showAlertWithoutHeaderText("Molecule added to collection: " + collectionPane.getName());

				}
			});

			addMenu.getItems().add(menuItem);
		}

		MenuItem newCollectionItem = new MenuItem("New collection");

		newCollectionItem.setOnAction(e -> {
			Benzenoid molecule = moleculeGroup.exportMolecule();
			molecule.setDescription(nameField.getText());
			AddToNewCollectionPane addPane = new AddToNewCollectionPane(application.getBenzenoidCollectionsPane(),
					molecule);

			Stage stage = new Stage();
			stage.setTitle("Add to new collection");

			stage.setResizable(false);

			Scene scene = new Scene(addPane);
			scene.getStylesheets().add("/resources/style/application.css");

			stage.setScene(scene);
			stage.show();
		});

		addMenu.getItems().add(newCollectionItem);

		Menu nbCrownsMenu = new Menu();
		HBox nbCrownsBox = new HBox(3.0);
		Label nbCrownsLabel = new Label("Number of crowns: ");
		Button minusButton = new Button("-");
		Button plusButton = new Button("+");
		nbCrownsBox.getChildren().addAll(nbCrownsLabel, minusButton, plusButton);

		nbCrownsMenu.setGraphic(nbCrownsBox);

		minusButton.setOnAction(e -> {
			removeCrown();
		});

		plusButton.setOnAction(e -> {
			addCrown();
		});

		Menu nameMenu = new Menu();
		HBox nameBox = new HBox(3.0);
		Label nameLabel = new Label("Name: ");
		nameField = new TextField();
		nameBox.getChildren().addAll(nameLabel, nameField);
		nameMenu.setGraphic(nameBox);

		menuBar.getMenus().addAll(fileMenu, addMenu, clearItem, selectAllItem, revertItem, nbCrownsMenu, nameMenu);

		this.setTop(menuBar);
	}

	public void importBenzenoid(Benzenoid molecule) {

		Couple<Integer, Integer>[] coords = molecule.getHexagonsCoords();

		int xShift = 0;
		int yShift = 0;

		for (Couple<Integer, Integer> coord : coords) {
			if (coord.getX() < xShift)
				xShift = coord.getX();
			if (coord.getY() < yShift)
				yShift = coord.getY();
		}

		if (xShift < 0) {
			xShift = xShift * -1;

			for (Couple<Integer, Integer> coord : coords)
				coord.setX(coord.getX() + xShift);
		}

		if (yShift < 0) {

			yShift = yShift * -1;

			for (Couple<Integer, Integer> coord : coords)
				coord.setY(coord.getY() + yShift);
		}

		nbCrowns = 2;
		boolean isEmbedded = false;

		while (!isEmbedded) {
			isEmbedded = true;

			for (Couple<Integer, Integer> coord : coords) {
				if (coord.getX() >= nbCrowns || coord.getY() >= nbCrowns) {
					isEmbedded = false;
					break;
				}
			}

			if (isEmbedded)
				System.out.print("");

			nbCrowns++;
		}

		MoleculeGroup newMoleculeGroup = new MoleculeGroup(nbCrowns, this);

		for (Couple<Integer, Integer> coord : coords)
			newMoleculeGroup.getHexagonsMatrix()[coord.getX()][coord.getY()].setLabel(PatternLabel.NEUTRAL);

		borderPane.getChildren().remove(moleculeGroup);
		moleculeGroup = newMoleculeGroup;

		borderPane.setCenter(moleculeGroup);
	}

	private void export(ExportType type) {

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save");
		File file = fileChooser.showSaveDialog(application.getStage());

		if (file != null) {

			Benzenoid benzenoid = moleculeGroup.exportMolecule();

			switch (type) {

			case GRAPH:
				try {
					BenzenoidParser.exportToGraphFile(benzenoid, file);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				break;

			case CML:
				try {
					CMLConverter.generateCmlFile(benzenoid, file);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				break;

			case COM:
				try {
					String title = nameField.getText();
					if ("".equals(title))
						title = "default_name";
					ComConverter.generateComFile(benzenoid, file, 0, ComType.IR, title);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				break;
			}
		}
	}

	public void addCrown() {
		nbCrowns++;
		borderPane.getChildren().remove(moleculeGroup);
		MoleculeGroup newMoleculeGroup = new MoleculeGroup(nbCrowns, this);

		HexagonDraw[][] hexagonMatrix = moleculeGroup.getHexagonsMatrix();

		for (int i = 0; i < hexagonMatrix.length; i++) {
			for (int j = 0; j < hexagonMatrix.length; j++) {
				if (hexagonMatrix[i][j] != null) {
					newMoleculeGroup.getHexagonsMatrix()[i + 1][j + 1].setLabel(hexagonMatrix[i][j].getLabel());
				}
			}
		}

		moleculeGroup = newMoleculeGroup;
		borderPane.setCenter(moleculeGroup);
	}

	public void removeCrown() {
		if (nbCrowns > 2) {

			nbCrowns--;
			MoleculeGroup newMoleculeGroup = new MoleculeGroup(nbCrowns, this);

			HexagonDraw[][] hexagonMatrix = moleculeGroup.getHexagonsMatrix();
			HexagonDraw[][] newHexagonMatrix = newMoleculeGroup.getHexagonsMatrix();

			for (int i = 1; i < hexagonMatrix.length; i++) {
				for (int j = 1; j < hexagonMatrix.length; j++) {
					if (i - 1 < newHexagonMatrix.length && j - 1 < newHexagonMatrix.length
							&& newHexagonMatrix[i - 1][j - 1] != null)
						newHexagonMatrix[i - 1][j - 1].setLabel(hexagonMatrix[i][j].getLabel());
				}
			}
			moleculeGroup = newMoleculeGroup;
			borderPane.setCenter(moleculeGroup);
		}
	}

	public void checkBorder() {
		boolean borderUsed = false;
		for (HexagonDraw hexagon : moleculeGroup.getExtendedBorder()) {
			if (hexagon.getLabel() == PatternLabel.NEUTRAL) {
				borderUsed = true;
				break;
			}
		}
		if (!borderUsed)
			removeCrown();
	}
}
