package uk.ac.ed.maize.parser;

import uk.ac.ed.maize.exceptions.CompilerError;
import uk.ac.ed.maize.exceptions.SyntaxError;
import uk.ac.ed.maize.lexer.Token;
import uk.ac.ed.maize.lexer.TokenType;
import uk.ac.ed.maize.meta.Enum;
import uk.ac.ed.maize.meta.EnumConst;
import uk.ac.ed.maize.meta.Member;
import uk.ac.ed.maize.meta.Scope;
import uk.ac.ed.maize.meta.Visibility;

public class EnumParser 
{
	public static Enum parse(ParserContext context, Token[] tokens) throws CompilerError
	{
		String     name       = null;
		Visibility visibility = null;

		// State variables
		boolean readName = false;
		boolean finished = false;
		
		for (Token token : tokens)
		{
			TokenType type   = token.getType();
			String    lexeme = token.getLexeme();
			
			if (finished) {
				throw new SyntaxError("Invalid enum declaration - unexpected token", token);
			}
			if (readName)
			{
				if (type == TokenType.Identifier)
				{
					name     = lexeme;
					readName = false;
					finished = true;
				}
				else throw new SyntaxError("Invalid enum declaration - expected enum name", token);
			}
			else if (type == TokenType.Keyword)
			{
				// Move on to reading name
				if (lexeme.equals("enum")) readName = true;
				
				else
				{
					Visibility vis = Visibility.parse(tokens[0].getLexeme());
					
					// Don't accept any other type of attribute/modifier
					if (vis == null) throw new SyntaxError("Invalid enum declaration - invalid keyword", token);
					
					// Only one modifier can be applied
					else if (visibility != null) {
						throw new SyntaxError("Invalid enum declaration -" +
								" more than one visibility modifier applied", token);
					}
					else visibility = vis;
				}
			}
			else throw new SyntaxError("Invalid enum decalaration - invalid token", token);
		}
		
		return new Enum(context.getCurrentScope(), tokens[0].getLineIndex(), name, visibility);
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
