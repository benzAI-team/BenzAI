package generator.properties.model;

import constraints.RectangleConstraint;
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
		super("rectangle", "Rectangle", new RectangleConstraint(), new RectangleFilter());
	}

	@Override
	public HBoxModelCriterion getHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxRectangleCriterion(parent, choiceBoxCriterion);
	}

	@Override
	public int computeHexagonNumberUpperBound() {
		ArrayList<PropertyExpression> expressions = this.getExpressions();
		int widthBound = expressions.stream().reduce(Integer.MAX_VALUE, (x,y) -> Math.min(x, ((RectangleExpression)y).getHeight()), Math::min);
		int heightBound = expressions.stream().reduce(Integer.MAX_VALUE, (x,y) -> Math.min(x, ((RectangleExpression)y).getWidth()), Math::min);
		return widthBound != Integer.MAX_VALUE && heightBound != Integer.MAX_VALUE ? widthBound * heightBound : Integer.MAX_VALUE;
	}

	public boolean hasUpperBounds() {
		ArrayList<PropertyExpression> expressions = this.getExpressions();
		return expressions.stream().anyMatch(e -> ((RectangleExpression)e).hasHeightUpperBound())
				&& expressions.stream().anyMatch(e -> ((RectangleExpression)e).hasWidthUpperBound());
	}
}
