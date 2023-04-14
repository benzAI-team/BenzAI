package collection_operations;

import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionsManagerPane;

public class CollectionCopy extends CollectionOperation{
    CollectionCopy() {
        super("Copy");
    }

    @Override
    public void execute(BenzenoidCollectionsManagerPane collectionManagerPane) {
        BenzenoidCollectionPane originBenzenoidCollectionPane = collectionManagerPane.getSelectedTab();
        originBenzenoidCollectionPane.copy();
    }
}
