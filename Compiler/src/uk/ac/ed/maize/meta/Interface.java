package uk.ac.ed.maize.meta;

import uk.ac.ed.maize.exceptions.CompilerError;
import uk.ac.ed.maize.exceptions.ObjectFrozenException;
import uk.ac.ed.maize.exceptions.TypeError;

public class Interface extends Type
{
	private static final long serialVersionUID = 1L;

	public Interface(Scope parentScope, int line, String name, Visibility vis, TypeRef[] bases)
	{
		super(parentScope, line, name, true, false, false, vis, bases);
	}
	
	@Override
	public Integer getSize() { return 0; }
	
	@Override
	public void addSubtype(Type subtype) throws CompilerError, ObjectFrozenException
	{
		throw new TypeError("Interfaces cannot contain sub-types or sub-interfaces", subtype);
	}
	
	@Override
	public void addMember(Member child) throws CompilerError, ObjectFrozenException
	{
		if (this.getIsFrozen()) throw new ObjectFrozenException(this);
		
		if (child.getVisibility() != Visibility.Public)
			throw new TypeError("Interface methods must be declared public or left unmodified", child);
		if (!child.getAttributes().isEmpty())
			throw new TypeError("Invalid interface method attribute", child);
		if (child.getType() != MemberType.Function)
			throw new TypeError("Interfaces can only contain methods", child);
		
		super.addMember(child);
	}
	
}
