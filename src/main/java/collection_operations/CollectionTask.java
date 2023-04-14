package collection_operations;

import javafx.concurrent.Service;

public abstract class CollectionTask extends CollectionComputation {
    private boolean operationIsRunning;
    private Service<Void> calculateService;
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

	public boolean operationIsRunning() {
		return operationIsRunning;
	}

	public void setOperationIsRunning(boolean isRunning) {
		this.operationIsRunning = isRunning;
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
