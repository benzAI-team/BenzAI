package constraints;

import generator.GeneralModel;
import generator.properties.model.expression.PropertyExpression;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMax;
import org.chocosolver.solver.search.strategy.selectors.values.IntValueSelector;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.search.strategy.selectors.variables.VariableSelector;
import org.chocosolver.solver.search.strategy.strategy.IntStrategy;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

import java.util.ArrayList;

public class PatternCollectionConstraint extends BenzAIConstraint {
    ArrayList<PatternConstraint> patternConstraints;
    ArrayList<PatternConstraint> interactionConstraints;

    public PatternCollectionConstraint() {
        patternConstraints = new ArrayList<>();
    }

    @Override
    public void build(GeneralModel generalModel, ArrayList<PropertyExpression> expressionList) {
        for (BenzAIConstraint c : patternConstraints) {
            System.out.println("build "+c);
            c.build(generalModel, expressionList);
        }
        super.build(generalModel,expressionList);
    }

    @Override
    public void buildVariables() {
    }

    @Override
    public void postConstraints() {
    }

    @Override
    public void addVariables() {
    }

    @Override
    public void changeSolvingStrategy() {
        GeneralModel generalModel = getGeneralModel();

        int presencesTotalLength = 0;
        for (PatternConstraint c : patternConstraints) {
            presencesTotalLength += c.getPresenceVariables().length;
        }

        IntVar[] branchingVariables = new IntVar[generalModel.getHexBoolVars().length + presencesTotalLength];
        int index = 0;

        for (BoolVar x : generalModel.getHexBoolVars()) {
            branchingVariables[index] = x;
            index++;
        }

        for (PatternConstraint c : patternConstraints) {
            for (BoolVar x : c.getPresenceVariables()) {
                branchingVariables[index] = x;
                index++;
            }
        }

        VariableSelector<IntVar> variableSelector = new FirstFail(generalModel.getProblem());
        IntValueSelector valueSelector = new IntDomainMax();

        generalModel.getProblem().getSolver()
                .setSearch(new IntStrategy(branchingVariables, variableSelector, valueSelector));
    }

    @Override
    public void changeGraphVertices() {
        // TODO Auto-generated method stub
    }

    public void addPatternConstraint (PatternConstraint patternConstraint) {
        patternConstraints.add(patternConstraint);
    }

    public void reset () {
        patternConstraints = new ArrayList<>();
    }
}
