package collection_operations;

import molecules.Molecule;
import utils.Utils;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionsManagerPane;
import view.collections.BenzenoidPane;

import java.util.ArrayList;

public class Ims2d1aComputation extends CollectionComputation{
    Ims2d1aComputation() {
        super("Ims2D_1A");
    }

    @Override
    public void execute(BenzenoidCollectionsManagerPane collectionManagerPane) {

        BenzenoidCollectionPane currentPane = collectionManagerPane.getSelectedTab();

        String name = getName();
        BenzenoidCollectionPane benzenoidSetPane = new BenzenoidCollectionPane(collectionManagerPane, collectionManagerPane.getBenzenoidSetPanes().size(),
                collectionManagerPane.getNextCollectionPaneLabel(currentPane.getName() + "-" + name));

        if (currentPane.getSelectedBenzenoidPanes().size() == 0)
            collectionManagerPane.selectAll();

        ArrayList<BenzenoidPane> panes = new ArrayList<>(currentPane.getSelectedBenzenoidPanes());

        int nbNotAvailable = 0; // the number of benzenoids for which the map is not available
        for (BenzenoidPane pane : panes) {
            Molecule molecule = currentPane.getMolecule(pane.getIndex());
            if (molecule.getIms2d1a() != null)
                benzenoidSetPane.addBenzenoid(molecule, BenzenoidCollectionPane.DisplayType.IMS2D1A);
            else
                nbNotAvailable++;
        }

        if (nbNotAvailable == currentPane.getSelectedBenzenoidPanes().size()) {
            Utils.alert("No map is available yet for the selection");
            return;
        } else if (nbNotAvailable == 1)
            Utils.alert("No map is available yet for one benzenoid of the selection");
        else
            Utils.alert("No map is available yet for " + nbNotAvailable + " benzenoids of the selection");

        addNewSetPane(benzenoidSetPane, collectionManagerPane);
    }
}
