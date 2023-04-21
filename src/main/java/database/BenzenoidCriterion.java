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

		if (operatorString.equals("<="))
			return Operator.LEQ;

		else if (operatorString.equals("<"))
			return Operator.LT;

		else if (operatorString.equals("="))
			return Operator.EQ;

		else if (operatorString.equals(">"))
			return Operator.GT;

		else if (operatorString.equals(">="))
			return Operator.GEQ;

		else if (operatorString.equals("!=") || operatorString.equals("<>"))
			return Operator.DIFF;

		else if (operatorString.equalsIgnoreCase("IN"))
			return Operator.IN;

		else
			return null;
	}

	@Override
	public String toString() {
		return subject.toString() + " " + getOperatorString() + " " + value;
	}
}
