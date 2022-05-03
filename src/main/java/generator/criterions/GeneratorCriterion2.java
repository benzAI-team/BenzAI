package generator.criterions;

public abstract class GeneratorCriterion2 {

	public enum Operator {
		LEQ, LT, EQ, GT, GEQ, DIFF, EVEN, ODD, NONE
	}

	protected Operator operator;
	protected String value;

	public GeneratorCriterion2(Operator operator, String value) {

		this.operator = operator;
		this.value = value;
	}

	public abstract int optimizeNbHexagons();

	public abstract int optimizeNbCrowns(int upperBoundNbHexagons);

	public boolean isUpperBound() {
		return operator == Operator.LEQ || operator == Operator.LT || operator == Operator.EQ;
	}
}
