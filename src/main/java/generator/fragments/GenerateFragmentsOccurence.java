package generator.fragments;

import java.io.File;
import java.io.IOException;

import generator.GeneralModel;
import generator.NbHexagons;
import generator.OrderStrategy;
import generator.ValueStrategy;
import generator.VariableStrategy;
import modules.OccurenceFragmentModule;
import solving_modes.GeneralModelMode;

public class GenerateFragmentsOccurence {

	public static void main(String [] args) throws IOException {
		
		Fragment f = Fragment.importFragment(new File("expe_fragments/armchair_edge.frg"));
		
		GeneralModel model = new GeneralModel(new NbHexagons(7));
		model.addModule(new OccurenceFragmentModule(model, f, 2, VariableStrategy.FIRST_FAIL, ValueStrategy.INT_MIN, OrderStrategy.CHANNELING_FIRST));
	
		model.solve();
	}
}
