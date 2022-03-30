package view.filtering.boxes;

import java.util.ArrayList;

import application.BenzenoidApplication;
import generator.GeneratorCriterion;
import generator.fragments.FragmentResolutionInformations;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import utils.Utils;
import view.filtering.ChoiceBoxFilteringCriterion;
import view.filtering.FilteringPane;
import view.filtering.criterions.FilteringCriterion;
import view.filtering.criterions.PatternCriterion;
import view.filtering.patterns.PatternsEditionPane;

public class HBoxPatternFilteringCriterion extends HBoxFilteringCriterion {

	private PatternsEditionPane fragmentPane;
	// private FragmentResolutionInformations patternInformations;

	private Stage patternStage;

	private Button editButton;

	private TextField patternInformationField;

	// TODO: changer en filtering criterion
	private GeneratorCriterion criterion;

	public HBoxPatternFilteringCriterion(FilteringPane parent, ChoiceBoxFilteringCriterion choiceBoxCriterion) {
		super(parent, choiceBoxCriterion);
	}

	@Override
	protected void checkValidity() {
		valid = true;

		this.getChildren().remove(warningIcon);
		this.getChildren().remove(editButton);
		this.getChildren().remove(deleteButton);

		if (patternInformationField.getText().equals("NO_PROPERTY")) {
			valid = false;
			this.getChildren().add(warningIcon);
		}

		this.getChildren().addAll(editButton, deleteButton);

	}

	@Override
	protected void initialize() {
		initializeEditButton();

		valid = false;

		patternInformationField = new TextField();
		patternInformationField.setEditable(false);
		patternInformationField.setText("NO_PROPERTY");

		this.getChildren().addAll(patternInformationField, warningIcon, editButton, deleteButton);
	}

	@Override
	public ArrayList<FilteringCriterion> buildCriterions() {
		ArrayList<FilteringCriterion> criterions = new ArrayList<>();

		if (valid) {
			FilteringCriterion filteringCriterion = new PatternCriterion(criterion, parent.getPatternInformations());
			criterions.add(filteringCriterion);
		}

		return criterions;
	}

	public void setCriterion(GeneratorCriterion criterion) {
		this.criterion = criterion;
	}

	private void initializeEditButton() {

		editButton = new Button();

		editButton.resize(30, 30);
		editButton.setStyle("-fx-background-color: transparent;");

		Image imageAddButton;

		if (Utils.onWindows())
			imageAddButton = new Image("/resources/graphics\\icon-edit.png");
		else
			imageAddButton = new Image("/resources/graphics/icon-edit.png");

		ImageView view = new ImageView(imageAddButton);
		editButton.setPadding(new Insets(0));
		editButton.setGraphic(view);

		editButton.setOnAction(e -> {
			displayPatternEditionWindows();
		});
	}

	public void displayPatternEditionWindows() {

		if (fragmentPane == null) {

			fragmentPane = new PatternsEditionPane(this);

			patternStage = new Stage();

			patternStage.getIcons().add(new Image("/resources/graphics/icon-benzene.png"));

			patternStage.setTitle("Add pattern properties");

			Scene scene = new Scene(fragmentPane);
			scene.getStylesheets().add("/resources/style/application.css");

			patternStage.setScene(scene);
			patternStage.show();
		}

		patternStage.show();
	}

	public void refreshPatternInformations(String information) {
		patternInformationField.setText(information);
		checkValidity();
	}

	public void hideFragmentStage() {
		patternStage.hide();
	}

	public PatternsEditionPane getPatternPane() {
		return fragmentPane;
	}

	public void setPatternPane(PatternsEditionPane patternPane) {
		this.fragmentPane = patternPane;
	}

	public Stage getPatternStage() {
		return patternStage;
	}

	public void setPatternStage(Stage patternStage) {
		this.patternStage = patternStage;
	}

	public BenzenoidApplication getApplication() {
		return parent.getApplication();
	}

	public void setFragmentResolutionInformations(FragmentResolutionInformations fragmentsInformations) {
		parent.setFragmentResolutionInformations(fragmentsInformations);
	}

//	public FragmentResolutionInformations getPatternInformations() {
//		return patternInformations;
//	}
}
