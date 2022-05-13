package generator.criterions;

import java.util.ArrayList;
import java.util.Map;

import utils.Couple;

public class RhombusGeneratorCriterion extends GeneratorCriterion2 {

	private ArrayList<Operator> dimensionsOperators;
	private ArrayList<String> dimensionsValues;

	public RhombusGeneratorCriterion(Operator operator, String value) {
		super(operator, value);
	}

	@Override
	public int optimizeNbHexagons() {

		int dimension = getUpperBoundDimension();

		if (dimension != -1)
			return dimension * dimension;

		return -1;
	}

	@Override
	public int optimizeNbCrowns(int upperBoundNbHexagons) {
		return -1;
	}

	public int getNbDimensionsCriterions() {
		return dimensionsOperators.size();
	}

	public void addDimensionCriterion(Operator lineOperator, String lineValue) {
		dimensionsOperators.add(lineOperator);
		dimensionsValues.add(lineValue);
	}

	public Couple<Operator, String> getDimensionCriterion(int index) {
		return new Couple<Operator, String>(dimensionsOperators.get(index), dimensionsValues.get(index));
	}

	private int getUpperBoundDimension() {

		int upperBoundDimension = -1;

		for (int i = 0; i < getNbDimensionsCriterions(); i++) {
			Couple<Operator, String> criterion = getDimensionCriterion(i);

			if (criterion.getX() == Operator.EQ || criterion.getX() == Operator.LEQ
					|| criterion.getX() == Operator.LT) {

				int dimension = Integer.parseInt(criterion.getY());

				if (criterion.getX() == Operator.LT)
					dimension--;

				if (dimension > upperBoundDimension)
					upperBoundDimension = dimension;
			}
		}

		return upperBoundDimension;
	}

	@Override
	public void buildMap(Map<String, ArrayList<GeneratorCriterion2>> criterionsMap) {
		if (criterionsMap.get("rhombus") == null)
			criterionsMap.put("rhombus", new ArrayList<>());
		criterionsMap.get("rhombus").add(this);
	}
}
