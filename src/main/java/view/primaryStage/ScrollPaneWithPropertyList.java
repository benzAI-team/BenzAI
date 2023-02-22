package view.primaryStage;

import java.util.ArrayList;

import generator.GeneralModel;
import javafx.scene.control.ScrollPane;
import view.generator.ChoiceBoxCriterion;
import view.generator.boxes.HBoxCriterion;
import view.generator.boxes.HBoxModelCriterion;
import view.generator.boxes.HBoxSolverCriterion;

public abstract class ScrollPaneWithPropertyList extends ScrollPane {

	private int nbCriterions;
	private ArrayList<ChoiceBoxCriterion> choiceBoxesCriterions;
	private ArrayList<HBoxCriterion> hBoxesCriterions;

	/***
	 * 
	 * @param index
	 * @param box
	 */
	public void setHBox(int index, HBoxCriterion box) {
		getHBoxesCriterions().set(index, box);
		placeComponents();
	}

	protected abstract void placeComponents();

	/***
	 * 
	 * @param choiceBoxCriterion
	 * @param hBoxCriterion
	 */
	public void removeCriterion(ChoiceBoxCriterion choiceBoxCriterion, HBoxCriterion hBoxCriterion) {
	
		getChoiceBoxesCriterions().remove(choiceBoxCriterion);
		getHBoxesCriterions().remove(hBoxCriterion);
		setNbCriterions(getNbCriterions() - 1);
	
		for (int i = 0; i < getNbCriterions(); i++)
			getChoiceBoxesCriterions().get(i).setIndex(i);
	
		placeComponents();
	}

	/***
	 * 
	 * @return
	 */
	protected boolean buildPropertyExpressions() {
		for (HBoxCriterion box : getHBoxesCriterions()) {
			if (!box.isValid())
				return false;
			if(box instanceof HBoxModelCriterion)
				((HBoxModelCriterion)box).addPropertyExpression(GeneralModel.getModelPropertySet());
		}
		return true;
	}

	/***
	 * getters, setters
	 */
	public ArrayList<HBoxCriterion> getHBoxesCriterions() {
		return hBoxesCriterions;
	}

	public abstract void refreshGenerationPossibility();

	public int getNbCriterions() {
		return nbCriterions;
	}

	public void setNbCriterions(int nbCriterions) {
		this.nbCriterions = nbCriterions;
	}

	public ArrayList<ChoiceBoxCriterion> getChoiceBoxesCriterions() {
		return choiceBoxesCriterions;
	}

	public void setChoiceBoxesCriterions(ArrayList<ChoiceBoxCriterion> choiceBoxesCriterions) {
		this.choiceBoxesCriterions = choiceBoxesCriterions;
	}

	public void setHBoxesCriterions(ArrayList<HBoxCriterion> hBoxesCriterions) {
		this.hBoxesCriterions = hBoxesCriterions;
	}

}
