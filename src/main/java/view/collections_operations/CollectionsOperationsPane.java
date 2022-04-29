package view.collections_operations;

import java.util.ArrayList;

import application.ApplicationMode;
import application.BenzenoidApplication;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import molecules.Molecule;
import utils.Utils;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionPane.DisplayType;
import view.collections.BenzenoidsCollectionsManagerPane;

public class CollectionsOperationsPane extends GridPane {

	private BenzenoidApplication application;

	private ChoiceBox<String> collectionBox1;
	private ChoiceBox<String> collectionBox2;
	private ChoiceBox<String> operatorBox;

	private TextField collectionName;

	private HBox operationBox;
	private HBox boxCollectionName;

	private Label labelCollectionName;

	private Button applyButton;

	public CollectionsOperationsPane(BenzenoidApplication application) {
		super();
		this.application = application;
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

		for (int i = 0; i < application.getBenzenoidCollectionsPane().getBenzenoidSetPanes().size() - 1; i++) {
			BenzenoidCollectionPane collectionPane = application.getBenzenoidCollectionsPane().getBenzenoidSetPanes().get(i);
			collectionBox1.getItems().add(collectionPane.getName());
			collectionBox2.getItems().add(collectionPane.getName());
		}

		labelCollectionName = new Label("New collection's name: ");
		collectionName = new TextField();

		operationBox = new HBox(5.0);
		operationBox.getChildren().addAll(new Label("Collection #1: "), collectionBox1, new Label("Operation: "), operatorBox,
				new Label("Collection #2: "), collectionBox2);

		boxCollectionName = new HBox(5.0);
		boxCollectionName.getChildren().addAll(labelCollectionName, collectionName);

		this.add(operationBox, 0, 1);
		this.add(boxCollectionName, 0, 2);


		ImageView imageClose = new ImageView(new Image("/resources/graphics/icon-close.png"));
		Button closeButton = new Button();
		closeButton.setGraphic(imageClose);
		Tooltip.install(closeButton, new Tooltip("Return to the collection"));
		closeButton.resize(30, 30);
		closeButton.setStyle("-fx-background-color: transparent;");

		closeButton.setOnAction(e -> {
			application.switchMode(ApplicationMode.COLLECTIONS);
		});

		applyButton = new Button("Apply");

		applyButton.setOnAction(e -> {

			BenzenoidCollectionPane pane1 = null;
			BenzenoidCollectionPane pane2 = null;

			for (BenzenoidCollectionPane pane : application.getBenzenoidCollectionsPane().getBenzenoidSetPanes()) {
				if (pane.getName().equals(collectionBox1.getValue()))
					pane1 = pane;
				if (pane.getName().equals(collectionBox2.getValue()))
					pane2 = pane;
			}

			ArrayList<Molecule> molecules1 = pane1.getMolecules();
			ArrayList<Molecule> molecules2 = pane2.getMolecules();

			ArrayList<Molecule> molecules = null;

			if (operatorBox.getValue().equals("Union"))
				molecules = Molecule.union(molecules1, molecules2);

			else if (operatorBox.getValue().equals("Intersection"))
				molecules = Molecule.intersection(molecules1, molecules2);

			else if (operatorBox.getValue().equals("Difference"))
				molecules = Molecule.diff(molecules1, molecules2);

			if (molecules != null) {

				BenzenoidsCollectionsManagerPane managerPane = application.getBenzenoidCollectionsPane();

				String name;

				if (!collectionName.getText().equals(""))
					name = collectionName.getText();
				else
					name = pane1.getName() + "_" + operatorBox.getValue() + "_" + pane2.getName();

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

				application.switchMode(ApplicationMode.COLLECTIONS);
			}

			else
				Utils.alert("Error !");
		});

    HBox boxButton = new HBox(5.0);
		boxButton.getChildren().addAll(closeButton,applyButton);

		this.add(boxButton, 0, 3);
	}

	public void refreshBoxes() {

		System.out.println("refreshBoxes");

		collectionBox1 = new ChoiceBox<>();
		collectionBox2 = new ChoiceBox<>();

		this.getChildren().remove(operationBox);

		for (int i = 0; i < application.getBenzenoidCollectionsPane().getBenzenoidSetPanes().size() - 1; i++) {
			BenzenoidCollectionPane collectionPane = application.getBenzenoidCollectionsPane().getBenzenoidSetPanes()
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
