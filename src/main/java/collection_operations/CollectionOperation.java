package collection_operations;

import view.collections.BenzenoidCollectionsManagerPane;

public abstract class CollectionOperation {
	private final String name;
	private BenzenoidCollectionsManagerPane collectionManagerPane;
	
	CollectionOperation(String name){
		this.name = name;
	}
	
	public abstract void execute();

	/***
	 * getters, setters
	 */
	public String getName() {
		return name;
	}

	public BenzenoidCollectionsManagerPane getCollectionManagerPane() {
		return collectionManagerPane;
	}


}
