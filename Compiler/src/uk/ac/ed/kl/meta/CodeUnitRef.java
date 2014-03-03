package uk.ac.ed.kl.meta;

import uk.ac.ed.kl.Freezable;
import uk.ac.ed.kl.exceptions.ObjectFrozenException;
import uk.ac.ed.kl.lexer.Token;

public class CodeUnitRef implements Freezable
{
	private boolean frozen;
	
	private CodeUnit resolvedUnit;
	private Token    unitPath;
	
	public boolean  getIsUnitResolved() { return this.resolvedUnit != null; }
	public CodeUnit getResolvedUnit()   { return this.resolvedUnit; }
	public String   getCodeUnitPath()   { return this.unitPath.getLexeme(); }
	public Token    getToken()          { return this.unitPath; }
	
	public CodeUnitRef(Token unitPath)
	{
		this.unitPath = unitPath;
	}
	
	public void resolve(CodeUnit unit) throws ObjectFrozenException
	{
		if (this.frozen) throw new ObjectFrozenException(this);
			
		this.resolvedUnit = unit;
		this.freeze();
	}

	@Override
	public void freeze() { this.frozen = true; }
	@Override
	public boolean getIsFrozen() { return this.frozen; }
	
}
