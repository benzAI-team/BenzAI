package collection_operations;

import molecules.Benzenoid;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionsManagerPane;
import view.collections.BenzenoidPane;

import java.util.ArrayList;
import java.util.Collections;

public class CollectionPaste extends CollectionOperation{
    CollectionPaste() {
        super("Paste");
    }

    @Override
    public void execute(BenzenoidCollectionsManagerPane collectionManagerPane) {
        BenzenoidCollectionPane destinationPane = collectionManagerPane.getSelectedTab();

        ArrayList<BenzenoidPane> copiedBenzenoidPanes= collectionManagerPane.getCopiedBenzenoidPanes();
        Collections.sort(copiedBenzenoidPanes);

        for (BenzenoidPane pane : copiedBenzenoidPanes) {

            BenzenoidCollectionPane originPane = pane.getBenzenoidCollectionPane();

            Benzenoid molecule = originPane.getMolecule(pane.getIndex());
            BenzenoidCollectionPane.DisplayType displayType = originPane.getDisplayType(pane.getIndex());

            destinationPane.addBenzenoid(molecule, displayType);
        }

        collectionManagerPane.log("Pasting " + copiedBenzenoidPanes.size() + " benzenoid(s) in " + destinationPane.getName(), true);

        destinationPane.refresh();

    }
}
