package modelProperty.expression;

public class IrregularityExpression extends BinaryNumericalExpression {

	private String parameter;

	public IrregularityExpression(String subject, String parameter, String operator, int value) {
		super(subject, operator, value);
		this.parameter = parameter;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
}
