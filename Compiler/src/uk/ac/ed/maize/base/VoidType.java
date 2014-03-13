package uk.ac.ed.maize.base;

import uk.ac.ed.maize.exceptions.CompilerError;
import uk.ac.ed.maize.meta.Type;
import uk.ac.ed.maize.meta.TypeRef;
import uk.ac.ed.maize.meta.Visibility;

public class VoidType extends BaseType
{
	private static final long serialVersionUID = 1L;

	private static Type type;

	static
	{
		try
		{
			type = new Type(null, null, "void", false, true, false, Visibility.Public, new TypeRef[0]);
		}
		catch (Exception e)
		{
			System.out.println("[ERROR] Error creating base type 'void'");
			System.exit(-1);
		}
	}
	
	public static Type getType() { 
		return type; 
	}
	public static TypeRef getTypeRef(int ptrLevel) throws CompilerError { 
		return new TypeRef(type, false, true, ptrLevel); 
	}
	
	// == INSTANCE ==
	public VoidType()
	{
		super("void", false, true, false);
	}
}
