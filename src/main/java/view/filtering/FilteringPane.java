package view.filtering;

import java.util.ArrayList;

import application.BenzenoidApplication;
import generator.GeneralModel;
import generator.patterns.PatternResolutionInformations;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
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
import modelProperty.ModelPropertySet;
import modelProperty.expression.BinaryNumericalExpression;
import molecules.Molecule;
import utils.Utils;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionPane.DisplayType;
import view.collections.BenzenoidsCollectionsManagerPane;
import view.filtering.boxes.HBoxDefaultFilteringCriterion;
import view.filtering.boxes.HBoxFilteringCriterion;
import view.filtering.criterions.FilteringCriterion;
import view.generator.ChoiceBoxCriterion;
import view.generator.boxes.HBoxCriterion;
import view.generator.boxes.HBoxDefaultCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;
import modelProperty.testers.Tester;

public class FilteringPane extends ScrollPaneWithPropertyList {

	private BenzenoidApplication application;
	private BenzenoidsCollectionsManagerPane collectionsPane;

	private Button addButton;
	private Button closeButton;
	private Button filterButton;

	private GridPane gridPane;

	private ChoiceBox<String> collectionChoiceBox;

	private PatternResolutionInformations patternsInformations;

	private Label titleLabel;

	private int lineConsole;
	private int indexFiltering;
	
	

	public FilteringPane(BenzenoidApplication application, BenzenoidsCollectionsManagerPane collectionsPane) {
		this.application = application;
		this.collectionsPane = collectionsPane;
		initialize();

	}

	private void initialize() {

		titleLabel = new Label("Filter a collection");
		titleLabel.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, FontPosture.ITALIC, 15));


		buildButtons();
		initializeGridPane();

		this.setPrefWidth(this.getPrefWidth());
		this.setContent(gridPane);

		setChoiceBoxesCriterions(new ArrayList<>());
		setHBoxesCriterions(new ArrayList<>());
		ChoiceBoxCriterion choiceBoxCriterion = new ChoiceBoxCriterion(0, this, getModelPropertySet());
		Tooltip.install(addButton, new Tooltip("Add new criterion"));
		getChoiceBoxesCriterions().add(choiceBoxCriterion);
		getHBoxesCriterions().add(new HBoxDefaultCriterion(this, choiceBoxCriterion));

		placeComponents();
	}

	private void buildButtons() {
		buildAddButton();
		buildCloseButton();
		buildFilterButton();
	}

	private void buildFilterButton() {
		filterButton = new Button("Filter");

		filterButton.setOnAction(e -> {

			ArrayList<Integer> invalidIndexes = containsInvalidCriterion();

			if (invalidIndexes.size() == 0)
				filter();
			else
				Utils.alert("Please, select at least one criterion");
		});
	}

	private void buildCloseButton() {
		ImageView imageClose = new ImageView(new Image("/resources/graphics/icon-close.png"));
		closeButton = new Button();
		closeButton.setGraphic(imageClose);
		Tooltip.install(closeButton, new Tooltip("Return to the collection"));
		closeButton.resize(30, 30);
		closeButton.setStyle("-fx-background-color: transparent;");

		closeButton.setOnAction(e -> {
			application.switchMode(application.getPanes().getCollectionsPane());
		});
	}

	private void buildAddButton() {
		ImageView image = new ImageView(new Image("/resources/graphics/icon-add.png"));

		addButton = new Button();
		addButton.setGraphic(image);

		Tooltip.install(addButton, new Tooltip("Add new criterion"));
		addButton.resize(30, 30);
		addButton.setStyle("-fx-background-color: transparent;");

		addButton.setOnAction(e -> {

			int nbCriterions = getChoiceBoxesCriterions().size();

			ArrayList<Integer> invalidIndexes = containsInvalidCriterion();

			if (invalidIndexes.size() == 0) {

				ChoiceBoxCriterion choiceBoxCriterion = new ChoiceBoxCriterion(nbCriterions, this, getModelPropertySet());
				getChoiceBoxesCriterions().add(choiceBoxCriterion);
				getHBoxesCriterions().add(new HBoxDefaultCriterion(this, choiceBoxCriterion));

				nbCriterions++;

				System.out.println(nbCriterions + " criterions");

				placeComponents();

			}

			else {
				Utils.alert("Invalid criterion(s)");
			}
		});
	}

	private void initializeGridPane() {

		gridPane = new GridPane();
		gridPane.setPrefWidth(1400);
		gridPane.setPadding(new Insets(50));
		gridPane.setHgap(5);
		gridPane.setVgap(5);
	}

	public void setHBox(int index, HBoxCriterion hBox) {
		getHBoxesCriterions().set(index, hBox);
		placeComponents();
	}

	@Override
	public void placeComponents() {

		int nbCriterions = getHBoxesCriterions().size();
		gridPane.getChildren().clear();

		gridPane.add(titleLabel, 0, 0, 2, 1);

		for (int i = 0; i < nbCriterions; i++) {
			GridPane.setValignment(getHBoxesCriterions().get(i), VPos.TOP);
			gridPane.add(getChoiceBoxesCriterions().get(i), 0, i + 1);
			gridPane.add(getHBoxesCriterions().get(i), 1, i + 1);
		}

		collectionChoiceBox = new ChoiceBox<>();
		for (int i = 0; i < collectionsPane.getBenzenoidSetPanes().size() - 1; i++) {
			BenzenoidCollectionPane collectionPane = collectionsPane.getBenzenoidSetPanes()
					.get(i);
			collectionChoiceBox.getItems().add(collectionPane.getName());
		}

		BenzenoidCollectionPane curentPane = collectionsPane.getSelectedTab();
		collectionChoiceBox.getSelectionModel().select(curentPane.getIndex());

		HBox buttonsBox = new HBox(5.0);
		buttonsBox.getChildren().addAll(closeButton, addButton, filterButton, collectionChoiceBox);

		gridPane.add(buttonsBox, 0, nbCriterions + 1);

	}

/**
 */
	private void filter() {

		BenzenoidsCollectionsManagerPane managerPane = collectionsPane;

		int index = collectionChoiceBox.getSelectionModel().getSelectedIndex();
		BenzenoidCollectionPane collectionPane = collectionsPane.getBenzenoidSetPanes()
				.get(index);

		BenzenoidCollectionPane newCollectionPane = new BenzenoidCollectionPane(managerPane,
				managerPane.getNbCollectionPanes(), managerPane.getNextCollectionPaneLabel(collectionPane.getName() + "(filter)"));

		managerPane.log("Filtering collection: " + collectionPane.getName(), true);
		for (HBoxCriterion criterion : this.getHBoxesCriterions()) 
			managerPane.log(criterion.toString(), false);


		int size = collectionPane.getMolecules().size();
		
		getModelPropertySet().buildModelPropertySet(getHBoxesCriterions());

		///////////////////////////////
//		for (int i = 0; i < collectionPane.getMolecules().size(); i++) {
//
//			indexFiltering = i;
//
//			Molecule molecule = collectionPane.getMolecules().get(i);
//			if(Tester.testAll(molecule, getModelPropertySet())) {
//				DisplayType displayType = collectionPane.getDisplayType(i);
//				newCollectionPane.addBenzenoid(molecule, displayType);
//			}
//
//			Platform.runLater(new Runnable() {
//				@Override
//				public void run() {
//					if (indexFiltering == 1) {
//						managerPane.log((indexFiltering+1) + " / " + size , false);
//						lineConsole = collectionPane.getConsole().getNbLines() - 1;
//					} else
//						managerPane.changeLineConsole((indexFiltering+1) + " / " + size, lineConsole);
//				}
//			});
//
//		}
		////////////////////////////
		final Service<Void> calculateService = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {

					@Override
					protected Void call() throws Exception {
						for (int i = 0; i < collectionPane.getMolecules().size(); i++) {

							indexFiltering = i;

							Molecule molecule = collectionPane.getMolecules().get(i);
							if(Tester.testAll(molecule, getModelPropertySet())) {
								DisplayType displayType = collectionPane.getDisplayType(i);
								newCollectionPane.addBenzenoid(molecule, displayType);
							}

							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									if (indexFiltering == 1) {
										managerPane.log((indexFiltering+1) + " / " + size , false);
										lineConsole = collectionPane.getConsole().getNbLines() - 1;
									} else
										managerPane.changeLineConsole((indexFiltering+1) + " / " + size, lineConsole);
								}
							});

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

					Utils.alert("Filtering failed.");
					break;
				case CANCELLED:

					Utils.alert("Filtering canceled");
					break;
				case SUCCEEDED:

					newCollectionPane.refresh();

					managerPane.getTabPane().getSelectionModel().clearAndSelect(0);
					managerPane.addBenzenoidSetPane(newCollectionPane);
					managerPane.getTabPane().getSelectionModel().clearAndSelect(managerPane.getBenzenoidSetPanes().size() - 2);

					collectionsPane.log("Filtering collection " + collectionPane.getName(), true);
					for (HBoxCriterion criterion : getHBoxesCriterions()) {
						application.getBenzenoidCollectionsPane().log(criterion.toString(), false);
					}
					collectionsPane.log("-> " + newCollectionPane.getName(), false);
					collectionsPane.log("", false);

					application.switchMode(application.getPanes().getCollectionsPane());

					break;

				default:
					break;
				}

			}

		});

		calculateService.start();

	}

	private ArrayList<Integer> containsInvalidCriterion() {

		ArrayList<Integer> indexes = new ArrayList<>();

		for (int i = 0; i < getHBoxesCriterions().size(); i++) {
			if (!getHBoxesCriterions().get(i).isValid())
				indexes.add(i);
		}
		return indexes;
	}

	public void removeCriterion(ChoiceBoxCriterion choiceBoxCriterion, HBoxCriterion hBoxCriterion) {

		getChoiceBoxesCriterions().remove(choiceBoxCriterion);
		getHBoxesCriterions().remove(hBoxCriterion);

		int nbCriterions = getHBoxesCriterions().size();

		for (int i = 0; i < nbCriterions; i++)
			getChoiceBoxesCriterions().get(i).setIndex(i);

		placeComponents();
	}

	public void selectChoiceBox(BenzenoidCollectionPane collectionPane) {
		collectionChoiceBox.getSelectionModel().select(collectionPane.getName());
	}

	public BenzenoidApplication getApplication() {
		return application;
	}

	public void setPatternResolutionInformations(PatternResolutionInformations patternsInformations) {
		this.patternsInformations = patternsInformations;
	}

	public PatternResolutionInformations getPatternInformations() {
		return patternsInformations;
	}

	@Override
	public void refreshGenerationPossibility() {
		// TODO Auto-generated method stub

	}


}
