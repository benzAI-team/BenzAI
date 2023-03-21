package collection_operations;

import javafx.concurrent.Service;

public abstract class CollectionTask extends CollectionComputation {

	private Service<Void> calculateService;
	private boolean isRunning;
	private int index;
	private int lineIndex;

	CollectionTask(String name) {
		super(name);
	}

	public Service<Void> getCalculateService() {
		return calculateService;
	}

	public void setCalculateService(Service<Void> calculateService) {
		this.calculateService = calculateService;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getLineIndex() {
		return lineIndex;
	}

	public void setLineIndex(int lineIndex) {
		this.lineIndex = lineIndex;
	}


}
