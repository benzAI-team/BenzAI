package properties.expression;

import generator.patterns.PatternResolutionInformations;

public class PatternExpression extends SubjectExpression {
	private final PatternResolutionInformations patternsInformations;

	public PatternExpression(String subject, PatternResolutionInformations patternsInformations) {
		super(subject);
		this.patternsInformations = patternsInformations;
	}

	public PatternResolutionInformations getPatternsInformations() {
		return patternsInformations;
	}

	@Override
	public String toString() {
		return super.toString() + " " + patternsInformations;
	}

	public static PatternExpression from(String string){
		String [] elements = string.split(" ");
		return new PatternExpression(elements[0], PatternResolutionInformations.fromString(elements[1]));
	}

}
