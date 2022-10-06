package generator.patterns;

import java.io.File;
import java.io.IOException;

import generator.GeneralModel;
import generator.NbHexagons;
import generator.OrderStrategy;
import generator.ValueStrategy;
import generator.VariableStrategy;
import modules.OccurencePatternModule;
import solving_modes.GeneralModelMode;

public class GeneratePatternsOccurence {

	public static void main(String [] args) throws IOException {
		
		Pattern f = Pattern.importPattern(new File("expe_fragments/armchair_edge.frg"));
		
		GeneralModel model = new GeneralModel(new NbHexagons(7));
		model.addModule(new OccurencePatternModule(model, f, 2, VariableStrategy.FIRST_FAIL, ValueStrategy.INT_MIN, OrderStrategy.CHANNELING_FIRST));
	
		model.solve();
	}
}
