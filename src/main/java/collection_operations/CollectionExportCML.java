package collection_operations;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import molecules.Molecule;
import parsers.CMLConverter;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionsManagerPane;

import java.io.File;
import java.io.IOException;

public class CollectionExportCML extends CollectionOperation{
    CollectionExportCML() {
        super(".cml");
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
                        CMLConverter.generateCmlFile(molecule, file);
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

                    String filename;

                    if (!currentPane.getSelectedBenzenoidPanes().get(i).getName().equals(""))
                        filename = currentPane.getSelectedBenzenoidPanes().get(i).getName().split("\n")[0] + ".cml";
                    else {
                        filename = "unknown_molecule_" + index + ".cml";
                        index++;
                    }

                    filename = filename.replace(".graph", "");

                    File file = new File(directoryPath + "/" + filename);
                    try {
                        CMLConverter.generateCmlFile(molecule, file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
}
