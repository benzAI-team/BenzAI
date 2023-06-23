package generator.properties.model.expression;

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
		return isUpperBoundingOperator(heightOperator) && height >= 0;
	}
	
	public boolean hasWidthUpperBound() {
		return isUpperBoundingOperator(widthOperator) && width >= 0;
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

	@Override
	public String toString() {
		return super.toString() + " " + heightOperator + " " + height + " " + widthOperator + " " + width;
	}

	static RectangleExpression from(String string){
		String [] elements = string.split(" ");
		return new RectangleExpression(elements[0], elements[1], Integer.parseInt(elements[2]), elements[3], Integer.parseInt(elements[4]));
	}
}
