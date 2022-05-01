package nogood;

import java.util.ArrayList;

import generator.GeneralModel;
import generator.Solution;

public class NoGoodUniqueRecorder extends NoGoodRecorder{

	public NoGoodUniqueRecorder(GeneralModel model) {
		super(model);
		
	}

	@Override
	public ArrayList<ArrayList<Integer>> computeOccurences(Solution solution) {
		ArrayList<ArrayList<Integer>> translations = new ArrayList<>();
		translations.add(solution.getVertices());
		return translations;
	}

	
}
