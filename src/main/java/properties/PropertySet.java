package properties;

import javafx.stage.FileChooser;
import properties.Property;
import properties.expression.PropertyExpressionFactory;
import view.generator.ChoiceBoxCriterion;
import view.generator.boxes.HBoxCriterion;
import view.generator.boxes.HBoxModelCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.regex.Pattern;

public class PropertySet implements Iterable<Property> {
	private ArrayList<Property> propertyList;

	public void add(Property property) {
		propertyList.add(property);
		property.setPropertySet(this);
	}

	/***
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
	 * @param name : full name of the property
	 * @return the property
	 */
	protected Property getByName(String name) {
		for(Property property : propertyList)
			if(Objects.equals(property.getName(), name))
				return property;
		return null;
	}
	
	@Override
	public Iterator<Property> iterator() {
		return propertyList.iterator();
	}

	public HBoxCriterion makeHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion, String name) {
		Property property = getByName(name);
		return Objects.requireNonNull(property).makeHBoxCriterion(parent, choiceBoxCriterion);
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
		return property != null && !property.getExpressions().isEmpty();
	}

	protected ArrayList<Property> getPropertyList() {
		return propertyList;
	}

	protected void setPropertyList(ArrayList<Property> propertyList) {
		this.propertyList = propertyList;
	}

	public void clearAllPropertyExpressions() {
		for(Property property : getPropertyList())
			property.clearExpressions();
	}

	/***
	 *
	 */
	public void buildPropertySet(ArrayList<HBoxCriterion> hBoxesCriterions) {
		clearAllPropertyExpressions();
		for (HBoxCriterion box : hBoxesCriterions) {
			if (!box.isValid())
				return;
			((HBoxModelCriterion)box).addPropertyExpression(this);
		}
	}

	public void save(){
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save criterions");
		File file = fileChooser.showSaveDialog(null);
		if(file != null)
			save(file);
	}

	/***
	 * save the constraint set to file 'constraints'
	 */
	public void save(File file) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(this.toString());
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void load() throws IOException{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Load criterions");
		File file = fileChooser.showOpenDialog(null);
		if(file != null)
			load(file);
	}

	public void load(File file) throws IOException {
		if (file.exists()) {
			clearAllPropertyExpressions();
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			while (line != null) {
				String[] elements = line.split(Pattern.quote(" "));
				Property property = getById(elements[0]);
				if(property != null)
					property.addExpression(PropertyExpressionFactory.build(line));
				line = reader.readLine();
			}
			reader.close();
		}
	}
}
