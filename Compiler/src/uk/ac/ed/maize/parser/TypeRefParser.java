package uk.ac.ed.maize.parser;

import uk.ac.ed.maize.exceptions.CompilerError;
import uk.ac.ed.maize.exceptions.SyntaxError;
import uk.ac.ed.maize.exceptions.InternalError;
import uk.ac.ed.maize.lexer.Token;
import uk.ac.ed.maize.lexer.TokenType;
import uk.ac.ed.maize.meta.TypeRef;

public class TypeRefParser
{
	public static final TypeRef parse(ParserContext context, Token[] tokens) throws CompilerError
	{
		int     ptrLevel = 0;
		boolean isArray  = false;
		boolean isConst  = false;
		Token   typeName = null;
		
		// Get if const
		if (tokens[0].getType() == TokenType.Keyword && tokens[0].getLexeme().equals("const"))
		{
			isConst = true;
			
			if (tokens.length == 1) {
				throw new SyntaxError("Invalid type reference - missing type name", typeName);
			}
			else typeName = tokens[1];
		}
		else typeName = tokens[0];
		
		// Check name
		if (typeName.getType() != TokenType.Identifier) {
			throw new SyntaxError("Invalid type reference - expecting type name", typeName);
		}
	
		// Get pointer level and/or if array
		if (tokens.length > 1)
		{
			for (int i = 1; i < tokens.length; i++)
			{
				Token  t      = tokens[i];
				String lexeme = t.getLexeme();
				
				// Look for pointer level
				if (t.getType() == TokenType.Operator)
				{
					for (int n = 0; n < lexeme.length(); n++)
					{
						if (lexeme.charAt(n) != '*') {
							throw new SyntaxError("Invalid token in type reference", tokens[i]);
						}
					}
					if (isArray)
					{
						throw new SyntaxError("Invalid type reference - cannot have a pointer to " +
								"compile-time array", tokens[i]);
					}
					else if (ptrLevel != 0) {
						throw new InternalError("Invalid type reference - pointer level already set", tokens[i]);
					}
					ptrLevel = lexeme.length();
				}
				// Look for array
				else if ("[".equals(lexeme))
				{
					if (i + 1 == tokens.length || !"]".equals(tokens[i+1].getLexeme())) {
						throw new SyntaxError("Invalid type reference - expected closing bracket", tokens[i]);
					}
					if (isArray) {
						throw new SyntaxError("Invalid type reference - jagged arrays are not supported", tokens[i]);
					}
					isArray = true;
				}
				// Otherwise it's a bad token
				else throw new SyntaxError("Invalid token in type reference", tokens[i]);
			}
		}
		// Return parsed type ref
		return new TypeRef(typeName, isArray, isConst, ptrLevel);
	}
}
