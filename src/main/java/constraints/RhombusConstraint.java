package constraints;

public class RhombusConstraint extends RectangleConstraint {

	@Override
	public void postConstraints() {
		super.postConstraints();
//		for (PropertyExpression rhombusExpression : this.getExpressionList()) {
//			String operator = ((RhombusExpression)rhombusExpression).getWidthOperator();
//			int value = ((RhombusExpression)rhombusExpression).getWidth();
//			getGeneralModel().getProblem().arithm(getWidthVar(), operator, value).post();
//		}
		getGeneralModel().getProblem().arithm(getHeightVar(), "=", getWidthVar()).post();
	}
}
