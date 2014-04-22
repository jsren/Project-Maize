package uk.ac.ed.maize.parser;

import uk.ac.ed.maize.exceptions.CompilerError;
import uk.ac.ed.maize.exceptions.SyntaxError;
import uk.ac.ed.maize.lexer.Token;
import uk.ac.ed.maize.lexer.TokenType;
import uk.ac.ed.maize.meta.Parameter;
import uk.ac.ed.maize.meta.TypeRef;

public final class ParameterParser
{
	public static Parameter parse(ParserContext context, Token[] tokens) throws CompilerError
	{
		boolean params = false;
		TypeRef type   = null;
		String  name   = null;
		
		// Check for params
		if (tokens[0].getType() == TokenType.Keyword && tokens[0].getLexeme().equals("params")) {
			params = true;
		}
		
		// Copy tokens for TypeRef parsing
		Token[] buffer = new Token[tokens.length - 1 - (params ? 1 : 0)];
		for (int i = params ? 1 : 0, n = 0; i < tokens.length - 1; i++, n++) {
			buffer[n] = tokens[i];
		}
		
		if (buffer.length == 0) {
			throw new SyntaxError("Invalid parameter - missing type reference", tokens[0]);
		}
		
		// Parse type
		type = TypeRefParser.parse(context, buffer);
		
		// Name
		Token nameToken = tokens[tokens.length - 1];
		if (nameToken.getType() != TokenType.Identifier) {
			throw new SyntaxError("Invalid parameter - expected identifier to complete type-name pair", nameToken);
		}
		name = nameToken.getLexeme();
		
		// Return parsed parameter
		return new Parameter(name, type, params);
	}
}
