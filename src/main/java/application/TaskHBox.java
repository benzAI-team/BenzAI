package application;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class TaskHBox extends HBox {

	private final BenzenoidApplication application;
	private final String task;

	public TaskHBox(BenzenoidApplication application, String task) {
		super(3.0);
		this.task = task;
		this.application = application;
		initialize();
	}

	private void initialize() {
    
    Label label = new Label(task);
		label.setTextFill(Color.BLACK);
    
    if ("None".equals(task)) {
      this.getChildren().addAll(label);
      return;
    }

		Image image = new Image("/resources/graphics/icon-load.gif", 16, 16, false, false);
		ImageView loadIcon = new ImageView(image);
		loadIcon.resize(16, 16);

		ImageView imageStop = new ImageView(new Image("/resources/graphics/icon-stop.png", 16, 16, false, false));
		Button stopButton = new Button();
		stopButton.setGraphic(imageStop);
		Tooltip.install(stopButton, new Tooltip("Stop task"));
		stopButton.resize(16, 16);
		stopButton.setStyle("-fx-background-color: transparent;");

		

		stopButton.setOnAction(e -> {
			switch (task) {
				case "Benzenoid generation":
					application.getGeneratorPane().stop();
					break;
				case "RE Lin":
					application.getBenzenoidCollectionsPane().stopLin();
					break;
				case "Clar cover":
					application.getBenzenoidCollectionsPane().stopClar();
					break;
				case "Ring Bond Order":
					application.getBenzenoidCollectionsPane().stopRBO();
					break;
			}
		});

    
		this.getChildren().addAll(label, loadIcon, stopButton);
	}

	public String getTask() {
		return task;
	}

}
