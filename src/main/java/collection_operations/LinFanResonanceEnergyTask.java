package collection_operations;

import molecules.Benzenoid;
import utils.Utils;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionsManagerPane;
import view.collections.BenzenoidPane;

import java.util.ArrayList;

public class LinFanResonanceEnergyTask extends CollectionTask{
    LinFanResonanceEnergyTask() {
        super("Resonance energy (Lin & Fan)");
    }

    @Override
    public void execute(BenzenoidCollectionsManagerPane collectionManagerPane) {
        BenzenoidCollectionPane currentPane = collectionManagerPane.getSelectedTab();

        if (currentPane.getBenzenoidPanes().size() == 0) {
            Utils.alert("There is no benzenoid!");
            return;
        }

        ArrayList<BenzenoidPane> selectedBenzenoidPanes = currentPane.getSelectedBenzenoidPanes();

        if (selectedBenzenoidPanes.size() == 0)
            collectionManagerPane.selectAll();

        String name = "RE Lin&Fan";
        BenzenoidCollectionPane benzenoidSetPane = new BenzenoidCollectionPane(collectionManagerPane, collectionManagerPane.getBenzenoidSetPanes().size(),
                collectionManagerPane.getNextCollectionPaneLabel(currentPane.getName() + "-" + name));

        for (BenzenoidPane benzenoidPane : selectedBenzenoidPanes) {
            Benzenoid molecule = currentPane.getMolecule(benzenoidPane.getIndex());
            benzenoidSetPane.addBenzenoid(molecule, BenzenoidCollectionPane.DisplayType.RE_LIN_FAN);
        }
        addNewSetPane(benzenoidSetPane, collectionManagerPane);
    }
}
