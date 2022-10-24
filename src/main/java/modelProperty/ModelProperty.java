package modelProperty;

import modules.Module;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.generator.boxes.HBoxModelCriterion;
import java.util.ArrayList;

import generator.GeneratorCriterion;
import generator.properties.Property;
import modelProperty.expression.PropertyExpression;
import modelProperty.checkers.Checker;
import modelProperty.expression.BinaryNumericalExpression;

/***
 * A model property defines a property required by a benzenoid. This property is 
 * defined by:
 * - a constraint type that must be posted to the Choco solver model
 * - a checker type that checks if the molecule found by the solver meets other requirements 
 * to fulfill the property 
 * - a list of expressions. Each expression allows to instanciate a constraint+checker combo
 * that must be fulfilled by the molecule. The conjonction of all that is expressed must be
 * fulfilled by the benzenoid.
 */
public abstract class ModelProperty extends Property {
	private ArrayList<PropertyExpression> expressions = new ArrayList<PropertyExpression>();
	private Module module; // for posting constraints on the model
	private Checker checker; // for checking if a solution to the model has the other requirements

	//model property with module and checker
	public ModelProperty(String id, String name, Module module,	Checker checker) {
		super(id, name);
		this.module = module;
		this.checker = checker;
	}
	// model property without checker
	public ModelProperty(String id, String name, Module module) {
		this(id, name, module, Checker.NOCHECKER);
	}
	// model property without module
	public ModelProperty(String id, String name, Checker checker) {
		this(id, name, Module.NOMODULE, checker);
	}

	/***
	 * Getting the JavaFX HBox to input one expression for this property
	 * @param parent
	 * @param choiceBoxCriterion
	 * @return the HBoxCriterion selected in choiceBoxCriterion
	 */
	public abstract HBoxModelCriterion getHBoxCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion);
	
	/***
	 * Compute the maximal number of hexagons according to the property
	 * @return the default upper bound for the number of hexagons in the molecule
	 */
	public int computeHexagonNumberUpperBound() {
		return Integer.MAX_VALUE;
	}

	/***
	 * Compute the maximal number of crowns  according to the property
	 * @return the default way to get the number of crowns from the max number of hexagons
	 */
	public int computeNbCrowns() {
		//return (int) (Math.ceil(3.0 + Math.sqrt(12.0 * (double) this.getModelPropertySet().getHexagonNumberUpperBound() - 3.0)));
		return (((ModelPropertySet)this.getPropertySet()).getHexagonNumberUpperBound() + 2) / 2;
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

	public void setModule(Module module) {
		this.module = module;
	}
	public ArrayList<PropertyExpression> getExpressions() {
		return expressions;
	}

	public Checker getChecker() {
		return checker;
	}

	public void setChecker(Checker checker) {
		this.checker = checker;
	}

}
