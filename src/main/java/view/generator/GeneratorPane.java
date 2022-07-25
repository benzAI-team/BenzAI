package view.generator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import application.BenzenoidApplication;
import application.Settings;
import generator.GeneralModel;
import generator.GeneratorCriterion;
import generator.GeneratorCriterion.Operator;
import generator.GeneratorCriterion.Subject;
import generator.ModelBuilder;
import generator.ResultSolver;
import generator.fragments.FragmentResolutionInformations;
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
import view.generator.boxes.HBoxCriterion;
import view.generator.boxes.HBoxDefaultCriterion;
import view.generator.boxes.HBoxNbCarbonsCriterion;
import view.generator.boxes.HBoxNbHexagonsCriterion;
import view.generator.boxes.HBoxNbHydrogensCriterion;
import view.generator.boxes.HBoxNbSolutionsCriterion;
import view.generator.boxes.HBoxTimeoutCriterion;

public class GeneratorPane extends ScrollPane {

	private BenzenoidApplication application;
	private GeneralModel model;
	private boolean canStartGeneration;
	private boolean isRunning;
	private ArrayList<Molecule> generatedMolecules;
	private int nbCriterions;
	ArrayList<GeneratorCriterion> criterions = new ArrayList<>();
	private FragmentResolutionInformations fragmentsInformations;

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
		ChoiceBoxCriterion choiceBoxCriterion = new ChoiceBoxCriterion(0, this);
		choiceBoxesCriterions.add(choiceBoxCriterion);
		hBoxesCriterions.add(new HBoxDefaultCriterion(this, choiceBoxCriterion));

		gridPane = buildGridPane();

		this.setContent(gridPane);

		checkSettings();

		refresh();
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
				ChoiceBoxCriterion choiceBoxCriterion = new ChoiceBoxCriterion(nbCriterions, this);
				choiceBoxesCriterions.add(choiceBoxCriterion);
				hBoxesCriterions.add(new HBoxDefaultCriterion(this, choiceBoxCriterion));
				nbCriterions++;

				System.out.println(nbCriterions + " criterions");

				refresh();

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

	public void refresh() {

		gridPane.getChildren().clear();

		gridPane.add(titleLabel, 0, 0, 2, 1);

		boolean valid = false;

		for (int i = 0; i < nbCriterions; i++) {
			GridPane.setValignment(choiceBoxesCriterions.get(i), VPos.TOP);
			gridPane.add(choiceBoxesCriterions.get(i), 0, i + 1);
			gridPane.add(hBoxesCriterions.get(i), 1, i + 1);

			HBoxCriterion box = hBoxesCriterions.get(i);

			if ((box instanceof HBoxNbHexagonsCriterion || box instanceof HBoxNbCarbonsCriterion
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

	public void setHBox(int index, HBoxCriterion hbox) {
		hBoxesCriterions.set(index, hbox);
		refresh();
	}

	public void removeCriterion(ChoiceBoxCriterion choiceBoxCriterion, HBoxCriterion hBoxCriterion) {

		choiceBoxesCriterions.remove(choiceBoxCriterion);
		hBoxesCriterions.remove(hBoxCriterion);
		nbCriterions--;

		for (int i = 0; i < nbCriterions; i++)
			choiceBoxesCriterions.get(i).setIndex(i);

		refresh();
	}

	public BenzenoidApplication getApplication() {
		return application;
	}

	public void setFragmentResolutionInformations(FragmentResolutionInformations fragmentsInformations) {
		this.fragmentsInformations = fragmentsInformations;
	}

	private ArrayList<GeneratorCriterion> buildCriterions() {

		ArrayList<GeneratorCriterion> criterions = new ArrayList<>();

		for (HBoxCriterion box : hBoxesCriterions) {

			if (!box.isValid())
				return null;

			criterions.addAll(box.buildCriterions());
		}

		return criterions;
	}

	public static HashMap<String, ArrayList<GeneratorCriterion>> buildCriterionsMap(
			ArrayList<GeneratorCriterion> criterions) {

		HashMap<String, ArrayList<GeneratorCriterion>> map = new HashMap<>();

		map.put("hexagons", new ArrayList<>());
		map.put("carbons", new ArrayList<>());
		map.put("hydrogens", new ArrayList<>());
		map.put("coronenoid", new ArrayList<>());
		map.put("irregularity", new ArrayList<>());
		map.put("diameter", new ArrayList<>());
		map.put("rectangle", new ArrayList<>());
		map.put("rhombus", new ArrayList<>());
		map.put("coronoid", new ArrayList<>());
		map.put("coronoid2", new ArrayList<>());
		map.put("catacondensed", new ArrayList<>());
		map.put("symmetries", new ArrayList<>());
		map.put("patterns", new ArrayList<>());
		map.put("stop", new ArrayList<>());

		for (GeneratorCriterion criterion : criterions) {

			Subject subject = criterion.getSubject();

			if (subject == Subject.NB_HEXAGONS)
				map.get("hexagons").add(criterion);

			else if (subject == Subject.NB_CARBONS)
				map.get("carbons").add(criterion);

			else if (subject == Subject.NB_HYDROGENS)
				map.get("hydrogens").add(criterion);

			else if (subject == Subject.CORONENOID || subject == Subject.NB_CROWNS)
				map.get("coronenoid").add(criterion);

			else if (subject == Subject.XI || subject == Subject.N0 || subject == Subject.N1 || subject == Subject.N2
					|| subject == Subject.N3 || subject == Subject.N4)
				map.get("irregularity").add(criterion);

			else if (subject == Subject.RECT_HEIGHT || subject == Subject.RECT_WIDTH)
				map.get("rectangle").add(criterion);

			else if (subject == Subject.RHOMBUS || subject == Subject.RHOMBUS_DIMENSION)
				map.get("rhombus").add(criterion);

			else if (subject == Subject.SYMM_MIRROR || subject == Subject.SYMM_ROT_60 || subject == Subject.SYMM_ROT_120
					|| subject == Subject.SYMM_ROT_180 || subject == Subject.SYMM_VERTICAL
					|| subject == Subject.SYMM_ROT_120_V || subject == Subject.SYMM_ROT_180_E)

				map.get("symmetries").add(criterion);

			else if (subject == Subject.DIAMETER)
				map.get("diameter").add(criterion);

			else if (subject == Subject.CORONOID)
				map.get("coronoid").add(criterion);

			else if (subject == Subject.CORONOID_2 || subject == Subject.NB_HOLES)
				map.get("coronoid2").add(criterion);

			else if (subject == Subject.CATACONDENSED)
				map.get("catacondensed").add(criterion);

			else if (subject == Subject.SINGLE_PATTERN || subject == Subject.MULTIPLE_PATTERNS
					|| subject == Subject.FORBIDDEN_PATTERN || subject == Subject.OCCURENCE_PATTERN)
				map.get("patterns").add(criterion);

			else if (subject == Subject.TIMEOUT || subject == Subject.NB_SOLUTIONS)
				map.get("stop").add(criterion);
		}

		return map;
	}

	private ArrayList<Molecule> buildMolecules(ResultSolver resultSolver, int beginIndex) {

		int index = beginIndex;

		NbKekuleStructuresCriterion kekuleFilteringCriterion = null;
		GeneratorCriterion kekuleGeneratorCriterion = null;
		boolean concealed = false;

		for (GeneratorCriterion criterion : criterions) {
			if (criterion.getSubject() == Subject.NB_KEKULE_STRUCTURES) {
				kekuleGeneratorCriterion = criterion;
				if (criterion.getOperator() != Operator.MIN && criterion.getOperator() != Operator.MAX) {
					FilteringOperator operator = FilteringOperator.getOperator(criterion.getOperatorString());
					kekuleFilteringCriterion = new NbKekuleStructuresCriterion(operator,
							Double.parseDouble(criterion.getValue()));
				}

				break;
			}

			else if (criterion.getSubject() == Subject.CONCEALED)
				concealed = true;
		}

		ArrayList<Molecule> molecules = new ArrayList<>();

		for (int i = 0; i < resultSolver.size(); i++) {

			Molecule molecule = null;
			ArrayList<Integer> verticesSolution = resultSolver.getVerticesSolution(i);

			try {

				String graphFilename = "tmp.graph";
				String graphCoordFilename = "tmp.graph_coord";

				GraphFileBuilder graphBuilder = new GraphFileBuilder(verticesSolution, graphFilename,
						resultSolver.getCrown(i));

				graphBuilder.buildGraphFile();

				GraphCoordFileBuilder graphCoordBuilder = new GraphCoordFileBuilder(graphFilename, graphCoordFilename);
				graphCoordBuilder.convertInstance();

				molecule = GraphParser.parseUndirectedGraph(graphCoordFilename, null, false);

				File file = new File("tmp.graph");
				file.delete();

				file = new File("tmp.graph_coord");
				file.delete();

				molecule.setVerticesSolutions(verticesSolution);

				String[] lines = resultSolver.getDescriptions().get(i).split("\n");
				StringBuilder b = new StringBuilder();

				b.append("solution_" + (index + 1) + "\n");
				for (int j = 1; j < lines.length; j++)
					b.append(lines[j] + "\n");

				molecule.setDescription(b.toString());

				molecule.setNbCrowns(resultSolver.getNbCrowns().get(i));

				molecules.add(molecule);

				index++;
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		ArrayList<Molecule> filteredMolecules = molecules;

		if (kekuleFilteringCriterion != null) {
			ArrayList<Molecule> kekuleMolecules = new ArrayList<>();

			for (Molecule molecule : molecules) {
				if (kekuleFilteringCriterion.checksCriterion(molecule))
					kekuleMolecules.add(molecule);
			}
			filteredMolecules = kekuleMolecules;
		}

		else if (kekuleGeneratorCriterion != null) {

			ArrayList<Molecule> minMolecules = new ArrayList<>();
			ArrayList<Molecule> maxMolecules = new ArrayList<>();

			double nbMinKekuleStructures = Double.MAX_VALUE;
			double nbMaxKekuleStructures = -1.0;

			for (Molecule molecule : molecules) {
				double nbKekuleStructures = molecule.getNbKekuleStructures();

				if (nbKekuleStructures >= nbMaxKekuleStructures) {
					if (nbKekuleStructures > nbMaxKekuleStructures) {
						maxMolecules.clear();
					}
					maxMolecules.add(molecule);
					nbMaxKekuleStructures = nbKekuleStructures;
				}

				if (nbKekuleStructures <= nbMinKekuleStructures) {
					if (nbKekuleStructures < nbMinKekuleStructures) {
						minMolecules.clear();
					}
					minMolecules.add(molecule);
					nbMinKekuleStructures = nbKekuleStructures;
				}
			}

			if (kekuleGeneratorCriterion.getOperator() == Operator.MIN)
				filteredMolecules = minMolecules;
			else
				filteredMolecules = maxMolecules;
		}

		if (concealed) {
			ConcealedNonKekuleanCriterion concealedCriterion = new ConcealedNonKekuleanCriterion();
			ArrayList<Molecule> concealeds = new ArrayList<>();

			for (Molecule molecule : filteredMolecules) {
				if (concealedCriterion.checksCriterion(molecule))
					concealeds.add(molecule);
			}

			filteredMolecules = concealeds;
		}

		return filteredMolecules;
	}

	@SuppressWarnings("rawtypes")
	private void generateBenzenoids() {

		if (canStartGeneration) {

			criterions = buildCriterions();
			HashMap<String, ArrayList<GeneratorCriterion>> criterionsMap = buildCriterionsMap(criterions);

			application.getBenzenoidCollectionsPane().log("Generating benzenoids", true);
			for (GeneratorCriterion criterion : criterions) {
				application.getBenzenoidCollectionsPane().log(criterion.toString(), false);
			}

			selectedCollectionTab = application.getBenzenoidCollectionsPane().getSelectedPane();

			buttonsBox.getChildren().clear();
			buttonsBox.getChildren().addAll(closeButton, loadIcon, pauseButton, stopButton);

			Iterator it = criterionsMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				System.out.println(pair.getKey() + " = " + pair.getValue().toString());
			}

			try {
				model = ModelBuilder.buildModel(criterions, criterionsMap, fragmentsInformations);
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
					"A criterion limiting the number of solutions (e.g. limiting hexagons/carbons/hydrogens/number of lines and columns) is required");
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

			ResultSolver resultSolver = model.getResultSolver();

			generatedMolecules = buildMolecules(resultSolver, generatedMolecules.size());

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
		checkGenerationTime();
		checkNbMaxSolutions();
	}

	/***
	 * Add time limit if in the settings
	 */
	private void checkGenerationTime() {
		Settings settings = application.getSettings();

		if (settings.getGenerationTime() > 0 && settings.getTimeUnit() != null) {
			ChoiceBoxCriterion choiceBoxCriterion = new ChoiceBoxCriterion(nbCriterions, this);
			choiceBoxesCriterions.add(choiceBoxCriterion);
			hBoxesCriterions.add(new HBoxDefaultCriterion(this, choiceBoxCriterion));

			choiceBoxCriterion.getSelectionModel().select("Time limit");

			HBoxTimeoutCriterion criterionBox = (HBoxTimeoutCriterion) hBoxesCriterions
					.get(choiceBoxCriterion.getIndex());

			criterionBox.setTime(Integer.toString(settings.getGenerationTime()));
			criterionBox.setTimeUnit(settings.getTimeUnit());

			nbCriterions++;

			refresh();
		}
	}

	/***
	 * Add number of solution limit if in the settings
	 */
	private void checkNbMaxSolutions() {
		Settings settings = application.getSettings();
		if (settings.getNbMaxSolutions() > 0) {

			ChoiceBoxCriterion choiceBoxCriterion = new ChoiceBoxCriterion(nbCriterions, this);
			choiceBoxesCriterions.add(choiceBoxCriterion);
			hBoxesCriterions.add(new HBoxDefaultCriterion(this, choiceBoxCriterion));

			choiceBoxCriterion.getSelectionModel().select("Number of solutions");

			HBoxNbSolutionsCriterion criterionBox = (HBoxNbSolutionsCriterion) hBoxesCriterions
					.get(choiceBoxCriterion.getIndex());

			criterionBox.setNbSolutions(Integer.toString(settings.getNbMaxSolutions()));

			nbCriterions++;

			refresh();
		}
	}

	public void stop() {
		stopButton.fire();
	}

	public void refreshGenerationPossibility() {

		ArrayList<GeneratorCriterion> criterions = buildCriterions();
		canStartGeneration = false;

		if (criterions != null) {

			boolean lines = false;
			boolean columns = false;

			for (int i = 0; i < criterions.size(); i++) {

				GeneratorCriterion criterion = criterions.get(i);

				Subject subject = criterion.getSubject();

				if ((subject == Subject.NB_HEXAGONS || subject == Subject.NB_CARBONS || subject == Subject.NB_HYDROGENS
						|| subject == Subject.RHOMBUS_DIMENSION) && criterion.isUpperBound()) {
					canStartGeneration = true;
					break;
				}

				if (subject == Subject.RECT_HEIGHT && criterion.isUpperBound()) {
					lines = true;
					if (lines && columns) {
						canStartGeneration = true;
						break;
					}
				}

				if (subject == Subject.RECT_WIDTH && criterion.isUpperBound()) {
					columns = true;
					if (lines && columns) {
						canStartGeneration = true;
						break;
					}
				}
			}

			buttonsBox.getChildren().remove(warningIcon);

			if (canStartGeneration)
				buttonsBox.getChildren().remove(warningIcon);
			else
				buttonsBox.getChildren().add(warningIcon);

		}
	}

	public ArrayList<HBoxCriterion> getHBoxesCriterions() {
		return hBoxesCriterions;
	}
}
