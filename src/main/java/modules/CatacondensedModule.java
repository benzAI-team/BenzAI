package modules;

import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMax;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.search.strategy.strategy.IntStrategy;

import generator.GeneralModel;

public class CatacondensedModule extends Module {


	@Override
	public void buildVariables() {
	}

	@Override
	public void postConstraints() {
		getGeneralModel().getProblem().tree(getGeneralModel().getXG()).post();
	}

	@Override
	public void addVariables() {
	}

	@Override
	public void changeSolvingStrategy() {
		getGeneralModel().getProblem().getSolver().setSearch(new IntStrategy(getGeneralModel().getChanneling(),
				new FirstFail(getGeneralModel().getProblem()), new IntDomainMax()));
	}

	@Override
	public void changeGraphVertices() {

	}

	@Override
	public String toString() {
		return "CatacondensedModule";
	}
}
