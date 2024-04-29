package properties;

import constraints.BenzAIConstraint;
import constraints.RectangleConstraint;
import properties.ModelProperty;
import properties.expression.PropertyExpression;
import properties.expression.RectangleExpression;
import properties.filters.Filter;
import properties.filters.RectangleFilter;
import view.generator.ChoiceBoxCriterion;
import view.generator.boxes.HBoxModelCriterion;
import view.generator.boxes.HBoxRectangleCriterion;
import view.primaryStage.ScrollPaneWithPropertyList;

import java.util.ArrayList;

public class RectangleProperty extends ModelProperty {

	RectangleProperty() {
		super("rectangle", "Rectangle", new RectangleConstraint(), new RectangleFilter());
	}

	RectangleProperty(String id, String name, BenzAIConstraint constraint, Filter filter) {
		super(id, name, constraint, filter);
	}

	@Override
	public HBoxModelCriterion makeHBoxCriterion(ScrollPaneWithPropertyList parent, ChoiceBoxCriterion choiceBoxCriterion) {
		return new HBoxRectangleCriterion(parent, choiceBoxCriterion);
	}

	@Override
	public int computeHexagonNumberUpperBound() {
		ArrayList<PropertyExpression> expressions = this.getExpressions();
		int widthBound = expressions.stream().reduce(Integer.MAX_VALUE, (acc,expression) -> Math.min(acc, ((RectangleExpression)expression).getHeight()), Math::min);
		int heightBound = expressions.stream().reduce(Integer.MAX_VALUE, (acc, expression) -> Math.min(acc, ((RectangleExpression)expression).getWidth()), Math::min);
		return Math.min(widthBound * heightBound, Integer.MAX_VALUE);
	}
	@Override
	public int computeNbCrowns(){
		ArrayList<PropertyExpression> expressions = this.getExpressions();
		int widthBound = expressions.stream().reduce(Integer.MAX_VALUE, (acc,expression) -> Math.min(acc, ((RectangleExpression)expression).getHeight()), Math::min);
		int heightBound = expressions.stream().reduce(Integer.MAX_VALUE, (acc, expression) -> Math.min(acc, ((RectangleExpression)expression).getWidth()), Math::min);
		return Math.max(widthBound, heightBound);
	}
	boolean hasUpperBounds() {
		ArrayList<PropertyExpression> expressions = this.getExpressions();
		return expressions.stream().anyMatch(expression -> ((RectangleExpression)expression).hasHeightUpperBound())
				&& expressions.stream().anyMatch(expression -> ((RectangleExpression)expression).hasWidthUpperBound());
	}
}
