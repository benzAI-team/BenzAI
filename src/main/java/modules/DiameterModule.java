package modules;

import java.util.ArrayList;

import org.chocosolver.solver.variables.IntVar;

import generator.GeneralModel;
import generator.GeneratorCriterion;
import generator.GeneratorCriterion.Subject;

public class DiameterModule extends Module {

	private ArrayList<GeneratorCriterion> criterions;

	private IntVar diameter;

	public DiameterModule(GeneralModel generalModel, ArrayList<GeneratorCriterion> criterions) {
		super(generalModel);
		this.criterions = criterions;
	}

	@Override
	public void setPriority() {
		priority = 1;
	}

	@Override
	public void buildVariables() {
		diameter = generalModel.getProblem().intVar("diameter", 0, generalModel.getNbMaxHexagons());
	}

	@Override
	public void postConstraints() {

		generalModel.getProblem().diameter(generalModel.getWatchedGraphVar(), diameter).post();

		for (GeneratorCriterion criterion : criterions) {

			Subject subject = criterion.getSubject();

			if (subject == Subject.DIAMETER) {

				String operator = criterion.getOperatorString();
				int value = Integer.parseInt(criterion.getValue());

				generalModel.getProblem().arithm(diameter, operator, value).post();
			}
		}
	}

	@Override
	public void addWatchedVariables() {
		generalModel.addWatchedVariable(diameter);
	}

	@Override
	public void changeSolvingStrategy() {

	}

	@Override
	public void changeWatchedGraphVertices() {

	}

}
