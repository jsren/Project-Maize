package uk.ac.ed.kl.parser;

import java.util.EnumSet;

import uk.ac.ed.kl.exceptions.ErrorType;
import uk.ac.ed.kl.exceptions.ParserError;
import uk.ac.ed.kl.lexer.Token;
import uk.ac.ed.kl.lexer.TokenType;
import uk.ac.ed.kl.meta.Scope;
import uk.ac.ed.kl.meta.Type;
import uk.ac.ed.kl.meta.TypeRef;

public class StructParser
{
	public static Type parse(UnitContext context, Token[] tokens) throws ParserError
	{
		String     name       = null;
		Visibility visibility = null;
		
		// Structs are sealed - for now at least
		EnumSet<Attribute> attributes = EnumSet.of(Attribute.Sealed);
		
		// Parsing states:
		boolean isKeywords = true;
		boolean isName     = false;
		
		for (Token token : tokens)
		{
			String    lexeme = token.getLexeme();
			TokenType type   = token.getType();
			
			// === ATTRIBUTES/MODIFIERS ===
			if (isKeywords)
			{
				if (type != TokenType.Keyword)
				{
					throw new ParserError("Invalid struct declaration - invalid keyword", context, token,
							ErrorType.SyntaxError);
				}
				else
				{
					// Move on to parsing name
					if (lexeme.equals("class")) {
						isKeywords = false; isName = true;
					}
					else
					{
						// Try and parse an attribute
						Attribute newAtt = Attribute.parse(lexeme);
						if (newAtt != null) attributes.add(newAtt);
						
						// Otherwise try and parse a visibility modifier
						else
						{
							Visibility newVis = Visibility.parse(token.getLexeme());
							if (newVis != null)
							{
								if (visibility != null)
								{
									throw new ParserError("Invalid struct declaration -" +
											" more than one visibility modifier applied", context, token, ErrorType.SyntaxError);
								}
								else visibility = newVis;
							}
							else
							{
								throw new ParserError("Invalid struct declaration - unknown keyword", context, token,
										ErrorType.Internal);
							}
						}
					}
				}
			}
			
			// === NAME PARSING ===
			else if (isName) 
			{
				if (type == TokenType.Identifier)
				{
					name   = lexeme;
					isName = false;
				}
				else throw new ParserError("Invalid struct declaration - expected struct name", context, token,
						ErrorType.SyntaxError);
			}
			else throw new ParserError("Unexpected token", context, token, ErrorType.SyntaxError);
		}
		return new Type(context.getCurrentScope(), name, false, true, false, visibility, new TypeRef[0]);
	}
}
