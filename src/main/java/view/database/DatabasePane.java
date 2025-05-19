package view.database;

import application.BenzenoidApplication;
import benzenoid.Benzenoid;
import database.BenzenoidCriterion;
import database.models.PropertiesEntry;
import http.JSonStringBuilder;
import http.Post;
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
import spectrums.ResultLogFile;
import utils.Utils;
import view.collections.BenzenoidCollectionPane.DisplayType;
import view.collections.BenzenoidCollectionsManagerPane;
import view.database.boxes.HBoxDatabaseCriterion;
import view.database.boxes.HBoxDefaultDatabaseCriterion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabasePane extends ScrollPane {

	private final BenzenoidApplication application;
	private GridPane gridPane;

	private int nbCriterions;
	private ArrayList<ChoiceBoxDatabaseCriterion> choiceBoxesCriterions;
	private ArrayList<HBoxDatabaseCriterion> hBoxesCriterions;

	private Button addButton;
	private Button closeButton;

	private Button findButton;
	private ImageView loadIcon;

	private HBox buttonsBox;

	private ArrayList<Benzenoid> molecules;

	private Label titleLabel;

	public DatabasePane(BenzenoidApplication application) {
		this.application = application;
		initialize();
	}

	private ArrayList<Integer> containsInvalidCriterion() {

		ArrayList<Integer> indexes = new ArrayList<>();

		for (int i = 0; i < hBoxesCriterions.size(); i++) {
			if (!hBoxesCriterions.get(i).isValid())
				indexes.add(i);
		}

		return indexes;
	}

	private void initialize() {

		titleLabel = new Label("Import benzenoids from database");
		titleLabel.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, FontPosture.ITALIC, 15));

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

		addButton.setOnAction(e -> {

			ArrayList<Integer> invalidIndexes = containsInvalidCriterion();

			if (invalidIndexes.size() == 0) {

				ChoiceBoxDatabaseCriterion choiceBoxCriterion = new ChoiceBoxDatabaseCriterion(nbCriterions, this);
				choiceBoxesCriterions.add(choiceBoxCriterion);
				hBoxesCriterions.add(new HBoxDefaultDatabaseCriterion(this, choiceBoxCriterion));

				nbCriterions++;

				System.out.println(nbCriterions + " criterions");

				refresh();

			}

			else {
				Utils.alert("Invalid criterion(s)");
			}
		});

		ImageView imageClose = new ImageView(new Image("/resources/graphics/icon-close.png"));
		closeButton = new Button();
		closeButton.setGraphic(imageClose);
		Tooltip.install(closeButton, new Tooltip("Return to the collection"));
		closeButton.resize(30, 30);
		closeButton.setStyle("-fx-background-color: transparent;");

		closeButton.setOnAction(e -> {
			application.switchMode(application.getPanes().getCollectionsPane());
		});

		ImageView imageGenerate = new ImageView(new Image("/resources/graphics/icon-resume.png"));
		findButton = new Button();
		findButton.setGraphic(imageGenerate);
		Tooltip.install(findButton, new Tooltip("Find benzenoids"));
		findButton.setStyle("-fx-background-color: transparent;");
		findButton.resize(30, 30);

		findButton.setOnAction(e -> {
			findBenzenoids();
		});

		choiceBoxesCriterions = new ArrayList<>();
		hBoxesCriterions = new ArrayList<>();

		ChoiceBoxDatabaseCriterion choiceBoxCriterion = new ChoiceBoxDatabaseCriterion(0, this);

		choiceBoxesCriterions.add(choiceBoxCriterion);
		hBoxesCriterions.add(new HBoxDefaultDatabaseCriterion(this, choiceBoxCriterion));

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

		refresh();
	}

	private void refresh() {

		gridPane.getChildren().clear();

		gridPane.add(titleLabel, 0, 0, 2, 1);

		for (int i = 0; i < nbCriterions; i++) {
			GridPane.setValignment(choiceBoxesCriterions.get(i), VPos.TOP);
			gridPane.add(choiceBoxesCriterions.get(i), 0, i + 1);
			gridPane.add(hBoxesCriterions.get(i), 1, i + 1);
		}

		buttonsBox = new HBox(5.0);
		buttonsBox.getChildren().addAll(closeButton, addButton, findButton);

		gridPane.add(buttonsBox, 0, nbCriterions + 1);
	}

	public ArrayList<BenzenoidCriterion> getCriterions() {
		ArrayList<BenzenoidCriterion> criterions = new ArrayList<>();

		for (HBoxDatabaseCriterion box : hBoxesCriterions)
			criterions.addAll(box.buildCriterions());

		return criterions;
	}

	private void updateGUI() {
		BenzenoidCollectionsManagerPane managerPane = application.getBenzenoidCollectionsPane();
		managerPane.log("-> " + managerPane.getSelectedTab().getName(), false);
		managerPane.getSelectedTab().refresh();
		application.switchMode(application.getPanes().getCollectionsPane());
	}

	@SuppressWarnings("rawtypes")
	private void findBenzenoids() {

		try {

			ArrayList<BenzenoidCriterion> criterions = getCriterions();

			BenzenoidCollectionsManagerPane managerPane = application.getBenzenoidCollectionsPane();

			managerPane.log("Requesting database", true);
			for (BenzenoidCriterion criterion : criterions)
				managerPane.log(criterion.toString(), false);

			String jsonInputString = buildJsonInputString(criterions);
			List<Map> results = Post.post("find_properties/", jsonInputString);

			molecules = new ArrayList<>();
			HashMap<String, ResultLogFile> logsResults = new HashMap<String, ResultLogFile>();

			if (results.size() > 0) {

				application.addTask("Find in database");

				final Service<Void> calculateService = new Service<Void>() {

					@Override
					protected Task<Void> createTask() {
						return new Task<Void>() {

							@Override
							protected Void call() throws Exception {
								int i = 0;
								for (Map map : results) {
									try {

										PropertiesEntry content = PropertiesEntry.buildQueryContent(map);

										Benzenoid molecule = null;

										molecule = content.buildMolecule();
                    					molecule.performCheckDatabase();

										i++;

										ResultLogFile resultLog = content.buildResultLogFile();

										molecules.add(molecule);
										logsResults.put(molecule.toString(), resultLog);

										application.getBenzenoidCollectionsPane().getSelectedTab()
												.addBenzenoid(molecule, DisplayType.BASIC);

									} catch (Exception e) {
										e.printStackTrace();
										System.err.println("Erreur création molécule " + i);
									}
								}

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
							updateGUI();
							application.removeTask("Find in database");
							break;
						case CANCELLED:
							Utils.alert("Cancelled");
							application.removeTask("Find in database");
							break;
						case SUCCEEDED:
							updateGUI();
							application.removeTask("Find in database");
							break;

						default:
							break;
						}

					}

				});

				calculateService.start();
			}

			else {
				Utils.alert("No molecule found");
			}

		} catch (Exception e1) {

			e1.printStackTrace();
		}
	}

	public void setHBox(int index, HBoxDatabaseCriterion hbox) {
		hBoxesCriterions.set(index, hbox);
		refresh();
	}

	public void removeCriterion(ChoiceBoxDatabaseCriterion choiceBoxCriterion, HBoxDatabaseCriterion hBoxCriterion) {

		choiceBoxesCriterions.remove(choiceBoxCriterion);
		hBoxesCriterions.remove(hBoxCriterion);
		nbCriterions--;

		for (int i = 0; i < nbCriterions; i++)
			choiceBoxesCriterions.get(i).setIndex(i);

		refresh();
	}

	private String buildJsonInputString(ArrayList<BenzenoidCriterion> criterions) {

		String id = "";
		String label = "";
		String nbHexagons = "";
		String nbCarbons = "";
		String nbHydrogens = "";
		String irregularity = "";
		String frequency = "";
		String intensity = "";
		String inchi = "";
		String solo = "";
		String duo = "";
		String trio = "";
		String quartet = "";
		String kekule = "";
    String catacondensed = "";
    String coronenoid = "";
    String coronoid = "";
    String symmetry = "";

		String opeId = "";
		String opeLabel = "";
		String opeHexagons = "";
		String opeCarbons = "";
		String opeHydrogens = "";
		String opeIrregularity = "";
		String opeFrequency = "";
		String opeIntensity = "";
		String opeInchi = "";
		String opeSolo = "";
		String opeDuo = "";
		String opeTrio = "";
		String opeQuartet = "";
		String opeKekule = "";
    String opeCatacondensed = "";
    String opeCoronenoid = "";
    String opeCoronoid = "";
    String opeSymmetry = "";
    
		for (BenzenoidCriterion criterion : criterions) {

			String operator = criterion.getOperatorString();
			String value = criterion.getValue();

			switch (criterion.getSubject()) {

			case ID_MOLECULE:
				id = value;
				opeId = operator;
				break;

			case MOLECULE_LABEL:
				label = value;
				opeLabel = operator;
				break;

			case NB_HEXAGONS:
				nbHexagons = value;
				opeHexagons = operator;
				break;

			case NB_CARBONS:
				nbCarbons = value;
				opeCarbons = operator;
				break;

			case NB_HYDROGENS:
				nbHydrogens = value;
				opeHydrogens = operator;
				break;

			case IRREGULARITY:
				irregularity = value;
				opeIrregularity = operator;
				break;

			case FREQUENCY:
				frequency = value;
				opeFrequency = operator;
				break;

			case INTENSITY:
				intensity = value;
				opeIntensity = operator;
				break;

			case INCHI:
				inchi = value;
				opeInchi = operator;
				break;
        
			case SOLO:
				solo = value;
				opeSolo = operator;
				break;
        
			case DUO:
				duo = value;
				opeDuo = operator;
				break;
        
			case TRIO:
				trio = value;
				opeTrio = operator;
				break;
        
			case QUARTET:
				quartet = value;
				opeQuartet = operator;
				break;
        
			case KEKULE:
				kekule = value;
				opeKekule = operator;
				break;        
      
			case CATACONDENSED:
				catacondensed = value;
				opeCatacondensed = operator;
				break;        
      
			case CORONENOID:
				coronenoid = value;
				opeCoronenoid = operator;
				break;        
      
			case CORONOID:
				coronoid = value;
				opeCoronoid = operator;
				break;        
      
			case SYMMETRY:
				symmetry = value;
				opeSymmetry = operator;
				break;        
      
			}

		}

		String json = JSonStringBuilder.buildNewJsonString(id, label, nbHexagons, nbCarbons, nbHydrogens, irregularity, frequency, intensity, inchi, solo, duo, trio, quartet, kekule, 
        catacondensed, coronenoid, coronoid, symmetry,
        opeId, opeLabel, opeHexagons, opeCarbons, opeHydrogens, opeIrregularity, opeFrequency, opeIntensity, opeInchi, opeSolo, opeDuo, opeTrio, opeQuartet, opeKekule,
        opeCatacondensed, opeCoronenoid, opeCoronoid, opeSymmetry);

		System.out.println(json);

		return json;
	}
}
