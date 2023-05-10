package collection_operations;

import application.BenzenoidApplication;
import molecules.Molecule;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionsManagerPane;

public class CollectionDraw extends CollectionOperation{
    CollectionDraw() {
        super("Draw");
    }

    @Override
    public void execute(BenzenoidCollectionsManagerPane collectionManagerPane) {
        BenzenoidCollectionPane currentPane = collectionManagerPane.getSelectedTab();
        BenzenoidApplication application = collectionManagerPane.getApplication();
        if (currentPane.getSelectedBenzenoidPanes().size() == 1) {
            Molecule molecule = currentPane.getMolecule(currentPane.getSelectedBenzenoidPanes().get(0).getIndex());
            application.getDrawPane().importBenzenoid(molecule);
            application.switchMode(application.getPanes().getDrawPane());
        }
        else if (collectionManagerPane.getHoveringPane() != null) {
            Molecule molecule = currentPane.getMolecule(collectionManagerPane.getHoveringPane().getIndex());
            application.getDrawPane().importBenzenoid(molecule);
            application.switchMode(application.getPanes().getDrawPane());
        }
    }
}