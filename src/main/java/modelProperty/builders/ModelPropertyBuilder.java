package modelProperty.builders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import generator.criterions.GeneratorCriterion2;
import modelProperty.expression.HexagonNumberExpression;
import view.generator.boxes.HBoxCriterion;

public abstract class ModelPropertyBuilder {
	
	/***
	 * List of all available model properties
	 */
	private static HashMap<String, ModelPropertyBuilder> modelPropertySet;
	
	static {
		modelPropertySet = new HashMap<String, ModelPropertyBuilder>();
		modelPropertySet.put("hexagons", new HexagonNumberPropertyBuilder("Hexagons number"));
	}
	
	private String name;
	
	public ModelPropertyBuilder(String name) {
		super();
		this.name = name;
	}
	
	
	/***
	 * 
	 * @param name
	 * @return the HBoxCriterion box for filling in the parameters of the property
	 */
	static public HBoxCriterion getHBoxCriterion(String name) {
		return modelPropertySet.get(name).getHBoxCriterion();
	}
	
	/***
	 * 
	 * @return the HBoxCriterion box for filling in the parameters of the property
	 */
	public abstract HBoxCriterion getHBoxCriterion();
	
	/***
	 * getters, setters
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
