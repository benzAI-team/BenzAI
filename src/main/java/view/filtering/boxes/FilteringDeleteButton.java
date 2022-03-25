package view.filtering.boxes;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import utils.Utils;

public class FilteringDeleteButton extends Button {

	private HBoxFilteringCriterion parent;

	public FilteringDeleteButton(HBoxFilteringCriterion parent) {
		super();
		this.parent = parent;
		initialize();
	}

	private void initialize() {

		this.resize(30, 30);
		this.setStyle("-fx-background-color: transparent;");

		Image imageAddButton;

    imageAddButton = new Image("/resources/graphics/icon-delete.png");

		ImageView view = new ImageView(imageAddButton);
		this.setPadding(new Insets(0));
		this.setGraphic(view);
	}
}
