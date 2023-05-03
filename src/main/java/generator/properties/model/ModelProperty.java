package generator.properties.model;

import constraints.BenzAIConstraint;
import generator.properties.model.expression.PropertyExpression;
import view.generator.ChoiceBoxCriterion;
import view.generator.boxes.HBoxModelCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

import generator.properties.Property;
import generator.properties.model.filters.Filter;
import generator.properties.model.checkers.Checker;

import java.util.Objects;

/***
 * A model property defines a property required by a benzenoid. This property is 
 * defined by:
 * - a constraint that must be posted to the Choco solver model
 * - a checker that checks if the molecule found by the solver meets other requirements 
 * to fulfill the property 
 * - a list of expressions. Each expression allows to instanciate a constraint+checker combo
 * that must be fulfilled by the molecule. The conjonction of all that is expressed must be
 * fulfilled by the benzenoid.
 * - a Filter that can be used to test if a molecule has the property
 */
public abstract class ModelProperty extends Property {
	private BenzAIConstraint module; // for posting constraints on the model
	private final Checker checker; // for checking if a solution to the model has the other requirements
	private Filter Filter; // for testing if a molecule has the full property
	
	//model property with module and checker
	public ModelProperty(String id, String name, BenzAIConstraint module, Checker checker, Filter Filter) {
		super(id, name);
		this.module = module;
		this.checker = checker;
		this.Filter = Filter;
	}
	// model property without checker
	public ModelProperty(String id, String name, BenzAIConstraint module, Filter Filter) {
		this(id, name, module, Checker.NOCHECKER, Filter);
	}
	// model property without module
	public ModelProperty(String id, String name, Checker checker, Filter Filter) {
		this(id, name, BenzAIConstraint.NOCONSTRAINT, checker, Filter);
	}

	/***
	 * Getting the JavaFX HBox to input one expression for this property
	 * @return the HBoxCriterion selected in choiceBoxCriterion
	 */
	public abstract HBoxModelCriterion getHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion);
	
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
		return (((ModelPropertySet) this.getPropertySet()).getHexagonNumberUpperBound() + 2) / 2;
	}

	static boolean isBoundingOperator(String operator) {
		return Objects.equals(operator, "=") || Objects.equals(operator, "<=") || Objects.equals(operator, "<");
	}

	/***
	 * 
	 * @return true iff any expression allows to limit the model size
	 */
	public boolean hasUpperBound() {
		return getExpressions().stream().anyMatch(PropertyExpression::hasUpperBound);
	}
	
	/***
	 * getters, setters
	 */
	public BenzAIConstraint getConstraint() {
		return module;
	}

	public void setModule(BenzAIConstraint module) {
		this.module = module;
	}

	public Checker getChecker() {
		return checker;
	}

	public Filter getFilter() {
		return Filter;
	}
	public void setFilter(Filter Filter) {
		this.Filter = Filter;
	}

}
