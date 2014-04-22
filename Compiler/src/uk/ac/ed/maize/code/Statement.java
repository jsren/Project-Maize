package uk.ac.ed.maize.code;

public abstract class Statement
{
	private StatementType type;
	private boolean scoped;
	
	public StatementType getType() {
		return this.type;
	}
	public boolean getIsScoped() {
		return this.scoped;
	}
	public abstract Statement[] getChildren();
	
	public Statement(StatementType type, boolean scoped)
	{
		this.type   = type;
		this.scoped = scoped;
	}
}
