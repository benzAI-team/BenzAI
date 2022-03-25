package expe;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.search.strategy.strategy.IntStrategy;
import org.chocosolver.solver.variables.IntVar;

public class TestModel {

	public static void main(String[] args) {

		Model model = new Model("debug");

		IntVar[] tab = new IntVar[5];

		for (int i = 0; i < tab.length; i++)
			tab[i] = model.intVar("tab_" + i, 0, 5);

		IntVar sum = model.intVar("sum", 0, 20);

		model.sum(tab, "=", sum).post();
		model.arithm(sum, "=", 20).post();

		model.getSolver().setSearch(new IntStrategy(tab, new FirstFail(model), new IntDomainMin()));

		while (model.getSolver().solve()) {

			for (IntVar x : tab)
				System.out.println(x.getName() + " = " + x.getValue());

			System.out.println(sum.getName() + " = " + sum.getValue());

			System.out.println("");
		}

		System.out.println(model);
	}
}
