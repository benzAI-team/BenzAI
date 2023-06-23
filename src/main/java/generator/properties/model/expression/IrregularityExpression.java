package generator.properties.model.expression;

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

	@Override
	public String toString() {
		return getId() + " " + getParameter() + " " + getOperator() + " " + getValue();
	}

	public static IrregularityExpression from(String string) {
		String [] elements = string.split(" ");
		return new IrregularityExpression(elements[0], elements[1], elements[2], Integer.valueOf(elements[3]));
	}
}
