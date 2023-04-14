package collection_operations;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import molecules.Molecule;
import utils.Utils;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionsManagerPane;
import view.collections.BenzenoidPane;

import java.util.ArrayList;

public class RingBoundOrderTask extends CollectionTask{
    RingBoundOrderTask() {
        super("Ring bond order");
    }

    @Override
    public void execute(BenzenoidCollectionsManagerPane collectionManagerPane) {
        BenzenoidCollectionPane currentPane = collectionManagerPane.getSelectedTab();
        if (currentPane.getBenzenoidPanes().size() == 0) {
            Utils.alert("There is no benzenoid!");
            return;
        }

        ArrayList<BenzenoidPane> selectedBenzenoidPanes = currentPane.getSelectedBenzenoidPanes();
        String name = "RBO";
        BenzenoidCollectionPane benzenoidSetPane = new BenzenoidCollectionPane(collectionManagerPane, collectionManagerPane.getBenzenoidSetPanes().size(),
                collectionManagerPane.getNextCollectionPaneLabel(currentPane.getName() + "-" + name));
        collectionManagerPane.getApplication().addTask("Ring Bond Order");
        setOperationIsRunning(true);
        if (selectedBenzenoidPanes.size() == 0) {
            collectionManagerPane.selectAll();
        }
        setCalculateService(new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() {
                        ArrayList<BenzenoidPane> panes = new ArrayList<>(selectedBenzenoidPanes);
                        setIndex(0);
                        int size = panes.size();
                        System.out.println("Computing Clar Cover of " + size + "benzenoids");
                        collectionManagerPane.log("Clar cover (" + size + " benzenoids)", true);
                        for (BenzenoidPane benzenoidPane : panes) {
                            if (operationIsRunning()) {
                                Molecule molecule = currentPane.getMolecule(benzenoidPane.getIndex());
                                molecule.getRBO();
                                benzenoidSetPane.addBenzenoid(molecule, BenzenoidCollectionPane.DisplayType.RBO);
                                setIndex(getIndex() + 1);
                                System.out.println(getIndex() + " / " + size);
                                Platform.runLater(() -> {
                                    if (getIndex() == 1) {
                                        collectionManagerPane.log(getIndex() + " / " + size, true);
                                        setLineIndex(currentPane.getConsole().getNbLines() - 1);
                                    } else
                                        collectionManagerPane.changeLineConsole(getIndex() + " / " + size, getLineIndex());
                                });

                            }
                        }
                        return null;
                    }
                };
            }
        });

        getCalculateService().stateProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case FAILED:
                    setOperationIsRunning(false);
                    Utils.alert("Failed");
                    break;
                case CANCELLED:
                case SUCCEEDED:
                    setOperationIsRunning(false);
                    addNewSetPane(benzenoidSetPane, collectionManagerPane);
                    collectionManagerPane.getApplication().removeTask("Ring Bond Order");
                    break;
            }
        });
        getCalculateService().start();
    }
}
