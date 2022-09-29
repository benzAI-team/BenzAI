package modelProperty;

import java.util.ArrayList;
import java.util.Iterator;

import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.generator.boxes.HBoxCriterion;

public class ModelPropertySet implements Iterable<ModelProperty> {
	private ArrayList<ModelProperty> modelPropertyList;
	private int hexagonNumberUpperBound;
	
	
	public ModelPropertySet() {
		super();
		modelPropertyList = new ArrayList<ModelProperty>();
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
		add(new FragmentProperty());
	}

	public void add(ModelProperty modelProperty) {
		modelPropertyList.add(modelProperty);
		modelProperty.setModelPropertySet(this);
	}

	/***
	 * 
	 * @param id
	 * @return the model property with 'id'
	 */
	public ModelProperty getById(String id) {
		for(ModelProperty modelProperty : modelPropertyList)
			if(modelProperty.getId() == id)
				return modelProperty;
		return null;
	}
	
	/***
	 * 
	 * @param name
	 * @return
	 */
	private ModelProperty getByName(String name) {
		for(ModelProperty modelProperty : modelPropertyList)
			if(modelProperty.getName() == name)
				return modelProperty;
		return null;
	}

	/***
	 * 
	 * @param id
	 * @return true if the set contains a model property for this id
	 */
	public boolean has(String id) {
		ModelProperty modelProperty = getById(id);
		return modelProperty != null && modelProperty.getExpressions().size() > 0;
	}
	
	@Override
	public Iterator<ModelProperty> iterator() {
		return modelPropertyList.iterator();
	}

	public HBoxCriterion getHBoxCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion, String name) {
		ModelProperty modelProperty = getByName(name);
		return modelProperty.getHBoxCriterion(parent, choiceBoxCriterion);
	}

	public Object[] getNames() {
		return (Object[]) modelPropertyList.stream().map(x -> x.getName()).toArray();
	}
	
	public Object[] getIds() {
		return (Object[]) modelPropertyList.stream().map(x -> x.getId()).toArray();
	}

	public int computeHexagonNumberUpperBound() {
		int upperBound = Integer.MAX_VALUE;
		for(ModelProperty modelProperty : modelPropertyList) {
			int bound = modelProperty.computeHexagonNumberUpperBound();
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
		for(ModelProperty modelProperty : modelPropertyList) {
			if(this.has(modelProperty.getId())) {
				int bound = modelProperty.computeNbCrowns();
				System.out.println(modelProperty.getId() + " " + bound);
				nbCrowns = nbCrowns < bound ? nbCrowns : bound;
			}
		}
		System.out.println("crowns : " + nbCrowns);
		return nbCrowns;
	}

	public void clearPropertyExpressions() {
		for(ModelProperty modelProperty : modelPropertyList)
			modelProperty.clearExpressions();
		
	}

	public boolean symmetryConstraintsAppliable() {
		return this.has("rectangle");
	}
	

}
