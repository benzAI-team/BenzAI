package collection_operations;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import benzenoid.Benzenoid;
import parsers.ComConverter;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionsManagerPane;

import java.io.File;
import java.io.IOException;

public class CollectionExportCOM extends CollectionOperation{
    CollectionExportCOM() {
        super(".com");
    }

    @Override
    public void execute(BenzenoidCollectionsManagerPane collectionManagerPane) {
        BenzenoidCollectionPane benzenoidCollectionPane = collectionManagerPane.getSelectedTab();
        if (benzenoidCollectionPane.getSelectedBenzenoidPanes().size() == 0) {
            if (collectionManagerPane.getHoveringPane() != null) {
                FileChooser fileChooser = new FileChooser();
                File file = fileChooser.showSaveDialog(collectionManagerPane.getApplication().getStage());
                if (file != null) {
                    int index = collectionManagerPane.getHoveringPane().getIndex();
                    Benzenoid molecule = benzenoidCollectionPane.getMolecule(index);
                    try {
                        ComConverter.generateComFile(molecule, index, file, 0, ComConverter.ComType.ER, file.getName());
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }

        else {

            DirectoryChooser directoryChooser = new DirectoryChooser();
            File directory = directoryChooser.showDialog(collectionManagerPane.getApplication().getStage());

            if (directory != null) {

                String directoryPath = directory.getAbsolutePath();

                int index = 0;

                for (int i = 0; i < benzenoidCollectionPane.getSelectedBenzenoidPanes().size(); i++) {

                    Benzenoid molecule = benzenoidCollectionPane
                            .getMolecule(benzenoidCollectionPane.getSelectedBenzenoidPanes().get(i).getIndex());

                    String fileName;

                    if (!"".equals(benzenoidCollectionPane.getSelectedBenzenoidPanes().get(i).getName()))
                        fileName = benzenoidCollectionPane.getSelectedBenzenoidPanes().get(i).getName().split("\n")[0] + ".com";
                    else {
                        fileName = "unknown_molecule_" + index + ".com";
                        index++;
                    }

                    fileName = fileName.replace(".graph", "");

                    File file = new File(directoryPath + "/" + fileName);
                    try {
                        ComConverter.generateComFile(molecule, index, file, 0, ComConverter.ComType.ER, file.getName());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
}
