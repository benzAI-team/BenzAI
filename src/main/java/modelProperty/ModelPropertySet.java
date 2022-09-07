package modelProperty;

import java.util.ArrayList;
import java.util.Iterator;

import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.generator.boxes.HBoxCriterion;

public class ModelPropertySet implements Iterable<ModelProperty> {
	private ArrayList<ModelProperty> modelPropertyList;
	
	
	public ModelPropertySet() {
		super();
		modelPropertyList = new ArrayList<ModelProperty>();
		modelPropertyList.add(new HexagonNumberProperty());
		modelPropertyList.add(new CarbonNumberProperty());
		modelPropertyList.add(new HydrogenNumberProperty());
		modelPropertyList.add(new CatacondensedProperty());
		modelPropertyList.add(new CoronenoidProperty());
		modelPropertyList.add(new CoronoidProperty());
		modelPropertyList.add(new DiameterProperty());
		modelPropertyList.add(new IrregularityProperty());
		modelPropertyList.add(new RectangleProperty());
		modelPropertyList.add(new RhombusProperty());
		modelPropertyList.add(new SymmetryProperty());
		modelPropertyList.add(new FragmentProperty());
	}

	public void add(ModelProperty modelProperty) {
		modelPropertyList.add(modelProperty);
	}

	/***
	 * 
	 * @param subject
	 * @return the model property with 'subject'
	 */
	public ModelProperty getBySubject(String subject) {
		for(ModelProperty modelProperty : modelPropertyList)
			if(modelProperty.getSubject() == subject)
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
	 * @param subject
	 * @return true if the set contains a model property for this subject
	 */
	public boolean has(String subject) {
		ModelProperty modelProperty = getBySubject(subject);
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
	
	public Object[] getSubjects() {
		return (Object[]) modelPropertyList.stream().map(x -> x.getSubject()).toArray();
	}

	public int computeHexagonNumberUpperBound() {
		int upperBound = Integer.MAX_VALUE;
		for(ModelProperty modelProperty : modelPropertyList) {
			int bound = modelProperty.computeHexagonNumberUpperBound();
			upperBound = upperBound < bound ? upperBound : bound;
		}
		return upperBound;
	}

	public int computeNbCrowns() {
		int nbCrowns = Integer.MAX_VALUE;
		for(ModelProperty modelProperty : modelPropertyList) {
			int bound = modelProperty.computeNbCrowns();
			nbCrowns = nbCrowns < bound ? nbCrowns : bound;
		}
		return nbCrowns;
	}

	public void clearPropertyExpressions() {
		for(ModelProperty modelProperty : modelPropertyList)
			modelProperty.clearExpressions();
		
	}
	

}
