package collection_operations;

import java.util.ArrayList;

public class CollectionOperationSet {
	private ArrayList<CollectionOperation> collectionOperations = new ArrayList<CollectionOperation>();
	
	CollectionOperationSet(){
		collectionOperations.add(new LinCollectionTask());
	}
	
}
