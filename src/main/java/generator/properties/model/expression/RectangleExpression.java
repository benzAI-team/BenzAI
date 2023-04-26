package generator.properties.model.expression;

import java.util.Objects;

public class RectangleExpression extends PropertyExpression {
	private final int height;
    private final int width;
	private final String heightOperator;
    private final String widthOperator;

	
	public RectangleExpression(String id, String heightOperator, int height, String widthOperator, int width) {
		super(id);
		this.height = height;
		this.width = width;
		this.heightOperator = heightOperator;
		this.widthOperator = widthOperator;
	}
	
	public boolean hasHeightUpperBound() {
		return (Objects.equals(heightOperator, "<") || Objects.equals(heightOperator, "=") | Objects.equals(heightOperator, "<=")) && height >= 0;
	}
	
	public boolean hasWidthUpperBound() {
		return (Objects.equals(widthOperator, "<") || Objects.equals(widthOperator, "=") | Objects.equals(widthOperator, "<=")) && width >= 0;
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
