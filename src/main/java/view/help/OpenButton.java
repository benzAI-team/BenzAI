package view.help;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import utils.Utils;

public class OpenButton extends Button {

	private final HelpHBox parent;

	public OpenButton(HelpHBox parent) {
		super();
		this.parent = parent;
		initialize();
	}

	private void initialize() {

		this.resize(16, 16);
		this.setStyle("-fx-background-color: transparent;");

		Image image;

		image = new Image("/resources/graphics/icon-devellop.png");

		ImageView view = new ImageView(image);
		this.setPadding(new Insets(0));
		this.setGraphic(view);

		this.setOnAction(e -> {
			if (!parent.isOpen())
				parent.open();
			else
				parent.close();
		});
	}
}
