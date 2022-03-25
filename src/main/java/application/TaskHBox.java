package application;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class TaskHBox extends HBox {

	private BenzenoidApplication application;
	private String task;

	public TaskHBox(BenzenoidApplication application, String task) {
		super(3.0);
		this.task = task;
		this.application = application;
		initialize();
	}

	private void initialize() {

		Image image = new Image("/resources/graphics/icon-load.gif", 16, 16, false, false);
		ImageView loadIcon = new ImageView(image);
		loadIcon.resize(16, 16);

		ImageView imageStop = new ImageView(new Image("/resources/graphics/icon-stop.png", 16, 16, false, false));
		Button stopButton = new Button();
		stopButton.setGraphic(imageStop);
		Tooltip.install(stopButton, new Tooltip("Stop task"));
		stopButton.resize(16, 16);
		stopButton.setStyle("-fx-background-color: transparent;");

		Label label = new Label(task);
		label.setTextFill(Color.BLACK);

		stopButton.setOnAction(e -> {

			if (task.equals("Benzenoid generation"))
				application.getGeneratorPane().stop();

			else if (task.equals("RE Lin"))
				application.getBenzenoidCollectionsPane().stopLin();

			else if (task.equals("Clar cover"))
				application.getBenzenoidCollectionsPane().stopClar();

			else if (task.equals("Ring Bond Order"))
				application.getBenzenoidCollectionsPane().stopRBO();
		});

		this.getChildren().addAll(label, loadIcon, stopButton);
	}

	public String getTask() {
		return task;
	}

}
