package uk.ac.ed.maize.base;

import java.util.EnumSet;

import uk.ac.ed.maize.exceptions.EvaluationError;
import uk.ac.ed.maize.lexer.Token;
import uk.ac.ed.maize.meta.Operator;
import uk.ac.ed.maize.meta.Type;
import uk.ac.ed.maize.meta.TypeRef;
import uk.ac.ed.maize.meta.Visibility;
import uk.ac.ed.maize.parser.Attribute;

public final class BooleanType extends BaseType
{ 
	private static final long serialVersionUID = 1L;
	
	private static Type    type;
	private static TypeRef typeRef;
	
	public  static Type    getType()         { return type; }
	public  static TypeRef getBasicTypeRef() { return typeRef; }
	
	static
	{
		try
		{
			type    = new Type(null, null, "bool", false, true, false, Visibility.Public, new TypeRef[0]);
			typeRef = new TypeRef(type, false, true, 0);
			
			type.addMember(new Operator(0, null, "==", typeRef, Visibility.Public, EnumSet.allOf(Attribute.class)));
		}
		catch (Exception e)
		{
			System.out.println("[ERROR] Error creating base type 'bool'");
			System.exit(-1);
		}
	}
	
	public BooleanType()
	{
		super("bool", false, true, false);
	}
	
	public boolean equalsOp(Token arg1, Token arg2)
	{
		return arg1.getLexeme().equals(arg2);
	}
	
	public boolean logicalInvertOp(Token arg1) throws EvaluationError
	{
		String lexeme = arg1.getLexeme();
		if (lexeme.equals("true")) return true;
		else if (lexeme.equals("false")) return false;
		
		else throw new EvaluationError("Invalid boolean value: '"+lexeme+'\'');
	}
	
	
}
