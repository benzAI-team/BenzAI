package collection_operations;

import benzenoid.Benzenoid;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionsManagerPane;
import view.collections.BenzenoidPane;

public class CheckDatabaseTask extends CollectionTask{
    CheckDatabaseTask() {
        super("Check database");
    }

    @Override
    public void execute(BenzenoidCollectionsManagerPane collectionManagerPane) {
        BenzenoidCollectionPane currentPane = collectionManagerPane.getSelectedTab();
        collectionManagerPane.log("Requesting database (" + currentPane.getName() + ", " + currentPane.getSelectedBenzenoidPanes().size()
                + " benzenoids)", true);
        Service<Void> calculateService = new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() {
                        setIndex(1);
                        int size = currentPane.getSelectedBenzenoidPanes().size();
                        for (BenzenoidPane pane : currentPane.getSelectedBenzenoidPanes()) {
                            Benzenoid benzenoid = currentPane.getMolecule(pane.getIndex());

                            if (benzenoid.getDatabaseInformation().findProperties()) {

                                System.out.println(benzenoid);
                                if (! benzenoid.hasCheckedDatabase())
                                {
                                  benzenoid.performCheckDatabase();
                                  Platform.runLater(() -> {
                                    Image image = new Image("/resources/graphics/icon-database.png");
                                    ImageView imgView = new ImageView(image);
                                    imgView.resize(30, 30);
                                    Tooltip.install(imgView,
                                            new Tooltip("This molecule exists in the database"));
                                    pane.getDescriptionBox().getChildren().add(imgView);
                                    if (getIndex() == 1) {
                                        collectionManagerPane.log(getIndex() + "/" + size, false);
                                        setLineIndex(currentPane.getConsole().getNbLines() - 1);
                                    } else {
                                        collectionManagerPane.changeLineConsole(getIndex() + "/" + size, getLineIndex());
                                    }
                                    setIndex(getIndex() + 1);
                                  });
                                }
                            }
                        }
                        return null;
                    }
                };
            }
        };

        calculateService.stateProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case FAILED:
                    System.out.println("failed");
                    break;
                case CANCELLED:
                    System.out.println("canceled");
                    break;
                case SUCCEEDED:
                    System.out.println("succeeded");
                    collectionManagerPane.unselectAll();
                    break;
            }
        });
        calculateService.start();
    }
}
