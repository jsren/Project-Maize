package uk.ac.ed.kl.parser;

import uk.ac.ed.kl.exceptions.ErrorType;
import uk.ac.ed.kl.exceptions.ParserError;
import uk.ac.ed.kl.lexer.Token;
import uk.ac.ed.kl.lexer.TokenType;
import uk.ac.ed.kl.meta.Enum;
import uk.ac.ed.kl.meta.EnumConst;
import uk.ac.ed.kl.meta.Member;
import uk.ac.ed.kl.meta.Scope;

public class EnumParser 
{
	public static Enum parse(UnitContext context, Token[] expression) throws ParserError
	{
		String     name       = null;
		Visibility visibility = null;

		// State variables
		boolean readName = false;
		boolean finished = false;
		
		for (Token token : expression)
		{
			TokenType type   = token.getType();
			String    lexeme = token.getLexeme();
			
			if (finished) throw new ParserError("Invalid enum declaration - unexpected token",
					context, token, ErrorType.SyntaxError);
			
			if (readName)
			{
				if (type == TokenType.Identifier)
				{
					name     = lexeme;
					readName = false;
					finished = true;
				}
				else throw new ParserError("Invalid enum declaration - expected enum name",
						context, token, ErrorType.SyntaxError);
			}
			else if (type == TokenType.Keyword)
			{
				// Move on to reading name
				if (lexeme.equals("enum")) readName = true;
				
				else
				{
					Visibility vis = Visibility.parse(expression[0].getLexeme());
					
					// Don't accept any other type of attribute/modifier
					if (vis == null) throw new ParserError("Invalid enum declaration - invalid keyword",
							context, token, ErrorType.SyntaxError);
					
					// Only one modifier can be applied
					else if (visibility != null) throw new ParserError("Invalid enum declaration -" +
							" more than one visibility modifier applied", context, token, ErrorType.SyntaxError);
					
					else visibility = vis;
				}
			}
			else throw new ParserError("Invalid enum decalaration - invalid token", 
					context, token, ErrorType.SyntaxError);
		}
		
		return new Enum(context.getCurrentScope(), name, visibility);
	}

	public static Member parseElement(Token[] tokens, Scope parentScope)
	{
		// Assume parentScope is Enum
		Enum parent = (Enum)parentScope;
		
		long value;
		
		// Automatic value
		if (tokens.length == 1)
		{
			Member[] members = parent.getMembers();
			if (members.length == 0) value = 0;
			
			EnumConst last = (EnumConst)members[members.length -1];
			
		}
		for (Token token : tokens)
		{
			
		}
		return null;
	}
}
