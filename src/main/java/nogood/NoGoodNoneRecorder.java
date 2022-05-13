package nogood;

import java.util.ArrayList;

import generator.GeneralModel;
import generator.Solution;

public class NoGoodNoneRecorder extends NoGoodRecorder {

	public NoGoodNoneRecorder(GeneralModel model, Solution solution) {
		super(model, solution);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ArrayList<ArrayList<Integer>> computeOccurences() {
		// TODO Auto-generated method stub
		return new ArrayList<>();
	}

}
