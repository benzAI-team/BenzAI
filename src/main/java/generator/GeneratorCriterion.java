package generator;

import java.util.List;

public class GeneratorCriterion {

//	public enum Subject {
//		NB_HEXAGONS, NB_CARBONS, NB_HYDROGENS, DIAMETER, RECT_HEIGHT, RECT_WIDTH, CORONOID, CATACONDENSED, SYMM_MIRROR,
//		SYMM_ROT_60, SYMM_ROT_120, SYMM_ROT_180, SYMM_VERTICAL, SYMM_ROT_120_V, SYMM_ROT_180_E, XI, N0, N1, N2, N3, N4,
//		VIEW_IRREG, RECTANGLE, SINGLE_PATTERN, MULTIPLE_PATTERNS, FORBIDDEN_PATTERN, OCCURENCE_PATTERN, CORONOID_2,
//		NB_HOLES, ROT_60_MIRROR, ROT_120_MIRROR_H, ROT_120_MIRROR_E, ROT_120_VERTEX_MIRROR, ROT_180_EDGE_MIRROR,
//		ROT_180_MIRROR, MIRROR_H, MIRROR_E, RHOMBUS, TIMEOUT, NB_SOLUTIONS, RHOMBUS_DIMENSION, CORONENOID, NB_CROWNS,
//		NB_KEKULE_STRUCTURES, CONCEALED
//	}

	public static boolean containsSymmetry(List<GeneratorCriterion> criterions) {

		for (GeneratorCriterion criterion : criterions) {

			String name = criterion.getName();

			if (name == "SYMM_MIRROR" || name == "SYMM_ROT_60" || name == "SYMM_ROT_120"
					|| name == "SYMM_ROT_180" || name == "SYMM_VERTICAL"
					|| name == "SYMM_ROT_120_V" || name == "SYMM_ROT_180_E"
					|| name == "ROT_60_MIRROR" || name == "ROT_120_MIRROR_H"
					|| name == "ROT_120_MIRROR_E" || name == "ROT_120_VERTEX_MIRROR"
					|| name == "ROT_180_EDGE_MIRROR" || name == "ROT_180_MIRROR"
			/* || subject == Subject.MIRROR_H || subject == Subject.MIRROR_E */)

				return true;
		}

		return false;
	}

	public static boolean containsRotation(List<GeneratorCriterion> criterions) {

		for (GeneratorCriterion criterion : criterions) {

			String name = criterion.getName();

			if (name == "SYMM_ROT_60" || name == "SYMM_ROT_120" || name == "SYMM_ROT_180"
					|| name == "SYMM_ROT_120_V" || name == "SYMM_ROT_180_E"
					|| name == "ROT_60_MIRROR" || name == "ROT_120_MIRROR_H"
					|| name == "ROT_120_MIRROR_E" || name == "ROT_120_VERTEX_MIRROR"
					|| name == "ROT_180_EDGE_MIRROR" || name == "ROT_180_MIRROR")

				return true;
		}

		return false;
	}

//	public enum Operator {
//		LEQ, LT, EQ, GT, GEQ, DIFF, EVEN, ODD, NONE, MIN, MAX
//	}

	private String name;
	private String operator;
	private int value;

	public GeneratorCriterion(String name, String operator, int value2) {

		this.name = name;
		this.operator = operator;
		this.value = value2;
	}


	public String getOperator() {
		return operator;
	}
	/***
	 * 
	 * @param operator
	 * @return true if operator allows to bound the number of crowns
	 */
	public static boolean isBoundingOperator(String operator) {
		return operator == "=" || operator == "<=" || operator == "<";
	}
	
//	public String getOperatorString() {
//		switch (operator) {
//
//		case LEQ:
//			return "<=";
//
//		case LT:
//			return "<";
//
//		case EQ:
//			return "=";
//
//		case GT:
//			return ">";
//
//		case GEQ:
//			return ">=";
//
//		case DIFF:
//			return "!=";
//
//		case NONE:
//			return "";
//
//		case EVEN:
//			return "EVEN";
//
//		case ODD:
//			return "ODD";
//
//		case MIN:
//			return "MIN";
//
//		case MAX:
//			return "MAX";
//
//		default:
//			return null;
//		}
//	}


//	public static Operator getOperator(String operatorString) {
//
//		if (operatorString.equals("<="))
//			return Operator.LEQ;
//
//		else if (operatorString.equals("<"))
//			return Operator.LT;
//
//		else if (operatorString.equals("="))
//			return Operator.EQ;
//
//		else if (operatorString.equals(">"))
//			return Operator.GT;
//
//		else if (operatorString.equals(">="))
//			return Operator.GEQ;
//
//		else if (operatorString.equals("!=") || operatorString.equals("<>"))
//			return Operator.DIFF;
//
//		else if (operatorString.equals("EVEN"))
//			return Operator.EVEN;
//
//		else if (operatorString.equals("ODD"))
//			return Operator.ODD;
//
//		else if (operatorString.equals("Min"))
//			return Operator.MIN;
//
//		else if (operatorString.equals("Max"))
//			return Operator.MAX;
//
//		else
//			return null;
//	}

	public static boolean containsSubject(List<GeneratorCriterion> criterions, String name) {

		for (GeneratorCriterion criterion : criterions) {

			if (criterion.getName().equals(name))
				return true;
		}

		return false;
	}

	public boolean isUpperBound() {
		return operator == "=" || operator == "<=" || operator == "<";
	}

	@Override
	public String toString() {
		return name + " " + operator + " " + value;
	}

	/***
	 * getters, setters
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}


}
