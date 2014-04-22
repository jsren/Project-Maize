package uk.ac.ed.maize.base;

import java.util.EnumSet;

import uk.ac.ed.maize.meta.Method;
import uk.ac.ed.maize.meta.Parameter;
import uk.ac.ed.maize.meta.Type;
import uk.ac.ed.maize.meta.TypeRef;
import uk.ac.ed.maize.meta.Visibility;
import uk.ac.ed.maize.parser.Attribute;

public final class EnumType
{
	private static Type    type;
	private static TypeRef typeRef;
	
	public  static Type    getType()         { return type; }
	public  static TypeRef getBasicTypeRef() { return typeRef; }
	
	static
	{
		try
		{
			EnumSet<Attribute> builtin = EnumSet.of(Attribute.Builtin);
			EnumSet<Attribute> Static  = EnumSet.of(Attribute.Static, Attribute.Builtin);
			
			type    = new Type(null, null, "Enum", true, false, false, Visibility.Public, new TypeRef[0]);
			typeRef = new TypeRef(type, false, true, 0);
		} 
		catch (Exception e)
		{
			System.out.println("[ERROR] Error creating base type 'Enum'");
			System.exit(-1);
		}
		
	}
}
