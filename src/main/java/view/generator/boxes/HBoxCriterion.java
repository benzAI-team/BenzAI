package view.generator.boxes;

import java.util.ArrayList;

import generator.GeneratorCriterion;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;

@SuppressWarnings("unused")
public abstract class HBoxCriterion extends HBox {

	protected boolean valid;
	
	protected DeleteButton deleteButton;
	protected ImageView warningIcon;
	 
	protected GeneratorPane parent;
	private ChoiceBoxCriterion choiceBoxCriterion;
	
	public HBoxCriterion(GeneratorPane parent, ChoiceBoxCriterion choiceBoxCriterion) {
		super(5.0);
		
		this.parent = parent;
		this.choiceBoxCriterion = choiceBoxCriterion;
		
		warningIcon = new ImageView(new Image("/resources/graphics/icon-warning.png"));
		deleteButton = new DeleteButton(this);
		
		Tooltip.install(warningIcon, new Tooltip("Invalid entry, criterion will not be considered"));
		Tooltip.install(deleteButton, new Tooltip("Delete criterion"));
		
		deleteButton.setOnAction(e -> {
			parent.removeCriterion(choiceBoxCriterion, this);
		});
		
		initialize();
	}
	
	protected abstract void checkValidity();
	protected abstract void initialize();
	
	public abstract ArrayList<GeneratorCriterion> buildCriterions();
	
	public boolean isValid() {
		return valid;
	}
	

}
