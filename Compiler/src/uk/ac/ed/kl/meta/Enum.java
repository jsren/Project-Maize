package uk.ac.ed.kl.meta;

import uk.ac.ed.kl.base.EnumType;
import uk.ac.ed.kl.exceptions.CompilerError;
import uk.ac.ed.kl.exceptions.ObjectFrozenException;
import uk.ac.ed.kl.exceptions.TypeError;
import uk.ac.ed.kl.parser.Visibility;

public class Enum extends Type
{
	private static final long serialVersionUID = 1L;

	public Enum(Scope parentScope, String name, Visibility vis)
	{
		super(parentScope, name, false, true, false, vis, new TypeRef[] { EnumType.getBasicTypeRef() });
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
