package uk.ac.ed.kl.parser;

import java.util.ArrayList;
import java.util.EnumSet;

import uk.ac.ed.kl.exceptions.CompilerError;
import uk.ac.ed.kl.exceptions.ErrorType;
import uk.ac.ed.kl.exceptions.ParserError;
import uk.ac.ed.kl.lexer.Token;
import uk.ac.ed.kl.lexer.TokenType;
import uk.ac.ed.kl.meta.Type;
import uk.ac.ed.kl.meta.TypeRef;

public class ClassParser
{
	public static Type parse(UnitContext context, Token[] tokens) throws CompilerError
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
				if (type != TokenType.Keyword)
				{
					throw new ParserError("Invalid class declaration - invalid keyword", context,
							token, ErrorType.SyntaxError);
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
									throw new ParserError("Invalid class declaration -" +
											" more than one visibility modifier applied", context, 
											token, ErrorType.SyntaxError);
								}
								else visibility = newVis;
							}
							else
							{
								throw new ParserError("Invalid class declaration - unknown keyword", context, 
										token, ErrorType.Internal);
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
					isName = false; // Look for next state
				}
				else throw new ParserError("Invalid class declaration - expected class name", context,
						token, ErrorType.SyntaxError);
			}
			
			// === BASE CLASSES ===
			else if (isBaseClassDef)
			{
				if (type == TokenType.Identifier)
				{
					if (needComma)
					{
						throw new ParserError("Invalid class declaration - bases must be comma-separated", context,
								token, ErrorType.SyntaxError);
					}
					bases.add(token);
					needComma = true;
				}
				else if (type != TokenType.Delimiter || lexeme.length() != 1 || lexeme.charAt(0) != ',')
				{
					throw new ParserError("Invalid class declaration - invalid token", context, token,
							ErrorType.SyntaxError);
				}
				else needComma = false;
			}
			
			// Check for base classes...
			else if (type == TokenType.Delimiter)
			{
				if (lexeme.length() == 1 && lexeme.charAt(0) == ':') {
					isBaseClassDef = true;
				}
				else throw new ParserError("Invalid token in class declaration", context, token, ErrorType.SyntaxError);
			}
			// === UNKNOWN TOKEN ===
			else throw new ParserError("Invalid token in class declaration", context, token, ErrorType.SyntaxError);
		}
		
		if (name == null)
		{
			throw new ParserError("Anonymous classes not permitted - class name not given", 
					context, tokens[0], ErrorType.SyntaxError);
		}
		
		// === FINISH UP! ===
		TypeRef[] baseTypes = new TypeRef[bases.size()];
		for (int i = 0; i < baseTypes.length; i++)
		{
			Token t = bases.get(i);
			baseTypes[i] = new TypeRef(context, t, false, false, 0);
		}
		
		// Default to internal visibility if none given
		if (visibility == null) visibility = Visibility.Public;
		
		return new Type(context.getCurrentScope(), name, attributes.contains(Attribute.Abstract),
				attributes.contains(Attribute.Sealed), attributes.contains(Attribute.Static),
				visibility, baseTypes);
	}
}
