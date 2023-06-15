package generator.properties.model;

import generator.properties.Property;
import generator.properties.PropertySet;
import view.generator.boxes.HBoxCriterion;
import view.generator.boxes.HBoxModelCriterion;
import generator.properties.model.expression.PropertyExpressionFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.regex.Pattern;

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
				|| ((RhombusProperty) getById("rhombus")).hasUpperBounds()
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
	public void buildModelPropertySet(ArrayList<HBoxCriterion> hBoxesCriterions) {
		clearPropertyExpressions();
		for (HBoxCriterion box : hBoxesCriterions) {
			if (!box.isValid())
				return;
			((HBoxModelCriterion)box).addPropertyExpression(this);
		}
		save();
	}

	@Override
	public String toString() {
		String string = "";
		for(Property property : this){
			string = string + ((ModelProperty)property).toString();
		}
		return string;
	}

	/***
	 * save the constraint set to file 'constraints'
	 */
	public void save() {
		File file = new File("constraints");

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(this.toString());
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void load() throws IOException {
		File file = new File("constraints");
		if (file.exists()) {
			clearPropertyExpressions();
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			while (line != null) {
				String[] elements = line.split(Pattern.quote(" "));
				Property property = getById(elements[0]);
				if(property != null)
					property.addExpression(PropertyExpressionFactory.build(line));
				line = reader.readLine();
			}
			reader.close();
		}
	}
}
