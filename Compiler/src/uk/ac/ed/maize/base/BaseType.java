package uk.ac.ed.maize.base;

import uk.ac.ed.maize.exceptions.CompilerError;
import uk.ac.ed.maize.exceptions.TypeError;
import uk.ac.ed.maize.exceptions.InternalError;
import uk.ac.ed.maize.meta.Type;
import uk.ac.ed.maize.meta.TypeRef;
import uk.ac.ed.maize.meta.Visibility;

public abstract class BaseType extends Type
{
	private static final long serialVersionUID = 1L;
	
	public BaseType(String name, boolean abs, boolean sea, boolean sta) {
		super(null, null, name, abs, sea, sta, Visibility.Public, new TypeRef[0]);
	}
	
	@Override
	public boolean getIsPODType() { return true; }
	@Override
	public void assertIsPODType() throws TypeError { }
	
	public static Type getBaseType(TypeRef ref) throws CompilerError
	{
		if (ref.getTypeName().equals("bool")) return BooleanType.getType();
		else if (ref.getTypeName().equals("int")) return BooleanType.getType();
		else if (ref.getTypeName().equals("byte")) return BooleanType.getType();
		else if (ref.getTypeName().equals("void")) return VoidType.getType();
		
		else throw new InternalError("Unknown base type: "+ref.getTypeName());
	}
	
	public static int getPointerSize()      { return 4; }
	public static int getPointerAlignment() { return 4; }
	
	private static String[] baseTypes = new String[]
	{
		"bool",
		"byte",  "sbyte",
		"double",
		"float",
		"int",   "uint",
		"long",  "ulong",
		"short", "ushort",
		"void"
	};
	
	public static boolean getIsBaseType(TypeRef ref)
	{
		if (ref.getIsBaseType()) return true;
		
		String name = ref.getTypeName();
		
		for (String s : baseTypes) {
			if (s.equals(name)) return true;
		}
		return false;
	}
}
