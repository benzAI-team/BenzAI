package generator.properties.model;

import constraints.RectangleConstraint2;
import generator.properties.model.expression.PropertyExpression;
import generator.properties.model.expression.RectangleExpression;
import generator.properties.model.filters.RectangleFilter;
import view.generator.ChoiceBoxCriterion;
import view.generator.boxes.HBoxModelCriterion;
import view.generator.boxes.HBoxRectangleCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

import java.util.ArrayList;

public class RectangleProperty extends ModelProperty {

	public RectangleProperty() {
		super("rectangle", "Rectangle", new RectangleConstraint2(), new RectangleFilter());
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
				widthBound = Math.min(widthBound, bound);
			}
		int heightBound = Integer.MAX_VALUE;
		for(PropertyExpression expression : expressions)
			if(((RectangleExpression)expression).hasHeightUpperBound()) {
				int bound = ((RectangleExpression)expression).getHeight();
				heightBound = Math.min(heightBound, bound);
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
