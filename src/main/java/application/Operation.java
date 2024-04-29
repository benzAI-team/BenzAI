package application;

import view.primaryStage.ScrollPaneWithPropertyList;

public abstract class Operation {
    private boolean possible = true;
    public abstract void run(ScrollPaneWithPropertyList pane);
    public abstract void stop(ScrollPaneWithPropertyList pane);

    public boolean isPossible() { return possible; }
    public void setPossible(boolean possible) {
        this.possible = possible;
    }
}
