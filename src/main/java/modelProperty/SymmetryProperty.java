package modelProperty;

import modelProperty.expression.ParameterizedExpression;
import modelProperty.testers.SymmetryTester;
import modules.SymmetriesModule;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.generator.boxes.HBoxModelCriterion;
import view.generator.boxes.HBoxSymmetriesCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class SymmetryProperty extends ModelProperty {

	public SymmetryProperty() {
		super("symmetry", "Symmetries", new SymmetriesModule(), new SymmetryTester());
	}
	
	@Override
	public int computeNbCrowns() {
		switch(((ParameterizedExpression)this.getExpressions().get(0)).getOperator()) {
		case "C_6h \"(face)-60-rotation\"" : 
		case "D_6h \"(vertex)-60-rotation+(edge)-mirror\"" : 
			return (((ModelPropertySet) this.getPropertySet()).getHexagonNumberUpperBound() + 10) / 6;
			
		case "C_3h(i) \"face-120-rotation\"":			
		case "C_3h(ii) \"vertex-120-rotation\"" : 
		case "D_3h(ii) \"vertex-120-rotation+(edge)-mirror\"" :
		case "D_3h(ia) \"face-120-rotation+face-mirror\"" :
		case "D_3h(ib) \"face-120-rotation+edge-mirror\"" :
			return (((ModelPropertySet) this.getPropertySet()).getHexagonNumberUpperBound() + 4) / 3;

		default:
			return super.computeNbCrowns();
		}
	}

	@Override
	public HBoxModelCriterion getHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxSymmetriesCriterion(parent, choiceBoxCriterion);
	}
}
