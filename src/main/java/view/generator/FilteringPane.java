package view.filtering;

import java.util.ArrayList;

import application.ApplicationMode;
import application.BenzenoidApplication;
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
import molecules.Molecule;
import utils.Utils;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionPane.DisplayType;
import view.collections.BenzenoidsCollectionsManagerPane;
import view.filtering.boxes.HBoxDefaultFilteringCriterion;
import view.filtering.boxes.HBoxFilteringCriterion;
import view.filtering.criterions.FilteringCriterion;

public class FilteringPane extends ScrollPane {

	private BenzenoidApplication application;

	private Button addButton;
  private Button closeButton;
	private Button filterButton;

	private GridPane gridPane;

	private ArrayList<ChoiceBoxFilteringCriterion> choiceBoxesCriterions;
	private ArrayList<HBoxFilteringCriterion> hBoxesCriterions;
	private ChoiceBox<String> collectionChoiceBox;

	private FragmentResolutionInformations fragmentsInformations;

	private Label titleLabel;

	private int lineConsole;
	private int indexFiltering;
	
	public FilteringPane(BenzenoidApplication application) {
		this.application = application;
		initialize();

	}

	private void initialize() {

		titleLabel = new Label("Filter a collection");
		titleLabel.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, FontPosture.ITALIC, 15));

		choiceBoxesCriterions = new ArrayList<>();
		hBoxesCriterions = new ArrayList<>();

		initializeButtons();
		initializeGridPane();

		this.setPrefWidth(this.getPrefWidth());
		this.setContent(gridPane);

		ChoiceBoxFilteringCriterion choiceBoxCriterion = new ChoiceBoxFilteringCriterion(0, this);
    Tooltip.install(addButton, new Tooltip("Add new criterion"));
		choiceBoxesCriterions.add(choiceBoxCriterion);
		hBoxesCriterions.add(new HBoxDefaultFilteringCriterion(this, choiceBoxCriterion));

		refresh();
	}

	private void initializeButtons() {

		ImageView image = new ImageView(new Image("/resources/graphics/icon-add.png"));

		addButton = new Button();
		addButton.setGraphic(image);

		Tooltip.install(addButton, new Tooltip("Add new criterion"));
		addButton.resize(30, 30);
		addButton.setStyle("-fx-background-color: transparent;");

		addButton.setOnAction(e -> {

			int nbCriterions = choiceBoxesCriterions.size();

			ArrayList<Integer> invalidIndexes = containsInvalidCriterion();

			if (invalidIndexes.size() == 0) {

				ChoiceBoxFilteringCriterion choiceBoxCriterion = new ChoiceBoxFilteringCriterion(nbCriterions, this);
				choiceBoxesCriterions.add(choiceBoxCriterion);
				hBoxesCriterions.add(new HBoxDefaultFilteringCriterion(this, choiceBoxCriterion));

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
			application.switchMode(ApplicationMode.COLLECTIONS);
		});

		filterButton = new Button("Filter");

		filterButton.setOnAction(e -> {

			ArrayList<Integer> invalidIndexes = containsInvalidCriterion();

			if (invalidIndexes.size() == 0)
        filter(getCriterions());
      else
        Utils.alert("Please, select at least one criterion");
		});
	}

	private void initializeGridPane() {

		gridPane = new GridPane();
		gridPane.setPrefWidth(1400);
		gridPane.setPadding(new Insets(50));
		gridPane.setHgap(5);
		gridPane.setVgap(5);
	}

	public void setHBox(int index, HBoxFilteringCriterion hBox) {
		hBoxesCriterions.set(index, hBox);
		refresh();
	}

	public void refresh() {

		int nbCriterions = hBoxesCriterions.size();
		gridPane.getChildren().clear();

		gridPane.add(titleLabel, 0, 0, 2, 1);

		for (int i = 0; i < nbCriterions; i++) {
			GridPane.setValignment(hBoxesCriterions.get(i), VPos.TOP);
			gridPane.add(choiceBoxesCriterions.get(i), 0, i + 1);
			gridPane.add(hBoxesCriterions.get(i), 1, i + 1);
		}

		collectionChoiceBox = new ChoiceBox<>();
		for (int i = 0; i < application.getBenzenoidCollectionsPane().getBenzenoidSetPanes().size() - 1; i++) {
			BenzenoidCollectionPane collectionPane = application.getBenzenoidCollectionsPane().getBenzenoidSetPanes()
					.get(i);
			collectionChoiceBox.getItems().add(collectionPane.getName());
		}

		BenzenoidCollectionPane curentPane = application.getBenzenoidCollectionsPane().getSelectedTab();
		collectionChoiceBox.getSelectionModel().select(curentPane.getIndex());

		HBox buttonsBox = new HBox(5.0);
		buttonsBox.getChildren().addAll(closeButton, addButton, filterButton, collectionChoiceBox);

		gridPane.add(buttonsBox, 0, nbCriterions + 1);

	}

	private ArrayList<FilteringCriterion> getCriterions() {

		ArrayList<FilteringCriterion> criterions = new ArrayList<>();
    
    for (HBoxFilteringCriterion hBoxCriterion : hBoxesCriterions) {
      criterions.addAll(hBoxCriterion.buildCriterions());
		}

		return criterions;
	}

	private void filter(ArrayList<FilteringCriterion> criterions) {

		BenzenoidsCollectionsManagerPane managerPane = application.getBenzenoidCollectionsPane();

		int index = collectionChoiceBox.getSelectionModel().getSelectedIndex();
		BenzenoidCollectionPane collectionPane = application.getBenzenoidCollectionsPane().getBenzenoidSetPanes()
				.get(index);

		BenzenoidCollectionPane newCollectionPane = new BenzenoidCollectionPane(managerPane,
				managerPane.getNbCollectionPanes(), managerPane.getNextCollectionPaneLabel(collectionPane.getName() + "(filter)"));

		managerPane.log("Filtering collection: " + collectionPane.getName(), true);
		for (FilteringCriterion criterion : criterions) 
			managerPane.log(criterion.toString(), false);
		
		
		int size = collectionPane.getMolecules().size();
		
		final Service<Void> calculateService = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {

					@Override
					protected Void call() throws Exception {
						
						for (int i = 0; i < collectionPane.getMolecules().size(); i++) {

							indexFiltering = i;
							
							Molecule molecule = collectionPane.getMolecules().get(i);
							if (FilteringCriterion.checksCriterions(molecule, criterions)) {

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

					application.getBenzenoidCollectionsPane().log("Filtering collection " + collectionPane.getName(), true);
					for (FilteringCriterion criterion : criterions) {
						application.getBenzenoidCollectionsPane().log(criterion.toString(), false);
					}
					application.getBenzenoidCollectionsPane().log("-> " + newCollectionPane.getName(), false);
					application.getBenzenoidCollectionsPane().log("", false);

					application.switchMode(ApplicationMode.COLLECTIONS);

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

		for (int i = 0; i < hBoxesCriterions.size(); i++) {
			if (!hBoxesCriterions.get(i).isValid())
				indexes.add(i);
		}

		return indexes;
	}

	public void removeCriterion(ChoiceBoxFilteringCriterion choiceBoxCriterion, HBoxFilteringCriterion hBoxCriterion) {

		choiceBoxesCriterions.remove(choiceBoxCriterion);
		hBoxesCriterions.remove(hBoxCriterion);

		int nbCriterions = hBoxesCriterions.size();

		for (int i = 0; i < nbCriterions; i++)
			choiceBoxesCriterions.get(i).setIndex(i);

		refresh();
	}

	public void selectChoiceBox(BenzenoidCollectionPane collectionPane) {
		collectionChoiceBox.getSelectionModel().select(collectionPane.getName());
	}

	public BenzenoidApplication getApplication() {
		return application;
	}

	public void setFragmentResolutionInformations(FragmentResolutionInformations fragmentsInformations) {
		this.fragmentsInformations = fragmentsInformations;
	}

	public FragmentResolutionInformations getPatternInformations() {
		return fragmentsInformations;
	}
}
