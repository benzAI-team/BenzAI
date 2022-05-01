package nogood;

import java.util.ArrayList;

import generator.GeneralModel;
import generator.Solution;

public class NoGoodVerticalAxisRecorder extends NoGoodRecorder {

	public NoGoodVerticalAxisRecorder(GeneralModel model, Solution solution) {
		super(model, solution);
	}

	@Override
	protected ArrayList<ArrayList<Integer>> computeOccurences() {
		return solution.translationsEdgeMirror();
	}

}
