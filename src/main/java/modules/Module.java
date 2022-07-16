package modules;

import generator.GeneralModel;

public abstract class Module {

	protected GeneralModel generalModel;
	
	public Module(GeneralModel generalModel) {
		this.generalModel = generalModel;
	}
	
	public abstract void buildVariables();	
	public abstract void postConstraints();
	public abstract void addVariables();
	public abstract void changeSolvingStrategy();
	public abstract void changeGraphVertices();	
}
