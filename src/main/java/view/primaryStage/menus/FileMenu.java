package view.primaryStage.menus;

import java.io.File;
import java.io.IOException;

import application.BenzenoidApplication;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Region;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import view.ames_format.AmesFormatPane;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidsCollectionsManagerPane;
import view.ir_spectra.IRSpectraPane;

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
		MenuItem generateAmesFile = new MenuItem("Generate Ames file format");

		fileMenu.getItems().addAll(exportBenzenoidMenu, exportCollection, importCollection, generateAmesFile);
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

		generateAmesFile.setOnAction(e -> {

			try {
				Region amesPane = new AmesFormatPane(app);
				Stage stage = new Stage();
				stage.setTitle("Intensities");

				Scene scene = new Scene(amesPane, 823, 515);
				scene.getStylesheets().add("/resources/style/application.css");

				stage.setScene(scene);
				stage.show();
			} catch (IOException exception) {
				exception.printStackTrace();
			}


		});

		return fileMenu;

	}
}
