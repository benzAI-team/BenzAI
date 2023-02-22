package modelProperty;

import java.util.ArrayList;
import java.util.Iterator;

import generator.properties.Property;
import generator.properties.PropertySet;
import modelProperty.testers.Tester;
import molecules.Molecule;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.generator.boxes.HBoxModelCriterion;

public class ModelPropertySet extends PropertySet {
	private int hexagonNumberUpperBound;
	
	
	public ModelPropertySet() {
		super();
		setPropertyList(new ArrayList<Property>());
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
		for(Property property : getPropertyList()) {
			if(this.has(property.getId())) {
				int bound = ((ModelProperty) property).computeNbCrowns();
				System.out.println(property.getId() + " " + bound);
				nbCrowns = nbCrowns < bound ? nbCrowns : bound;
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
	
	public boolean testAll(Molecule molecule) {
		for(Property property : this.getPropertyList())
			if(property.hasExpressions()) {
				Tester t = ((ModelProperty)property).getTester();
				System.out.println(t);
				if(!((ModelProperty)property).getTester().test(molecule, property.getExpressions()))
					return false;
			}
		return true;
	}

}
