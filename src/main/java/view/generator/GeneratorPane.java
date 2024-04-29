package view.generator;

import application.BenzenoidApplication;
import application.InterruptibleOperation;
import application.Settings;
import benzenoid.Benzenoid;
import generator.GeneralModel;
import generator.ModelBuilder;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import properties.ModelPropertySet;
import properties.Property;
import utils.Utils;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionPane.DisplayType;
import view.generator.boxes.HBoxCriterion;
import view.generator.boxes.HBoxNbSolutionsCriterion;
import view.generator.boxes.HBoxSolverCriterion;
import view.generator.boxes.HBoxTimeoutCriterion;
import view.primaryStage.ButtonBoxWithPause;
import view.primaryStage.ScrollPaneWithPropertyList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GeneratorPane extends ScrollPaneWithPropertyList {

    private GeneralModel model;
    private ArrayList<Benzenoid> generatedMolecules;

    private BenzenoidCollectionPane selectedCollectionTab;

    private Label titleLabel;

    private ArrayList<HBoxCriterion> hBoxesSolverCriterions;


    private final Label solutionTextLabel = new Label("Number of solutions already found:");
    private final Label solutionNumberLabel = new Label("0");

    private HBox criterionListBox;

    static final File defaultPropertyListFile = new File("constraints");


    public GeneratorPane(BenzenoidApplication application) {
        super(new ModelPropertySet(), new InterruptibleOperation() {
                    @Override
                    public void pause(ScrollPaneWithPropertyList pane) {
                        //TODO
                    }

                    @Override
                    public void resume(ScrollPaneWithPropertyList pane) {
                        //TODO
                    }

                    @Override
                    public void run(ScrollPaneWithPropertyList pane) {
                        if (pane.getOperation().isPossible())
                            ((GeneratorPane) pane).generateBenzenoids();
                        else
                            Utils.alert("Invalid criterion(s)");
                    }

                    @Override
                    public void stop(ScrollPaneWithPropertyList pane) {
                        ((GeneratorPane) pane).stop();
                    }
                },
                application);
        initialize();
    }

    /***
     *
     */
    private void initialize() {
        titleLabel = new Label("Benzenoids properties");
        titleLabel.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, FontPosture.ITALIC, 15));
        setPaneDimensions();
        buildIcons();
        setButtonBox(new ButtonBoxWithPause("generate", this));
        setGridPane(buildGridPane());
        this.setContent(getGridPane());
        initializeCriterionListButtons();
        hBoxesSolverCriterions = initializeSolverCriterionBoxes();
        loadPropertyExpressionListAutosave();
        initializeCriterionBoxes();
        checkSettings();
        placeComponents();
        //getModelPropertySet().clearAllPropertyExpressions();
    }

    private void loadPropertyExpressionListAutosave() {
        try {
            getPropertySet().load(defaultPropertyListFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void initializeCriterionListButtons() {
        Button resetButton = buildResetButton();
        Button loadButton = buildLoadButton();
        Button saveButton = buildSaveButton();
        criterionListBox = new HBox();
        criterionListBox.getChildren().addAll(resetButton, loadButton, saveButton);
    }

    private Button buildResetButton() {
        Button resetButton = new Button("Reset");
        resetButton.setOnAction(e -> {
            getPropertySet().clearAllPropertyExpressions();
            initializeCriterionBoxes();
            placeComponents();
        });
        return resetButton;
    }

    private Button buildLoadButton() {
        Button loadButton = new Button("Load");
        loadButton.setOnAction(e -> {
            try {
                getPropertySet().load();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            initializeCriterionBoxes();
            placeComponents();
            getPropertySet().clearAllPropertyExpressions();
        });
        return loadButton;
    }

    private Button buildSaveButton() {
        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            getPropertySet().buildPropertySet(getHBoxCriterions());
            getPropertySet().save();
            getPropertySet().clearAllPropertyExpressions();
        });
        return saveButton;
    }

    private ArrayList<HBoxCriterion> initializeSolverCriterionBoxes() {
        ArrayList<HBoxCriterion> hBoxSolverCriterions = new ArrayList<>();
        for (Property property : GeneralModel.getSolverPropertySet())
            hBoxSolverCriterions.add(property.makeHBoxCriterion(this, null));
        return hBoxSolverCriterions;
    }

    /***
     *
     */
    @Override
    public void placeComponents() {
        getGridPane().getChildren().clear();
        getGridPane().add(titleLabel, 0, 0, 2, 1);
        getGridPane().add(criterionListBox, 1, 0, 3, 1);
        //System.out.println("Place");
        placeModelPropertyComponents();
        placeSolverPropertyComponents();
        initEventHandlers();
        refreshGlobalValidity();
    }

    private void placeModelPropertyComponents() {
        placeCriterionBoxes();
        getGridPane().add(getButtonBox(), 0, getNbBoxCriterions() + 1);
    }

    private void placeSolverPropertyComponents() {
        int nbCriterions = getNbBoxCriterions();
        getGridPane().add(new Label("Solver properties:"), 0, nbCriterions + 2);
        for (int i = 0; i < hBoxesSolverCriterions.size(); i++) {
            getGridPane().add(new Label(GeneralModel.getSolverPropertySet().getNames()[i]), 0, i + nbCriterions + 3);
            getGridPane().add(this.hBoxesSolverCriterions.get(i), 1, i + nbCriterions + 3);
        }
    }

    @Override
    protected boolean buildPropertyExpressions() {
        for (HBoxCriterion box : hBoxesSolverCriterions) {
            if (box instanceof HBoxSolverCriterion)
                ((HBoxSolverCriterion) box).addPropertyExpression(GeneralModel.getSolverPropertySet());
        }
        return super.buildPropertyExpressions();
    }

    /***
     *
     */
    private void generateBenzenoids() {
        if (getOperation().isPossible()) {
            getPropertySet().buildPropertySet(getHBoxCriterions());
            getPropertySet().save(defaultPropertyListFile);
            GeneralModel.buildSolverPropertySet(hBoxesSolverCriterions);

            getApplication().getBenzenoidCollectionsPane().log("Generating benzenoids", true);
            for (Property modelProperty : getPropertySet())
                if (getPropertySet().has(modelProperty.getId()))
                    getApplication().getBenzenoidCollectionsPane().log(modelProperty.getId(), false);

            selectedCollectionTab = getApplication().getBenzenoidCollectionsPane().getSelectedPane();

            getButtonBox().getChildren().clear();
            getButtonBox().getChildren().addAll(getButtonBox().getCloseButton(), getLoadIcon(), solutionTextLabel, solutionNumberLabel, ((ButtonBoxWithPause) getButtonBox()).getPauseButton(), getButtonBox().getStopButton());

            try {
                model = ModelBuilder.buildModel((ModelPropertySet) getPropertySet());
                assert model != null;
                solutionNumberLabel.textProperty().bind(model.getNbTotalSolutions().asString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            getApplication().addTask("Benzenoid generation");

            generatedMolecules = new ArrayList<>();

            final Service<Void> calculateService = new Service<>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<>() {
                        @Override
                        protected Void call() {
                            model.solve();
                            System.out.println("Fin génération");
                            return null;
                        }
                    };
                }
            };

            calculateService.stateProperty().addListener((observable, oldValue, newValue) -> {
                switch (newValue) {
                    case FAILED:
                        Utils.alert("Generation failed.");
                        break;
                    case CANCELLED:
                        Utils.alert("Generation canceled");
                        break;
                    case SUCCEEDED:
                        if (model.isPaused()) {
                            resetButtonBox(getButtonBox().getCloseButton(), ((ButtonBoxWithPause) getButtonBox()).getResumeButton(), getButtonBox().getStopButton());
                        } else {
                            resetButtonBox(getButtonBox().getCloseButton(), getButtonBox().getAddButton(), getButtonBox().getOperationButton());
                            buildBenzenoidPanesThread();
                            getApplication().removeTask("Benzenoid generation");
                        }
                        break;
                    default:
                        break;
                }
            });
            calculateService.start();
        } else {
            Utils.alert(
                    "A criterion limiting the number of solutions (e.g. limiting hexagons/carbons/hydrogens/number of lines and columns) is required");
        }
    }

    @Override
    protected void resumeOperation() {

        getButtonBox().getChildren().clear();
        getButtonBox().getChildren().addAll(getButtonBox().getCloseButton(), getLoadIcon(), ((ButtonBoxWithPause) getButtonBox()).getPauseButton(), getButtonBox().getStopButton());

        final Service<Void> calculateService = new Service<>() {

            @Override
            protected Task<Void> createTask() {
                return new Task<>() {

                    @Override
                    protected Void call() {
                        model.getGeneratorRun().resume();
                        return null;
                    }
                };
            }
        };

        calculateService.stateProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case FAILED:
                    Utils.alert("Generation failed.");
                    break;
                case CANCELLED:
                    Utils.alert("Generation canceled");
                    break;
                case SUCCEEDED:
                    if (model.isPaused()) {
                        resetButtonBox(getButtonBox().getCloseButton(), ((ButtonBoxWithPause) getButtonBox()).getResumeButton(), getButtonBox().getStopButton());
                    } else {
                        if (!model.isPaused()) {
                            resetButtonBox(getButtonBox().getCloseButton(), getButtonBox().getAddButton(), getButtonBox().getOperationButton());
                            buildBenzenoidPanes();
                        }
                    }
                    break;
                default:
                    break;
            }
        });
        calculateService.start();
    }

    /***
     *
     */
    private void buildBenzenoidPanesThread() {

        int size = model.getResultSolver().size();

        if (size > 0) {
            Platform.runLater(() -> {
                buildBenzenoidPanes();
                getApplication().switchMode(getApplication().getPanes().getCollectionsPane());
            });

        } else {
            Utils.alert("Generation: No solution found");
        }
    }

    /***
     *
     */
    private void buildBenzenoidPanes() {
        if (model.isPaused()) {
            Utils.alert("No benzenoid found");
        } else {

            //generatedMolecules = buildMolecules(model.getResultSolver(), generatedMolecules.size());
            generatedMolecules = model.getResultSolver().getMolecules();
            if (generatedMolecules.isEmpty()) {
                Utils.alert("No benzenoid found");
                return;
            }

            getApplication().getBenzenoidCollectionsPane().log("-> " + selectedCollectionTab.getName(), false);
            getApplication().getBenzenoidCollectionsPane().log("", false);

            for (Benzenoid molecule : generatedMolecules) {
                selectedCollectionTab.addBenzenoid(molecule, DisplayType.BASIC);
            }

            selectedCollectionTab.refresh();
            resetButtonBox(getButtonBox().getCloseButton(), getButtonBox().getAddButton(), getButtonBox().getOperationButton());
            getApplication().switchMode(getApplication().getPanes().getCollectionsPane());
        }
    }

    /***
     * Add automatic stop criterions based on the settings
     */
    private void checkSettings() {
        //System.out.println("Currently not checking the setting");
        checkGenerationTime();
        checkNbMaxSolutions();
    }

    /***
     * Add time limit if in the settings
     */
    private void checkGenerationTime() {
        Settings settings = getApplication().getSettings();

        if (settings.getGenerationTime() > 0 && settings.getTimeUnit() != null) {
            for (HBoxCriterion hBoxCriterion : this.hBoxesSolverCriterions)
                if (hBoxCriterion instanceof HBoxTimeoutCriterion) {
                    ((HBoxTimeoutCriterion) hBoxCriterion).setTime(Integer.toString(settings.getGenerationTime()));
                    ((HBoxTimeoutCriterion) hBoxCriterion).setTimeUnit(settings.getTimeUnit());
                }
        }
    }

    /***
     * Add number of solution limit if in the settings
     */
    private void checkNbMaxSolutions() {
        Settings settings = getApplication().getSettings();
        if (settings.getNbMaxSolutions() > 0) {
            for (HBoxCriterion hBoxCriterion : this.hBoxesSolverCriterions)
                if (hBoxCriterion instanceof HBoxNbSolutionsCriterion)
                    ((HBoxNbSolutionsCriterion) hBoxCriterion).setNbSolutions(Integer.toString(settings.getNbMaxSolutions()));
        }
    }

    public void stop() {
        //stopButton.fire();
        if (model.isPaused())
            resumeOperation();
        model.getProblem().getSolver().limitSearch(() -> model.getGeneratorRun().isStopped());
        model.stop();
        getButtonBox().getChildren().clear();
        getButtonBox().getChildren().addAll(getButtonBox().getCloseButton(), getButtonBox().getAddButton(), getButtonBox().getOperationButton());
    }

    public void refreshGlobalValidity() {
        getOperation().setPossible(getHBoxCriterions().stream().allMatch(HBoxCriterion::isValid)
                && getHBoxCriterions().stream().anyMatch(HBoxCriterion::isBounding));
        getButtonBox().getChildren().remove(getWarningIcon());
        if (!getOperation().isPossible())
            getButtonBox().getChildren().add(getWarningIcon());
    }


    private void resetButtonBox(Button... buttons) {
        getButtonBox().getChildren().clear();
        getButtonBox().getChildren().addAll(buttons);
    }
}
