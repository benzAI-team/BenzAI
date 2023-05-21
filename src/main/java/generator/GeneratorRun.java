package generator;

public class GeneratorRun {

	private final GeneralModel model;
	private boolean isStopped;
	private boolean isPaused;

	GeneratorRun(GeneralModel model) {
		this.model = model;
		isStopped = false;
		isPaused = false;
	}

	public boolean isStopped() {
		return isStopped;
	}

	boolean isPaused() {
		return isPaused;
	}
	
	void stop() {
		isStopped = true;
	}
	
	void pause() {
		isPaused = true;
	}
	
	public void resume() {
		isPaused = false;
		model.resume();
	}

}
