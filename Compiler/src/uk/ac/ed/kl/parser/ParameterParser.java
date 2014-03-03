package uk.ac.ed.kl.parser;

import uk.ac.ed.kl.exceptions.CompilerError;
import uk.ac.ed.kl.exceptions.ErrorType;
import uk.ac.ed.kl.exceptions.ParserError;
import uk.ac.ed.kl.exceptions.TypeError;
import uk.ac.ed.kl.lexer.Token;
import uk.ac.ed.kl.lexer.TokenType;
import uk.ac.ed.kl.meta.Parameter;
import uk.ac.ed.kl.meta.TypeRef;

public final class ParameterParser
{
	public static Parameter parse(UnitContext context, Token[] tokens) throws CompilerError
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
						if (_const) throw new ParserError("Duplicate 'const' keyword", context, tokens[i],
								ErrorType.SyntaxError);
						else _const = true;
					}
					else if (lexeme.equals("params"))
					{
						if (params) throw new ParserError("Duplicate 'params' keyword", context, tokens[i],
								ErrorType.SyntaxError);
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
					if (type != TokenType.Identifier)
					{
						throw new ParserError("Invalid token - expected type name", context, tokens[i], ErrorType.SyntaxError);
					}
					else valueType = tokens[i];
				}
				// Get pointer level
				else if (type == TokenType.Operator)
				{
					for (int n = 0; n < lexeme.length(); n++)
					{
						if (lexeme.charAt(n) != '*')
							throw new ParserError("Invalid token in type reference", context, tokens[i], ErrorType.SyntaxError);
					}
					if (array)
						throw new TypeError("Invalid type reference - cannot have a pointer to compile-time array; "
								+ "consider casting array to pointer-type", context, tokens[i]);
					// TODO: Assuming ptr level is already 0 (TEST THIS)
					ptrLevel = lexeme.length();
				}
				// Get if compile-time array
				else if (type == TokenType.Delimiter && lexeme.equals("["))
				{
					if (i + 1 == tokens.length || !"]".equals(tokens[i+1].getLexeme()))
					{
						throw new ParserError("Expecting ']' closing bracket", context, tokens[i], ErrorType.SyntaxError);
					}
					else if (ptrLevel != 0)
						throw new TypeError("Invalid type reference - cannot have a pointer to compile-time array; "
								+ "consider casting array to pointer-type", context, tokens[i]);
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
					throw new ParserError("Expected identifier to complete type-name pair", context, tokens[i], 
							ErrorType.SyntaxError);
				else 
				{
					name = lexeme;
				}
			}
			else throw new ParserError("Invalid type-name pair declaration", context, tokens[i], ErrorType.Internal);
		}
		
		return new Parameter(name, new TypeRef(context, valueType, array, _const, ptrLevel), params);
	}
}
