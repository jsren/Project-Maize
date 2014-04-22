package uk.ac.ed.maize.parser;

import java.util.ArrayList;

import uk.ac.ed.maize.exceptions.CompilerError;
import uk.ac.ed.maize.exceptions.SyntaxError;
import uk.ac.ed.maize.lexer.Token;
import uk.ac.ed.maize.lexer.TokenType;
import uk.ac.ed.maize.meta.Interface;
import uk.ac.ed.maize.meta.TypeRef;
import uk.ac.ed.maize.meta.Visibility;

public class InterfaceParser
{
	public static Interface parse(ParserContext context, Token[] tokens) throws CompilerError
	{
		String     name       = null;
		Visibility visibility = null;
		
		ArrayList<String> bases = new ArrayList<String>();
		
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
					throw new SyntaxError("Invalid interface declaration - invalid keyword", token);
				
				if (lexeme.equals("interface")) {
					isKeywords = false; isName = true;
				}
				else
				{					
					// Try and parse a visibility modifier
					Visibility newVis = Visibility.parse(token.getLexeme());
					if (newVis != null)
					{
						if (visibility != null)
						{
							throw new SyntaxError("Invalid interface declaration -" +
									" more than one visibility modifier applied", token);
						}
						else visibility = newVis;
					}
					else throw new SyntaxError("Invalid interface declaration - invalid keyword", token);
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
					if (needComma)
					{
						throw new SyntaxError("Invalid interface declaration - bases must be comma-separated", token);
					}
					bases.add(lexeme);
					needComma = true;
				}
				else if (type != TokenType.Delimiter || lexeme.length() != 1 || lexeme.charAt(0) != ',')
				{
					throw new SyntaxError("Invalid interface declaration - invalid token", token);
				}
				else needComma = false;
			}
			
			// Check for base classes...
			else if (type == TokenType.Delimiter)
			{
				if (lexeme.length() == 1 && lexeme.charAt(0) == ':') {
					isBaseClassDef = true;
				}
				else throw new SyntaxError("Invalid token in interface declaration", token);
			}
			// === UNKNOWN TOKEN ===
			else throw new SyntaxError("Invalid token in interface declaration", token);
		}
		
		if (name == null) {
			throw new SyntaxError("Anonymous interfaces not permitted - name not given", tokens[0]);
		}
		
		int line = tokens[0].getLineIndex();
		int Char = tokens[0].getCharIndex();
		
		TypeRef[] baseTypes = new TypeRef[bases.size()];
		for (int i = 0; i < baseTypes.length; i++)
		{
			Token token  = new Token(TokenType.Identifier, bases.get(i), line, Char);
			baseTypes[i] = new TypeRef(token, false, false, 0);
		}
		
		// Default to internal visibility if none given
		if (visibility == null) visibility = Visibility.Public;
		return new Interface(context.getCurrentScope(), tokens[0].getLineIndex(), name, visibility, baseTypes);
	}
}
