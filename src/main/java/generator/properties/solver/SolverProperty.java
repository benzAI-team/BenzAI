package generator.properties.solver;

import java.util.ArrayList;

import org.chocosolver.solver.Solver;

import generator.properties.Property;
import modelProperty.expression.PropertyExpression;
import modules.Module;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.generator.boxes.HBoxModelCriterion;
import view.generator.boxes.HBoxSolverCriterion;

public abstract class SolverProperty extends Property{
	private SolverSpecifier specifier;
	
	public SolverProperty(String id, String name, SolverSpecifier specifier) {
		super(id, name);
		this.specifier = specifier;
	}
	
	public SolverSpecifier getSpecifier() {
		return specifier;
	}

	public void setSpecifier(SolverSpecifier specifier) {
		this.specifier = specifier;
	}
	
}
