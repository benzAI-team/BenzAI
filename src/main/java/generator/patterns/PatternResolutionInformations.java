package generator.patterns;

import java.util.ArrayList;

public class PatternResolutionInformations {

	private final PatternGenerationType type;
	private final ArrayList<Pattern> patterns;
	private PatternsInterraction interraction;

	public PatternResolutionInformations(PatternGenerationType type, ArrayList<Pattern> patterns) {
		this.type = type;
		this.patterns = patterns;
	}

	public void setInterraction(PatternsInterraction interraction) {
		this.interraction = interraction;
	}
	
	public PatternGenerationType getType() {
		return type;
	}

	public ArrayList<Pattern> getPatterns() {
		return patterns;
	}

	public PatternsInterraction getInterraction() {
		return interraction;
	}
}
