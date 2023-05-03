package collection_operations;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import molecules.Molecule;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionsManagerPane;

import java.io.File;
import java.io.IOException;

public class CollectionExportGraph extends CollectionOperation{
    CollectionExportGraph() {
        super(".graph");
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
                        currentPane.getMolecule(collectionManagerPane.getHoveringPane().getIndex()).exportToGraphFile(file);
                    } catch (IOException e) {
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
                    String name = currentPane.getSelectedBenzenoidPanes().get(i).getName();
                    String filename;
                    if (!"".equals(name))
                        filename = name.split("\n")[0] + ".graph";
                    else {
                        filename = "unknown_molecule_" + index + ".graph";
                        index++;
                    }
//					File file = new File(directoryPath + "/molecule_" + i + ".graph");
                    File file = new File(directoryPath + filename);
                    try {
                        molecule.exportToGraphFile(file);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
