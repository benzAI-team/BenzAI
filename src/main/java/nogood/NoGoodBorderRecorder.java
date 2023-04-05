package nogood;

import java.util.ArrayList;

import generator.GeneralModel;
import generator.Solution;

public class NoGoodBorderRecorder extends NoGoodRecorder {

	private ArrayList<Integer> topBorder;
	private ArrayList<Integer> leftBorder;

	public NoGoodBorderRecorder(GeneralModel model, Solution solution, ArrayList<Integer> topBorder,
			ArrayList<Integer> leftBorder) {
		super(model, solution);
		this.topBorder = topBorder;
		this.leftBorder = leftBorder;
	}

	@Override
	protected ArrayList<ArrayList<Integer>> computeOccurences() {
		return getSolution().borderTranslations(topBorder, leftBorder);
	}

}
