package uk.ac.ed.maize.parser;

import uk.ac.ed.maize.exceptions.CompilerError;
import uk.ac.ed.maize.exceptions.InternalError;
import uk.ac.ed.maize.exceptions.ObjectFrozenException;
import uk.ac.ed.maize.exceptions.SyntaxError;
import uk.ac.ed.maize.lexer.Token;
import uk.ac.ed.maize.lexer.TokenType;
import uk.ac.ed.maize.meta.CodeUnit;
import uk.ac.ed.maize.meta.TypeRef;


public abstract class AliasParser
{
	public static void parse(ParserContext context, Token[] tokens) 
			throws ObjectFrozenException, CompilerError
	{
		// Ensure the statement is only located at unit scope
		if (!(context.getCurrentScope() instanceof CodeUnit)) 
		{
			throw new SyntaxError("Invalid alias statement - can only place at file scope, " +
					"consider moving",  tokens[0]);
		}
		
		Token   name = null;
		TypeRef type = null;
		
		boolean isKeyword = true;
		boolean isName    = false;
		boolean isEquals  = false;
		boolean isType    = false;
		
		for (int i = 0; i < tokens.length; i++)
		{
			Token token = tokens[i];
			
			if (isKeyword)
			{
				if (token.getType() != TokenType.Keyword || !"alias".equals(token.getLexeme())) {
					throw new InternalError("Parsing as alias - expected 'alias' keyword", token);
				}
				else
				{
					isKeyword = false;
					isName    = true;
				}
			}
			else if (isName)
			{
				if (token.getType() != TokenType.Identifier)
				{
					throw new SyntaxError("Invalid alias statement - invalid token, expected alias name. "
							+ "Alias names cannot contain non-identifier symbols such as pointers or indexers.", token);
				}
				else
				{
					name     = token;
					isName   = false;
					isEquals = true;
				}
			}
			else if (isEquals)
			{
				if (!"!".equals(token.getLexeme())) {
					throw new SyntaxError("Invalid alias statement - expected assignment operator", token);
				}
				else
				{
					isEquals = false;
					isType   = true;
				}
			}
			else if (isType)
			{
				// Little hack here - use the ParameterParser. The reason I don't have a separate "TypeRefParser"
				// is that it could be difficult to establish, without parsing, where the TypeRef ends so that we 
				// can pass the Token[] to it per the model.
				Token[] buffer = new Token[tokens.length + 1 - i];
				
				int n = 0;
				for (; i < tokens.length; n++, i++)
				{
					buffer[n] = tokens[i];
				}
				// Add an empty identifier
				buffer[n] = new Token(TokenType.Identifier, "", -1, -1);
				
				// Now parse and just use the TypeRef
				type = ParameterParser.parse(context, buffer).getTypeRef();
			}			
			else throw new SyntaxError("Invalid alias statement - unexpected token", token);
		}
		
		if (name == null || type == null) {
			throw new SyntaxError("Invalid alias statement - incomplete statement", tokens[0]);
		}
		context.getCodeUnit().addAlias(type, name);
	}
}
