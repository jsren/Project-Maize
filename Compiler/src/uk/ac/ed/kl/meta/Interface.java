package uk.ac.ed.kl.meta;

import uk.ac.ed.kl.exceptions.CompilerError;
import uk.ac.ed.kl.exceptions.ObjectFrozenException;
import uk.ac.ed.kl.exceptions.TypeError;
import uk.ac.ed.kl.parser.Visibility;

public class Interface extends Type
{
	private static final long serialVersionUID = 1L;

	public Interface(String name, Visibility vis, TypeRef[] bases, Scope parentScope)
	{
		super(parentScope, name, true, false, false, vis, bases);
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
