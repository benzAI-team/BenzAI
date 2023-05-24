package collection_operations;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import molecules.Molecule;
import solution.ClarCoverSolution;
import solveur.ClarCoverSolver;
import utils.Utils;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionsManagerPane;
import view.collections.BenzenoidPane;

import java.util.ArrayList;

public class ClarCoverTask extends CollectionTask{
    ClarCoverTask() {
        super("Clar cover");
    }

    @Override
    public void execute(BenzenoidCollectionsManagerPane collectionManagerPane) {
        BenzenoidCollectionPane currentPane = collectionManagerPane.getSelectedTab();

        if (currentPane.getBenzenoidPanes().isEmpty()) {
            Utils.alert("There is no benzenoid!");
            return;
        }

        ArrayList<BenzenoidPane> selectedBenzenoidPanes = currentPane.getSelectedBenzenoidPanes();

        String name = "Clar cover";
        BenzenoidCollectionPane benzenoidSetPane = new BenzenoidCollectionPane(collectionManagerPane, collectionManagerPane.getBenzenoidSetPanes().size(),
                collectionManagerPane.getNextCollectionPaneLabel(currentPane.getName() + "-" + name));

        collectionManagerPane.getApplication().addTask("Clar cover");

        if (selectedBenzenoidPanes.isEmpty()) {
            collectionManagerPane.selectAll();
        }

        setCalculateService(new Service<>() {

            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() {
                        setOperationIsRunning(true);
                        ArrayList<BenzenoidPane> panes = new ArrayList<>(selectedBenzenoidPanes);

                        setIndex(0);
                        int size = panes.size();

                        System.out.println("Computing Clar Cover of " + size + " benzenoids");
                        collectionManagerPane.log("Clar Cover (" + size + " benzenoids)", true);

                        for (BenzenoidPane benzenoidPane : panes) {
                            if (operationIsRunning()) {
                                Molecule molecule = currentPane.getMolecule(benzenoidPane.getIndex());

                                ArrayList<ClarCoverSolution> clarCoverSolutions = ClarCoverSolver.solve(molecule);
                                if (clarCoverSolutions.size() > 0) {
                                    ClarCoverSolution clarCoverSolution = clarCoverSolutions
                                            .get(clarCoverSolutions.size() - 1);
                                    molecule.setClarCoverSolution(clarCoverSolution);
                                    benzenoidSetPane.addBenzenoid(molecule, BenzenoidCollectionPane.DisplayType.CLAR_COVER);
                                }
                                setIndex(getIndex() + 1);
                                System.out.println(getIndex() + " / " + size);

                                Platform.runLater(() -> {
                                    if (getIndex() == 1) {
                                        collectionManagerPane.log(getIndex() + " / " + size, false);
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
                    collectionManagerPane.getApplication().removeTask("Clar cover");
                    break;
            }
        });
        getCalculateService().start();
    }
}
