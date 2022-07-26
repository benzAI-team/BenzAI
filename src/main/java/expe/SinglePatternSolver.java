package expe;

import java.io.File;
import java.io.IOException;

import generator.OrderStrategy;
import generator.ValueStrategy;
import generator.VariableStrategy;
import generator.fragments.Fragment;

public class SinglePatternSolver {

	private static void usage() {
		System.out.println("args : pattern_file single_pattern_model nb_hexagons var_strat val_strat order_strat");
	}

	public static void main(String[] args) throws IOException {

		// model.addModule(new SingleFragment1Module(model,
		// patternsInformations.getFragments().get(0),
		// VariableStrategy.FIRST_FAIL, ValueStrategy.INT_MAX,
		// OrderStrategy.CHANNELING_FIRST));

		Fragment p1 = Fragment.importFragment(new File(args[0]));

		int model = Integer.parseInt(args[1]);
		int nbHexagons = Integer.parseInt(args[2]);

		// FIRST_FAIL, DOM_WDEG, DOM_WDEG_REF, CHS
		VariableStrategy variableStrategy = null;
		int varStrat = Integer.parseInt(args[3]);

		if (varStrat == 1)
			variableStrategy = VariableStrategy.FIRST_FAIL;
		else if (varStrat == 2)
			variableStrategy = VariableStrategy.DOM_WDEG;
		else if (varStrat == 3)
			variableStrategy = VariableStrategy.DOM_WDEG_REF;
		else if (varStrat == 4)
			variableStrategy = VariableStrategy.CHS;

		// INT_MIN, INT_MAX;
		ValueStrategy valueStrategy = null;
		int valStrat = Integer.parseInt(args[4]);

		if (valStrat == 1)
			valueStrategy = ValueStrategy.INT_MIN;
		else if (valStrat == 2)
			valueStrategy = ValueStrategy.INT_MAX;

		// CHANNELING_FIRST, CHANNELING_LAST;
		OrderStrategy orderStrategy = null;
		int orderStrat = Integer.parseInt(args[5]);

		if (orderStrat == 1)
			orderStrategy = OrderStrategy.CHANNELING_FIRST;
		else if (orderStrat == 2)
			orderStrategy = OrderStrategy.CHANNELING_LAST;

		System.out.println("Pattern: " + args[0]);
		System.out.println("Model: single_" + model);
		System.out.println("Nb_Hexagons: " + nbHexagons);
		System.out.println("Variable_Strategy: " + variableStrategy);
		System.out.println("ValueStrategy: " + valueStrategy);
		System.out.println("OrderStrategy: " + orderStrategy);
	}
}
