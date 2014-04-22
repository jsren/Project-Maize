package uk.ac.ed.maize.code;

public class WhileStatement extends Statement
{
	private Expression condition;
	private Statement[] children;
	
	public Expression getCondition() { return this.condition; }
	@Override
	public Statement[] getChildren() { return this.children; }
	
	public WhileStatement(Expression condition, Statement[] children) 
	{
		super(StatementType.While, true);
		this.condition = condition;
		this.children  = children;
	}
}
