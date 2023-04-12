package view.draw;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import application.BenzenoidApplication;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import molecules.Molecule;
import parsers.CMLConverter;
import parsers.ComConverter;
import parsers.ComConverter.ComType;
import parsers.GraphParser;
import utils.Couple;
import utils.Utils;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionsManagerPane;
import view.collections.BenzenoidCollectionPane.DisplayType;
import view.collections.CollectionMenuItem;

public class DrawBenzenoidPane extends BorderPane {

	private enum ExportType {
		GRAPH, CML, COM
	}

	private BenzenoidApplication application;
	private BenzenoidCollectionsManagerPane collectionsPane;
	
	private MenuBar menuBar;
	private HBox buttonBar;
	private BorderPane borderPane;

	private MoleculeGroup moleculeGroup;
	private TextField nameField;

	private int nbCrowns;

	public DrawBenzenoidPane(BenzenoidApplication application, BenzenoidCollectionsManagerPane collectionsPane) {

		this.application = application;
		this.collectionsPane = collectionsPane;
		initialize();
	}

	private void initialize() {

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

	public void refreshMenuBar() {

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
				Molecule molecule = GraphParser.parseUndirectedGraph(file);
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
				hexagon.setLabel(0);
		});
		clearItem.setGraphic(clearLabel);

		Menu selectAllItem = new Menu();
		Label selectAllLabel = new Label("Select all");
		selectAllLabel.setOnMouseClicked(e -> {
			for (HexagonDraw hexagon : moleculeGroup.getHexagons())
				hexagon.setLabel(1);
		});
		selectAllItem.setGraphic(selectAllLabel);

		Menu revertItem = new Menu();
		Label revertLabel = new Label("Reverse");
		revertLabel.setOnMouseClicked(e -> {
			for (HexagonDraw hexagon : moleculeGroup.getHexagons())
				hexagon.setLabel(1 - hexagon.getLabel());
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

				Molecule molecule = null;

				try {
					molecule = moleculeGroup.exportMolecule();
				} catch (Exception e1) {
					molecule = null;
					Utils.alert("Invalid draw");
				}

				if (molecule != null) {
					molecule.setDescription(nameField.getText());
					
					collectionPane.unselectAll();
					collectionPane.addBenzenoid(molecule, DisplayType.BASIC);
					collectionPane.refresh();

					Utils.showAlertWithoutHeaderText("Molecule added to collection: " + collectionPane.getName());

				}
			});

			addMenu.getItems().add(menuItem);
		}

		MenuItem newCollectionItem = new MenuItem("New collection");

		newCollectionItem.setOnAction(e -> {
			Molecule molecule = moleculeGroup.exportMolecule();
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

	public void importBenzenoid(Molecule molecule) {

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
				if (coord.getX() >= nbCrowns || coord.getY() >= nbCrowns)
					isEmbedded = false;
			}

			if (isEmbedded)
				System.out.print("");

			nbCrowns++;
		}

		MoleculeGroup newMoleculeGroup = new MoleculeGroup(nbCrowns, this);

		for (Couple<Integer, Integer> coord : coords)
			newMoleculeGroup.getHexagonsMatrix()[coord.getX()][coord.getY()].setLabel(1);

		borderPane.getChildren().remove(moleculeGroup);
		moleculeGroup = newMoleculeGroup;

		borderPane.setCenter(moleculeGroup);
	}

	private void export(ExportType type) {

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save");
		File file = fileChooser.showSaveDialog(application.getStage());

		if (file != null) {

			Molecule molecule = moleculeGroup.exportMolecule();

			switch (type) {

			case GRAPH:
				try {
					molecule.exportToGraphFile(file);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				break;

			case CML:
				try {
					CMLConverter.generateCmlFile(molecule, file);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				break;

			case COM:
				try {
					String title = nameField.getText();
					if (title.equals(""))
						title = "default_name";
					ComConverter.generateComFile(molecule, file, 0, ComType.IR, title);
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
			if (hexagon.getLabel() == 1) {
				borderUsed = true;
				break;
			}
		}

		if (!borderUsed)
			removeCrown();
	}
}
