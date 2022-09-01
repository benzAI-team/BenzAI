package modelProperty.expression;

public class RectangleExpression extends PropertyExpression {
	private int height, width;
	private String heightOperator, widthOperator;

	
	public RectangleExpression(String subject, int height, int width, String heightOperator, String widthOperator) {
		super(subject);
		this.height = height;
		this.width = width;
		this.heightOperator = heightOperator;
		this.widthOperator = widthOperator;
	}
	
	public boolean hasHeightUpperBound() {
		return (heightOperator == "<" || heightOperator == "=" | heightOperator == "<=") && height >= 0;
	}
	
	public boolean hasWidthUpperBound() {
		return (widthOperator == "<" || widthOperator == "=" | widthOperator == "<=") && width >= 0;
	}

	/***
	 * getters
	 */
	public int getHeight() {
		return height;
	}
	public int getWidth() {
		return width;
	}
	public String getHeightOperator() {
		return heightOperator;
	}
	public String getWidthOperator() {
		return widthOperator;
	}
	
	
	
}
