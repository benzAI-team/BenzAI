package properties;

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

	public boolean symmetryConstraintsAppliable() {
		return false;// TODO this.has("rectangle");
	}

	@Override
	public String toString() {
		String string = "";
		for(Property property : getPropertyList()){
			string = string + property.toString();
		}
		return string;
	}

}
