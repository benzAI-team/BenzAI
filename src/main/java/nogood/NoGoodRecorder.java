package nogood;

import generator.GeneralModel;
import generator.Solution;

public abstract class NoGoodRecorder {
	
	protected GeneralModel model;
	protected Solution solution;
	
	public NoGoodRecorder (GeneralModel model, Solution solution) {
		this.solution = solution;
	}
	
	public abstract void record(GeneralModel model, Solution solution);
}
