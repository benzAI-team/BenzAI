package modules;

import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMax;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.search.strategy.strategy.IntStrategy;

import generator.GeneralModel;

public class CatacondensedModule extends Module {

	public CatacondensedModule(GeneralModel generalModel) {
		super(generalModel);
	}

	@Override
	public void buildVariables() {
	}

	@Override
	public void postConstraints() {
		generalModel.getProblem().tree(generalModel.getXG()).post();
	}

	@Override
	public void addVariables() {
	}

	@Override
	public void changeSolvingStrategy() {
		generalModel.getProblem().getSolver().setSearch(new IntStrategy(generalModel.getChanneling(),
				new FirstFail(generalModel.getProblem()), new IntDomainMax()));
	}

	@Override
	public void changeGraphVertices() {

	}

//	@Override
//	public void setPriority() {
//		priority = 2;
//	}

	@Override
	public String toString() {
		return "CatacondensedModule";
	}
}
