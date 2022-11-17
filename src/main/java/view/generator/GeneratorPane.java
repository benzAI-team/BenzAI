package view.generator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import application.BenzenoidApplication;
import application.Settings;
import generator.GeneralModel;
import generator.ModelBuilder;
import generator.patterns.PatternResolutionInformations;
import generator.properties.Property;
import generator.properties.solver.SolverProperty;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import modelProperty.ModelProperty;
import modelProperty.ModelPropertySet;
import modelProperty.RectangleProperty;
import molecules.Molecule;
import parsers.GraphCoordFileBuilder;
import parsers.GraphFileBuilder;
import parsers.GraphParser;
import utils.Utils;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionPane.DisplayType;
import view.generator.boxes.HBoxModelCriterion;
import view.generator.boxes.HBoxCriterion;
import view.generator.boxes.HBoxDefaultCriterion;
import view.generator.boxes.HBoxNbCarbonsCriterion;
import view.generator.boxes.HBoxHexagonNumberCriterion;
import view.generator.boxes.HBoxNbHydrogensCriterion;
import view.generator.boxes.HBoxNbSolutionsCriterion;
import view.generator.boxes.HBoxSolverCriterion;
import view.generator.boxes.HBoxTimeoutCriterion;


public class GeneratorPane extends ScrollPane {

	private BenzenoidApplication application;
	private GeneralModel model;
	private boolean canStartGeneration;
	private boolean isRunning;
	private ArrayList<Molecule> generatedMolecules;
	private int nbCriterions;

	private PatternResolutionInformations patternsInformations;

	BenzenoidCollectionPane selectedCollectionTab;

	private Label titleLabel;
	private ImageView loadIcon;
	private ImageView warningIcon;
	private GridPane gridPane;
	private ArrayList<ChoiceBoxCriterion> choiceBoxesCriterions;
	private ArrayList<HBoxCriterion> hBoxesCriterions;
	private ArrayList<HBoxCriterion> hBoxesSolverCriterions;
	

	private HBox buttonsBox;
	private Button addButton;
	private Button closeButton;
	private Button generateButton;
	private Button stopButton;
	private Button pauseButton;
	private Button resumeButton;
	private Label solutionTextLabel = new Label("Number of solutions already found:");
	private Label solutionNumberLabel = new Label("0");


	public GeneratorPane(BenzenoidApplication application) {
		this.application = application;
		isRunning = false;
		nbCriterions = 1;
		initializePane();
	}

	private void initializePane() {

		titleLabel = new Label("Benzenoids properties");
		titleLabel.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, FontPosture.ITALIC, 15));
		this.setFitToHeight(true);
		this.setFitToWidth(true);
		this.setPrefWidth(1400);
		this.setPrefWidth(this.getPrefWidth());

		loadIcon = buildLoadIcon();
		warningIcon = buildWarningIcon();
		
		addButton = buildAddButton();
		closeButton = buildCloseButton();
		stopButton = buildStopButton();
		pauseButton = buildPauseButton();
		resumeButton = buildResumeButton();
		generateButton = buildGenerateButton();

		choiceBoxesCriterions = new ArrayList<>();
		ChoiceBoxCriterion choiceBoxCriterion = new ChoiceBoxCriterion(0, this, GeneralModel.getModelPropertySet(), GeneralModel.getSolverPropertySet());

		hBoxesCriterions = new ArrayList<>();
		choiceBoxesCriterions.add(choiceBoxCriterion);
		hBoxesCriterions.add(new HBoxDefaultCriterion(this, choiceBoxCriterion));

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
	 * @return 
	 */
	private ImageView buildLoadIcon() {
		Image image = new Image("/resources/graphics/icon-load.gif");
		ImageView loadIcon = new ImageView(image);
		loadIcon.resize(30, 30);
		return loadIcon;
	}
	/***
	 * 
	 * @return
	 */
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
				ChoiceBoxCriterion choiceBoxCriterion = new ChoiceBoxCriterion(nbCriterions, this, GeneralModel.getModelPropertySet(), GeneralModel.getSolverPropertySet());
				choiceBoxesCriterions.add(choiceBoxCriterion);
				hBoxesCriterions.add(new HBoxDefaultCriterion(this, choiceBoxCriterion));
				nbCriterions++;

				System.out.println(nbCriterions + " criterions");

				placeComponents();

			}
			else {
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

		closeButton.setOnAction(e -> {
			application.switchMode(application.getPanes().getCollectionsPane());
		});
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

			model.getProblem().getSolver().limitSearch(() -> {
				return model.getGeneratorRun().isStopped();
			});

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
		resumeButton.setOnAction(e -> {
			resumeGeneration();
		});
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
	
	/***
	 * 
	 * @return
	 */
	private ArrayList<Integer> containsInvalidCriterion() {

		ArrayList<Integer> indexes = new ArrayList<>();

		for (int i = 0; i < hBoxesCriterions.size(); i++) {
			if (!hBoxesCriterions.get(i).isValid())
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

		for (int i = 0; i < nbCriterions; i++) {
			GridPane.setValignment(choiceBoxesCriterions.get(i), VPos.TOP);
			gridPane.add(choiceBoxesCriterions.get(i), 0, i + 1);
			gridPane.add(hBoxesCriterions.get(i), 1, i + 1);

			HBoxCriterion box = hBoxesCriterions.get(i);

			if ((box instanceof HBoxHexagonNumberCriterion || box instanceof HBoxNbCarbonsCriterion
					|| box instanceof HBoxNbHydrogensCriterion) && box.isValid()) {

				valid = true;
			}
		}

		buttonsBox = new HBox(5.0);
		buttonsBox.getChildren().addAll(closeButton, addButton, generateButton);
		if (!valid)
			buttonsBox.getChildren().add(warningIcon);

		gridPane.add(buttonsBox, 0, nbCriterions + 1);
		
		gridPane.add(new Label("Solver properties:"), 0, nbCriterions + 2);
		for(int i = 0; i < hBoxesSolverCriterions.size(); i++) {
			gridPane.add(new Label(GeneralModel.getSolverPropertySet().getNames()[i]), 0, i + nbCriterions + 3);
			gridPane.add(this.hBoxesSolverCriterions.get(i), 1, i + nbCriterions + 3);
		}
	}
	/***
	 * 
	 * @param index
	 * @param box
	 */
	public void setHBox(int index, HBoxCriterion box) {
		hBoxesCriterions.set(index, box);
		placeComponents();
	}

	/***
	 * 
	 * @param choiceBoxCriterion
	 * @param hBoxCriterion
	 */
	public void removeCriterion(ChoiceBoxCriterion choiceBoxCriterion, HBoxCriterion hBoxCriterion) {

		choiceBoxesCriterions.remove(choiceBoxCriterion);
		hBoxesCriterions.remove(hBoxCriterion);
		nbCriterions--;

		for (int i = 0; i < nbCriterions; i++)
			choiceBoxesCriterions.get(i).setIndex(i);

		placeComponents();
	}
	
	/***
	 * 
	 * @param patternsInformations
	 */
	public void setPatternResolutionInformations(PatternResolutionInformations patternsInformations) {
		this.patternsInformations = patternsInformations;
	}

	/***
	 * 
	 * @return
	 */
	private boolean buildPropertyExpressions() {

		for (HBoxCriterion box : hBoxesCriterions) {
			if (!box.isValid())
				return false;
			if(box instanceof HBoxModelCriterion)
				((HBoxModelCriterion)box).addPropertyExpression(GeneralModel.getModelPropertySet());
		}
		for (HBoxCriterion box : hBoxesSolverCriterions) {
			if(box instanceof HBoxSolverCriterion) 
				((HBoxSolverCriterion)box).addPropertyExpression(GeneralModel.getSolverPropertySet());
		}
		return true;
	}

	/***
	 * 
	 * @param description
	 * @param nbCrowns
	 * @param index
	 * @param verticesSolution
	 * @return
	 */
	public static Molecule buildMolecule(String description, int nbCrowns, int index, ArrayList<Integer> verticesSolution) {
		Molecule molecule = null;
		try {
			String graphFilename = "tmp.graph";
			String graphCoordFilename = "tmp.graph_coord";

			buildGraphFile(nbCrowns, verticesSolution, graphFilename);
			convertGraphCoordFileInstance(graphFilename, graphCoordFilename);
			molecule = buildMolecule(description, nbCrowns, index, verticesSolution, graphCoordFilename);
			deleteTmpFiles(graphFilename, graphCoordFilename);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return molecule;
	}

	private static Molecule buildMolecule(String description, int nbCrowns, int index, ArrayList<Integer> verticesSolution,
			String graphCoordFilename) {
		Molecule molecule = GraphParser.parseUndirectedGraph(graphCoordFilename, null, false);
		molecule.setVerticesSolutions(verticesSolution);
		molecule.setDescription(buildMoleculeDescription(description, index));
		molecule.setNbCrowns(nbCrowns);
		return molecule;
	}

	private static String buildMoleculeDescription(String description, int index) {
		String[] lines = description.split("\n");
		StringBuilder b = new StringBuilder();

		b.append("solution_" + index + "\n");
		for (int j = 1; j < lines.length; j++)
			b.append(lines[j] + "\n");
		return b.toString();
	}

	private static void deleteTmpFiles(String graphFilename, String graphCoordFilename) {
		File file = new File(graphFilename);
		file.delete();
		file = new File(graphCoordFilename);
		file.delete();
	}

	private static void convertGraphCoordFileInstance(String graphFilename, String graphCoordFilename) {
		GraphCoordFileBuilder graphCoordBuilder = new GraphCoordFileBuilder(graphFilename, graphCoordFilename);
		graphCoordBuilder.convertInstance();
	}

	private static void buildGraphFile(int nbCrowns, ArrayList<Integer> verticesSolution,
			String graphFilename) throws IOException {
		GraphFileBuilder graphBuilder = new GraphFileBuilder(verticesSolution, graphFilename,
				nbCrowns);
		graphBuilder.buildGraphFile();
	}


	
	/***
	 * 
	 */
	private void generateBenzenoids() {

		if (canStartGeneration) {
			GeneralModel.buildModelPropertySet(hBoxesCriterions);
			GeneralModel.buildSolverPropertySet(hBoxesSolverCriterions);

			application.getBenzenoidCollectionsPane().log("Generating benzenoids", true);
			for(Property modelProperty : GeneralModel.getModelPropertySet())
				if(GeneralModel.getModelPropertySet().has(modelProperty.getId()))
					application.getBenzenoidCollectionsPane().log(modelProperty.getId(), false);
				
			selectedCollectionTab = application.getBenzenoidCollectionsPane().getSelectedPane();

			buttonsBox.getChildren().clear();
			buttonsBox.getChildren().addAll(closeButton, loadIcon, solutionTextLabel, solutionNumberLabel, pauseButton, stopButton);

			try {
				model = ModelBuilder.buildModel(GeneralModel.getModelPropertySet(), patternsInformations);
				solutionNumberLabel.textProperty().bind(model.getNbTotalSolutions().asString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			isRunning = true;

			application.addTask("Benzenoid generation");

			generatedMolecules = new ArrayList<>();

			final Service<Void> calculateService = new Service<Void>() {

				@Override
				protected Task<Void> createTask() {
					return new Task<Void>() {

						@Override
						protected Void call() throws Exception {

							model.solve();
							System.out.println("Fin génération");
							return null;
						}
					};
				}
			};

			calculateService.stateProperty().addListener(new ChangeListener<State>() {

				@Override
				public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {

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

				}

			});

			calculateService.start();
		}

		else {
			Utils.alert(
					"A criterion limiting the number of solutions (eg, limiting hexagons/carbons/hydrogens/number of lines and columns) is required");
		}
	}



	private void resumeGeneration() {

		buttonsBox.getChildren().clear();
		buttonsBox.getChildren().addAll(closeButton, loadIcon, pauseButton, stopButton);

		final Service<Void> calculateService = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {

					@Override
					protected Void call() throws Exception {
						model.getGeneratorRun().resume();

						return null;
					}
				};
			}
		};

		calculateService.stateProperty().addListener(new ChangeListener<State>() {

			@Override
			public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {

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

			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					buildBenzenoidPanes();
					application.switchMode(application.getPanes().getCollectionsPane());
				}
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
//			ChoiceBoxCriterion choiceBoxCriterion = new ChoiceBoxCriterion(nbCriterions, this, GeneralModel.getModelPropertySet(), GeneralModel.getSolverPropertySet());
//			choiceBoxesCriterions.add(choiceBoxCriterion);
//			hBoxesCriterions.add(new HBoxDefaultCriterion(this, choiceBoxCriterion));
//
//			choiceBoxCriterion.getSelectionModel().select("Time limit");

//			HBoxTimeoutCriterion criterionBox = (HBoxTimeoutCriterion) hBoxesCriterions.get(choiceBoxCriterion.getIndex());
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

//			ChoiceBoxCriterion choiceBoxCriterion = new ChoiceBoxCriterion(nbCriterions, this, GeneralModel.getModelPropertySet(), GeneralModel.getSolverPropertySet());
//			choiceBoxesCriterions.add(choiceBoxCriterion);
//			hBoxesCriterions.add(new HBoxDefaultCriterion(this, choiceBoxCriterion));
//
//			choiceBoxCriterion.getSelectionModel().select("Number of solutions");
//
//			HBoxNbSolutionsCriterion criterionBox = (HBoxNbSolutionsCriterion) hBoxesCriterions
//					.get(choiceBoxCriterion.getIndex());
//			HBoxNbSolutionsCriterion criterionBox = new HBoxNbSolutionsCriterion(this, null);
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
		ModelPropertySet modelPropertySet = GeneralModel.getModelPropertySet();
		if (ok) {
			if(((ModelProperty) modelPropertySet.getById("hexagons")).hasUpperBound()
					|| ((ModelProperty) modelPropertySet.getById("carbons")).hasUpperBound()
					|| ((ModelProperty) modelPropertySet.getById("hydrogens")).hasUpperBound()
					|| ((ModelProperty) modelPropertySet.getById("rhombus")).hasUpperBound())
				canStartGeneration = true;
			if(((RectangleProperty)modelPropertySet.getById("rectangle")).hasUpperBounds())
				canStartGeneration = true;

			buttonsBox.getChildren().remove(warningIcon);

			if (canStartGeneration)
				buttonsBox.getChildren().remove(warningIcon);
			else
				buttonsBox.getChildren().add(warningIcon);

		}
	}
	
	/***
	 * getters, setters
	 */

	public ArrayList<HBoxCriterion> getHBoxesCriterions() {
		return hBoxesCriterions;
	}
	

	public BenzenoidApplication getApplication() {
		return application;
	}
}
