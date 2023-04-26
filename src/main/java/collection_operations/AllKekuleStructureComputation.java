package collection_operations;

import molecules.Molecule;
import solveur.KekuleStructureSolver;
import utils.Utils;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionsManagerPane;
import view.collections.BenzenoidPane;

import java.util.ArrayList;

public class AllKekuleStructureComputation extends CollectionComputation{

    AllKekuleStructureComputation() {
        super("Kekulé structures");
    }

    @Override
    public void execute(BenzenoidCollectionsManagerPane collectionManagerPane) {
        BenzenoidCollectionPane currentPane = collectionManagerPane.getSelectedTab();

        if (currentPane.getBenzenoidPanes().size() == 0) {
            Utils.alert("There is no benzenoid!");
            return;
        }

        ArrayList<BenzenoidPane> selectedBenzenoidPanes = currentPane.getSelectedBenzenoidPanes();

        BenzenoidCollectionPane benzenoidSetPane = new BenzenoidCollectionPane(collectionManagerPane, collectionManagerPane.getBenzenoidSetPanes().size(),
                collectionManagerPane.getNextCollectionPaneLabel("Kekulé structures"));

        if (selectedBenzenoidPanes.size() == 0) {
            Utils.alert("Please select a benzenoid");
            return;
        } else {
            if (selectedBenzenoidPanes.size() > 1) {
                Utils.alert("Please select only one benzenoid");
                return;
            } else {
                Molecule molecule = selectedBenzenoidPanes.get(0).getMolecule();

                if (molecule.getNbKekuleStructures() == 0) {
                    Utils.alert("The selected benzenoid has no Kekulé structures.");
                    return;
                }

                ArrayList<int[][]> kekuleStructures = KekuleStructureSolver.computeKekuleStructures(molecule, 20);
                molecule.setKekuleStructures(kekuleStructures);
//TODO kekuleStructure is never used
                for (int[][] kekuleStructure : kekuleStructures) {
                    benzenoidSetPane.addBenzenoid(molecule, BenzenoidCollectionPane.DisplayType.KEKULE);
                }
            }
        }
        addNewSetPane(benzenoidSetPane, collectionManagerPane);

    }

}
