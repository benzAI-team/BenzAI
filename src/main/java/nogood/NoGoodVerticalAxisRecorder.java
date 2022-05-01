package nogood;

import java.util.ArrayList;

import generator.GeneralModel;
import generator.Solution;

public class NoGoodVerticalAxisRecorder extends NoGoodRecorder {

	public NoGoodVerticalAxisRecorder(GeneralModel model) {
		super(model);
	}

	@Override
	public ArrayList<ArrayList<Integer>> computeOccurences(Solution solution) {
		return solution.translationsEdgeMirror();
	}

}
