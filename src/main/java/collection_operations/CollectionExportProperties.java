package collection_operations;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import benzenoid.Benzenoid;
import benzenoid.BenzenoidParser;
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
                        Benzenoid benzenoid = currentPane.getMolecule(collectionManagerPane.getHoveringPane().getIndex());
                        BenzenoidParser.exportProperties(benzenoid, file);
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
                        Benzenoid benzenoid = currentPane.getMolecule(benzenoidPane.getIndex());
                        BenzenoidParser.exportProperties(benzenoid, file);
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
                        Benzenoid benzenoid = currentPane.getMolecule(benzenoidPane.getIndex());

                        try {
                            File benzenoidFile;
                            if (benzenoid.getDescription() != null && !"".equals(benzenoid.getDescription()))
                                benzenoidFile = new File(
                                        directoryPath + "/" + benzenoid.getDescription().replace("\n", "") + ".csv");
                            else
                                benzenoidFile = new File(directoryPath + "/" + "solution_" + i + ".csv");

                            BenzenoidParser.exportProperties(benzenoid, benzenoidFile);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                }
            }

        }

    }
}
