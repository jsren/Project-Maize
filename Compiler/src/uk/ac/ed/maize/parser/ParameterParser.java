package uk.ac.ed.maize.parser;

import uk.ac.ed.maize.exceptions.CompilerError;
import uk.ac.ed.maize.exceptions.SyntaxError;
import uk.ac.ed.maize.exceptions.InternalError;
import uk.ac.ed.maize.lexer.Token;
import uk.ac.ed.maize.lexer.TokenType;
import uk.ac.ed.maize.meta.Parameter;
import uk.ac.ed.maize.meta.TypeRef;

public final class ParameterParser
{
	public static Parameter parse(ParserContext context, Token[] tokens) throws CompilerError
	{
		boolean params = false;
		boolean _const = false;
		
		String  name      = null;
		Token   valueType = null;
		boolean array     = false;
		int     ptrLevel  = 0;
		
		boolean isKeywords = true;
		boolean isType     = false;
		boolean isName     = false;
		
		for (int i = 0; i < tokens.length; i++)
		{
			TokenType type   = tokens[i].getType();
			String    lexeme = tokens[i].getLexeme();
			
			// Looking for the 'const' and/or 'params' keywords
			if (isKeywords)
			{
				if (type == TokenType.Keyword)
				{
					if (lexeme.equals("const"))
					{
						if (_const) throw new SyntaxError("Duplicate 'const' keyword", tokens[i]);
						else _const = true;
					}
					else if (lexeme.equals("params"))
					{
						if (params) throw new SyntaxError("Duplicate 'params' keyword", tokens[i]);
						else params = true;
					}
				}
				else
				{
					isKeywords = false;
					isType     = true;
					i--;
					continue;
				}
			}
			// Parse the parameter's type
			else if (isType)
			{
				// TODO: Generics type argument support
				// Get the parameter's name
				if (valueType == null)
				{
					if (type != TokenType.Identifier) {
						throw new SyntaxError("Invalid token - expected type name", tokens[i]);
					}
					else valueType = tokens[i];
				}
				// Get pointer level
				else if (type == TokenType.Operator)
				{
					for (int n = 0; n < lexeme.length(); n++)
					{
						if (lexeme.charAt(n) != '*') {
							throw new SyntaxError("Invalid token in type reference", tokens[i]);
						}
					}
					if (array)
					{
						throw new SyntaxError("Invalid type reference - cannot have a pointer to compile-time array; "
								+ "consider casting array to pointer-type", tokens[i]);
					}
					// TODO: Assuming ptr level is already 0 (TEST THIS)
					ptrLevel = lexeme.length();
				}
				// Get if compile-time array
				else if (type == TokenType.Delimiter && lexeme.equals("["))
				{
					if (i + 1 == tokens.length || !"]".equals(tokens[i+1].getLexeme())) {
						throw new SyntaxError("Expecting ']' closing bracket", tokens[i]);
					}
					else if (ptrLevel != 0)
					{
						throw new SyntaxError("Invalid type reference - cannot have a pointer to compile-time array; "
								+ "consider casting array to pointer-type", tokens[i]);
					}
					else array = true;
				}
				// Otherwise move on to the next state
				else
				{
					isType = false;
					isName = true;
					i--;
					continue;
				}
			}
			else if (isName)
			{
				if (type != TokenType.Identifier)
					throw new SyntaxError("Expected identifier to complete type-name pair", tokens[i]);
				
				else name = lexeme;
			}
			else throw new InternalError("Invalid type-name pair declaration", tokens[i]);
		}
		
		return new Parameter(name, new TypeRef(context, valueType, array, _const, ptrLevel), params);
	}
}
