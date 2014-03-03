package uk.ac.ed.kl.parser;

import uk.ac.ed.kl.Freezable;
import uk.ac.ed.kl.meta.CodeUnit;
import uk.ac.ed.kl.meta.Scope;
import uk.ac.ed.kl.meta.Type;

public class UnitContext implements Freezable
{
	private CodeUnit unit;
	private Type     type;
	private Scope    scope;
	private boolean  frozen;
	
	private ParserContext globalContext;
	
	public CodeUnit getCodeUnit()     { return this.unit; }
	public Type     getCurrentType()  { return this.type; }
	public Scope    getCurrentScope() { return this.scope; }
	
	public ParserContext getGlobalContext() { return this.globalContext; }
	
	public void setCurrentType(Type type)    { this.type = type; }
	public void setCurrentScope(Scope scope) { this.scope = scope; }
	
	public UnitContext(ParserContext global, CodeUnit unit)
	{
		this.globalContext = global;
		this.unit = unit;
	}
	
	/**
	 * Unwinds the current scope to its parent and
	 * updates the current type.
	 */
	public void unwind()
	{
		this.scope = this.scope.getParentScope();
		
		if (this.scope instanceof Type) {
			this.type = (Type)this.scope;
		}
		else this.type = null;
	}
	
	@Override
	public void freeze() { this.frozen = true; }
	@Override
	public boolean getIsFrozen() { return this.frozen; }
	
	@Override
	protected Object clone() throws CloneNotSupportedException
	{
		UnitContext output = new UnitContext(this.globalContext, this.unit);
		output.scope       = this.scope;
		output.type        = this.type;
		return output;
	}
	
}
