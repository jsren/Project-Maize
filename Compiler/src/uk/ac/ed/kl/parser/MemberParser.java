package uk.ac.ed.kl.parser;

import java.util.ArrayList;
import java.util.EnumSet;

import uk.ac.ed.kl.exceptions.CompilerError;
import uk.ac.ed.kl.exceptions.ErrorType;
import uk.ac.ed.kl.exceptions.ParserError;
import uk.ac.ed.kl.lexer.Token;
import uk.ac.ed.kl.lexer.TokenType;
import uk.ac.ed.kl.meta.Expression;
import uk.ac.ed.kl.meta.Field;
import uk.ac.ed.kl.meta.Member;
import uk.ac.ed.kl.meta.Method;
import uk.ac.ed.kl.meta.Parameter;

public class MemberParser
{
	// We're looking for fields and bodiless functions (abstract/extern)
	
	public static Member parse(UnitContext context, Token[] tokens) throws CompilerError
	{
		Parameter  decl       = null;
		Visibility visibility = null;
		
		EnumSet<Attribute> atts = EnumSet.noneOf(Attribute.class);
		
		// First look for visibility & attributes
		int tokenIndex = 0;
		for (; tokenIndex < tokens.length; tokenIndex++)
		{
			Token token = tokens[tokenIndex];
			if (token.getType() == TokenType.Keyword)
			{
				Visibility vis = Visibility.parse(token.getLexeme());
				if (vis != null)
				{
					if (visibility == null) visibility = vis;
					else throw new ParserError("Invalid member declaration - more than one visibility modifier applied", 
							context, token, ErrorType.SyntaxError);
				}
			}
			else break;
		}
		
		boolean isFunction    = false;
		boolean isInitialised = false;
		
		// "Peek" ahead to look for a '(' character - in the case of a bodiless function,
		// an assignment operator in the case of an intialised variable, or simply the end.
		int endIndex = tokenIndex;
		for (; endIndex < tokens.length; endIndex++)
		{
			Token     token  = tokens[endIndex];
			TokenType type   = token.getType();
			String    lexeme = token.getLexeme();
			
			     if (type == TokenType.Delimiter && "(".equals(lexeme)) { isFunction    = true; break; }
			else if (type == TokenType.Operator  && "=".equals(lexeme)) { isInitialised = true; break; }
		}
		
		// Attempt to parse the type-name pair with a ParameterParser
		Token[] buffer = new Token[endIndex - tokenIndex];
		for (int i = 0, n = tokenIndex; n < endIndex; i++, n++)
		{
			buffer[i] = tokens[n];
		}
		try { decl = ParameterParser.parse(context, buffer); }
		// Throw a context-specific error
		catch (CompilerError e)
		{
			throw new ParserError("Invalid member declaration - " + e.getMessage(), 
					context, tokens[tokenIndex], ErrorType.SyntaxError);
		}
		
		if (decl.getIsParams()) throw new ParserError("Invalid member declaration - only parameters can be marked 'params'", 
				context, tokens[tokenIndex], ErrorType.SyntaxError);
		
		// Return as function
		if (isFunction)
		{
			ArrayList<Parameter> params = new ArrayList<Parameter>();
			
			tokenIndex = endIndex + 1;
			
			// Loop over each parameter
			boolean foundEnd = false;
			while (!foundEnd)
			{				
				// Find the end of the next parameter
				for (; endIndex < tokens.length; endIndex++)
				{
					Token token = tokens[endIndex];
					
					// Break on encountering ')' or ','
					if (token.getType() == TokenType.Delimiter)
					{
						String lexeme = token.getLexeme();
						     if (")".equals(lexeme)) { foundEnd = true; break; }
						else if (",".equals(lexeme)) { break; }
					}
				}
				// == Parser Error ==
				if (endIndex == tokens.length) throw new ParserError("Invalid method declaration - missing closing "
						+ "parenthesis", context, tokens[endIndex - 1], ErrorType.SyntaxError);
				// ===================
				
				// Parse argument (also increments tokenIndex)
				buffer = new Token[endIndex - tokenIndex];
				for (int i = 0; tokenIndex < endIndex; i++, tokenIndex++)
				{
					buffer[i] = tokens[tokenIndex];
				}
				try { params.add(ParameterParser.parse(context, buffer)); }
				// Throw a context-specific error
				// TODO: This will not give good output on a misplaced "constructor" (is this actually a bad thing?)
				catch (CompilerError e) 
				{ 
					throw new ParserError("Invalid method declaration - "+e.getMessage(), 
							context, tokens[endIndex - buffer.length], ErrorType.SyntaxError); 
				}
			}
			// == Parser Error ==
			if (foundEnd && endIndex != tokens.length - 1) throw new ParserError("Invalid method declaration - "
					+ "unexpected token", context, tokens[endIndex + 1], ErrorType.SyntaxError);
			// ===================
			
			return new Method(tokens[0].getLineIndex(), context.getCurrentScope(), decl.getName(), decl.getTypeRef(), 
					visibility, atts, false, false, params.toArray(new Parameter[0]));
		}
		
		// Return as field
		else if (isInitialised)
		{
			// Look for bitfield value
			
			buffer = new Token[tokens.length - endIndex - 1];
			for (int i = 0, n = endIndex + 1; n < tokens.length; i++, n++)
			{
				buffer[i] = tokens[n];
			}
			
			Expression value;
			try { value = Expression.parse(buffer, context.getCurrentScope()); }
			// Throw a context-specific error
			catch (CompilerError e) 
			{
				throw new ParserError("Invalid member declaration - " + e.getMessage(), context, 
						tokens[tokenIndex], ErrorType.SyntaxError);
			}
			return new Field(tokens[0].getLineIndex(), context.getCurrentScope(), 
					decl.getName(), decl.getTypeRef(), visibility, atts, value);
		}
		else
		{
			return new Field(tokens[0].getLineIndex(), context.getCurrentScope(), 
					decl.getName(), decl.getTypeRef(), visibility, atts, null);
		}
	}
}
