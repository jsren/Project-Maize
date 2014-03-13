package uk.ac.ed.maize.meta;

import uk.ac.ed.maize.base.EnumType;
import uk.ac.ed.maize.exceptions.CompilerError;
import uk.ac.ed.maize.exceptions.ObjectFrozenException;
import uk.ac.ed.maize.exceptions.TypeError;

public class Enum extends Type
{
	private static final long serialVersionUID = 1L;

	public Enum(Scope parentScope, int line, String name, Visibility vis)
	{
		super(parentScope, line, name, false, true, false, vis, 
				new TypeRef[] { EnumType.getBasicTypeRef() });
	}
	
	@Override
	public void addMember(Member child) throws CompilerError, ObjectFrozenException
	{
		if (child.getType() == MemberType.EnumConst) {
			super.addMember(child);
		}
		else throw new TypeError("Enumerations can only contain EnumElements", child);
	}
}
