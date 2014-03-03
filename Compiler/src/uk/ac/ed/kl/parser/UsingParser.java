package uk.ac.ed.kl.parser;

import uk.ac.ed.kl.exceptions.ErrorType;
import uk.ac.ed.kl.exceptions.ParserError;
import uk.ac.ed.kl.lexer.Token;
import uk.ac.ed.kl.lexer.TokenType;

public final class UsingParser
{
	public static void parse(UnitContext context, Token[] tokens) throws ParserError
	{
		if (context.getCurrentScope() != context.getCodeUnit()) 
			throw new ParserError("Invalid alias statement - can only place at file scope, consider moving", context,
					tokens[0], ErrorType.SyntaxError);
		
		Token filepath = null;
		
		boolean isKeyword = true;
		boolean isPath    = false;
		
		for (Token token : tokens)
		{
			if (isKeyword)
			{
				if (token.getType() != TokenType.Keyword || !"alias".equals(token.getLexeme()))
				{
					throw new ParserError("Parsing as using - expected 'using' keyword", context, token, ErrorType.Internal);
				}
				else
				{
					isKeyword = false;
					isPath    = true;
				}
			}
			else if (isPath)
			{
				if (token.getType() != TokenType.StringLiteral)
				{
					throw new ParserError("Invalid using statement - expected string literal giving the filepath", context, 
							token, ErrorType.SyntaxError);
				}
				filepath = token;
				isPath   = false;
			}
			else throw new ParserError("Invalid using statement - unexpected token", context, token, ErrorType.SyntaxError);
		}
		
		if (filepath == null)
		{
			throw new ParserError("Invalid using statement - expected string literal giving the filepath", context,
					tokens[0], ErrorType.SyntaxError);
		}
		
		context.getCodeUnit().addReference(filepath);
	}
}
