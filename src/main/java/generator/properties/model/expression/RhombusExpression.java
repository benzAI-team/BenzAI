package generator.properties.model.expression;

public class RhombusExpression extends RectangleExpression {
    public RhombusExpression(String id, String heightOperator, int height) {
        super(id, heightOperator, height, heightOperator, height);
    }
}
