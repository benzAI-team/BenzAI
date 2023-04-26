package generator.properties.model.expression;

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
}
