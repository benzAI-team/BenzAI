package generator.properties.model.expression;

public class BinaryNumericalExpression extends ParameterizedExpression {
	private int value;

	public BinaryNumericalExpression(String subject, String operator, int value) {
		super(subject, operator);
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public boolean hasUpperBound() {
		return isUpperBoundingOperator(getOperator()) && value >= 0;
	}

	public boolean hasLowerBound() {
		return isLowerBoundingOperator(getOperator());
	}

	public boolean test(int x, String operator, int y) {
		switch(operator) {
		case "<=": return x <= y;
		case "<" : return x < y;
		case "=" : return x == y;
		case "!=": return x != y;
		case ">" : return x > y;
		case ">=": return x >= y;
		}
		return true;

	}

	public boolean test(double x, String operator, int y) {
		switch(operator) {
		case "<=": return x <= y;
		case "<" : return x < y;
		case "=" : return x == y;
		case "!=": return x != y;
		case ">" : return x > y;
		case ">=": return x >= y;
		}
		return true;

	}

}
