package modules;

import java.util.ArrayList;

import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

import generator.GeneralModel;
import generator.GeneratorCriterion;

public class RectangleModule2 extends Module {

	private ArrayList<GeneratorCriterion> criterions;

	private int[] correspondances;

	private BoolVar[][] lines;
	private BoolVar[][] columns;

	private IntVar nbLines;
	private IntVar nbColumns;

	private IntVar zero;

	public RectangleModule2(GeneralModel generalModel, ArrayList<GeneratorCriterion> criterions) {
		super(generalModel);
		this.criterions = criterions;

		buildCorrespondances();
		System.out.println("");
	}

	private void buildCorrespondances() {

		correspondances = new int[generalModel.getDiameter()];

		int center = (generalModel.getDiameter() - 1) / 2;
		correspondances[0] = center;
		int shift = 1;

		for (int i = 1; i < generalModel.getDiameter(); i++) {

			if (i % 2 == 1) {
				correspondances[i] = center - shift;
			}

			else {
				correspondances[i] = center + shift;
				shift++;
			}
		}
	}

	@Override
	public void setPriority() {
		priority = 1;
	}

	@Override
	public void buildVariables() {
		// TODO Auto-generated method stub

	}

	@Override
	public void postConstraints() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addWatchedVariables() {
		// TODO Auto-generated method stub

	}

	@Override
	public void changeSolvingStrategy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void changeWatchedGraphVertices() {
		// TODO Auto-generated method stub

	}

}
