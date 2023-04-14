package collection_operations;

import javafx.stage.DirectoryChooser;
import view.collections.BenzenoidCollectionsManagerPane;

import java.io.File;

public class CollectionImport extends CollectionOperation{
    CollectionImport() {
        super("Import collection");
    }

    @Override
    public void execute(BenzenoidCollectionsManagerPane collectionManagerPane) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File directory = directoryChooser.showDialog(collectionManagerPane.getApplication().getStage());

        if (directory != null) {
            collectionManagerPane.importCollection(directory);
        }
    }
}
