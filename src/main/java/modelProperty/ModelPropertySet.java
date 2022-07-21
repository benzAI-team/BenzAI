package modelProperty;

import java.util.ArrayList;
import java.util.Iterator;

public class ModelPropertySet implements Iterable<ModelProperty> {
	private ArrayList<ModelProperty> modelPropertySet = new ArrayList<ModelProperty>();
	
	public void add(ModelProperty modelProperty) {
		modelPropertySet.add(modelProperty);
	}

	/***
	 * 
	 * @param name
	 * @return the model property named 'name'
	 */
	public ModelProperty getByName(String name) {
		for(ModelProperty modelProperty : modelPropertySet)
			if(modelProperty.getName() == name)
				return modelProperty;
		return null;
	}
	
	/***
	 * 
	 * @param name
	 * @return true if the set contains a model property named 'name'
	 */
	public boolean has(String name) {
		return getByName(name) != null;
	}
	
	@Override
	public Iterator<ModelProperty> iterator() {
		return modelPropertySet.iterator();
	}
	
}
