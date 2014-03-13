package uk.ac.ed.maize.parser;

import uk.ac.ed.maize.exceptions.CompilerError;
import uk.ac.ed.maize.exceptions.SyntaxError;
import uk.ac.ed.maize.lexer.Token;
import uk.ac.ed.maize.lexer.TokenType;

public final class UsingParser
{
	public static void parse(ParserContext context, Token[] tokens) throws CompilerError
	{
		if (context.getCurrentScope() != context.getCodeUnit()) 
			throw new SyntaxError("Invalid alias statement - can only place at file scope, consider moving", tokens[0]);
		
		Token filepath = null;
		
		boolean isKeyword = true;
		boolean isPath    = false;
		
		for (Token token : tokens)
		{
			if (isKeyword)
			{
				if (token.getType() != TokenType.Keyword || !"alias".equals(token.getLexeme())) {
					throw new SyntaxError("Parsing as using - expected 'using' keyword", token);
				}
				else
				{
					isKeyword = false;
					isPath    = true;
				}
			}
			else if (isPath)
			{
				if (token.getType() != TokenType.StringLiteral) {
					throw new SyntaxError("Invalid using statement - expected string literal giving the filepath", token);
				}
				filepath = token;
				isPath   = false;
			}
			else throw new SyntaxError("Invalid using statement - unexpected token", token);
		}
		
		if (filepath == null) {
			throw new SyntaxError("Invalid using statement - expected string literal giving the filepath", tokens[0]);
		}
		
		context.getCodeUnit().addReference(filepath);
	}
}
