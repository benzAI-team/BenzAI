package application;

import java.io.File;
import java.util.ArrayList;

import molecules.sort.IrregularityComparator;
import molecules.sort.MoleculeComparator;
import molecules.sort.NbCarbonsComparator;
import molecules.sort.NbHexagonsComparator;
import molecules.sort.NbHydrogensComparator;
import molecules.sort.NbKekuleStructuresComparator;


import http.Post;
import javafx.scene.robot.Robot;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import molecules.Molecule;
import parsers.GraphParser;
import solveur.Aromaticity.RIType;
import utils.Utils;
import view.catalog.CatalogPane;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionPane.DisplayType;
import view.collections.BenzenoidsCollectionsManagerPane;
import view.collections.RenameCollectionPane;
import view.collections.IRSpectraParameterPane;
import view.collections_operations.CollectionsOperationsPane;
import view.database.DatabasePane;
import view.draw.DrawBenzenoidPane;
import view.filtering.FilteringPane;
import view.generator.GeneratorPane;
import view.generator.preferences.GeneratorPreferencesPane;
import view.groups.AromaticityDisplayType;
import view.groups.AromaticityGroup;
import view.help.HelpPane;

public class BenzenoidApplication extends Application {

	private Stage stage;
	private ApplicationMode mode;
	private BorderPane rootPane;

	private Configuration configuration;

	/*
	 * Home region
	 */

	private Region homeRegion;

	/*
	 * Main regions
	 */

	private Region generatorPane;
	private Region collectionsPane;
	private DrawBenzenoidPane drawPane;
	private Region filteringPane;
	private Region databasePane;
	private Region operationPane;
	private Region generationPreferencesPane;

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

			mode = ApplicationMode.COLLECTIONS;

			homeRegion = new AboutPane(this);

			collectionsPane = new BenzenoidsCollectionsManagerPane(this);
			generatorPane = new GeneratorPane(this);
			drawPane = new DrawBenzenoidPane(this);
			databasePane = new CatalogPane(this);
			filteringPane = new FilteringPane(this);
			operationPane = new CollectionsOperationsPane(this);
			generationPreferencesPane = new GeneratorPreferencesPane(this);

			((BenzenoidsCollectionsManagerPane) collectionsPane).log("BenzAI started", true);

			if (database)
				((BenzenoidsCollectionsManagerPane) collectionsPane).log("Connection to database established", true);
			else
				((BenzenoidsCollectionsManagerPane) collectionsPane).log("Connection to database failed", true);

			((BenzenoidsCollectionsManagerPane) collectionsPane).log("", false);

			tasksBoxes = new ArrayList<>();

			stage = primaryStage;

			databasePane = new DatabasePane(this);

			rootPane = buildRootPane();

			generatorScene = new Scene(rootPane);
			generatorScene.getStylesheets().add("/resources/style/application.css");
			// generatorScene.getStylesheets().add("application/dark_mode.css");

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

		mode = ApplicationMode.COLLECTIONS;
		rootPane.setCenter(collectionsPane);

		return rootPane;
	}

	/***
	 * 
	 * @param rootPane
	 * @return the primaryStage menu bar
	 */
	private MenuBar buildMenuBar(BorderPane rootPane) {

		MenuBar menuBar = new MenuBar();
    menuBar.getMenus().addAll(fileMenu(), collectionsMenu(), inputMenu(), sortMenu(), filterMenu(), computationsMenu(), preferencesMenu(), tasksMenu(), helpMenu());
    
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


  private Menu fileMenu() {
    // defines the menu item related to the file management
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
			switchMode(ApplicationMode.COLLECTIONS);
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
			File directory = directoryChooser.showDialog(getStage());

			if (directory != null) {
				((BenzenoidsCollectionsManagerPane) collectionsPane).importCollection(directory);
			}
		});

		exportCollection.setOnAction(e -> {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			File directory = directoryChooser.showDialog(getStage());

			if (directory != null) {
				BenzenoidCollectionPane currentPane = ((BenzenoidsCollectionsManagerPane) collectionsPane).getSelectedTab();
				currentPane.export(directory);
			}
		});
    
    return fileMenu;
  }

  private Menu collectionsMenu() {
    // defines the menu item related to the collections
 		final Menu collectionsMenu = new Menu("_Collections");

  
    collectionsMenu.setOnShowing(e -> {
			switchMode(ApplicationMode.COLLECTIONS);
		});
  
    
    
		MenuItem itemRename = new MenuItem("Rename collection");
		MenuItem itemDelete = new MenuItem("Delete benzenoid(s)");
		MenuItem itemCopy = new MenuItem("Copy benzenoid(s)");
		MenuItem itemPaste = new MenuItem("Paste benzenoid(s)");
		MenuItem itemSelect = new MenuItem("Select all");
		MenuItem unselectAllItem = new MenuItem("Unselect all");
    MenuItem operationsMenu = new MenuItem("Operations on collections");

		operationsMenu.setOnAction(e -> {
			switchMode(ApplicationMode.COLLECTIONS_OPERATIONS);
		});
    
		itemPaste.setOnAction(e -> {
			((BenzenoidsCollectionsManagerPane) collectionsPane).paste();
		});

		itemSelect.setOnAction(e -> {
			((BenzenoidsCollectionsManagerPane) collectionsPane).selectAll();
		});
    
    unselectAllItem.setOnAction(e -> {
			((BenzenoidsCollectionsManagerPane) collectionsPane).unselectAll();
		});

		itemRename.setOnAction(e -> {
			RenameCollectionPane root;
			root = new RenameCollectionPane((BenzenoidsCollectionsManagerPane) collectionsPane);
			Stage stage = new Stage();
			stage.setTitle("Rename collection");

			stage.setResizable(false);

			stage.getIcons().add(new Image("/resources/graphics/icon-benzene.png"));

			Scene scene = new Scene(root);
			scene.getStylesheets().add("/resources/style/application.css");

			stage.setScene(scene);
			stage.show();
		});

		itemDelete.setOnAction(e -> {
			BenzenoidCollectionPane currentPane = ((BenzenoidsCollectionsManagerPane) collectionsPane).getSelectedTab();
			currentPane.removeBenzenoidPanes(currentPane.getSelectedBenzenoidPanes());
			((BenzenoidsCollectionsManagerPane) collectionsPane).log("Deleting " + currentPane.getSelectedBenzenoidPanes().size() + " benzenoid(s) from " + currentPane.getName(), true);
		});

		itemCopy.setOnAction(e -> {
			BenzenoidCollectionPane originBenzenoidCollectionPane = ((BenzenoidsCollectionsManagerPane) collectionsPane).getSelectedTab();
			originBenzenoidCollectionPane.copy();
		});

		collectionsMenu.getItems().addAll(itemRename, itemDelete, itemCopy, itemPaste, itemSelect, ((BenzenoidsCollectionsManagerPane) collectionsPane).initializeMoveMenuItem(),operationsMenu);
    
    return collectionsMenu;
  }

  private Menu inputMenu() {
    // defines the menu item related to the input
    final Menu inputMenu = new Menu("_Input");

    inputMenu.setOnShowing(e -> {
			switchMode(ApplicationMode.COLLECTIONS);
		});

		MenuItem generatorMenu = new MenuItem("Generator");
		MenuItem databaseMenu = new MenuItem("Database");
		MenuItem drawMenu = new MenuItem("Draw");
		Menu importMenu = new Menu("Import");
		MenuItem importBenzenoidItem = new MenuItem("Import benzenoid");

		importBenzenoidItem.setOnAction(e -> {

			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Import benzenoid");
			File file = fileChooser.showOpenDialog(stage);

			if (file != null) {

				try {
					Molecule molecule = GraphParser.parseUndirectedGraph(file);
					molecule.setDescription(file.getName());

					BenzenoidCollectionPane benzenoidCollectionPane = ((BenzenoidsCollectionsManagerPane) collectionsPane).getSelectedTab();

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
			File file = directoryChooser.showDialog(stage);

			if (file != null) {
				((BenzenoidsCollectionsManagerPane) collectionsPane).importCollection(file);
			}

		});

		importMenu.getItems().addAll(importBenzenoidItem, importCollectionItem);


 		databaseMenu.setOnAction(e -> {
			switchMode(ApplicationMode.DATABASE);
		});

		generatorMenu.setOnAction(e -> {
			switchMode(ApplicationMode.GENERATOR);
		});

		drawMenu.setOnAction(e -> {
			((DrawBenzenoidPane) drawPane).refreshMenuBar();
			switchMode(ApplicationMode.DRAW);
		});


		inputMenu.getItems().add(generatorMenu);
		inputMenu.getItems().add(databaseMenu);
		inputMenu.getItems().add(drawMenu);
		inputMenu.getItems().add(importMenu);
    
    return inputMenu;
  }

  private Menu sortMenu() {
    // defines the menu item related to the sort of collections

		Menu sortMenu = new Menu("_Sort");
		Menu nbCarbonsItem = new Menu("Number of carbons");
		Menu nbHydrogensItem = new Menu("Number of hydrogens");
		Menu nbHexagonsItem = new Menu("Number of hexagons");
		Menu nbKekuleStructuresItem = new Menu("Number of Kekulé structures");
		Menu irregularityItem = new Menu("Irregularity");

    sortMenu.setOnShowing(e -> {
			switchMode(ApplicationMode.COLLECTIONS);
		});

		/*
		 * Nb Carbons
		 */

		MenuItem nbCarbonsIncreasing = new MenuItem("Increasing");
		MenuItem nbCarbonsDecreasing = new MenuItem("Decreasing");

		nbCarbonsIncreasing.setOnAction(e -> {
			((BenzenoidsCollectionsManagerPane) collectionsPane).sort(new NbCarbonsComparator(), false);
		});

		nbCarbonsDecreasing.setOnAction(e -> {
			((BenzenoidsCollectionsManagerPane) collectionsPane).sort(new NbCarbonsComparator(), true);
		});

		nbCarbonsItem.getItems().addAll(nbCarbonsIncreasing, nbCarbonsDecreasing);

		/*
		 * Nb hydrogens
		 */

		MenuItem nbHydrogensIncreasing = new MenuItem("Increasing");
		MenuItem nbHydrogensDecreasing = new MenuItem("Decreasing");

		nbHydrogensIncreasing.setOnAction(e -> {
			((BenzenoidsCollectionsManagerPane) collectionsPane).sort(new NbHydrogensComparator(), false);
		});

		nbHydrogensDecreasing.setOnAction(e -> {
			((BenzenoidsCollectionsManagerPane) collectionsPane).sort(new NbHydrogensComparator(), true);
		});

		nbHydrogensItem.getItems().addAll(nbHydrogensIncreasing, nbHydrogensDecreasing);

		/*
		 * Nb Hexagons
		 */

		MenuItem nbHexagonsIncreasing = new MenuItem("Increasing");
		MenuItem nbHexagonsDecreasing = new MenuItem("Decreasing");

		nbHexagonsIncreasing.setOnAction(e -> {
			((BenzenoidsCollectionsManagerPane) collectionsPane).sort(new NbHexagonsComparator(), false);
		});

		nbHexagonsDecreasing.setOnAction(e -> {
			((BenzenoidsCollectionsManagerPane) collectionsPane).sort(new NbHexagonsComparator(), true);
		});

		nbHexagonsItem.getItems().addAll(nbHexagonsIncreasing, nbHexagonsDecreasing);

		/*
		 * Nb Kekule Structures
		 */

		MenuItem nbKekuleStructuresIncreasing = new MenuItem("Increasing");
		MenuItem nbKekuleStructuresDecreasing = new MenuItem("Decreasing");

		nbKekuleStructuresIncreasing.setOnAction(e -> {
			((BenzenoidsCollectionsManagerPane) collectionsPane).sort(new NbKekuleStructuresComparator(), false);
		});

		nbKekuleStructuresDecreasing.setOnAction(e -> {
			((BenzenoidsCollectionsManagerPane) collectionsPane).sort(new NbKekuleStructuresComparator(), true);
		});

		nbKekuleStructuresItem.getItems().addAll(nbKekuleStructuresIncreasing, nbKekuleStructuresDecreasing);

		/*
		 * Irregularity
		 */

		MenuItem irregularityIncreasing = new MenuItem("Increasing");
		MenuItem irregularityDecreasing = new MenuItem("Decreasing");

		irregularityIncreasing.setOnAction(e -> {
			((BenzenoidsCollectionsManagerPane) collectionsPane).sort(new IrregularityComparator(), false);
		});

		irregularityDecreasing.setOnAction(e -> {
			((BenzenoidsCollectionsManagerPane) collectionsPane).sort(new IrregularityComparator(), true);
		});

		irregularityItem.getItems().addAll(irregularityIncreasing, irregularityDecreasing);

		sortMenu.getItems().addAll(nbCarbonsItem, nbHydrogensItem, nbHexagonsItem, nbKekuleStructuresItem,
				irregularityItem);
        
    return sortMenu;
  }

  private Menu filterMenu() {
    // defines the menu item related to filters
    Menu filterMenu = new Menu("Fi_lter");
    
    final MenuItem menuItem = new MenuItem();
    filterMenu.getItems().add(menuItem);
    filterMenu.addEventHandler(Menu.ON_SHOWN, event -> filterMenu.hide());
    filterMenu.addEventHandler(Menu.ON_SHOWING, event -> filterMenu.fire());
    
		filterMenu.setOnAction(e -> {
			switchMode(ApplicationMode.FILTER);
		});
    
    return filterMenu;
  }
  
  private Menu computationsMenu() {
    // defines the menu item related to the computations
    Menu computationsMenu = new Menu("C_omputations");
    
		MenuItem reItem = new MenuItem("Resonance Energy (Lin)");
		MenuItem clarItem = new MenuItem("Clar Cover");
		MenuItem rboItem = new MenuItem("Ring Bond Order");
		MenuItem irregularityStatsItem = new MenuItem("Irregularity Statistics");
		MenuItem irSpectraItem = new MenuItem("IR Spectra");

    computationsMenu.setOnShowing(e -> {
			switchMode(ApplicationMode.COLLECTIONS);
		});


		reItem.setOnAction(e -> {
			((BenzenoidsCollectionsManagerPane) collectionsPane).resonanceEnergyLin();
		});

		clarItem.setOnAction(e -> {
			((BenzenoidsCollectionsManagerPane) collectionsPane).clarCover();
		});

		rboItem.setOnAction(e -> {
			((BenzenoidsCollectionsManagerPane) collectionsPane).ringBoundOrder();
		});

		irregularityStatsItem.setOnAction(e -> {
			((BenzenoidsCollectionsManagerPane) collectionsPane).irregularityStatistics();
		});
    
    irSpectraItem.setOnAction(e -> {
			((BenzenoidsCollectionsManagerPane) collectionsPane).IRSpectra();
		});

		computationsMenu.getItems().addAll(reItem, clarItem, rboItem, irregularityStatsItem,irSpectraItem);

    return computationsMenu;
  }

  private Menu preferencesMenu (){
    // defines the menu item related to the preferences
    
    Menu preferencesMenu = new Menu("P_references");
		Menu aromaticityDisplayMenu = new Menu("Resonance energy display");
		CheckMenuItem localColorScaleItem = new CheckMenuItem("Local color scale");
		CheckMenuItem globalColorScaleItem = new CheckMenuItem("Global color scale");

		Menu riMenu = new Menu("Ri values");
		CheckMenuItem optimizedValues = new CheckMenuItem("Optimized values");
		CheckMenuItem defaultValues = new CheckMenuItem("Default values");
		riMenu.getItems().addAll(optimizedValues, defaultValues);

		MenuItem irSpectraParameterItem = new MenuItem("IR Spectra parameter");

		Menu windowMenu = new Menu("Window");

		localColorScaleItem.setSelected(true);
		AromaticityGroup.aromaticityDisplayType = AromaticityDisplayType.LOCAL_COLOR_SCALE;

    preferencesMenu.setOnShowing(e -> {
			switchMode(ApplicationMode.COLLECTIONS);
		});

		localColorScaleItem.setOnAction(e -> {

			if (localColorScaleItem.isSelected()) {
				globalColorScaleItem.setSelected(false);
				AromaticityGroup.aromaticityDisplayType = AromaticityDisplayType.LOCAL_COLOR_SCALE;
				((BenzenoidsCollectionsManagerPane) collectionsPane).refreshColorScales();
			}

			else
				localColorScaleItem.setSelected(true);

		});

		globalColorScaleItem.setOnAction(e -> {

			if (globalColorScaleItem.isSelected()) {
				localColorScaleItem.setSelected(false);
				AromaticityGroup.aromaticityDisplayType = AromaticityDisplayType.GLOBAL_COLOR_SCALE;
				((BenzenoidsCollectionsManagerPane) collectionsPane).refreshColorScales();
			}

			else
				globalColorScaleItem.setSelected(true);

		});

		optimizedValues.setSelected(true);

		optimizedValues.setOnAction(e -> {
			defaultValues.setSelected(false);
			((BenzenoidsCollectionsManagerPane) collectionsPane).refreshRIType(RIType.OPTIMIZED);
		});

		defaultValues.setOnAction(e -> {
			optimizedValues.setSelected(false);
			((BenzenoidsCollectionsManagerPane) collectionsPane).refreshRIType(RIType.NORMAL);
		});

		irSpectraParameterItem.setOnAction(e -> {
			Region parameterPane = new IRSpectraParameterPane((BenzenoidsCollectionsManagerPane) collectionsPane);
			Stage stage = new Stage();
			stage.setTitle("Set parameters");

			Scene scene = new Scene(parameterPane, 573, 535);
			scene.getStylesheets().add("/resources/style/application.css");

			stage.setScene(scene);
			stage.show();
		});

		MenuItem generationPreferencesItem = new MenuItem("Generation preferences");
		generationPreferencesItem.setOnAction(e -> {
			this.switchMode(ApplicationMode.GENERATOR_PREFERENCES);
		});

		aromaticityDisplayMenu.getItems().addAll(localColorScaleItem, globalColorScaleItem);
		preferencesMenu.getItems().addAll(aromaticityDisplayMenu, riMenu, irSpectraParameterItem, windowMenu,
				generationPreferencesItem);

		CheckMenuItem rememberResizeItem = new CheckMenuItem("Remember windows size");

		rememberResizeItem.setOnAction(e -> {

			if (rememberResizeItem.isSelected())
				configuration.setRemembersSize(true);

			else
				configuration.setRemembersSize(false);

			configuration.save();
		});

		windowMenu.getItems().add(rememberResizeItem);
    
    return preferencesMenu;
  }
  

  private Menu tasksMenu (){
    // defines the menu item related to the active tasks

		tasksMenu = new Menu("_Active tasks");
    
    addTask("None");
    
    return tasksMenu;
  }

  private Menu helpMenu (){
    // defines the menu item related to the help
    
    Menu helpMenu = new Menu("_Help");

		MenuItem helpItem = new MenuItem("Help content");
		MenuItem aboutItem = new MenuItem("About BenzAI");

		helpItem.setOnAction(e -> {
			Region helpPane = new HelpPane();
			Stage stage = new Stage();
			stage.setTitle("Help");

			Scene scene = new Scene(helpPane);
			scene.getStylesheets().add("/resources/style/application.css");

			stage.setScene(scene);
			stage.show();
		});

		aboutItem.setOnAction(e -> {
			displayAboutWindow();
		});

		helpMenu.getItems().addAll(helpItem, aboutItem);
    
    return helpMenu;
  }
  
  private Menu debugMenu (){
    // defines the menu item related to the help
		Menu fill1Item = new Menu();

		Label labelFill1 = new Label("Fill (debug)");
		labelFill1.setOnMouseClicked(e -> {
			// Trash.generate5HCriterion(getBenzenoidCollectionsPane());
		});

		fill1Item.setGraphic(labelFill1);
    
    return fill1Item;
  }

	public void switchMode(ApplicationMode mode) {

		if (!this.mode.equals(mode)) {

			this.mode = mode;

			switch (mode) {

			case COLLECTIONS:
				rootPane.setCenter(collectionsPane);
				break;

			case GENERATOR:
				rootPane.setCenter(generatorPane);
				break;

			case DRAW:
				rootPane.setCenter(drawPane);
				break;

			case DATABASE:
				rootPane.setCenter(databasePane);
				break;

			case FILTER:
				Robot robot = new Robot();
        robot.keyPress(KeyCode.ESCAPE);
        robot.keyRelease(KeyCode.ESCAPE);
        ((FilteringPane) filteringPane).refresh();
				rootPane.setCenter(filteringPane);
				break;

			case COLLECTIONS_OPERATIONS:
				((CollectionsOperationsPane) operationPane).refreshBoxes();
				rootPane.setCenter(operationPane);
				break;

			case GENERATOR_PREFERENCES:
				rootPane.setCenter(generationPreferencesPane);
				break;

			default:
				break;
			}
		}
	}

	public BenzenoidsCollectionsManagerPane getBenzenoidCollectionsPane() {
		return (BenzenoidsCollectionsManagerPane) collectionsPane;
	}

	public DrawBenzenoidPane getDrawPane() {
		return drawPane;
	}

	public FilteringPane getFilteringPane() {
		return (FilteringPane) filteringPane;
	}

	public void addTask(String task) {
		TaskHBox hBox = new TaskHBox(this, task);
		tasksBoxes.add(hBox);

		MenuItem menuItem = new MenuItem();
		menuItem.setGraphic(hBox);

		tasksMenu.getItems().add(menuItem);

    if (tasksMenu.getItems().size() > 1)
      removeTask ("None");
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
		return (GeneratorPane) generatorPane;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
