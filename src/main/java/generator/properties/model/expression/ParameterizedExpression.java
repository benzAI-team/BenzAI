package generator.properties.model.expression;

public class ParameterizedExpression extends PropertyExpression {
	private String operator;

	public ParameterizedExpression(String subject, String operator) {
		super(subject);
		this.operator = operator;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	@Override
	public String toString() {
		return super.toString() + " " + getOperator();
	}

	public static ParameterizedExpression from(String string){
		String [] elements = string.split(" ");
		return new ParameterizedExpression(elements[0], elements[1]);
	}
}
