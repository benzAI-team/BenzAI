package properties;

import properties.expression.PropertyExpression;
import view.generator.ChoiceBoxCriterion;
import view.generator.boxes.HBoxCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

import java.util.ArrayList;

/***
*
* @author nicolasprcovic
*
*/
public abstract class Property {
	private String id;
	private String name;
	private PropertySet propertySet;
	private final ArrayList<PropertyExpression> expressions = new ArrayList<>();

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
	public abstract HBoxCriterion makeHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion);
	
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
		return !expressions.isEmpty();
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
