package collection_operations;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import molecules.Benzenoid;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionsManagerPane;
import view.collections.BenzenoidPane;

import java.io.File;
import java.io.IOException;

public class CollectionExportProperties extends CollectionOperation {
    CollectionExportProperties() {
        super("Export properties");
    }

    @Override
    public void execute(BenzenoidCollectionsManagerPane collectionManagerPane) {
        BenzenoidCollectionPane currentPane = collectionManagerPane.getSelectedTab();

        if (currentPane.getSelectedBenzenoidPanes().size() == 0) {
            if (collectionManagerPane.getHoveringPane() != null) {
                FileChooser fileChooser = new FileChooser();
                File file = fileChooser.showSaveDialog(collectionManagerPane.getApplication().getStage());

                if (file != null) {
                    try {
                        currentPane.getMolecule(collectionManagerPane.getHoveringPane().getIndex()).exportProperties(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        else {

            if (currentPane.getSelectedBenzenoidPanes().size() == 1) {
                BenzenoidPane benzenoidPane = currentPane.getSelectedBenzenoidPanes().get(0);

                FileChooser fileChooser = new FileChooser();
                File file = fileChooser.showSaveDialog(collectionManagerPane.getApplication().getStage());

                if (file != null) {
                    try {
                        currentPane.getMolecule(benzenoidPane.getIndex()).exportProperties(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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

                        try {
                            File moleculeFile;
                            if (molecule.getDescription() != null && !"".equals(molecule.getDescription()))
                                moleculeFile = new File(
                                        directoryPath + "/" + molecule.getDescription().replace("\n", "") + ".csv");
                            else
                                moleculeFile = new File(directoryPath + "/" + "solution_" + i + ".csv");

                            molecule.exportProperties(moleculeFile);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                }
            }

        }

    }
}
