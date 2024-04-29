package view.primaryStage;

import application.BenzenoidApplication;
import application.Operation;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import properties.Property;
import properties.PropertySet;
import properties.expression.PropertyExpression;
import view.generator.ChoiceBoxCriterion;
import view.generator.boxes.HBoxCriterion;
import view.generator.boxes.HBoxDefaultCriterion;
import view.generator.boxes.HBoxModelCriterion;

import java.util.ArrayList;

public abstract class ScrollPaneWithPropertyList extends ScrollPane {

	private final BenzenoidApplication application;

	private final Operation operation;
	private ImageView loadIcon;
	private ImageView warningIcon;
	private GridPane gridPane;
	private int nbBoxCriterions;
	private ArrayList<ChoiceBoxCriterion> choiceBoxCriterions;
	private ArrayList<HBoxCriterion> hBoxCriterions;
	private final PropertySet propertySet;

	private ButtonBox buttonBox;

	public ScrollPaneWithPropertyList(PropertySet propertySet, Operation operation, BenzenoidApplication application) {
		this.propertySet = propertySet;
		this.operation = operation;
		this.application = application;
	}

	public HBoxCriterion getHBox(int index){
		return getHBoxCriterions().get(index);
	}
	public void setHBox(int index, HBoxCriterion box) {
		getHBoxCriterions().set(index, box);
		placeComponents();
	}

	/***
 *
 */
	protected void setPaneDimensions() {
		this.setFitToHeight(true);
		this.setFitToWidth(true);
		this.setPrefWidth(1400);
		this.setPrefWidth(this.getPrefWidth());
	}

	protected void buildIcons() {
		loadIcon = buildLoadIcon();
		warningIcon = buildWarningIcon();
	}

	private ImageView buildLoadIcon() {
		Image image = new Image("/resources/graphics/icon-load.gif");
		ImageView loadIcon = new ImageView(image);
		loadIcon.resize(30, 30);
		return loadIcon;
	}

	private ImageView buildWarningIcon() {
		ImageView warningIcon = new ImageView(new Image("/resources/graphics/icon-warning.png"));
		warningIcon.resize(30, 30);
		Tooltip.install(warningIcon, new Tooltip(
				"A criterion limiting the number of hexagons/carbons/hydrogens/number of lines and columns or diameter is required. Moreover, all the criterions must be valid"));
		return warningIcon;
	}

	protected void addEmptyCriterionBox() {
		ChoiceBoxCriterion choiceBoxCriterion = createAndAddChoiceBoxCriterion();
		getHBoxCriterions().add(new HBoxDefaultCriterion(this, choiceBoxCriterion));
		setNbBoxCriterions(getNbBoxCriterions() + 1);
	}

	protected boolean containsInvalidCriterion() {
		return getHBoxCriterions().stream().anyMatch(box -> !box.isValid());
	}

	/***
	 *
	 * @return gridPane
	 */
	protected GridPane buildGridPane() {
		GridPane gridPane = new GridPane();
		gridPane.setPrefWidth(1400);
		gridPane.setPadding(new Insets(50));
		gridPane.setHgap(5);
		gridPane.setVgap(5);
		return gridPane;
	}

	protected void initializeCriterionBoxes() {
		setChoiceBoxCriterions(new ArrayList<>());
		setHBoxesCriterions(new ArrayList<>());
		setNbBoxCriterions(0);

		for(Property property : getPropertySet())
			if(property.hasExpressions())
				for(PropertyExpression expression : property.getExpressions())
					addPropertyBoxPair(property, expression);
		getPropertySet().clearAllPropertyExpressions();
	}

	private void addPropertyBoxPair(Property property, PropertyExpression expression) {
		ChoiceBoxCriterion choiceBoxCriterion = createAndAddChoiceBoxCriterion();
		getHBoxCriterions().add(choiceBoxCriterion.getHBoxCriterion());
		choiceBoxCriterion.getSelectionModel().select(property.getName());
		//System.out.println(">>>" + expression);
		choiceBoxCriterion.getHBoxCriterion().assign(expression);
		setNbBoxCriterions(getNbBoxCriterions() + 1);
	}

	private ChoiceBoxCriterion createAndAddChoiceBoxCriterion() {
		ChoiceBoxCriterion choiceBoxCriterion = new ChoiceBoxCriterion(getNbBoxCriterions(), this, getPropertySet());
		getChoiceBoxCriterions().add(choiceBoxCriterion);
		return choiceBoxCriterion;
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

    protected void initEventHandlers() {
        for(HBoxCriterion box : getHBoxCriterions()){
            box.initEventHandling();
        }
    }

	protected void placeCriterionBoxes() {
		//System.out.println(getNbBoxCriterions());
		for (int propertyIndex = 0; propertyIndex < getNbBoxCriterions(); propertyIndex++) {
			placeCriterionBox(propertyIndex);
		}
	}

	private void placeCriterionBox(int propertyIndex) {
		GridPane.setValignment(getChoiceBoxCriterions().get(propertyIndex), VPos.TOP);
		gridPane.add(getChoiceBoxCriterions().get(propertyIndex), 0, propertyIndex + 1);
		gridPane.add(getHBoxCriterions().get(propertyIndex), 1, propertyIndex + 1);
		getHBoxCriterions().get(propertyIndex).updateValidity();
	}

	/***
	 *
	 */
	protected boolean buildPropertyExpressions() {
		for (HBoxCriterion box : getHBoxCriterions()) {
			if (!box.isValid())
				return false;
			if(box instanceof HBoxModelCriterion)
				((HBoxModelCriterion)box).addPropertyExpression(propertySet);
		}
		return true;
	}

	public abstract void refreshGlobalValidity();

	/***
	 * getters, setters
	 */
	public ArrayList<HBoxCriterion> getHBoxCriterions() {
		return hBoxCriterions;
	}


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

	public PropertySet getPropertySet() {
		return propertySet;
	}

	public BenzenoidApplication getApplication() {
		return application;
	}

	public ImageView getLoadIcon() {
		return loadIcon;
	}

	public ImageView getWarningIcon() {
		return warningIcon;
	}

	public GridPane getGridPane() {
		return gridPane;
	}

	public void setGridPane(GridPane gridPane) {
		this.gridPane = gridPane;
	}

	protected void pauseOperation() {
	}

	protected   void resumeOperation() {
	}

	public Operation getOperation() {
		return operation;
	}

	public ButtonBox getButtonBox() {
		return buttonBox;
	}

	public void setButtonBox(ButtonBox buttonBox) {
		this.buttonBox = buttonBox;
	}


}
