package view.collections;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import molecules.Benzenoid;
import molecules.BenzenoidParser;
import molecules.sort.MoleculeComparator;
import solveur.Aromaticity;
import solveur.Aromaticity.RIType;
import solveur.LinFanAlgorithm;
import utils.Utils;
import view.groups.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class BenzenoidCollectionPane extends Tab {

    public enum DisplayedProperty {
        PROPERTIES, FREQUENCIES
    }

    public enum DisplayType {
        BASIC, RE_LIN, RE_LIN_FAN, CLAR_COVER, RBO, RADICALAR, IMS2D1A, CLAR_COVER_FIXED, KEKULE, CLAR_RE
    }

    private final BenzenoidCollectionsManagerPane parent;

    private int index;

    private GridPane gridPane;

    private DisplayedProperty displayedProperty = DisplayedProperty.PROPERTIES;
    private TextArea benzenoidPropertiesArea;
    private Console console;

    private TextArea IRSpectraArea;

    private Button previousButton;
    private Button nextButton;

    private Label propertiesLabel;
    private Label IRSpectraLabel;

    private TextArea selectedArea;
    private BorderPane borderPane;

    private ArrayList<DisplayType> displayTypes;

    private ArrayList<BenzenoidPane> benzenoidPanes;
    private ArrayList<BenzenoidPane> selectedBenzenoidPanes;

    private ArrayList<Benzenoid> molecules;
    private ArrayList<Benzenoid> selectedMolecules;

    private FlowPane flowPane;

    private BenzenoidPane hoveringPane;

    private boolean lock;

    public BenzenoidCollectionPane(BenzenoidCollectionsManagerPane parent, int index, String name) {

        super(name);
        this.index = index;
        this.parent = parent;
        initialize();
    }

    private void initialize() {

        this.setOnCloseRequest(e -> parent.remove(this));

        lock = false;

        propertiesLabel = new Label("Benzenoid's properties");
        IRSpectraLabel = new Label("IR Spectra");

        propertiesLabel.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, FontPosture.ITALIC, 15));
        propertiesLabel.setMaxWidth(Double.MAX_VALUE);
        propertiesLabel.setAlignment(Pos.CENTER);

        IRSpectraLabel.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, FontPosture.ITALIC, 15));
        IRSpectraLabel.setMaxWidth(Double.MAX_VALUE);
        IRSpectraLabel.setAlignment(Pos.CENTER);

        console = new Console();
        benzenoidPropertiesArea = new TextArea();
        IRSpectraArea = new TextArea();

        selectedArea = benzenoidPropertiesArea;

        previousButton = new Button("<");
        nextButton = new Button(">");

        nextButton.setOnAction(e -> {

            switch (displayedProperty) {
                case PROPERTIES:
                    displayedProperty = DisplayedProperty.FREQUENCIES;
                    gridPane.getChildren().remove(borderPane);
                    gridPane.getChildren().remove(selectedArea);
                    selectedArea = IRSpectraArea;

                    borderPane = new BorderPane();
                    borderPane.setLeft(previousButton);
                    borderPane.setCenter(IRSpectraLabel);
                    borderPane.setRight(nextButton);

                    gridPane.add(borderPane, 1, 0);
                    gridPane.add(selectedArea, 1, 1);

                    break;

                case FREQUENCIES:
                    displayedProperty = DisplayedProperty.PROPERTIES;
                    gridPane.getChildren().remove(borderPane);
                    gridPane.getChildren().remove(selectedArea);
                    selectedArea = benzenoidPropertiesArea;

                    borderPane = new BorderPane();
                    borderPane.setLeft(previousButton);
                    borderPane.setCenter(propertiesLabel);
                    borderPane.setRight(nextButton);

                    gridPane.add(borderPane, 1, 0);
                    gridPane.add(selectedArea, 1, 1);
                    break;

                default:
                    // DO_NOTHING
                    break;
            }

        });

        previousButton.setOnAction(e -> {
            switch (displayedProperty) {

                case PROPERTIES:
                    displayedProperty = DisplayedProperty.FREQUENCIES;
                    gridPane.getChildren().remove(borderPane);
                    gridPane.getChildren().remove(selectedArea);
                    selectedArea = IRSpectraArea;

                    borderPane = new BorderPane();
                    borderPane.setLeft(previousButton);
                    borderPane.setCenter(IRSpectraLabel);
                    borderPane.setRight(nextButton);

                    gridPane.add(borderPane, 1, 0);
                    gridPane.add(selectedArea, 1, 1);
                    break;

                case FREQUENCIES:
                    displayedProperty = DisplayedProperty.PROPERTIES;
                    gridPane.getChildren().remove(borderPane);
                    gridPane.getChildren().remove(selectedArea);
                    selectedArea = benzenoidPropertiesArea;

                    borderPane = new BorderPane();
                    borderPane.setLeft(previousButton);
                    borderPane.setCenter(propertiesLabel);
                    borderPane.setRight(nextButton);

                    gridPane.add(borderPane, 1, 0);
                    gridPane.add(selectedArea, 1, 1);
                    break;

                default:
                    // DO_NOTHING
                    break;

            }
        });

        displayTypes = new ArrayList<>();

        benzenoidPanes = new ArrayList<>();
        selectedBenzenoidPanes = new ArrayList<>();

        molecules = new ArrayList<>();
        selectedMolecules = new ArrayList<>();

        refresh();
    }

    public void addBenzenoid(Benzenoid molecule, DisplayType displayType) {
        molecules.add(molecule);
        displayTypes.add(displayType);
        selectedBenzenoidPanes.clear();
    }

    public void addSelectedBenzenoidPane(BenzenoidPane benzenoidPane) {
        selectedBenzenoidPanes.add(benzenoidPane);
    }

    public void removeSelectedBenzenoidPane(BenzenoidPane benzenoidPane) {
        selectedBenzenoidPanes.remove(benzenoidPane);
    }

    public void setDescription(String description) {
        benzenoidPropertiesArea.setText(description);
    }

    public String getName() {
        return this.getText();
    }

    public void refresh() {

        System.out.println("refresh() 2 !");

        gridPane = new GridPane();

        gridPane.setPadding(new Insets(20));
        gridPane.setHgap(25);
        gridPane.setVgap(15);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(85);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(15);

        gridPane.getColumnConstraints().addAll(col1, col2);

        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(5);

        RowConstraints row2 = new RowConstraints();
        row2.setPercentHeight(55);

        RowConstraints row3 = new RowConstraints();
        row3.setPercentHeight(5);

        RowConstraints row4 = new RowConstraints();
        row4.setPercentHeight(35);

        gridPane.getRowConstraints().addAll(row1, row2, row3, row4);

        ScrollPane scrollPane = new ScrollPane();

        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        flowPane = new FlowPane();

        flowPane.getChildren().clear();

        flowPane.setHgap(20);
        flowPane.setVgap(20);
        flowPane.setPadding(new Insets(10));

        benzenoidPanes.clear();

        BenzenoidCollectionPane collectionPane = this;

        final Service<Void> calculateService = new Service<>() {

            @Override
            protected Task<Void> createTask() {
                return new Task<>() {

                    @Override
                    protected Void call() {

                        int index = 0;

                        for (int i = 0; i < molecules.size(); i++) {

                            Benzenoid molecule = molecules.get(i);
                            DisplayType displayType = displayTypes.get(i);

                            MoleculeGroup group;
                            String description = molecule.getDescription();

                            try {

                                switch (displayType) {
                                    case RE_LIN: {
                                        Aromaticity aromaticity = molecule.getAromaticity();

                                        double[][] circuits = aromaticity.getLocalCircuits();

                                        for (int j = 0; j < circuits.length; j++) {
                                            System.out.print("H" + j + " : ");
                                            for (int k = 0; k < circuits[j].length; k++) {
                                                System.out.print(circuits[j][k] + " ");
                                            }
                                            System.out.println();
                                        }

                                        for (int j = 0; j < molecule.getNbHexagons(); j++) {
                                            System.out.println("H_" + j + " = " + aromaticity.getLocalAromaticity()[j]);
                                        }

                                        group = new AromaticityGroup(molecule, aromaticity);
                                    }
                                    break;

                                    case RE_LIN_FAN: {
                                        Aromaticity aromaticity = LinFanAlgorithm.computeEnergy(molecule);
                                        aromaticity.normalize(molecule.getNbKekuleStructures());

                                        double[][] circuits = aromaticity.getLocalCircuits();

                                        for (int j = 0; j < circuits.length; j++) {
                                            System.out.print("H" + j + " : ");
                                            for (int k = 0; k < circuits[j].length; k++) {
                                                System.out.print(circuits[j][k] + " ");
                                            }
                                            System.out.println();
                                        }

                                        for (int j = 0; j < molecule.getNbHexagons(); j++) {
                                            System.out.println("H_" + j + " = " + aromaticity.getLocalAromaticity()[j]);
                                        }

                                        group = new AromaticityGroup(molecule, aromaticity);
                                    }
                                    break;

								case CLAR_COVER:
									group = new ClarCoverGroup(molecule, molecule.getClarCoverSolution());
									break;

								case KEKULE:
									int[][] kekuleStructure = molecule.getKekuleStructures().get(index);
									group = new KekuleStructureGroup(molecule, kekuleStructure);
									description += "structure " + (index + 1);
									break;

								case CLAR_COVER_FIXED:
									group = new ClarCoverFixedBondGroup(molecule, molecule.getClarCoverSolution(),
											molecule.getFixedBonds(), molecule.getFixedCircles());
									break;

								case RBO:
									group = new RBOGroup(molecule);
									break;

								case RADICALAR:
									group = new RadicalarClarCoverGroup(molecule);
									break;

								case IMS2D1A:
									group = new IMS2D1AGroup(molecule);
									break;

                                    case CLAR_RE:
                                        group = new ClarCoverREGroup(molecule, molecule.resonanceEnergyClar());
                                        break;

								default:
									group = new MoleculeGroup(molecule);
									break;
								}

                                BenzenoidPane benzenoidPane = new BenzenoidPane(collectionPane, null, group,
                                        description, molecule.getVerticesSolutions(), index, false);
                                benzenoidPanes.add(benzenoidPane);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            index++;
                        }

                        return null;
                    }

                };
            }
        };

        calculateService.stateProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case FAILED:
                case CANCELLED:
                case SUCCEEDED:
                    flowPane.getChildren().clear();
                    for (BenzenoidPane pane : benzenoidPanes) {
                        flowPane.getChildren().add(pane);
                    }
                    break;

                default:
                    break;
            }

        });

        calculateService.start();

        gridPane.setPrefWidth(1400);

        gridPane.setPadding(new Insets(20));
        gridPane.setHgap(25);
        gridPane.setVgap(15);

        gridPane.setPrefWidth(gridPane.getPrefWidth());

        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        scrollPane.setPrefWidth(1400);

        scrollPane.setContent(flowPane);
        gridPane.add(scrollPane, 0, 0, 1, 4);

		borderPane = new BorderPane();
		selectedArea = null;

		switch (displayedProperty) {

		case PROPERTIES:
			borderPane.setLeft(previousButton);
			borderPane.setCenter(propertiesLabel);
			borderPane.setRight(nextButton);
			selectedArea = benzenoidPropertiesArea;
			break;

		case FREQUENCIES:
			borderPane.setLeft(previousButton);
			borderPane.setCenter(IRSpectraLabel);
			borderPane.setRight(nextButton);
			selectedArea = IRSpectraArea;
			break;

            default:
                // DO_NOTHING
                break;
        }

        Label logsLabel = new Label("Logs");

        Button clearButton = new Button();

        clearButton.resize(30, 30);
        clearButton.setStyle("-fx-background-color: transparent;");

        Image imageAddButton;

        imageAddButton = new Image("/resources/graphics/icon-delete.png");

        ImageView view = new ImageView(imageAddButton);
        clearButton.setPadding(new Insets(0));
        clearButton.setGraphic(view);

        clearButton.setOnAction(e -> parent.clearConsoles());

        HBox logBox = new HBox(3.0);
        logBox.getChildren().addAll(logsLabel, clearButton);

        logBox.setAlignment(Pos.CENTER);

        logsLabel.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, FontPosture.ITALIC, 15));
        logsLabel.setMaxWidth(Double.MAX_VALUE);
        logsLabel.setAlignment(Pos.CENTER);

        benzenoidPropertiesArea.setEditable(false);

        refreshCollectionProperties();

        gridPane.add(borderPane, 1, 0);
        gridPane.add(selectedArea, 1, 1);

        gridPane.add(logBox, 1, 2);
        gridPane.add(console, 1, 3);

        GridPane.setFillHeight(benzenoidPropertiesArea, true);

        this.setContent(gridPane);
    }

    public void refreshCollectionProperties() {

        StringBuilder collectionProperties = new StringBuilder();
        collectionProperties.append(molecules.size()).append(" benzenoid");
        if (molecules.size() > 1)
            collectionProperties.append("s");

        collectionProperties.append(", ").append(selectedBenzenoidPanes.size()).append(" selected benzenoid");
        if (selectedBenzenoidPanes.size() > 1)
            collectionProperties.append("s");

        int nbClassic = 0;
        int nbRe = 0;
        int nbClar = 0;
        int nbRBO = 0;

        for (DisplayType type : displayTypes) {
            switch (type) {
                case RE_LIN:
                case RE_LIN_FAN:
                    nbRe++;
                    break;
                case CLAR_COVER:
                    nbClar++;
                    break;
                case RBO:
                    nbRBO++;
                    break;
                default:
                    nbClassic++;
                    break;
            }
        }

        if (nbClassic > 0) {
            collectionProperties.append(", ").append(nbClassic).append(" normal view");
            if (nbClassic > 1)
                collectionProperties.append("s");
        }

        if (nbRe > 0) {
            collectionProperties.append(", ").append(nbRe).append(" RE view");
            if (nbRe > 1)
                collectionProperties.append("s");
        }

        if (nbClar > 0) {
            collectionProperties.append(", ").append(nbClar).append(" Clar cover view");
            if (nbClar > 1)
                collectionProperties.append("s");
        }

        if (nbRBO > 0) {
            collectionProperties.append(", ").append(nbRBO).append(" RBO view");
            if (nbRBO > 1)
                collectionProperties.append("s");
        }

        parent.setCollectionPropertiesText(collectionProperties.toString());
    }

    public BenzenoidCollectionsManagerPane getParent() {
        return parent;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void removeBenzenoidPanes(ArrayList<BenzenoidPane> benzenoidPanesRemove) {

        System.out.println("removeBenzenoidPanes(): " + selectedBenzenoidPanes.size() + " selections");

        ArrayList<Benzenoid> moleculesToRemove = new ArrayList<>();
        ArrayList<BenzenoidPane> panesToRemove = new ArrayList<>();
        ArrayList<Integer> displayTypesToRemove = new ArrayList<>();

        for (BenzenoidPane pane : benzenoidPanesRemove) {

            moleculesToRemove.add(molecules.get(pane.getIndex()));
            displayTypesToRemove.add(pane.getIndex());
            panesToRemove.add(pane);
        }

        for (Integer i : displayTypesToRemove) {
            displayTypes.set(i, null);
        }

        for (int i = 0; i < displayTypes.size(); i++) {
            if (displayTypes.get(i) == null) {
                displayTypes.remove(i);
                i--;
            }
        }

        for (int i = 0; i < moleculesToRemove.size(); i++) {

            Benzenoid molecule = moleculesToRemove.get(i);
            BenzenoidPane pane = panesToRemove.get(i);

            molecules.remove(molecule);
            selectedMolecules.remove(molecule);
            benzenoidPanes.remove(pane);
        }

        selectedBenzenoidPanes.clear();

        refresh();
    }



	public Benzenoid getMolecule(int index) {
		return molecules.get(index);
	}

	@Override
	public String toString() {
		return "BenzenoidSetPane::" + getName();
	}

    public void copy() {
        if (selectedBenzenoidPanes.size() > 0)
            parent.copy(selectedBenzenoidPanes);
        else if (hoveringPane != null) {
            ArrayList<BenzenoidPane> selection = new ArrayList<>();
            selection.add(hoveringPane);
            parent.copy(selection);
        }
    }

	public void move(BenzenoidCollectionPane originPane, BenzenoidCollectionPane destinationPane) {
		parent.move(originPane, destinationPane);
	}


	public ArrayList<BenzenoidPane> getSelectedBenzenoidPanes() {
		return selectedBenzenoidPanes;
	}

	public DisplayType getDisplayType(int index) {
		return displayTypes.get(index);
	}

    public void setPropertiesArea(String properties) {
        benzenoidPropertiesArea.setText(properties);
    }

    public ArrayList<BenzenoidPane> getBenzenoidPanes() {
        return benzenoidPanes;
    }

    public BenzenoidPane getHoveringPane() {
        return hoveringPane;
    }

    public void setHoveringPane(BenzenoidPane hoveringPane) {
        this.hoveringPane = hoveringPane;
    }

    public void unselectAll() {

        for (BenzenoidPane benzenoidPane : benzenoidPanes) {

            if (benzenoidPane.isSelected())
                benzenoidPane.unselect();
        }
    }

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public void refreshColorScales() {
        for (int i = 0; i < benzenoidPanes.size(); i++) {
            DisplayType displayType = displayTypes.get(i);
            BenzenoidPane benzenoidPane = benzenoidPanes.get(i);

            if (displayType == DisplayType.RE_LIN || displayType == DisplayType.RE_LIN_FAN) {
                AromaticityGroup aromaticityGroup = (AromaticityGroup) benzenoidPane.getBenzenoidDraw();
                aromaticityGroup.refreshColors();
            }
        }
    }

	public void setComparator(MoleculeComparator comparator) {
		for (Benzenoid molecule : molecules)
			molecule.setComparator(comparator);
	}

    public void sort(boolean ascending) {
        unselectAll();
        if (ascending)
            molecules.sort(Collections.reverseOrder());
        else
            Collections.sort(molecules);
        refresh();
    }

    public ArrayList<Benzenoid> getMolecules() {
        return molecules;
    }

	public void export(File directory) {
		for (int i = 0; i < molecules.size(); i++) {

            Benzenoid benzenoid = molecules.get(i);

            String separator;

            if (Utils.onWindows())
                separator = "\\";
            else
                separator = "/";

			String filename = benzenoid.getNames().get(0) + ".graph";

            File file = new File(directory.getAbsolutePath() + separator + filename);
            try {
                BenzenoidParser.exportToGraphFile(benzenoid, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void refreshRIType(RIType type) {
        for (BenzenoidPane benzenoidPane : benzenoidPanes) {
            benzenoidPane.refreshRIType(type);
        }
    }

    public void setIRSpectraData(String frequencies) {
        IRSpectraArea.setText(frequencies);
    }

    public Console getConsole() {
        return console;
    }
}
