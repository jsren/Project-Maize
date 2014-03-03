package uk.ac.ed.kl.parser;

import java.util.ArrayList;
import java.util.EnumSet;

import uk.ac.ed.kl.exceptions.CompilerError;
import uk.ac.ed.kl.exceptions.ErrorType;
import uk.ac.ed.kl.exceptions.ParserError;
import uk.ac.ed.kl.lexer.Token;
import uk.ac.ed.kl.lexer.TokenType;
import uk.ac.ed.kl.meta.Method;
import uk.ac.ed.kl.meta.Parameter;
import uk.ac.ed.kl.meta.Type;
import uk.ac.ed.kl.meta.TypeRef;

public class MethodParser
{
	public static Method parse(UnitContext context, Token[] tokens, boolean hasBody) 
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
							throw new ParserError("Invalid method declaration -" +
									" more than one visibility modifier applied", context, tokens[i], ErrorType.SyntaxError);
						}
						else visibility = newVis;
					}
					else
					{
						throw new ParserError("Invalid method declaration - unknown keyword", context, tokens[i], ErrorType.Internal);
					}
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
							throw new ParserError("Invalid method declaration - expected open parenthesis", 
									context, tokens[i], ErrorType.SyntaxError);
						}
						constructr = true;
						returnType = new TypeRef(context, parentType, false, false, 0);
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
						if (bufferSize == tokens.length)
						{
							throw new ParserError("Invalid method declaration - missing opening parenthesis", 
									context, tokens[i], ErrorType.SyntaxError);
						}
						else if (bufferSize == 0)
						{
							throw new ParserError("Invalid method declaration - missing return type", 
									context, tokens[i], ErrorType.SyntaxError);
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
				else throw new ParserError("Invalid method declaration - expected return type", 
						context, tokens[i], ErrorType.SyntaxError);
			}
			// If a 'void' method, parse the name
			else if (isName)
			{
				if (type != TokenType.Identifier)
				{
					throw new ParserError("Invalid method declaration - expected method name", 
							context, tokens[i], ErrorType.SyntaxError);
				}
				else if (i + 1 == tokens.length || !"(".equals(tokens[i+1].getLexeme()))
				{
					throw new ParserError("Invalid method declaration - expected open parenthesis", 
							context, tokens[i], ErrorType.SyntaxError);
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
				if (bufferSize == tokens.length)
				{
					throw new ParserError("Invalid method declaration - missing closing parenthesis", 
							context, tokens[i], ErrorType.SyntaxError);
				}
				if (bufferSize == 0)
				{
					throw new ParserError("Invalid method declaration - missing parameter", 
							context, tokens[i], ErrorType.SyntaxError);
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
				else throw new ParserError("Invalid method declaration - expecting comma-separated list of "
						+ "parameters or close parenthesis", context, tokens[i], ErrorType.SyntaxError);
			}
			else throw new ParserError("Invalid method declaration - invalid token", context, tokens[i], ErrorType.SyntaxError);
		}
		
		return new Method(tokens[0].getLineIndex(), context.getCurrentScope(), name, returnType, visibility, atts, 
				constructr, hasBody, params.toArray(new Parameter[0]));
	}
}
