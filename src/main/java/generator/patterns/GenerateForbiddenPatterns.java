package generator.patterns;

import java.io.File;
import java.io.IOException;
import generator.GeneralModel;
import generator.NbHexagons;
import generator.OrderStrategy;
import generator.ValueStrategy;
import generator.VariableStrategy;
import modules.ForbiddenPatternModule1;
import modules.ForbiddenPatternModule2;
import modules.ForbiddenPatternModule3;
import solving_modes.GeneralModelMode;

public class GenerateForbiddenPatterns {

	public static void solve1(Pattern pattern, int nbHexagons, VariableStrategy variableStrategy, ValueStrategy valueStrategy, OrderStrategy orderStrategy) {
		
		GeneralModel model = new GeneralModel(new NbHexagons(nbHexagons));
		model.addModule(new ForbiddenPatternModule1(model, pattern, variableStrategy, valueStrategy, orderStrategy));
		model.solve();
	}
	
	public static void solve2(Pattern pattern, int nbHexagons, VariableStrategy variableStrategy, ValueStrategy valueStrategy, OrderStrategy orderStrategy) {
		
		GeneralModel model = new GeneralModel(new NbHexagons(nbHexagons));
		model.addModule(new ForbiddenPatternModule2(model, pattern, variableStrategy, valueStrategy, orderStrategy));
		model.solve();
	}
	
	public static void solve3(Pattern pattern, int nbHexagons, VariableStrategy variableStrategy, ValueStrategy valueStrategy) {
		
		GeneralModel model = new GeneralModel(new NbHexagons(nbHexagons));
		model.addModule(new ForbiddenPatternModule3(model, pattern, variableStrategy, valueStrategy));
		model.solve();
	}
	
	public static void main(String [] args) throws IOException {
		
		Pattern pattern = Pattern.importPattern(new File(args[0]));
		int nbHexagons = Integer.parseInt(args[1]);
		int mode = Integer.parseInt(args[2]);
		
		VariableStrategy variableStrategy;
		
		if (args[3].equals("1"))
			variableStrategy = VariableStrategy.FIRST_FAIL;
		else if (args[3].equals("2"))
			variableStrategy = VariableStrategy.DOM_WDEG;
		else if (args[3].equals("3"))
			variableStrategy = VariableStrategy.DOM_WDEG_REF;
		else
			variableStrategy = VariableStrategy.CHS;
		
		ValueStrategy valueStrategy;
		if (args[4].equals("1"))
			valueStrategy = ValueStrategy.INT_MIN;
		else
			valueStrategy = ValueStrategy.INT_MAX;
		
		OrderStrategy orderStrategy;
		
		if (args[5].equals("1"))
			orderStrategy = OrderStrategy.CHANNELING_FIRST;
		else
			orderStrategy = OrderStrategy.CHANNELING_LAST;
		
		if (mode == 1)
			solve1(pattern, nbHexagons, variableStrategy, valueStrategy, orderStrategy);
		
		else if (mode == 2)
			solve2(pattern, nbHexagons, variableStrategy, valueStrategy, orderStrategy);
		
		else if (mode == 3)
			solve3(pattern, nbHexagons, variableStrategy, valueStrategy);
		
	}
}
