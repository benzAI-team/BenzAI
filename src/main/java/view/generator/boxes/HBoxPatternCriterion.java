package view.generator.boxes;

import application.BenzenoidApplication;
import generator.patterns.PatternResolutionInformations;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import generator.properties.model.PatternProperty;
import generator.properties.model.ModelProperty;
import generator.properties.model.ModelPropertySet;
import generator.properties.model.expression.PatternExpression;
import utils.Utils;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.patterns.PatternsEditionPane;
import view.primaryStage.ScrollPaneWithPropertyList;

public class HBoxPatternCriterion extends HBoxModelCriterion {

	private PatternResolutionInformations patternInformations;
	private PatternProperty patternProperty;

	private PatternsEditionPane patternPane;
	private Stage patternStage;
	private Button editButton;
	private TextField patternInformationField;

	//private GeneratorCriterion criterion;
	

	public HBoxPatternCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion, ModelProperty modelProperty) {
		super(parent, choiceBoxCriterion);
		setPatternProperty((PatternProperty)modelProperty);
	}

	@Override
	protected void checkValidity() {

		setValid(true);

		this.getChildren().remove(getWarningIcon());
		this.getChildren().remove(editButton);
		this.getChildren().remove(getDeleteButton());

		if (patternInformationField.getText().equals("NO_PROPERTY")) {
			setValid(false);
			this.getChildren().add(getWarningIcon());
		}

		this.getChildren().addAll(editButton, getDeleteButton());
	}

	@Override
	protected void initialize() {
		initializeEditButton();

		setValid(false);

		patternInformationField = new TextField();
		patternInformationField.setEditable(false);
		patternInformationField.setText("NO_PROPERTY");

		this.getChildren().addAll(patternInformationField, getWarningIcon(), editButton, getDeleteButton());
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

		editButton.setOnAction(e -> displayPatternEditionWindows());
	}

	@Override
	public void addPropertyExpression(ModelPropertySet modelPropertySet) {

		if (isValid())
			modelPropertySet.getById("pattern").addExpression(new PatternExpression(patternInformationField.getText(), this.patternInformations));
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

	public BenzenoidApplication getApplication() {
		return ((GeneratorPane) getPane()).getApplication();
	}

	public void setPatternResolutionInformations(PatternResolutionInformations patternsInformations) {
		getPane().setPatternsInformations(patternsInformations);
	}

	public PatternProperty getPatternProperty() {
		return patternProperty;
	}

	public void setPatternProperty(PatternProperty patternProperty) {
		this.patternProperty = patternProperty;
	}
}
