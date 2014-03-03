package uk.ac.ed.kl.parser;

import uk.ac.ed.kl.exceptions.ErrorType;
import uk.ac.ed.kl.exceptions.ParserError;
import uk.ac.ed.kl.lexer.Token;
import uk.ac.ed.kl.lexer.TokenType;
import uk.ac.ed.kl.meta.Namespace;
import uk.ac.ed.kl.meta.Scope;

public class NamespaceParser
{
	public static Namespace parse(UnitContext context, Token[] expression) throws ParserError
	{
		if (!expression[0].getLexeme().equals("namespace"))
		{
			throw new ParserError("Invalid namespace declaration - invalid keyword",
					context, expression[0], ErrorType.SyntaxError);
			
		}
		if (expression.length < 2 || expression[1].getType() != TokenType.Identifier)
		{
			throw new ParserError("Invalid namespace declaration - expected name",
					context, expression[1], ErrorType.SyntaxError);
		}
		if (expression.length != 2)
		{
			throw new ParserError("Invalid namespace declaration - unexpected token",
					context, expression[2], ErrorType.SyntaxError);
		}
		
		String name = expression[1].getLexeme();
		return new Namespace(name, context.getCurrentScope());
	}
}
