package view.collections_operations;

import application.BenzenoidApplication;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import molecules.Molecule;
import utils.Utils;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionPane.DisplayType;
import view.collections.BenzenoidCollectionsManagerPane;

import java.util.ArrayList;

public class CollectionsOperationsPane extends GridPane {

	private final BenzenoidApplication application;
	private final BenzenoidCollectionsManagerPane collectionsPane;

	private ChoiceBox<String> collectionBox1;
	private ChoiceBox<String> collectionBox2;
	private ChoiceBox<String> operatorBox;

	private TextField collectionName;

	private HBox operationBox;

	public CollectionsOperationsPane(BenzenoidApplication application, BenzenoidCollectionsManagerPane collectionsPane) {
		this.application = application;
		this.collectionsPane = collectionsPane;
		initialize();
	}

	private void initialize() {

		this.setPadding(new Insets(50));
		this.setHgap(5);
		this.setVgap(5);

		Label label = new Label("Operations on collections");
		label.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, FontPosture.ITALIC, 15));

		this.add(label, 0, 0);

		collectionBox1 = new ChoiceBox<>();
		operatorBox = new ChoiceBox<>();
		collectionBox2 = new ChoiceBox<>();

		operatorBox.getItems().addAll("Union", "Intersection", "Difference");

		for (int i = 0; i < collectionsPane.getBenzenoidSetPanes().size() - 1; i++) {
			BenzenoidCollectionPane collectionPane = collectionsPane.getBenzenoidSetPanes().get(i);
			collectionBox1.getItems().add(collectionPane.getName());
			collectionBox2.getItems().add(collectionPane.getName());
		}

		Label labelCollectionName = new Label("New collection's name: ");
		collectionName = new TextField();

		operationBox = new HBox(5.0);
		operationBox.getChildren().addAll(new Label("Collection #1: "), collectionBox1, new Label("Operation: "), operatorBox,
				new Label("Collection #2: "), collectionBox2);

		HBox boxCollectionName = new HBox(5.0);
		boxCollectionName.getChildren().addAll(labelCollectionName, collectionName);

		this.add(operationBox, 0, 1);
		this.add(boxCollectionName, 0, 2);


		ImageView imageClose = new ImageView(new Image("/resources/graphics/icon-close.png"));
		Button closeButton = new Button();
		closeButton.setGraphic(imageClose);
		Tooltip.install(closeButton, new Tooltip("Return to the collection"));
		closeButton.resize(30, 30);
		closeButton.setStyle("-fx-background-color: transparent;");

		closeButton.setOnAction(e -> application.switchMode(application.getPanes().getCollectionsPane()));

		Button applyButton = new Button("Apply");

		applyButton.setOnAction(e -> {

			BenzenoidCollectionPane pane1 = null;
			BenzenoidCollectionPane pane2 = null;

			for (BenzenoidCollectionPane pane : collectionsPane.getBenzenoidSetPanes()) {
				if (pane.getName().equals(collectionBox1.getValue()))
					pane1 = pane;
				if (pane.getName().equals(collectionBox2.getValue()))
					pane2 = pane;
			}

			ArrayList<Molecule> molecules1 = pane1.getMolecules();
			ArrayList<Molecule> molecules2 = pane2.getMolecules();

			ArrayList<Molecule> molecules = null;

			if ("Union".equals(operatorBox.getValue()))
				molecules = Molecule.union(molecules1, molecules2);

			else if ("Intersection".equals(operatorBox.getValue()))
				molecules = Molecule.intersection(molecules1, molecules2);

			else if ("Difference".equals(operatorBox.getValue()))
				molecules = Molecule.diff(molecules1, molecules2);

			if (molecules != null) {

				BenzenoidCollectionsManagerPane managerPane = collectionsPane;

				String name;

				if (collectionName.getText().isEmpty())
					name = pane1.getName() + "_" + operatorBox.getValue() + "_" + pane2.getName();
				else name = collectionName.getText();

				BenzenoidCollectionPane newCollectionPane = new BenzenoidCollectionPane(managerPane,
						managerPane.getNbCollectionPanes(), name);

				for (Molecule molecule : molecules) {
					newCollectionPane.addBenzenoid(molecule, DisplayType.BASIC);
				}

				newCollectionPane.refresh();

				managerPane.getTabPane().getSelectionModel().clearAndSelect(0);
				managerPane.addBenzenoidSetPane(newCollectionPane);
				managerPane.getTabPane().getSelectionModel()
						.clearAndSelect(managerPane.getBenzenoidSetPanes().size() - 2);

				application.switchMode(application.getPanes().getCollectionsPane());
			}

			else
				Utils.alert("Error !");
		});

    HBox boxButton = new HBox(5.0);
		boxButton.getChildren().addAll(closeButton, applyButton);

		this.add(boxButton, 0, 3);
	}

	public void refreshBoxes() {

		System.out.println("refreshBoxes");

		collectionBox1 = new ChoiceBox<>();
		collectionBox2 = new ChoiceBox<>();

		this.getChildren().remove(operationBox);

		for (int i = 0; i < collectionsPane.getBenzenoidSetPanes().size() - 1; i++) {
			BenzenoidCollectionPane collectionPane = collectionsPane.getBenzenoidSetPanes()
					.get(i);
			collectionBox1.getItems().add(collectionPane.getName());
			collectionBox2.getItems().add(collectionPane.getName());
		}

		operationBox = new HBox(5.0);
		operationBox.getChildren().addAll(new Label("Collection #1: "), collectionBox1, new Label("Operation: "), operatorBox,
				new Label("Collection #2: "), collectionBox2);

		this.add(operationBox, 0, 1);
	}
}
