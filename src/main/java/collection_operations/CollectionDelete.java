package collection_operations;

import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionsManagerPane;

public class CollectionDelete extends CollectionOperation {
    CollectionDelete() {
        super("Delete");
    }

    @Override
    public void execute(BenzenoidCollectionsManagerPane collectionManagerPane) {
        BenzenoidCollectionPane currentPane = collectionManagerPane.getSelectedTab();
        currentPane.removeBenzenoidPanes(currentPane.getSelectedBenzenoidPanes());
    }
}
