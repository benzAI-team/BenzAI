package view.primaryStage;

import generator.properties.model.ModelPropertySet;
import javafx.scene.control.ScrollPane;
import view.generator.ChoiceBoxCriterion;
import view.generator.boxes.HBoxCriterion;
import view.generator.boxes.HBoxModelCriterion;

import java.util.ArrayList;

public abstract class ScrollPaneWithPropertyList extends ScrollPane {

	private int nbCriterions;
	private ArrayList<ChoiceBoxCriterion> choiceBoxCriterions;
	private ArrayList<HBoxCriterion> hBoxCriterions;
	private final ModelPropertySet modelPropertySet = new ModelPropertySet();

	/***
	 *
	 */
	public void setHBox(int index, HBoxCriterion box) {
		getHBoxesCriterions().set(index, box);
		placeComponents();
	}

	protected abstract void placeComponents();

	/***
	 *
	 */
	public void removeCriterion(ChoiceBoxCriterion choiceBoxCriterion, HBoxCriterion hBoxCriterion) {
		getChoiceBoxCriterions().remove(choiceBoxCriterion);
		getHBoxesCriterions().remove(hBoxCriterion);
		setNbCriterions(getNbCriterions() - 1);
		for (int i = 0; i < getNbCriterions(); i++)
			getChoiceBoxCriterions().get(i).setIndex(i);
		placeComponents();
	}

	/***
	 *
	 */
	protected boolean buildPropertyExpressions() {
		for (HBoxCriterion box : getHBoxesCriterions()) {
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
	public ArrayList<HBoxCriterion> getHBoxesCriterions() {
		return hBoxCriterions;
	}

	public abstract void refreshGenerationPossibility();

	protected int getNbCriterions() {
		return nbCriterions;
	}

	protected void setNbCriterions(int nbCriterions) {
		this.nbCriterions = nbCriterions;
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
