package uk.ac.ed.maize.parser;

import uk.ac.ed.maize.Freezable;
import uk.ac.ed.maize.meta.CodeUnit;
import uk.ac.ed.maize.meta.Member;
import uk.ac.ed.maize.meta.Scope;
import uk.ac.ed.maize.meta.Type;

public class ParserContext implements Freezable
{
	private CodeUnit unit;
	private Type     type;
	private Scope    scope;
	private Member   member;
	private boolean  frozen;
	
	private CompilerContext globalContext;
	
	public CodeUnit getCodeUnit()     { return this.unit; }
	public Type     getCurrentType()  { return this.type; }
	public Scope    getCurrentScope() { return this.scope; }
	public Member   getCurrentMember(){ return this.member; }
	
	public CompilerContext getGlobalContext() { return this.globalContext; }
	
	public void setCurrentType(Type type)       { this.type = type; }
	public void setCurrentScope(Scope scope)    { this.scope = scope; }
	public void setCurrentMember(Member member) { this.member = member; }
	
	public ParserContext(CompilerContext global, CodeUnit unit)
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
		ParserContext output = new ParserContext(this.globalContext, this.unit);
		output.scope         = this.scope;
		output.type          = this.type;
		output.member 		 = this.member;
		return output;
	}
	
}
