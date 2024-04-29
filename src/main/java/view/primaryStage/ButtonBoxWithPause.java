package view.primaryStage;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ButtonBoxWithPause extends ButtonBox {
    private Button pauseButton;
    private Button resumeButton;

    public ButtonBoxWithPause(String name, ScrollPaneWithPropertyList embeddingPane) {
        super(name, embeddingPane);
    }

    @Override
    public void buildButtons() {
        super.buildButtons();
        pauseButton = buildPauseButton();
        resumeButton = buildResumeButton();
    }

    private Button buildPauseButton() {
        Button pauseButton = new Button();
        ImageView imagePause = new ImageView(new Image("/resources/graphics/icon-pause.png"));
        pauseButton.setGraphic(imagePause);
        pauseButton.setStyle("-fx-background-color: transparent;");
        pauseButton.resize(32, 32);
        Tooltip.install(pauseButton, new Tooltip("Pause"));
        pauseButton.setOnAction(e -> getEmbeddingPane().pauseOperation());
        return pauseButton;
    }

    private Button buildResumeButton() {
        Button resumeButton = new Button();
        ImageView imageResume = new ImageView(new Image("/resources/graphics/icon-resume.png"));
        resumeButton.setGraphic(imageResume);
        resumeButton.setStyle("-fx-background-color: transparent;");
        resumeButton.resize(30, 30);
        Tooltip.install(resumeButton, new Tooltip("Resume generation"));
        resumeButton.setOnAction(e -> getEmbeddingPane().resumeOperation());
        return resumeButton;
    }

    public Button getPauseButton() {
        return pauseButton;
    }

    public Button getResumeButton() {
        return resumeButton;
    }
}
