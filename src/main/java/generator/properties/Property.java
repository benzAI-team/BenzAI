package generator.properties;

import java.util.ArrayList;

import generator.properties.model.expression.PropertyExpression;
import view.generator.ChoiceBoxCriterion;
import view.generator.boxes.HBoxCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

/***
*
* @author nicolasprcovic
*
*/
public abstract class Property {
	private String id;
	private String name;
	private PropertySet propertySet;
	private ArrayList<PropertyExpression> expressions = new ArrayList<>();

	/***
	 *
	 */
	public Property(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	/***
	 * 
	 * @return the HBoxCriterion selected in choiceBoxCriterion
	 */
	public abstract HBoxCriterion getHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion);
	
	/***
	 *
	 */
	public void addExpression(PropertyExpression expression) {
		expressions.add(expression);
	}

	public void clearExpressions() {
		expressions.clear();
		
	}
	/***
	 *
	 */
	public boolean hasExpressions() {
		return expressions.size() > 0;
	}

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

	public ArrayList<PropertyExpression> getExpressions() {
		return expressions;
	}

}
