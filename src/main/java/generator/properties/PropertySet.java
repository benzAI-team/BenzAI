package generator.properties;

import java.util.ArrayList;
import java.util.Iterator;

import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.generator.boxes.HBoxCriterion;
import view.generator.boxes.HBoxModelCriterion;

public class PropertySet<P extends Property> implements Iterable<P> {
	private ArrayList<P> propertyList;

	public void add(P property) {
		propertyList.add(property);
		property.setPropertySet(this);
	}

	/***
	 * 
	 * @param id
	 * @return the model property with 'id'
	 */
	public P getById(String id) {
		for(P property : propertyList)
			if(property.getId() == id)
				return property;
		return null;
	}
	
	/***
	 * 
	 * @param name
	 * @return
	 */
	public P getByName(String name) {
		for(P property : propertyList)
			if(property.getName() == name)
				return property;
		return null;
	}
	
	@Override
	public Iterator<P> iterator() {
		return (Iterator<P>) propertyList.iterator();
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

	public ArrayList<P> getPropertyList() {
		return propertyList;
	}

	public void setPropertyList(ArrayList<P> propertyList) {
		this.propertyList = propertyList;
	}
	
	
}
