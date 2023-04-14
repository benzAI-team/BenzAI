package collection_operations;

import javafx.scene.control.MenuItem;
import view.collections.BenzenoidCollectionPane;
import view.collections.BenzenoidCollectionsManagerPane;


public abstract class CollectionOperation {
	private final String name;
	private final MenuItem menuItem;
	CollectionOperation(String name){
		this.name = name;
		menuItem = new MenuItem(name);
	}
	
	public abstract void execute(BenzenoidCollectionsManagerPane collectionManagerPane);

	/***
	 * getters, setters
	 */
	public String getName() {
		return name;
	}

	public MenuItem getMenuItem() {
		return menuItem;
	}

	protected void addNewSetPane(BenzenoidCollectionPane benzenoidSetPane, BenzenoidCollectionsManagerPane collectionManagerPane) {
		benzenoidSetPane.refresh();
		collectionManagerPane.getTabPane().getSelectionModel().clearAndSelect(0);
		collectionManagerPane.addBenzenoidSetPane(benzenoidSetPane);
		collectionManagerPane.getTabPane().getSelectionModel().clearAndSelect(collectionManagerPane.getBenzenoidSetPanes().size() - 2);
	}
}
