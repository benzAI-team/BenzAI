package modelProperty;

import modules.Module;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.generator.boxes.HBoxCriterion;
import java.util.ArrayList;

import generator.GeneratorCriterion;
import modelProperty.expression.PropertyExpression;
import modelProperty.expression.BinaryNumericalExpression;

/***
 *
 * @author nicolasprcovic
 *
 */
public abstract class ModelProperty {
	private String id;
	private String name;
	private ArrayList<PropertyExpression> expressions = new ArrayList<PropertyExpression>();
	private Module module;
	private ModelPropertySet modelPropertySet;

	/***
	 * 
	 * @param id
	 * @param name
	 * @param module
	 */
	public ModelProperty(String id, String name, Module module) {
		super();
		this.id = id;
		this.name = name;
		this.module = module;
	}
	
	/***
	 * 
	 * @param parent
	 * @param choiceBoxCriterion
	 * @return the HBoxCriterion selected in choiceBoxCriterion
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
		//return (int) (Math.ceil(3.0 + Math.sqrt(12.0 * (double) this.getModelPropertySet().getHexagonNumberUpperBound() - 3.0)));
		return (this.getModelPropertySet().getHexagonNumberUpperBound() + 2) / 2;
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
	public boolean hasExpressions() {
		return expressions.size() > 0;
	}

	/***
	 * 
	 * @return true iff any expression allows to limit the model size
	 */
	public boolean hasUpperBound() {
		return expressions.stream().anyMatch(x -> x.hasUpperBound());
	}
	
	/***
	 * getters, setters
	 */
	public Module getModule() {
		return module;
	}
	
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

	public void setModule(Module module) {
		this.module = module;
	}
	public ArrayList<PropertyExpression> getExpressions() {
		return expressions;
	}

	public ModelPropertySet getModelPropertySet() {
		return modelPropertySet;
	}

	public void setModelPropertySet(ModelPropertySet modelPropertySet) {
		this.modelPropertySet = modelPropertySet;
	}



}
