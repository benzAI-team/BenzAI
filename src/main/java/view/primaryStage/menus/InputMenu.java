package view.primaryStage.menus;

import java.io.File;

import application.BenzenoidApplication;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import molecules.Molecule;
import parsers.GraphParser;
import utils.Utils;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionsManagerPane;
import view.collections.BenzenoidCollectionPane.DisplayType;
import view.draw.DrawBenzenoidPane;

public class InputMenu {

	public InputMenu() {
		// TODO Auto-generated constructor stub
	}
	public static Menu build(BenzenoidApplication app) {
		BenzenoidCollectionsManagerPane collectionsPane =  (BenzenoidCollectionsManagerPane) app.getPanes().getCollectionsPane();
		final Menu inputMenu = new Menu("_Input");

		inputMenu.setOnShowing(e -> {
			app.switchMode(app.getPanes().getCollectionsPane());
		});

		MenuItem generatorMenu = new MenuItem("Generator");
		MenuItem databaseMenu = new MenuItem("Database");
		MenuItem drawMenu = new MenuItem("Draw");
		Menu importMenu = new Menu("Import");
		MenuItem importBenzenoidItem = new MenuItem("Import benzenoid");

		importBenzenoidItem.setOnAction(e -> {

			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Import benzenoid");
			File file = fileChooser.showOpenDialog(app.getStage());

			if (file != null) {

				try {
					Molecule molecule = GraphParser.parseUndirectedGraph(file);
					molecule.setDescription(file.getName());

					BenzenoidCollectionPane benzenoidCollectionPane = collectionsPane.getSelectedTab();

					benzenoidCollectionPane.addBenzenoid(molecule, DisplayType.BASIC);
					benzenoidCollectionPane.refresh();
				} catch (Exception e1) {
					Utils.alert("Invalid file");
				}
			}

		});

		MenuItem importCollectionItem = new MenuItem("Import collection");

		importCollectionItem.setOnAction(e -> {

			DirectoryChooser directoryChooser = new DirectoryChooser();
			File file = directoryChooser.showDialog(app.getStage());

			if (file != null) {
				collectionsPane.importCollection(file);
			}

		});

		importMenu.getItems().addAll(importBenzenoidItem, importCollectionItem);

		databaseMenu.setOnAction(e -> {
			app.switchMode(app.getPanes().getDatabasePane());
		});

		generatorMenu.setOnAction(e -> {
			app.switchMode(app.getPanes().getGeneratorPane());
		});

		drawMenu.setOnAction(e -> {
			((DrawBenzenoidPane) app.getPanes().getDrawPane()).refreshMenuBar();
			app.switchMode(app.getPanes().getDrawPane());
		});

		inputMenu.getItems().add(generatorMenu);
		inputMenu.getItems().add(databaseMenu);
		inputMenu.getItems().add(drawMenu);
		inputMenu.getItems().add(importMenu);

		return inputMenu;

	}
}
