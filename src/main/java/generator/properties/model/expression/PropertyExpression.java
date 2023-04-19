package generator.properties.model.expression;

public abstract class PropertyExpression {
	private String id;

	
	public PropertyExpression(String id) {
		super();
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean hasUpperBound() {
		return false;
	}
	
	

}
