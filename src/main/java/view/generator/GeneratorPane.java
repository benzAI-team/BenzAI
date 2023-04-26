package view.generator;

import application.BenzenoidApplication;
import application.Settings;
import generator.GeneralModel;
import generator.ModelBuilder;
import generator.properties.Property;
import generator.properties.model.ModelProperty;
import generator.properties.model.RectangleProperty;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import molecules.Molecule;
import utils.Utils;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionPane.DisplayType;
import view.generator.boxes.*;
import view.primaryStage.ScrollPaneWithPropertyList;

import java.util.ArrayList;

public class GeneratorPane extends ScrollPaneWithPropertyList {

	private final BenzenoidApplication application;
	private GeneralModel model;
	private boolean canStartGeneration;
	private boolean isRunning;
	private ArrayList<Molecule> generatedMolecules;

	BenzenoidCollectionPane selectedCollectionTab;

	private Label titleLabel;
	private ImageView loadIcon;
	private ImageView warningIcon;
	private GridPane gridPane;

	private ArrayList<HBoxCriterion> hBoxesSolverCriterions;
	

	private HBox buttonsBox;
	private Button addButton;
	private Button closeButton;
	private Button generateButton;
	private Button stopButton;
	private Button pauseButton;
	private Button resumeButton;
	private final Label solutionTextLabel = new Label("Number of solutions already found:");
	private final Label solutionNumberLabel = new Label("0");


	public GeneratorPane(BenzenoidApplication application) {
		this.application = application;
		isRunning = false;
		setNbCriterions(1);
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
		buildButtons();

		setChoiceBoxesCriterions(new ArrayList<>());
		ChoiceBoxCriterion choiceBoxCriterion = new ChoiceBoxCriterion(0, this, getModelPropertySet());

		setHBoxesCriterions(new ArrayList<>());
		getChoiceBoxesCriterions().add(choiceBoxCriterion);
		getHBoxesCriterions().add(new HBoxDefaultCriterion(this, choiceBoxCriterion));

		hBoxesSolverCriterions = new ArrayList<>();
		for(Property property : GeneralModel.getSolverPropertySet())
			hBoxesSolverCriterions.add(property.getHBoxCriterion(this, null));
		gridPane = buildGridPane();

		this.setContent(gridPane);

		checkSettings();

		placeComponents();
	}
/***
 * 
 */
	private void setPaneDimensions() {
		this.setFitToHeight(true);
		this.setFitToWidth(true);
		this.setPrefWidth(1400);
		this.setPrefWidth(this.getPrefWidth());
	}
	/***
	 * 
	 */
	private void buildButtons() {
		addButton = buildAddButton();
		closeButton = buildCloseButton();
		stopButton = buildStopButton();
		pauseButton = buildPauseButton();
		resumeButton = buildResumeButton();
		generateButton = buildGenerateButton();
	}

	private void buildIcons() {
		loadIcon = buildLoadIcon();
		warningIcon = buildWarningIcon();
	}

	private ImageView buildLoadIcon() {
		Image image = new Image("/resources/graphics/icon-load.gif");
		ImageView loadIcon = new ImageView(image);
		loadIcon.resize(30, 30);
		return loadIcon;
	}

	private ImageView buildWarningIcon() {
		ImageView warningIcon = new ImageView(new Image("/resources/graphics/icon-warning.png"));
		warningIcon.resize(30, 30);
		Tooltip.install(warningIcon, new Tooltip(
				"A criterion limiting the number of hexagons/carbons/hydrogens/number of lines and columns is required. Moreover, all the criterions must be valid"));
		return warningIcon;
	}

	/***
	 * 
	 * @return addButton for adding a criterion
	 */
	private Button buildAddButton() {
		Button addButton = new Button();
		ImageView imageAdd = new ImageView(new Image("/resources/graphics/icon-add.png"));
		addButton.setGraphic(imageAdd);
		Tooltip.install(addButton, new Tooltip("Add new criterion"));
		addButton.resize(30, 30);
		addButton.setStyle("-fx-background-color: transparent;");
		addButton.setOnAction(e -> {

			ArrayList<Integer> invalidIndexes = containsInvalidCriterion();

			if (invalidIndexes.size() == 0) {
				ChoiceBoxCriterion choiceBoxCriterion = new ChoiceBoxCriterion(getNbCriterions(), this, getModelPropertySet());
				getChoiceBoxesCriterions().add(choiceBoxCriterion);
				getHBoxesCriterions().add(new HBoxDefaultCriterion(this, choiceBoxCriterion));
				setNbCriterions(getNbCriterions() + 1);


				System.out.println(getNbCriterions() + " criterions");

				placeComponents();

			} else {
				Utils.alert("Invalid criterion(s)");
			}
		});

		return addButton;
	}

	/***
	 * 
	 * @return closeButton to return to collection pane
	 */
	private Button buildCloseButton() {
		Button closeButton = new Button();
		ImageView imageClose = new ImageView(new Image("/resources/graphics/icon-close.png"));
		closeButton.setGraphic(imageClose);
		Tooltip.install(closeButton, new Tooltip("Return to the collection"));
		closeButton.resize(30, 30);
		closeButton.setStyle("-fx-background-color: transparent;");

		closeButton.setOnAction(e -> application.switchMode(application.getPanes().getCollectionsPane()));
		return closeButton;
	}

	/***
	 * 
	 * @return stopButton to stop generation
	 */
	private Button buildStopButton() {
		ImageView imageStop = new ImageView(new Image("/resources/graphics/icon-stop.png"));
		Button stopButton = new Button();
		stopButton.setGraphic(imageStop);
		Tooltip.install(stopButton, new Tooltip("Stop generation"));
		stopButton.resize(30, 30);
		stopButton.setStyle("-fx-background-color: transparent;");

		stopButton.setOnAction(e -> {

			if (model.isPaused())
				resumeGeneration();

			model.getProblem().getSolver().limitSearch(() -> model.getGeneratorRun().isStopped());

			model.stop();

			buttonsBox.getChildren().clear();
			buttonsBox.getChildren().addAll(closeButton, addButton, generateButton);
		});
		return stopButton;
	}

	/***
	 * 
	 * @return pauseButton to pause generation
	 */
	private Button buildPauseButton() {
		Button pauseButton = new Button();
		ImageView imagePause = new ImageView(new Image("/resources/graphics/icon-pause.png"));
		pauseButton.setGraphic(imagePause);
		pauseButton.setStyle("-fx-background-color: transparent;");
		pauseButton.resize(32, 32);
		Tooltip.install(pauseButton, new Tooltip("Pause"));
		pauseButton.setOnAction(e -> {
			model.pause();
			buttonsBox.getChildren().clear();
			buttonsBox.getChildren().addAll(closeButton, resumeButton, stopButton);
		});
		return pauseButton;
	}

	/***
	 * 
	 * @return resumeButton to resume generation
	 */
	private Button buildResumeButton() {
		Button resumeButton = new Button();
		ImageView imageResume = new ImageView(new Image("/resources/graphics/icon-resume.png"));
		resumeButton.setGraphic(imageResume);
		resumeButton.setStyle("-fx-background-color: transparent;");
		resumeButton.resize(30, 30);
		Tooltip.install(resumeButton, new Tooltip("Resume generation"));
		resumeButton.setOnAction(e -> resumeGeneration());
		return resumeButton;
	}

	/***
	 * 
	 * @return generateButton to start generation
	 */
	private Button buildGenerateButton() {
		Button generateButton = new Button();
		ImageView imageGenerate = new ImageView(new Image("/resources/graphics/icon-resume.png"));
		generateButton.setGraphic(imageGenerate);
		Tooltip.install(generateButton, new Tooltip("Generate benzenoids"));
		generateButton.setStyle("-fx-background-color: transparent;");
		generateButton.resize(30, 30);

		generateButton.setOnAction(e -> {
			if (!isRunning) {
				generateBenzenoids();
			} else
				Utils.alert("Cannot launch generation: another one is running");
		});
		return generateButton;
	}

	/***
	 * 
	 * @return gridPane
	 */
	private GridPane buildGridPane() {
		GridPane gridPane = new GridPane();
		gridPane.setPrefWidth(1400);
		gridPane.setPadding(new Insets(50));
		gridPane.setHgap(5);
		gridPane.setVgap(5);
		return gridPane;
	}

	private ArrayList<Integer> containsInvalidCriterion() {

		ArrayList<Integer> indexes = new ArrayList<>();

		for (int i = 0; i < getHBoxesCriterions().size(); i++) {
			if (!getHBoxesCriterions().get(i).isValid())
				indexes.add(i);
		}

		return indexes;
	}

	/***
	 * 
	 */
	public void placeComponents() {

		gridPane.getChildren().clear();

		gridPane.add(titleLabel, 0, 0, 2, 1);

		boolean valid = false;

		for (int i = 0; i < getNbCriterions(); i++) {
			GridPane.setValignment(getChoiceBoxesCriterions().get(i), VPos.TOP);
			gridPane.add(getChoiceBoxesCriterions().get(i), 0, i + 1);
			gridPane.add(getHBoxesCriterions().get(i), 1, i + 1);

			HBoxCriterion box = getHBoxesCriterions().get(i);

			if ((box instanceof HBoxHexagonNumberCriterion || box instanceof HBoxNbCarbonsCriterion
					|| box instanceof HBoxNbHydrogensCriterion) && box.isValid()) {

				valid = true;
			}
		}

		buttonsBox = new HBox(5.0);
		buttonsBox.getChildren().addAll(closeButton, addButton, generateButton);
		if (!valid)
			buttonsBox.getChildren().add(warningIcon);

		gridPane.add(buttonsBox, 0, getNbCriterions() + 1);
		
		gridPane.add(new Label("Solver properties:"), 0, getNbCriterions() + 2);
		for(int i = 0; i < hBoxesSolverCriterions.size(); i++) {
			gridPane.add(new Label(GeneralModel.getSolverPropertySet().getNames()[i]), 0, i + getNbCriterions() + 3);
			gridPane.add(this.hBoxesSolverCriterions.get(i), 1, i + getNbCriterions() + 3);
		}
	}

	
	@Override
	protected boolean buildPropertyExpressions() {
		for (HBoxCriterion box : hBoxesSolverCriterions) {
			if(box instanceof HBoxSolverCriterion)
				((HBoxSolverCriterion)box).addPropertyExpression(GeneralModel.getSolverPropertySet());
		}
		return super.buildPropertyExpressions();
	}





	
	/***
	 * 
	 */
	private void generateBenzenoids() {

		if (canStartGeneration) {
			getModelPropertySet().buildModelPropertySet(getHBoxesCriterions());
			GeneralModel.buildSolverPropertySet(hBoxesSolverCriterions);

			application.getBenzenoidCollectionsPane().log("Generating benzenoids", true);
			for(Property modelProperty : getModelPropertySet())
				if(getModelPropertySet().has(modelProperty.getId()))
					application.getBenzenoidCollectionsPane().log(modelProperty.getId(), false);
				
			selectedCollectionTab = application.getBenzenoidCollectionsPane().getSelectedPane();

			buttonsBox.getChildren().clear();
			buttonsBox.getChildren().addAll(closeButton, loadIcon, solutionTextLabel, solutionNumberLabel, pauseButton, stopButton);

			try {
				model = ModelBuilder.buildModel(getModelPropertySet());
				assert model != null;
				solutionNumberLabel.textProperty().bind(model.getNbTotalSolutions().asString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			isRunning = true;

			application.addTask("Benzenoid generation");

			generatedMolecules = new ArrayList<>();
			//model.solve();

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
					isRunning = false;
					Utils.alert("Generation failed.");
					break;
				case CANCELLED:
					isRunning = false;
					Utils.alert("Generation canceled");
					break;
				case SUCCEEDED:
					isRunning = false;
					if (!model.isPaused()) {
						buttonsBox.getChildren().clear();
						buttonsBox.getChildren().addAll(closeButton, addButton, generateButton);
						buildBenzenoidPanesThread();
						application.removeTask("Benzenoid generation");
					}

					else {
						buttonsBox.getChildren().clear();
						buttonsBox.getChildren().addAll(closeButton, resumeButton, stopButton);
					}

					break;

				default:
					break;
				}

			});

			calculateService.start();
		}

		else {
			Utils.alert(
					"A criterion limiting the number of solutions (e.g. limiting hexagons/carbons/hydrogens/number of lines and columns) is required");
		}
	}



	private void resumeGeneration() {

		buttonsBox.getChildren().clear();
		buttonsBox.getChildren().addAll(closeButton, loadIcon, pauseButton, stopButton);

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
				isRunning = false;
				Utils.alert("Generation failed.");
				break;
			case CANCELLED:
				isRunning = false;
				Utils.alert("Generation canceled");
				break;
			case SUCCEEDED:

				if (model.isPaused()) {
					buttonsBox.getChildren().clear();
					buttonsBox.getChildren().addAll(closeButton, resumeButton, stopButton);
				}

				else {
					if (!model.isPaused()) {
						buttonsBox.getChildren().clear();
						buttonsBox.getChildren().addAll(closeButton, addButton, generateButton);
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
				application.switchMode(application.getPanes().getCollectionsPane());
			});

		}

		else {
			Utils.alert("Generation: No solution found");
		}
	}

	/***
	 * 
	 */
	private void buildBenzenoidPanes() {
		if (!model.isPaused()) {

			isRunning = false;



			//generatedMolecules = buildMolecules(model.getResultSolver(), generatedMolecules.size());
			generatedMolecules = model.getResultSolver().getMolecules();
			if (generatedMolecules.size() == 0) {
				Utils.alert("No benzenoid found");
				return;
			}
			
			application.getBenzenoidCollectionsPane().log("-> " + selectedCollectionTab.getName(), false);
			application.getBenzenoidCollectionsPane().log("", false);

			for (Molecule molecule : generatedMolecules) {
				selectedCollectionTab.addBenzenoid(molecule, DisplayType.BASIC);
			}

			selectedCollectionTab.refresh();

			buttonsBox.getChildren().clear();
			buttonsBox.getChildren().addAll(closeButton, addButton, generateButton);
			application.switchMode(application.getPanes().getCollectionsPane());
		}

		else {
			isRunning = false;
			Utils.alert("No benzenoid found");
		}
	}

	/***
	 * Add automatic stop criterions based on the settings
	 */
	private void checkSettings() {
		//System.out.println("Currently not checking the setting");
		checkGenerationTime();
		checkNbMaxSolutions();
		placeComponents();
	}

	/***
	 * Add time limit if in the settings
	 */
	private void checkGenerationTime() {
		Settings settings = application.getSettings();

		if (settings.getGenerationTime() > 0 && settings.getTimeUnit() != null) {
			for(HBoxCriterion hBoxCriterion : this.hBoxesSolverCriterions)
				if(hBoxCriterion instanceof HBoxTimeoutCriterion) {
					((HBoxTimeoutCriterion)hBoxCriterion).setTime(Integer.toString(settings.getGenerationTime()));
					((HBoxTimeoutCriterion)hBoxCriterion).setTimeUnit(settings.getTimeUnit());
				}
		}
	}

	/***
	 * Add number of solution limit if in the settings
	 */
	private void checkNbMaxSolutions() {
		Settings settings = application.getSettings();
		if (settings.getNbMaxSolutions() > 0) {
			for(HBoxCriterion hBoxCriterion : this.hBoxesSolverCriterions)
				if(hBoxCriterion instanceof HBoxNbSolutionsCriterion)
					((HBoxNbSolutionsCriterion)hBoxCriterion).setNbSolutions(Integer.toString(settings.getNbMaxSolutions()));
		}
	}

	public void stop() {
		stopButton.fire();
	}

	public void refreshGenerationPossibility() {

		boolean ok = buildPropertyExpressions();
		canStartGeneration = false;
		if (ok) {
			if(((ModelProperty) getModelPropertySet().getById("hexagons")).hasUpperBound()
					|| ((ModelProperty) getModelPropertySet().getById("carbons")).hasUpperBound()
					|| ((ModelProperty) getModelPropertySet().getById("hydrogens")).hasUpperBound()
					|| ((ModelProperty) getModelPropertySet().getById("rhombus")).hasUpperBound())
				canStartGeneration = true;
			if(((RectangleProperty)getModelPropertySet().getById("rectangle")).hasUpperBounds())
				canStartGeneration = true;

			buttonsBox.getChildren().remove(warningIcon);

			if (canStartGeneration)
				buttonsBox.getChildren().remove(warningIcon);
			else
				buttonsBox.getChildren().add(warningIcon);

		}
	}
	
	public BenzenoidApplication getApplication() {
		return application;
	}
}
