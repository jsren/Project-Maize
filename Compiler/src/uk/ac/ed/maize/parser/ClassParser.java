package uk.ac.ed.maize.parser;

import java.util.ArrayList;
import java.util.EnumSet;

import uk.ac.ed.maize.exceptions.CompilerError;
import uk.ac.ed.maize.exceptions.SyntaxError;
import uk.ac.ed.maize.exceptions.InternalError;
import uk.ac.ed.maize.lexer.Token;
import uk.ac.ed.maize.lexer.TokenType;
import uk.ac.ed.maize.meta.Type;
import uk.ac.ed.maize.meta.TypeRef;
import uk.ac.ed.maize.meta.Visibility;

public class ClassParser
{
	public static Type parse(ParserContext context, Token[] tokens) throws CompilerError
	{
		String     name       = null;
		Visibility visibility = null;
		
		ArrayList<Token>   bases      = new ArrayList<Token>();
		EnumSet<Attribute> attributes = EnumSet.noneOf(Attribute.class);
		
		// Parsing states:
		boolean isKeywords     = true;
		boolean isName         = false;
		boolean isBaseClassDef = false;
		boolean needComma      = false;
		
		for (Token token : tokens)
		{
			String    lexeme = token.getLexeme();
			TokenType type   = token.getType();
			
			// === ATTRIBUTES/MODIFIERS ===
			if (isKeywords)
			{
				if (type != TokenType.Keyword) {
					throw new SyntaxError("Invalid class declaration - invalid keyword", token);
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
									throw new SyntaxError("Invalid class declaration -" +
											" more than one visibility modifier applied", token);
								}
								else visibility = newVis;
							}
							else throw new InternalError("Invalid class declaration - unknown keyword", token);
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
					isName = false; // Look for next state
				}
				else throw new SyntaxError("Invalid class declaration - expected class name", token);
			}
			
			// === BASE CLASSES ===
			else if (isBaseClassDef)
			{
				if (type == TokenType.Identifier)
				{
					if (needComma) {
						throw new SyntaxError("Invalid class declaration - bases must be comma-separated", token);
					}
					bases.add(token);
					needComma = true;
				}
				else if (type != TokenType.Delimiter || lexeme.length() != 1 || lexeme.charAt(0) != ',') {
					throw new SyntaxError("Invalid class declaration - invalid token", token);
				}
				else needComma = false;
			}
			
			// Check for base classes...
			else if (type == TokenType.Delimiter)
			{
				if (lexeme.length() == 1 && lexeme.charAt(0) == ':') {
					isBaseClassDef = true;
				}
				else throw new SyntaxError("Invalid token in class declaration", token);
			}
			// === UNKNOWN TOKEN ===
			else throw new SyntaxError("Invalid token in class declaration", token);
		}
		
		if (name == null) {
			throw new SyntaxError("Anonymous classes not permitted - class name not given", tokens[0]);
		}
		
		// === FINISH UP! ===
		TypeRef[] baseTypes = new TypeRef[bases.size()];
		for (int i = 0; i < baseTypes.length; i++)
		{
			Token t = bases.get(i);
			baseTypes[i] = new TypeRef(t, false, false, 0);
		}
		
		// Default to internal visibility if none given
		if (visibility == null) visibility = Visibility.Public;
		
		int line = tokens[0].getLineIndex();
		
		return new Type(context.getCurrentScope(), line, name, attributes.contains(Attribute.Abstract),
				attributes.contains(Attribute.Sealed), attributes.contains(Attribute.Static),
				visibility, baseTypes);
	}
}
