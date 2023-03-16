package generator.patterns;

import java.util.ArrayList;

public class PatternResolutionInformations {

	private PatternGenerationType type;
	private ArrayList<Pattern> patterns;
	private PatternsInterraction interraction;
	private boolean enabled;
	
	public PatternResolutionInformations(PatternGenerationType type, ArrayList<Pattern> patterns) {
		this.type = type;
		this.patterns = patterns;
		enabled = true;
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

	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public PatternsInterraction getInterraction() {
		return interraction;
	}
}
