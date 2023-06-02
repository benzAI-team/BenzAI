package application;

import http.Post;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.robot.Robot;
import javafx.stage.Stage;
import view.collections.BenzenoidCollectionsManagerPane;
import view.collections_operations.CollectionsOperationsPane;
import view.database.DatabasePane;
import view.draw.DrawBenzenoidPane;
import view.filtering.FilteringPane;
import view.generator.GeneratorPane;
import view.primaryStage.AboutPane;
import view.primaryStage.Panes;
import view.primaryStage.menus.*;

import java.util.ArrayList;


public class BenzenoidApplication extends Application {

	private Stage stage;
	private BorderPane rootPane;
	private Settings settings;

	/*
	 * Home region
	 */

	/*
	 * Main regions
	 */

	private Panes panes;

	/*
	 * Menus
	 */

	private Menu tasksMenu;

	private ArrayList<TaskHBox> tasksBoxes;

	public Stage getStage() {
		return stage;
	}

	@Override
	public void start(Stage primaryStage) {
		try {

			boolean database = Post.checkDatabaseConnection();

			settings = Settings.readSettingsFile();

			panes = new Panes(this);

			((BenzenoidCollectionsManagerPane) panes.getCollectionsPane()).log("BenzAI started", true);

			if (database)
				((BenzenoidCollectionsManagerPane) panes.getCollectionsPane())
						.log("Connection to database established", true);
			else
				((BenzenoidCollectionsManagerPane) panes.getCollectionsPane()).log("Connection to database failed",
						true);

			((BenzenoidCollectionsManagerPane) panes.getCollectionsPane()).log("", false);

			tasksBoxes = new ArrayList<>();

			stage = primaryStage;

			panes.setDatabasePane(new DatabasePane(this));

			rootPane = buildRootPane();

			Scene generatorScene = new Scene(rootPane);
			generatorScene.getStylesheets().add("/resources/style/application.css");

			// initializeIcons();

			initPrimaryStageProperties(primaryStage);
			primaryStage.setScene(generatorScene);
			primaryStage.show();

			if (settings.isDisplayHomeWindow())
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
		MenuBar menuBar = buildMenuBar();
		rootPane.setTop(menuBar);

		rootPane.setCenter(panes.getCollectionsPane());

		return rootPane;
	}

	/***
	 *
	 * @return the primaryStage menu bar
	 */
	private MenuBar buildMenuBar() {

		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().addAll(FileMenu.build(this), CollectionsMenu.build(this), InputMenu.build(this),
				SortMenu.build(this), FilterMenu.build(this), ComputationsMenu.build(this), PreferencesMenu.build(this),
				TasksMenu.build(this), HelpMenu.build(this));

		return menuBar;
	}

	/***
	 * set the primary stage title, width, length, ...
	 */
	private void initPrimaryStageProperties(Stage primaryStage) {
		primaryStage.setTitle("BenzAI");

		primaryStage.setWidth(settings.getWidth());
		primaryStage.setHeight(settings.getHeight());

		primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
			if (settings.remembersSize()) {
				settings.setWidth(newVal.doubleValue());
				settings.save();
			}
		});

		primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
			if (settings.remembersSize()) {
				settings.setHeight(newVal.doubleValue());
				settings.save();
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
	 */
	public void switchMode(Region pane) {
		if (pane instanceof FilteringPane) {
			Robot robot = new Robot();
			robot.keyPress(KeyCode.ESCAPE);
			robot.keyRelease(KeyCode.ESCAPE);
			((FilteringPane) pane).placeComponents();
		}
		if (pane instanceof CollectionsOperationsPane)
			((CollectionsOperationsPane) pane).refreshBoxes();
		rootPane.setCenter(pane);
	}

	/***
	 * getters, setters
	 */
	public BenzenoidCollectionsManagerPane getBenzenoidCollectionsPane() {
		return (BenzenoidCollectionsManagerPane) panes.getCollectionsPane();
	}

	public DrawBenzenoidPane getDrawPane() {
		return panes.getDrawPane();
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
		if (tasksBoxes.isEmpty())
			addTask("None");
	}

	public Settings getSettings() {
		return settings;
	}

	GeneratorPane getGeneratorPane() {
		return (GeneratorPane) panes.getGeneratorPane();
	}

	public Panes getPanes() {
		return panes;
	}

	public static void main(String[] args) {
		launch(args);
	}

}
