package view.generator.preferences;

import application.BenzenoidApplication;
import application.Settings;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import utils.Utils;

public class GeneratorPreferencesPane extends GridPane {

	private final BenzenoidApplication application;

	private TextField timeField;
	private ChoiceBox<String> timeUnitBox;
	private TextField solutionsField;

	public GeneratorPreferencesPane(BenzenoidApplication application) {
		this.application = application;
		initialize();
	}

	private void initialize() {

		this.setPadding(new Insets(50));
		this.setHgap(5);
		this.setVgap(5);

		Label label = new Label("Generation preferences");
		label.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, FontPosture.ITALIC, 15));

		this.add(label, 0, 0);

		timeField = new TextField();
		timeUnitBox = new ChoiceBox<>();
		solutionsField = new TextField();

		timeUnitBox.getItems().addAll("milliseconds", "seconds", "minutes", "hours");
		timeUnitBox.getSelectionModel().select(0);

		timeField.setOnKeyReleased(e -> {
			Settings settings = application.getSettings();

			if (Utils.isNumber(timeField.getText())) {
				settings.setGenerationTime(Integer.parseInt(timeField.getText()));
				settings.setTimeUnit(timeUnitBox.getValue());
				settings.save();
			}
		});

		timeUnitBox.setOnAction(e -> {
			Settings settings = application.getSettings();

			if (Utils.isNumber(timeField.getText())) {
				settings.setGenerationTime(Integer.parseInt(timeField.getText()));
				settings.setTimeUnit(timeUnitBox.getValue());
				settings.save();
			}
		});

		solutionsField.setOnKeyReleased(e -> {
			Settings settings = application.getSettings();

			if (Utils.isNumber(solutionsField.getText())) {
				settings.setNbMaxSolutions(Integer.parseInt(solutionsField.getText()));
				settings.save();
			}
		});

		Label timeLabel = new Label("Time limit: ");

		HBox timeBox = new HBox(3.0);
		timeBox.getChildren().addAll(timeLabel, timeField, timeUnitBox);

		this.add(timeBox, 0, 1);

		Label solutionLabel = new Label("Maximal solutions: ");

		HBox solutionBox = new HBox(3.0);
		solutionBox.getChildren().addAll(solutionLabel, solutionsField);
		this.add(solutionBox, 0, 2);

		Button applyButton = new Button("Apply");

		applyButton.setOnAction(e -> {

		});

		this.add(applyButton, 0, 3);

		refresh();
	}

	private void refresh() {

		Settings settings = application.getSettings();

		if (settings.getGenerationTime() > 0 && settings.getTimeUnit() != null) {
			timeField.setText(Integer.toString(settings.getGenerationTime()));
			timeUnitBox.getSelectionModel().select(settings.getTimeUnit());
		}

		if (settings.getNbMaxSolutions() > 0) {
			solutionsField.setText(Integer.toString(settings.getNbMaxSolutions()));
		}
	}
}
