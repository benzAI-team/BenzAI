package view.collections_operations;

import java.util.ArrayList;

import application.ApplicationMode;
import application.BenzenoidApplication;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
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
	private HBox box;

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
			BenzenoidCollectionPane collectionPane = application.getBenzenoidCollectionsPane().getBenzenoidSetPanes()
					.get(i);
			collectionBox1.getItems().add(collectionPane.getName());
			collectionBox2.getItems().add(collectionPane.getName());
		}

		box = new HBox(3.0);
		box.getChildren().addAll(collectionBox1, operatorBox, collectionBox2);

		this.add(box, 0, 1);

		Button applyButton = new Button("Apply");

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

				String name = pane1.getName() + "_" + operatorBox.getValue() + "_" + pane2.getName();

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

		this.add(applyButton, 0, 2);
	}

	public void refreshBoxes() {

		System.out.println("refreshBoxes");

		collectionBox1 = new ChoiceBox<>();
		collectionBox2 = new ChoiceBox<>();

		for (int i = 0; i < application.getBenzenoidCollectionsPane().getBenzenoidSetPanes().size() - 1; i++) {
			BenzenoidCollectionPane collectionPane = application.getBenzenoidCollectionsPane().getBenzenoidSetPanes()
					.get(i);
			collectionBox1.getItems().add(collectionPane.getName());
			collectionBox2.getItems().add(collectionPane.getName());
		}

		box = new HBox(3.0);
		box.getChildren().addAll(collectionBox1, operatorBox, collectionBox2);

		this.add(box, 0, 1);
	}
}
