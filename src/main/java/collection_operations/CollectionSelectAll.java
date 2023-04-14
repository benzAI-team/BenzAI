package collection_operations;

import view.collections.BenzenoidCollectionsManagerPane;

public class CollectionSelectAll extends CollectionOperation{
    CollectionSelectAll() {
        super("Select all");
    }
    @Override
    public void execute(BenzenoidCollectionsManagerPane collectionManagerPane) {
        collectionManagerPane.selectAll();
    }
}
