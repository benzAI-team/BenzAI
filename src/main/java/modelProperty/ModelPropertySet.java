package modelProperty;

import java.util.ArrayList;
import java.util.Iterator;

import generator.properties.Property;
import generator.properties.PropertySet;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.generator.boxes.HBoxModelCriterion;

public class ModelPropertySet extends PropertySet<ModelProperty> {
	private int hexagonNumberUpperBound;
	
	
	public ModelPropertySet() {
		super();
		setPropertyList(new ArrayList<ModelProperty>());
		add(new HexagonNumberProperty());
		add(new CarbonNumberProperty());
		add(new HydrogenNumberProperty());
		add(new CatacondensedProperty());
		add(new CoronenoidProperty());
		add(new CoronoidProperty());
		add(new DiameterProperty());
		add(new IrregularityProperty());
		add(new RectangleProperty());
		add(new RhombusProperty());
		add(new SymmetryProperty());
		add(new PatternProperty());
		add(new NbKekuleStructureProperty());
		add(new ConcealedNonKekuleanProperty());
	}

	/***
	 * 
	 * @param id
	 * @return true if the set contains a model property for this id
	 */
	public boolean has(String id) {
		ModelProperty property =  getById(id);
		return property != null && property.getExpressions().size() > 0;
	}

	public int computeHexagonNumberUpperBound() {
		int upperBound = Integer.MAX_VALUE;
		for(ModelProperty property : getPropertyList()) {
			int bound = property.computeHexagonNumberUpperBound();
			upperBound = upperBound < bound ? upperBound : bound;
		}
		hexagonNumberUpperBound =  upperBound;
		return upperBound;
	}

	public int getHexagonNumberUpperBound() {
		return hexagonNumberUpperBound;
	}

	public int computeNbCrowns() {
		int nbCrowns = Integer.MAX_VALUE;
		for(ModelProperty property : getPropertyList()) {
			if(this.has(property.getId())) {
				int bound = property.computeNbCrowns();
				System.out.println(property.getId() + " " + bound);
				nbCrowns = nbCrowns < bound ? nbCrowns : bound;
			}
		}
		System.out.println("crowns : " + nbCrowns);
		return nbCrowns;
	}

	public void clearPropertyExpressions() {
		for(ModelProperty property : getPropertyList())
			property.clearExpressions();
		
	}

	public boolean symmetryConstraintsAppliable() {
		return this.has("rectangle");
	}
	
	

}
