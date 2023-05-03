package generator.properties;

import view.generator.ChoiceBoxCriterion;
import view.generator.boxes.HBoxCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class PropertySet implements Iterable<Property> {
	private ArrayList<Property> propertyList;

	public void add(Property property) {
		propertyList.add(property);
		property.setPropertySet(this);
	}

	/***
	 * 
	 * @param id : identifier
	 * @return the model property with 'id'
	 */
	public Property getById(String id) {
		for(Property property : propertyList)
			if(Objects.equals(property.getId(), id))
				return property;
		return null;
	}
	
	/***
	 * 
	 * @param name : full name of the property
	 * @return the property
	 */
	public Property getByName(String name) {
		for(Property property : propertyList)
			if(Objects.equals(property.getName(), name))
				return property;
		return null;
	}
	
	@Override
	public Iterator<Property> iterator() {
		return propertyList.iterator();
	}

	public HBoxCriterion getHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion, String name) {
		Property property = getByName(name);
		return property.getHBoxCriterion(parent, choiceBoxCriterion);
	}

	public String[] getNames() {
		String[] names = new String[propertyList.size()];
		int i = 0;
		for(Property property : propertyList) {
			names[i] = property.getName();
			i++;
		}
		return names;
	}

	/***
	 * 
	 * @param id : identifier
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
