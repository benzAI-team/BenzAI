package view.primaryStage;

import generator.properties.model.ModelPropertySet;
import javafx.scene.control.ScrollPane;
import view.generator.ChoiceBoxCriterion;
import view.generator.boxes.HBoxCriterion;
import view.generator.boxes.HBoxModelCriterion;

import java.util.ArrayList;

public abstract class ScrollPaneWithPropertyList extends ScrollPane {

	private int nbBoxCriterions;
	private ArrayList<ChoiceBoxCriterion> choiceBoxCriterions;
	private ArrayList<HBoxCriterion> hBoxCriterions;
	private final ModelPropertySet modelPropertySet = new ModelPropertySet();


	public HBoxCriterion getHBox(int index){
		return getHBoxCriterions().get(index);
	}
	public void setHBox(int index, HBoxCriterion box) {
		getHBoxCriterions().set(index, box);
		placeComponents();
	}

	protected abstract void placeComponents();

	/***
	 *
	 */
	public void removeCriterion(ChoiceBoxCriterion choiceBoxCriterion, HBoxCriterion hBoxCriterion) {
		getChoiceBoxCriterions().remove(choiceBoxCriterion);
		getHBoxCriterions().remove(hBoxCriterion);
		setNbBoxCriterions(getNbBoxCriterions() - 1);
		for (int i = 0; i < getNbBoxCriterions(); i++)
			getChoiceBoxCriterions().get(i).setIndex(i);
		placeComponents();
	}

	/***
	 *
	 */
	protected boolean buildPropertyExpressions() {
		for (HBoxCriterion box : getHBoxCriterions()) {
			if (!box.isValid())
				return false;
			if(box instanceof HBoxModelCriterion)
				((HBoxModelCriterion)box).addPropertyExpression(modelPropertySet);
		}
		return true;
	}

	/***
	 * getters, setters
	 */
	public ArrayList<HBoxCriterion> getHBoxCriterions() {
		return hBoxCriterions;
	}

	public abstract void refreshGenerationPossibility();

	protected int getNbBoxCriterions() {
		return nbBoxCriterions;
	}

	protected void setNbBoxCriterions(int nbBoxCriterions) {
		this.nbBoxCriterions = nbBoxCriterions;
	}

	protected ArrayList<ChoiceBoxCriterion> getChoiceBoxCriterions() {
		return choiceBoxCriterions;
	}

	protected void setChoiceBoxCriterions(ArrayList<ChoiceBoxCriterion> choiceBoxCriterions) {
		this.choiceBoxCriterions = choiceBoxCriterions;
	}

	protected void setHBoxesCriterions(ArrayList<HBoxCriterion> hBoxesCriterions) {
		this.hBoxCriterions = hBoxesCriterions;
	}

	public ModelPropertySet getModelPropertySet() {
		return modelPropertySet;
	}


}
