package generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import generator.fragments.FragmentResolutionInformations;
import modules.CatacondensedModule;
import modules.CatacondensedModule2;
import modules.CoronenoidModule;
import modules.CoronoidModule;
import modules.CoronoidModule2;
import modules.DiameterModule;
import modules.ForbiddenFragmentModule1;
import modules.IrregularityModule;
import modules.MultipleFragments1Module;
import modules.CarbonNumberModule;
import modules.HydrogenNumberModule;
import modules.RectangleModule;
import modules.RectangleModule2;
import modules.RhombusModule;
import modules.SingleFragment1Module;
import modules.SymmetriesModule;
import solving_modes.SymmetryType;
import utils.Utils;
import modelProperty.HexagonNumberProperty;
import modelProperty.ModelProperty;
import modelProperty.ModelPropertySet;
import modelProperty.expression.BinaryNumericalExpression;


public class ModelBuilder {

	public static GeneralModel buildModel(ModelPropertySet modelPropertySet, FragmentResolutionInformations patternsInformations) {

		GeneralModel model = null;

		if(noLimitingProperties(modelPropertySet))
			return null;

		int hexagonsUpperBound = modelPropertySet.computeHexagonNumberUpperBound();
		int nbCrowns = modelPropertySet.computeNbCrowns();
		
		model = new GeneralModel(modelPropertySet);
		// A retirer ?
		if(!modelPropertySet.has("hexagons")) {
			ModelProperty hexagonNumberProperty = modelPropertySet.getById("hexagons");
			hexagonNumberProperty.addExpression(new BinaryNumericalExpression("hexagons", "<=", hexagonsUpperBound));
		}

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
	 * @param nbHexagons
	 * @return the number of crowns required for the given number of hexagons
	 */
	private static int getNbCrownsMax(int nbHexagons) {
		return (int) (Math.ceil(3.0 + Math.sqrt(12.0 * (double) nbHexagons - 3.0)));
	}

	/***
	 * 
	 * @param criterions
	 * @param map
	 * @param nbCrowns
	 * @param patternsInformations
	 * @return
	 */
	public static GeneralModel buildModel(ModelPropertySet modelPropertySet, int nbCrowns,
			FragmentResolutionInformations patternsInformations) {

		GeneralModel model = buildModel(modelPropertySet, nbCrowns);
		
		//  Patterns

		if (GeneratorCriterion.containsSubject(map.get("patterns"), Subject.SINGLE_PATTERN))
			model.addModule(new SingleFragment1Module(model, patternsInformations.getFragments().get(0),
					VariableStrategy.FIRST_FAIL, ValueStrategy.INT_MAX, OrderStrategy.CHANNELING_FIRST));

		if (GeneratorCriterion.containsSubject(map.get("patterns"), Subject.MULTIPLE_PATTERNS))
			model.addModule(new MultipleFragments1Module(model, patternsInformations.getFragments(),
					VariableStrategy.FIRST_FAIL, ValueStrategy.INT_MAX, OrderStrategy.CHANNELING_FIRST));

		if (GeneratorCriterion.containsSubject(map.get("patterns"), Subject.FORBIDDEN_PATTERN))
			model.addModule(new ForbiddenFragmentModule1(model, patternsInformations.getFragments().get(0),
					VariableStrategy.FIRST_FAIL, ValueStrategy.INT_MAX, OrderStrategy.CHANNELING_FIRST));

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

	/***
	 * 
	 * @param modelPropertySet
	 * @return
	 */
	public static GeneralModel buildModel(ModelPropertySet modelPropertySet) {
		GeneralModel model = buildModel(modelPropertySet);
		for(ModelProperty modelProperty : modelPropertySet)
			model.applyModelProperty(modelProperty);
		return model;
	}
}
