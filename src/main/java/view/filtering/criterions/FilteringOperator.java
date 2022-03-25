package view.filtering.criterions;

public enum FilteringOperator {

	LEQ, LT, EQ, NEQ, GT, GEQ, EVEN, ODD;

	public static FilteringOperator getOperator(String operatorStr) {

		if (operatorStr.equals("<="))
			return FilteringOperator.LEQ;

		else if (operatorStr.equals("<"))
			return FilteringOperator.LT;

		else if (operatorStr.equals("="))
			return FilteringOperator.EQ;

		else if (operatorStr.equals("!="))
			return FilteringOperator.NEQ;

		else if (operatorStr.equals(">"))
			return FilteringOperator.GT;

		else if (operatorStr.equals(">="))
			return FilteringOperator.GEQ;

		else if (operatorStr.equals("EVEN"))
			return FilteringOperator.EVEN;

		else if (operatorStr.equals("ODD"))
			return FilteringOperator.ODD;

		else
			return null;

	}
}
