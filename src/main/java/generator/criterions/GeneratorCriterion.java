package generator.criterions;

import java.util.ArrayList;
import java.util.Map;

public abstract class GeneratorCriterion {

//	public enum Operator {
//		LEQ, LT, EQ, GT, GEQ, DIFF, EVEN, ODD, MIN, MAX, NONE
//	}

	private String operator;
	private String value;

	public GeneratorCriterion(String operator, String value) {

		this.operator = operator;
		this.value = value;
	}

	public abstract int optimizeNbHexagons();

	public abstract int optimizeNbCrowns(int upperBoundNbHexagons);

	//public abstract void buildMap(Map<String, ArrayList<GeneratorCriterion>> criterionsMap);

	public boolean isUpperBound() {
		return operator == "<=" || operator == "<" || operator == "=";
	}
}
