package application;

import view.primaryStage.ScrollPaneWithPropertyList;

public abstract class InterruptibleOperation extends Operation {
    private boolean running = false;
    protected abstract void pause(ScrollPaneWithPropertyList pane);
    public abstract void resume(ScrollPaneWithPropertyList pane);

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
