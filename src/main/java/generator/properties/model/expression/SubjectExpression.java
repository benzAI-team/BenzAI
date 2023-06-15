package generator.properties.model.expression;

public class SubjectExpression extends PropertyExpression {

	public SubjectExpression(String subject) {
		super(subject);
	}

	public static SubjectExpression fromString(String string){
		return new SubjectExpression(string);
	}

}
