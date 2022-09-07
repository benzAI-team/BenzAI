package modelProperty;

import modules.Module;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.generator.boxes.HBoxCriterion;
import java.util.ArrayList;

import generator.GeneratorCriterion;
import modelProperty.expression.PropertyExpression;
import modelProperty.expression.BinaryNumericalExpression;

public abstract class ModelProperty {
	private String subject;
	private String name;
	private ArrayList<PropertyExpression> expressions = new ArrayList<PropertyExpression>();
	private Module module;

	/***
	 * 
	 * @param subject
	 * @param module
	 */
	public ModelProperty(String subject, String name, Module module) {
		super();
		this.subject = subject;
		this.name = name;
		this.module = module;
	}
	
	/***
	 * 
	 * @param parent
	 * @param choiceBoxCriterion
	 * @return
	 */
	public abstract HBoxCriterion getHBoxCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion);
	
	/***
	 * 
	 * @return the default upper bound for the number of hexagons
	 */
	public int computeHexagonNumberUpperBound() {
		return Integer.MAX_VALUE;
	}

	/***
	 * 
	 * @return the default way to get the number of crowns from the max number of hexagons
	 */
	public int computeNbCrowns() {
		return (int) (Math.ceil(3.0 + Math.sqrt(12.0 * (double) computeHexagonNumberUpperBound() - 3.0)));
	}

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
	public boolean isExpressed() {
		return expressions.size() > 0;
	}
	
	/***
	 * getters, setters
	 */
	public Module getModule() {
		return module;
	}
	
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setModule(Module module) {
		this.module = module;
	}
	public ArrayList<PropertyExpression> getExpressions() {
		return expressions;
	}

	public boolean hasUpperBound() {
		return expressions.stream().anyMatch(x -> x.hasUpperBound());
	}



}
