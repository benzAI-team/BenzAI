package database;

public class BenzenoidCriterion {

	public enum Operator {
		LEQ, LT, EQ, GT, GEQ, DIFF, IN
	}

	private final String name;
	private final Operator operator;
	private final String value;

	public BenzenoidCriterion(String name, Operator operator, String value) {
		super();
		this.name = name;
		this.operator = operator;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public Operator getOperator() {
		return operator;
	}

	public String getOperatorString() {
		switch (operator) {

		case LEQ:
			return "<=";

		case LT:
			return "<";

		case EQ:
			return "=";

		case GT:
			return ">";

		case GEQ:
			return ">=";

		case DIFF:
			return "<>";

		case IN:
			return "IN";

		default:
			return null;
		}
	}

	public String getValue() {
		return value;
	}

	public static Operator getOperator(String operatorString) {

		if ("<=".equals(operatorString))
			return Operator.LEQ;

		else if ("<".equals(operatorString))
			return Operator.LT;

		else if ("=".equals(operatorString))
			return Operator.EQ;

		else if (">".equals(operatorString))
			return Operator.GT;

		else if (">=".equals(operatorString))
			return Operator.GEQ;

		else if ("!=".equals(operatorString) || "<>".equals(operatorString))
			return Operator.DIFF;

		else if ("IN".equalsIgnoreCase(operatorString))
			return Operator.IN;

		else
			return null;
	}

	@Override
	public String toString() {
		return name + " " + getOperatorString() + " " + value;
	}
}
