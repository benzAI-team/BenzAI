package view.generator.boxes;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class DeleteButton extends Button {

	DeleteButton() {
		super();
		initialize();
	}

	private void initialize() {
		this.resize(30, 30);
		this.setStyle("-fx-background-color: transparent;");
		Image imageAddButton = new Image("/resources/graphics/icon-delete.png");
		ImageView view = new ImageView(imageAddButton);
		this.setPadding(new Insets(0));
		this.setGraphic(view);
	}

}
