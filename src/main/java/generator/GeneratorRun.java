package generator;

public class GeneratorRun {

	private GeneralModel model;
	private boolean isStopped;
	private boolean isPaused;

	public GeneratorRun(GeneralModel model) {
		this.model = model;
		isStopped = false;
		isPaused = false;
	}

	public boolean isStopped() {
		return isStopped;
	}

	public boolean isPaused() {
		return isPaused;
	}
	
	public void stop() {
		isStopped = true;
	}
	
	public void pause() {
		isPaused = true;
	}
	
	public void resume() {
		isPaused = false;
		model.resume();
	}

}
