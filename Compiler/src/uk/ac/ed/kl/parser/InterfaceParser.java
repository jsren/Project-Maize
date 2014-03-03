package uk.ac.ed.kl.parser;

import java.util.ArrayList;

import uk.ac.ed.kl.exceptions.CompilerError;
import uk.ac.ed.kl.exceptions.ErrorType;
import uk.ac.ed.kl.exceptions.ParserError;
import uk.ac.ed.kl.lexer.Token;
import uk.ac.ed.kl.lexer.TokenType;
import uk.ac.ed.kl.meta.Interface;
import uk.ac.ed.kl.meta.TypeRef;

public class InterfaceParser
{
	static void throwSyntaxError(String message, UnitContext context, Token token) throws ParserError {
		throw new ParserError(message, context, token, ErrorType.SyntaxError);
	}
	
	public static Interface parse(UnitContext context, Token[] expression) throws CompilerError
	{
		String     name       = null;
		Visibility visibility = null;
		
		ArrayList<String> bases = new ArrayList<String>();
		
		// Parsing states:
		boolean isKeywords     = true;
		boolean isName         = false;
		boolean isBaseClassDef = false;
		boolean needComma      = false;
		
		for (Token token : expression)
		{
			String    lexeme = token.getLexeme();
			TokenType type   = token.getType();
			
			// === ATTRIBUTES/MODIFIERS ===
			if (isKeywords)
			{
				if (type != TokenType.Keyword)
				{
					throwSyntaxError("Invalid interface declaration - invalid keyword", context, token);
				}
				
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
							throwSyntaxError("Invalid interface declaration -" +
									" more than one visibility modifier applied", context, token);
						}
						else visibility = newVis;
					}
					else
					{
						throwSyntaxError("Invalid interface declaration - invalid keyword", context, token);
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
				else throwSyntaxError("Invalid class declaration - expected class name", context, token);
			}
			// === BASE CLASSES ===
			else if (isBaseClassDef)
			{
				if (type == TokenType.Identifier)
				{
					if (needComma)
					{
						throwSyntaxError("Invalid interface declaration - bases must be comma-separated", context, token);
					}
					bases.add(lexeme);
					needComma = true;
				}
				else if (type != TokenType.Delimiter || lexeme.length() != 1 || lexeme.charAt(0) != ',')
				{
					throwSyntaxError("Invalid interface declaration - invalid token", context, token);
				}
				else needComma = false;
			}
			
			// Check for base classes...
			else if (type == TokenType.Delimiter)
			{
				if (lexeme.length() == 1 && lexeme.charAt(0) == ':') {
					isBaseClassDef = true;
				}
				else throwSyntaxError("Invalid token in interface declaration", context, token);
			}
			// === UNKNOWN TOKEN ===
			else throwSyntaxError("Invalid token in interface declaration", context, token);
		}
		
		if (name == null) {
			throwSyntaxError("Anonymous interfaces not permitted - name not given", context, expression[0]);
		}
		
		int line = expression[0].getLineIndex();
		int Char = expression[0].getCharIndex();
		
		TypeRef[] baseTypes = new TypeRef[bases.size()];
		for (int i = 0; i < baseTypes.length; i++)
		{
			Token token  = new Token(TokenType.Identifier, bases.get(i), line, Char);
			baseTypes[i] = new TypeRef(context, token, false, false, 0);
		}
		
		// Default to internal visibility if none given
		if (visibility == null) visibility = Visibility.Public;
		return new Interface(name, visibility, baseTypes, context.getCurrentScope());
	}
}
