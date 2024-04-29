package properties.expression;

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
                return BinaryNumericalExpression.from(string);
            case "irregularity":
                return IrregularityExpression.from(string);
            case "pattern":
                return PatternExpression.from(string);
            case "rectangle":
                return RectangleExpression.from(string);
            case "rhombus":
                return RhombusExpression.from(string);
            case "catacondensed":
            case "concealed":
                return SubjectExpression.from(string);
            case "symmetry":
                return ParameterizedExpression.from(string);
            default:
                return null;
        }
    }
}
