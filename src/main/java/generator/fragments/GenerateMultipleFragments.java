package generator.fragments;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import generator.GeneralModel;
import generator.OrderStrategy;
import generator.ValueStrategy;
import generator.VariableStrategy;
import modules.MultipleFragments1Module;
import modules.MultipleFragments2Module;
import modules.MultipleFragments3Module;
import solving_modes.GeneralModelMode;

public class GenerateMultipleFragments {

	private static void solve1(ArrayList<Fragment> fragments, int nbHexagons, VariableStrategy variableStrategy, ValueStrategy valueStrategy, OrderStrategy orderStrategy){
		
		GeneralModel model = new GeneralModel(nbHexagons, GeneralModelMode.NB_HEXAGONS);
		model.addModule(new MultipleFragments1Module(model, fragments, variableStrategy, valueStrategy, orderStrategy));
		System.out.println(model.getNbCrowns() + " crowns");
		model.solve();
	}
	
	private static void solve2(ArrayList<Fragment> fragments, int nbHexagons, VariableStrategy variableStrategy, ValueStrategy valueStrategy, OrderStrategy orderStrategy){
			
		GeneralModel model = new GeneralModel(nbHexagons, GeneralModelMode.NB_HEXAGONS);
		model.addModule(new MultipleFragments2Module(model, fragments, variableStrategy, valueStrategy, orderStrategy));
		
		System.out.println(model.getNbCrowns() + " crowns");
		
		model.solve();
	}
	
	private static void solve3(ArrayList<Fragment> fragments, int nbHexagons, VariableStrategy variableStrategy, ValueStrategy valueStrategy, OrderStrategy orderStrategy) {
		
		GeneralModel model = new GeneralModel(nbHexagons, GeneralModelMode.NB_HEXAGONS);
		model.addModule(new MultipleFragments3Module(model, fragments, variableStrategy, valueStrategy, orderStrategy));
		
		System.out.println(model.getNbCrowns() + " crowns");
		
		model.solve();
	}
	
	public static void main(String [] args) throws IOException {
		
		Fragment fragment1 = Fragment.importFragment(new File(args[0]));
		Fragment fragment2 = Fragment.importFragment(new File(args[1]));		
		
		ArrayList<Fragment> fragments = new ArrayList<Fragment>();
		fragments.add(fragment1);
		fragments.add(fragment2);
		
		int nbHexagons = Integer.parseInt(args[2]);
		
		int mode = Integer.parseInt(args[3]);
		
		VariableStrategy variableStrategy;
		ValueStrategy valueStrategy;
		OrderStrategy orderStrategy;
		
		if (args[4].equals("1"))
			variableStrategy = VariableStrategy.FIRST_FAIL;
		else if (args[4].equals("2"))
			variableStrategy = VariableStrategy.DOM_WDEG;
		else if (args[4].equals("3"))
			variableStrategy = VariableStrategy.DOM_WDEG_REF;
		else
			variableStrategy = VariableStrategy.CHS;
		
		
		if (args[5].equals("1"))
			valueStrategy = ValueStrategy.INT_MIN;
		else
			valueStrategy = ValueStrategy.INT_MAX;
		
		
		if (args[6].equals("1"))
			orderStrategy = OrderStrategy.CHANNELING_FIRST;
		else
			orderStrategy = OrderStrategy.CHANNELING_LAST;
		
		if (mode == 1)
			solve1(fragments, nbHexagons, variableStrategy, valueStrategy, orderStrategy);
		
		else if (mode == 2)
			solve2(fragments, nbHexagons, variableStrategy, valueStrategy, orderStrategy);
		
		else if (mode == 3)
			solve3(fragments, nbHexagons, variableStrategy, valueStrategy, orderStrategy);
	}
}