package uk.ac.ed.maize.parser;

import java.util.EnumSet;

import uk.ac.ed.maize.exceptions.CompilerError;
import uk.ac.ed.maize.lexer.Token;
import uk.ac.ed.maize.meta.Operator;
import uk.ac.ed.maize.meta.Visibility;

public class OperatorParser
{
	public static Operator parse(ParserContext context, Token[] tokens) throws CompilerError
	{
		Visibility visibility = null;
		EnumSet<Attribute> atts = EnumSet.noneOf(Attribute.class);
		
		if (visibility == null) visibility = Visibility.Public;
		
		// TODO
		throw new InternalError("Operator syntax not yet implemented");
	}
}
