package generator.properties.model.expression;

import java.util.regex.Pattern;

public enum PropertyExpressionFactory {
    ;

    public static PropertyExpression build(String string){
        String[] elements = string.split(Pattern.quote(" "));
        switch(elements[0]){
            case "hexagons":
            case "carbons":
            case "hydrogens":
            case "coronenoid":
            case "coronoid":
            case "diameter":
            case "kekule":
                return BinaryNumericalExpression.fromString(string);
            case "irregularity":
                return IrregularityExpression.fromString(string);
            case "pattern":
                return PatternExpression.fromString(string);
            case "rectangle":
                return RectangleExpression.fromString(string);
            case "rhombus":
                return RhombusExpression.fromString(string);
            case "catacondensed":
            case "concealed":
                return SubjectExpression.fromString(string);
            case "symmetry":
                return ParameterizedExpression.fromString(string);
            default:
                return null;
        }
    }
}
