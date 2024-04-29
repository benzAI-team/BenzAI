package view.database;

import application.BenzenoidApplication;
import application.Operation;
import benzenoid.Benzenoid;
import database.BenzenoidCriterion;
import database.models.BenzenoidEntry;
import http.JSonStringBuilder;
import http.Post;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import properties.database.DatabasePropertySet;
import spectrums.ResultLogFile;
import utils.Utils;
import view.collections.BenzenoidCollectionPane.DisplayType;
import view.collections.BenzenoidCollectionsManagerPane;
import view.database.boxes.HBoxDatabaseCriterion;
import view.primaryStage.ButtonBox;
import view.primaryStage.ScrollPaneWithPropertyList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabasePane extends ScrollPaneWithPropertyList {

	private final BenzenoidApplication application;// TODO supprimer : existe dans super
	private GridPane gridPane;

	private int nbCriterions;
	private ArrayList<ChoiceBoxDatabaseCriterion> choiceBoxesCriterions;
	private ArrayList<HBoxDatabaseCriterion> hBoxesCriterions;

	private ArrayList<Benzenoid> molecules;

	private Label titleLabel;

	public DatabasePane(BenzenoidApplication application) {
		super(new DatabasePropertySet(), new Operation() {
			@Override
			public void run(ScrollPaneWithPropertyList pane) {
				((DatabasePane)pane).findBenzenoids();
			}

			@Override
			public void stop(ScrollPaneWithPropertyList pane) {
				((DatabasePane)pane).stop();
			}
		},
				application);
		this.application = application;
		initialize();
	}

	public boolean containsInvalidCriterion() {

		ArrayList<Integer> indexes = new ArrayList<>();

		for (int i = 0; i < hBoxesCriterions.size(); i++) {
			if (!hBoxesCriterions.get(i).isValid())
				indexes.add(i);
		}
		return indexes.size() > 0;
	}

	private void initialize() {
		titleLabel = new Label("Benzenoids properties");
		titleLabel.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, FontPosture.ITALIC, 15));
		setPaneDimensions();

		buildIcons();
		setButtonBox(new ButtonBox("generate", this));

		gridPane = buildGridPane();
		this.setContent(gridPane);

		initializeCriterionBoxes();
		placeComponents();

//		choiceBoxesCriterions = new ArrayList<>();
//		hBoxesCriterions = new ArrayList<>();
//
//		ChoiceBoxDatabaseCriterion choiceBoxCriterion = new ChoiceBoxDatabaseCriterion(0, this);
//
//		choiceBoxesCriterions.add(choiceBoxCriterion);
//		hBoxesCriterions.add(new HBoxDefaultDatabaseCriterion(this, choiceBoxCriterion));
//
//
//		refresh();
	}

	private void refresh() {
		gridPane.getChildren().clear();
		gridPane.add(titleLabel, 0, 0, 2, 1);
		for (int i = 0; i < nbCriterions; i++) {
			GridPane.setValignment(choiceBoxesCriterions.get(i), VPos.TOP);
			gridPane.add(choiceBoxesCriterions.get(i), 0, i + 1);
			gridPane.add(hBoxesCriterions.get(i), 1, i + 1);
		}
		setButtonBox(new ButtonBox("database", this));
		gridPane.add(getButtonBox(), 0, nbCriterions + 1);
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
			List<Map> results = Post.post("find_benzenoids/", jsonInputString);
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
										BenzenoidEntry content = BenzenoidEntry.buildQueryContent(map);
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
		Long id = -1L;
		String name = "none";
		String nbHexagons = "";
		String nbCarbons = "";
		String nbHydrogens = "";
		String irregularity = "";
		String frequency = "";
		String intensity = "";
		String opeId = "";
		String opeName = "";
		String opeHexagons = "";
		String opeCarbons = "";
		String opeHydrogens = "";
		String opeIrregularity = "";
		String opeFrequency = "";
		String opeIntensity = "";

		for (BenzenoidCriterion criterion : criterions) {
			String operator = criterion.getOperatorString();
			String value = criterion.getValue();

			switch (criterion.getSubject()) {
			case ID_MOLECULE:
				id = Long.parseLong(value);
				opeId = operator;
				break;
			case MOLECULE_NAME:
				name = value;
				opeName = operator;
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
			}

		}

		String json = JSonStringBuilder.buildNewJsonString(id, name, nbHexagons, nbCarbons, nbHydrogens, irregularity,
				frequency, intensity, opeId, opeName, opeHexagons, opeCarbons, opeHydrogens, opeIrregularity,
				opeFrequency, opeIntensity);

		System.out.println(json);
		return json;
	}

	@Override
	protected void placeComponents() {
		gridPane.getChildren().clear();
		gridPane.add(titleLabel, 0, 0, 2, 1);
		placeDatabasePropertyComponents();
		initEventHandlers();
		refreshGlobalValidity();
	}

	private void placeDatabasePropertyComponents() {
		placeCriterionBoxes();
		gridPane.add(getButtonBox(), 0, getNbBoxCriterions() + 1);
	}

	@Override
	public void refreshGlobalValidity() {
		boolean canStartSearch = getHBoxCriterions().stream().allMatch(box -> box.isValid());
		getButtonBox().getChildren().remove(getWarningIcon());
		if (!canStartSearch)
			getButtonBox().getChildren().add(getWarningIcon());
	}

	public void stop() {
	}
}
