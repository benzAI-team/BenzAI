package generator.criterions;

import java.util.ArrayList;
import java.util.Map;

import utils.Couple;

public class CoronoidGeneratorCriterion extends GeneratorCriterion {

	private ArrayList<Operator> holesOperators;
	private ArrayList<String> holesValues;

	public CoronoidGeneratorCriterion() {
		super(Operator.NONE, "");
		initializeLists();
	}

	private void initializeLists() {
		holesOperators = new ArrayList<>();
		holesValues = new ArrayList<>();
	}

	public int getNbHolesCriterions() {
		return holesOperators.size();
	}

	public void addHoleCriterion(Operator operator, String value) {
		holesOperators.add(operator);
		holesValues.add(value);
	}

	public Couple<Operator, String> getHoleCriterion(int index) {
		return new Couple<Operator, String>(holesOperators.get(index), holesValues.get(index));
	}

	@Override
	public int optimizeNbHexagons() {
		return -1;
	}

	@Override
	public int optimizeNbCrowns(int upperBoundNbHexagons) {
		int nbCrowns = -1;

		int upperBoundNbHoles = getUpperBoundNbHoles();
		if (upperBoundNbHoles != -1) {
			if (upperBoundNbHexagons > 4 * upperBoundNbHoles)
				nbCrowns = (upperBoundNbHexagons + 2 - 4 * upperBoundNbHoles) / 2;
			else
				nbCrowns = 1;
		}

		return nbCrowns;
	}

	private int getUpperBoundNbHoles() {

		int upperBoundNbHoles = -1;

		for (int i = 0; i < getNbHolesCriterions(); i++) {
			Couple<Operator, String> criterion = getHoleCriterion(i);

			if (criterion.getX() == Operator.EQ || criterion.getX() == Operator.LEQ
					|| criterion.getX() == Operator.LT) {

				int nbHoles = Integer.parseInt(criterion.getY());

				if (criterion.getX() == Operator.LT)
					nbHoles--;

				if (nbHoles > upperBoundNbHoles)
					upperBoundNbHoles = nbHoles;
			}
		}

		return upperBoundNbHoles;
	}

	@Override
	public void buildMap(Map<String, ArrayList<GeneratorCriterion>> criterionsMap) {
		if (criterionsMap.get("coronoid") == null)
			criterionsMap.put("coronoid", new ArrayList<>());
		criterionsMap.get("coronoid").add(this);
	}

}
