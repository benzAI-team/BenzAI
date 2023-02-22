package modelProperty.testers;

import java.util.ArrayList;
import java.util.HashMap;

import generator.GeneralModel;
import generator.GeneratorCriterion;
import generator.ModelBuilder;
import generator.SolverResults;
import modelProperty.expression.BinaryNumericalExpression;
import modelProperty.expression.PropertyExpression;
import modules.BenzenoidModule;
import molecules.Molecule;
import view.generator.GeneratorPane;
import modules.Module;

public class DefaultTester extends Tester {
	private Module module;
	
	public DefaultTester(Module module) {
		super();
		this.module = module;
	}

	@Override
	public boolean test(Molecule molecule, ArrayList<PropertyExpression> propertyExpressionList) {
		GeneralModel.getModelPropertySet().getById("hexagons").addExpression(new BinaryNumericalExpression("hexagons", "=", molecule.getNbHexagons()));
		GeneralModel model = ModelBuilder.buildModel(GeneralModel.getModelPropertySet(), molecule.getNbCrowns());
		module.build(model, propertyExpressionList);
		(new BenzenoidModule(molecule)).build(model, propertyExpressionList);

		SolverResults solverResults = model.solve();

		return solverResults.size() > 0;
	}

}
