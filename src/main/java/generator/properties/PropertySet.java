package generator.properties;

import java.util.ArrayList;
import java.util.Iterator;

import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.generator.boxes.HBoxCriterion;

public class PropertySet implements Iterable<Property> {
	private ArrayList<Property> propertyList;

	public void add(Property property) {
		propertyList.add(property);
		property.setPropertySet(this);
	}

	/***
	 * 
	 * @param id
	 * @return the model property with 'id'
	 */
	public Property getById(String id) {
		for(Property property : propertyList)
			if(property.getId() == id)
				return property;
		return null;
	}
	
	/***
	 * 
	 * @param name
	 * @return
	 */
	public Property getByName(String name) {
		for(Property property : propertyList)
			if(property.getName() == name)
				return property;
		return null;
	}
	
	@Override
	public Iterator<Property> iterator() {
		return (Iterator<Property>) propertyList.iterator();
	}

	public HBoxCriterion getHBoxCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion, String name) {
		Property property = getByName(name);
		return property.getHBoxCriterion(parent, choiceBoxCriterion);
	}

	public String[] getNames() {
		String[] names = new String[propertyList.size()];
		int i = 0;
		for(Property property : propertyList)
			names[i++] = property.getName();
		return names;
	}
	
	public Object[] getIds() {
		return (Object[]) propertyList.stream().map(x -> x.getId()).toArray();
	}

	/***
	 * 
	 * @param id
	 * @return true if the set contains a model property for this id
	 */
	public boolean has(String id) {
		Property property =  getById(id);
		return property != null && property.getExpressions().size() > 0;
	}

	public ArrayList<Property> getPropertyList() {
		return propertyList;
	}

	public void setPropertyList(ArrayList<Property> propertyList) {
		this.propertyList = propertyList;
	}
	
	
}
