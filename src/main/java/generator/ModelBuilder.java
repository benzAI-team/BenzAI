package generator;

import generator.patterns.PatternResolutionInformations;
import generator.properties.Property;
import generator.properties.model.ModelProperty;
import generator.properties.model.ModelPropertySet;


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
