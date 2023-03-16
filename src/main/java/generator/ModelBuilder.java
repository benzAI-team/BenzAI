package generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import generator.patterns.PatternResolutionInformations;
import generator.properties.Property;
import generator.properties.solver.SolverProperty;
import modules.CatacondensedModule;
import modules.CatacondensedModule2;
import modules.CoronenoidModule;
import modules.CoronoidModule;
import modules.CoronoidModule2;
import modules.DiameterModule;
import modules.ForbiddenPatternModule1;
import modules.IrregularityModule;
import modules.MultiplePatterns1Module;
import modules.CarbonNumberModule;
import modules.HydrogenNumberModule;
import modules.RectangleModule;
import modules.RectangleModule2;
import modules.RhombusModule;
import modules.SinglePattern1Module;
import modules.SymmetriesModule;
import solving_modes.SymmetryType;
import utils.Utils;
import modelProperty.HexagonNumberProperty;
import modelProperty.ModelProperty;
import modelProperty.ModelPropertySet;
import modelProperty.PatternProperty;
import modelProperty.expression.BinaryNumericalExpression;
import modelProperty.expression.PropertyExpression;


public class ModelBuilder {

	public static GeneralModel buildModel(ModelPropertySet modelPropertySet, PatternResolutionInformations patternsInformations) {
		if(noLimitingProperties(modelPropertySet))
			return null;
		GeneralModel model = new GeneralModel(modelPropertySet);
		model.setPatternsInformations(patternsInformations);
		return model;
	}
	
	/***
	 * Checks if any given model property allows to fix the model size
	 * @param map
	 */
	private static boolean noLimitingProperties(ModelPropertySet modelPropertySet) {
		return !(modelPropertySet.has("hexagons") || modelPropertySet.has("carbons")
				|| modelPropertySet.has("hydrogens")|| modelPropertySet.has("coronenoid")
				|| modelPropertySet.has("rectangle")|| modelPropertySet.has("rhombus"));
	}
	
	/***
	 * 
	 * @param modelPropertySet
	 * @param nbCrowns
	 * @param patternsInformations
	 * @return
	 */
	public static GeneralModel buildModel(ModelPropertySet modelPropertySet, int nbCrowns,
			PatternResolutionInformations patternsInformations) {

		GeneralModel model = buildModel(modelPropertySet, nbCrowns);
		model.setPatternsInformations(patternsInformations);		
		return model;
	}

	/***
	 * 
	 * @param criterions
	 * @param map
	 * @param nbCrowns
	 * @return
	 */
	public static GeneralModel buildModel(ModelPropertySet modelPropertySet, int nbCrowns) {
		GeneralModel model = new GeneralModel(modelPropertySet, nbCrowns);
		for(Property modelProperty : modelPropertySet)
			if(modelProperty.hasExpressions())
				model.applyModelProperty((ModelProperty) modelProperty);
//		for(Property solverProperty : GeneralModel.getSolverPropertySet())
//			if(solverProperty.hasExpressions())
//				model.applySolverProperty((SolverProperty)solverProperty);
		return model;
	}
}
