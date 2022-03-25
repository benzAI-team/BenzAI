package solving_modes;

public class ParameterIrregularity {

	public enum Subject { XI, N0, N1, N2, N3, N4 }
	public enum Operator{L, LEQ, EQ, GEQ, G}
	
	private Subject subject;
	private Operator operator;
	private int value;
	
	public ParameterIrregularity(Subject subject, Operator operator, int value) {
		this.subject = subject;
		this.operator = operator;
		this.value = value;
	}
	
	public Subject getSubject() {
		return subject;
	}
	
	public Operator getOperator() {
		return operator;
	}
	
	public int getValue() {
		return value;
	}
	
	public String operatorToString() {
		
		switch (operator) {
		
			case L:
				return "<";
				
			case LEQ:
				return "<=";
				
			case EQ:
				return "=";
				
			case GEQ:
				return ">=";
				
			case G:
				return ">";
				
			default:
				return null;
		}
	}
	
	@Override
	public String toString() {
		return subject.toString() + " " + operatorToString() + " " + value;
	}
}
