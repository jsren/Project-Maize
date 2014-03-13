package uk.ac.ed.maize.parser;

import java.util.EnumSet;

import uk.ac.ed.maize.exceptions.CompilerError;
import uk.ac.ed.maize.exceptions.InternalError;
import uk.ac.ed.maize.exceptions.SyntaxError;
import uk.ac.ed.maize.lexer.Token;
import uk.ac.ed.maize.lexer.TokenType;
import uk.ac.ed.maize.meta.Type;
import uk.ac.ed.maize.meta.TypeRef;
import uk.ac.ed.maize.meta.Visibility;

public class StructParser
{
	public static Type parse(ParserContext context, Token[] tokens) throws CompilerError
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
				if (type != TokenType.Keyword) {
					throw new SyntaxError("Invalid struct declaration - invalid keyword", token);
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
									throw new SyntaxError("Invalid struct declaration -" +
											" more than one visibility modifier applied", token);
								}
								else visibility = newVis;
							}
							else throw new InternalError("Invalid struct declaration - unknown keyword", token);
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
				else throw new SyntaxError("Invalid struct declaration - expected struct name", token);
			}
			else throw new SyntaxError("Unexpected token", token);
		}
		
		int line = tokens[0].getLineIndex();
		return new Type(context.getCurrentScope(), line, name, false, true, false, visibility, new TypeRef[0]);
	}
}
