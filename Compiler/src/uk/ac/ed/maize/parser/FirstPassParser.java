package uk.ac.ed.maize.parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import uk.ac.ed.maize.TokenChain;
import uk.ac.ed.maize.exceptions.CompilerError;
import uk.ac.ed.maize.exceptions.LexerError;
import uk.ac.ed.maize.exceptions.ObjectFrozenException;
import uk.ac.ed.maize.exceptions.CompilerException;
import uk.ac.ed.maize.exceptions.SyntaxError;
import uk.ac.ed.maize.exceptions.InternalError;
import uk.ac.ed.maize.lexer.Lexer;
import uk.ac.ed.maize.lexer.Token;
import uk.ac.ed.maize.lexer.TokenType;
import uk.ac.ed.maize.meta.CodeUnit;
import uk.ac.ed.maize.meta.Enum;
import uk.ac.ed.maize.meta.Member;
import uk.ac.ed.maize.meta.Namespace;
import uk.ac.ed.maize.meta.Scope;
import uk.ac.ed.maize.meta.Type;

public class FirstPassParser
{
	private Lexer lexer;
	private File  filepath;
	
	public FirstPassParser(InputStreamReader reader, File filepath)
	{
		this.filepath = filepath;
		this.lexer    = new Lexer(reader);
	}
	
	/** 
	 * Skips over the current block (assumes the first brace has been consumed).
	 * Consumes the last brace.
	 */
	private void skipBlock() throws IOException, LexerError
	{
		int braceLevel = 1;
		while (!lexer.isEndOfStream() && braceLevel > 0)
		{
			Token  t = lexer.nextToken();
			String s = t.getLexeme();
			
			if (t.getType() == TokenType.Delimiter && s.length() == 1)
			{
				     if (s.charAt(0) == '{') ++braceLevel;
				else if (s.charAt(0) == '}') --braceLevel;
			}
		}
	}
	
	
	public CodeUnit parse(CompilerContext globalContext) 
			throws Exception, IOException, CompilerException, ObjectFrozenException
	{
		// Create temporary store for current tokens
		TokenChain tokenChain  = new TokenChain();
		// Create context object with a new code unit
		ParserContext context = new ParserContext(globalContext, new CodeUnit(this.filepath)); 
		try
		{
			while (!this.lexer.isEndOfStream())
			{
				Token token = this.lexer.nextToken();
				
				// Ignore comments
				// TODO: Parse docstrings
				if (TokenType.isComment(token.getType())) continue;
				if (token.getType() == TokenType.None)    continue;
				
				else if (token.getType() == TokenType.Delimiter)
				{
					String delim = token.getLexeme();
					
					if (delim.length() == 1)
					{					
						// End of current block
						if (delim.charAt(0) == '}')
						{
							context.unwind(); // Roll back to the parent scope(/type)
							
							// If within an enum, treat any current element as an enum const
							Scope currentScope = context.getCurrentScope();
							if (currentScope instanceof Enum && tokenChain.size() != 0)
							{
								((Enum)currentScope).addMember(EnumParser.parseElement(tokenChain.toArray(), currentScope));
							}
							// Check that we haven't unwound the code unit
							if (currentScope == null) {
								throw new SyntaxError("Mismatched braces - unexpected close brace", token);
							}
						}
						
						// Now check to see if we're currently inside an enum
						// as then we're parsing EnumElements
						else if (context.getCurrentScope() instanceof Enum && ",".equals(token.getLexeme()))
						{
							Scope currentScope = context.getCurrentScope();
							((Enum)currentScope).addMember(EnumParser.parseElement(tokenChain.toArray(), currentScope));
						}
						
						// Start of new block (end of its expression)
						else if (delim.charAt(0) == '{') 
						{
							// New Scope Block - these are only valid within functions
							if (tokenChain.size() == 0) {
								throw new SyntaxError("Invalid new scope block - perhaps missing expression", token);
							}
							
							// Holds whether an acceptable parser
							// was found for the token chain.
							boolean parsed = false;
							
							// Holds the block expression (tokens before the '{' character)
							Token[] tokens   = tokenChain.toArray();
							
							// Search the tokens for some identifying keyword
							// == PARSE KEYWORD BLOCKS ==
							for (Token t : tokens)
							{
								if (t.getType() == TokenType.Keyword)
								{
									String lexeme = t.getLexeme();
									
									if (lexeme.equals("class"))
									{
										Type newType = ClassParser.parse(context, tokens);
										context.getCurrentScope().addSubtype(newType);
										
										context.setCurrentType(newType);
										context.setCurrentScope(newType);
										parsed = true; break;
									}
									else if (lexeme.equals("enum"))
									{
										Type newType = EnumParser.parse(context, tokens);
										context.getCurrentScope().addSubtype(newType);
										
										context.setCurrentType(newType);
										context.setCurrentScope(newType);
										parsed = true; break;
									}
									else if (lexeme.equals("namespace"))
									{
										Namespace newSpace = NamespaceParser.parse(context, tokens);
										
										((Namespace)context.getCurrentScope()).addNamespace(newSpace);
										context.setCurrentScope(newSpace);
										parsed = true; break;
									}
									else if (lexeme.equals("interface"))
									{
										Type newType = InterfaceParser.parse(context, tokens);
										context.getCurrentScope().addSubtype(newType);
										
										context.setCurrentType(newType);
										context.setCurrentScope(newType);
										parsed = true; break;
									}
									else if (lexeme.equals("operator"))
									{
										Member newMember = OperatorParser.parse(context, tokens);
										context.getCurrentScope().addMember(newMember);
										
										this.skipBlock(); // Skip function blocks - we'll parse these last
										parsed = true; break;
									}
									else if (lexeme.equals("struct"))
									{
										Type newType = StructParser.parse(context, tokens);
										context.getCurrentScope().addSubtype(newType);
										
										context.setCurrentType(newType);
										context.setCurrentScope(newType);
										parsed = true; break;
									}
									// Otherwise continue looking for a keyword
									else continue;
								}
							}
							// It must be a function (hopefully...)
							// == PARSE FUNCTION BLOCKS ==
							if (!parsed)
							{
								Member newMember = MethodParser.parse(context, tokens, true, token);
								context.getCurrentScope().addMember(newMember);
								this.skipBlock(); // Skip function body for now
							}
							// Finally, clear the token chain
							tokenChain.clear();
						}
						
						// Looking for a field, keyword statement or abstract/extern method
						else if (delim.charAt(0) == ';')
						{
							// Ignore empty statements
							if (tokenChain.size() == 0) continue;
							
							boolean parsed = false;
							
							// Holds the block expression (tokens before the '{' character)
							Token[] tokens   = tokenChain.toArray();
							
							// == PARSE KEYWORD STATEMENTS ==
							for (Token t : tokenChain)
							{
								String    lexeme = t.getLexeme();
								TokenType type   = t.getType();
								
								if (type == TokenType.Keyword)
								{
									     if (lexeme.equals("alias")) AliasParser.parse(context, tokens);
									else if (lexeme.equals("using")) UsingParser.parse(context, tokens);
								}
							}
							// Hopefully a field or abstract/extern method
							// == PARSE EMPTY METHOD ==
							if (!parsed)
							{
								context.getCurrentScope().addMember(MemberParser.parse(context, tokens));
							}
							// Finally, clear the token chain
							tokenChain.clear();
						}
						
						// ===========================================
						// ACTUALLY ADDS THE TOKENS TO THE TOKEN CHAIN
						// ===========================================
						else tokenChain.add(token);
					}
				}
				// Add non-delimiter chars to the token chain
				else tokenChain.add(token);
			}
		}
		// Throw an internal error as a real exception
		catch (InternalError e)	{
			throw new Exception(e);
		}
		// Throw a context-aware exception
		catch (CompilerError e)
		{
			ParserContext c = (ParserContext)context.clone();
			c.freeze();
			
			throw new CompilerException(c, e);
		}
		// Return the resulting code unit
		return context.getCodeUnit();
	}
}
