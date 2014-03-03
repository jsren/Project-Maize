package uk.ac.ed.kl.parser;

import uk.ac.ed.kl.exceptions.CompilerError;
import uk.ac.ed.kl.exceptions.ErrorType;
import uk.ac.ed.kl.exceptions.ObjectFrozenException;
import uk.ac.ed.kl.exceptions.ParserError;
import uk.ac.ed.kl.lexer.Token;
import uk.ac.ed.kl.lexer.TokenType;
import uk.ac.ed.kl.meta.CodeUnit;
import uk.ac.ed.kl.meta.TypeRef;

public abstract class AliasParser
{
	public static void parse(UnitContext context, Token[] tokens) throws ObjectFrozenException, CompilerError
	{
		// Ensure the statement is only located at unit scope
		if (!(context.getCurrentScope() instanceof CodeUnit)) 
		{
			throw new ParserError("Invalid alias statement - can only place at file scope, consider moving", 
					context, tokens[0], ErrorType.SyntaxError);
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
				if (token.getType() != TokenType.Keyword || !"alias".equals(token.getLexeme()))
				{
					throw new ParserError("Parsing as alias - expected 'alias' keyword", context, 
							token, ErrorType.Internal);
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
					throw new ParserError("Invalid alias statement - invalid token, expected alias name. "
							+ "Alias names cannot contain non-identifier symbols such as pointers or indexers.", 
							context, token, ErrorType.SyntaxError);
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
				if (!"!".equals(token.getLexeme()))
				{
					throw new ParserError("Invalid alias statement - expected assignment operator", context,
							token, ErrorType.SyntaxError);
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
			else throw new ParserError("Invalid alias statement - unexpected token", context, token, 
					ErrorType.SyntaxError);
		}
		
		if (name == null || type == null)
		{
			throw new ParserError("Invalid alias statement - incomplete statement", context, tokens[0], 
					ErrorType.SyntaxError);
		}
		context.getCodeUnit().addAlias(type, name);
	}
}
