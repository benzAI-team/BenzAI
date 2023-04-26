package view.catalog;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class DeleteButton extends Button {

	public DeleteButton(CatalogPane parent, int index) {

		Image image;

    image = new Image("/resources/graphics/close_button.png");

		ImageView view = new ImageView(image);

		this.resize(30, 30);

		this.setPadding(new Insets(0));

		this.setGraphic(view);

		this.setOnAction(e -> parent.removeEntry(index));

	}

}
