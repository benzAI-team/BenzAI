package generator.patterns;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import generator.GeneralModel;
import generator.NbHexagons;
import generator.OrderStrategy;
import generator.ValueStrategy;
import generator.VariableStrategy;
import modules.MultiplePatterns1Module;
import modules.MultiplePatterns2Module;
import modules.MultiplePatterns3Module;
import solving_modes.GeneralModelMode;

public class GenerateMultiplePatterns {

	private static void solve1(ArrayList<Pattern> patterns, int nbHexagons, VariableStrategy variableStrategy, ValueStrategy valueStrategy, OrderStrategy orderStrategy){
		
		GeneralModel model = new GeneralModel(new NbHexagons(nbHexagons));
		model.addModule(new MultiplePatterns1Module(model, patterns, variableStrategy, valueStrategy, orderStrategy));
		System.out.println(model.getNbCrowns() + " crowns");
		model.solve();
	}
	
	private static void solve2(ArrayList<Pattern> patterns, int nbHexagons, VariableStrategy variableStrategy, ValueStrategy valueStrategy, OrderStrategy orderStrategy){
			
		GeneralModel model = new GeneralModel(new NbHexagons(nbHexagons));
		model.addModule(new MultiplePatterns2Module(model, patterns, variableStrategy, valueStrategy, orderStrategy));
		
		System.out.println(model.getNbCrowns() + " crowns");
		
		model.solve();
	}
	
	private static void solve3(ArrayList<Pattern> patterns, int nbHexagons, VariableStrategy variableStrategy, ValueStrategy valueStrategy, OrderStrategy orderStrategy) {
		
		GeneralModel model = new GeneralModel(new NbHexagons(nbHexagons));
		model.addModule(new MultiplePatterns3Module(model, patterns, variableStrategy, valueStrategy, orderStrategy));
		
		System.out.println(model.getNbCrowns() + " crowns");
		
		model.solve();
	}
	
	public static void main(String [] args) throws IOException {
		
		Pattern pattern1 = Pattern.importPattern(new File(args[0]));
		Pattern pattern2 = Pattern.importPattern(new File(args[1]));		
		
		ArrayList<Pattern> patterns = new ArrayList<Pattern>();
		patterns.add(pattern1);
		patterns.add(pattern2);
		
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
			solve1(patterns, nbHexagons, variableStrategy, valueStrategy, orderStrategy);
		
		else if (mode == 2)
			solve2(patterns, nbHexagons, variableStrategy, valueStrategy, orderStrategy);
		
		else if (mode == 3)
			solve3(patterns, nbHexagons, variableStrategy, valueStrategy, orderStrategy);
	}
}