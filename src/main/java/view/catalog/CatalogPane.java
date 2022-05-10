package view.catalog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.BenzenoidApplication;
import classifier.CarbonHydrogenClassifier;
import classifier.Classifier;
import classifier.Irregularity;
import classifier.IrregularityClassifier;
import classifier.MoleculeInformation;
import classifier.PAHClass;
import database.BenzenoidCriterion;
import database.BenzenoidCriterion.Subject;
import database.models.IRSpectraEntry;
import http.JSonStringBuilder;
import http.Post;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import molecules.Molecule;
import spectrums.Parameter;
import spectrums.ResultLogFile;
import spectrums.ResultSpectrums;
import spectrums.SpectrumsComputer;
import utils.Utils;
import view.groups.MoleculeGroup;

public class CatalogPane extends GridPane {

	private BenzenoidApplication app;

	private ArrayList<BenzenoidCriterion> criterions;

	private ListView<GridPane> listView;
	private ArrayList<GridPane> boxItems;

	private FlowPane flowPane;

	private ScrollPane scrollPane = new ScrollPane();

	ArrayList<Molecule> molecules;

	HashMap<String, ResultLogFile> logsResults;

	ArrayList<PAHClass> classes;

	private Parameter parameter;

	public CatalogPane(BenzenoidApplication app) {
		super();
		this.app = app;
		initialize();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initialize() {

		parameter = buildDefaultParameter();

		scrollPane.setFitToHeight(true);
		scrollPane.setFitToWidth(true);

		this.setPrefWidth(1400);

		this.setPadding(new Insets(20));
		this.setHgap(25);
		this.setVgap(15);

		this.setPrefWidth(this.getPrefWidth());

		criterions = new ArrayList<>();
		boxItems = new ArrayList<>();

		/*
		 * id_molecule
		 */

		Label idMoleculeLabel = new Label("id_molecule");

		ChoiceBox idMoleculeChoiceBox = new ChoiceBox();
		idMoleculeChoiceBox.getItems().add("<=");
		idMoleculeChoiceBox.getItems().add("<");
		idMoleculeChoiceBox.getItems().add("=");
		idMoleculeChoiceBox.getItems().add(">");
		idMoleculeChoiceBox.getItems().add(">=");
		idMoleculeChoiceBox.getItems().add("!=");

		TextField idMoleculeField = new TextField();
		Button idMoleculeAddButton = new Button("+");

		this.add(idMoleculeLabel, 0, 0);
		this.add(idMoleculeChoiceBox, 1, 0);
		this.add(idMoleculeField, 2, 0);
		this.add(idMoleculeAddButton, 3, 0);

		GridPane.setFillWidth(idMoleculeChoiceBox, true);
		GridPane.setFillHeight(idMoleculeChoiceBox, true);

		idMoleculeAddButton.setOnAction(e -> {

			if (idMoleculeChoiceBox.getValue() != null && Utils.isNumber(idMoleculeField.getText())) {
				BenzenoidCriterion criterion = new BenzenoidCriterion(Subject.ID_MOLECULE,
						BenzenoidCriterion.getOperator(idMoleculeChoiceBox.getValue().toString()),
						idMoleculeField.getText());
				addEntry(criterion);
			}

			else {
				Utils.alert("Invalid fields");
			}
		});

		/*
		 * molecule_name
		 */

		Label moleculeNameLabel = new Label("molecule_name");
		TextField moleculeNameField = new TextField();
		Button moleculeNameAddButton = new Button("+");

		ChoiceBox moleculeNameChoiceBox = new ChoiceBox();
		moleculeNameChoiceBox.getItems().add("=");
		moleculeNameChoiceBox.getItems().add("!=");

		this.add(moleculeNameLabel, 0, 1);
		this.add(moleculeNameChoiceBox, 1, 1);
		this.add(moleculeNameField, 2, 1);
		this.add(moleculeNameAddButton, 3, 1);

		moleculeNameAddButton.setOnAction(e -> {

			if (!moleculeNameField.getText().equals("")) {

				BenzenoidCriterion criterion = new BenzenoidCriterion(Subject.MOLECULE_NAME,
						BenzenoidCriterion.getOperator(moleculeNameChoiceBox.getValue().toString()),
						moleculeNameField.getText());
				addEntry(criterion);
			}

			else
				Utils.alert("Invalid fields");

		});

		/*
		 * nb_hexagons
		 */

		Label nbHexagonsLabel = new Label("nb_hexagons");

		ChoiceBox nbHexagonsChoiceBox = new ChoiceBox();
		nbHexagonsChoiceBox.getItems().add("<=");
		nbHexagonsChoiceBox.getItems().add("<");
		nbHexagonsChoiceBox.getItems().add("=");
		nbHexagonsChoiceBox.getItems().add(">");
		nbHexagonsChoiceBox.getItems().add(">=");
		nbHexagonsChoiceBox.getItems().add("!=");

		TextField nbHexagonsField = new TextField();
		Button nbHexagonsAddButton = new Button("+");

		this.add(nbHexagonsLabel, 0, 2);
		this.add(nbHexagonsChoiceBox, 1, 2);
		this.add(nbHexagonsField, 2, 2);
		this.add(nbHexagonsAddButton, 3, 2);

		nbHexagonsAddButton.setOnAction(e -> {

			if (nbHexagonsChoiceBox.getValue() != null && Utils.isNumber(nbHexagonsField.getText())) {
				BenzenoidCriterion criterion = new BenzenoidCriterion(Subject.NB_HEXAGONS,
						BenzenoidCriterion.getOperator(nbHexagonsChoiceBox.getValue().toString()),
						nbHexagonsField.getText());
				addEntry(criterion);
			}

			else {
				Utils.alert("Invalid fields");
			}

		});

		/*
		 * nb_carbons
		 */

		Label nbCarbonsLabel = new Label("nb_carbons");

		ChoiceBox nbCarbonsChoiceBox = new ChoiceBox();
		nbCarbonsChoiceBox.getItems().add("<=");
		nbCarbonsChoiceBox.getItems().add("<");
		nbCarbonsChoiceBox.getItems().add("=");
		nbCarbonsChoiceBox.getItems().add(">");
		nbCarbonsChoiceBox.getItems().add(">=");
		nbCarbonsChoiceBox.getItems().add("!=");

		TextField nbCarbonsField = new TextField();
		Button nbCarbonsAddButton = new Button("+");

		this.add(nbCarbonsLabel, 0, 3);
		this.add(nbCarbonsChoiceBox, 1, 3);
		this.add(nbCarbonsField, 2, 3);
		this.add(nbCarbonsAddButton, 3, 3);

		nbCarbonsAddButton.setOnAction(e -> {
			if (nbCarbonsChoiceBox.getValue() != null && Utils.isNumber(nbCarbonsField.getText())) {
				BenzenoidCriterion criterion = new BenzenoidCriterion(Subject.NB_CARBONS,
						BenzenoidCriterion.getOperator(nbCarbonsChoiceBox.getValue().toString()),
						nbCarbonsField.getText());
				addEntry(criterion);
			}

			else {
				Utils.alert("Invalid fields");
			}
		});

		/*
		 * nb_hydrogens
		 */

		Label nbHydrogensLabel = new Label("nb_hydrogens");

		ChoiceBox nbHydrogensChoiceBox = new ChoiceBox();
		nbHydrogensChoiceBox.getItems().add("<=");
		nbHydrogensChoiceBox.getItems().add("<");
		nbHydrogensChoiceBox.getItems().add("=");
		nbHydrogensChoiceBox.getItems().add(">");
		nbHydrogensChoiceBox.getItems().add(">=");
		nbHydrogensChoiceBox.getItems().add("!=");

		TextField nbHydrogensField = new TextField();
		Button nbHydrogensAddButton = new Button("+");

		this.add(nbHydrogensLabel, 0, 4);
		this.add(nbHydrogensChoiceBox, 1, 4);
		this.add(nbHydrogensField, 2, 4);
		this.add(nbHydrogensAddButton, 3, 4);

		nbHydrogensAddButton.setOnAction(e -> {
			if (nbHydrogensChoiceBox.getValue() != null && Utils.isNumber(nbHydrogensField.getText())) {
				BenzenoidCriterion criterion = new BenzenoidCriterion(Subject.NB_HYDROGENS,
						BenzenoidCriterion.getOperator(nbHydrogensChoiceBox.getValue().toString()),
						nbHydrogensField.getText());
				addEntry(criterion);
			}

			else {
				Utils.alert("Invalid fields");
			}
		});

		/*
		 * irregularity
		 */

		Label irregularityLabel = new Label("irregularity");

		ChoiceBox irregularityChoiceBox = new ChoiceBox();
		irregularityChoiceBox.getItems().add("<=");
		irregularityChoiceBox.getItems().add("<");
		irregularityChoiceBox.getItems().add("=");
		irregularityChoiceBox.getItems().add(">");
		irregularityChoiceBox.getItems().add(">=");
		irregularityChoiceBox.getItems().add("!=");

		TextField irregularityField = new TextField();
		Button irregularityAddButton = new Button("+");

		this.add(irregularityLabel, 0, 5);
		this.add(irregularityChoiceBox, 1, 5);
		this.add(irregularityField, 2, 5);
		this.add(irregularityAddButton, 3, 5);

		irregularityAddButton.setOnAction(e -> {
			if (irregularityChoiceBox.getValue() != null && Utils.isNumber(irregularityField.getText())) {
				BenzenoidCriterion criterion = new BenzenoidCriterion(Subject.IRREGULARITY,
						BenzenoidCriterion.getOperator(irregularityChoiceBox.getValue().toString()),
						irregularityField.getText());
				addEntry(criterion);
			}

			else {
				Utils.alert("Invalid fields");
			}
		});

		/*
		 * ListView
		 */

		listView = new ListView<GridPane>();
		this.add(listView, 4, 0, 1, 6);

		/*
		 * Clear button
		 */

		Button clearButton = new Button("Clear criterions");

		this.add(clearButton, 5, 0);

		clearButton.setOnAction(e -> {

			boxItems.clear();
			ObservableList<GridPane> items = FXCollections.observableArrayList(boxItems);
			listView.setItems(items);

			criterions.clear();

			for (int i = 0; i < boxItems.size(); i++) {
				boxItems.get(i).getChildren().remove(1);
				boxItems.get(i).add(new DeleteButton(this, i), 1, 0);
			}
		});

		clearButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		GridPane.setFillWidth(clearButton, true);

		/*
		 * Parameter button
		 */

		Button parameterButton = new Button("Set parameters");

		this.add(parameterButton, 5, 1);

		parameterButton.setOnAction(e -> {
			Region parameterPane = new ParameterPane(this);
			Stage stage = new Stage();
			stage.setTitle("Set parameters");

			Scene scene = new Scene(parameterPane, 573, 535);
			scene.getStylesheets().add("/resources/style/application.css");

			stage.setScene(scene);
			stage.show();
		});

		parameterButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		GridPane.setFillWidth(parameterButton, true);

		/*
		 * Find button
		 */

		Button findButton = new Button("Find");

		findButton.setOnAction(e -> {

			try {

				String jsonInputString = buildJsonInputString();

				// List<Map> results = Post.post("http://localhost:8080/benzenoids/find_ir/",
				// jsonInputString);
				List<Map> results = Post.post("https://benzenoids.lis-lab.fr/find_ir/", jsonInputString);

				flowPane = new FlowPane();

				flowPane.getChildren().clear();

				flowPane.setHgap(20);
				flowPane.setVgap(20);
				flowPane.setPadding(new Insets(10));

				molecules = new ArrayList<>();
				logsResults = new HashMap<String, ResultLogFile>();

				if (results.size() > 0) {

					for (Map map : results) {

						IRSpectraEntry content = IRSpectraEntry.buildQueryContent(map);

						Molecule molecule = content.buildMolecule();
						ResultLogFile resultLog = content.buildResultLogFile();

						molecules.add(molecule);
						logsResults.put(molecule.toString(), resultLog);

						MoleculeGroup benzenoidDraw = new MoleculeGroup(molecule);

						String description = molecule.toString() + "(" + content.getIdMolecule() + ")\n"
								+ content.getNbHexagons() + " hexagons\n" + content.getNbCarbons() + " carbons\n"
								+ content.getNbHydrogens() + " hydrogens\n" + "XI = " + content.getIrregularity();

						BenzenoidPane benzenoidPane = new BenzenoidPane(this, -1, null, benzenoidDraw, description,
								molecule.getVerticesSolutions());

						flowPane.getChildren().add(benzenoidPane);

						System.out.print("");
					}

					scrollPane.setContent(flowPane);

				}

				else {
					Utils.alert("No molecule found");
				}

			} catch (Exception e1) {

				e1.printStackTrace();
			}

		});

		this.add(findButton, 5, 2);

		findButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		GridPane.setFillWidth(findButton, true);

		/*
		 * Classifier
		 */

		ChoiceBox classifierChoiceBox = new ChoiceBox();
		classifierChoiceBox.getItems().add("Nb carbons/hydrogens");
		classifierChoiceBox.getItems().add("Irregularity");

		Label classifierLabel = new Label("Classify by : ");
		Button classifyButton = new Button("Classify");

		Label stepLabel = new Label("step : ");
		TextField stepField = new TextField();

		HBox classifierBox = new HBox();
		classifierBox.setSpacing(5.0);
		classifierBox.getChildren().addAll(classifierLabel, classifierChoiceBox, /* classifyButton, */ stepLabel,
				stepField);

		this.add(classifierBox, 5, 3);

		stepField.setVisible(false);
		stepLabel.setVisible(false);

		classifierChoiceBox.setOnAction(e -> {
			if (classifierChoiceBox.getValue().toString().equals("Irregularity")) {
				stepField.setVisible(true);
				stepLabel.setVisible(true);
			}

			else {
				stepField.setVisible(false);
				stepLabel.setVisible(false);
			}
		});

		/*
		 * Spectras
		 */

		Button IRButton = new Button("Compute IR spectras");
		Button UVButton = new Button("Compute UV spectras");

		this.add(IRButton, 5, 4);
		this.add(UVButton, 5, 5);

		IRButton.setOnAction(e -> {

			if (molecules != null) {

				if (parameter != null) {

					HashMap<String, MoleculeInformation> moleculesInformations = new HashMap<String, MoleculeInformation>();

					for (Molecule molecule : molecules) {
						MoleculeInformation information = new MoleculeInformation(molecule.toString(), molecule);
						moleculesInformations.put(molecule.toString(), information);
					}

					Classifier classifier = null;

					boolean ok = true;

					if (classifierChoiceBox.getValue() != null) {

						String choice = classifierChoiceBox.getValue().toString();

						if (choice.equals("Irregularity")) {

							try {
								double step = Double.parseDouble(stepField.getText());
								classifier = new IrregularityClassifier(moleculesInformations, step);
							} catch (NumberFormatException exception) {
								ok = false;
								System.out.print("Bad format : step");
							} catch (IOException e1) {
								ok = false;
								e1.printStackTrace();
							}

						}

						else {

							try {
								classifier = new CarbonHydrogenClassifier(moleculesInformations);
							} catch (IOException e1) {
								ok = false;
								e1.printStackTrace();
							}

						}
					}

					System.out.println("");

					if (ok) {

						classes = classifier.classify();

						for (PAHClass PAHClass : classes) {
							System.out.println(PAHClass.getTitle());
							for (String name : PAHClass.getMoleculesNames())
								System.out.println(name);
							System.out.println("");
						}

						ArrayList<ResultSpectrums> results = new ArrayList<ResultSpectrums>();

						for (PAHClass PAHClass : classes) {
							System.out.println("treating " + PAHClass.getTitle());

							ArrayList<ResultLogFile> classResults = new ArrayList<>();
							HashMap<String, Double> finalEnergies = new HashMap<>();

							for (String name : PAHClass.getMoleculesNames()) {

								ResultLogFile result = logsResults.get(name);

								classResults.add(result);

								finalEnergies.put(name,
										result.getFinalEnergy().get(result.getFinalEnergy().size() - 1));
							}

							HashMap<String, Double> irregularities = new HashMap<>();

							HashMap<String, MoleculeInformation> info = PAHClass.getMoleculesInformations();

							for (Map.Entry<String, MoleculeInformation> entry : info.entrySet()) {
								String key = entry.getKey();
								MoleculeInformation value = entry.getValue();

								Molecule molecule = value.getMolecule();

								Irregularity irregularity = IrregularityClassifier
										.computeParameterOfIrregularity(molecule);

								if (irregularity != null)
									irregularities.put(key, irregularity.getXI());
								else
									irregularities.put(key, -1.0);
							}

							try {
								ResultSpectrums resultSpectrum = SpectrumsComputer.treatClass(PAHClass, parameter,
										classResults);

								resultSpectrum.setFinalEnergies(finalEnergies);
								resultSpectrum.setIrregularities(irregularities);

								results.add(resultSpectrum);
								// results.add(SpectrumsComputer.treatClass(PAHClass, parameter, classResults));
							} catch (IOException e1) {
								e1.printStackTrace();
							}

							System.out.println("");

						}

						ArrayList<ComputedPlotPane> panes = new ArrayList<ComputedPlotPane>();

						for (ResultSpectrums result : results)
							panes.add(new ComputedPlotPane(result));

						Region plotPane = new DisplayEnergiesPlotPane(panes, this, parameter);
						Stage stage = new Stage();
						stage.setTitle("Intensities");

						Scene scene = new Scene(plotPane, 823, 515);
						scene.getStylesheets().add("/resources/style/application.css");

						stage.setScene(scene);
						stage.show();

					}

					else {
						Utils.alert("Bad parameter");
					}
				}

				else {
					Utils.alert("Parameters empty.");
				}
			}

			else {
				Utils.alert("No entry");
			}
		});

		IRButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		GridPane.setFillWidth(IRButton, true);

		UVButton.setMaxSize(Double.MAX_VALUE, 32.0);
		GridPane.setFillWidth(UVButton, true);

		this.add(scrollPane, 0, 7, 6, 1);
	}

	public void addEntry(BenzenoidCriterion criterion) {

		Label label = new Label(criterion.toString());
		DeleteButton button = new DeleteButton(this, criterions.size());

		criterions.add(criterion);

		GridPane pane = new GridPane();
		pane.setPadding(new Insets(1));

		pane.add(label, 0, 0);
		label.setAlignment(Pos.BASELINE_CENTER);

		pane.add(button, 1, 0);
		button.setAlignment(Pos.BASELINE_RIGHT);

		boxItems.add(pane);
		ObservableList<GridPane> items = FXCollections.observableArrayList(boxItems);
		listView.setItems(items);
	}

	private String buildJsonInputString() {

		Long id = -1L;
		String name = "none";
		int nbHexagons = 0;
		int nbCarbons = 0;
		int nbHydrogens = 0;
		double irregularity = -1.0;

		String opeId = "";
		String opeName = "";
		String opeHexagons = "";
		String opeCarbons = "";
		String opeHydrogens = "";
		String opeIrregularity = "";

		for (BenzenoidCriterion criterion : criterions) {

			String operator = criterion.getOperatorStringURL();
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
				nbHexagons = Integer.parseInt(value);
				opeHexagons = operator;
				break;

			case NB_CARBONS:
				nbCarbons = Integer.parseInt(value);
				opeCarbons = operator;
				break;

			case NB_HYDROGENS:
				nbHydrogens = Integer.parseInt(value);
				opeHydrogens = operator;
				break;

			case IRREGULARITY:
				irregularity = Double.parseDouble(value);
				opeIrregularity = operator;
				break;
			}

		}

		return JSonStringBuilder.buildJsonString(id, name, nbHexagons, nbCarbons, nbHydrogens, irregularity, opeId,
				opeName, opeHexagons, opeCarbons, opeHydrogens, opeIrregularity);
	}

	/*
	 * VMin = 600 VMax = 1700 step = 1 FWHM = 30 ZT = 1.0 gi = 1.0 N = 100.0 T =
	 * 100.0 kb = 1.380649 -23
	 */

	private Parameter buildDefaultParameter() {

		Parameter parameter = new Parameter();

		parameter.setVMin(600);
		parameter.setVMax(1700);
		parameter.setStep(1);
		parameter.setFWHM(30);
		parameter.setZT(1.0);
		parameter.setGi(1.0);
		parameter.setN(100.0);
		parameter.setT(100.0);

		double factor = 1.380649;
		int exponent = -23;

		parameter.setKb(factor * ((double) Math.pow(10, exponent)));

		return parameter;
	}

	public void removeEntry(int index) {

		boxItems.remove(index);
		ObservableList<GridPane> items = FXCollections.observableArrayList(boxItems);
		listView.setItems(items);

		criterions.remove(index);

		for (int i = 0; i < boxItems.size(); i++) {
			boxItems.get(i).getChildren().remove(1);
			boxItems.get(i).add(new DeleteButton(this, i), 1, 0);
		}
	}

	public BenzenoidApplication getApp() {
		return app;
	}

	public void setParameter(Parameter parameter) {
		this.parameter = parameter;
	}
}
