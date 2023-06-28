package collection_operations;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import benzenoid.Benzenoid;
import spectrums.ResultLogFile;
import utils.Utils;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionsManagerPane;
import view.collections.BenzenoidPane;

import java.util.ArrayList;
import java.util.Optional;

public class IRSpectraTask extends CollectionTask {
    private int indexDatabase;
    private int lineIndexDatabase;

    IRSpectraTask() {
        super("IR spectra");
    }

    @Override
    public void execute(BenzenoidCollectionsManagerPane collectionManagerPane) {

        BenzenoidCollectionPane currentPane = collectionManagerPane.getSelectedTab();

        if (currentPane.getBenzenoidPanes().size() == 0) {
            Utils.alert("There is no benzenoid!");
            return;
        }

        collectionManagerPane.log("Requesting database (" + currentPane.getName() + ", " + currentPane.getSelectedBenzenoidPanes().size()
                + " benzenoids)", true);

        if (currentPane.getSelectedBenzenoidPanes().size() == 0) {
            Utils.alert("Please, assign at least one benzenoid having less than 10 hexagons");
            return;
        }

        ArrayList<BenzenoidPane> panes = new ArrayList<>(currentPane.getSelectedBenzenoidPanes());

        Service<Void> calculateService = new Service<>() {

            @Override
            protected Task<Void> createTask() {
                return new Task<>() {

                    @Override
                    protected Void call() {
                        indexDatabase = 1;
                        int size = panes.size();

                        for (BenzenoidPane pane : panes) {

                            Benzenoid benzenoid = currentPane.getMolecule(pane.getIndex());

                            Optional<ResultLogFile> IRSpectra = benzenoid.getDatabaseInformation().findIRSpectra();

                            if (IRSpectra.isPresent()) {
                                System.out.println(benzenoid);

                                Platform.runLater(() -> {
                                    Image image = new Image("/resources/graphics/icon-database.png");
                                    ImageView imgView = new ImageView(image);
                                    imgView.resize(30, 30);
                                    Tooltip.install(imgView,
                                            new Tooltip("This molecule exists in the database"));
                                    pane.getDescriptionBox().getChildren().add(imgView);

                                    if (indexDatabase == 1) {
                                        collectionManagerPane.log(indexDatabase + "/" + size, false);
                                        lineIndexDatabase = currentPane.getConsole().getNbLines() - 1;
                                    } else {
                                        collectionManagerPane.changeLineConsole(indexDatabase + "/" + size, lineIndexDatabase);
                                    }

                                    indexDatabase++;
                                });

                                pane.buildFrequencies();
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
                    collectionManagerPane.displayIRSpectra(panes, currentPane);
                    break;
            }
        });

        calculateService.start();


    }
}
