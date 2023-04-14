package collection_operations;

import view.collections.BenzenoidCollectionsManagerPane;

public class CollectionUnselectAll extends CollectionOperation{
    CollectionUnselectAll() {
        super("Unselect all");
    }

    @Override
    public void execute(BenzenoidCollectionsManagerPane collectionManagerPane) {
    collectionManagerPane.unselectAll();
    }
}
