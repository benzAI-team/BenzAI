package view.collections;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import molecules.Molecule;
import molecules.sort.MoleculeComparator;
import molecules.sort.ResonanceEnergyComparator;
import new_classifier.NewCarbonsHydrogensClassifier;
import new_classifier.NewClassifier;
import parsers.CMLConverter;
import parsers.ComConverter;
import parsers.ComConverter.ComType;
import parsers.GraphParser;
import solution.ClarCoverSolution;
import solveur.Aromaticity.RIType;
import solveur.ClarCoverForcedRadicalsSolver;
import solveur.ClarCoverSolver;
import solveur.KekuleStructureSolver;
import spectrums.IRSpectra;
import spectrums.Parameter;
import spectrums.ResultLogFile;
import spectrums.SpectrumsComputer;
import utils.Utils;
import view.collections.BenzenoidCollectionPane.DisplayType;
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

	private boolean selectAll;

	private BenzenoidPane hoveringPane;
	private BenzenoidCollectionPane addTab;

	private Parameter parameter;

	private TextArea collectionPropertiesArea;

	private Menu moveItem;

	private Menu moveItemMenu;

	/*
	 * Threads
	 */

	private Service<Void> calculateServiceLin;
	private boolean linRunning;
	private int indexLin;
	private int lineIndexLin;

	private Service<Void> calculateServiceClarCover;
	private boolean clarRunning;
	private int indexClar;
	private int lineIndexClar;

	private Service<Void> calculateServiceRadicalar;
	private boolean radicalarRunning;

	private Service<Void> calculateServiceRBO;
	private boolean rboRunning;
	private int indexRBO;
	private int lineIndexRBO;


	private int indexDatabase;
	private int lineIndexDatabase;

	public BenzenoidsCollectionsManagerPane(BenzenoidApplication parent) {

		selectAll = false;

		this.application = parent;
		initialize();
	}

	private void initialize() {

		parameter = Parameter.defaultParameter();

		collectionPropertiesArea = new TextArea();
		collectionPropertiesArea.setEditable(false);
		collectionPropertiesArea.setPrefRowCount(1);

		initializeContextMenu();

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

		log("Copying " + copiedBenzenoidPanes.size() + " benzenoid(s) from " + originBenzenoidCollectionPane.getName(),
				true);
	}

	private void createTabPane() {
		// creates the tab pane
		tabPane = new TabPane();

		// definition of the key combination
		KeyCombination Ctrl_a = new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN);
		KeyCombination Ctrl_c = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);
		KeyCombination Ctrl_n = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);
		KeyCombination Ctrl_v = new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN);
		KeyCombination Ctrl_w = new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN);
		KeyCombination Ctrl_pagedown = new KeyCodeCombination(KeyCode.PAGE_DOWN, KeyCombination.CONTROL_DOWN);
		KeyCombination Ctrl_pageup = new KeyCodeCombination(KeyCode.PAGE_UP, KeyCombination.CONTROL_DOWN);
		KeyCombination Alt_c = new KeyCodeCombination(KeyCode.C, KeyCombination.ALT_DOWN);
		KeyCombination Alt_t = new KeyCodeCombination(KeyCode.T, KeyCombination.ALT_DOWN);
		KeyCombination Left = new KeyCodeCombination(KeyCode.LEFT);
		KeyCombination Right = new KeyCodeCombination(KeyCode.RIGHT);
		KeyCombination Down = new KeyCodeCombination(KeyCode.DOWN);
		KeyCombination Up = new KeyCodeCombination(KeyCode.UP);
		KeyCombination End = new KeyCodeCombination(KeyCode.END);

		// treatment of the pressed key(s)
		tabPane.addEventFilter(KeyEvent.KEY_PRESSED, evt -> {
			if (Ctrl_a.match(evt)) { // selects all benzenoids
				selectAll();
				evt.consume();
			} else if (Ctrl_c.match(evt)) { // copies the selected benzenoids
				BenzenoidCollectionPane currentPane = getSelectedTab();
				currentPane.copy();
				evt.consume();
			} else if (Ctrl_v.match(evt)) { // pastes the copied benzenoids
				paste();
				evt.consume();
			} else if (Ctrl_w.match(evt)) { // closes the current tab
				remove(benzenoidSetPanes.get(tabPane.getSelectionModel().getSelectedIndex()));
				tabPane.getTabs().remove(tabPane.getSelectionModel().getSelectedItem());

				evt.consume();
			} else if ((Ctrl_pagedown.match(evt)) || (Right.match(evt) || (Down.match(evt)))) { // moves to the next tab
																								// but skips the add
				if (tabPane.getSelectionModel().getSelectedIndex() == benzenoidSetPanes.size() - 2) {
					tabPane.getSelectionModel().select(0);
					evt.consume();
				}
			} else if ((Ctrl_pageup.match(evt)) || (Left.match(evt) || (Up.match(evt)))) { // moves to the previous tab
																							// but skips the add tab
				if (tabPane.getSelectionModel().getSelectedIndex() == 0) {
					tabPane.getSelectionModel().select(benzenoidSetPanes.size() - 2);
					evt.consume();
				}
			} else if (End.match(evt)) { // moves to the last tab (i.e. the tab before the add tab)
				tabPane.getSelectionModel().select(benzenoidSetPanes.size() - 2);
				evt.consume();
			} else if (Ctrl_n.match(evt)) { // adds a new tab
				BenzenoidCollectionPane benzenoidSetPane2 = new BenzenoidCollectionPane(this, benzenoidSetPanes.size(),
						getNextCollectionPaneLabel());
				addBenzenoidSetPane(benzenoidSetPane2);
				tabPane.getSelectionModel().clearAndSelect(benzenoidSetPanes.size() - 2);
				evt.consume();
			} else if (Alt_c.match(evt)) { // runs the collection windows
				application.switchMode(application.getPanes().getCollectionsPane());
				evt.consume();
			} else if (Alt_t.match(evt)) { // runs the filter windows
				application.switchMode(application.getPanes().getFilteringPane());
				evt.consume();
			}
		});

		tabPane.setOnMouseClicked(e -> {

			if (e.getButton() == MouseButton.PRIMARY) {

				hideContextMenu();

				BenzenoidCollectionPane currentPane = getSelectedTab();

				if (currentPane.getHoveringPane() == null && selectAll) {
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
	}

	public Menu initializeMoveMenuItem() {

		moveItemMenu = new Menu("Move");

		moveItemMenu.setOnAction(e -> {
			refreshMoveItem();
		});
		moveItemMenu.setOnMenuValidation(e -> {
			refreshMoveItem();
		});

		CollectionMenuItem menuItem = new CollectionMenuItem(0, "(none)");
		moveItemMenu.getItems().addAll(menuItem);

		return moveItemMenu;
	}

	public void remove(BenzenoidCollectionPane pane) {
		benzenoidSetPanes.remove(pane);

		System.out.println("remove() : " + benzenoidSetPanes.size() + " panes restants");
	}

	public int size() {
		return benzenoidSetPanes.size();
	}

	public boolean isCollectionPaneLabel(String name) {
		// returns true if name is the label of an existing pane
		Iterator<BenzenoidCollectionPane> iter = benzenoidSetPanes.iterator();

		while ((iter.hasNext()) && (!iter.next().getName().equals(name))) {
		}

		return iter.hasNext();
	}

	public String getNextCollectionPaneLabel() {
		// returns the next label of the form "collection #num"
		int i = 1;
		String label;
		do {
			label = "Collection #" + i;
			i = i + 1;
		} while (isCollectionPaneLabel(label));

		return label;
	}

	public String getNextCollectionPaneLabel(String name) {
		// returns the next label of the form "collection #num"
		if (isCollectionPaneLabel(name)) {
			int i = 1;
			String label;
			do {
				label = name + "(" + i + ")";
				i = i + 1;
			} while (isCollectionPaneLabel(label));

			return label;
		} else
			return name;
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
		MenuItem clarStatsItem = new MenuItem("Clar cover with fixed bond");
		MenuItem kekuleItem = new MenuItem("Kekulé structures");
		MenuItem rboItem = new MenuItem("Ring bond order");

		MenuItem dbItem = new MenuItem("Find in database (DEBUG)");
		MenuItem irSpectraItem = new MenuItem("IR spectra");

		MenuItem checkDatabaseItem = new MenuItem("Check database");

		MenuItem radicalarStatsItem = new MenuItem("Radicalar statistics");

		MenuItem ims2d1aItem = new MenuItem("IMS2D-1A");
		
		MenuItem clarCoverForcedSinglesItem = new MenuItem("Clar cover with forced singles");
		MenuItem clarCoverForcedSinglesStatsItem = new MenuItem("Forced singles Statistics");
		

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

		kekuleItem.setOnAction(e -> {
			kekuleStructures();
		});

		ims2d1aItem.setOnAction(e -> {
			ims2d1a();
		});

		radicalarStatsItem.setOnAction(e -> {
			radicalarStatistics();
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
			BenzenoidCollectionPane currentPane = getSelectedTab();
			currentPane.removeBenzenoidPanes(currentPane.getSelectedBenzenoidPanes());
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

		clarStatsItem.setOnAction(e -> {
			clarCoverStatsFixed();
		});

		clarCoverForcedSinglesItem.setOnAction(e -> {
			TextInputDialog textInputDialog = new TextInputDialog("2");
			textInputDialog.setHeaderText("Enter number of singles:");
			textInputDialog.showAndWait();
            String textInput = textInputDialog.getEditor().getText();
            int nbRadicals = Integer.parseInt(textInput);
			clarCoverForcedRadicals(nbRadicals);
		});
		
		clarCoverForcedSinglesStatsItem.setOnAction(e -> {
			TextInputDialog textInputDialog = new TextInputDialog("2");
			textInputDialog.setHeaderText("Enter number of singles:");
			textInputDialog.showAndWait();
            String textInput = textInputDialog.getEditor().getText();
            int nbRadicals = Integer.parseInt(textInput);
			forcedRadicalsStatistics(nbRadicals);
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
				BenzenoidCollectionPane currentPane = getSelectedTab();
				currentPane.export(directory);
			}

		});

		dbItem.setOnAction(e -> {
			BenzenoidCollectionPane currentPane = getSelectedTab();
			for (BenzenoidPane pane : currentPane.getSelectedBenzenoidPanes()) {
				Molecule molecule = currentPane.getMolecule(pane.getIndex());
				System.out.println(molecule.getIRSpectraResult());
			}
		});

		irSpectraItem.setOnAction(e -> {
			IRSpectra();
		});

		checkDatabaseItem.setOnAction(e -> {
			checkDatabase();
		});

		contextMenu.getItems().addAll(exportMenu, importCollectionItem, exportCollectionItem, new SeparatorMenuItem(),
				renameMenu, deleteItem, copyItem, pasteItem, moveItem, selectAllItem, unselectAllItem,
				checkDatabaseItem, new SeparatorMenuItem(), drawItem, new SeparatorMenuItem(), reLinItem, reLinFanItem,
				clarItem, clarStatsItem, kekuleItem, rboItem, irregularityItem, irSpectraItem, radicalarStatsItem,
				ims2d1aItem, clarCoverForcedSinglesItem, clarCoverForcedSinglesStatsItem);

		this.setOnContextMenuRequested(e -> {

			moveItem.getItems().clear();
			ArrayList<MenuItem> items = new ArrayList<>();
			BenzenoidCollectionPane currentPane = getSelectedTab();

			hoveringPane = currentPane.getHoveringPane();

			if (hoveringPane != null)
				currentPane.setPropertiesArea(hoveringPane.buildDescription());

			for (int i = 0; i < benzenoidSetPanes.size() - 1; i++) {
				BenzenoidCollectionPane collectionPane = benzenoidSetPanes.get(i);

				if (!collectionPane.equals(currentPane)) {

					CollectionMenuItem menuItem = new CollectionMenuItem(collectionPane.getIndex(),
							collectionPane.getName());

					menuItem.setOnAction(e2 -> {

						BenzenoidCollectionPane setPaneOrigin = currentPane;
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

		ArrayList<BenzenoidPane> benzenoidPanesMoved = new ArrayList<>();
		ArrayList<Molecule> moleculesMoved = new ArrayList<>();
		ArrayList<DisplayType> displayTypesMoved = new ArrayList<>();

		Collections.sort(setPaneOrigin.getSelectedBenzenoidPanes());

		for (int i = 0; i < setPaneOrigin.getSelectedBenzenoidPanes().size(); i++) {

			BenzenoidPane benzenoidPane = setPaneOrigin.getSelectedBenzenoidPanes().get(i);
			Molecule molecule = setPaneOrigin.getMolecule(benzenoidPane.getIndex());
			DisplayType displayType = setPaneOrigin.getDisplayType(benzenoidPane.getIndex());

			benzenoidPanesMoved.add(benzenoidPane);
			moleculesMoved.add(molecule);
			displayTypesMoved.add(displayType);
		}

		setPaneOrigin.removeBenzenoidPanes(benzenoidPanesMoved);

		log("Moving " + benzenoidPanesMoved.size() + " benzenoid(s) from " + setPaneOrigin.getName() + " to "
				+ setPaneDestination.getName(), true);

		for (int i = 0; i < moleculesMoved.size(); i++) {
			setPaneDestination.addBenzenoid(moleculesMoved.get(i), displayTypesMoved.get(i));
		}

		// refreshing the two considered panes
		setPaneDestination.refresh();
		setPaneOrigin.refresh();

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

		Collections.sort(copiedBenzenoidPanes);

		for (BenzenoidPane pane : copiedBenzenoidPanes) {

			BenzenoidCollectionPane originPane = pane.getBenzenoidCollectionPane();

			Molecule molecule = originPane.getMolecule(pane.getIndex());
			DisplayType displayType = originPane.getDisplayType(pane.getIndex());

			destinationPane.addBenzenoid(molecule, displayType);
		}

		log("Pasting " + copiedBenzenoidPanes.size() + " benzenoid(s) in " + destinationPane.getName(), true);

		destinationPane.refresh();
	}

	public void resonanceEnergyLin() {
		BenzenoidCollectionPane currentPane = getSelectedTab();

		if (currentPane.getBenzenoidPanes().size() == 0) {
			Utils.alert("There is no benzenoid!");
			return;
		}

		linRunning = true;
		ArrayList<BenzenoidPane> selectedBenzenoidPanes = currentPane.getSelectedBenzenoidPanes();

		String name = "RE Lin";
		BenzenoidCollectionPane benzenoidSetPane = new BenzenoidCollectionPane(this, getBenzenoidSetPanes().size(),
				getNextCollectionPaneLabel(currentPane.getName() + "-" + name));

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
								Molecule molecule = currentPane.getMolecule(benzenoidPane.getIndex());
								molecule.getAromaticity();
								benzenoidSetPane.addBenzenoid(molecule, DisplayType.RE_LIN);
								indexLin++;
								System.out.println(indexLin + " / " + size);

								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										if (indexLin == 1) {
											log(indexLin + " / " + size, false);
											lineIndexLin = currentPane.getConsole().getNbLines() - 1;
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
		BenzenoidCollectionPane currentPane = getSelectedTab();

		if (currentPane.getBenzenoidPanes().size() == 0) {
			Utils.alert("There is no benzenoid!");
			return;
		}

		ArrayList<BenzenoidPane> selectedBenzenoidPanes = currentPane.getSelectedBenzenoidPanes();

		if (selectedBenzenoidPanes.size() == 0)
			selectAll();

		String name = "RE Lin&Fan";
		BenzenoidCollectionPane benzenoidSetPane = new BenzenoidCollectionPane(this, getBenzenoidSetPanes().size(),
				getNextCollectionPaneLabel(currentPane.getName() + "-" + name));

		for (BenzenoidPane benzenoidPane : selectedBenzenoidPanes) {
			Molecule molecule = currentPane.getMolecule(benzenoidPane.getIndex());
			benzenoidSetPane.addBenzenoid(molecule, DisplayType.RE_LIN_FAN);
		}

		benzenoidSetPane.refresh();

		tabPane.getSelectionModel().clearAndSelect(0);
		addBenzenoidSetPane(benzenoidSetPane);
		tabPane.getSelectionModel().clearAndSelect(benzenoidSetPanes.size() - 2);
	}

	public void clarCoverStatsFixed() {

		BenzenoidCollectionPane currentPane = getSelectedTab();

		if (currentPane.getBenzenoidPanes().size() == 0) {
			Utils.alert("There is no benzenoid!");
			return;
		}

		ArrayList<BenzenoidPane> selectedBenzenoidPanes = currentPane.getSelectedBenzenoidPanes();

		String name = "Clar cover - fixed bonds";
		BenzenoidCollectionPane benzenoidSetPane = new BenzenoidCollectionPane(this, getBenzenoidSetPanes().size(),
				getNextCollectionPaneLabel(currentPane.getName() + "-" + name));

		if (selectedBenzenoidPanes.size() == 0) {
			selectAll();
		}

		ArrayList<BenzenoidPane> panes = new ArrayList<>();

		for (BenzenoidPane pane : selectedBenzenoidPanes)
			panes.add(pane);

		indexClar = 0;
		int size = panes.size();

		System.out.println("Computing Clar Cover of " + size + "benzenoids");
		log("Clar Cover (" + size + "benzenoids)", true);

		for (BenzenoidPane benzenoidPane : panes) {
			Molecule molecule = currentPane.getMolecule(benzenoidPane.getIndex());
			ArrayList<ClarCoverSolution> clarCoverSolutions = ClarCoverSolver.solve(molecule);
			if (clarCoverSolutions.size() > 0) {
				// 0 = non défini // 1 = pas de cercle // 2 = cercle
				int[] circles = new int[molecule.getNbHexagons()];
				// (i,j) = 1 => full simple // (i,j) = 2 => full double
				int[][] bonds = new int[molecule.getNbNodes()][molecule.getNbNodes()];
				for (ClarCoverSolution solution : clarCoverSolutions) {

					for (int i = 0; i < molecule.getNbHexagons(); i++) {
						if (solution.isCircle(i)) {
							for (int j = 0; j < 6; j++) {
								int k = (j + 1) % 6;

								int u = molecule.getHexagon(i)[j];
								int v = molecule.getHexagon(i)[k];

								bonds[u][v] = -1;
								bonds[v][u] = -1;
							}

							if (circles[i] == 0) // non défini
								circles[i] = 2;

							if (circles[i] == 1) // pas de rond
								circles[i] = -1;
						}

						else {
							if (circles[i] == 0) // non défini
								circles[i] = 1;

							if (circles[i] == 2) // rond
								circles[i] = -1;
						}
					}

					for (int i = 0; i < molecule.getNbNodes(); i++) {
						for (int j = (i + 1); j < molecule.getNbNodes(); j++) {
							if (molecule.getEdgeMatrix()[i][j] == 1) {
								if (solution.isDoubleBond(i, j)) {
									if (bonds[i][j] == 0) {
										bonds[i][j] = 2;
										bonds[j][i] = 2;
									} else if (bonds[i][j] == 1) {
										bonds[i][j] = -1;
										bonds[j][i] = -1;
									}
								} else {
									if (bonds[i][j] == 0) {
										bonds[i][j] = 1;
										bonds[j][i] = 1;
									} else if (bonds[i][j] == 2) {
										bonds[i][j] = -1;
										bonds[j][i] = -1;
									}
								}
							}
						}
					}
				}
				System.out.print("");
				molecule.setFixedBonds(bonds);
				molecule.setFixedCircles(circles);

				ClarCoverSolution clarCoverSolution = clarCoverSolutions.get(clarCoverSolutions.size() - 1);
				molecule.setClarCoverSolution(clarCoverSolution);
				benzenoidSetPane.addBenzenoid(molecule, DisplayType.CLAR_COVER_FIXED);

			}
		}

		benzenoidSetPane.refresh();
		tabPane.getSelectionModel().clearAndSelect(0);
		addBenzenoidSetPane(benzenoidSetPane);
		tabPane.getSelectionModel().clearAndSelect(benzenoidSetPanes.size() - 2);

	}

	public void kekuleStructures() {
		BenzenoidCollectionPane currentPane = getSelectedTab();

		if (currentPane.getBenzenoidPanes().size() == 0) {
			Utils.alert("There is no benzenoid!");
			return;
		}

		ArrayList<BenzenoidPane> selectedBenzenoidPanes = currentPane.getSelectedBenzenoidPanes();

		BenzenoidCollectionPane benzenoidSetPane = new BenzenoidCollectionPane(this, getBenzenoidSetPanes().size(),
				getNextCollectionPaneLabel("Kekulé structures"));

		if (selectedBenzenoidPanes.size() == 0) {
			Utils.alert("Please select a benzenoid");
			return;
		} else {
			if (selectedBenzenoidPanes.size() > 1) {
				Utils.alert("Please select only one benzenoid");
				return;
			} else {
				Molecule molecule = selectedBenzenoidPanes.get(0).getMolecule();

				if (molecule.getNbKekuleStructures() == 0) {
					Utils.alert("The selected benzenoid has no Kekulé structures.");
					return;
				}

				ArrayList<int[][]> kekuleStructures = KekuleStructureSolver.computeKekuleStructures(molecule, 20);
				molecule.setKekuleStructures(kekuleStructures);

				for (int[][] kekuleStructure : kekuleStructures) {
					benzenoidSetPane.addBenzenoid(molecule, DisplayType.KEKULE);
				}
			}
		}
		benzenoidSetPane.refresh();
		tabPane.getSelectionModel().clearAndSelect(0);
		addBenzenoidSetPane(benzenoidSetPane);
		tabPane.getSelectionModel().clearAndSelect(benzenoidSetPanes.size() - 2);
	}

	public void clarCoverRE() {

		BenzenoidCollectionPane currentPane = getSelectedTab();
		ArrayList<BenzenoidPane> selectedBenzenoidPanes = currentPane.getSelectedBenzenoidPanes();

		String name = "Clar cover";
		BenzenoidCollectionPane benzenoidSetPane = new BenzenoidCollectionPane(this, getBenzenoidSetPanes().size(),
				getNextCollectionPaneLabel(currentPane.getName() + "-" + name));

		ArrayList<BenzenoidPane> panes = new ArrayList<>();

		for (BenzenoidPane pane : selectedBenzenoidPanes)
			panes.add(pane);

		indexClar = 0;
		// int size = panes.size();

		for (BenzenoidPane benzenoidPane : panes) {
			Molecule molecule = currentPane.getMolecule(benzenoidPane.getIndex());

			ArrayList<ClarCoverSolution> clarCoverSolutions = ClarCoverSolver.solve(molecule);
			molecule.setClarCoverSolutions(clarCoverSolutions);
			benzenoidSetPane.addBenzenoid(molecule, DisplayType.CLAR_RE);
		}

		benzenoidSetPane.refresh();
		tabPane.getSelectionModel().clearAndSelect(0);
		addBenzenoidSetPane(benzenoidSetPane);
		tabPane.getSelectionModel().clearAndSelect(benzenoidSetPanes.size() - 2);
		// application.removeTask("Clar cover");
	}

	public void clarCover() {

		BenzenoidCollectionPane currentPane = getSelectedTab();

		if (currentPane.getBenzenoidPanes().size() == 0) {
			Utils.alert("There is no benzenoid!");
			return;
		}

		ArrayList<BenzenoidPane> selectedBenzenoidPanes = currentPane.getSelectedBenzenoidPanes();

		String name = "Clar cover";
		BenzenoidCollectionPane benzenoidSetPane = new BenzenoidCollectionPane(this, getBenzenoidSetPanes().size(),
				getNextCollectionPaneLabel(currentPane.getName() + "-" + name));

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
								Molecule molecule = currentPane.getMolecule(benzenoidPane.getIndex());

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
											lineIndexClar = currentPane.getConsole().getNbLines() - 1;
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

	/**
	 * @param nbRadicals *
	 * 
	 */
	public void clarCoverForcedRadicals(int nbRadicals) {

		BenzenoidCollectionPane currentPane = getSelectedTab();

		if (currentPane.getBenzenoidPanes().size() == 0) {
			Utils.alert("There is no benzenoid!");
			return;
		}
		
		ArrayList<BenzenoidPane> selectedBenzenoidPanes = currentPane.getSelectedBenzenoidPanes();

		String name = "Clar cover";
		BenzenoidCollectionPane benzenoidSetPane = new BenzenoidCollectionPane(this, getBenzenoidSetPanes().size(),
				getNextCollectionPaneLabel(currentPane.getName() + "-" + name));

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
								Molecule molecule = currentPane.getMolecule(benzenoidPane.getIndex());

								ArrayList<ClarCoverSolution> clarCoverSolutions = ClarCoverForcedRadicalsSolver.solve(molecule, nbRadicals);
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
											lineIndexClar = currentPane.getConsole().getNbLines() - 1;
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

		BenzenoidCollectionPane currentPane = getSelectedTab();

		if (currentPane.getBenzenoidPanes().size() == 0) {
			Utils.alert("There is no benzenoid!");
			return;
		}

		ArrayList<BenzenoidPane> selectedBenzenoidPanes = currentPane.getSelectedBenzenoidPanes();

		String name = "RBO";
		BenzenoidCollectionPane benzenoidSetPane = new BenzenoidCollectionPane(this, getBenzenoidSetPanes().size(),
				getNextCollectionPaneLabel(currentPane.getName() + "-" + name));

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
								Molecule molecule = currentPane.getMolecule(benzenoidPane.getIndex());
								molecule.getRBO();
								benzenoidSetPane.addBenzenoid(molecule, DisplayType.RBO);
								indexRBO++;
								System.out.println(indexRBO + " / " + size);

								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										if (indexRBO == 1) {
											log(indexRBO + " / " + size, true);
											lineIndexRBO = currentPane.getConsole().getNbLines() - 1;
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

		BenzenoidCollectionPane currentPane = getSelectedTab();

		if (currentPane.getBenzenoidPanes().size() == 0) {
			Utils.alert("There is no benzenoid!");
			return;
		}

		ArrayList<BenzenoidPane> selectedBenzenoidPanes = currentPane.getSelectedBenzenoidPanes();
		ArrayList<Molecule> molecules = new ArrayList<>();

		if (selectedBenzenoidPanes.size() == 0)
			selectAll();

		for (BenzenoidPane benzenoidPane : selectedBenzenoidPanes) {
			Molecule molecule = currentPane.getMolecule(benzenoidPane.getIndex());
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

		BenzenoidCollectionPane currentPane = getSelectedTab();
		ArrayList<BenzenoidPane> benzenoidPanes = currentPane.getBenzenoidPanes();

		for (BenzenoidPane benzenoidPane : benzenoidPanes) {

			if (!benzenoidPane.isSelected())
				benzenoidPane.select();
		}

	}

	public void unselectAll() {

		BenzenoidCollectionPane currentPane = getSelectedTab();
		ArrayList<BenzenoidPane> benzenoidPanes = currentPane.getBenzenoidPanes();

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
		BenzenoidCollectionPane currentPane = getSelectedTab();

		if (currentPane.getSelectedBenzenoidPanes().size() == 1) {
			Molecule molecule = currentPane.getMolecule(currentPane.getSelectedBenzenoidPanes().get(0).getIndex());
			application.getDrawPane().importBenzenoid(molecule);
			application.switchMode(application.getPanes().getDrawPane());

		}

		else if (hoveringPane != null) {
			Molecule molecule = currentPane.getMolecule(hoveringPane.getIndex());
			application.getDrawPane().importBenzenoid(molecule);
			application.switchMode(application.getPanes().getDrawPane());
		}
	}

	public void exportPng() {

		BenzenoidCollectionPane currentPane = getSelectedTab();

		if (currentPane.getSelectedBenzenoidPanes().size() == 0) {
			if (hoveringPane != null) {
				FileChooser fileChooser = new FileChooser();
				File file = fileChooser.showSaveDialog(application.getStage());

				if (file != null) {
					hoveringPane.exportAsPNG(file);
				}
			}
		}

		else {

			if (currentPane.getSelectedBenzenoidPanes().size() == 1) {
				BenzenoidPane benzenoidPane = currentPane.getSelectedBenzenoidPanes().get(0);

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

					for (int i = 0; i < currentPane.getSelectedBenzenoidPanes().size(); i++) {

						BenzenoidPane benzenoidPane = currentPane.getSelectedBenzenoidPanes().get(i);
						Molecule molecule = currentPane.getMolecule(benzenoidPane.getIndex());

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

		BenzenoidCollectionPane currentPane = getSelectedTab();

		if (currentPane.getSelectedBenzenoidPanes().size() == 0) {
			if (hoveringPane != null) {
				FileChooser fileChooser = new FileChooser();
				File file = fileChooser.showSaveDialog(application.getStage());

				if (file != null) {
					try {
						currentPane.getMolecule(hoveringPane.getIndex()).exportProperties(file);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		else {

			if (currentPane.getSelectedBenzenoidPanes().size() == 1) {
				BenzenoidPane benzenoidPane = currentPane.getSelectedBenzenoidPanes().get(0);

				FileChooser fileChooser = new FileChooser();
				File file = fileChooser.showSaveDialog(application.getStage());

				if (file != null) {
					try {
						currentPane.getMolecule(benzenoidPane.getIndex()).exportProperties(file);
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

					for (int i = 0; i < currentPane.getSelectedBenzenoidPanes().size(); i++) {

						BenzenoidPane benzenoidPane = currentPane.getSelectedBenzenoidPanes().get(i);
						Molecule molecule = currentPane.getMolecule(benzenoidPane.getIndex());

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

	public void sort(MoleculeComparator comparator, boolean ascending) {

		if (comparator instanceof ResonanceEnergyComparator) {
			selectAll();
			resonanceEnergyLin();

//			BenzenoidCollectionPane curentPane = getSelectedTab();
//			for (Molecule molecule : curentPane.getMolecules())
//				molecule.getAromaticity();
		}

		BenzenoidCollectionPane currentPane = getSelectedTab();
		currentPane.setComparator(comparator);
		currentPane.sort(ascending);
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
				if (file.isFile() && file.getName().contains(".graph")) {
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

	public void exportCOM() {

		BenzenoidCollectionPane currentPane = getSelectedTab();

		if (currentPane.getSelectedBenzenoidPanes().size() == 0) {

			if (hoveringPane != null) {

				FileChooser fileChooser = new FileChooser();
				File file = fileChooser.showSaveDialog(application.getStage());

				if (file != null) {
					Molecule molecule = currentPane.getMolecule(hoveringPane.getIndex());
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

				int index = 0;

				for (int i = 0; i < currentPane.getSelectedBenzenoidPanes().size(); i++) {

					Molecule molecule = currentPane
							.getMolecule(currentPane.getSelectedBenzenoidPanes().get(i).getIndex());

					String fileName;

					if (!currentPane.getSelectedBenzenoidPanes().get(i).getName().equals(""))
						fileName = currentPane.getSelectedBenzenoidPanes().get(i).getName().split("\n")[0] + ".com";
					else {
						fileName = "unknown_molecule_" + index + ".com";
						index++;
					}

					fileName = fileName.replace(".graph", "");

					File file = new File(directoryPath + "/" + fileName);
					try {
						ComConverter.generateComFile(molecule, file, 0, ComType.ER, file.getName());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void exportCML() {

		BenzenoidCollectionPane currentPane = getSelectedTab();

		if (currentPane.getSelectedBenzenoidPanes().size() == 0) {

			if (hoveringPane != null) {

				FileChooser fileChooser = new FileChooser();
				File file = fileChooser.showSaveDialog(application.getStage());

				if (file != null) {
					Molecule molecule = currentPane.getMolecule(hoveringPane.getIndex());
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

				int index = 0;

				for (int i = 0; i < currentPane.getSelectedBenzenoidPanes().size(); i++) {

					Molecule molecule = currentPane
							.getMolecule(currentPane.getSelectedBenzenoidPanes().get(i).getIndex());

					String filename;

					if (!currentPane.getSelectedBenzenoidPanes().get(i).getName().equals(""))
						filename = currentPane.getSelectedBenzenoidPanes().get(i).getName().split("\n")[0] + ".cml";
					else {
						filename = "unknown_molecule_" + index + ".cml";
						index++;
					}

					filename = filename.replace(".graph", "");

					File file = new File(directoryPath + "/" + filename);
					try {
						CMLConverter.generateCmlFile(molecule, file);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

	public void exportGraph() {

		BenzenoidCollectionPane currentPane = getSelectedTab();

		if (currentPane.getSelectedBenzenoidPanes().size() == 0) {

			if (hoveringPane != null) {

				FileChooser fileChooser = new FileChooser();
				File file = fileChooser.showSaveDialog(application.getStage());

				if (file != null) {
					try {
						currentPane.getMolecule(hoveringPane.getIndex()).exportToGraphFile(file);
					} catch (IOException e) {
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

				int index = 0;

				for (int i = 0; i < currentPane.getSelectedBenzenoidPanes().size(); i++) {

					Molecule molecule = currentPane
							.getMolecule(currentPane.getSelectedBenzenoidPanes().get(i).getIndex());

					String name = currentPane.getSelectedBenzenoidPanes().get(i).getName();
					String filename;

					if (!name.equals(""))
						filename = name.split("\n")[0] + ".graph";
					else {
						filename = "unknown_molecule_" + index + ".graph";
						index++;
					}

//					File file = new File(directoryPath + "/molecule_" + i + ".graph");
					File file = new File(directoryPath + filename);

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
		// BenzenoidCollectionPane currentPane = getSelectedTab();
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

	public void checkDatabase() {

		BenzenoidCollectionPane currentPane = getSelectedTab();

		log("Requesting database (" + currentPane.getName() + ", " + currentPane.getSelectedBenzenoidPanes().size()
				+ " benzenoids)", true);

		Service<Void> calculateService = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {

					@Override
					protected Void call() throws Exception {

						indexDatabase = 1;
						int size = currentPane.getSelectedBenzenoidPanes().size();

						for (BenzenoidPane pane : currentPane.getSelectedBenzenoidPanes()) {

							Molecule molecule = currentPane.getMolecule(pane.getIndex());

							if (!molecule.databaseCheckedIR()) {
								if (molecule.getIRSpectraResult() != null) {
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
												lineIndexDatabase = currentPane.getConsole().getNbLines() - 1;
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

	public void ims2d1a() {

		BenzenoidCollectionPane currentPane = getSelectedTab();

		String name = "Ims2D_1A";
		BenzenoidCollectionPane benzenoidSetPane = new BenzenoidCollectionPane(this, getBenzenoidSetPanes().size(),
				getNextCollectionPaneLabel(currentPane.getName() + "-" + name));

		if (currentPane.getSelectedBenzenoidPanes().size() == 0) {
			Utils.alert("Please, select at least one benzenoid having less than 10 hexagons");
			return;
		}

		if (currentPane.getSelectedBenzenoidPanes().size() == 0)
			selectAll();

		ArrayList<BenzenoidPane> panes = new ArrayList<>();
		for (BenzenoidPane pane : currentPane.getSelectedBenzenoidPanes())
			panes.add(pane);

		int nbNotAvailable = 0; // the number of benzenoids for which the map is not available
		for (BenzenoidPane pane : panes) {
			Molecule molecule = currentPane.getMolecule(pane.getIndex());
			if (molecule.getIms2d1a() != null)
				benzenoidSetPane.addBenzenoid(molecule, DisplayType.IMS2D1A);
			else
				nbNotAvailable++;
		}

		if (nbNotAvailable == currentPane.getSelectedBenzenoidPanes().size()) {
			Utils.alert("No map is available yet for the selection");
			return;
		} else if (nbNotAvailable == 1)
			Utils.alert("No map is available yet for one benzenoid of the selection");
		else
			Utils.alert("No map is available yet for " + nbNotAvailable + " benzenoids of the selection");

		benzenoidSetPane.refresh();
		tabPane.getSelectionModel().clearAndSelect(0);
		addBenzenoidSetPane(benzenoidSetPane);
		tabPane.getSelectionModel().clearAndSelect(benzenoidSetPanes.size() - 2);
	}

	public void IRSpectra() {

		BenzenoidCollectionPane currentPane = getSelectedTab();

		if (currentPane.getBenzenoidPanes().size() == 0) {
			Utils.alert("There is no benzenoid!");
			return;
		}

		log("Requesting database (" + currentPane.getName() + ", " + currentPane.getSelectedBenzenoidPanes().size()
				+ " benzenoids)", true);

		if (currentPane.getSelectedBenzenoidPanes().size() == 0) {
			Utils.alert("Please, select at least one benzenoid having less than 10 hexagons");
			return;
		}

		ArrayList<BenzenoidPane> panes = new ArrayList<>();
		for (BenzenoidPane pane : currentPane.getSelectedBenzenoidPanes())
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

							Molecule molecule = currentPane.getMolecule(pane.getIndex());

							if (!molecule.databaseCheckedIR()) {
								if (molecule.getIRSpectraResult() != null) {
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
												lineIndexDatabase = currentPane.getConsole().getNbLines() - 1;
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
					displayIRSpectra(panes, currentPane);
					break;
				}
			}
		});

		calculateService.start();

	}

	private void displayIRSpectra(ArrayList<BenzenoidPane> panes, BenzenoidCollectionPane currentPane) {

		ArrayList<Molecule> moleculesInDB = new ArrayList<>();

		for (BenzenoidPane pane : panes) {

			Molecule molecule = currentPane.getMolecule(pane.getIndex());
			if (molecule.getIRSpectraResult() != null)
				moleculesInDB.add(molecule);
		}

		log("IR Spectra (" + currentPane.getName() + ", " + moleculesInDB.size() + " benzenoids)", true);

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

				ResultLogFile result = molecule.getIRSpectraResult();
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
		BenzenoidCollectionPane currentPane = getSelectedTab();

		hoveringPane = currentPane.getHoveringPane();

		if (hoveringPane != null)
			currentPane.setPropertiesArea(hoveringPane.buildDescription());

		for (int i = 0; i < benzenoidSetPanes.size() - 1; i++) {
			System.out.println(benzenoidSetPanes.get(i).getName());
		}

		if (benzenoidSetPanes.size() <= 2) {
			CollectionMenuItem menuItem = new CollectionMenuItem(0, "(none)");
			items.add(menuItem);
		} else {
			for (int i = 0; i < benzenoidSetPanes.size() - 1; i++) {

				BenzenoidCollectionPane collectionPane = benzenoidSetPanes.get(i);

				if (!collectionPane.equals(currentPane)) {

					CollectionMenuItem menuItem = new CollectionMenuItem(collectionPane.getIndex(),
							collectionPane.getName());

					menuItem.setOnAction(e2 -> {

						BenzenoidCollectionPane setPaneOrigin = currentPane;
						BenzenoidCollectionPane setPaneDestination = collectionPane;

						move(setPaneOrigin, setPaneDestination);
					});

					items.add(menuItem);
				}
			}
		}

		moveItemMenu.getItems().addAll(items);
	}

	public void radicalarStatistics() {

		BenzenoidCollectionPane currentPane = getSelectedTab();

		if (currentPane.getBenzenoidPanes().size() == 0) {
			Utils.alert("There is no benzenoid!");
			return;
		}
		ArrayList<BenzenoidPane> selectedBenzenoidPanes = currentPane.getSelectedBenzenoidPanes();

		String name = "Radicalar statistics";
		BenzenoidCollectionPane benzenoidSetPane = new BenzenoidCollectionPane(this, getBenzenoidSetPanes().size(),
				getNextCollectionPaneLabel(currentPane.getName() + "-" + name));

		application.addTask("Radicalar statistics");

		clarRunning = true;

		if (selectedBenzenoidPanes.size() == 0) {
			selectAll();
		}

		calculateServiceRadicalar = new Service<Void>() {

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

						System.out.println("Computing radicalar statistics of " + size + "benzenoids");
						log("Radicalar statistics (" + size + "benzenoids)", true);

						for (BenzenoidPane benzenoidPane : panes) {
							if (clarRunning) {
								Molecule molecule = currentPane.getMolecule(benzenoidPane.getIndex());

								ArrayList<ClarCoverSolution> clarCoverSolutions = ClarCoverSolver.solve(molecule);
								if (clarCoverSolutions.size() > 0) {
//									ClarCoverSolution clarCoverSolution = clarCoverSolutions
//											.get(clarCoverSolutions.size() - 1);
//									molecule.setClarCoverSolution(clarCoverSolution);
//									benzenoidSetPane.addBenzenoid(molecule, DisplayType.CLAR_COVER);
									molecule.setClarCoverSolutions(clarCoverSolutions);
									benzenoidSetPane.addBenzenoid(molecule, DisplayType.RADICALAR);
								}
								indexClar++;
								System.out.println(indexClar + " / " + size);

								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										if (indexClar == 1) {
											log(indexClar + " / " + size, false);
											lineIndexClar = currentPane.getConsole().getNbLines() - 1;
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

		calculateServiceRadicalar.stateProperty().addListener(new ChangeListener<State>() {

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

		calculateServiceRadicalar.start();
	}
	
	/***
	 * idem radicalStatistics mais sur ClarCoverForcedRadicalsSolver au lieu de ClarCoverSolver
	 */
	public void forcedRadicalsStatistics(int nbRadicals) {

		BenzenoidCollectionPane currentPane = getSelectedTab();

		if (currentPane.getBenzenoidPanes().size() == 0) {
			Utils.alert("There is no benzenoid!");
			return;
		}
				
		ArrayList<BenzenoidPane> selectedBenzenoidPanes = currentPane.getSelectedBenzenoidPanes();

		String name = "Radicalar statistics";
		BenzenoidCollectionPane benzenoidSetPane = new BenzenoidCollectionPane(this, getBenzenoidSetPanes().size(),
				getNextCollectionPaneLabel(currentPane.getName() + "-" + name));

		application.addTask("Radicalar statistics");

		clarRunning = true;

		if (selectedBenzenoidPanes.size() == 0) {
			selectAll();
		}

		calculateServiceRadicalar = new Service<Void>() {

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

						System.out.println("Computing radicalar statistics of " + size + "benzenoids");
						log("Radicalar statistics (" + size + "benzenoids)", true);

						for (BenzenoidPane benzenoidPane : panes) {
							if (clarRunning) {
								Molecule molecule = currentPane.getMolecule(benzenoidPane.getIndex());

								ArrayList<ClarCoverSolution> clarCoverSolutions = ClarCoverForcedRadicalsSolver.solve(molecule, nbRadicals);
								if (clarCoverSolutions.size() > 0) {
//									ClarCoverSolution clarCoverSolution = clarCoverSolutions
//											.get(clarCoverSolutions.size() - 1);
//									molecule.setClarCoverSolution(clarCoverSolution);
//									benzenoidSetPane.addBenzenoid(molecule, DisplayType.CLAR_COVER);
									molecule.setClarCoverSolutions(clarCoverSolutions);
									benzenoidSetPane.addBenzenoid(molecule, DisplayType.RADICALAR);
								}
								indexClar++;
								System.out.println(indexClar + " / " + size);

								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										if (indexClar == 1) {
											log(indexClar + " / " + size, false);
											lineIndexClar = currentPane.getConsole().getNbLines() - 1;
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

		calculateServiceRadicalar.stateProperty().addListener(new ChangeListener<State>() {

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

		calculateServiceRadicalar.start();
	}

}
