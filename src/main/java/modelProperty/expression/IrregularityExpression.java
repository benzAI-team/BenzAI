package modelProperty.expression;

public class IrregularityExpression extends PropertyExpression {

	private double xi;
	private int n0, n1, n2, n3, n4;
	
	private String xiOperator, n0Operator, n1Operator, n2Operator, n3Operator, n4Operator;

	
	public IrregularityExpression(String subject, double xi, int n0, int n1, int n2, int n3, int n4, String xiOperator,
			String n0Operator, String n1Operator, String n2Operator, String n3Operator, String n4Operator) {
		super(subject);
		this.xi = xi;
		this.n0 = n0;
		this.n1 = n1;
		this.n2 = n2;
		this.n3 = n3;
		this.n4 = n4;
		this.xiOperator = xiOperator;
		this.n0Operator = n0Operator;
		this.n1Operator = n1Operator;
		this.n2Operator = n2Operator;
		this.n3Operator = n3Operator;
		this.n4Operator = n4Operator;
	}

	public double getXi() {
		return xi;
	}

	public int getN0() {
		return n0;
	}

	public int getN1() {
		return n1;
	}

	public int getN2() {
		return n2;
	}

	public int getN3() {
		return n3;
	}

	public int getN4() {
		return n4;
	}

	public String getXiOperator() {
		return xiOperator;
	}

	public String getN0Operator() {
		return n0Operator;
	}

	public String getN1Operator() {
		return n1Operator;
	}

	public String getN2Operator() {
		return n2Operator;
	}

	public String getN3Operator() {
		return n3Operator;
	}

	public String getN4Operator() {
		return n4Operator;
	}
		
	

}
