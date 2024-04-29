package properties.expression;

public class SubjectExpression extends PropertyExpression {

	public SubjectExpression(String subject) {
		super(subject);
	}

	public static SubjectExpression from(String string){
		return new SubjectExpression(string);
	}

}
