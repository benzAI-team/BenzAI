package generator.criterions;

import java.util.ArrayList;

import utils.Couple;

public class RectangleGeneratorCriterion extends GeneratorCriterion2 {

	private ArrayList<Operator> linesOperators;
	private ArrayList<String> linesValues;

	private ArrayList<Operator> columnsOperators;
	private ArrayList<String> columnsValues;

	public RectangleGeneratorCriterion() {
		super(Operator.NONE, "");
		initializeLists();
	}

	private void initializeLists() {
		linesOperators = new ArrayList<>();
		linesValues = new ArrayList<>();
		columnsOperators = new ArrayList<>();
		columnsValues = new ArrayList<>();
	}

	@Override
	public int optimizeNbHexagons() {

		int nbLines = getUpperBoundNbLines();
		int nbColumns = getUpperBoundNbColumns();

		if (nbLines != -1 && nbColumns != -1)
			return nbLines * nbColumns;

		return -1;
	}

	@Override
	public int optimizeNbCrowns(int upperBoundNbHexagons) {
		return -1;
	}

	public int getNbLineCriterions() {
		return linesOperators.size();
	}

	public int getNbColumnCriterions() {
		return columnsOperators.size();
	}

	public void addLineCriterion(Operator lineOperator, String lineValue) {
		linesOperators.add(lineOperator);
		linesValues.add(lineValue);
	}

	public void addColumnCriterion(Operator columnOperator, String columnValue) {
		columnsOperators.add(columnOperator);
		columnsValues.add(columnValue);
	}

	public Couple<Operator, String> getLineCriterion(int index) {
		return new Couple<Operator, String>(linesOperators.get(index), linesValues.get(index));
	}

	public Couple<Operator, String> getColumnCriterion(int index) {
		return new Couple<Operator, String>(columnsOperators.get(index), columnsValues.get(index));
	}

	private int getUpperBoundNbLines() {

		int upperBoundNbLines = -1;

		for (int i = 0; i < getNbLineCriterions(); i++) {
			Couple<Operator, String> criterion = getLineCriterion(i);

			if (criterion.getX() == Operator.EQ || criterion.getX() == Operator.LEQ
					|| criterion.getX() == Operator.LT) {

				int nbLines = Integer.parseInt(criterion.getY());

				if (criterion.getX() == Operator.LT)
					nbLines--;

				if (nbLines > upperBoundNbLines)
					upperBoundNbLines = nbLines;
			}
		}

		return upperBoundNbLines;
	}

	private int getUpperBoundNbColumns() {

		int upperBoundNbColumns = -1;

		for (int i = 0; i < getNbLineCriterions(); i++) {
			Couple<Operator, String> criterion = getColumnCriterion(i);

			if (criterion.getX() == Operator.EQ || criterion.getX() == Operator.LEQ
					|| criterion.getX() == Operator.LT) {

				int nbColumns = Integer.parseInt(criterion.getY());

				if (criterion.getX() == Operator.LT)
					nbColumns--;

				if (nbColumns > upperBoundNbColumns)
					upperBoundNbColumns = nbColumns;
			}
		}

		return upperBoundNbColumns;
	}
}
