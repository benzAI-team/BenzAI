package collection_operations;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import benzenoid.Benzenoid;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionsManagerPane;
import view.collections.BenzenoidPane;

import java.io.File;

public class CollectionExportPNG extends CollectionOperation{
    CollectionExportPNG() {
        super(".png");
    }

    @Override
    public void execute(BenzenoidCollectionsManagerPane collectionManagerPane) {
        BenzenoidCollectionPane currentPane = collectionManagerPane.getSelectedTab();
        if (currentPane.getSelectedBenzenoidPanes().size() == 0) {
            if (collectionManagerPane.getHoveringPane() != null) {
                FileChooser fileChooser = new FileChooser();
                File file = fileChooser.showSaveDialog(collectionManagerPane.getApplication().getStage());
                if (file != null) {
                    collectionManagerPane.getHoveringPane().exportAsPNG(file);
                }
            }
        }
        else {
            if (currentPane.getSelectedBenzenoidPanes().size() == 1) {
                BenzenoidPane benzenoidPane = currentPane.getSelectedBenzenoidPanes().get(0);
                FileChooser fileChooser = new FileChooser();
                File file = fileChooser.showSaveDialog(collectionManagerPane.getApplication().getStage());
                if (file != null) {
                    benzenoidPane.exportAsPNG(file);
                }
            }
            else {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                File file = directoryChooser.showDialog(collectionManagerPane.getApplication().getStage());
                if (file != null) {
                    String directoryPath = file.getAbsolutePath();
                    for (int i = 0; i < currentPane.getSelectedBenzenoidPanes().size(); i++) {
                        BenzenoidPane benzenoidPane = currentPane.getSelectedBenzenoidPanes().get(i);
                        Benzenoid molecule = currentPane.getMolecule(benzenoidPane.getIndex());
                        File moleculeFile;
                        if (molecule.getDescription() != null && !"".equals(molecule.getDescription()))
                            moleculeFile = new File(
                                    directoryPath + "/" + molecule.getDescription().replace("\n", "") + ".png");
                        else
                            moleculeFile = new File(directoryPath + "/" + "solution_" + i + ".png");
                        benzenoidPane.exportAsPNG(moleculeFile);
                    }
                }
            }
        }
    }
}
