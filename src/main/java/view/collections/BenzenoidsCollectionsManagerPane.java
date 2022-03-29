package view.collections;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import application.ApplicationMode;
import application.BenzenoidApplication;
import classifier.Irregularity;
import classifier.MoleculeInformation;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import molecules.Molecule;
import molecules.sort.IrregularityComparator;
import molecules.sort.MoleculeComparator;
import molecules.sort.NbCarbonsComparator;
import molecules.sort.NbHexagonsComparator;
import molecules.sort.NbHydrogensComparator;
import molecules.sort.NbKekuleStructuresComparator;
import new_classifier.NewCarbonsHydrogensClassifier;
import new_classifier.NewClassifier;
import parsers.CMLConverter;
import parsers.ComConverter;
import parsers.ComConverter.ComType;
import parsers.GraphParser;
import solution.ClarCoverSolution;
import solveur.Aromaticity.RIType;
import solveur.ClarCoverSolver;
import spectrums.IRSpectra;
import spectrums.Parameter;
import spectrums.ResultLogFile;
import spectrums.SpectrumsComputer;
import utils.Utils;
import view.collections.BenzenoidCollectionPane.DisplayType;
import view.groups.RBOGroup;
import view.ir_spectra.ComputedPlotPane;
import view.ir_spectra.IRSpectraPane;
import view.irregularity.IrregularityPane;

public class BenzenoidsCollectionsManagerPane extends BorderPane {

	private BenzenoidApplication application;

	private TabPane tabPane;

	private ArrayList<BenzenoidCollectionPane> benzenoidSetPanes;

	private BenzenoidCollectionPane originBenzenoidCollectionPane;
	private ArrayList<BenzenoidPane> copiedBenzenoidPanes;

	private ContextMenu contextMenu;

	private boolean flagCtrl;
	private boolean flagA;
	private boolean flagC;
	private boolean flagV;
	private boolean selectAll;

	private BenzenoidPane hoveringPane;
	private BenzenoidCollectionPane addTab;
	private MenuBar menuBar;
	private boolean removingLock;

	private Parameter parameter;

	private TextArea collectionPropertiesArea;

	private Menu moveItem;

	private Menu moveItemMenu;
	private Menu manageCollection;

	/*
	 * Threads
	 */

	private Service<Void> calculateServiceLin;
	private boolean linRunning;

	private Service<Void> calculateServiceClarCover;
	private boolean clarRunning;

	private Service<Void> calculateServiceRBO;
	private boolean rboRunning;

	private int indexLin;
	private int indexClar;
	private int indexRBO;

	private int lineIndexLin;
	private int lineIndexClar;
	private int lineIndexRBO;

	private int indexDatabase;
	private int lineIndexDatabase;

	public BenzenoidsCollectionsManagerPane(BenzenoidApplication parent) {

		flagCtrl = false;
		flagA = false;
		flagC = false;
		flagV = false;
		selectAll = false;

		this.application = parent;
		initialize();
	}

	private void initialize() {

		parameter = Parameter.defaultParameter();

		collectionPropertiesArea = new TextArea();
		collectionPropertiesArea.setEditable(false);
		collectionPropertiesArea.setPrefRowCount(1);

		initializeMenu();
		initializeContextMenu();

		removingLock = false;

		copiedBenzenoidPanes = new ArrayList<>();
		benzenoidSetPanes = new ArrayList<>();

		createTabPane();

		BenzenoidCollectionPane Tab = new BenzenoidCollectionPane(this, 0, "Collection #1");
		addBenzenoidSetPane(Tab);

		addTab = new BenzenoidCollectionPane(this, benzenoidSetPanes.size(), "+");

		addTab.setOnSelectionChanged(e -> {
			if (addTab.isSelected()) {
				BenzenoidCollectionPane benzenoidSetPane2 = new BenzenoidCollectionPane(this, benzenoidSetPanes.size(),
						getNextCollectionPaneLabel());

				addBenzenoidSetPane(benzenoidSetPane2);
				tabPane.getSelectionModel().clearAndSelect(benzenoidSetPanes.size() - 2);
			}
		});
		addBenzenoidSetPane(addTab);

		this.setCenter(tabPane);
		this.setBottom(collectionPropertiesArea);
	}

	public void copy(ArrayList<BenzenoidPane> copiedBenzenoidPanes) {
		originBenzenoidCollectionPane = getSelectedTab();
		this.copiedBenzenoidPanes.clear();
		this.copiedBenzenoidPanes.addAll(copiedBenzenoidPanes);
		System.out.println(copiedBenzenoidPanes.size() + " benzenoid(s) copied");
	}

	private void createTabPane() {
		// creates the tab pane
		tabPane = new TabPane();

		tabPane.setOnKeyPressed(e -> {

			System.out.println("tabPane.keyPress() (l. 283)");

			if (e.getCode() == KeyCode.CONTROL || e.getCode() == KeyCode.COMMAND)
				flagCtrl = true;

			if (e.getCode() == KeyCode.A)
				flagA = true;

			if (e.getCode() == KeyCode.C)
				flagC = true;

			if (e.getCode() == KeyCode.V)
				flagV = true;

			if (flagCtrl && flagA && !selectAll)
				selectAll();

			if (flagCtrl && flagC) {
				BenzenoidCollectionPane curentPane = getSelectedTab();
				curentPane.copy();
				System.out.println("CTRL + C: " + copiedBenzenoidPanes.size() + " copied");
			}

			if (flagCtrl && flagV)
				paste();

		});

		tabPane.setOnKeyReleased(e -> {

			if (e.getCode() == KeyCode.CONTROL)
				flagCtrl = false;

			if (e.getCode() == KeyCode.A)
				flagA = false;

			if (e.getCode() == KeyCode.C)
				flagC = false;

			if (e.getCode() == KeyCode.V)
				flagV = false;
		});

		tabPane.setOnMouseClicked(e -> {

			if (e.getButton() == MouseButton.PRIMARY) {

				hideContextMenu();

				BenzenoidCollectionPane curentPane = getSelectedTab();

				if (curentPane.getHoveringPane() == null && selectAll) {
					selectAll = false;
					unselectAll();
				}

			} else if (e.getButton() == MouseButton.SECONDARY) {

			}
		});

		tabPane.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
			getSelectedTab().refreshCollectionProperties();
		});
	}

	public void addBenzenoidSetPane(BenzenoidCollectionPane benzenoidSetPane) {

//		String name = benzenoidSetPane.getName();
//		for (BenzenoidCollectionPane)

		String consoleContent = "";
		if (benzenoidSetPanes.size() > 0)
			consoleContent = benzenoidSetPanes.get(0).getConsole().getText();
		benzenoidSetPane.getConsole().append(consoleContent, false);
		benzenoidSetPane.getConsole().setScrollTop(Double.MAX_VALUE);

		if (benzenoidSetPanes.size() > 1) {
			// the added pane is different from the add pane
			benzenoidSetPanes.add(benzenoidSetPanes.size() - 1, benzenoidSetPane);
			tabPane.getTabs().add(benzenoidSetPanes.size() - 2, benzenoidSetPane);
		} else if (benzenoidSetPane.getName().equals("+")) {
			benzenoidSetPanes.add(benzenoidSetPane);
			tabPane.getTabs().add(benzenoidSetPane);
		} else {
			benzenoidSetPanes.add(0, benzenoidSetPane);
			tabPane.getTabs().add(0, benzenoidSetPane);
		}

		System.out.println("Adding new collection: " + benzenoidSetPane.getName());
		System.out.println("Taille " + benzenoidSetPanes.size());

		System.out.println("Collection courante: ");
		for (int i = 0; i < benzenoidSetPanes.size(); i++) {
			System.out.println(benzenoidSetPanes.get(i).getName());
		}
	}

	private void initializeMenu() {

		menuBar = new MenuBar();
		moveItemMenu = new Menu("Move");

		moveItemMenu.setOnAction(e -> {
			refreshMoveItem();
		});
		moveItemMenu.setOnMenuValidation(e -> {
			refreshMoveItem();
		});

		CollectionMenuItem menuItem = new CollectionMenuItem(0, "(none)");
		moveItemMenu.getItems().addAll(menuItem);

		Menu sortMenu = new Menu("Sort");
		Menu nbCarbonsItem = new Menu("Number of carbons");
		Menu nbHydrogensItem = new Menu("Number of hydrogens");
		Menu nbHexagonsItem = new Menu("Number of hexagons");
		Menu nbKekuleStructuresItem = new Menu("Number of Kekulé structures");
		Menu irregularityItem = new Menu("Irregularity");

		/*
		 * Nb Carbons
		 */

		MenuItem nbCarbonsIncreasing = new MenuItem("Increasing");
		MenuItem nbCarbonsDecreasing = new MenuItem("Decreasing");

		nbCarbonsIncreasing.setOnAction(e -> {
			sort(new NbCarbonsComparator(), false);
		});

		nbCarbonsDecreasing.setOnAction(e -> {
			sort(new NbCarbonsComparator(), true);
		});

		nbCarbonsItem.getItems().addAll(nbCarbonsIncreasing, nbCarbonsDecreasing);

		/*
		 * Nb hydrogens
		 */

		MenuItem nbHydrogensIncreasing = new MenuItem("Increasing");
		MenuItem nbHydrogensDecreasing = new MenuItem("Decreasing");

		nbHydrogensIncreasing.setOnAction(e -> {
			sort(new NbHydrogensComparator(), false);
		});

		nbHydrogensDecreasing.setOnAction(e -> {
			sort(new NbHydrogensComparator(), true);
		});

		nbHydrogensItem.getItems().addAll(nbHydrogensIncreasing, nbHydrogensDecreasing);

		/*
		 * Nb Hexagons
		 */

		MenuItem nbHexagonsIncreasing = new MenuItem("Increasing");
		MenuItem nbHexagonsDecreasing = new MenuItem("Decreasing");

		nbHexagonsIncreasing.setOnAction(e -> {
			sort(new NbHexagonsComparator(), false);
		});

		nbHexagonsDecreasing.setOnAction(e -> {
			sort(new NbHexagonsComparator(), true);
		});

		nbHexagonsItem.getItems().addAll(nbHexagonsIncreasing, nbHexagonsDecreasing);

		/*
		 * Nb Kekule Structures
		 */

		MenuItem nbKekuleStructuresIncreasing = new MenuItem("Increasing");
		MenuItem nbKekuleStructuresDecreasing = new MenuItem("Decreasing");

		nbKekuleStructuresIncreasing.setOnAction(e -> {
			sort(new NbKekuleStructuresComparator(), false);
		});

		nbKekuleStructuresDecreasing.setOnAction(e -> {
			sort(new NbKekuleStructuresComparator(), true);
		});

		nbKekuleStructuresItem.getItems().addAll(nbKekuleStructuresIncreasing, nbKekuleStructuresDecreasing);

		/*
		 * Irregularity
		 */

		MenuItem irregularityIncreasing = new MenuItem("Increasing");
		MenuItem irregularityDecreasing = new MenuItem("Decreasing");

		irregularityIncreasing.setOnAction(e -> {
			sort(new IrregularityComparator(), false);
		});

		irregularityDecreasing.setOnAction(e -> {
			sort(new IrregularityComparator(), true);
		});

		irregularityItem.getItems().addAll(irregularityIncreasing, irregularityDecreasing);

		sortMenu.getItems().addAll(nbCarbonsItem, nbHydrogensItem, nbHexagonsItem, nbKekuleStructuresItem,
				irregularityItem);

		Menu filterMenu = new Menu();
		Label filterLabel = new Label("Filter");
		filterMenu.setGraphic(filterLabel);

		filterLabel.setOnMouseClicked(e -> {
			application.switchMode(ApplicationMode.FILTER);
		});

		Menu fileMenu = new Menu("File");
		Menu exportBenzenoidMenu = new Menu("Export benzenoid(s)");
		MenuItem exportGraph = new MenuItem(".graph");
		MenuItem exportPng = new MenuItem(".png");
		MenuItem exportCml = new MenuItem(".cml");
		MenuItem exportCom = new MenuItem(".com");

		MenuItem exportCollection = new MenuItem("Export collection");
		MenuItem importCollection = new MenuItem("Import collection");

		fileMenu.getItems().addAll(exportBenzenoidMenu, exportCollection, importCollection);
		exportBenzenoidMenu.getItems().addAll(exportGraph, exportPng, exportCml, exportCom);

		exportGraph.setOnAction(e -> {
			exportGraph();
		});

		exportPng.setOnAction(e -> {
			exportPng();
		});

		exportCml.setOnAction(e -> {
			exportCML();
		});

		exportCom.setOnAction(e -> {
			exportCOM();
		});

		importCollection.setOnAction(e -> {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			File directory = directoryChooser.showDialog(application.getStage());

			if (directory != null) {
				importCollection(directory);
			}
		});

		exportCollection.setOnAction(e -> {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			File directory = directoryChooser.showDialog(application.getStage());

			if (directory != null) {
				BenzenoidCollectionPane curentPane = getSelectedTab();
				curentPane.export(directory);
			}
		});

		// boulot ici

		manageCollection = new Menu("Manage collection");

		MenuItem itemRename = new MenuItem("Rename");
		MenuItem itemDelete = new MenuItem("Delete benzenoid(s)");
		MenuItem itemCopy = new MenuItem("Copy benzenoid(s)");
		MenuItem itemPaste = new MenuItem("Paster benzenoid(s)");
		MenuItem itemSelect = new MenuItem("Select all");

		itemPaste.setOnAction(e -> {
			paste();
		});

		itemSelect.setOnAction(e -> {
			selectAll();
		});

		itemRename.setOnAction(e -> {
			RenameCollectionPane root;
			root = new RenameCollectionPane(this);
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
			BenzenoidCollectionPane curentPane = getSelectedTab();
			curentPane.removeBenzenoidPanes(curentPane.getSelectedBenzenoidPanes());
		});

		itemCopy.setOnAction(e -> {
			originBenzenoidCollectionPane = getSelectedTab();
			originBenzenoidCollectionPane.copy();
		});

		manageCollection.getItems().addAll(itemRename, itemDelete, itemCopy, itemSelect, moveItemMenu);

		Menu computationsMenu = new Menu("Computations");
		MenuItem reItem = new MenuItem("Resonance Energy (Lin)");
		MenuItem clarItem = new MenuItem("Clar Cover");
		MenuItem rboItem = new MenuItem("Ring Bond Order");
		MenuItem irregularityStatsItem = new MenuItem("IrregularityStatistics");

		reItem.setOnAction(e -> {
			resonanceEnergyLin();
		});

		clarItem.setOnAction(e -> {
			clarCover();
		});

		rboItem.setOnAction(e -> {
			ringBoundOrder();
		});

		irregularityStatsItem.setOnAction(e -> {
			irregularityStatistics();
		});

		computationsMenu.getItems().addAll(reItem, clarItem, rboItem, irregularityStatsItem);

		menuBar.getMenus().addAll(fileMenu, manageCollection, sortMenu, filterMenu, computationsMenu);

		this.setTop(menuBar);

	}

	// ~ private void refresh() {

	// ~ System.out.println("Refresh 1");
	// ~ this.setPrefWidth(1400);

	// ~ this.setPrefWidth(this.getPrefWidth());

	// ~ this.getChildren().clear();

	// ~ this.setTop(menuBar);

	// ~ System.out.println("Taille l563 "+benzenoidSetPanes.size());
	// ~ for (BenzenoidCollectionPane benzenoidSetPane : benzenoidSetPanes)
	// ~ tabPane.getTabs().add(benzenoidSetPane);

	// ~ this.setCenter(tabPane);
	// ~ this.setBottom(collectionPropertiesArea);

	// ~ refreshMoveItem();
	// ~ }

	public void remove(BenzenoidCollectionPane pane) {
		removingLock = true;
		benzenoidSetPanes.remove(pane);

		System.out.println("remove() : " + benzenoidSetPanes.size() + " panes restants");

		// ~ for (int i = 0; i < benzenoidSetPanes.size(); i++)
		// ~ benzenoidSetPanes.get(i).setIndex(i);

		// refresh();
	}

	public int size() {
		return benzenoidSetPanes.size();
	}

	public String getNextCollectionPaneLabel() {
		int i = 1;
		String label = "Collection #" + i;
		boolean again = true;
		System.out.println("Size P " + benzenoidSetPanes.size());
		do {
			label = "Collection #" + i;
			Iterator<BenzenoidCollectionPane> iter = benzenoidSetPanes.iterator();

			while ((iter.hasNext()) && (!iter.next().getName().equals(label))) {
			}

			if (iter.hasNext())
				i++;
			else
				again = false;
		} while (again);

		return label;
	}

	public BenzenoidCollectionPane getSelectedPane() {

		SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
		return (BenzenoidCollectionPane) selectionModel.getSelectedItem();
	}

	public ArrayList<BenzenoidCollectionPane> getBenzenoidSetPanes() {
		return benzenoidSetPanes;
	}

	public BenzenoidApplication getApplication() {
		return application;
	}

	private void initializeContextMenu() {

		contextMenu = new ContextMenu();

		MenuItem renameMenu = new MenuItem("Rename collection");

		moveItem = new Menu("Move");
		MenuItem copyItem = new MenuItem("Copy");
		MenuItem pasteItem = new MenuItem("Paste");
		MenuItem deleteItem = new MenuItem("Delete");

		Menu exportMenu = new Menu("Export");
		Menu exportBenzenoidItem = new Menu("Export benzenoid");
		MenuItem exportPropertiesItem = new MenuItem("Export properties");

		MenuItem exportGraph = new MenuItem(".graph");
		MenuItem exportPng = new MenuItem(".png");
		MenuItem exportCml = new MenuItem(".cml");
		MenuItem exportCom = new MenuItem(".com");
		exportBenzenoidItem.getItems().addAll(exportGraph, exportPng, exportCml, exportCom);

		MenuItem importCollectionItem = new MenuItem("Import collection");
		MenuItem exportCollectionItem = new MenuItem("Export collection");

		MenuItem selectAllItem = new MenuItem("Select all");
		MenuItem unselectAllItem = new MenuItem("Unselect all");
		MenuItem drawItem = new MenuItem("Draw");
		MenuItem irregularityItem = new MenuItem("Irregularity statistics");
		MenuItem reLinItem = new MenuItem("Resonance energy (Lin)");
		MenuItem reLinFanItem = new MenuItem("Resonance energy (Lin & Fan)");
		MenuItem clarItem = new MenuItem("Clar cover");
		MenuItem rboItem = new MenuItem("Ring bond Order");

		MenuItem dbItem = new MenuItem("Find in database (DEBUG)");
		MenuItem irSpectraItem = new MenuItem("IR Spectra");

		MenuItem checkDatabaseItem = new MenuItem("Check database");

		exportMenu.getItems().addAll(exportBenzenoidItem, exportPropertiesItem);

		renameMenu.setOnAction(e -> {

			RenameCollectionPane root;
			root = new RenameCollectionPane(this);
			Stage stage = new Stage();
			stage.setTitle("Rename collection");

			stage.setResizable(false);

			stage.getIcons().add(new Image("/resources/graphics/icon-benzene.png"));

			Scene scene = new Scene(root);
			scene.getStylesheets().add("/resources/style/application.css");

			stage.setScene(scene);
			stage.show();

		});

		exportPropertiesItem.setOnAction(e -> {
			exportProperties();
		});

		exportGraph.setOnAction(e -> {
			exportGraph();
		});

		exportPng.setOnAction(e -> {
			exportPng();
		});

		exportCml.setOnAction(e -> {
			exportCML();
		});

		exportCom.setOnAction(e -> {
			exportCOM();
		});

		copyItem.setOnAction(e -> {
			originBenzenoidCollectionPane = getSelectedTab();
			originBenzenoidCollectionPane.copy();
		});

		pasteItem.setOnAction(e -> {
			paste();
		});

		deleteItem.setOnAction(e -> {
			BenzenoidCollectionPane curentPane = getSelectedTab();
			curentPane.removeBenzenoidPanes(curentPane.getSelectedBenzenoidPanes());
		});

		reLinItem.setOnAction(e -> {
			resonanceEnergyLin();
		});

		reLinFanItem.setOnAction(e -> {
			resonanceEnergyLinFan();
		});

		clarItem.setOnAction(e -> {
			clarCover();
		});

		rboItem.setOnAction(e -> {
			ringBoundOrder();
		});

		irregularityItem.setOnAction(e -> {
			irregularityStatistics();
		});

		selectAllItem.setOnAction(e -> {
			selectAll();
		});

		unselectAllItem.setOnAction(e -> {
			unselectAll();
		});

		drawItem.setOnAction(e -> {
			draw();
		});

		importCollectionItem.setOnAction(e -> {

			DirectoryChooser directoryChooser = new DirectoryChooser();
			File directory = directoryChooser.showDialog(application.getStage());

			if (directory != null) {
				importCollection(directory);
			}
		});

		exportCollectionItem.setOnAction(e -> {

			DirectoryChooser directoryChooser = new DirectoryChooser();
			File directory = directoryChooser.showDialog(application.getStage());

			if (directory != null) {
				BenzenoidCollectionPane curentPane = getSelectedTab();
				curentPane.export(directory);
			}

		});

		dbItem.setOnAction(e -> {
			BenzenoidCollectionPane curentPane = getSelectedTab();
			for (BenzenoidPane pane : curentPane.getSelectedBenzenoidPanes()) {
				Molecule molecule = curentPane.getMolecule(pane.getIndex());
				System.out.println(molecule.getNicsResult());
			}
		});

		irSpectraItem.setOnAction(e -> {
			IRSpectra();
		});

		checkDatabaseItem.setOnAction(e -> {
			checkDatabase();
		});

		contextMenu.getItems().addAll(renameMenu, importCollectionItem, exportCollectionItem, moveItem, copyItem,
				pasteItem, deleteItem, exportMenu, selectAllItem, unselectAllItem, drawItem, irregularityItem,
				reLinItem, clarItem, rboItem/* , reLinFanItem, dbItem */, irSpectraItem, checkDatabaseItem);

		this.setOnContextMenuRequested(e -> {

			moveItem.getItems().clear();
			ArrayList<MenuItem> items = new ArrayList<>();
			BenzenoidCollectionPane curentPane = getSelectedTab();

			hoveringPane = curentPane.getHoveringPane();

			if (hoveringPane != null)
				curentPane.setPropertiesArea(hoveringPane.buildDescription());

			for (int i = 0; i < benzenoidSetPanes.size() - 1; i++) {
				// for (BenzenoidCollectionPane collectionPane : benzenoidSetPanes) {

				BenzenoidCollectionPane collectionPane = benzenoidSetPanes.get(i);

				if (!collectionPane.equals(curentPane)) {

					CollectionMenuItem menuItem = new CollectionMenuItem(collectionPane.getIndex(),
							collectionPane.getName());

					menuItem.setOnAction(e2 -> {

						BenzenoidCollectionPane setPaneOrigin = curentPane;
						BenzenoidCollectionPane setPaneDestination = collectionPane;

						move(setPaneOrigin, setPaneDestination);
					});

					items.add(menuItem);
				}
			}

			moveItem.getItems().addAll(items);
			contextMenu.show(this, e.getScreenX(), e.getScreenY());
		});
	}

	public void move(BenzenoidCollectionPane setPaneOrigin, BenzenoidCollectionPane setPaneDestination) {

		System.out.println("begin move() : origin.benzenoidPanes.size() = " + setPaneOrigin.getBenzenoidPanes().size());
		System.out.println(
				"begin move() : destination.benzenoidPanes.size() = " + setPaneDestination.getBenzenoidPanes().size());

		ArrayList<BenzenoidPane> benzenoidPanesMoved = new ArrayList<>();
		ArrayList<Molecule> moleculesMoved = new ArrayList<>();
		ArrayList<DisplayType> displayTypesMoved = new ArrayList<>();

		for (int i = 0; i < setPaneOrigin.getSelectedBenzenoidPanes().size(); i++) {

			BenzenoidPane benzenoidPane = setPaneOrigin.getSelectedBenzenoidPanes().get(i);
			Molecule molecule = setPaneOrigin.getMolecule(benzenoidPane.getIndex());
			DisplayType displayType = setPaneOrigin.getDisplayType(benzenoidPane.getIndex());

			benzenoidPanesMoved.add(benzenoidPane);
			moleculesMoved.add(molecule);
			displayTypesMoved.add(displayType);
		}

		setPaneOrigin.removeBenzenoidPanes(benzenoidPanesMoved);
		setPaneOrigin.refresh();

		System.out.println(moleculesMoved.size() + " molecules moved");

		for (int i = 0; i < moleculesMoved.size(); i++) {
			setPaneDestination.addBenzenoid(moleculesMoved.get(i), displayTypesMoved.get(i));
		}

		setPaneDestination.refresh();

		System.out.println("end move() : origin.benzenoidPanes.size() = " + setPaneOrigin.getBenzenoidPanes().size());
		System.out.println(
				"end move() : destination.benzenoidPanes.size() = " + setPaneDestination.getBenzenoidPanes().size());
	}

	public BenzenoidCollectionPane getSelectedTab() {
		return benzenoidSetPanes.get(tabPane.getSelectionModel().getSelectedIndex());
	}

	public void hideContextMenu() {
		contextMenu.hide();
	}

	public void paste() {

		BenzenoidCollectionPane destinationPane = getSelectedTab();

		for (BenzenoidPane pane : copiedBenzenoidPanes) {

			BenzenoidCollectionPane originPane = pane.getBenzenoidCollectionPane();

			Molecule molecule = originPane.getMolecule(pane.getIndex());
			DisplayType displayType = originPane.getDisplayType(pane.getIndex());

			destinationPane.addBenzenoid(molecule, displayType);
		}

		destinationPane.refresh();
	}

	public void resonanceEnergyLin() {
		BenzenoidCollectionPane curentPane = getSelectedTab();

		linRunning = true;
		ArrayList<BenzenoidPane> selectedBenzenoidPanes = curentPane.getSelectedBenzenoidPanes();

		String name = "RE Lin";
		BenzenoidCollectionPane benzenoidSetPane = new BenzenoidCollectionPane(this, getBenzenoidSetPanes().size(),
				name);

		application.addTask("RE Lin");

		if (selectedBenzenoidPanes.size() == 0)
			selectAll();

		calculateServiceLin = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {

					@Override
					protected Void call() throws Exception {

						ArrayList<BenzenoidPane> panes = new ArrayList<>();

						for (BenzenoidPane pane : selectedBenzenoidPanes)
							panes.add(pane);

						indexLin = 0;
						int size = panes.size();

						System.out.println("Computing resonance energy of " + size + " benzenoids.");
						log("RE Lin (" + size + " benzenoids)", true);

						for (BenzenoidPane benzenoidPane : panes) {
							if (linRunning) {
								Molecule molecule = curentPane.getMolecule(benzenoidPane.getIndex());
								molecule.getAromaticity();
								benzenoidSetPane.addBenzenoid(molecule, DisplayType.RE_LIN);
								indexLin++;
								System.out.println(indexLin + " / " + size);

								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										if (indexLin == 1) {
											log(indexLin + " / " + size, false);
											lineIndexLin = curentPane.getConsole().getNbLines() - 1;
										} else
											changeLineConsole(indexLin + " / " + size, lineIndexLin);
									}
								});
							}
						}

						return null;
					}

				};
			}
		};

		calculateServiceLin.stateProperty().addListener(new ChangeListener<State>() {

			@SuppressWarnings("incomplete-switch")
			@Override
			public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {

				switch (newValue) {
				case FAILED:
					Utils.alert("No selected benzenoid");
					linRunning = false;
					break;

				case CANCELLED:
					// Utils.alert("No selected benzenoid");
					benzenoidSetPane.refresh();
					tabPane.getSelectionModel().clearAndSelect(0);
					addBenzenoidSetPane(benzenoidSetPane);
					tabPane.getSelectionModel().clearAndSelect(benzenoidSetPanes.size() - 2);
					application.removeTask("RE Lin");
					linRunning = false;
					break;

				case SUCCEEDED:
					benzenoidSetPane.refresh();
					tabPane.getSelectionModel().clearAndSelect(0);
					addBenzenoidSetPane(benzenoidSetPane);
					tabPane.getSelectionModel().clearAndSelect(benzenoidSetPanes.size() - 2);
					application.removeTask("RE Lin");
					linRunning = false;
					break;
				}
			}
		});

		calculateServiceLin.start();
	}

	public void resonanceEnergyLinFan() {
		BenzenoidCollectionPane curentPane = getSelectedTab();

		ArrayList<BenzenoidPane> selectedBenzenoidPanes = curentPane.getSelectedBenzenoidPanes();

		String name = "RE Lin&Fan";
		BenzenoidCollectionPane benzenoidSetPane = new BenzenoidCollectionPane(this, getBenzenoidSetPanes().size(),
				name);

		for (BenzenoidPane benzenoidPane : selectedBenzenoidPanes) {
			Molecule molecule = curentPane.getMolecule(benzenoidPane.getIndex());
			benzenoidSetPane.addBenzenoid(molecule, DisplayType.RE_LIN_FAN);
		}

		benzenoidSetPane.refresh();

		tabPane.getSelectionModel().clearAndSelect(0);
		addBenzenoidSetPane(benzenoidSetPane);
		tabPane.getSelectionModel().clearAndSelect(benzenoidSetPanes.size() - 2);
	}

	public void clarCover() {

		BenzenoidCollectionPane curentPane = getSelectedTab();
		ArrayList<BenzenoidPane> selectedBenzenoidPanes = curentPane.getSelectedBenzenoidPanes();

		String name = "Clar cover";
		BenzenoidCollectionPane benzenoidSetPane = new BenzenoidCollectionPane(this, getBenzenoidSetPanes().size(),
				name);

		application.addTask("Clar cover");

		clarRunning = true;

		if (selectedBenzenoidPanes.size() == 0) {
			selectAll();
		}

		calculateServiceClarCover = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {

					@Override
					protected Void call() throws Exception {

						ArrayList<BenzenoidPane> panes = new ArrayList<>();

						for (BenzenoidPane pane : selectedBenzenoidPanes)
							panes.add(pane);

						indexClar = 0;
						int size = panes.size();

						System.out.println("Computing Clar Cover of " + size + "benzenoids");
						log("Clar Cover (" + size + "benzenoids)", true);

						for (BenzenoidPane benzenoidPane : panes) {
							if (clarRunning) {
								Molecule molecule = curentPane.getMolecule(benzenoidPane.getIndex());

								ArrayList<ClarCoverSolution> clarCoverSolutions = ClarCoverSolver.solve(molecule);
								if (clarCoverSolutions.size() > 0) {
									ClarCoverSolution clarCoverSolution = clarCoverSolutions
											.get(clarCoverSolutions.size() - 1);
									molecule.setClarCoverSolution(clarCoverSolution);
									benzenoidSetPane.addBenzenoid(molecule, DisplayType.CLAR_COVER);
								}
								indexClar++;
								System.out.println(indexClar + " / " + size);

								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										if (indexClar == 1) {
											log(indexClar + " / " + size, false);
											lineIndexClar = curentPane.getConsole().getNbLines() - 1;
										} else
											changeLineConsole(indexClar + " / " + size, lineIndexClar);
									}
								});

							}
						}

						return null;
					}

				};
			}
		};

		calculateServiceClarCover.stateProperty().addListener(new ChangeListener<State>() {

			@SuppressWarnings("incomplete-switch")
			@Override
			public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {

				switch (newValue) {
				case FAILED:
					clarRunning = false;
					Utils.alert("Failed");
					break;

				case CANCELLED:
					clarRunning = false;
					benzenoidSetPane.refresh();
					tabPane.getSelectionModel().clearAndSelect(0);
					addBenzenoidSetPane(benzenoidSetPane);
					tabPane.getSelectionModel().clearAndSelect(benzenoidSetPanes.size() - 2);
					application.removeTask("Clar cover");
					break;

				case SUCCEEDED:
					clarRunning = false;
					benzenoidSetPane.refresh();
					tabPane.getSelectionModel().clearAndSelect(0);
					addBenzenoidSetPane(benzenoidSetPane);
					tabPane.getSelectionModel().clearAndSelect(benzenoidSetPanes.size() - 2);
					application.removeTask("Clar cover");
					break;
				}
			}
		});

		calculateServiceClarCover.start();
	}

	public void ringBoundOrder() {

		BenzenoidCollectionPane curentPane = getSelectedTab();
		ArrayList<BenzenoidPane> selectedBenzenoidPanes = curentPane.getSelectedBenzenoidPanes();

		String name = "RBO";
		BenzenoidCollectionPane benzenoidSetPane = new BenzenoidCollectionPane(this, getBenzenoidSetPanes().size(),
				name);

		BenzenoidsCollectionsManagerPane manager = this;

		application.addTask("Ring Bond Order");

		rboRunning = true;

		if (selectedBenzenoidPanes.size() == 0) {
			selectAll();
		}

		calculateServiceRBO = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {

					@Override
					protected Void call() throws Exception {

						ArrayList<BenzenoidPane> panes = new ArrayList<>();

						for (BenzenoidPane pane : selectedBenzenoidPanes)
							panes.add(pane);

						indexRBO = 0;
						int size = panes.size();

						System.out.println("Computing Clar Cover of " + size + "benzenoids");
						log("Clar cover (" + size + " benzenoids)", true);

						for (BenzenoidPane benzenoidPane : panes) {
							if (rboRunning) {
								Molecule molecule = curentPane.getMolecule(benzenoidPane.getIndex());
								molecule.getRBO();
								molecule.setRBOGroup(new RBOGroup(manager, molecule));
								benzenoidSetPane.addBenzenoid(molecule, DisplayType.RBO);
								indexRBO++;
								System.out.println(indexRBO + " / " + size);

								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										if (indexRBO == 1) {
											log(indexRBO + " / " + size, true);
											lineIndexRBO = curentPane.getConsole().getNbLines() - 1;
										} else
											changeLineConsole(indexRBO + " / " + size, lineIndexRBO);
									}
								});

							}
						}

						return null;
					}

				};
			}
		};

		calculateServiceRBO.stateProperty().addListener(new ChangeListener<State>() {

			@SuppressWarnings("incomplete-switch")
			@Override
			public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {

				switch (newValue) {
				case FAILED:
					rboRunning = false;
					Utils.alert("Failed");
					break;

				case CANCELLED:
					rboRunning = false;
					benzenoidSetPane.refresh();
					tabPane.getSelectionModel().clearAndSelect(0);
					addBenzenoidSetPane(benzenoidSetPane);
					tabPane.getSelectionModel().clearAndSelect(benzenoidSetPanes.size() - 2);
					application.removeTask("Ring Bond Order");
					break;

				case SUCCEEDED:
					rboRunning = false;
					benzenoidSetPane.refresh();
					tabPane.getSelectionModel().clearAndSelect(0);
					addBenzenoidSetPane(benzenoidSetPane);
					tabPane.getSelectionModel().clearAndSelect(benzenoidSetPanes.size() - 2);
					application.removeTask("Ring Bond Order");
					break;
				}
			}
		});

		calculateServiceRBO.start();
	}

	public void irregularityStatistics() {

		BenzenoidCollectionPane curentPane = getSelectedTab();
		ArrayList<BenzenoidPane> selectedBenzenoidPanes = curentPane.getSelectedBenzenoidPanes();
		ArrayList<Molecule> molecules = new ArrayList<>();

		for (BenzenoidPane benzenoidPane : selectedBenzenoidPanes) {
			Molecule molecule = curentPane.getMolecule(benzenoidPane.getIndex());
			molecules.add(molecule);
		}

		IrregularityPane root;
		try {
			root = new IrregularityPane(this, molecules, 0.1);
			Stage stage = new Stage();
			stage.setTitle("Irregularity stats");

			stage.setResizable(false);

			stage.getIcons().add(new Image("/resources/graphics/icon-benzene.png"));

			Scene scene = new Scene(root);
			scene.getStylesheets().add("/resources/style/application.css");

			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void selectAll() {

		selectAll = true;

		BenzenoidCollectionPane curentPane = getSelectedTab();
		ArrayList<BenzenoidPane> benzenoidPanes = curentPane.getBenzenoidPanes();

		for (BenzenoidPane benzenoidPane : benzenoidPanes) {

			if (!benzenoidPane.isSelected())
				benzenoidPane.select();
		}

	}

	public void unselectAll() {

		BenzenoidCollectionPane curentPane = getSelectedTab();
		ArrayList<BenzenoidPane> benzenoidPanes = curentPane.getBenzenoidPanes();

		for (BenzenoidPane benzenoidPane : benzenoidPanes) {

			if (benzenoidPane.isSelected())
				benzenoidPane.unselect();
		}
	}

	public boolean isSelectAllActivated() {
		return selectAll;
	}

	public void disableSelectAll() {
		selectAll = false;
	}

	public void draw() {
		BenzenoidCollectionPane curentPane = getSelectedTab();

		if (curentPane.getSelectedBenzenoidPanes().size() == 1) {
			Molecule molecule = curentPane.getMolecule(curentPane.getSelectedBenzenoidPanes().get(0).getIndex());
			application.getDrawPane().importBenzenoid(molecule);
			application.switchMode(ApplicationMode.DRAW);

		}

		else if (hoveringPane != null) {
			Molecule molecule = curentPane.getMolecule(hoveringPane.getIndex());
			application.getDrawPane().importBenzenoid(molecule);
			application.switchMode(ApplicationMode.DRAW);
		}
	}

	public void exportPng() {

		BenzenoidCollectionPane curentPane = getSelectedTab();

		if (curentPane.getSelectedBenzenoidPanes().size() == 0) {
			if (hoveringPane != null) {
				FileChooser fileChooser = new FileChooser();
				File file = fileChooser.showSaveDialog(application.getStage());

				if (file != null) {
					hoveringPane.exportAsPNG(file);
				}
			}
		}

		else {

			if (curentPane.getSelectedBenzenoidPanes().size() == 1) {
				BenzenoidPane benzenoidPane = curentPane.getSelectedBenzenoidPanes().get(0);

				FileChooser fileChooser = new FileChooser();
				File file = fileChooser.showSaveDialog(application.getStage());

				if (file != null) {
					benzenoidPane.exportAsPNG(file);
				}
			}

			else {

				DirectoryChooser directoryChooser = new DirectoryChooser();
				File file = directoryChooser.showDialog(application.getStage());

				if (file != null) {

					String directoryPath = file.getAbsolutePath();

					for (int i = 0; i < curentPane.getSelectedBenzenoidPanes().size(); i++) {

						BenzenoidPane benzenoidPane = curentPane.getSelectedBenzenoidPanes().get(i);
						Molecule molecule = curentPane.getMolecule(benzenoidPane.getIndex());

						File moleculeFile;
						if (molecule.getDescription() != null && !molecule.getDescription().equals(""))
							moleculeFile = new File(
									directoryPath + "/" + molecule.getDescription().replace("\n", "") + ".png");
						else
							moleculeFile = new File(directoryPath + "/" + "solution_" + i + ".png");

						benzenoidPane.exportAsPNG(moleculeFile);

					}

				}
			}
		}
	}

	public void exportProperties() {

		BenzenoidCollectionPane curentPane = getSelectedTab();

		if (curentPane.getSelectedBenzenoidPanes().size() == 0) {
			if (hoveringPane != null) {
				FileChooser fileChooser = new FileChooser();
				File file = fileChooser.showSaveDialog(application.getStage());

				if (file != null) {
					try {
						curentPane.getMolecule(hoveringPane.getIndex()).exportProperties(file);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		else {

			if (curentPane.getSelectedBenzenoidPanes().size() == 1) {
				BenzenoidPane benzenoidPane = curentPane.getSelectedBenzenoidPanes().get(0);

				FileChooser fileChooser = new FileChooser();
				File file = fileChooser.showSaveDialog(application.getStage());

				if (file != null) {
					try {
						curentPane.getMolecule(benzenoidPane.getIndex()).exportProperties(file);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			else {

				DirectoryChooser directoryChooser = new DirectoryChooser();
				File file = directoryChooser.showDialog(application.getStage());

				if (file != null) {

					String directoryPath = file.getAbsolutePath();

					for (int i = 0; i < curentPane.getSelectedBenzenoidPanes().size(); i++) {

						BenzenoidPane benzenoidPane = curentPane.getSelectedBenzenoidPanes().get(i);
						Molecule molecule = curentPane.getMolecule(benzenoidPane.getIndex());

						try {
							File moleculeFile;
							if (molecule.getDescription() != null && !molecule.getDescription().equals(""))
								moleculeFile = new File(
										directoryPath + "/" + molecule.getDescription().replace("\n", "") + ".csv");
							else
								moleculeFile = new File(directoryPath + "/" + "solution_" + i + ".csv");

							molecule.exportProperties(moleculeFile);

						} catch (IOException e) {
							e.printStackTrace();
						}

					}

				}
			}

		}
	}

	public ArrayList<BenzenoidPane> getCopiedBenzenoidPanes() {
		return copiedBenzenoidPanes;
	}

	public void refreshColorScales() {
		for (BenzenoidCollectionPane collectionPane : benzenoidSetPanes)
			collectionPane.refreshColorScales();
	}

	private void sort(MoleculeComparator comparator, boolean ascending) {
		BenzenoidCollectionPane curentPane = getSelectedTab();
		curentPane.setComparator(comparator);
		curentPane.sort(ascending);
	}

	public int getNbCollectionPanes() {
		return benzenoidSetPanes.size() - 1;
	}

	public TabPane getTabPane() {
		return tabPane;
	}

	public void importCollection(File directory) {

		System.out.println("import collection");

		boolean ok = true;

		BenzenoidCollectionPane collectionPane = null;

		try {

			collectionPane = new BenzenoidCollectionPane(this, getNbCollectionPanes(), directory.getName());

			File[] listOfFiles = directory.listFiles();

			for (int i = 0; i < listOfFiles.length; i++) {
				File file = listOfFiles[i];
				if (file.isFile() && file.getName().endsWith(".graph_coord")) {
					Molecule molecule = GraphParser.parseUndirectedGraph(file);
					molecule.setDescription(file.getName());
					collectionPane.addBenzenoid(molecule, DisplayType.BASIC);
				}
			}

			collectionPane.refresh();

		} catch (Exception e) {
			e.printStackTrace();
			ok = false;
		}

		if (ok && collectionPane != null) {
			tabPane.getSelectionModel().clearAndSelect(0);
			addBenzenoidSetPane(collectionPane);
			tabPane.getSelectionModel().clearAndSelect(benzenoidSetPanes.size() - 2);
			// addBenzenoidSetPane(collectionPane);
		} else
			Utils.alert("Error while importing collection");

	}

	private void exportCOM() {

		BenzenoidCollectionPane curentPane = getSelectedTab();

		if (curentPane.getSelectedBenzenoidPanes().size() == 0) {

			if (hoveringPane != null) {

				FileChooser fileChooser = new FileChooser();
				File file = fileChooser.showSaveDialog(application.getStage());

				if (file != null) {
					Molecule molecule = curentPane.getMolecule(hoveringPane.getIndex());
					try {
						ComConverter.generateComFile(molecule, file, 0, ComType.ER, file.getName());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		else {

			DirectoryChooser directoryChooser = new DirectoryChooser();
			File directory = directoryChooser.showDialog(application.getStage());

			if (directory != null) {

				String directoryPath = directory.getAbsolutePath();

				for (int i = 0; i < curentPane.getSelectedBenzenoidPanes().size(); i++) {

					Molecule molecule = curentPane
							.getMolecule(curentPane.getSelectedBenzenoidPanes().get(i).getIndex());

					File file = new File(directoryPath + "/molecule_" + i + ".cml");
					try {
						ComConverter.generateComFile(molecule, file, 0, ComType.ER, file.getName());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void exportCML() {

		BenzenoidCollectionPane curentPane = getSelectedTab();

		if (curentPane.getSelectedBenzenoidPanes().size() == 0) {

			if (hoveringPane != null) {

				FileChooser fileChooser = new FileChooser();
				File file = fileChooser.showSaveDialog(application.getStage());

				if (file != null) {
					Molecule molecule = curentPane.getMolecule(hoveringPane.getIndex());
					try {
						CMLConverter.generateCmlFile(molecule, file);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		else {

			DirectoryChooser directoryChooser = new DirectoryChooser();
			File directory = directoryChooser.showDialog(application.getStage());

			if (directory != null) {

				String directoryPath = directory.getAbsolutePath();

				for (int i = 0; i < curentPane.getSelectedBenzenoidPanes().size(); i++) {

					Molecule molecule = curentPane
							.getMolecule(curentPane.getSelectedBenzenoidPanes().get(i).getIndex());
					File file = new File(directoryPath + "/molecule_" + i + ".cml");
					try {
						CMLConverter.generateCmlFile(molecule, file);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

	}

	private void exportGraph() {

		BenzenoidCollectionPane curentPane = getSelectedTab();

		if (curentPane.getSelectedBenzenoidPanes().size() == 0) {

			if (hoveringPane != null) {

				FileChooser fileChooser = new FileChooser();
				File file = fileChooser.showSaveDialog(application.getStage());

				if (file != null) {
					try {
						curentPane.getMolecule(hoveringPane.getIndex()).exportToGraphFile(file);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		else {

			DirectoryChooser directoryChooser = new DirectoryChooser();
			File directory = directoryChooser.showDialog(application.getStage());

			if (directory != null) {

				String directoryPath = directory.getAbsolutePath();

				for (int i = 0; i < curentPane.getSelectedBenzenoidPanes().size(); i++) {

					Molecule molecule = curentPane
							.getMolecule(curentPane.getSelectedBenzenoidPanes().get(i).getIndex());
					File file = new File(directoryPath + "/molecule_" + i + ".graph");
					try {
						molecule.exportToGraphFile(file);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

	}

	public void refreshRIType(RIType type) {
		// BenzenoidCollectionPane curentPane = getSelectedTab();
		for (int i = 0; i < benzenoidSetPanes.size() - 1; i++) {
			BenzenoidCollectionPane pane = benzenoidSetPanes.get(i);
			pane.refreshRIType(type);
		}

	}

	public void setIRSpectraParameter(Parameter parameter) {
		this.parameter = parameter;
	}

	public void renameCurentTab(String name) {
		BenzenoidCollectionPane collectionPane = getSelectedTab();
		collectionPane.setText(name);
	}

	public void stopLin() {
		linRunning = false;
		calculateServiceLin.cancel();
	}

	public void stopClar() {
		clarRunning = false;
		calculateServiceClarCover.cancel();
	}

	public void stopRBO() {
		rboRunning = false;
		calculateServiceRBO.cancel();
	}

	public void setCollectionPropertiesText(String text) {
		collectionPropertiesArea.setText(text);
	}

	public void log(String line, boolean displayDate) {
		for (BenzenoidCollectionPane pane : benzenoidSetPanes) {
			pane.getConsole().append(line, displayDate);
		}
	}

	public void changeLineConsole(String line, int lineIndex) {
		for (BenzenoidCollectionPane pane : benzenoidSetPanes)
			pane.getConsole().changeLine(line, lineIndex);
	}

	public void clearConsoles() {
		for (BenzenoidCollectionPane pane : benzenoidSetPanes)
			pane.getConsole().clear();
	}

	private void checkDatabase() {

		BenzenoidCollectionPane curentPane = getSelectedTab();

		log("Requesting database (" + curentPane.getName() + ", " + curentPane.getSelectedBenzenoidPanes().size()
				+ " benzenoids)", true);

		Service<Void> calculateService = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {

					@Override
					protected Void call() throws Exception {

						indexDatabase = 1;
						int size = curentPane.getSelectedBenzenoidPanes().size();

						for (BenzenoidPane pane : curentPane.getSelectedBenzenoidPanes()) {

							Molecule molecule = curentPane.getMolecule(pane.getIndex());

							if (!molecule.databaseChecked()) {
								if (molecule.getNicsResult() != null) {
									System.out.println(molecule);

									Platform.runLater(new Runnable() {
										@Override
										public void run() {
											Image image = new Image("/resources/graphics/icon-database.png");
											ImageView imgView = new ImageView(image);
											imgView.resize(30, 30);
											Tooltip.install(imgView,
													new Tooltip("This molecule exists in the database"));
											pane.getDescriptionBox().getChildren().add(imgView);

											if (indexDatabase == 1) {
												log(indexDatabase + "/" + size, false);
												lineIndexDatabase = curentPane.getConsole().getNbLines() - 1;
											}

											else {
												changeLineConsole(indexDatabase + "/" + size, lineIndexDatabase);
											}

											indexDatabase++;
										}

									});

									pane.buildFrequencies();
									pane.buildIntensities();
									pane.buildEnergies();
								}
							}
						}

						return null;
					}

				};
			}
		};

		calculateService.stateProperty().addListener(new ChangeListener<State>() {

			@SuppressWarnings("incomplete-switch")
			@Override
			public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {

				switch (newValue) {
				case FAILED:
					System.out.println("failed");
					break;

				case CANCELLED:
					System.out.println("canceled");
					break;

				case SUCCEEDED:
					System.out.println("succeeded");
					unselectAll();
					break;
				}
			}
		});

		calculateService.start();

	}

	private void IRSpectra() {

		BenzenoidCollectionPane curentPane = getSelectedTab();

		log("Requesting database (" + curentPane.getName() + ", " + curentPane.getSelectedBenzenoidPanes().size()
				+ " benzenoids)", true);

		ArrayList<BenzenoidPane> panes = new ArrayList<>();
		for (BenzenoidPane pane : curentPane.getSelectedBenzenoidPanes())
			panes.add(pane);

		Service<Void> calculateService = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {

					@Override
					protected Void call() throws Exception {

						indexDatabase = 1;
						int size = panes.size();

						for (BenzenoidPane pane : panes) {

							Molecule molecule = curentPane.getMolecule(pane.getIndex());

							if (!molecule.databaseChecked()) {
								if (molecule.getNicsResult() != null) {
									System.out.println(molecule);

									Platform.runLater(new Runnable() {
										@Override
										public void run() {
											Image image = new Image("/resources/graphics/icon-database.png");
											ImageView imgView = new ImageView(image);
											imgView.resize(30, 30);
											Tooltip.install(imgView,
													new Tooltip("This molecule exists in the database"));
											pane.getDescriptionBox().getChildren().add(imgView);

											if (indexDatabase == 1) {
												log(indexDatabase + "/" + size, false);
												lineIndexDatabase = curentPane.getConsole().getNbLines() - 1;
											}

											else {
												changeLineConsole(indexDatabase + "/" + size, lineIndexDatabase);
											}

											indexDatabase++;
										}

									});

									pane.buildFrequencies();
									pane.buildIntensities();
									pane.buildEnergies();
								}
							}
						}

						return null;
					}

				};
			}
		};

		calculateService.stateProperty().addListener(new ChangeListener<State>() {

			@SuppressWarnings("incomplete-switch")
			@Override
			public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {

				switch (newValue) {
				case FAILED:
					System.out.println("failed");
					break;

				case CANCELLED:
					System.out.println("canceled");
					break;

				case SUCCEEDED:
					System.out.println("succeeded");
					unselectAll();
					displayIRSpectra(panes, curentPane);
					break;
				}
			}
		});

		calculateService.start();

	}

	private void displayIRSpectra(ArrayList<BenzenoidPane> panes, BenzenoidCollectionPane curentPane) {

		ArrayList<Molecule> moleculesInDB = new ArrayList<>();

		for (BenzenoidPane pane : panes) {

			Molecule molecule = curentPane.getMolecule(pane.getIndex());
			if (molecule.getNicsResult() != null)
				moleculesInDB.add(molecule);
		}

		log("IR Spectra (" + curentPane.getName() + ", " + moleculesInDB.size() + " benzenoids)", true);

		HashMap<String, MoleculeInformation> moleculesInformations = new HashMap<String, MoleculeInformation>();

		for (Molecule molecule : moleculesInDB) {
			MoleculeInformation information = new MoleculeInformation(molecule.toString(), molecule);
			moleculesInformations.put(molecule.toString(), information);
		}

		NewClassifier classifier = new NewCarbonsHydrogensClassifier(moleculesInDB);
		HashMap<String, ArrayList<Molecule>> classes = classifier.classify();

		ArrayList<IRSpectra> spectraDatas = new ArrayList<>();

		for (Map.Entry<String, ArrayList<Molecule>> entry : classes.entrySet()) {

			String key = entry.getKey();
			ArrayList<Molecule> moleculesClasses = entry.getValue();

			System.out.println("Treating " + key);

			ArrayList<ResultLogFile> classResults = new ArrayList<>();
			HashMap<String, Double> finalEnergies = new HashMap<>();
			HashMap<String, Double> irregularities = new HashMap<>();

			for (Molecule molecule : moleculesClasses) {

				ResultLogFile result = molecule.getNicsResult();
				classResults.add(result);
				finalEnergies.put(molecule.getNames().get(0),
						result.getFinalEnergy().get(result.getFinalEnergy().size() - 1));

				Irregularity irregularity = molecule.getIrregularity();
				if (irregularity == null)
					irregularities.put(molecule.getNames().get(0), -1.0);
				else
					irregularities.put(molecule.getNames().get(0), irregularity.getXI());
			}

			try {
				IRSpectra spectraData = SpectrumsComputer.buildSpectraData(key, moleculesClasses, parameter);
				spectraData.setFinalEnergies(finalEnergies);
				spectraData.setIrregularities(irregularities);
				spectraDatas.add(spectraData);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		ArrayList<ComputedPlotPane> plotPanes = new ArrayList<>();
		for (IRSpectra spectraData : spectraDatas)
			plotPanes.add(new ComputedPlotPane(spectraData));

		Region plotPane = new IRSpectraPane(plotPanes, this, parameter);
		Stage stage = new Stage();
		stage.setTitle("Intensities");

		Scene scene = new Scene(plotPane, 823, 515);
		scene.getStylesheets().add("/resources/style/application.css");

		stage.setScene(scene);
		stage.show();
	}

	private void refreshMoveItem() {
		moveItemMenu.getItems().clear();
		ArrayList<MenuItem> items = new ArrayList<>();
		BenzenoidCollectionPane curentPane = getSelectedTab();

		hoveringPane = curentPane.getHoveringPane();

		if (hoveringPane != null)
			curentPane.setPropertiesArea(hoveringPane.buildDescription());

		for (int i = 0; i < benzenoidSetPanes.size() - 1; i++) {
			System.out.println(benzenoidSetPanes.get(i).getName());
		}

		if (benzenoidSetPanes.size() <= 2) {
			CollectionMenuItem menuItem = new CollectionMenuItem(0, "(none)");
			items.add(menuItem);
		} else {
			for (int i = 0; i < benzenoidSetPanes.size() - 1; i++) {

				BenzenoidCollectionPane collectionPane = benzenoidSetPanes.get(i);

				if (!collectionPane.equals(curentPane)) {

					CollectionMenuItem menuItem = new CollectionMenuItem(collectionPane.getIndex(),
							collectionPane.getName());

					menuItem.setOnAction(e2 -> {

						BenzenoidCollectionPane setPaneOrigin = curentPane;
						BenzenoidCollectionPane setPaneDestination = collectionPane;

						move(setPaneOrigin, setPaneDestination);
					});

					items.add(menuItem);
				}
			}
		}

		moveItemMenu.getItems().addAll(items);
	}
}
