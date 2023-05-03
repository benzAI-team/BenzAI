package database;

public class BenzenoidCriterion {

	public enum Subject {
		ID_MOLECULE, MOLECULE_NAME, NB_HEXAGONS, NB_CARBONS, NB_HYDROGENS, IRREGULARITY, FREQUENCY, INTENSITY
	}

	public enum Operator {
		LEQ, LT, EQ, GT, GEQ, DIFF, IN
	}

	private final Subject subject;
	private final Operator operator;
	private final String value;

	public BenzenoidCriterion(Subject subject, Operator operator, String value) {
		super();
		this.subject = subject;
		this.operator = operator;
		this.value = value;
	}

	public Subject getSubject() {
		return subject;
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

	public String getOperatorStringURL() {
		switch (operator) {

		case LEQ:
			return "leq";

		case LT:
			return "lt";

		case EQ:
			return "=";

		case GT:
			return "gt";

		case GEQ:
			return "geq";

		case DIFF:
			return "dif";

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
		return subject.toString() + " " + getOperatorString() + " " + value;
	}
}
