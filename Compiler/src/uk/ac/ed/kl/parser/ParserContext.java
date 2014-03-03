package uk.ac.ed.kl.parser;

import java.io.File;

import uk.ac.ed.kl.CachedArrayList;
import uk.ac.ed.kl.Freezable;
import uk.ac.ed.kl.Log;
import uk.ac.ed.kl.exceptions.ObjectFrozenException;
import uk.ac.ed.kl.meta.CodeUnit;

public class ParserContext implements Freezable
{
	private boolean frozen;
	
	public Log log;
	
	private CachedArrayList<CodeUnit> codeUnits;
	
	public ParserContext() {
		this.codeUnits = new CachedArrayList<>(CodeUnit.class);
	}
	
	public CodeUnit[] getLoadedCodeUnits() {
		return this.codeUnits.toArray();
	}
	
	public CodeUnit getCodeUnit(File filepath)
	{
		String name = '<' + filepath.toString() + '>';
		for (CodeUnit cu : this.codeUnits)
		{
			if (name.equals(cu.getName())) return cu;
		}
		return null;
	}
	
	public void addCodeUnit(CodeUnit unit) throws ObjectFrozenException
	{
		if (this.frozen) throw new ObjectFrozenException(this);
		else this.codeUnits.add(unit);
	}
	
	@Override
	public void freeze() { this.frozen = true; }
	@Override
	public boolean getIsFrozen() { return this.frozen; }
}
