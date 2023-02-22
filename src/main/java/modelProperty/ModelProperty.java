package modelProperty;

import modules.CarbonNumberModule;
import modules.Module;
import molecules.Molecule;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.generator.boxes.HBoxModelCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

import java.util.ArrayList;

import generator.GeneratorCriterion;
import generator.properties.Property;
import modelProperty.expression.PropertyExpression;
import modelProperty.testers.CarbonNumberTester;
import modelProperty.testers.Tester;
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
	private Module module; // for posting constraints on the model
	private Checker checker; // for checking if a solution to the model has the other requirements
	private Tester tester; // for testing if a molecule has the full property
	
	//model property with module and checker
	public ModelProperty(String id, String name, Module module,	Checker checker, Tester tester) {
		super(id, name);
		this.module = module;
		this.checker = checker;
		this.tester = tester;
	}
	// model property without checker
	public ModelProperty(String id, String name, Module module, Tester tester) {
		this(id, name, module, Checker.NOCHECKER, tester);
	}
	// model property without module
	public ModelProperty(String id, String name, Checker checker, Tester tester) {
		this(id, name, Module.NOMODULE, checker, tester);
	}

	/***
	 * Getting the JavaFX HBox to input one expression for this property
	 * @param parent
	 * @param choiceBoxCriterion
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


	/***
	 * 
	 * @return true iff any expression allows to limit the model size
	 */
	public boolean hasUpperBound() {
		return getExpressions().stream().anyMatch(x -> x.hasUpperBound());
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

	public Checker getChecker() {
		return checker;
	}

	public void setChecker(Checker checker) {
		this.checker = checker;
	}
	
	public Tester getTester() {
		return tester;
	}
	public void setTester(Tester tester) {
		this.tester = tester;
	}

}
