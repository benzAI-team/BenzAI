package generator.criterions;

import java.util.ArrayList;
import java.util.Map;

import utils.Couple;

public class CoronenoidGeneratorCriterion extends GeneratorCriterion2 {

	private ArrayList<Operator> crownsOperators;
	private ArrayList<String> crownsValues;

	public CoronenoidGeneratorCriterion() {
		super(Operator.NONE, "");
		initializeLists();
	}

	private void initializeLists() {
		crownsOperators = new ArrayList<>();
		crownsValues = new ArrayList<>();
	}

	@Override
	public int optimizeNbHexagons() {
		return -1;
	}

	@Override
	public int optimizeNbCrowns(int upperBoundNbHexagons) {

		int upperBoundNbCrowns = getUpperBoundNbCrowns();

		if (upperBoundNbCrowns != -1)
			return (int) (6.0 * (((double) upperBoundNbCrowns * ((double) upperBoundNbCrowns - 1.0)) / 2.0) + 1.0);

		return -1;
	}

	public int getNbCrownsCriterions() {
		return crownsOperators.size();
	}

	public void addCrownsCriterion(Operator crownsOperator, String crownsValue) {
		crownsOperators.add(crownsOperator);
		crownsValues.add(crownsValue);
	}

	public Couple<Operator, String> getCrownsCriterion(int index) {
		return new Couple<Operator, String>(crownsOperators.get(index), crownsValues.get(index));
	}

	private int getUpperBoundNbCrowns() {

		int upperBoundNbCrowns = -1;

		for (int i = 0; i < getNbCrownsCriterions(); i++) {
			Couple<Operator, String> criterion = getCrownsCriterion(i);

			if (criterion.getX() == Operator.EQ || criterion.getX() == Operator.LEQ
					|| criterion.getX() == Operator.LT) {

				int nbCrowns = Integer.parseInt(criterion.getY());

				if (criterion.getX() == Operator.LT)
					nbCrowns--;

				if (nbCrowns > upperBoundNbCrowns)
					upperBoundNbCrowns = nbCrowns;
			}
		}

		return upperBoundNbCrowns;
	}

	@Override
	public void buildMap(Map<String, ArrayList<GeneratorCriterion2>> criterionsMap) {
		if (criterionsMap.get("coronenoid") == null)
			criterionsMap.put("coronenoid", new ArrayList<>());
		criterionsMap.get("coronenoid").add(this);
	}

}
