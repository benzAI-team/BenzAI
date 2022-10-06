package generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import generator.patterns.PatternResolutionInformations;
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

		GeneralModel model = null;

		if(noLimitingProperties(modelPropertySet))
			return null;

		//int hexagonsUpperBound = modelPropertySet.computeHexagonNumberUpperBound();
		model = new GeneralModel(modelPropertySet);
		// A retirer ?
//		if(!modelPropertySet.has("hexagons")) {
//			ModelProperty hexagonNumberProperty = modelPropertySet.getById("hexagons");
//			hexagonNumberProperty.addExpression(new BinaryNumericalExpression("hexagons", "<=", hexagonsUpperBound));
//		}

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
		
		//  Patterns
		if(modelPropertySet.has("pattern")) {
			PatternProperty patternProperty = (PatternProperty) modelPropertySet.getById("pattern");
			for(PropertyExpression expression : patternProperty.getExpressions()) {
				if(expression.getId() == "SINGLE_PATTERN")
					model.addModule(new SinglePattern1Module(model, patternsInformations.getPatterns().get(0),
							VariableStrategy.FIRST_FAIL, ValueStrategy.INT_MAX, OrderStrategy.CHANNELING_FIRST));
			}
			if (patternProperty.getExpressions().contains("SINGLE_PATTERN"))
			model.addModule(new SinglePattern1Module(model, patternsInformations.getPatterns().get(0),
					VariableStrategy.FIRST_FAIL, ValueStrategy.INT_MAX, OrderStrategy.CHANNELING_FIRST));

		if (GeneratorCriterion.containsSubject(map.get("patterns"), Subject.MULTIPLE_PATTERNS))
			model.addModule(new MultiplePatterns1Module(model, patternsInformations.getPatterns(),
					VariableStrategy.FIRST_FAIL, ValueStrategy.INT_MAX, OrderStrategy.CHANNELING_FIRST));

		if (GeneratorCriterion.containsSubject(map.get("patterns"), Subject.FORBIDDEN_PATTERN))
			model.addModule(new ForbiddenPatternModule1(model, patternsInformations.getPatterns().get(0),
					VariableStrategy.FIRST_FAIL, ValueStrategy.INT_MAX, OrderStrategy.CHANNELING_FIRST));
		}
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
		for(ModelProperty modelProperty : modelPropertySet)
			model.applyModelProperty(modelProperty);
		return model;
	}
}
