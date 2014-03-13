package uk.ac.ed.maize.parser;

import uk.ac.ed.maize.exceptions.CompilerError;
import uk.ac.ed.maize.exceptions.SyntaxError;
import uk.ac.ed.maize.lexer.Token;
import uk.ac.ed.maize.lexer.TokenType;
import uk.ac.ed.maize.meta.Namespace;

public class NamespaceParser
{
	public static Namespace parse(ParserContext context, Token[] expression) 
			throws CompilerError
	{
		if (!expression[0].getLexeme().equals("namespace"))
		{
			throw new SyntaxError("Invalid namespace declaration - invalid keyword",
					expression[0]);
			
		}
		if (expression.length < 2 || expression[1].getType() != TokenType.Identifier)
		{
			throw new SyntaxError("Invalid namespace declaration - expected name",
					expression[1]);
		}
		if (expression.length != 2)
		{
			throw new SyntaxError("Invalid namespace declaration - unexpected token",
					expression[2]);
		}
		
		String name = expression[1].getLexeme();
		return new Namespace(name, context.getCurrentScope());
	}
}
