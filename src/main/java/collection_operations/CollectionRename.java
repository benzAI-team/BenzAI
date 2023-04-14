package collection_operations;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import view.collections.BenzenoidCollectionsManagerPane;
import view.collections.RenameCollectionPane;

public class CollectionRename extends CollectionOperation{
    CollectionRename() {
        super("Rename collection");
    }

    @Override
    public void execute(BenzenoidCollectionsManagerPane collectionManagerPane) {
        RenameCollectionPane root;
        root = new RenameCollectionPane(collectionManagerPane);
        Stage stage = new Stage();
        stage.setTitle("Rename collection");

        stage.setResizable(false);

        stage.getIcons().add(new Image("/resources/graphics/icon-benzene.png"));

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/resources/style/application.css");

        stage.setScene(scene);
        stage.show();

    }
}
