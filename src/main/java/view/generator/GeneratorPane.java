package view.generator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import application.BenzenoidApplication;
import application.Settings;
import generator.GeneralModel;
import generator.ModelBuilder;
import generator.SolverResults;
import generator.patterns.PatternResolutionInformations;
import generator.properties.Property;
import generator.properties.solver.SolverPropertySet;
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
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import modelProperty.ModelProperty;
import modelProperty.ModelPropertySet;
import modelProperty.RectangleProperty;
import modelProperty.expression.BinaryNumericalExpression;
import modelProperty.expression.PropertyExpression;
import modelProperty.expression.ParameterizedExpression;
import molecules.Molecule;
import parsers.GraphCoordFileBuilder;
import parsers.GraphFileBuilder;
import parsers.GraphParser;
import utils.Utils;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionPane.DisplayType;
import view.filtering.criterions.ConcealedNonKekuleanCriterion;
import view.filtering.criterions.FilteringOperator;
import view.filtering.criterions.NbKekuleStructuresCriterion;
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
	//ArrayList<GeneratorCriterion> criterions = new ArrayList<>();
	private ModelPropertySet modelPropertySet = new ModelPropertySet();
	private SolverPropertySet solverPropertySet = new SolverPropertySet();

	private PatternResolutionInformations patternsInformations;

	BenzenoidCollectionPane selectedCollectionTab;

	private Label titleLabel;
	private ImageView loadIcon;
	private ImageView warningIcon;
	private GridPane gridPane;
	private ArrayList<ChoiceBoxCriterion> choiceBoxesCriterions;
	private ArrayList<HBoxCriterion> hBoxesCriterions;

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

		titleLabel = new Label("Generate benzenoids");
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
		hBoxesCriterions = new ArrayList<>();
		ChoiceBoxCriterion choiceBoxCriterion = new ChoiceBoxCriterion(0, this, modelPropertySet, solverPropertySet);
		choiceBoxesCriterions.add(choiceBoxCriterion);
		hBoxesCriterions.add(new HBoxDefaultCriterion(this, choiceBoxCriterion));

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
				ChoiceBoxCriterion choiceBoxCriterion = new ChoiceBoxCriterion(nbCriterions, this, modelPropertySet, solverPropertySet);
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
				((HBoxModelCriterion)box).addPropertyExpression(modelPropertySet);
			if(box instanceof HBoxSolverCriterion)
				((HBoxSolverCriterion)box).addPropertyExpression(solverPropertySet);
		}
		return true;
	}

//	public static HashMap<String, ArrayList<GeneratorCriterion>> buildCriterionsMap(
//			ArrayList<GeneratorCriterion> criterions) {
//
//		HashMap<String, ArrayList<GeneratorCriterion>> map = new HashMap<>();
//
//		map.put("hexagons", new ArrayList<>());
//		map.put("carbons", new ArrayList<>());
//		map.put("hydrogens", new ArrayList<>());
//		map.put("coronenoid", new ArrayList<>());
//		map.put("irregularity", new ArrayList<>());
//		map.put("diameter", new ArrayList<>());
//		map.put("rectangle", new ArrayList<>());
//		map.put("rhombus", new ArrayList<>());
//		map.put("coronoid", new ArrayList<>());
//		map.put("coronoid2", new ArrayList<>());
//		map.put("catacondensed", new ArrayList<>());
//		map.put("symmetries", new ArrayList<>());
//		map.put("patterns", new ArrayList<>());
//		map.put("stop", new ArrayList<>());
//
//		for (GeneratorCriterion criterion : criterions) {
//
//			Subject subject = criterion.getSubject();
//
//			if (subject == Subject.NB_HEXAGONS)
//				map.get("hexagons").add(criterion);
//
//			else if (subject == Subject.NB_CARBONS)
//				map.get("carbons").add(criterion);
//
//			else if (subject == Subject.NB_HYDROGENS)
//				map.get("hydrogens").add(criterion);
//
//			else if (subject == Subject.CORONENOID || subject == Subject.NB_CROWNS)
//				map.get("coronenoid").add(criterion);
//
//			else if (subject == Subject.XI || subject == Subject.N0 || subject == Subject.N1 || subject == Subject.N2
//					|| subject == Subject.N3 || subject == Subject.N4)
//				map.get("irregularity").add(criterion);
//
//			else if (subject == Subject.RECT_HEIGHT || subject == Subject.RECT_WIDTH)
//				map.get("rectangle").add(criterion);
//
//			else if (subject == Subject.RHOMBUS || subject == Subject.RHOMBUS_DIMENSION)
//				map.get("rhombus").add(criterion);
//
//			else if (subject == Subject.SYMM_MIRROR || subject == Subject.SYMM_ROT_60 || subject == Subject.SYMM_ROT_120
//					|| subject == Subject.SYMM_ROT_180 || subject == Subject.SYMM_VERTICAL
//					|| subject == Subject.SYMM_ROT_120_V || subject == Subject.SYMM_ROT_180_E)
//
//				map.get("symmetries").add(criterion);
//
//			else if (subject == Subject.DIAMETER)
//				map.get("diameter").add(criterion);
//
//			else if (subject == Subject.CORONOID)
//				map.get("coronoid").add(criterion);
//
//			else if (subject == Subject.CORONOID_2 || subject == Subject.NB_HOLES)
//				map.get("coronoid2").add(criterion);
//
//			else if (subject == Subject.CATACONDENSED)
//				map.get("catacondensed").add(criterion);
//
//			else if (subject == Subject.SINGLE_PATTERN || subject == Subject.MULTIPLE_PATTERNS
//					|| subject == Subject.FORBIDDEN_PATTERN || subject == Subject.OCCURENCE_PATTERN)
//				map.get("patterns").add(criterion);
//
//			else if (subject == Subject.TIMEOUT || subject == Subject.NB_SOLUTIONS)
//				map.get("stop").add(criterion);
//		}
//
//		return map;
//	}

	private ArrayList<Molecule> buildMolecules(SolverResults solverResults, int beginIndex) {

		int index = beginIndex;

		ArrayList<Molecule> molecules = new ArrayList<Molecule>();

		for (int i = 0; i < solverResults.size(); i++) {
			ArrayList<Integer> verticesSolution = solverResults.getVerticesSolution(i);
			molecules.add(buildMolecule(solverResults, index, i, verticesSolution));
			index++;
		}

		ArrayList<Molecule> filteredMolecules = filterMolecules(molecules);

		return filteredMolecules;
	}

	private Molecule buildMolecule(SolverResults solverResults, int index, int i, ArrayList<Integer> verticesSolution) {
		Molecule molecule = null;
		try {
			String graphFilename = "tmp.graph";
			String graphCoordFilename = "tmp.graph_coord";

			buildGraphFile(solverResults, i, verticesSolution, graphFilename);
			convertGraphCoordFileInstance(graphFilename, graphCoordFilename);
			molecule = buildMolecule(solverResults, index, i, verticesSolution, graphCoordFilename);
			deleteTmpFiles(graphFilename, graphCoordFilename);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return molecule;
	}

	private Molecule buildMolecule(SolverResults solverResults, int index, int i, ArrayList<Integer> verticesSolution,
			String graphCoordFilename) {
		Molecule molecule = GraphParser.parseUndirectedGraph(graphCoordFilename, null, false);
		molecule.setVerticesSolutions(verticesSolution);
		molecule.setDescription(buildMoleculeDescription(solverResults.getDescriptions().get(i), index));
		molecule.setNbCrowns(solverResults.getNbCrowns().get(i));
		return molecule;
	}

	private String buildMoleculeDescription(String description, int index) {
		String[] lines = description.split("\n");
		StringBuilder b = new StringBuilder();

		b.append("solution_" + index + "\n");
		for (int j = 1; j < lines.length; j++)
			b.append(lines[j] + "\n");
		return b.toString();
	}

	private void deleteTmpFiles(String graphFilename, String graphCoordFilename) {
		File file = new File(graphFilename);
		file.delete();
		file = new File(graphCoordFilename);
		file.delete();
	}

	private void convertGraphCoordFileInstance(String graphFilename, String graphCoordFilename) {
		GraphCoordFileBuilder graphCoordBuilder = new GraphCoordFileBuilder(graphFilename, graphCoordFilename);
		graphCoordBuilder.convertInstance();
	}

	private void buildGraphFile(SolverResults solverResults, int i, ArrayList<Integer> verticesSolution,
			String graphFilename) throws IOException {
		GraphFileBuilder graphBuilder = new GraphFileBuilder(verticesSolution, graphFilename,
				solverResults.getCrown(i));
		graphBuilder.buildGraphFile();
	}

	/***
	 * Filter the molecules thanks to the various checks according to the property set
	 * @param molecules
	 * @return molecules filtered 
	 */
	public ArrayList<Molecule> filterMolecules(ArrayList<Molecule> molecules){
		ArrayList<Molecule> filteredMolecules;
		
		for(Property property : modelPropertySet)
			if(property.hasExpressions()) {
				filteredMolecules = new ArrayList<Molecule>();
				for(Molecule molecule : molecules)
					if(((ModelProperty) property).getChecker().checks(molecule, (ModelProperty) property)) {
						filteredMolecules.add(molecule);
					}
				molecules = filteredMolecules;
			}
		return molecules;

	}
	
	/***
	 * 
	 */
	private void generateBenzenoids() {

		if (canStartGeneration) {

			//criterions = buildCriterions();
			buildModelPropertySet();

			application.getBenzenoidCollectionsPane().log("Generating benzenoids", true);
			for(Property modelProperty : modelPropertySet)
				if(modelPropertySet.has(modelProperty.getId()))
					application.getBenzenoidCollectionsPane().log(modelProperty.getId(), false);
				
			selectedCollectionTab = application.getBenzenoidCollectionsPane().getSelectedPane();

			buttonsBox.getChildren().clear();
			buttonsBox.getChildren().addAll(closeButton, loadIcon, solutionTextLabel, solutionNumberLabel, pauseButton, stopButton);

			try {
				model = ModelBuilder.buildModel(modelPropertySet, patternsInformations);
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

	private boolean buildModelPropertySet() {
		modelPropertySet.clearPropertyExpressions();
		for (HBoxCriterion box : hBoxesCriterions) {
			if (!box.isValid())
				return false;
			if(box instanceof HBoxModelCriterion)
				((HBoxModelCriterion)box).addPropertyExpression(modelPropertySet);
			//TODO : retirer
			if(box instanceof HBoxSolverCriterion)
				((HBoxSolverCriterion)box).addPropertyExpression(solverPropertySet);
		}
		return true;
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

			generatedMolecules = buildMolecules(model.getResultSolver(), generatedMolecules.size());

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
		System.out.println("Currently not checking the setting");
		//checkGenerationTime();
		//checkNbMaxSolutions();
	}
	
	/***
	 * Add time limit if in the settings
	 */
	private void checkGenerationTime() {
		Settings settings = application.getSettings();

		if (settings.getGenerationTime() > 0 && settings.getTimeUnit() != null) {
			ChoiceBoxCriterion choiceBoxCriterion = new ChoiceBoxCriterion(nbCriterions, this, modelPropertySet, solverPropertySet);
			choiceBoxesCriterions.add(choiceBoxCriterion);
			hBoxesCriterions.add(new HBoxDefaultCriterion(this, choiceBoxCriterion));

			choiceBoxCriterion.getSelectionModel().select("Time limit");

			HBoxTimeoutCriterion criterionBox = (HBoxTimeoutCriterion) hBoxesCriterions
					.get(choiceBoxCriterion.getIndex());

			criterionBox.setTime(Integer.toString(settings.getGenerationTime()));
			criterionBox.setTimeUnit(settings.getTimeUnit());

			nbCriterions++;

			placeComponents();
		}
	}
	
	/***
	 * Add number of solution limit if in the settings
	 */
	private void checkNbMaxSolutions() {
		Settings settings = application.getSettings();
		if (settings.getNbMaxSolutions() > 0) {

			ChoiceBoxCriterion choiceBoxCriterion = new ChoiceBoxCriterion(nbCriterions, this, modelPropertySet, solverPropertySet);
			choiceBoxesCriterions.add(choiceBoxCriterion);
			hBoxesCriterions.add(new HBoxDefaultCriterion(this, choiceBoxCriterion));

			choiceBoxCriterion.getSelectionModel().select("Number of solutions");

			HBoxNbSolutionsCriterion criterionBox = (HBoxNbSolutionsCriterion) hBoxesCriterions
					.get(choiceBoxCriterion.getIndex());

			criterionBox.setNbSolutions(Integer.toString(settings.getNbMaxSolutions()));

			nbCriterions++;

			placeComponents();
		}
	}

	public void stop() {
		stopButton.fire();
	}

	public void refreshGenerationPossibility() {

		boolean ok = buildPropertyExpressions();
		canStartGeneration = false;

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
