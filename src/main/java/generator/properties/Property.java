package generator.properties;

import java.util.ArrayList;

import javafx.scene.control.ScrollPane;
import modelProperty.expression.PropertyExpression;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.generator.boxes.HBoxCriterion;
import view.generator.boxes.HBoxModelCriterion;
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
	private ArrayList<PropertyExpression> expressions = new ArrayList<PropertyExpression>();

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
	public abstract HBoxCriterion getHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion);
	
	/***
	 *  
	 * @param expression
	 */
	public void addExpression(PropertyExpression expression) {
		expressions.add(expression);
	}
	
	public void removeExpression(PropertyExpression expression) {
		expressions.remove(expression);
	}
	
	public void clearExpressions() {
		expressions.clear();
		
	}
	/***
	 * 
	 * @return
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

	public void setExpressions(ArrayList<PropertyExpression> expressions) {
		this.expressions = expressions;
	}
}
