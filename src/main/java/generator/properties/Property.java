package generator.properties;

import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.generator.boxes.HBoxCriterion;
import view.generator.boxes.HBoxModelCriterion;

/***
*
* @author nicolasprcovic
*
*/
public abstract class Property {
	private String id;
	private String name;
	private PropertySet propertySet;

	/***
	 * 
	 * @param id
	 * @param name
	 */
	public Property(String id, String name) {
		super();
		this.id = id;
		this.name = name;

	}
	
	/***
	 * 
	 * @param parent
	 * @param choiceBoxCriterion
	 * @return the HBoxCriterion selected in choiceBoxCriterion
	 */
	public abstract HBoxCriterion getHBoxCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion);
	

	/***
	 * getters, setters
	 */
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PropertySet getPropertySet() {
		return propertySet;
	}

	public void setPropertySet(PropertySet propertySet) {
		this.propertySet = propertySet;
	}



}
