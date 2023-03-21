package collection_operations;

public abstract class CollectionOperation {
	private String name;
	
	CollectionOperation(String name){
		this.name = name;
	}
	
	public abstract void execute();
}
