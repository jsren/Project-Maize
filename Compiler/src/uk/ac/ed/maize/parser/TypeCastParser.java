package uk.ac.ed.maize.parser;

import uk.ac.ed.maize.code.TypeCast;
import uk.ac.ed.maize.exceptions.CompilerError;
import uk.ac.ed.maize.lexer.Token;
import uk.ac.ed.maize.meta.TypeRef;

public class TypeCastParser
{
	public static final TypeCast parse(ParserContext context, Token[] tokens) throws CompilerError
	{
		TypeRef type = TypeRefParser.parse(context, tokens);
		
		return new TypeCast(context, type);
	}
}
