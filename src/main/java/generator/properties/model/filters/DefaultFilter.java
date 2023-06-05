package generator.properties.model.filters;

import java.util.ArrayList;

import generator.GeneralModel;
import generator.ModelBuilder;
import generator.SolverResults;
import generator.properties.model.ModelPropertySet;
import generator.properties.model.expression.BinaryNumericalExpression;
import generator.properties.model.expression.PropertyExpression;
import constraints.BenzenoidConstraint;
import benzenoid.Benzenoid;
import constraints.BenzAIConstraint;

public class DefaultFilter extends Filter {
	private final BenzAIConstraint module;
	
	public DefaultFilter(BenzAIConstraint module) {
		super();
		this.module = module;
	}

	@Override
	public boolean test(Benzenoid molecule, ArrayList<PropertyExpression> propertyExpressionList, ModelPropertySet modelPropertySet) {
		modelPropertySet.getById("hexagons").addExpression(new BinaryNumericalExpression("hexagons", "=", molecule.getNbHexagons()));
		GeneralModel model = ModelBuilder.buildModel(modelPropertySet, molecule.getNbCrowns());
		module.build(model, propertyExpressionList);
		(new BenzenoidConstraint(molecule)).build(model, propertyExpressionList);

		SolverResults solverResults = model.solve();

		return solverResults.size() > 0;
	}

}
