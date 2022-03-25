package view.draw;

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
import molecules.Molecule;
import utils.Utils;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidsCollectionsManagerPane;
import view.collections.BenzenoidCollectionPane.DisplayType;

public class AddToNewCollectionPane extends GridPane {

	private Molecule molecule;
	private BenzenoidsCollectionsManagerPane parent;
	private TextField fieldCollectionName;

	public AddToNewCollectionPane(BenzenoidsCollectionsManagerPane parent, Molecule molecule) {

		this.parent = parent;
		this.molecule = molecule;

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
			String name = "Collection #" + (parent.size() + 1);

			if (!fieldCollectionName.getText().equals(""))
				name = fieldCollectionName.getText();

			BenzenoidCollectionPane collectionPane = new BenzenoidCollectionPane(parent,
					parent.getBenzenoidSetPanes().size(), name);

			collectionPane.addBenzenoid(molecule, DisplayType.BASIC);
			collectionPane.refresh();
			
			parent.addBenzenoidSetPane(collectionPane);
			
			Utils.showAlertWithoutHeaderText("Molecule added to collection: " + name);
			
			Stage stage = (Stage) this.getScene().getWindow();
			stage.close();
			
			
		});

		HBox box = new HBox(5.0);
		box.getChildren().addAll(label, fieldCollectionName, addButton);
		this.add(box, 0, 0);
	}
}
