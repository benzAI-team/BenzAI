package collection_operations;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.TextInputDialog;
import benzenoid.Benzenoid;
import solution.ClarCoverSolution;
import solveur.ClarCoverForcedRadicalsSolver;
import utils.Utils;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionsManagerPane;
import view.collections.BenzenoidPane;

import java.util.ArrayList;

public class ForcedSingleClarCoverTask extends CollectionTask{
    ForcedSingleClarCoverTask() {
        super("All Clar covers with forced singles");
    }

    @Override
    public void execute(BenzenoidCollectionsManagerPane collectionManagerPane) {
        TextInputDialog textInputDialog = new TextInputDialog("2");
        textInputDialog.setHeaderText("Enter number of singles:");
        textInputDialog.showAndWait();
        String textInput = textInputDialog.getEditor().getText();
        int nbRadicals = Integer.parseInt(textInput);
        ForcedSingleClarCoverCompute(nbRadicals, collectionManagerPane);
    }
    private void ForcedSingleClarCoverCompute(int nbRadicals, BenzenoidCollectionsManagerPane collectionManagerPane) {

        BenzenoidCollectionPane currentPane = collectionManagerPane.getSelectedTab();

        if (currentPane.getBenzenoidPanes().size() == 0) {
            Utils.alert("There is no benzenoid!");
            return;
        }

        ArrayList<BenzenoidPane> selectedBenzenoidPanes = currentPane.getSelectedBenzenoidPanes();

        String name = "Clar cover";
        BenzenoidCollectionPane benzenoidSetPane = new BenzenoidCollectionPane(collectionManagerPane, collectionManagerPane.getBenzenoidSetPanes().size(),
                collectionManagerPane.getNextCollectionPaneLabel(currentPane.getName() + "-" + name));

        collectionManagerPane.getApplication().addTask("Clar cover");

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
                        collectionManagerPane.log("Clar Cover (" + size + "benzenoids)", true);

                        for (BenzenoidPane benzenoidPane : panes) {
                            if (operationIsRunning()) {
                                Benzenoid molecule = currentPane.getMolecule(benzenoidPane.getIndex());

                                ArrayList<ClarCoverSolution> clarCoverSolutions = ClarCoverForcedRadicalsSolver.solve(molecule, nbRadicals);
                                if (clarCoverSolutions.size() > 0) {
//                                    ClarCoverSolution clarCoverSolution = clarCoverSolutions
//                                            .get(clarCoverSolutions.size() - 1);
//                                    molecule.setClarCoverSolution(clarCoverSolution);
//                                    benzenoidSetPane.addBenzenoid(molecule, BenzenoidCollectionPane.DisplayType.CLAR_COVER);
                                    molecule.setClarCoverSolutions(clarCoverSolutions);
                                    for(ClarCoverSolution clarCoverSolution : clarCoverSolutions){
                                        System.out.println(clarCoverSolution);
                                        molecule.setClarCoverSolution(clarCoverSolution);
                                        benzenoidSetPane.addBenzenoid(molecule, BenzenoidCollectionPane.DisplayType.CLAR_COVER);
                                    }
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
