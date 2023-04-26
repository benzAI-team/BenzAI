package generator.properties.model;

import java.util.ArrayList;

import generator.properties.Property;
import generator.properties.PropertySet;
import view.generator.boxes.HBoxCriterion;
import view.generator.boxes.HBoxModelCriterion;

public class ModelPropertySet extends PropertySet {
	private int hexagonNumberUpperBound;
	
	
	public ModelPropertySet() {
		super();
		setPropertyList(new ArrayList<>());
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


	public int computeHexagonNumberUpperBound() {
		int upperBound = Integer.MAX_VALUE;
		for(Property property : getPropertyList()) {
			int bound = ((ModelProperty) property).computeHexagonNumberUpperBound();
			upperBound = Math.min(upperBound, bound);
		}
		hexagonNumberUpperBound =  upperBound;
		return upperBound;
	}

	public int getHexagonNumberUpperBound() {
		return hexagonNumberUpperBound;
	}

	public int computeNbCrowns() {
		int nbCrowns = Integer.MAX_VALUE;
		for(Property property : getPropertyList()) {
			if(this.has(property.getId())) {
				int bound = ((ModelProperty) property).computeNbCrowns();
				System.out.println(property.getId() + " " + bound);
				nbCrowns = Math.min(nbCrowns, bound);
			}
		}
		System.out.println("crowns : " + nbCrowns);
		return nbCrowns;
	}

	public void clearPropertyExpressions() {
		for(Property property : getPropertyList())
			property.clearExpressions();
		
	}

	public boolean symmetryConstraintsAppliable() {
		return this.has("rectangle");
	}

	/***
	 *
	 */
	public boolean buildModelPropertySet(ArrayList<HBoxCriterion> hBoxesCriterions) {
		clearPropertyExpressions();
		for (HBoxCriterion box : hBoxesCriterions) {
			if (!box.isValid())
				return false;
			((HBoxModelCriterion)box).addPropertyExpression(this);
		}
		return true;
	}

}
