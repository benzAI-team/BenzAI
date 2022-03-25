package modules;

import java.util.ArrayList;

import generator.GeneralModel;
import generator.GeneratorCriterion;
import generator.GeneratorCriterion.Subject;

public class RhombusModule extends RectangleModule {

	public RhombusModule(GeneralModel generalModel, ArrayList<GeneratorCriterion> criterions) {
		super(generalModel, criterions);
	}

	@Override
	public void postConstraints() {

		super.postConstraints();

		for (GeneratorCriterion criterion : criterions) {

			if (criterion.getSubject() == Subject.RHOMBUS_DIMENSION) {
				String operatorStr = criterion.getOperatorString();
				generalModel.getProblem().arithm(xW, operatorStr, Integer.parseInt(criterion.getValue())).post();
			}
		}

		generalModel.getProblem().arithm(rotation, "=", 1).post();
		generalModel.getProblem().arithm(xH, "=", xW).post();
	}
}
