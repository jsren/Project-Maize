package uk.ac.ed.maize.meta;

import java.io.File;
import java.util.HashMap;

import uk.ac.ed.maize.CachedArrayList;
import uk.ac.ed.maize.exceptions.CompilerError;
import uk.ac.ed.maize.exceptions.NameError;
import uk.ac.ed.maize.exceptions.ObjectFrozenException;
import uk.ac.ed.maize.lexer.Token;

public class CodeUnit extends Namespace
{
	private static final long serialVersionUID = 1L;
	
	private File unitFile;
	private CachedArrayList<CodeUnitRef> references;
	private HashMap<String, TypeRef> aliases;
	
	public File getFile() { return this.unitFile; }
	
	public CodeUnit(File filepath)
	{
		super('<' + filepath.toString() + '>', null);
		this.unitFile   = filepath;
		this.aliases    = new HashMap<String, TypeRef>();
		this.references = new CachedArrayList<>(CodeUnitRef.class);
	}
	
	public void addAlias(TypeRef type, Token token) throws ObjectFrozenException, CompilerError
	{
		this.assertFrozen();
		
		String alias = token.getLexeme();
		
		if (aliases.containsKey(alias)) {
			throw new NameError("Alias with same name already declared in this unit", token);
		}
		else aliases.put(alias, type);
	}
	
	public TypeRef tryGetAlias(String alias)
	{
		if (aliases.containsKey(alias)) {
			return aliases.get(alias);
		}
		else return null;
	}
	
	public TypeRef[] getAliasedTypes() {
		return aliases.entrySet().toArray(new TypeRef[0]);
	}
	
	public void addReference(Token codeUnitPath) {
		this.references.add(new CodeUnitRef(codeUnitPath));
	}
	
	public CodeUnitRef[] getReferences() {
		return this.references.toArray();
	}
	
	public static CodeUnit traceCodeUnit(Scope scope)
	{
		while (true)
		{
			Scope parent = scope.getParentScope();
			if (parent == null)
			{
				if (scope instanceof CodeUnit) return (CodeUnit)scope;
				else return null;
			}
			else scope = parent;
		}
	}
	
	public static String traceFilepath(Scope scope)
	{
		CodeUnit cu = traceCodeUnit(scope);
		return cu == null ? null : cu.getName();
	}
	
}
