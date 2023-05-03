package expe;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.extension.Tuples;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMax;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.search.strategy.strategy.IntStrategy;
import org.chocosolver.solver.variables.IntVar;

public enum TestModel {
    ;

    public static void main(String[] args) {

		Model model = new Model("debug");

		IntVar x = model.intVar("x", 0, 1);
		IntVar y = model.intVar("y", 0, 1);
		IntVar z = model.intVar("z", 0, 2);

		Tuples table = new Tuples(true);
		table.setUniversalValue(-1);

		table.add(-1, -1, 2);

		IntVar[] tuple = new IntVar[] { x, y, z };

		model.table(tuple, table, "CT+").post();

		Solver solver = model.getSolver();
		solver.setSearch(new IntStrategy(tuple, new FirstFail(model), new IntDomainMax()));

		while (solver.solve()) {
			System.out.println("(x,y,z) = (" + x.getValue() + ", " + y.getValue() + ", " + z.getValue() + ")");
		}
	}
}
