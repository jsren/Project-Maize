package uk.ac.ed.maize.parser;

import java.util.ArrayList;
import java.util.EnumSet;

import uk.ac.ed.maize.exceptions.CompilerError;
import uk.ac.ed.maize.exceptions.SyntaxError;
import uk.ac.ed.maize.exceptions.InternalError;
import uk.ac.ed.maize.lexer.Token;
import uk.ac.ed.maize.lexer.TokenType;
import uk.ac.ed.maize.meta.Method;
import uk.ac.ed.maize.meta.Parameter;
import uk.ac.ed.maize.meta.Type;
import uk.ac.ed.maize.meta.TypeRef;
import uk.ac.ed.maize.meta.Visibility;

public class MethodParser
{
	public static Method parse(ParserContext context, Token[] tokens, boolean hasBody, Token bodyStart) 
			throws CompilerError
	{
		String     name       = null;
		TypeRef    returnType = null; // Null TypeRef == void
		Visibility visibility = null;
		boolean    constructr = false;
		
		EnumSet<Attribute>   atts   = EnumSet.noneOf(Attribute.class);
		ArrayList<Parameter> params = new ArrayList<Parameter>();
		
		// State indicators
		boolean isKeywords = true;
		boolean isRtrnType = false;
		boolean isName     = false;
		boolean isParam    = false;
		boolean isParSep   = false;
		
		for (int i = 0; i < tokens.length; i++)
		{
			TokenType type   = tokens[i].getType();
			String    lexeme = tokens[i].getLexeme();
			
			if (isKeywords)
			{
				if (type != TokenType.Keyword)
				{
					i--; 
					isKeywords = false;
					isRtrnType  = true;
					continue;
				}
				
				// Try and parse an attribute
				Attribute newAtt = Attribute.parse(lexeme);
				if (newAtt != null) atts.add(newAtt);
				
				// Otherwise try and parse a visibility modifier
				else
				{
					Visibility newVis = Visibility.parse(lexeme);
					if (newVis != null)
					{
						if (visibility != null)
						{
							throw new SyntaxError("Invalid method declaration -" +
									" more than one visibility modifier applied", tokens[i]);
						}
						else visibility = newVis;
					}
					else throw new InternalError("Invalid method declaration - unknown keyword", tokens[i]);
				}
			}
			else if (isRtrnType)
			{
				// Void keyword
				if (lexeme.equals("void"))
				{					
					isRtrnType = false;
					isName     = true;
					continue;
				}
				else if (type == TokenType.Identifier)
				{
					// Check for constructors
					Type parentType = context.getCurrentType();
					if (context.getCurrentScope() == parentType && lexeme.equals(parentType.getName()))
					{
						if (i + 1 == tokens.length || !"(".equals(tokens[i+1].getLexeme()))
						{
							throw new SyntaxError("Invalid method declaration - expected open parenthesis", 
									tokens[i]);
						}
						constructr = true;
						returnType = new TypeRef(parentType, false, false, 0);
						name       = lexeme;
						isRtrnType = false;
						isParam    = true;
						i++;
						continue;
					}
					// Use the parameter parser to get the return type & method name
					else 
					{
						int bufferSize = 0;
						for (int n = i; n < tokens.length; n++, bufferSize++)
						{
							if (tokens[n].getType() == TokenType.Delimiter
									&& tokens[n].getLexeme().equals("("))
							{
								break;
							}
						}
						if (bufferSize == tokens.length) {
							throw new SyntaxError("Invalid method declaration - missing opening parenthesis", tokens[i]);
						}
						else if (bufferSize == 0) {
							throw new SyntaxError("Invalid method declaration - missing return type", tokens[i]);
						}
						else
						{
							Token[] buffer = new Token[bufferSize];
							for (int n = i, t = 0; t < bufferSize; n++, t++) { buffer[t] = tokens[n]; }
							
							Parameter func = ParameterParser.parse(context, buffer);
							name       = func.getName();
							returnType = func.getTypeRef();
							
							isRtrnType = false;
							isName     = false;
							isParam    = true;
							i += bufferSize;
							continue;
						}
					}
				}
				else throw new SyntaxError("Invalid method declaration - expected return type", tokens[i]);
			}
			// If a 'void' method, parse the name
			else if (isName)
			{
				if (type != TokenType.Identifier) {
					throw new SyntaxError("Invalid method declaration - expected method name", tokens[i]);
				}
				else if (i + 1 == tokens.length || !"(".equals(tokens[i+1].getLexeme())) {
					throw new SyntaxError("Invalid method declaration - expected open parenthesis", tokens[i]);
				}
				else
				{
					name    = lexeme;
					isName  = false;
					isParam = true;
					i++;
					continue;
				}
			}
			// Parse the parameters
			else if (isParam)
			{
				if (type == TokenType.Delimiter && ")".equals(lexeme))
				{
					isParam  = false;
					continue;
				}
				
				int bufferSize = 0;
				for (int n = i; n < tokens.length; n++, bufferSize++)
				{
					if (tokens[n].getType() == TokenType.Delimiter
							&& (tokens[n].getLexeme().equals(",") || tokens[n].getLexeme().equals(")")))
					{
						break;
					}
				}
				if (bufferSize == tokens.length) {
					throw new SyntaxError("Invalid method declaration - missing closing parenthesis", tokens[i]);
				}
				if (bufferSize == 0) {
					throw new SyntaxError("Invalid method declaration - missing parameter", tokens[i]);
				}
				else
				{
					Token[] buffer = new Token[bufferSize];
					for (int n = i, t = 0; t < bufferSize; n++, t++) { buffer[t] = tokens[n]; }
					
					params.add(ParameterParser.parse(context, buffer));
					isParam  = false;
					isParSep = true;
					i += bufferSize - 1;
					continue;
				}
			}
			else if (isParSep)
			{
				if (type == TokenType.Delimiter && ")".equals(lexeme))
				{
					isParSep  = false;
					continue;
				}
				else if (type == TokenType.Delimiter && ",".equals(lexeme))
				{
					isParSep = false;
					isParam  = true;
					continue;
				}
				else throw new SyntaxError("Invalid method declaration - expecting comma-separated list of "
						+ "parameters or close parenthesis", tokens[i]);
			}
			else throw new SyntaxError("Invalid method declaration - invalid token", tokens[i]);
		}
		
		return new Method(tokens[0].getLineIndex(), context.getCurrentScope(), name, returnType, visibility, atts, 
				constructr, hasBody, params.toArray(new Parameter[0]), bodyStart);
	}
}
