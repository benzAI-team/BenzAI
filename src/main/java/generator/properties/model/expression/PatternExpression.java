package generator.properties.model.expression;


public class PatternExpression extends SubjectExpression {

	public PatternExpression(String subject) {
		super(subject);
	}

	@Override
	public String toString() {
		return super.toString() + "OKAY !!";
	}

	public static PatternExpression from(String string){
		String [] elements = string.split(" ");
		return new PatternExpression(elements[0]);
	}

}
