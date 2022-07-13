package application;

import java.io.File;
import java.util.ArrayList;

import http.Post;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.robot.Robot;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import molecules.Molecule;
import molecules.sort.IrregularityComparator;
import molecules.sort.NbCarbonsComparator;
import molecules.sort.NbHexagonsComparator;
import molecules.sort.NbHydrogensComparator;
import molecules.sort.NbKekuleStructuresComparator;
import molecules.sort.ResonanceEnergyComparator;
import parsers.GraphParser;
import solveur.Aromaticity.RIType;
import utils.Utils;
import view.catalog.CatalogPane;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionPane.DisplayType;
import view.collections.BenzenoidsCollectionsManagerPane;
import view.collections.IRSpectraParameterPane;
import view.collections.RenameCollectionPane;
import view.collections_operations.CollectionsOperationsPane;
import view.database.DatabasePane;
import view.draw.DrawBenzenoidPane;
import view.filtering.FilteringPane;
import view.generator.GeneratorPane;
import view.generator.preferences.GeneratorPreferencesPane;
import view.groups.AromaticityDisplayType;
import view.groups.AromaticityGroup;
import view.help.HelpPane;
import view.primaryStage.Panes;
import view.primaryStage.menus.*;

public class BenzenoidApplication extends Application {

	private Stage stage;
	private BorderPane rootPane;

	private Configuration configuration;

	/*
	 * Home region
	 */

	private Region homeRegion;

	/*
	 * Main regions
	 */

	private Panes panes;

	/*
	 * Menus
	 */

	private Menu tasksMenu;
	private Scene generatorScene;

	private ArrayList<TaskHBox> tasksBoxes;

	public Stage getStage() {
		return stage;
	}

	@Override
	public void start(Stage primaryStage) {
		try {

			boolean database = Post.checkDatabaseConnection();

			configuration = Configuration.readConfigurationFile();

			homeRegion = new AboutPane(this);

			panes = new Panes(this);

			((BenzenoidsCollectionsManagerPane) panes.getCollectionsPane()).log("BenzAI started", true);

			if (database)
				((BenzenoidsCollectionsManagerPane) panes.getCollectionsPane()).log("Connection to database established", true);
			else
				((BenzenoidsCollectionsManagerPane) panes.getCollectionsPane()).log("Connection to database failed", true);

			((BenzenoidsCollectionsManagerPane) panes.getCollectionsPane()).log("", false);

			tasksBoxes = new ArrayList<>();

			stage = primaryStage;

			panes.setDatabasePane( new DatabasePane(this));

			rootPane = buildRootPane();

			generatorScene = new Scene(rootPane);
			generatorScene.getStylesheets().add("/resources/style/application.css");

			// initializeIcons();

			initPrimaryStageProperties(primaryStage);
			primaryStage.setScene(generatorScene);
			primaryStage.show();

			if (configuration.isDisplayHomeWindow())
				displayAboutWindow();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void displayAboutWindow() {

		AboutPane root = new AboutPane(this);
		Stage stage = new Stage();
		stage.setTitle("About BenzAI");

		stage.setWidth(820);
		stage.setHeight(325);

		stage.setResizable(false);

		stage.getIcons().add(new Image("/resources/graphics/icon-benzene.png"));

		Scene scene = new Scene(root);
		scene.getStylesheets().add("/resources/style/application.css");

		stage.setScene(scene);
		stage.show();
	}

	/***
	 * 
	 * @return the root pane
	 */
	private BorderPane buildRootPane() {
		BorderPane rootPane = new BorderPane();
		MenuBar menuBar = buildMenuBar(rootPane);
		rootPane.setTop(menuBar);

		rootPane.setCenter(panes.getCollectionsPane());

		return rootPane;
	}

	/***
	 * 
	 * @param rootPane
	 * @return the primaryStage menu bar
	 */
	private MenuBar buildMenuBar(BorderPane rootPane) {

		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().addAll(FileMenu.build(this), CollectionsMenu.build(this), InputMenu.build(this), SortMenu.build(this), FilterMenu.build(this),
				ComputationsMenu.build(this), PreferencesMenu.build(this), TasksMenu.build(this), HelpMenu.build(this));

		return menuBar;
	}

	/***
	 * set the primary stage title, width, length, ...
	 * 
	 * @param primaryStage
	 */
	private void initPrimaryStageProperties(Stage primaryStage) {
		primaryStage.setTitle("BenzAI");

		primaryStage.setWidth(configuration.getWidth());
		primaryStage.setHeight(configuration.getHeight());

		primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
			if (configuration.remembersSize()) {
				configuration.setWidth(newVal.doubleValue());
				configuration.save();
			}
		});

		primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
			if (configuration.remembersSize()) {
				configuration.setHeight(newVal.doubleValue());
				configuration.save();
			}
		});

		primaryStage.centerOnScreen();

		primaryStage.getIcons().add(new Image("/resources/graphics/icon-benzene.png"));

	}


	private Menu debugMenu() {
		Menu fill1Item = new Menu();

		Label labelFill1 = new Label("Fill (debug)");
		labelFill1.setOnMouseClicked(e -> {
			// Trash.generate5HCriterion(getBenzenoidCollectionsPane());
		});

		fill1Item.setGraphic(labelFill1);

		return fill1Item;
	}

	/***
	 * 
	 * @param pane
	 */
	public void switchMode(Region pane) {
		if(pane instanceof FilteringPane) {
			Robot robot = new Robot();
			robot.keyPress(KeyCode.ESCAPE);
			robot.keyRelease(KeyCode.ESCAPE);
			((FilteringPane) pane).refresh();
		}
		if(pane instanceof CollectionsOperationsPane)
			((CollectionsOperationsPane) pane).refreshBoxes();
		rootPane.setCenter(pane);
	}
	
	/***
	 * getters, setters
	 */
	public BenzenoidsCollectionsManagerPane getBenzenoidCollectionsPane() {
		return (BenzenoidsCollectionsManagerPane) panes.getCollectionsPane();
	}

	public DrawBenzenoidPane getDrawPane() {
		return panes.getDrawPane();
	}

	public FilteringPane getFilteringPane() {
		return (FilteringPane) panes.getFilteringPane();
	}

	public Menu getTasksMenu() {
		return tasksMenu;
	}

	public void setTasksMenu(Menu tasksMenu) {
		this.tasksMenu = tasksMenu;
	}

	public void addTask(String task) {
		TaskHBox hBox = new TaskHBox(this, task);
		tasksBoxes.add(hBox);

		MenuItem menuItem = new MenuItem();
		menuItem.setGraphic(hBox);

		tasksMenu.getItems().add(menuItem);

		if (tasksMenu.getItems().size() > 1)
			removeTask("None");
	}

	public void removeTask(String task) {
		for (int i = 0; i < tasksBoxes.size(); i++) {
			TaskHBox hBox = tasksBoxes.get(i);
			if (hBox.getTask().equals(task)) {
				tasksMenu.getItems().remove(i);
				tasksBoxes.remove(i);
				break;
			}
		}
		if (tasksBoxes.size() == 0)
			addTask("None");
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public GeneratorPane getGeneratorPane() {
		return (GeneratorPane) panes.getGeneratorPane();
	}

	public Panes getPanes() {
		return panes;
	}

	public ArrayList<TaskHBox> getTasksBoxes() {
		return tasksBoxes;
	}

	public void setTasksBoxes(ArrayList<TaskHBox> tasksBoxes) {
		this.tasksBoxes = tasksBoxes;
	}

	public static void main(String[] args) {
		launch(args);
	}

}
