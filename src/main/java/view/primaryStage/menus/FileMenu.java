package view.primaryStage.menus;

import java.io.File;

import application.BenzenoidApplication;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.DirectoryChooser;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidsCollectionsManagerPane;

public class FileMenu {

	public FileMenu() {
		// TODO Auto-generated constructor stub
	}
public static Menu build(BenzenoidApplication app) {
		BenzenoidsCollectionsManagerPane collectionsPane = (BenzenoidsCollectionsManagerPane) app.getPanes().getCollectionsPane();
		Menu fileMenu = new Menu("_File");
		Menu exportBenzenoidMenu = new Menu("Export benzenoid(s)");
		MenuItem exportGraph = new MenuItem(".graph");
		MenuItem exportPng = new MenuItem(".png");
		MenuItem exportCml = new MenuItem(".cml");
		MenuItem exportCom = new MenuItem(".com");

		MenuItem exportCollection = new MenuItem("Export collection");
		MenuItem importCollection = new MenuItem("Import collection");

		fileMenu.getItems().addAll(exportBenzenoidMenu, exportCollection, importCollection);
		exportBenzenoidMenu.getItems().addAll(exportGraph, exportPng, exportCml, exportCom);

		fileMenu.setOnShowing(e -> {
			app.switchMode(collectionsPane);
		});

		exportGraph.setOnAction(e -> {
			((BenzenoidsCollectionsManagerPane) collectionsPane).exportGraph();
		});

		exportPng.setOnAction(e -> {
			((BenzenoidsCollectionsManagerPane) collectionsPane).exportPng();
		});

		exportCml.setOnAction(e -> {
			((BenzenoidsCollectionsManagerPane) collectionsPane).exportCML();
		});

		exportCom.setOnAction(e -> {
			((BenzenoidsCollectionsManagerPane) collectionsPane).exportCOM();
		});

		importCollection.setOnAction(e -> {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			File directory = directoryChooser.showDialog(app.getStage());

			if (directory != null) {
				((BenzenoidsCollectionsManagerPane) collectionsPane).importCollection(directory);
			}
		});

		exportCollection.setOnAction(e -> {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			File directory = directoryChooser.showDialog(app.getStage());

			if (directory != null) {
				BenzenoidCollectionPane currentPane = ((BenzenoidsCollectionsManagerPane) collectionsPane)
						.getSelectedTab();
				currentPane.export(directory);
			}
		});

		return fileMenu;

	}
}
