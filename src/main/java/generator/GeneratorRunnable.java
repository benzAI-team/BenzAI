package generator;

public class GeneratorRunnable implements Runnable {

	private final GeneralModel generalModel;
	private boolean terminated;

	public GeneratorRunnable(GeneralModel generalModel) {
		this.generalModel = generalModel;
		terminated = false;
	}

	public SolverResults getResultSolver() {
		return generalModel.getResultSolver();
	}

	public boolean isTerminated() {
		return terminated;
	}

	@Override
	public void run() {
		generalModel.solve();
		System.out.println("FINIIIII");
		terminated = true;
	}
}
