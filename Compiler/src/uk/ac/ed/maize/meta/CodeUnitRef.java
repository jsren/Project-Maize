package uk.ac.ed.maize.meta;

import uk.ac.ed.maize.MetaReference;
import uk.ac.ed.maize.exceptions.ObjectFrozenException;
import uk.ac.ed.maize.lexer.Token;

public class CodeUnitRef implements MetaReference<CodeUnit>
{
	private boolean  frozen;
	private Token    unitPath;
	private CodeUnit resolvedUnit;
	
	public String   getCodeUnitPath() { return this.unitPath.getLexeme(); }
	public Token    getToken()        { return this.unitPath; }
	public boolean  getIsResolved()   { return this.resolvedUnit != null; }
	public CodeUnit getResolvedUnit() { return this.resolvedUnit; }
	
	public CodeUnitRef(Token unitPath)
	{
		this.unitPath = unitPath;
	}
	
	public void resolveReference(CodeUnit unit) throws ObjectFrozenException
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
