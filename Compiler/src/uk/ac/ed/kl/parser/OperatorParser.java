package uk.ac.ed.kl.parser;

import java.util.EnumSet;

import uk.ac.ed.kl.exceptions.ErrorType;
import uk.ac.ed.kl.exceptions.ParserError;
import uk.ac.ed.kl.lexer.Token;
import uk.ac.ed.kl.meta.Operator;

public class OperatorParser
{
	public static Operator parse(UnitContext context, Token[] tokens) throws ParserError
	{
		Visibility visibility = null;
		EnumSet<Attribute> atts = EnumSet.noneOf(Attribute.class);
		
		if (visibility == null) visibility = Visibility.Public;
		
		throw new ParserError("Not yet implemented", null, ErrorType.Internal);
	}
}
