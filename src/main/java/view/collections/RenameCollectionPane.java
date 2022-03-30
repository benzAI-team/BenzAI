package view.collections;

import java.util.Iterator;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import utils.Utils;

public class RenameCollectionPane extends GridPane {

	private BenzenoidsCollectionsManagerPane parent;

	private TextField fieldCollectionName;

	public RenameCollectionPane(BenzenoidsCollectionsManagerPane parent) {
		this.parent = parent;
		initialize();
	}

	private void initialize() {

		this.setPadding(new Insets(20));
		this.setHgap(25);
		this.setVgap(15);

		Label label = new Label("Collection's name: ");
		fieldCollectionName = new TextField();

		Button addButton = new Button();
		addButton.resize(32, 32);
		addButton.setStyle("-fx-background-color: transparent;");

		Image imageAddButton;

    imageAddButton = new Image("/resources/graphics/icon-add.png");

		ImageView view = new ImageView(imageAddButton);
		addButton.setPadding(new Insets(0));
		addButton.setGraphic(view);

		fieldCollectionName.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER) {
				addButton.fire();
			}
		});

		addButton.setOnAction(e -> {

			String name = fieldCollectionName.getText();

			if ((!name.equals("")) && (!name.equals("+"))) {
        Iterator<BenzenoidCollectionPane> iter = parent.getBenzenoidSetPanes().iterator();
        while ((iter.hasNext()) && (!iter.next().getName().equals(name))) {
        }

        if (! iter.hasNext())
          parent.renameCurentTab(name);
			}

			Stage stage = (Stage) this.getScene().getWindow();
			stage.close();
		});

		HBox box = new HBox(5.0);
		box.getChildren().addAll(label, fieldCollectionName, addButton);
		this.add(box, 0, 0);
	}
}
