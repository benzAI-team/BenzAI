package generator.properties.model;

import generator.properties.Property;
import generator.properties.PropertySet;
import view.generator.boxes.HBoxCriterion;
import view.generator.boxes.HBoxModelCriterion;

import java.util.ArrayList;

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


	public boolean hasUpperBound(){
		return ((ModelProperty) getById("hexagons")).hasUpperBound()
				|| ((ModelProperty) getById("carbons")).hasUpperBound()
				|| ((ModelProperty) getById("hydrogens")).hasUpperBound()
				|| ((ModelProperty) getById("rhombus")).hasUpperBound()
				|| ((ModelProperty) getById("diameter")).hasUpperBound()
				|| ((ModelProperty) getById("coronenoid")).hasUpperBound()
				|| ((RectangleProperty) getById("rectangle")).hasUpperBounds();
	}
	public int computeHexagonNumberUpperBound() {
		int upperBound = Integer.MAX_VALUE;
		for(Property property : getPropertyList()) {
			ModelProperty modelProperty = (ModelProperty) property;
			if(modelProperty.hasExpressions()) {
				int bound = modelProperty.computeHexagonNumberUpperBound();
				upperBound = Math.min(upperBound, bound);
			}
		}
		hexagonNumberUpperBound =  upperBound;
		return upperBound;
	}

	int getHexagonNumberUpperBound() {
		return hexagonNumberUpperBound;
	}

	public int computeNbCrowns() {
		int nbCrowns = Integer.MAX_VALUE;
		for(Property property : getPropertyList()) {
			if(this.has(property.getId())) {
				int bound = ((ModelProperty) property).computeNbCrowns();
				//System.out.println(property.getId() + " " + bound);
				nbCrowns = Math.min(nbCrowns, bound);
			}
		}
		System.out.println("crowns : " + nbCrowns);
		return nbCrowns;
	}

	private void clearPropertyExpressions() {
		for(Property property : getPropertyList())
			property.clearExpressions();
		
	}

	public boolean symmetryConstraintsAppliable() {
		return false;// TODO this.has("rectangle");
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
