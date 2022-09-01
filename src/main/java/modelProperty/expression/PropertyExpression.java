package modelProperty.expression;

public abstract class PropertyExpression {
	private String subject;

	
	public PropertyExpression(String subject) {
		super();
		this.subject = subject;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public boolean hasUpperBound() {
		return false;
	}
	
	

}
