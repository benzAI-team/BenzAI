package generator.properties.model.expression;

public class RhombusExpression extends RectangleExpression {
    public RhombusExpression(String id, String heightOperator, int height) {
        super(id, heightOperator, height, heightOperator, height);
    }

    @Override
    public String toString() {
        return getId() + " " + getHeightOperator() + " " + getHeight();
    }

    public static RhombusExpression from(String string){
        String [] elements = string.split(" ");
        return new RhombusExpression(elements[0], elements[1], Integer.parseInt(elements[2]));
    }

}
