package view.filtering.boxes;

import java.util.ArrayList;

import application.BenzenoidApplication;
import generator.GeneratorCriterion;
import generator.patterns.PatternResolutionInformations;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import modelProperty.expression.PropertyExpression;
import utils.Utils;
import view.filtering.ChoiceBoxFilteringCriterion;
import view.filtering.FilteringPane;
import view.filtering.criterions.FilteringCriterion;
import view.filtering.criterions.PatternCriterion;
import view.filtering.patterns.PatternsEditionPane;

public class HBoxPatternFilteringCriterion extends HBoxFilteringCriterion {

	private PatternsEditionPane patternPane;
	// private PatternResolutionInformations patternInformations;

	private Stage patternStage;
	private Button editButton;
	private TextField patternInformationField;

	private PropertyExpression expression;

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

		if (patternPane == null) {

			patternPane = new PatternsEditionPane(this);

			patternStage = new Stage();

			patternStage.getIcons().add(new Image("/resources/graphics/icon-benzene.png"));

			patternStage.setTitle("Add pattern properties");

			Scene scene = new Scene(patternPane);
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

	public void hidePatternStage() {
		patternStage.hide();
	}

	public PatternsEditionPane getPatternPane() {
		return patternPane;
	}

	public void setPatternPane(PatternsEditionPane patternPane) {
		this.patternPane = patternPane;
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

	public void setPatternResolutionInformations(PatternResolutionInformations patternsInformations) {
		parent.setPatternResolutionInformations(patternsInformations);
	}

	public void setExpression(PropertyExpression expression) {
		this.expression = expression;
		
	}

//	public PatternResolutionInformations getPatternInformations() {
//		return patternInformations;
//	}
}
