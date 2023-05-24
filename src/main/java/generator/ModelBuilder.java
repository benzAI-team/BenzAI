package generator;

import generator.properties.Property;
import generator.properties.model.ModelProperty;
import generator.properties.model.ModelPropertySet;


public enum ModelBuilder {
	;

	public static GeneralModel buildModel(ModelPropertySet modelPropertySet) {
		if(noLimitingProperties(modelPropertySet))
			return null;
		return new GeneralModel(modelPropertySet);
	}
	
	/***
	 * Checks if any given model property allows to fix the model size
	 */
	private static boolean noLimitingProperties(ModelPropertySet modelPropertySet) {
		return !(modelPropertySet.has("hexagons") || modelPropertySet.has("carbons")
				|| modelPropertySet.has("hydrogens")|| modelPropertySet.has("coronenoid")
				|| modelPropertySet.has("rectangle")|| modelPropertySet.has("rhombus")
				|| modelPropertySet.has("diameter"));
	}

	/***
	 * 
	 * @param nbCrowns : number of crowns
	 * @return a basic model for generating benzenoids
	 */
	public static GeneralModel buildModel(ModelPropertySet modelPropertySet, int nbCrowns) {
		GeneralModel model = new GeneralModel(modelPropertySet, nbCrowns);
		for(Property modelProperty : modelPropertySet)
			if(modelProperty.hasExpressions())
				model.applyModelConstraint((ModelProperty) modelProperty);
		return model;
	}
}
