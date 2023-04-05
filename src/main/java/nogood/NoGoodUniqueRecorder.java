package nogood;

import java.util.ArrayList;

import generator.GeneralModel;
import generator.Solution;

public class NoGoodUniqueRecorder extends NoGoodRecorder {

	public NoGoodUniqueRecorder(GeneralModel model, Solution solution) {
		super(model, solution);

	}

	@Override
	protected ArrayList<ArrayList<Integer>> computeOccurences() {
		ArrayList<ArrayList<Integer>> translations = new ArrayList<>();
		translations.add(getSolution().getVertices());
		return translations;
	}

}
