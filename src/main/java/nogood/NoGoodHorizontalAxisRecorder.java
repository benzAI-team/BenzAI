package nogood;

import java.util.ArrayList;
import generator.GeneralModel;
import generator.Solution;

public class NoGoodHorizontalAxisRecorder extends NoGoodRecorder {

	public NoGoodHorizontalAxisRecorder(GeneralModel model) {
		super(model);
	}

	@Override
	public ArrayList<ArrayList<Integer>> computeOccurences(Solution solution) {
		return solution.translationsFaceMirror();
	}
}
 