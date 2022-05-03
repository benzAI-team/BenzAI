package generator.criterions;

import java.util.ArrayList;

import utils.Couple;

public class IrregularityGeneratorCriterion extends GeneratorCriterion2 {

	private ArrayList<Operator> xiOperators;
	private ArrayList<String> xiValues;

	private ArrayList<Operator> n0Operators;
	private ArrayList<String> n0Values;

	private ArrayList<Operator> n1Operators;
	private ArrayList<String> n1Values;

	private ArrayList<Operator> n2Operators;
	private ArrayList<String> n2Values;

	private ArrayList<Operator> n3Operators;
	private ArrayList<String> n3Values;

	private ArrayList<Operator> n4Operators;
	private ArrayList<String> n4Values;

	public IrregularityGeneratorCriterion() {
		super(Operator.NONE, "");
		initializeLists();
	}

	private void initializeLists() {
		xiOperators = new ArrayList<>();
		xiValues = new ArrayList<>();
		n0Operators = new ArrayList<>();
		n0Values = new ArrayList<>();
		n1Operators = new ArrayList<>();
		n1Values = new ArrayList<>();
		n2Operators = new ArrayList<>();
		n2Values = new ArrayList<>();
		n3Operators = new ArrayList<>();
		n3Values = new ArrayList<>();
		n4Operators = new ArrayList<>();
		n4Values = new ArrayList<>();
	}

	@Override
	public int optimizeNbHexagons() {
		return -1;
	}

	@Override
	public int optimizeNbCrowns(int upperBoundNbHexagons) {
		return -1;
	}

	public void addXICriterion(Operator operator, String value) {
		xiOperators.add(operator);
		xiValues.add(value);
	}

	public void addN1Criterion(Operator operator, String value) {
		n1Operators.add(operator);
		n1Values.add(value);
	}

	public void addN2Criterion(Operator operator, String value) {
		n2Operators.add(operator);
		n2Values.add(value);
	}

	public void addN3Criterion(Operator operator, String value) {
		n3Operators.add(operator);
		n3Values.add(value);
	}

	public void addN4Criterion(Operator operator, String value) {
		n4Operators.add(operator);
		n4Values.add(value);
	}

	public Couple<Operator, String> getXICriterion(int index) {
		return new Couple<>(xiOperators.get(index), xiValues.get(index));
	}

	public Couple<Operator, String> getN0Criterion(int index) {
		return new Couple<>(n0Operators.get(index), n0Values.get(index));
	}

	public Couple<Operator, String> getN1Criterion(int index) {
		return new Couple<>(n1Operators.get(index), n1Values.get(index));
	}

	public Couple<Operator, String> getN2Criterion(int index) {
		return new Couple<>(n2Operators.get(index), n2Values.get(index));
	}

	public Couple<Operator, String> getN3Criterion(int index) {
		return new Couple<>(n3Operators.get(index), n3Values.get(index));
	}

	public Couple<Operator, String> getN4Criterion(int index) {
		return new Couple<>(n4Operators.get(index), n4Values.get(index));
	}

	public int getNbXICriterions() {
		return xiOperators.size();
	}

	public int getNbN0Criterions() {
		return n0Operators.size();
	}

	public int getNbN1Criterions() {
		return n1Operators.size();
	}

	public int getNbN2Criterions() {
		return n2Operators.size();
	}

	public int getNbN3Criterions() {
		return n3Operators.size();
	}

	public int getNbN4Criterions() {
		return n4Operators.size();
	}
}
