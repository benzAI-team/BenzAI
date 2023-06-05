package collection_operations;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import molecules.Benzenoid;
import utils.Utils;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionsManagerPane;
import view.collections.BenzenoidPane;
import view.irregularity.IrregularityPane;

import java.io.IOException;
import java.util.ArrayList;

public class IrregularityComputation extends CollectionComputation{
    IrregularityComputation() {
        super("Irregularity statistics");
    }

    @Override
    public void execute(BenzenoidCollectionsManagerPane collectionManagerPane) {
        BenzenoidCollectionPane currentPane = collectionManagerPane.getSelectedTab();

        if (currentPane.getBenzenoidPanes().size() == 0) {
            Utils.alert("There is no benzenoid!");
            return;
        }

        ArrayList<BenzenoidPane> selectedBenzenoidPanes = currentPane.getSelectedBenzenoidPanes();
        ArrayList<Benzenoid> molecules = new ArrayList<>();

        if (selectedBenzenoidPanes.size() == 0)
            collectionManagerPane.selectAll();

        for (BenzenoidPane benzenoidPane : selectedBenzenoidPanes) {
            Benzenoid molecule = currentPane.getMolecule(benzenoidPane.getIndex());
            molecules.add(molecule);
        }

        IrregularityPane root;
        try {
            root = new IrregularityPane(collectionManagerPane, molecules, 0.1);
            Stage stage = new Stage();
            stage.setTitle("Irregularity stats");

            stage.setResizable(false);

            stage.getIcons().add(new Image("/resources/graphics/icon-benzene.png"));

            Scene scene = new Scene(root);
            scene.getStylesheets().add("/resources/style/application.css");

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
