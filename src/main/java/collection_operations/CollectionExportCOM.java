package collection_operations;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import molecules.Molecule;
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
        BenzenoidCollectionPane currentPane = collectionManagerPane.getSelectedTab();
        if (currentPane.getSelectedBenzenoidPanes().size() == 0) {
            if (collectionManagerPane.getHoveringPane() != null) {
                FileChooser fileChooser = new FileChooser();
                File file = fileChooser.showSaveDialog(collectionManagerPane.getApplication().getStage());
                if (file != null) {
                    Molecule molecule = currentPane.getMolecule(collectionManagerPane.getHoveringPane().getIndex());
                    try {
                        ComConverter.generateComFile(molecule, file, 0, ComConverter.ComType.ER, file.getName());
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

                for (int i = 0; i < currentPane.getSelectedBenzenoidPanes().size(); i++) {

                    Molecule molecule = currentPane
                            .getMolecule(currentPane.getSelectedBenzenoidPanes().get(i).getIndex());

                    String fileName;

                    if (!"".equals(currentPane.getSelectedBenzenoidPanes().get(i).getName()))
                        fileName = currentPane.getSelectedBenzenoidPanes().get(i).getName().split("\n")[0] + ".com";
                    else {
                        fileName = "unknown_molecule_" + index + ".com";
                        index++;
                    }

                    fileName = fileName.replace(".graph", "");

                    File file = new File(directoryPath + "/" + fileName);
                    try {
                        ComConverter.generateComFile(molecule, file, 0, ComConverter.ComType.ER, file.getName());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
}
