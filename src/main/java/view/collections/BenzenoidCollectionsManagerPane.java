package view.collections;

import application.BenzenoidApplication;
import classifier.Irregularity;
import collection_operations.CollectionOperation;
import collection_operations.CollectionOperationSet;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import benzenoid.Benzenoid;
import benzenoid.BenzenoidParser;
import benzenoid.sort.MoleculeComparator;
import benzenoid.sort.ResonanceEnergyComparator;
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
import view.ir_spectra.ComputedPlotPane;
import view.ir_spectra.IRSpectraPane;
import view.irregularity.IrregularityPane;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BenzenoidCollectionsManagerPane extends BorderPane {

	private final BenzenoidApplication application;

	private TabPane tabPane;

	private ArrayList<BenzenoidCollectionPane> benzenoidSetPanes;

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


	private Service<Void> calculateServiceRBO;
	private boolean rboRunning;
	private int indexRBO;
	private int lineIndexRBO;


	public BenzenoidCollectionsManagerPane(BenzenoidApplication parent) {

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

		BenzenoidCollectionPane originBenzenoidCollectionPane = getSelectedTab();
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

			}
		});

		tabPane.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> getSelectedTab().refreshCollectionProperties());
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
		} else if ("+".equals(benzenoidSetPane.getName())) {
			benzenoidSetPanes.add(benzenoidSetPane);
			tabPane.getTabs().add(benzenoidSetPane);
		} else {
			benzenoidSetPanes.add(0, benzenoidSetPane);
			tabPane.getTabs().add(0, benzenoidSetPane);
		}
	}

	public Menu initializeMoveMenuItem() {

		moveItemMenu = new Menu("Move");

		moveItemMenu.setOnAction(e -> refreshMoveItem());
		moveItemMenu.setOnMenuValidation(e -> refreshMoveItem());

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

		while ((iter.hasNext()) && (!iter.next().getName().equals(name)));

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

		moveItem = new Menu("Move");

		Menu exportMenu = new Menu("Export");
		Menu exportBenzenoidItem = new Menu("Export benzenoid");
		MenuItem exportPropertiesItem = CollectionOperationSet.getMenuItemByName("Export properties");

		MenuItem exportGraph = CollectionOperationSet.getMenuItemByName(".graph");
		MenuItem exportPng = CollectionOperationSet.getMenuItemByName(".png");
		MenuItem exportCml = CollectionOperationSet.getMenuItemByName(".cml");
		MenuItem exportCom = CollectionOperationSet.getMenuItemByName(".com");
		exportBenzenoidItem.getItems().addAll(exportGraph, exportPng, exportCml, exportCom);

		MenuItem importCollectionItem = CollectionOperationSet.getMenuItemByName("Import collection");
		MenuItem exportCollectionItem = CollectionOperationSet.getMenuItemByName("Export collection");
		exportMenu.getItems().addAll(exportBenzenoidItem, exportPropertiesItem);


//		//MenuItem dbItem = new MenuItem("Find in database (DEBUG)");
//		dbItem.setOnAction(e -> {
//			BenzenoidCollectionPane currentPane = getSelectedTab();
//			for (BenzenoidPane pane : currentPane.getSelectedBenzenoidPanes()) {
//				Molecule molecule = currentPane.getMolecule(pane.getIndex());
//				System.out.println(molecule.getIRSpectraResult());
//			}
//		});

		// Organisation des items
		contextMenu.getItems().addAll(
				exportMenu, importCollectionItem, exportCollectionItem, new SeparatorMenuItem());
		for(CollectionOperation operation : CollectionOperationSet.getCollectionSimpleOperationSet())
			contextMenu.getItems().add(operation.getMenuItem());
		contextMenu.getItems().addAll(new SeparatorMenuItem(), moveItem);
		for(CollectionOperation operation : CollectionOperationSet.getCollectionComputationSet())
			contextMenu.getItems().add(operation.getMenuItem());

		// Liens items <-> operations
		for(CollectionOperation operation : CollectionOperationSet.getCollectionOperationSet())
			operation.getMenuItem().setOnAction(e -> operation.execute(this));

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

					menuItem.setOnAction(e2 -> move(currentPane, collectionPane));

					items.add(menuItem);
				}
			}

			moveItem.getItems().addAll(items);
			contextMenu.show(this, e.getScreenX(), e.getScreenY());
		});
	}

	public void move(BenzenoidCollectionPane setPaneOrigin, BenzenoidCollectionPane setPaneDestination) {

		ArrayList<BenzenoidPane> benzenoidPanesMoved = new ArrayList<>();
		ArrayList<Benzenoid> moleculesMoved = new ArrayList<>();
		ArrayList<DisplayType> displayTypesMoved = new ArrayList<>();

		Collections.sort(setPaneOrigin.getSelectedBenzenoidPanes());

		for (int i = 0; i < setPaneOrigin.getSelectedBenzenoidPanes().size(); i++) {

			BenzenoidPane benzenoidPane = setPaneOrigin.getSelectedBenzenoidPanes().get(i);
			Benzenoid molecule = setPaneOrigin.getMolecule(benzenoidPane.getIndex());
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

			Benzenoid molecule = originPane.getMolecule(pane.getIndex());
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

		calculateServiceLin = new Service<>() {

			@Override
			protected Task<Void> createTask() {
				return new Task<>() {

					@Override
					protected Void call() {

						ArrayList<BenzenoidPane> panes = new ArrayList<>(selectedBenzenoidPanes);

						indexLin = 0;
						int size = panes.size();

						System.out.println("Computing resonance energy of " + size + " benzenoids.");
						log("RE Lin (" + size + " benzenoids)", true);

						for (BenzenoidPane benzenoidPane : panes) {
							if (linRunning) {
								Benzenoid molecule = currentPane.getMolecule(benzenoidPane.getIndex());
								molecule.getAromaticity();
								benzenoidSetPane.addBenzenoid(molecule, DisplayType.RE_LIN);
								indexLin++;
								System.out.println(indexLin + " / " + size);

								Platform.runLater(() -> {
									if (indexLin == 1) {
										log(indexLin + " / " + size, false);
										lineIndexLin = currentPane.getConsole().getNbLines() - 1;
									} else
										changeLineConsole(indexLin + " / " + size, lineIndexLin);
								});
							}
						}

						return null;
					}

				};
			}
		};

		calculateServiceLin.stateProperty().addListener((observable, oldValue, newValue) -> {

			switch (newValue) {
			case FAILED:
				Utils.alert("No selected benzenoid");
				linRunning = false;
				break;

			case CANCELLED:
				case SUCCEEDED:
					// Utils.alert("No selected benzenoid");
				benzenoidSetPane.refresh();
				tabPane.getSelectionModel().clearAndSelect(0);
				addBenzenoidSetPane(benzenoidSetPane);
				tabPane.getSelectionModel().clearAndSelect(benzenoidSetPanes.size() - 2);
				application.removeTask("RE Lin");
				linRunning = false;
				break;
			}
		});

		calculateServiceLin.start();
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

		calculateServiceClarCover = new Service<>() {

			@Override
			protected Task<Void> createTask() {
				return new Task<>() {

					@Override
					protected Void call() {

						ArrayList<BenzenoidPane> panes = new ArrayList<>(selectedBenzenoidPanes);

						indexClar = 0;
						int size = panes.size();

						System.out.println("Computing Clar Cover of " + size + "benzenoids");
						log("Clar Cover (" + size + "benzenoids)", true);

						for (BenzenoidPane benzenoidPane : panes) {
							if (clarRunning) {
								Benzenoid molecule = currentPane.getMolecule(benzenoidPane.getIndex());

								ArrayList<ClarCoverSolution> clarCoverSolutions = ClarCoverSolver.solve(molecule);
								if (clarCoverSolutions.size() > 0) {
									ClarCoverSolution clarCoverSolution = clarCoverSolutions
											.get(clarCoverSolutions.size() - 1);
									molecule.setClarCoverSolution(clarCoverSolution);
									benzenoidSetPane.addBenzenoid(molecule, DisplayType.CLAR_COVER);
								}
								indexClar++;
								System.out.println(indexClar + " / " + size);

								Platform.runLater(() -> {
									if (indexClar == 1) {
										log(indexClar + " / " + size, false);
										lineIndexClar = currentPane.getConsole().getNbLines() - 1;
									} else
										changeLineConsole(indexClar + " / " + size, lineIndexClar);
								});

							}
						}

						return null;
					}

				};
			}
		};

		calculateServiceClarCover.stateProperty().addListener((observable, oldValue, newValue) -> {

			switch (newValue) {
			case FAILED:
				clarRunning = false;
				Utils.alert("Failed");
				break;

			case CANCELLED:

				case SUCCEEDED:
					clarRunning = false;
				benzenoidSetPane.refresh();
				tabPane.getSelectionModel().clearAndSelect(0);
				addBenzenoidSetPane(benzenoidSetPane);
				tabPane.getSelectionModel().clearAndSelect(benzenoidSetPanes.size() - 2);
				application.removeTask("Clar cover");
				break;
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

		application.addTask("Ring Bond Order");

		rboRunning = true;

		if (selectedBenzenoidPanes.size() == 0) {
			selectAll();
		}

		calculateServiceRBO = new Service<>() {

			@Override
			protected Task<Void> createTask() {
				return new Task<>() {

					@Override
					protected Void call() {

						ArrayList<BenzenoidPane> panes = new ArrayList<>(selectedBenzenoidPanes);

						indexRBO = 0;
						int size = panes.size();

						System.out.println("Computing Clar Cover of " + size + "benzenoids");
						log("Clar cover (" + size + " benzenoids)", true);

						for (BenzenoidPane benzenoidPane : panes) {
							if (rboRunning) {
								Benzenoid molecule = currentPane.getMolecule(benzenoidPane.getIndex());
								molecule.getRBO();
								benzenoidSetPane.addBenzenoid(molecule, DisplayType.RBO);
								indexRBO++;
								System.out.println(indexRBO + " / " + size);

								Platform.runLater(() -> {
									if (indexRBO == 1) {
										log(indexRBO + " / " + size, true);
										lineIndexRBO = currentPane.getConsole().getNbLines() - 1;
									} else
										changeLineConsole(indexRBO + " / " + size, lineIndexRBO);
								});

							}
						}

						return null;
					}

				};
			}
		};

		calculateServiceRBO.stateProperty().addListener((observable, oldValue, newValue) -> {

			switch (newValue) {
			case FAILED:
				rboRunning = false;
				Utils.alert("Failed");
				break;

			case CANCELLED:

				case SUCCEEDED:
					rboRunning = false;
				benzenoidSetPane.refresh();
				tabPane.getSelectionModel().clearAndSelect(0);
				addBenzenoidSetPane(benzenoidSetPane);
				tabPane.getSelectionModel().clearAndSelect(benzenoidSetPanes.size() - 2);
				application.removeTask("Ring Bond Order");
				break;
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
		ArrayList<Benzenoid> molecules = new ArrayList<>();

		if (selectedBenzenoidPanes.size() == 0)
			selectAll();

		for (BenzenoidPane benzenoidPane : selectedBenzenoidPanes) {
			Benzenoid molecule = currentPane.getMolecule(benzenoidPane.getIndex());
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
			Benzenoid molecule = currentPane.getMolecule(currentPane.getSelectedBenzenoidPanes().get(0).getIndex());
			application.getDrawPane().importBenzenoid(molecule);
			application.switchMode(application.getPanes().getDrawPane());

		}

		else if (hoveringPane != null) {
			Benzenoid molecule = currentPane.getMolecule(hoveringPane.getIndex());
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
						Benzenoid molecule = currentPane.getMolecule(benzenoidPane.getIndex());

						File moleculeFile;
						if (molecule.getDescription() != null && !"".equals(molecule.getDescription()))
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

	public void refreshColorScales() {
		for (BenzenoidCollectionPane collectionPane : benzenoidSetPanes)
			collectionPane.refreshColorScales();
	}

	public void sort(MoleculeComparator comparator, boolean ascending) {

		if (comparator instanceof ResonanceEnergyComparator) {
			selectAll();
			resonanceEnergyLin();
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

			assert listOfFiles != null;
			for (File file : listOfFiles) {
				if (file.isFile() && file.getName().contains(".graph")) {
					Benzenoid molecule = GraphParser.parseUndirectedGraph(file);
					molecule.setDescription(file.getName());
					collectionPane.addBenzenoid(molecule, DisplayType.BASIC);
				}
			}

			collectionPane.refresh();

		} catch (Exception e) {
			e.printStackTrace();
			ok = false;
		}

		if (ok) {
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
					Benzenoid molecule = currentPane.getMolecule(hoveringPane.getIndex());
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

					Benzenoid molecule = currentPane
							.getMolecule(currentPane.getSelectedBenzenoidPanes().get(i).getIndex());
/*
					String fileName;

					if (!"".equals(currentPane.getSelectedBenzenoidPanes().get(i).getName()))
						fileName = currentPane.getSelectedBenzenoidPanes().get(i).getName().split("\n")[0] + ".com";
					else {
						fileName = "unknown_molecule_" + index + ".com";
						index++;
					}

					fileName = fileName.replace(".graph", "");
*/

					String fileName = molecule.getNames().get(0) + ".com";

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
					Benzenoid molecule = currentPane.getMolecule(hoveringPane.getIndex());
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

					Benzenoid molecule = currentPane
							.getMolecule(currentPane.getSelectedBenzenoidPanes().get(i).getIndex());

					String filename;

					if (!"".equals(currentPane.getSelectedBenzenoidPanes().get(i).getName()))
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
						Benzenoid benzenoid = currentPane.getMolecule(hoveringPane.getIndex());
						BenzenoidParser.exportToGraphFile(benzenoid, file);
						//currentPane.getMolecule(hoveringPane.getIndex()).exportToGraphFile(file);
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

					Benzenoid benzenoid = currentPane
							.getMolecule(currentPane.getSelectedBenzenoidPanes().get(i).getIndex());

					String name = currentPane.getSelectedBenzenoidPanes().get(i).getName();
					String filename;

					if (!"".equals(name))
						filename = name.split("\n")[0] + ".graph";
					else {
						filename = "unknown_molecule_" + index + ".graph";
						index++;
					}

					File file = new File(directoryPath + filename);

					try {
						BenzenoidParser.exportToGraphFile(benzenoid, file);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

	public void refreshRIType(RIType type) {
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

	public void displayIRSpectra(ArrayList<BenzenoidPane> panes, BenzenoidCollectionPane currentPane) {

		ArrayList<Benzenoid> moleculesInDB = new ArrayList<>();

		for (BenzenoidPane pane : panes) {

			Benzenoid benzenoid = currentPane.getMolecule(pane.getIndex());
			Optional<ResultLogFile> IRSpectra = benzenoid.getDatabaseInformation().findIRSpectra();

			if (IRSpectra.isPresent())
				moleculesInDB.add(benzenoid);
		}

		log("IR Spectra (" + currentPane.getName() + ", " + moleculesInDB.size() + " benzenoids)", true);

		NewClassifier classifier = new NewCarbonsHydrogensClassifier(moleculesInDB);
		HashMap<String, ArrayList<Benzenoid>> classes = classifier.classify();

		ArrayList<IRSpectra> spectraDatas = new ArrayList<>();

		for (Map.Entry<String, ArrayList<Benzenoid>> entry : classes.entrySet()) {

			String key = entry.getKey();
			ArrayList<Benzenoid> moleculesClasses = entry.getValue();

			System.out.println("Treating " + key);

			HashMap<String, Double> finalEnergies = new HashMap<>();
			HashMap<String, Double> irregularities = new HashMap<>();
			ArrayList<String> amesFormats = new ArrayList<>();

			for (Benzenoid molecule : moleculesClasses) {

				Optional<ResultLogFile> IRSpectra = molecule.getDatabaseInformation().findIRSpectra();
				if (IRSpectra.isPresent()) {
					finalEnergies.put(molecule.getNames().get(0),
							IRSpectra.get().getFinalEnergy().get(IRSpectra.get().getFinalEnergy().size() - 1));
          
          Irregularity irregularity = molecule.getIrregularity();

					irregularities.put(molecule.getNames().get(0), irregularity.getXI());

					amesFormats.add(IRSpectra.get().getAmesFormat());
				}
			}

			try {
				IRSpectra spectraData = SpectrumsComputer.buildSpectraData(key, moleculesClasses, parameter);
				spectraData.setFinalEnergies(finalEnergies);
				spectraData.setIrregularities(irregularities);
				spectraData.setAmesFormats(amesFormats);
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

					menuItem.setOnAction(e2 -> move(currentPane, collectionPane));

					items.add(menuItem);
				}
			}
		}

		moveItemMenu.getItems().addAll(items);
	}

	public BenzenoidPane getHoveringPane() {
		return hoveringPane;
	}

	public ArrayList<BenzenoidPane> getCopiedBenzenoidPanes() {
		return copiedBenzenoidPanes;
	}
}
