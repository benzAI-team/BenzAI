package modelProperty;

import java.util.ArrayList;

import modelProperty.expression.PropertyExpression;
import modelProperty.expression.RectangleExpression;
import modelProperty.testers.RectangleTester;
import modules.RectangleModule;
import view.generator.ChoiceBoxCriterion;
import view.generator.GeneratorPane;
import view.generator.boxes.HBoxModelCriterion;
import view.generator.boxes.HBoxRectangleCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

public class RectangleProperty extends ModelProperty {

	public RectangleProperty() {
		super("rectangle", "Rectangle", new RectangleModule(), new RectangleTester());
	}

	@Override
	public HBoxModelCriterion getHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxRectangleCriterion(parent, choiceBoxCriterion);
	}

	@Override
	public int computeHexagonNumberUpperBound() {
		ArrayList<PropertyExpression> expressions = this.getExpressions();
		int widthBound = Integer.MAX_VALUE;
		for(PropertyExpression expression : expressions)
			if(((RectangleExpression)expression).hasWidthUpperBound()) {
				int bound = ((RectangleExpression)expression).getWidth();
				widthBound = widthBound < bound ? widthBound : bound;
			}
		int heightBound = Integer.MAX_VALUE;
		for(PropertyExpression expression : expressions)
			if(((RectangleExpression)expression).hasHeightUpperBound()) {
				int bound = ((RectangleExpression)expression).getHeight();
				heightBound = heightBound < bound ? heightBound : bound;
			}
		return widthBound != Integer.MAX_VALUE && heightBound != Integer.MAX_VALUE ? widthBound * heightBound : Integer.MAX_VALUE;
	}

	public boolean hasUpperBounds() {
		ArrayList<PropertyExpression> expressions = this.getExpressions();
		boolean hasWidthBound = false;
		for(PropertyExpression expression : expressions)
			if(((RectangleExpression)expression).hasWidthUpperBound())
				hasWidthBound = true;
		boolean hasHeightBound = false;
		for(PropertyExpression expression : expressions)
			if(((RectangleExpression)expression).hasHeightUpperBound())
				hasHeightBound = true;
		return hasWidthBound && hasHeightBound;
	}
}
