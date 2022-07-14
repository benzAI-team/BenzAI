package modules;

import generator.GeneralModel;

public abstract class Module implements Comparable<Module>{

	protected int priority;
	protected GeneralModel generalModel;
	
	public Module(GeneralModel generalModel) {
		this.generalModel = generalModel;
		setPriority();
	}
	
	public abstract void setPriority();
	public abstract void buildVariables();	
	public abstract void postConstraints();
	public abstract void addVariables();
	public abstract void changeSolvingStrategy();
	public abstract void changeGraphVertices();
	
	public int getPriority() {
		return priority;
	}
	
	@Override
	public int compareTo(Module o) {
		return - Integer.compare(priority, o.getPriority());
	}
	
}
