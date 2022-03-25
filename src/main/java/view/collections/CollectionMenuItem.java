package view.collections;

import javafx.scene.control.MenuItem;

public class CollectionMenuItem extends MenuItem{

	private int index;
	private String name;
	
	public CollectionMenuItem(int index, String name) {
		super(name);
		this.index = index;
		this.name = name;
	}
	
	public int getIndex() {
		return index;
	}
	
	public String getName() {
		return name;
	}
}
