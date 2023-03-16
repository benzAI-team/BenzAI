package modules;

import java.util.ArrayList;

import generator.GeneralModel;
import modelProperty.ModelPropertySet;
import modelProperty.expression.BinaryNumericalExpression;
import modelProperty.expression.PropertyExpression;

public abstract class Module {

	public static final Module NOMODULE = new Module() {
		public void buildVariables() {}	
		public void postConstraints() {}
		public void addVariables() {}
		public void changeSolvingStrategy() {}
		public void changeGraphVertices() {}

	};
	private GeneralModel generalModel;
	ArrayList<PropertyExpression> expressionList;

	public Module() {}
	
	
	public abstract void buildVariables();	
	public abstract void postConstraints();
	public abstract void addVariables();
	public abstract void changeSolvingStrategy();
	public abstract void changeGraphVertices();

	public void build(GeneralModel generalModel, ArrayList<PropertyExpression> expressionList) {
		this.generalModel = generalModel;
		this.expressionList = expressionList;
		buildVariables();	
		postConstraints();
		addVariables();
		changeSolvingStrategy();
		changeGraphVertices();
	}
	
	public GeneralModel getGeneralModel() {
		return generalModel;
	}

	public void setGeneralModel(GeneralModel generalModel) {
		this.generalModel = generalModel;
	}


	public ArrayList<PropertyExpression> getExpressionList() {
		return expressionList;
	}


	public void setExpressionList(ArrayList<PropertyExpression> expressionList) {
		this.expressionList = expressionList;
	}
	
}
