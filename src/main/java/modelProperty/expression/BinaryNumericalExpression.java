package modelProperty.expression;


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
		return (getOperator() == "<" || getOperator() == "=" | getOperator() == "<=") && value >= 0;
	}

	public boolean hasLowerBound() {
		return (getOperator() == ">" || getOperator() == "=" | getOperator() == ">=");
	}


}
