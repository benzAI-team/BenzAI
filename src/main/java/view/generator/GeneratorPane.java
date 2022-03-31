package view.generator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import application.ApplicationMode;
import application.BenzenoidApplication;
import application.Configuration;
import generator.GeneralModel;
import generator.GeneratorCriterion;
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
import view.generator.boxes.HBoxCriterion;
import view.generator.boxes.HBoxDefaultCriterion;
import view.generator.boxes.HBoxNbCarbonsCriterion;
import view.generator.boxes.HBoxNbHexagonsCriterion;
import view.generator.boxes.HBoxNbHydrogensCriterion;
import view.generator.boxes.HBoxNbSolutionsCriterion;
import view.generator.boxes.HBoxTimeoutCriterion;

public class GeneratorPane extends ScrollPane {

	BenzenoidCollectionPane selectedCollectionTab;

	private ArrayList<GeneralModel> models;
	private GeneralModel curentModel;

	private BenzenoidApplication application;
	private GridPane gridPane;
	private int nbCriterions;
	private ArrayList<ChoiceBoxCriterion> choiceBoxesCriterions;
	private ArrayList<HBoxCriterion> hBoxesCriterions;
	private Button addButton;
	private Button generateButton;
	private Button stopButton;
	private Button pauseButton;
	private Button resumeButton;
	private ImageView loadIcon;

	private boolean isRunning;

	private HBox buttonsBox;

	private Label titleLabel;

	private ImageView warningIcon;

	/*
	 * Solver informations
	 */

	private FragmentResolutionInformations fragmentsInformations;

	public GeneratorPane(BenzenoidApplication application) {
		this.application = application;
		initialize();
	}

	private void initialize() {

		titleLabel = new Label("Generate benzenoids");
		titleLabel.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, FontPosture.ITALIC, 15));

		isRunning = false;

		nbCriterions = 1;

		Image image = new Image("/resources/graphics/icon-load.gif");
		loadIcon = new ImageView(image);
		loadIcon.resize(30, 30);

		ImageView imageAdd = new ImageView(new Image("/resources/graphics/icon-add.png"));

		addButton = new Button();
		addButton.setGraphic(imageAdd);
		Tooltip.install(addButton, new Tooltip("Add new criterion"));
		addButton.resize(30, 30);
		addButton.setStyle("-fx-background-color: transparent;");

		ImageView imageStop = new ImageView(new Image("/resources/graphics/icon-stop.png"));
		stopButton = new Button();
		stopButton.setGraphic(imageStop);
		Tooltip.install(stopButton, new Tooltip("Stop generation"));
		stopButton.resize(30, 30);
		stopButton.setStyle("-fx-background-color: transparent;");

		stopButton.setOnAction(e -> {
			for (GeneralModel model : models) {
				if (model.isPaused())
					resumeGeneration();

				model.getProblem().getSolver().limitSearch(() -> {
					return curentModel.getGeneratorRun().isStopped();
				});

				model.stop();
			}

			buttonsBox.getChildren().clear();
			buttonsBox.getChildren().addAll(addButton, generateButton);
		});

		ImageView imagePause = new ImageView(new Image("/resources/graphics/icon-pause.png"));

		pauseButton = new Button();
		pauseButton.setGraphic(imagePause);
		pauseButton.setStyle("-fx-background-color: transparent;");
		pauseButton.resize(32, 32);
		Tooltip.install(pauseButton, new Tooltip("Pause"));
		pauseButton.setOnAction(e -> {
			curentModel.pause();
			buttonsBox.getChildren().clear();
			buttonsBox.getChildren().addAll(resumeButton, stopButton);
		});

		ImageView imageResume = new ImageView(new Image("/resources/graphics/icon-resume.png"));
		resumeButton = new Button();
		resumeButton.setGraphic(imageResume);
		resumeButton.setStyle("-fx-background-color: transparent;");
		resumeButton.resize(30, 30);
		Tooltip.install(resumeButton, new Tooltip("Resume generation"));
		resumeButton.setOnAction(e -> {
			resumeGeneration();
		});

		addButton.setOnAction(e -> {

			ArrayList<Integer> invalidIndexes = containsInvalidCriterion();

			if (invalidIndexes.size() == 0) {

				ChoiceBoxCriterion choiceBoxCriterion = new ChoiceBoxCriterion(nbCriterions, this);
				choiceBoxesCriterions.add(choiceBoxCriterion);
				hBoxesCriterions.add(new HBoxDefaultCriterion(this, choiceBoxCriterion));

				nbCriterions++;

				System.out.println(nbCriterions + " criterions");

				refresh();

			}

			else {
				Utils.alert("Invalid criterion(s)");
			}
		});

		ImageView imageGenerate = new ImageView(new Image("/resources/graphics/icon-resume.png"));
		generateButton = new Button();
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

		choiceBoxesCriterions = new ArrayList<>();
		hBoxesCriterions = new ArrayList<>();

		ChoiceBoxCriterion choiceBoxCriterion = new ChoiceBoxCriterion(0, this);

		choiceBoxesCriterions.add(choiceBoxCriterion);
		hBoxesCriterions.add(new HBoxDefaultCriterion(this, choiceBoxCriterion));

		this.setFitToHeight(true);
		this.setFitToWidth(true);
		this.setPrefWidth(1400);

		gridPane = new GridPane();

		gridPane.setPrefWidth(1400);

		gridPane.setPadding(new Insets(50));
		gridPane.setHgap(5);
		gridPane.setVgap(5);

		this.setPrefWidth(this.getPrefWidth());

		this.setContent(gridPane);

		warningIcon = new ImageView(new Image("/resources/graphics/icon-warning.png"));
		warningIcon.resize(30, 30);
		Tooltip.install(warningIcon,
				new Tooltip("A criterion limiting the number of hexagons/carbons/hydrogens is required"));

		checkConfiguration();

		refresh();
	}

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
		buttonsBox.getChildren().addAll(addButton, generateButton);

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

		for (HBoxCriterion box : hBoxesCriterions)
			criterions.addAll(box.buildCriterions());

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

			else if (subject == Subject.RECT_NB_LINES || subject == Subject.RECT_NB_COLUMNS)
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

				GraphCoordFileBuilder graphCoordBuilder = new GraphCoordFileBuilder(graphFilename,
						graphCoordFilename);
				graphCoordBuilder.convertInstance();

				molecule = GraphParser.parseUndirectedGraph(graphCoordFilename, null, false);

				File file = new File("tmp.graph");
				file.delete();

				file = new File("tmp.graph_coord");
				file.delete();

				molecule.setVerticesSolutions(verticesSolution);

				String[] lines = resultSolver.getDescriptions().get(i).split("\n");
				StringBuilder b = new StringBuilder();

				b.append("solution_" + index + "\n");
				for (int j = 1; j < lines.length; j++)
					b.append(lines[j] + "\n");

				molecule.setDescription(b.toString());
				
				molecules.add(molecule);
				
				index++;
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		return molecules;
	}
	
	@SuppressWarnings("rawtypes")
	private void generateBenzenoids() {

		ArrayList<GeneratorCriterion> criterions = buildCriterions();
		HashMap<String, ArrayList<GeneratorCriterion>> criterionsMap = buildCriterionsMap(criterions);

		application.getBenzenoidCollectionsPane().log("Generating benzenoids", true);
		for (GeneratorCriterion criterion : criterions) {
			application.getBenzenoidCollectionsPane().log(criterion.toString(), false);
		}

		selectedCollectionTab = application.getBenzenoidCollectionsPane().getSelectedPane();

		buttonsBox.getChildren().clear();
		buttonsBox.getChildren().addAll(loadIcon, pauseButton, stopButton);

		Iterator it = criterionsMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			System.out.println(pair.getKey() + " = " + pair.getValue().toString());
		}

		try {
			models = ModelBuilder.buildModel(criterions, criterionsMap, fragmentsInformations);
		} catch (Exception e) {
			e.printStackTrace();
		}
		isRunning = true;

		application.addTask("Benzenoid generation");

		ArrayList<Molecule> generatedMolecules = new ArrayList<>();
		
		final Service<Void> calculateService = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {

					@Override
					protected Void call() throws Exception {
						
						for (GeneralModel model : models) {
							curentModel = model;
							
							model.applyNoGoods(generatedMolecules);
							
							curentModel.solve();
							
							generatedMolecules.addAll(buildMolecules(model.getResultSolver(), generatedMolecules.size()));
							
						}

						
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
					if (!curentModel.isPaused()) {
						buttonsBox.getChildren().clear();
						buttonsBox.getChildren().addAll(addButton, generateButton);
						buildBenzenoidPanesThread();
						application.removeTask("Benzenoid generation");
					}

					else {
						buttonsBox.getChildren().clear();
						buttonsBox.getChildren().addAll(resumeButton, stopButton);
					}

					break;

				default:
					break;
				}

			}

		});

		calculateService.start();

	}

	private void resumeGeneration() {

		buttonsBox.getChildren().clear();
		buttonsBox.getChildren().addAll(loadIcon, pauseButton, stopButton);

		final Service<Void> calculateService = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {

					@Override
					protected Void call() throws Exception {
						curentModel.getGeneratorRun().resume();
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

					if (curentModel.isPaused()) {
						buttonsBox.getChildren().clear();
						buttonsBox.getChildren().addAll(resumeButton, stopButton);
					}

					else {
						if (!curentModel.isPaused()) {
							buttonsBox.getChildren().clear();
							buttonsBox.getChildren().addAll(addButton, generateButton);
							// buildBenzenoidPanesThread();
							buildBenzenoidPanes();
						}
					}

					// buildBenzenoidPanesThread();

					break;

				default:
					break;
				}

			}

		});

		calculateService.start();

	}

	private void buildBenzenoidPanesThread() {

		int size = 0;

		for (GeneralModel model : models) {
			size += model.getResultSolver().size();
		}

		if (size > 0) {

			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					buildBenzenoidPanes();
					application.switchMode(ApplicationMode.COLLECTIONS);
				}
			});

		}

		else {
			Utils.alert("Generation: No solution found");
		}
	}

	private void buildBenzenoidPanes() {
		if (!curentModel.isPaused()) {

			isRunning = false;

			ResultSolver resultSolver = new ResultSolver();

			for (GeneralModel model : models) {
				resultSolver.addResult(model.getResultSolver());
			}

			application.getBenzenoidCollectionsPane().log("-> " + selectedCollectionTab.getName(), false);
			application.getBenzenoidCollectionsPane().log("", false);

			int index = 1;

			if (curentModel.getResultSolver().size() > 0) {
				for (int i = 0; i < resultSolver.size(); i++) {

					Molecule molecule = null;
					ArrayList<Integer> verticesSolution = resultSolver.getVerticesSolution(i);

					try {

						String graphFilename = "tmp.graph";
						String graphCoordFilename = "tmp.graph_coord";

						GraphFileBuilder graphBuilder = new GraphFileBuilder(verticesSolution, graphFilename,
								resultSolver.getCrown(i));

						graphBuilder.buildGraphFile();

						GraphCoordFileBuilder graphCoordBuilder = new GraphCoordFileBuilder(graphFilename,
								graphCoordFilename);
						graphCoordBuilder.convertInstance();

						molecule = GraphParser.parseUndirectedGraph(graphCoordFilename, null, false);

						File file = new File("tmp.graph");
						file.delete();

						file = new File("tmp.graph_coord");
						file.delete();

						molecule.setVerticesSolutions(verticesSolution);

						String[] lines = resultSolver.getDescriptions().get(i).split("\n");
						StringBuilder b = new StringBuilder();

						b.append("solution_" + index + "\n");
						for (int j = 1; j < lines.length; j++)
							b.append(lines[j] + "\n");

						molecule.setDescription(/* resultSolver.getDescriptions().get(i) */ b.toString());
						index++;

						selectedCollectionTab.addBenzenoid(molecule, DisplayType.BASIC);

					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}

				selectedCollectionTab.refresh();

				buttonsBox.getChildren().clear();
				buttonsBox.getChildren().addAll(addButton, generateButton);
				application.switchMode(ApplicationMode.COLLECTIONS);
			}

			else {
				isRunning = false;
				Utils.alert("No benzenoid found");
			}
		}
	}

	private void checkConfiguration() {
		Configuration configuration = application.getConfiguration();

		if (configuration.getGenerationTime() > 0 && configuration.getTimeUnit() != null) {
			ChoiceBoxCriterion choiceBoxCriterion = new ChoiceBoxCriterion(nbCriterions, this);
			choiceBoxesCriterions.add(choiceBoxCriterion);
			hBoxesCriterions.add(new HBoxDefaultCriterion(this, choiceBoxCriterion));

			choiceBoxCriterion.getSelectionModel().select("Time limit");

			HBoxTimeoutCriterion criterionBox = (HBoxTimeoutCriterion) hBoxesCriterions
					.get(choiceBoxCriterion.getIndex());

			criterionBox.setTime(Integer.toString(configuration.getGenerationTime()));
			criterionBox.setTimeUnit(configuration.getTimeUnit());

			nbCriterions++;

			refresh();
		}

		if (configuration.getNbMaxSolutions() > 0) {

			ChoiceBoxCriterion choiceBoxCriterion = new ChoiceBoxCriterion(nbCriterions, this);
			choiceBoxesCriterions.add(choiceBoxCriterion);
			hBoxesCriterions.add(new HBoxDefaultCriterion(this, choiceBoxCriterion));

			choiceBoxCriterion.getSelectionModel().select("Number of solutions");

			HBoxNbSolutionsCriterion criterionBox = (HBoxNbSolutionsCriterion) hBoxesCriterions
					.get(choiceBoxCriterion.getIndex());

			criterionBox.setNbSolutions(Integer.toString(configuration.getNbMaxSolutions()));

			nbCriterions++;

			refresh();
		}
	}

	public void stop() {
		stopButton.fire();
	}

	public void refreshValidity() {

		boolean valid1 = false;

		for (int i = 0; i < nbCriterions; i++) {

			HBoxCriterion box = hBoxesCriterions.get(i);

			if ((box instanceof HBoxNbHexagonsCriterion || box instanceof HBoxNbCarbonsCriterion
					|| box instanceof HBoxNbHydrogensCriterion)) {

				valid1 = true;
			}
		}

		if (valid1)
			buttonsBox.getChildren().remove(warningIcon);
		else
			buttonsBox.getChildren().add(warningIcon);
	}

	public ArrayList<HBoxCriterion> getHBoxesCriterions() {
		return hBoxesCriterions;
	}
}
