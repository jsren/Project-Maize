package uk.ac.ed.maize.parser;

import java.io.File;

import uk.ac.ed.maize.CachedArrayList;
import uk.ac.ed.maize.Freezable;
import uk.ac.ed.maize.Log;
import uk.ac.ed.maize.exceptions.ObjectFrozenException;
import uk.ac.ed.maize.meta.CodeUnit;

public class CompilerContext implements Freezable
{
	private boolean frozen;
	
	public Log log;
	
	private CachedArrayList<CodeUnit> codeUnits;
	
	public CompilerContext() {
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
