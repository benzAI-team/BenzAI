package view.filtering;

import application.BenzenoidApplication;
import application.Operation;
import benzenoid.Benzenoid;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import properties.ModelPropertySet;
import properties.filters.Filter;
import utils.Utils;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionPane.DisplayType;
import view.collections.BenzenoidCollectionsManagerPane;
import view.generator.ChoiceBoxCriterion;
import view.generator.boxes.HBoxCriterion;
import view.generator.boxes.HBoxDefaultCriterion;
import view.primaryStage.ButtonBox;
import view.primaryStage.ScrollPaneWithPropertyList;

import java.util.ArrayList;

public class FilteringPane extends ScrollPaneWithPropertyList {

	private final BenzenoidApplication application;
	private final BenzenoidCollectionsManagerPane collectionsPane;

	private GridPane gridPane;

	private ChoiceBox<String> collectionChoiceBox;

	private Label titleLabel;

	private int indexFiltering;
	private final Label alreadyFilteredLabel = new Label("");
	private final Label alreadyFilteredNumberLabel = new Label("");


	public FilteringPane(BenzenoidApplication application, BenzenoidCollectionsManagerPane collectionsPane) {
		super(new ModelPropertySet(), new Operation() {
			@Override
			public void run(ScrollPaneWithPropertyList pane) {
				if (pane.getOperation().isPossible())
					((FilteringPane)pane).filter();
				else
					Utils.alert("Invalid criterion(s)");

			}
			@Override
			public void stop(ScrollPaneWithPropertyList pane){
				((FilteringPane)pane).stop();
			}
		},
				application);
		this.application = application; //TODO application redondant
		this.collectionsPane = collectionsPane;
		initialize();
	}

	private void initialize() {
		titleLabel = new Label("Filter a collection");
		titleLabel.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, FontPosture.ITALIC, 15));
		setButtonBox(new ButtonBox("Filter", this));
		getButtonBox().buildButtons();
		initializeGridPane();
		this.setContent(gridPane);
		initBoxCriterions();
		placeComponents();
	}

	private void initializeGridPane() {
		gridPane = new GridPane();
		gridPane.setPrefWidth(1400);
		gridPane.setPadding(new Insets(50));
		gridPane.setHgap(5);
		gridPane.setVgap(5);
	}

	private void initBoxCriterions() {
		setChoiceBoxCriterions(new ArrayList<>());
		setHBoxesCriterions(new ArrayList<>());
		ChoiceBoxCriterion choiceBoxCriterion = new ChoiceBoxCriterion(0, this, getPropertySet());
		getChoiceBoxCriterions().add(choiceBoxCriterion);
		getHBoxCriterions().add(new HBoxDefaultCriterion(this, choiceBoxCriterion));
	}

	public void setHBox(int index, HBoxCriterion hBox) {
		getHBoxCriterions().set(index, hBox);
		placeComponents();
	}

	@Override
	public void placeComponents() {

		int nbCriterions = getHBoxCriterions().size();
		gridPane.getChildren().clear();

		gridPane.add(titleLabel, 0, 0, 2, 1);

		for (int i = 0; i < nbCriterions; i++) {
			GridPane.setValignment(getHBoxCriterions().get(i), VPos.TOP);
			gridPane.add(getChoiceBoxCriterions().get(i), 0, i + 1);
			gridPane.add(getHBoxCriterions().get(i), 1, i + 1);
		}

		collectionChoiceBox = new ChoiceBox<>();
		for (int i = 0; i < collectionsPane.getBenzenoidSetPanes().size() - 1; i++) {
			BenzenoidCollectionPane collectionPane = collectionsPane.getBenzenoidSetPanes()
					.get(i);
			collectionChoiceBox.getItems().add(collectionPane.getName());
		}

		BenzenoidCollectionPane curentPane = collectionsPane.getSelectedTab();
		collectionChoiceBox.getSelectionModel().select(curentPane.getIndex());

		ButtonBox buttonBox = new ButtonBox("Filter", this);
		//buttonBox.getChildren().addAll(closeButton, addButton, filterButton, collectionChoiceBox, alreadyFilteredNumberLabel, alreadyFilteredLabel);

		gridPane.add(buttonBox, 0, nbCriterions + 1);
		initEventHandlers();
		refreshGlobalValidity();
	}

/**
 */
	private void filter() {
		BenzenoidCollectionsManagerPane managerPane = collectionsPane;
		int index = collectionChoiceBox.getSelectionModel().getSelectedIndex();
		BenzenoidCollectionPane collectionPane = collectionsPane.getBenzenoidSetPanes()
				.get(index);
		BenzenoidCollectionPane newCollectionPane = new BenzenoidCollectionPane(managerPane,
				managerPane.getNbCollectionPanes(), managerPane.getNextCollectionPaneLabel(collectionPane.getName() + "(filter)"));
		managerPane.log("Filtering collection: " + collectionPane.getName(), true);
		for (HBoxCriterion criterion : this.getHBoxCriterions())
			managerPane.log(criterion.toString(), false);
		getPropertySet().buildPropertySet(getHBoxCriterions());

		final Service<Void> calculateService = new Service<>() {
			@Override
			protected Task<Void> createTask() {
				return new Task<>() {
					@Override
					protected Void call() {
						int nbMolecules = collectionPane.getMolecules().size();
						Platform.runLater(() -> {
							alreadyFilteredNumberLabel.setText("0");
							alreadyFilteredLabel.setText("/ " + nbMolecules + " already filtered");
						});
						for (int i = 0; i < collectionPane.getMolecules().size(); i++) {
							indexFiltering = i;
							Benzenoid molecule = collectionPane.getMolecules().get(i);
							if (Filter.testAll(molecule, getPropertySet())) {
								DisplayType displayType = collectionPane.getDisplayType(i);
								newCollectionPane.addBenzenoid(molecule, displayType);
							}
							Platform.runLater(() -> alreadyFilteredNumberLabel.setText(String.valueOf(indexFiltering + 1)));
						}
						return null;
					}
				};
			}
		};

		calculateService.stateProperty().addListener((observable, oldValue, newValue) -> {
			switch (newValue) {
				case FAILED:
					Utils.alert("Filtering failed.");
					break;
				case CANCELLED:
					Utils.alert("Filtering canceled");
					break;
				case SUCCEEDED:
					if (newCollectionPane.getMolecules().isEmpty()) {
						Utils.alert("There is no remaining benzenoids after filtering ");
						return;
					}
					newCollectionPane.refresh();

					managerPane.getTabPane().getSelectionModel().clearAndSelect(0);
					managerPane.addBenzenoidSetPane(newCollectionPane);
					managerPane.getTabPane().getSelectionModel().clearAndSelect(managerPane.getBenzenoidSetPanes().size() - 2);

					collectionsPane.log("Filtering collection " + collectionPane.getName(), true);
					for (HBoxCriterion criterion : getHBoxCriterions()) {
						application.getBenzenoidCollectionsPane().log(criterion.toString(), false);
					}
					collectionsPane.log("-> " + newCollectionPane.getName(), false);
					collectionsPane.log("", false);

					application.switchMode(application.getPanes().getCollectionsPane());
					break;
				default:
					break;
			}
		});
		calculateService.start();

	}

	public void removeCriterion(ChoiceBoxCriterion choiceBoxCriterion, HBoxCriterion hBoxCriterion) {
		getChoiceBoxCriterions().remove(choiceBoxCriterion);
		getHBoxCriterions().remove(hBoxCriterion);
		int nbCriterions = getHBoxCriterions().size();
		for (int i = 0; i < nbCriterions; i++)
			getChoiceBoxCriterions().get(i).setIndex(i);
		placeComponents();
	}

	public BenzenoidApplication getApplication() {
		return application;
	}

	public void stop() {
		//TODO
	}

	@Override
	public void refreshGlobalValidity() {
		getOperation().setPossible(getHBoxCriterions().stream().allMatch(HBoxCriterion::isValid));
	}
}
