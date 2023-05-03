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

	public boolean isUpperBoundingOperator(String operator){
		return "<".equals(operator) || "<=".equals(operator) || "=".equals(operator);
	}

	public boolean isLowerBoundingOperator(String operator){
		return ">".equals(operator) || ">=".equals(operator) || "=".equals(operator);
	}



}
