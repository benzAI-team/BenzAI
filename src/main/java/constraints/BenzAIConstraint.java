package constraints;

import generator.GeneralModel;
import generator.properties.model.expression.PropertyExpression;

import java.util.ArrayList;

@SuppressWarnings("unused")
public abstract class BenzAIConstraint {

	public static final BenzAIConstraint NOCONSTRAINT = new BenzAIConstraint() {
		public void buildVariables() {}	
		public void postConstraints() {}
		public void addVariables() {}
		public void changeSolvingStrategy() {}
		public void changeGraphVertices() {}

	};
	private GeneralModel generalModel;
	private ArrayList<PropertyExpression> expressionList;

	public BenzAIConstraint() {}
	
	
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

	public ArrayList<PropertyExpression> getExpressionList() {
		return expressionList;
	}
}
